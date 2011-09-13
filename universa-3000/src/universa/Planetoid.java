package universa;

import org.cogaen.core.Core;
import org.cogaen.entity.Entity;
import org.cogaen.entity.EntityManager;
import org.cogaen.name.NameService;

import cogaenfix.Vector2f;

public class Planetoid extends Entity {

	public static String TYPE = "Planetoid";
	public static double TOTAL_MASS = 0;
	public static int TOTAL_COUNT = 0;
	
	private static double G = 0.000000000066738480d;

	private Vector2f pos;
	private Vector2f vel;

	private static int DENSITY = 5;
	private double mass;

	public Planetoid(Core core, Vector2f pos, double mass) {
		this(core, NameService.getInstance(core).generateName(), pos, mass);
	}

	public Planetoid(Core core, String name, Vector2f pos, double mass) {
		super(core, name);
		this.pos = pos;
		this.vel = new Vector2f();
		this.mass = mass;
	}

	public double getVolume() {
		return mass / (double) DENSITY;
	}

	public double getRadius() {
		return Math.pow(getVolume() * (4 / (double) 3) * Math.PI,
				1 / (double) 3);
	}

	public Vector2f getPos() {
		return this.pos;
	}

	public Vector2f getVel() {
		return this.vel;
	}

	public void setVel(Vector2f vel) {
		this.vel = vel;
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
		MotionManager.getInstance(getCore()).addBody(this);
		TOTAL_MASS += mass;
		TOTAL_COUNT++;
	}

	@Override
	protected void tearDown() {
		TOTAL_MASS -= mass;
		TOTAL_COUNT--;
		MotionManager.getInstance(getCore()).removeBody(this);
	}

	@Override
	public void update() {
		
	}

	public void update(double dt) {
		//TODO: should this be done here?
		EntityManager entMngr = EntityManager.getInstance(getCore());
		for (int i = 0; i < entMngr.getNumEntities(); i++) {
			Entity entity = entMngr.getEntity(i);
			if (entity.getType().equals(TYPE) && !entity.equals(this)) {
				Planetoid planetoid = (Planetoid) entity;
				Vector2f ab = planetoid.getPos().sub(this.getPos());
				double distance = ab.length();
				if (distance == 0.0) {
					distance = Double.MIN_VALUE;
				}
				ab.normalize();
				double force = 10 * this.mass * planetoid.getMass() / Math.pow(distance, 2);
				this.vel = this.vel.add(ab.multi(force));
			}
		}
		
		//Inertia & dt
		this.pos = this.pos.add(this.vel.multi(dt).div(this.mass));
	}

	public boolean isColliding(Planetoid p2) {
		return p2.getPos().sub(this.pos).length() < p2.getRadius() + this.getRadius();
	}
}
