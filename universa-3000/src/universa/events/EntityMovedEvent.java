package universa.events;

import org.cogaen.event.Event;
import org.cogaen.event.EventType;

import cogaenfix.Vector2f;


public class EntityMovedEvent extends Event {
	
	public static EventType TYPE = new EventType("EntityMoved");
	
	private String name;
	private Vector2f pos;
	private Vector2f vel;

	public EntityMovedEvent(String name, Vector2f pos, Vector2f vel) {
		this.name = name;
		this.pos = pos;
		this.vel = vel;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Vector2f getPos() {
		return this.pos;
	}
	
	public Vector2f getVel() {
		return this.vel;
	}
	
	@Override
	public EventType getType() {
		return TYPE;
	}

}
