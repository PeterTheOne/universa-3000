package universa.events;

import java.util.ArrayList;

import org.cogaen.event.Event;
import org.cogaen.event.EventType;

public class CollisionEvent extends Event {

	public static EventType TYPE = new EventType("Collision");
	private ArrayList<String> collidingPlanetoids;
	
	public CollisionEvent(ArrayList<String> collidingPlanetoids) {
		this.collidingPlanetoids = collidingPlanetoids;
	}
	
	public ArrayList<String> getCollidingPlanetoids() {
		return this.collidingPlanetoids;
	}

	@Override
	public EventType getType() {
		return TYPE;
	}

}
