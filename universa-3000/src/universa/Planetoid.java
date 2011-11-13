package universa;

import org.cogaen.core.Core;
import org.cogaen.entity.ComponentEntity;
import org.cogaen.entity.Entity;
import org.cogaen.entity.EntityService;
import org.cogaen.event.EventService;
import org.cogaen.name.CogaenId;
import org.cogaen.name.IdService;

import universa.component.LifecycleComponent;
import universa.entity.Pose2D;
import universa.events.PoseUpdateEvent;

import cogaenfix.Vector2f;

public class Planetoid extends ComponentEntity implements Pose2D {

	public static CogaenId TYPE_ID = new CogaenId("Planetoid");
	public static double TOTAL_MASS = 0;
	public static int TOTAL_COUNT = 0;
	
	private static double G = 100d;

	private Vector2f pos;
	private Vector2f vel;

	private static int DENSITY = 5;
	private double mass;

	public Planetoid(Core core, Vector2f pos, double mass) {
		this(core, IdService.getInstance(core).generateId(), pos, mass);
	}

	public Planetoid(Core core, CogaenId id, Vector2f pos, double mass) {
		super(core, id, TYPE_ID);

		addComponent(new LifecycleComponent());
		addAttribute(Pose2D.ATTR_ID, this);
		
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
	public CogaenId getType() {
		return TYPE_ID;
	}

	@Override
	public void engage() {
		super.engage();
		MotionManager.getInstance(getCore()).addBody(this);
		TOTAL_MASS += mass;
		TOTAL_COUNT++;
	}

	public void disengage() {
		TOTAL_MASS -= mass;
		TOTAL_COUNT--;
		MotionManager.getInstance(getCore()).removeBody(this);
		super.disengage();
	}

	public void update(double dt) {
		Vector2f acc = new Vector2f();
		EntityService entSvr = EntityService.getInstance(getCore());
		for (int i = 0; i < entSvr.numEntities(); i++) {
			Entity entity = entSvr.getEntity(i);
			if (entity.getType().equals(TYPE_ID) && !entity.equals(this)) {
				Planetoid planetoid = (Planetoid) entity;
				Vector2f ab = planetoid.getPos().sub(this.getPos());
				double distance = ab.length();
				if (distance == 0.0) {
					distance = Double.MIN_VALUE;
				}
				ab.normalize();
				double force = G * this.mass * planetoid.getMass() / Math.pow(distance, 2);
				acc = acc.add(ab.multi(force).div(this.mass));
			}
		}
		
		this.vel = this.vel.add(acc.multi(dt));
		this.pos = this.pos.add(this.vel.multi(dt));

		PoseUpdateEvent event = new PoseUpdateEvent(getId(), this);
		EventService.getInstance(getCore()).dispatchEvent(event);
	}

	public boolean isColliding(Planetoid p2) {
		return p2.getPos().sub(this.pos).length() < p2.getRadius() + this.getRadius();
	}

	@Override
	public double getPosX() {
		return this.pos.getX();
	}

	@Override
	public double getPosY() {
		return this.pos.getY();
	}

	@Override
	public void setPosition(double x, double y) {
		this.pos.setX(x);
		this.pos.setY(y);
	}
}
