package eu.hartmann.wizard;

import javax.swing.*;
import java.awt.*;

/**
 * A simple test class that shows how to use the wizard.
 *
 * @author Tamino Hartmann
 */
public class Main {

    /**
     * Variable that proves that we can move values between Steps.
     */
    private String name;

    public Main() {
        // create a new wizard with the given title and a fixed window size
        Wizard wizard = new Wizard("Demo Wizard", new Dimension(600, 400));
        // and now start the wizard with the given Step
        wizard.start(new StartWizard());
        // the rest happen in Wizard, so done!
    }

    public static void main(String[] args) {
        // we initialize a class so that we don't have to make all the steps static
        new Main();
    }

    /**
     * First panel.
     */
    private class StartWizard implements Step {

        @Override
        public Step next() {
            // give the wizard the next step. Usually as new Step, but you could reuse objects too.
            return new NameInputStep();
        }

        @Override
        public Step previous() {
            // This means nothing happens when the user presses previous. You could make the button exit the wizard
            // if you wanted a sensible alternative.
            return null;
        }

        @Override
        public JPanel draw() {
            // draw() always returns a panel in which you can do what you want.
            JPanel test = new JPanel();
            // here we just write a bit of text:
            test.add(new JLabel("Welcome to the wizard test wizard! Press next to continue."));
            return test;
        }
    }

    /**
     * Second panel with text entry.
     */
    private class NameInputStep implements Step {

        private JTextField text;

        @Override
        public Step next() {
            // read name from textfield on next
            name = text.getText();
            // go to next step
            return new GreeterStep();
        }

        @Override
        public Step previous() {
            // return to previous step
            return new StartWizard();
        }

        @Override
        public JPanel draw() {
            // draw a bit more
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            JLabel prompt = new JLabel("Please enter your name:");
            text = new JTextField();
            panel.add(prompt);
            panel.add(text);
            return panel;
        }
    }

    /**
     * Final panel where we read the name and display it.
     */
    private class GreeterStep implements Step {

        @Override
        public Step next() {
            // use this to close the wizard:
            return Wizard.EXIT(new Step() {
                @Override
                public Step next() {
                    // note that whatever you write here won't be called due to how EXIT works.
                    return null;
                }

                @Override
                public Step previous() {
                    // note that whatever you write here won't be called due to how EXIT works.
                    return null;
                }

                @Override
                public JPanel draw() {
                    // note: only draw will be called on Wizard.EXIT.
                    JPanel panel = new JPanel();
                    panel.add(new JLabel("Quitting!"));
                    return panel;
                }
            });
        }

        @Override
        public Step previous() {
            return new NameInputStep();
        }

        @Override
        public JPanel draw() {
            // catch to be sure
            if (name == null || name.isEmpty()) {
                name = "Anonymous";
            }
            JPanel panel = new JPanel();
            panel.add(new JLabel("Hello " + name + "! As you can see the name has been updated!"));
            return panel;
        }
    }
}
