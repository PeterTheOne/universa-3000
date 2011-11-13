package universa.events;

import java.util.ArrayList;

import org.cogaen.event.Event;
import org.cogaen.name.CogaenId;
public class CollisionEvent extends Event {

	public static final CogaenId TYPE_ID = new CogaenId("Collision");
	private ArrayList<CogaenId> collidingPlanetoids;
	
	public CollisionEvent(ArrayList<CogaenId> collidingPlanetoids) {
		this.collidingPlanetoids = collidingPlanetoids;
	}
	
	public ArrayList<CogaenId> getCollidingPlanetoids() {
		return this.collidingPlanetoids;
	}

	@Override
	public CogaenId getTypeId() {
		return TYPE_ID;
	}

}
