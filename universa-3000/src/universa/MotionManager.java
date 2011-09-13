package universa;

import java.util.ArrayList;
import java.util.List;

import org.cogaen.core.Core;
import org.cogaen.core.UpdateableService;
import org.cogaen.event.EventManager;
import org.cogaen.logging.LoggingService;
import org.cogaen.time.TimeService;
import org.cogaen.time.Timer;

import universa.events.CollisionEvent;
import universa.events.EntityMovedEvent;

//TODO: extend cogaen.MontionManager?
public class MotionManager implements UpdateableService {

	public static final String NAME = "universa.motionmanager";
	
	private List<Planetoid> planetoids = new ArrayList<Planetoid>();
	private Core core;
	private EventManager evtMngr;
	private String timerName;
	private Timer timer;
	
	ArrayList<ArrayList<String>> collisionGroups = new ArrayList<ArrayList<String>>();

	public MotionManager() {
		this(TimeService.DEFAULT_TIMER);
	}
	
	public MotionManager(String timerName) {
		this.timerName = timerName;
	}

	@Override
	public void initialize(Core core) {
		this.core = core;
		this.evtMngr = EventManager.getInstance(this.core);
		this.timer = TimeService.getInstance(this.core).getTimer(this.timerName);
	}
	
	public static MotionManager getInstance(Core core) {
		return (MotionManager) core.getService(NAME);
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
			EntityMovedEvent event = new EntityMovedEvent(planetoid.getName(), 
					planetoid.getPos(), planetoid.getVel(), planetoid.getMass());
			this.evtMngr.enqueueEvent(event);
		}
		doCollisionTest();
	}
	
	private void doCollisionTest() {
		collisionGroups.clear();
		
		for (int i = 0; i < this.planetoids.size(); ++i) {
			Planetoid p1 = this.planetoids.get(i);
			for (int j = i + 1; j < this.planetoids.size(); ++j) {
				Planetoid p2 = this.planetoids.get(j);
				
				if (p1.isColliding(p2)) {
					ArrayList<String> p1FoundGroup = null;
					ArrayList<String> p2FoundGroup = null;
					for (ArrayList<String> group : collisionGroups) {
						// check p1 or p2 has already collided 
						for (String name : group) {
							if (name.equals(p1.getName())) {
								p1FoundGroup = group;
							}
							if (name.equals(p2.getName())) {
								p2FoundGroup = group;
							}
						}
					}
					
					if (p1FoundGroup != null && p2FoundGroup != null) {
						// merge groups
						if (p1FoundGroup.equals(p2FoundGroup)) {
							continue;
						}
						ArrayList<String> newGroup = new ArrayList<String>();
						for (String name : p1FoundGroup) {
							newGroup.add(name);
						}
						for (String name : p2FoundGroup) {
							newGroup.add(name);
						}
						this.collisionGroups.remove(p1FoundGroup);
						this.collisionGroups.remove(p2FoundGroup);
						this.collisionGroups.add(newGroup);
					} else if (p1FoundGroup != null && p2FoundGroup == null) {
						//add to group
						p1FoundGroup.add(p2.getName());
					} else if (p1FoundGroup == null && p2FoundGroup != null) {
						//add to group
						p2FoundGroup.add(p1.getName());
					} else if (p1FoundGroup == null && p2FoundGroup == null) {
						// create new groupe
						ArrayList<String> newGroup = new ArrayList<String>();
						newGroup.add(p1.getName());
						newGroup.add(p2.getName());
						this.collisionGroups.add(newGroup);
					}
				}
				
			}
		}
		
		for (ArrayList<String> group : collisionGroups) {
			EventManager.getInstance(this.core).enqueueEvent(new CollisionEvent(group));
		}
	}

	@Override
	public String getName() {
		return NAME;
	}

}
