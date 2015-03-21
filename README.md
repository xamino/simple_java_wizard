# Simple Java Wizard
This is a simple Java based wizard implementation because all the ones we tried
didn't do what we wanted. This one basically promises not to eat anything it
shouldn't.

## Usage
Really simple actually. See the Main class for a demonstration. Basically you
give the Wizard class a starting object that implement the Step interface. Steps
define their previous and next neighbours, dynamically changeable. Linear and
more complex wizard flows are thus easily possible. The Wizard class handles the
window, the Steps only have to handle their one JPanel. Wizard will flow through
the sequence of Steps as the programmer has coded them. Wizard can be neatly
closed with wizard.createExit(Step step) which will transform the given step into a
special closing step with the given Step's content.

The advancement of Steps can also be called programmatically, a feature that
I required for where I used this little framework.

## Requirements
You'll need to use Java 8 for the code to work without further modifications,
but the code can easily be reconfigured for previous versions.

## License
MIT – please attribute but otherwise feel free to do what you want with it.
