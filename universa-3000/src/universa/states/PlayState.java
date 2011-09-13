package universa.states;

import java.util.ArrayList;

import org.cogaen.core.Core;
import org.cogaen.entity.Entity;
import org.cogaen.entity.EntityManager;
import org.cogaen.event.Event;
import org.cogaen.event.EventListener;
import org.cogaen.event.EventManager;
import org.cogaen.logging.LoggingService;
import org.cogaen.resource.ResourceManager;
import org.cogaen.state.GameState;
import org.cogaen.view.View;

import cogaenfix.Vector2f;

import universa.Planetoid;
import universa.events.CollisionEvent;
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
		EventManager evtMngr = EventManager.getInstance(this.core);
		evtMngr.addListener(this, CollisionEvent.TYPE);		
		ResourceManager.getInstance(this.core).loadGroup(NAME);
		this.view.engage();

		for (int i = 0; i < 200; i++) {
			//TODO: bug when vector stays the same
			double rand1 = Math.random();
			double rand2;
			do {
				rand2 = Math.random();
			} while (1 < rand1 * rand1 + rand2 * rand2);
			Vector2f pos = new Vector2f(Math.random(), Math.random());
			pos = pos.multi(80d).sub(40d);
			Planetoid planetoid = new Planetoid(this.core, pos, 10 / Math.pow(pos.length(), 2));
			Vector2f vel = new Vector2f(pos.normalize().getY(), -pos.normalize().getX());
			vel = vel.multi(60d / pos.length());
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
		EntityManager entMngr = EntityManager.getInstance(this.core);
		ArrayList<Planetoid> planetoids = new ArrayList<Planetoid>();
		ArrayList<String> group = event.getCollidingPlanetoids();
		for (String name : group) {
			Entity entity;
			if ((entity = entMngr.getEntity(name)) != null && 
					entity instanceof Planetoid) {
				planetoids.add((Planetoid) entity);
			} else {
				LoggingService.getInstance(this.core).logError("Planetoid", "entity not found, cannot merge");
				return;
			}
		}
		
		int size = planetoids.size();

		double mass = 0;
		for (Planetoid planetoid : planetoids) {
			mass += planetoid.getMass();
		}
		
		Vector2f pos;
		Vector2f vel;

		if (size <= 2) {
			Planetoid p1 = planetoids.get(0);
			Planetoid p2 = planetoids.get(1);
			double massRelation = p2.getMass() / mass;
			pos = p1.getPos().interpolate(p2.getPos(), massRelation);
			vel = p1.getVel().interpolate(p2.getVel(), massRelation);
		} else if (size == 3) {
			Planetoid p1 = planetoids.get(0);
			Planetoid p2 = planetoids.get(1);
			Planetoid p3 = planetoids.get(2);
			double massRelationP2 = p2.getMass() / mass;
			double massRelationP3 = p3.getMass() / mass;
			pos = p1.getPos().interpolate(p2.getPos(), massRelationP2, p3.getPos(), massRelationP3);
			vel = p1.getVel().interpolate(p2.getVel(), massRelationP2, p3.getPos(), massRelationP3);
		} else {
			//TODO: mass interpolation
			pos = new Vector2f();
			vel = new Vector2f();
			for (Planetoid planetoid : planetoids) {
				pos = pos.add(planetoid.getPos());
				vel = vel.add(planetoid.getVel());
				if (!entMngr.hasEntity(planetoid.getName())) {
					LoggingService.getInstance(this.core).logError("Planetoid", "cannot remove");
				}
			}
			pos = pos.div(size);
			vel = vel.div(size);
		}
		
		for (Planetoid planetoid : planetoids) {
			entMngr.removeEntity(planetoid);
		}
		Planetoid newPlanetoid = new Planetoid(this.core, pos, mass);
		newPlanetoid.setVel(vel);
		entMngr.addEntity(newPlanetoid);
	}

}
