# Simple Java Wizard
This is a simple Java based wizard implementation because all the ones we tried
didn't do what we wanted. This one basically promises not to eat anything it
shouldn't.

## Usage
Really simple actually. See the Main class for a demonstration. Basically you
give the Wizard class objects that implement the Step interface. Steps define
their previous and next neighbours, dynamically changeable. Linear and more
complex wizard flows are thus easily possible.

## License
MIT – please attribute but otherwise feel free to do what you want with it.
