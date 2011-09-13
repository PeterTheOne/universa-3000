package cogaenfix;

import org.cogaen.event.Event;
import org.cogaen.event.EventType;

public class MouseDraggedEvent extends Event {

	public static final EventType TYPE = new EventType("MouseDragged");
	
	private int posX;
	private int posY;
	
	public MouseDraggedEvent(int posX, int posY) {
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
	public EventType getType() {
		return TYPE;
	}

}
