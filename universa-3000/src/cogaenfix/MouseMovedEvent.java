package cogaenfix;

import org.cogaen.event.Event;
import org.cogaen.name.CogaenId;

public class MouseMovedEvent extends Event {

	public static final CogaenId TYPE_ID = new CogaenId("MouseMoved");
	
	private int posX;
	private int posY;
	
	public MouseMovedEvent(int posX, int posY) {
		this.posX = posX;
		this.posY = posY;
	}
	
	public int getX() {
		return posX;
	}
	
	public int getY() {
		return posY;
	}

	@Override
	public CogaenId getTypeId() {
		return TYPE_ID;
	}

}
