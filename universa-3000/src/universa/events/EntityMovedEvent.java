package universa.events;

import org.cogaen.event.Event;
import org.cogaen.event.EventType;
import org.cogaen.motion.Vector;

public class EntityMovedEvent extends Event {
	
	public static EventType TYPE = new EventType("EntityMoved");
	
	private String name;
	private Vector pos;
	private Vector vel;

	public EntityMovedEvent(String name, Vector pos, Vector vel) {
		this.name = name;
		this.pos = pos;
		this.vel = vel;
	}
	
	public String getName() {
		return this.name;
	}
	
	public Vector getPos() {
		return this.pos;
	}
	
	public Vector getVel() {
		return this.vel;
	}
	
	@Override
	public EventType getType() {
		return TYPE;
	}

}
