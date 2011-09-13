package universa.events;

import org.cogaen.event.Event;
import org.cogaen.event.EventType;

public class GamespeedChangedEvent extends Event {
	
	public static EventType TYPE = new EventType("GamespeedChanged");
	private double gamespeed;
	
	public GamespeedChangedEvent(double gamespeed) {
		this.gamespeed = gamespeed;
	}
	
	public double getGamespeed() {
		return this.gamespeed;
	}

	@Override
	public EventType getType() {
		return TYPE;
	}

}
