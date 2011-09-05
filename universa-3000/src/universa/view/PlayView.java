package universa.view;

import java.awt.Color;

import org.cogaen.core.Core;
import org.cogaen.entity.EntityCreatedEvent;
import org.cogaen.entity.EntityDestroyedEvent;
import org.cogaen.entity.EntityManager;
import org.cogaen.event.Event;
import org.cogaen.event.EventListener;
import org.cogaen.event.EventManager;
import org.cogaen.java2d.Camera;
import org.cogaen.java2d.CircleVisual;
import org.cogaen.java2d.SceneManager;
import org.cogaen.java2d.SceneNode;
import org.cogaen.view.AbstractView;

import universa.Planetoid;
import universa.Vector2f;
import universa.events.EntityMovedEvent;

public class PlayView extends AbstractView implements EventListener {

	private SceneManager scnMngr;

	public PlayView(Core core) {
		super(core);
		this.scnMngr = SceneManager.getInstance(core);
	}

	@Override
	public void engage() {
		this.scnMngr.setClearBackground(true);
		this.scnMngr.setBackgroundColor(Color.black);

		Camera cam = this.scnMngr.createCamera();
		// 15 = wie viele meter will ich sehn..
		cam.setZoom(this.scnMngr.getScreen().getWidth() / 60);

		EventManager evtMngr = EventManager.getInstance(getCore());
		evtMngr.addListener(this, EntityCreatedEvent.TYPE);
		evtMngr.addListener(this, EntityDestroyedEvent.TYPE);
		evtMngr.addListener(this, EntityMovedEvent.TYPE);
	}

	@Override
	public void disengage() {
		EventManager.getInstance(getCore()).removeListener(this);
		this.scnMngr.destroyAll();
	}

	public void registerResources(String group) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEvent(Event event) {
		if (event.isOfType(EntityCreatedEvent.TYPE)) {
			handleEntityCreated((EntityCreatedEvent) event);
		} else if (event.isOfType(EntityDestroyedEvent.TYPE)) {
			handleEntityDestroyedEvent((EntityDestroyedEvent) event);
		} else if (event.isOfType(EntityMovedEvent.TYPE)) {
			handleEntityMovedEvent((EntityMovedEvent) event);
		}
	}

	private void handleEntityCreated(EntityCreatedEvent event) {
		if (event.getEntityType().equals(Planetoid.TYPE)) {
			EntityManager entMngr = EntityManager.getInstance(getCore());
			Planetoid planetoid = (Planetoid) entMngr.getEntity(event.getEntityName());
			SceneNode scnNode = this.scnMngr.createSceneNode(event.getEntityName());
			CircleVisual circle = this.scnMngr.createCircleVisual(planetoid.getRadius());
			circle.setColor(Color.WHITE);			
			scnNode.addVisual(circle);
			SceneNode targetNode = this.scnMngr.createSceneNode(event.getEntityName() + "Target");
			CircleVisual targetCircle = this.scnMngr.createCircleVisual(planetoid.getRadius() / 10d);
			targetCircle.setColor(Color.BLUE);
			targetNode.addVisual(targetCircle);
			scnNode.addChild(targetNode);
			this.scnMngr.getRootSceneNode().addChild(scnNode);
		}
	}

	private void handleEntityDestroyedEvent(EntityDestroyedEvent event) {
		this.scnMngr.destroySceneNode(event.getEntityName());
	}

	private void handleEntityMovedEvent(EntityMovedEvent event) {
		SceneNode node = this.scnMngr.getSceneNode(event.getName());
		Vector2f pos = event.getPos();
		//setPose? rotation?
		node.setPose(pos.getX(), pos.getY(), 0);
		
		//TODO: no node.getChild(String name) ?		
		SceneNode targetNode = this.scnMngr.getSceneNode(event.getName() + "Target");
		Vector2f vel = event.getVel();
		targetNode.setPose(vel.getX(), vel.getY(), 0);
	}
}
