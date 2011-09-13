package cogaenfix;

public class KeyState {

	private volatile boolean pressed;

	public boolean isPressed() {
		return pressed;
	}
	
	public void setPressed(boolean pressed) {
		this.pressed = pressed;
	}
}
