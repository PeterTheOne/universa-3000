package universa.states;

import org.cogaen.core.Core;
import org.cogaen.entity.Entity;
import org.cogaen.entity.EntityManager;
import org.cogaen.event.Event;
import org.cogaen.event.EventListener;
import org.cogaen.event.EventManager;
import org.cogaen.motion.CollisionEvent;
import org.cogaen.resource.ResourceManager;
import org.cogaen.state.GameState;
import org.cogaen.view.View;

import universa.Planetoid;
import universa.Vector2f;
import universa.view.PlayView;

public class PlayState implements GameState, EventListener {

	public static String NAME = "Play";

	private Core core;
	private View view;

	public PlayState(Core core) {
		this.core = core;
		this.view = new PlayView(core);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void onEnter() {
		EventManager.getInstance(this.core).addListener(this, CollisionEvent.TYPE);
		ResourceManager.getInstance(this.core).loadGroup(NAME);
		this.view.engage();

		for (int i = 0; i < 20; i++) {
			Vector2f pos = new Vector2f(Math.random(), Math.random());
			pos = pos.multi(40d).sub(20d);
			Planetoid planetoid = new Planetoid(this.core, pos, 0.1);
			Vector2f vel = new Vector2f(Math.random(), Math.random());
			vel = vel.multi(1d).sub(0.5d);
			planetoid.setVel(vel);
			EntityManager.getInstance(this.core).addEntity(planetoid);
		}
	}

	@Override
	public void onExit() {
		EntityManager.getInstance(this.core).removeAllEntities();

		this.view.disengage();
		ResourceManager.getInstance(this.core).unloadGroup(NAME);
		EventManager.getInstance(this.core).removeListener(this);
	}

	@Override
	public void handleEvent(Event event) {
		if (event.isOfType(CollisionEvent.TYPE)) {
			handleCollisionEvent((CollisionEvent) event);
		}
	}

	private void handleCollisionEvent(CollisionEvent event) {
		//TODO: cleanup
		EntityManager entMngr = EntityManager.getInstance(this.core);
		Entity firstEntity = entMngr.getEntity(event.getFirstEntity());
		Entity secondEntity = entMngr.getEntity(event.getSecondEntity());
		if (firstEntity == null || secondEntity == null) {
			//TODO: error
			return;
		}
		Planetoid firstPlanetoid = null;
		Planetoid secondPlanetoid = null;
		if (firstEntity instanceof Planetoid && secondEntity instanceof Planetoid ) {
			firstPlanetoid = (Planetoid) firstEntity;
			secondPlanetoid = (Planetoid) secondEntity;
		} else {
			//TODO: error
			return;
		}
		
		double massRelation = secondPlanetoid.getMass() / (secondPlanetoid.getMass() + firstPlanetoid.getMass());
		Vector2f pos = firstPlanetoid.getPos().interpolate(secondPlanetoid.getPos(), massRelation);
		double mass = firstPlanetoid.getMass() + secondPlanetoid.getMass();
		Planetoid planetoid = new Planetoid(this.core, pos, mass);
		Vector2f vel = firstPlanetoid.getVel().interpolate(secondPlanetoid.getVel(), massRelation);
		planetoid.setVel(vel);

		entMngr.removeEntity(firstEntity);
		entMngr.removeEntity(secondEntity);
		entMngr.addEntity(planetoid);
	}

}
