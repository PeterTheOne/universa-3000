package universa.events;

import org.cogaen.event.Event;
import org.cogaen.event.EventType;

import cogaenfix.Vector2f;


public class EntityMovedEvent extends Event {
	
	public static EventType TYPE = new EventType("EntityMoved");
	
	private String name;
	private Vector2f pos;
	private Vector2f vel;
	private double mass;

	public EntityMovedEvent(String name, Vector2f pos, Vector2f vel, double mass) {
		this.name = name;
		this.pos = pos;
		this.vel = vel;
		this.mass = mass;
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
	
	public double getMass() {
		return this.mass;
	}
	
	@Override
	public EventType getType() {
		return TYPE;
	}

}
