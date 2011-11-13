package universa.events;

import org.cogaen.event.Event;
import org.cogaen.name.CogaenId;

import universa.entity.Pose2D;

public class PoseUpdateEvent extends Event {

	public static final CogaenId TYPE_ID = new CogaenId("PoseUpdate");
	
	private CogaenId entityId;
	private double posX;
	private double posY;

	public PoseUpdateEvent(CogaenId entityId, Pose2D pose) {
		super();
		this.entityId = entityId;
		this.posX = pose.getPosX();
		this.posY = pose.getPosY();
	}

	@Override
	public CogaenId getTypeId() {
		return TYPE_ID;
	}
	
	public CogaenId getEntityId() {
		return entityId;
	}

	public double getPosX() {
		return posX;
	}

	public double getPosY() {
		return posY;
	}
}
