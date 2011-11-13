package universa.component;

import org.cogaen.entity.Component;
import org.cogaen.event.EventService;

import universa.entity.Pose2D;
import universa.events.EntityCreatedEvent;
import universa.events.EntityDestroyedEvent;

public class LifecycleComponent extends Component {

	@Override
	public void engage() {
		super.engage();
		
		Pose2D pose = (Pose2D) getParent().getAttribute(Pose2D.ATTR_ID);
		EntityCreatedEvent event = new EntityCreatedEvent(getParent().getId(), getParent().getType(), pose);
		EventService.getInstance(getCore()).dispatchEvent(event);
	}

	@Override
	public void disengage() {
		Pose2D pose = (Pose2D) getParent().getAttribute(Pose2D.ATTR_ID);
		EntityDestroyedEvent event = new EntityDestroyedEvent(getParent().getId(), getParent().getType(), pose);
		EventService.getInstance(getCore()).dispatchEvent(event);
		
		super.disengage();
	}

}
