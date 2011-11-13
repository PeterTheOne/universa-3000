package universa.events;

import org.cogaen.event.Event;
import org.cogaen.name.CogaenId;

public class GamespeedChangedEvent extends Event {
	
	public static final CogaenId TYPE_ID = new CogaenId("GamespeedChanged");
	private double gamespeed;
	
	public GamespeedChangedEvent(double gamespeed) {
		this.gamespeed = gamespeed;
	}
	
	public double getGamespeed() {
		return this.gamespeed;
	}

	@Override
	public CogaenId getTypeId() {
		return TYPE_ID;
	}

}
