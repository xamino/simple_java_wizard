package eu.hartmann.wizard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Implements a simple but working Java wizard.
 *
 * @author Tamino Hartmann
 */
// TODO: handle Layout better; etc
// suppress executeNext and executePrevious warnings.
@SuppressWarnings("UnusedDeclaration")
public class Wizard {

    // action strings
    private final String NEXT = "nextButton";
    private final String PREVIOUS = "previousButton";
    // callback for what happens if the wizard is to be closed
    private final WizardClose wizardClose;
    // references to the buttons
    private JButton nextButton;
    private JButton previousButton;
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
        this(title, null, null);
    }

    /**
     * Creates a new wizard with all given options.
     *
     * @param title         The title of the wizard window.
     * @param wizardClose   The callback for when the wizard is closed. Nullable.
     * @param preferredSize The preferred size of the complete wizard window. Nullable.
     */
    public Wizard(String title, WizardClose wizardClose, Dimension preferredSize) {
        // assign callback
        this.wizardClose = wizardClose;
        // use system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | UnsupportedLookAndFeelException | IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        masterFrame = new JFrame(title);
        // call correct close operation even on window close:
        masterFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                super.windowClosing(windowEvent);
                handleWizardClose();
            }
        });
        if (preferredSize != null) {
            masterFrame.setPreferredSize(preferredSize);
        }
        masterFrame.setResizable(false);
        masterFrame.setLocationRelativeTo(null);
        // set up border stuff
        content = new JPanel();
        content.setLayout(new BorderLayout());
        JPanel buttons = new JPanel();
        nextButton = new JButton("Next");
        nextButton.setMnemonic(KeyEvent.VK_N);
        previousButton = new JButton("Previous");
        previousButton.setMnemonic(KeyEvent.VK_P);
        // assign buttons
        ActionReceiver receiver = new ActionReceiver();
        nextButton.addActionListener(receiver);
        previousButton.addActionListener(receiver);
        nextButton.setActionCommand(NEXT);
        previousButton.setActionCommand(PREVIOUS);
        buttons.add(previousButton);
        buttons.add(nextButton);
        content.add(buttons, BorderLayout.SOUTH);
        masterFrame.add(content);
    }

    /**
     * Given a step will take its content and generate a closing panel with it. Can also be used to call the WizardClose
     * callback immediately if the step is null.
     *
     * @param exitStep The Step used to generate the content from. If NULL the wizard will call the WizardClose callback
     *                 immediately!
     * @return The Step to use on the nextButton() or previousButton() where you want to exit the wizard. Will call
     * WizardClose immediately!
     */
    public Step createExit(Step exitStep) {
        // if null --> exit NOW
        if (exitStep == null) {
            handleWizardClose();
            return null;
        }
        // tricky but cool: create a new step with special stuff but use the draw from the given step!
        return new Step() {
            @Override
            public Step next() {
                // This can happen when the step is programmatically called nextButton, so call WizardClose callback:
                handleWizardClose();
                return null;
            }

            @Override
            public Step previous() {
                // This can happen when the step is programmatically called previousButton, so call WizardClose callback.
                handleWizardClose();
                return null;
            }

            @Override
            public JPanel draw() {
                // disable previousButton button
                previousButton.setEnabled(false);
                // update nextButton button text
                nextButton.setText("Exit");
                // set new mnemonic
                nextButton.setMnemonic(KeyEvent.VK_E);
                // override
                nextButton.addActionListener(actionEvent -> handleWizardClose());
                // now draw custom content:
                return exitStep.draw();
            }
        };
    }

    /**
     * If available will call the WizardClose callback. Finally the wizard will destroy its window.
     */
    private void handleWizardClose() {
        if (wizardClose != null) {
            wizardClose.onWizardClose();
        }
        // hide window
        masterFrame.setVisible(false);
        // dispose frame
        masterFrame.dispose();
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
     * Allows to programmatic go to the nextButton Step. Equivalent to pressing the Next button.
     */
    public void executeNext() {
        goStep(currentStep.next());
    }

    /**
     * Allows to programmatic go to the previousButton Step. Equivalent to pressing the Previous button.
     */
    public void executePrevious() {
        goStep(currentStep.previous());
    }

    /**
     * Interface that a wizard step must implement.
     *
     * @author Tamino Hartmann
     */
    public interface Step {
        /**
         * This method will be called when NEXT is pressed. It should return the next Step the wizard is to go to once
         * this one is finished.
         *
         * @return The next Step. If null the wizard will not continue.
         */
        public Step next();

        /**
         * This method is called when PREVIOUS is pressed. It should return the previous Step that the wizard is supposed
         * to return to.
         *
         * @return The previous Step. If null the wizard will not continue.
         */
        public Step previous();

        /**
         * Called to generate the JPanel the wizard will display when this step is to be shown. Guaranteed fresh!
         *
         * @return The JPanel to display.
         */
        public JPanel draw();
    }

    /**
     * Interface for callback when the wizard finishes / is closed. Afterwards Wizard will hide and destroy the window.
     */
    public interface WizardClose {
        /**
         * Method that is called when the Wizard is to be closed.
         */
        public void onWizardClose();
    }

    /**
     * Action receiver that handles the wizard button presses.
     */
    private class ActionReceiver implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            if (e.getActionCommand().equals(NEXT)) {
                // go to the next step
                goStep(currentStep.next());
            } else if (e.getActionCommand().equals(PREVIOUS)) {
                // go to the previous step
                goStep(currentStep.previous());
            } else {
                JOptionPane.showMessageDialog(new JDialog(), "The received action event is unknown!", "Error", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
}
