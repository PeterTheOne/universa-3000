package universa.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

import org.cogaen.core.Core;
import org.cogaen.event.Event;
import org.cogaen.event.EventListener;
import org.cogaen.event.EventService;
import org.cogaen.lwjgl.scene.Camera;
import org.cogaen.lwjgl.scene.SceneNode;
import org.cogaen.lwjgl.scene.SceneService;
import org.cogaen.view.View;

import cogaenfix.MouseDraggedEvent;
import cogaenfix.MouseReleasedEvent;
import cogaenfix.SplineVisual;
import cogaenfix.Vector2f;

import universa.Planetoid;
import universa.events.EntityMovedEvent;
import universa.events.GamespeedChangedEvent;

public class PlayView extends View implements EventListener {

	private SceneService scnSrv;
	private Dimension scrDim;
	private Camera cam;
	private Point prevMousePos;
	private int buttonPressed;
	
	/*private Overlay speedOverlay;
	private TextVisual speed;
	private Overlay zoomOverlay;
	private TextVisual zoom;
	private Overlay planetoidCountOverlay;
	private TextVisual planetoidCount;
	private Overlay planetoidMassOverlay;
	private TextVisual planetoidMass;*/

	public PlayView(Core core) {
		super(core);
		this.scnSrv = SceneService.getInstance(core);
		
		int width = this.scnSrv.getScreenWidth();
		int height = this.scnSrv.getScreenHeight();
		this.scrDim = new Dimension(width, height);
	}

	@Override
	public void engage() {
		//this.scnSvr.setClearBackground(true);
		//this.scnSrv.setBackgroundColor(Color.black);

		this.cam = this.scnSrv.createCamera();
		this.cam.setZoom(this.scnSrv.getScreenWidth() / 200000000000d);

		EventService evtSrv = EventService.getInstance(getCore());
		//evtSrv.addListener(this, EntityCreatedEvent.TYPE_ID);
		//evtSrv.addListener(this, EntityDestroyedEvent.TYPE_ID);
		evtSrv.addListener(this, EntityMovedEvent.TYPE_ID);
		//evtSrv.addListener(this, MousePressedEvent.TYPE_ID);
		evtSrv.addListener(this, MouseDraggedEvent.TYPE_ID);
		evtSrv.addListener(this, MouseReleasedEvent.TYPE_ID);
		//evtSrv.addListener(this, KeyPressedEvent.TYPE_ID);
		evtSrv.addListener(this, GamespeedChangedEvent.TYPE_ID);
		
		this.prevMousePos = new Point();
		this.buttonPressed = MouseEvent.NOBUTTON;

		/*this.speedOverlay = this.scnSrv.createOverlay();
		this.speedOverlay.setPosition(20, this.scrDim.height - 80);
		this.speed = this.scnSrv.createTextVisual("Gamespeed: " + 1d);
		this.speed.setColor(Color.WHITE);
		this.speedOverlay.addVisual(this.speed);

		this.zoomOverlay = this.scnSrv.createOverlay();
		this.zoomOverlay.setPosition(20, this.scrDim.height - 60);
		this.zoom = this.scnSrv.createTextVisual("Zoom: " + this.cam.getZoom());
		this.zoom.setColor(Color.WHITE);
		this.zoomOverlay.addVisual(this.zoom);

		this.planetoidCountOverlay = this.scnSrv.createOverlay();
		this.planetoidCountOverlay.setPosition(20, this.scrDim.height - 40);
		this.planetoidCount = this.scnSrv.createTextVisual("Planetoid Count: ");
		this.planetoidCount.setColor(Color.WHITE);
		this.planetoidCountOverlay.addVisual(this.planetoidCount);
		
		this.planetoidMassOverlay = this.scnSrv.createOverlay();
		this.planetoidMassOverlay.setPosition(20, this.scrDim.height - 20);
		this.planetoidMass = this.scnSrv.createTextVisual("Planetoid Mass: ");
		this.planetoidMass.setColor(Color.WHITE);
		this.planetoidMassOverlay.addVisual(this.planetoidMass);*/
	}

	@Override
	public void disengage() {
		EventService.getInstance(getCore()).removeListener(this);
		this.scnSrv.destroyAll();
	}

	public void registerResources(String group) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleEvent(Event event) {
		/*if (event.isOfType(EntityCreatedEvent.TYPE_ID)) {
			handleEntityCreated((EntityCreatedEvent) event);
		} else if (event.isOfType(EntityDestroyedEvent.TYPE_ID)) {
			handleEntityDestroyedEvent((EntityDestroyedEvent) event);
		} else */if (event.isOfType(EntityMovedEvent.TYPE_ID)) {
			handleEntityMovedEvent((EntityMovedEvent) event);
		} else /*if (event.isOfType(MousePressedEvent.TYPE_ID)) {
			handleMousePressedEvent((MousePressedEvent) event);
		} else*/ if (event.isOfType(MouseDraggedEvent.TYPE_ID)) {
			handleMouseDraggedEvent((MouseDraggedEvent) event);
		} else /*if (event.isOfType(KeyPressedEvent.TYPE_ID)) {
			handleKeyPressedEvent((KeyPressedEvent) event);
		} else*/ if (event.isOfType(GamespeedChangedEvent.TYPE_ID)) {
			handleGamespeedChangedEvent((GamespeedChangedEvent) event);
		}
	}

	/*private void handleEntityCreated(EntityCreatedEvent event) {
		if (event.getEntityType().equals(Planetoid.TYPE)) {
			EntityManager entMngr = EntityManager.getInstance(getCore());
			Planetoid planetoid = (Planetoid) entMngr.getEntity(event.getEntityName());
			
			// Trails
			SceneNode trailNode = this.scnSrv.createSceneNode(event.getEntityName() + "Trail");
			SplineVisual trailVisual = new SplineVisual((float) (planetoid.getRadius() * 2));
			trailVisual.setColor(Color.DARK_GRAY);
			trailNode.addVisual((Visual)trailVisual);
			this.scnSrv.getRootSceneNode().addChild(trailNode);
			
			SceneNode scnNode = this.scnSrv.createSceneNode(event.getEntityName());
			CircleVisual circle = this.scnSrv.createCircleVisual(planetoid.getRadius());
			circle.setColor(Color.WHITE);			
			scnNode.addVisual(circle);
			this.scnSrv.getRootSceneNode().addChild(scnNode);

			this.planetoidCount.setText("Planetoid Count: " + Planetoid.TOTAL_COUNT);
			this.planetoidMass.setText("Planetoid Mass: " + Planetoid.TOTAL_MASS);
		}
	}

	private void handleEntityDestroyedEvent(EntityDestroyedEvent event) {
		this.scnSrv.destroySceneNode(event.getEntityName() + "Trail");
		this.scnSrv.destroySceneNode(event.getEntityName());
		this.planetoidCount.setText("Planetoid Count: " + Planetoid.TOTAL_COUNT);
		this.planetoidMass.setText("Planetoid Mass: " + Planetoid.TOTAL_MASS);
	}*/

	private void handleEntityMovedEvent(EntityMovedEvent event) {
		SceneNode node = this.scnSrv.getSceneNode(event.getName());
		Vector2f pos = event.getPos();
		node.setPose(pos.getX(), pos.getY(), 0);
		
		// Trails
		/*SceneNode trailNode = this.scnSrv.getSceneNode(event.getName() + "Trail");
		SplineVisual trailVisual = (SplineVisual) trailNode.getVisuals().get(0);
		trailVisual.addVertex(pos);*/
	}

	/*private void handleMousePressedEvent(MousePressedEvent event) {
		this.prevMousePos = new Point(event.getX(), event.getY());
		this.buttonPressed = event.getButton();
	}*/

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
	
	/*private void handleKeyPressedEvent(KeyPressedEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_N) {
			zoomIn();
		} else if (event.getKeyCode() == KeyEvent.VK_M) {
			zoomOut();
		}
	}*/

	private void zoomIn() {
		double zoom = this.cam.getZoom();
		this.cam.setZoom(zoom * 2d);
		//this.zoom.setText("Zoom: " + this.cam.getZoom());
	}

	private void zoomOut() {
		double zoom = this.cam.getZoom();
		this.cam.setZoom(zoom / 2d);
		//this.zoom.setText("Zoom: " + this.cam.getZoom());
	}

	private void handleGamespeedChangedEvent(GamespeedChangedEvent event) {
		//this.speed.setText("Gamespeed: " + event.getGamespeed());
	}
}
