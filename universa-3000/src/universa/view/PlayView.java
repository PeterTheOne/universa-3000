package universa.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.cogaen.core.Core;
import org.cogaen.entity.EntityCreatedEvent;
import org.cogaen.entity.EntityDestroyedEvent;
import org.cogaen.entity.EntityManager;
import org.cogaen.event.Event;
import org.cogaen.event.EventListener;
import org.cogaen.event.EventManager;
import org.cogaen.input.KeyPressedEvent;
import org.cogaen.input.MousePressedEvent;
import org.cogaen.java2d.Camera;
import org.cogaen.java2d.CircleVisual;
import org.cogaen.java2d.Overlay;
import org.cogaen.java2d.SceneManager;
import org.cogaen.java2d.SceneNode;
import org.cogaen.java2d.Screen;
import org.cogaen.java2d.TextVisual;
import org.cogaen.java2d.Visual;
import org.cogaen.view.AbstractView;

import cogaenfix.MouseDraggedEvent;
import cogaenfix.MouseReleasedEvent;
import cogaenfix.SplineVisual;
import cogaenfix.Vector2f;

import universa.Planetoid;
import universa.events.EntityMovedEvent;
import universa.events.GamespeedChangedEvent;

public class PlayView extends AbstractView implements EventListener {

	private SceneManager scnMngr;
	private Dimension scrDim;
	private Camera cam;
	private Point prevMousePos;
	private int buttonPressed;
	
	private Overlay speedOverlay;
	private TextVisual speed;
	private Overlay zoomOverlay;
	private TextVisual zoom;
	private Overlay planetoidCountOverlay;
	private TextVisual planetoidCount;
	private Overlay planetoidMassOverlay;
	private TextVisual planetoidMass;

	public PlayView(Core core) {
		super(core);
		this.scnMngr = SceneManager.getInstance(core);
		
		Screen screen = scnMngr.getScreen();
		int width = screen.getWidth();
		int height = screen.getHeight();
		this.scrDim = new Dimension(width, height);
	}

	@Override
	public void engage() {
		this.scnMngr.setClearBackground(true);
		this.scnMngr.setBackgroundColor(Color.black);

		this.cam = this.scnMngr.createCamera();
		this.cam.setZoom(this.scnMngr.getScreen().getWidth() / 200000000000d);

		EventManager evtMngr = EventManager.getInstance(getCore());
		evtMngr.addListener(this, EntityCreatedEvent.TYPE);
		evtMngr.addListener(this, EntityDestroyedEvent.TYPE);
		evtMngr.addListener(this, EntityMovedEvent.TYPE);
		evtMngr.addListener(this, MousePressedEvent.TYPE);
		evtMngr.addListener(this, MouseDraggedEvent.TYPE);
		evtMngr.addListener(this, MouseReleasedEvent.TYPE);
		evtMngr.addListener(this, KeyPressedEvent.TYPE);
		evtMngr.addListener(this, GamespeedChangedEvent.TYPE);
		
		this.prevMousePos = new Point();
		this.buttonPressed = MouseEvent.NOBUTTON;

		this.speedOverlay = this.scnMngr.createOverlay();
		this.speedOverlay.setPosition(20, this.scrDim.height - 80);
		this.speed = this.scnMngr.createTextVisual("Gamespeed: " + 1d);
		this.speed.setColor(Color.WHITE);
		this.speedOverlay.addVisual(this.speed);

		this.zoomOverlay = this.scnMngr.createOverlay();
		this.zoomOverlay.setPosition(20, this.scrDim.height - 60);
		this.zoom = this.scnMngr.createTextVisual("Zoom: " + this.cam.getZoom());
		this.zoom.setColor(Color.WHITE);
		this.zoomOverlay.addVisual(this.zoom);

		this.planetoidCountOverlay = this.scnMngr.createOverlay();
		this.planetoidCountOverlay.setPosition(20, this.scrDim.height - 40);
		this.planetoidCount = this.scnMngr.createTextVisual("Planetoid Count: ");
		this.planetoidCount.setColor(Color.WHITE);
		this.planetoidCountOverlay.addVisual(this.planetoidCount);
		
		this.planetoidMassOverlay = this.scnMngr.createOverlay();
		this.planetoidMassOverlay.setPosition(20, this.scrDim.height - 20);
		this.planetoidMass = this.scnMngr.createTextVisual("Planetoid Mass: ");
		this.planetoidMass.setColor(Color.WHITE);
		this.planetoidMassOverlay.addVisual(this.planetoidMass);
	}

	@Override
	public void disengage() {
		EventManager.getInstance(getCore()).removeListener(this);
		this.scnMngr.destroyAll();
	}

	public void registerResources(String group) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEvent(Event event) {
		if (event.isOfType(EntityCreatedEvent.TYPE)) {
			handleEntityCreated((EntityCreatedEvent) event);
		} else if (event.isOfType(EntityDestroyedEvent.TYPE)) {
			handleEntityDestroyedEvent((EntityDestroyedEvent) event);
		} else if (event.isOfType(EntityMovedEvent.TYPE)) {
			handleEntityMovedEvent((EntityMovedEvent) event);
		} else if (event.isOfType(MousePressedEvent.TYPE)) {
			handleMousePressedEvent((MousePressedEvent) event);
		} else if (event.isOfType(MouseDraggedEvent.TYPE)) {
			handleMouseDraggedEvent((MouseDraggedEvent) event);
		} else if (event.isOfType(KeyPressedEvent.TYPE)) {
			handleKeyPressedEvent((KeyPressedEvent) event);
		} else if (event.isOfType(GamespeedChangedEvent.TYPE)) {
			handleGamespeedChangedEvent((GamespeedChangedEvent) event);
		}
	}

	private void handleEntityCreated(EntityCreatedEvent event) {
		if (event.getEntityType().equals(Planetoid.TYPE)) {
			EntityManager entMngr = EntityManager.getInstance(getCore());
			Planetoid planetoid = (Planetoid) entMngr.getEntity(event.getEntityName());
			
			// Trails
			SceneNode trailNode = this.scnMngr.createSceneNode(event.getEntityName() + "Trail");
			SplineVisual trailVisual = new SplineVisual((float) (planetoid.getRadius() * 2));
			trailVisual.setColor(Color.DARK_GRAY);
			trailNode.addVisual((Visual)trailVisual);
			this.scnMngr.getRootSceneNode().addChild(trailNode);
			
			SceneNode scnNode = this.scnMngr.createSceneNode(event.getEntityName());
			CircleVisual circle = this.scnMngr.createCircleVisual(planetoid.getRadius());
			circle.setColor(Color.WHITE);			
			scnNode.addVisual(circle);
			this.scnMngr.getRootSceneNode().addChild(scnNode);

			this.planetoidCount.setText("Planetoid Count: " + Planetoid.TOTAL_COUNT);
			this.planetoidMass.setText("Planetoid Mass: " + Planetoid.TOTAL_MASS);
		}
	}

	private void handleEntityDestroyedEvent(EntityDestroyedEvent event) {
		this.scnMngr.destroySceneNode(event.getEntityName() + "Trail");
		this.scnMngr.destroySceneNode(event.getEntityName());
		this.planetoidCount.setText("Planetoid Count: " + Planetoid.TOTAL_COUNT);
		this.planetoidMass.setText("Planetoid Mass: " + Planetoid.TOTAL_MASS);
	}

	private void handleEntityMovedEvent(EntityMovedEvent event) {
		SceneNode node = this.scnMngr.getSceneNode(event.getName());
		Vector2f pos = event.getPos();
		node.setPose(pos.getX(), pos.getY(), 0);
		
		// Trails
		SceneNode trailNode = this.scnMngr.getSceneNode(event.getName() + "Trail");
		SplineVisual trailVisual = (SplineVisual) trailNode.getVisuals().get(0);
		trailVisual.addVertex(pos);
	}

	private void handleMousePressedEvent(MousePressedEvent event) {
		this.prevMousePos = new Point(event.getX(), event.getY());
		this.buttonPressed = event.getButton();
	}

	private void handleMouseDraggedEvent(MouseDraggedEvent event) {
		if (this.buttonPressed == MouseEvent.BUTTON1) {
			Point mousePosDiv = new Point(prevMousePos.x - event.getX(), 
					prevMousePos.y - event.getY());
			//TODO: why cam.getTransform() not public
			//		why cam constructor not public
			//		why no getPosX() and getPosY()
			double x = this.cam.getPosX() + mousePosDiv.x / this.cam.getZoom();
			double y = this.cam.getPosY() - mousePosDiv.y / this.cam.getZoom();
			this.cam.setPosition(x, y);
			this.prevMousePos = new Point(event.getX(), event.getY());
		}	
	}
	
	private void handleKeyPressedEvent(KeyPressedEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_N) {
			zoomIn();
		} else if (event.getKeyCode() == KeyEvent.VK_M) {
			zoomOut();
		}
	}

	private void zoomIn() {
		double zoom = this.cam.getZoom();
		this.cam.setZoom(zoom * 2d);
		this.zoom.setText("Zoom: " + this.cam.getZoom());
	}

	private void zoomOut() {
		double zoom = this.cam.getZoom();
		this.cam.setZoom(zoom / 2d);
		this.zoom.setText("Zoom: " + this.cam.getZoom());
	}

	private void handleGamespeedChangedEvent(GamespeedChangedEvent event) {
		this.speed.setText("Gamespeed: " + event.getGamespeed());
	}
}
