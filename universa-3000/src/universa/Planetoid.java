package universa;

import org.cogaen.core.Core;
import org.cogaen.entity.Entity;
import org.cogaen.entity.EntityManager;
import org.cogaen.event.EventManager;
import org.cogaen.motion.Vector;
import org.cogaen.name.NameService;

import universa.events.EntityMovedEvent;

public class Planetoid extends Entity {

	public static String TYPE = "Planetoid";

	private Vector pos;
	private Vector vel;

	private static int DENSITY = 5;
	private double mass;

	public Planetoid(Core core, Vector pos, double mass) {
		this(core, NameService.getInstance(core).generateName(), pos, mass);
	}

	public Planetoid(Core core, String name, Vector pos, double mass) {
		super(core, name);
		this.pos = pos;
		this.vel = new Vector();
		this.mass = mass;
	}

	public double getVolume() {
		return mass / (double) DENSITY;
	}

	public double getRadius() {
		return Math.pow(getVolume() * (4 / (double) 3) * Math.PI,
				1 / (double) 3);
	}

	public Vector getPos() {
		return this.pos;
	}

	public double getMass() {
		return this.mass;
	}

	@Override
	public String getType() {
		return TYPE;
	}

	@Override
	protected void setUp() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void tearDown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void update() {
		//TODO: should this be done here?
		EntityManager entMngr = EntityManager.getInstance(getCore());
		for (int i = 0; i < entMngr.getNumEntities(); i++) {
			Entity entity = entMngr.getEntity(i);
			if (entity.getType().equals(TYPE) && !entity.equals(this)) {
				Planetoid planetoid = (Planetoid) entity;
				//TODO: replace Vector class
				Vector ab = new Vector(planetoid.getPos().x - this.getPos().x, 
						planetoid.getPos().y - this.getPos().y);
				double distance = ab.length();
				ab.normalize();
				//TODO: fix constant
				double constant = 0.1;
				//TODO: remove divition by zero
				double force = constant * this.mass * planetoid.getMass() / distance;
				this.vel = new Vector(this.vel.x + ab.x * force, this.vel.y + ab.y * force);
			}
		}
		
		//TODO: replace Vector class
		//this.pos = this.pos.add(this.vel);
		this.pos = new Vector(this.pos.x + this.vel.x, this.pos.y + this.vel.y);
		
		EventManager evntMngr = EventManager.getInstance(getCore());
		evntMngr.enqueueEvent(new EntityMovedEvent(getName(), this.pos, this.vel));
	}
}
