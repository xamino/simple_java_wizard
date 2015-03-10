package eu.hartmann.wizard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Implements a simple but working Java wizard.
 *
 * @author Tamino Hartmann
 */
public class Wizard {

    // requires static references to work with static function
    private static JButton next;
    private static JButton previous;
    // action strings
    private final String NEXT = "next";
    private final String PREVIOUS = "previous";
    // gui stuff
    private JPanel content;
    private JFrame masterFrame;
    private Step currentStep;
    private JPanel oldPanel;

    /**
     * Creates a new wizard. Every wizard will use its own window to display its contents. Note that this does not
     * already display the wizard. To display the first Step call start(Step step). From there on the wizard will
     * navigate in accordance to the order given by the Steps.
     *
     * @param title The title of the wizard window.
     */
    public Wizard(String title) {
        this(title, null);
    }

    /**
     * Creates a new wizard.
     *
     * @param title         The title of the wizard window.
     * @param preferredSize The preferred size of the complete wizard window.
     */
    public Wizard(String title, Dimension preferredSize) {
        // use system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        masterFrame = new JFrame(title);
        // wizard will close on exit without further ado
        masterFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        if (preferredSize != null) {
            masterFrame.setPreferredSize(preferredSize);
        }
        masterFrame.setResizable(false);
        masterFrame.setLocationRelativeTo(null);
        // set up border stuff
        content = new JPanel();
        content.setLayout(new BorderLayout());
        JPanel buttons = new JPanel();
        next = new JButton("Next");
        previous = new JButton("Previous");
        // assign buttons
        ActionReceiver receiver = new ActionReceiver();
        next.addActionListener(receiver);
        previous.addActionListener(receiver);
        next.setActionCommand(NEXT);
        previous.setActionCommand(PREVIOUS);
        buttons.add(previous);
        buttons.add(next);
        content.add(buttons, BorderLayout.SOUTH);
        masterFrame.add(content);
    }

    /**
     * Given a step will take its content and generate a closing panel with it. Can also be used to close the wizard
     * immediately.
     *
     * @param exitStep The Step used to generate the content from. If NULL the wizard will close immediately!
     * @return The Step to use on the next() or previous() where you want to exit the wizard. Will close the wizard!
     */
    public static Step EXIT(final Step exitStep) {
        // if null --> exit NOW
        if (exitStep == null) {
            System.exit(0);
            return null;
        }
        // tricky but cool: create a new step with special stuff but use the draw from the given step!
        return new Step() {
            @Override
            public Step next() {
                // This can happen when the step is programmatically called next, so quit.
                System.exit(0);
                return null;
            }

            @Override
            public Step previous() {
                // This can happen when the step is programmatically called previous, so quit.
                System.exit(0);
                return null;
            }

            @Override
            public JPanel draw() {
                // disable previous button
                previous.setEnabled(false);
                // update next button text
                next.setText("Exit");
                // override
                next.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent actionEvent) {
                        System.exit(0);
                    }
                });
                // now draw custom content:
                return exitStep.draw();
            }
        };
    }

    /**
     * Starts the wizard with the given step. This will immediately display the first step!
     *
     * @param step The step to start with.
     */
    public void start(Step step) {
        // call the normal goStep method
        goStep(step);
    }

    /**
     * Sets the wizard to the given step.
     *
     * @param step The Step to go to. If null the wizard will do nothing.
     */
    private void goStep(Step step) {
        // if null we ignore and return
        if (step == null) {
            return;
        }
        // paint new panel
        paintPanel(step.draw());
        // store so we know what to call on buttons
        currentStep = step;
    }

    /**
     * Paints a panel to the frame.
     *
     * @param panel The panel to paint.
     */
    private void paintPanel(JPanel panel) {
        // remove old panel if applicable
        if (oldPanel != null) {
            content.remove(oldPanel);
        }
        // set new one
        content.add(panel, BorderLayout.CENTER);
        // pack and show
        masterFrame.pack();
        masterFrame.setVisible(true);
        // update old panel so we can remove it later
        oldPanel = panel;
    }

    /**
     * Allows to programmatic go to the next Step. Equivalent to pressing the Next button.
     */
    public void executeNext() {
        goStep(currentStep.next());
    }

    /**
     * Allows to programmatic go to the previous Step. Equivalent to pressing the Previous button.
     */
    public void executePrevious() {
        goStep(currentStep.previous());
    }

    /**
     * Action receiver that handles the wizard button presses.
     */
    private class ActionReceiver implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals(NEXT)) {
                goStep(currentStep.next());
            } else if (e.getActionCommand().equals(PREVIOUS)) {
                goStep(currentStep.previous());
            } else {
                JOptionPane.showMessageDialog(new JDialog(), "The received action event is unknown!", "Error", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
