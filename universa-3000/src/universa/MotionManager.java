package universa;

import java.util.ArrayList;
import java.util.List;

import org.cogaen.core.Core;
import org.cogaen.core.ServiceException;
import org.cogaen.core.UpdateableService;
import org.cogaen.event.EventService;
import org.cogaen.name.CogaenId;
import org.cogaen.time.TimeService;
import org.cogaen.time.Timer;

import universa.events.CollisionEvent;
import universa.events.PoseUpdateEvent;

public class MotionManager extends UpdateableService {

	public static final CogaenId ID = new CogaenId("universa.motionmanager");
	public static final String NAME = "Motion Manager";
	
	private List<Planetoid> planetoids = new ArrayList<Planetoid>();
	private EventService evtSrv;
	private Timer timer;
	
	ArrayList<ArrayList<CogaenId>> collisionGroups = new ArrayList<ArrayList<CogaenId>>();
	
	public MotionManager() {
		addDependency(TimeService.ID);
	}

	@Override
	public void doStart() throws ServiceException {
		super.doStart();
		this.evtSrv = EventService.getInstance(getCore());
		this.timer = TimeService.getInstance(getCore()).getTimer();
	}
	
	public static MotionManager getInstance(Core core) {
		return (MotionManager) core.getService(ID);
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
					ArrayList<CogaenId> p1FoundGroup = null;
					ArrayList<CogaenId> p2FoundGroup = null;
					for (ArrayList<CogaenId> group : collisionGroups) {
						// check p1 or p2 has already collided 
						for (CogaenId id : group) {
							if (id.equals(p1.getId())) {
								p1FoundGroup = group;
							}
							if (id.equals(p2.getId())) {
								p2FoundGroup = group;
							}
						}
					}
					
					if (p1FoundGroup != null && p2FoundGroup != null) {
						// merge groups
						if (p1FoundGroup.equals(p2FoundGroup)) {
							continue;
						}
						ArrayList<CogaenId> newGroup = new ArrayList<CogaenId>();
						for (CogaenId id : p1FoundGroup) {
							newGroup.add(id);
						}
						for (CogaenId id : p2FoundGroup) {
							newGroup.add(id);
						}
						this.collisionGroups.remove(p1FoundGroup);
						this.collisionGroups.remove(p2FoundGroup);
						this.collisionGroups.add(newGroup);
					} else if (p1FoundGroup != null && p2FoundGroup == null) {
						//add to group
						p1FoundGroup.add(p2.getId());
					} else if (p1FoundGroup == null && p2FoundGroup != null) {
						//add to group
						p2FoundGroup.add(p1.getId());
					} else if (p1FoundGroup == null && p2FoundGroup == null) {
						// create new group
						ArrayList<CogaenId> newGroup = new ArrayList<CogaenId>();
						newGroup.add(p1.getId());
						newGroup.add(p2.getId());
						this.collisionGroups.add(newGroup);
					}
				}
				
			}
		}
		
		for (ArrayList<CogaenId> group : collisionGroups) {
			EventService.getInstance(getCore()).dispatchEvent(new CollisionEvent(group));
		}
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public CogaenId getId() {
		return ID;
	}

}
