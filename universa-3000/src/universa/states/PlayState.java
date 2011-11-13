package universa.states;

import java.util.ArrayList;

import org.cogaen.core.Core;
import org.cogaen.entity.Entity;
import org.cogaen.entity.EntityService;
import org.cogaen.event.Event;
import org.cogaen.event.EventListener;
import org.cogaen.event.EventService;
import org.cogaen.logging.LoggingService;
import org.cogaen.name.CogaenId;
import org.cogaen.resource.ResourceService;
import org.cogaen.state.BasicState;
import org.cogaen.view.View;

import cogaenfix.Vector2f;

import universa.Planetoid;
import universa.events.CollisionEvent;
import universa.view.PlayView;

public class PlayState extends BasicState implements EventListener {
	
	public static final double WORLD_WIDTH = 1000;
	public static CogaenId ID = new CogaenId("Play");

	private View view;

	public PlayState(Core core) {
		super(core);
		this.view = new PlayView(core);
	}

	@Override
	public void onEnter() {
		EventService evtSrv = EventService.getInstance(getCore());
		evtSrv.addListener(this, CollisionEvent.TYPE_ID);		
		//ResourceService.getInstance(getCore()).loadGroup(ID);
		this.view.engage();

		Vector2f sunPos = new Vector2f(0, 0);
		Planetoid sun = new Planetoid(getCore(), sunPos, 400);
		EntityService.getInstance(getCore()).addEntity(sun);
		
		for (int i = 0; i < 20; i++) {
			//TODO: bug when vector stays the same
			double rand1 = (Math.random() * 2) - 1;
			double rand2;
			double d;
			do {
				rand2 = (Math.random() * 2) - 1;
				d = rand1 * rand1 + rand2 * rand2;
			} while (1 < d);
			Vector2f pos = new Vector2f(rand1, rand2);
			pos = pos.multi(500d);
			Planetoid planetoid = new Planetoid(getCore(), pos, 10);
			Vector2f vel = new Vector2f(pos.normalize().getY(), -pos.normalize().getX());
			vel = vel.multi(100000d / (pos.length() * 2));
			planetoid.setVel(vel);
			EntityService.getInstance(getCore()).addEntity(planetoid);
		}
	}

	@Override
	public void onExit() {
		EntityService.getInstance(getCore()).removeAllEntities();

		this.view.disengage();
		ResourceService.getInstance(getCore()).unloadGroup(ID);
		EventService.getInstance(getCore()).removeListener(this);
	}

	@Override
	public void handleEvent(Event event) {
		if (event.isOfType(CollisionEvent.TYPE_ID)) {
			handleCollisionEvent((CollisionEvent) event);
		}
	}

	private void handleCollisionEvent(CollisionEvent event) {
		EntityService entMngr = EntityService.getInstance(getCore());
		ArrayList<Planetoid> planetoids = new ArrayList<Planetoid>();
		ArrayList<CogaenId> group = event.getCollidingPlanetoids();
		for (CogaenId id : group) {
			Entity entity;
			if ((entity = entMngr.getEntity(id)) != null && 
					entity instanceof Planetoid) {
				planetoids.add((Planetoid) entity);
			} else {
				LoggingService.getInstance(getCore()).logError("Planetoid", 
						"entity not found, cannot merge");
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
				if (!entMngr.hasEntity(planetoid.getId())) {
					LoggingService.getInstance(getCore()).logError("Planetoid", "cannot remove");
				}
			}
			pos = pos.div(size);
			vel = vel.div(size);
		}
		
		for (Planetoid planetoid : planetoids) {
			entMngr.removeEntity(planetoid.getId());
		}
		Planetoid newPlanetoid = new Planetoid(getCore(), pos, mass);
		newPlanetoid.setVel(vel);
		entMngr.addEntity(newPlanetoid);
	}

}
