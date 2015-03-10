package eu.hartmann.wizard;

import javax.swing.*;

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
