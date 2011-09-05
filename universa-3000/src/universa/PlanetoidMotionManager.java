package universa;

import java.util.ArrayList;
import java.util.List;

import org.cogaen.core.Core;
import org.cogaen.core.UpdateableService;
import org.cogaen.event.EventManager;
import org.cogaen.logging.LoggingService;
import org.cogaen.motion.CollisionEvent;
import org.cogaen.time.TimeService;
import org.cogaen.time.Timer;

import universa.events.EntityMovedEvent;

public class PlanetoidMotionManager implements UpdateableService {

	public static final String NAME = "planetoidmotionmanager";
	
	private List<Planetoid> planetoids = new ArrayList<Planetoid>();
	private Core core;
	private EventManager evtMngr;
	private String timerName;
	private Timer timer;

	public PlanetoidMotionManager() {
		this(TimeService.DEFAULT_TIMER);
	}
	
	public PlanetoidMotionManager(String timerName) {
		this.timerName = timerName;
	}

	@Override
	public void initialize(Core core) {
		this.core = core;
		this.evtMngr = EventManager.getInstance(this.core);
		this.timer = TimeService.getInstance(this.core).getTimer(this.timerName);
	}
	
	public static PlanetoidMotionManager getInstance(Core core) {
		return (PlanetoidMotionManager) core.getService(NAME);
	}
			
	public void addBody(Planetoid planetoid) {
		this.planetoids.add(planetoid);
	}
	
	public void removeBody(Planetoid planetoid) {
		this.planetoids.remove(planetoid);
	}
		
	@Override
	public void update() {
		double dt = this.timer.getDeltaTime();
		for (Planetoid planetoid : this.planetoids) {
			planetoid.update(dt);
			this.evtMngr.enqueueEvent(new EntityMovedEvent(planetoid.getName(), planetoid.getPos(), planetoid.getVel()));
		}
		doCollisionTest();
	}
	
	private void doCollisionTest() {
		for (int i = 0; i < this.planetoids.size(); ++i) {
			Planetoid p1 = this.planetoids.get(i);
			for (int j = i + 1; j < this.planetoids.size(); ++j) {
				Planetoid p2 = this.planetoids.get(j);
				
				if (p1.isColliding(p2)) {
					this.evtMngr.enqueueEvent( new CollisionEvent(p1.getName(), p2.getName()) );					
				}
				
			}
		}		
	}
		
	@Override
	public String getName() {
		return NAME;
	}

}
