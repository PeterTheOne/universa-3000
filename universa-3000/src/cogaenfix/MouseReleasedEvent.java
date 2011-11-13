package cogaenfix;

import org.cogaen.event.Event;
import org.cogaen.name.CogaenId;

public class MouseReleasedEvent extends Event {

	public static final CogaenId TYPE_ID = new CogaenId("MouseReleased");
	
	private int button;
	private int posX;
	private int posY;
	
	public MouseReleasedEvent(int button, int posX, int posY) {
		this.button = button;
		this.posX = posX;
		this.posY = posY;
	}
	
	public int getX() {
		return posX;
	}
	
	public int getY() {
		return posY;
	}
	
	public int getButton() {
		return this.button;
	}

	@Override
	public CogaenId getTypeId() {
		return TYPE_ID;
	}

}
