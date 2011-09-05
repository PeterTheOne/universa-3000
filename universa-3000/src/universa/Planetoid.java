package universa;

import org.cogaen.core.Core;
import org.cogaen.entity.Entity;
import org.cogaen.entity.EntityManager;
import org.cogaen.name.NameService;

public class Planetoid extends Entity {

	public static String TYPE = "Planetoid";

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
		PlanetoidMotionManager.getInstance(getCore()).addBody(this);
	}

	@Override
	protected void tearDown() {
		PlanetoidMotionManager.getInstance(getCore()).removeBody(this);
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
				//TODO: fix constant
				double constant = 10;
				double force = constant * this.mass * planetoid.getMass() / Math.pow(distance, 2);
				this.vel = this.vel.add(ab.multi(force));
			}
		}
		
		//TODO: replace Vector class
		//Inertia & dt
		this.pos = this.pos.add(this.vel.multi(dt).div(this.mass));

		//bounce-border
		/*if ((this.pos.getX() > 20 && this.vel.getX() > 0 ) || (this.pos.getX() < -20 && this.vel.getX() < 0)) {
			this.vel.setX(-this.vel.getX());
		}
		if ((this.pos.getY() > 20 && this.vel.getY() > 0 ) || (this.pos.getY() < -20 && this.vel.getY() < 0)) {
			this.vel.setY(-this.vel.getY());
		}*/
	}

	public boolean isColliding(Planetoid p2) {
		return p2.getPos().sub(this.pos).length() < p2.getRadius() + this.getRadius();
	}
}
