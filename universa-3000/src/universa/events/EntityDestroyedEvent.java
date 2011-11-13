package universa.events;

import org.cogaen.event.Event;
import org.cogaen.name.CogaenId;

import universa.entity.Pose2D;

public class EntityDestroyedEvent extends Event {

	public static final CogaenId TYPE_ID = new CogaenId("Destroy");

	private CogaenId entityId;
	private CogaenId entityTypeId;
	private double posX;
	private double posY;
	
	public EntityDestroyedEvent(CogaenId entityId, CogaenId entityTypeId, Pose2D pose) {
		super();
		this.entityTypeId = entityTypeId;
		this.entityId = entityId;
		this.posX = pose.getPosX();
		this.posY = pose.getPosY();
	}

	@Override
	public CogaenId getTypeId() {
		return TYPE_ID;
	}

	public CogaenId getEntityTypeId() {
		return entityTypeId;
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
