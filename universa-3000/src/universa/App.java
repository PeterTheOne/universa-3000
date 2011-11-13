package universa;

import java.awt.event.KeyEvent;

import org.cogaen.core.Core;
import org.cogaen.core.ServiceException;
import org.cogaen.entity.EntityService;
import org.cogaen.event.Event;
import org.cogaen.event.EventListener;
import org.cogaen.event.EventService;
import org.cogaen.logging.ConsoleLogger;
import org.cogaen.lwjgl.input.KeyPressedEvent;
import org.cogaen.lwjgl.input.KeyboardService;
import org.cogaen.lwjgl.scene.SceneService;
import org.cogaen.name.CogaenId;
import org.cogaen.name.IdService;
import org.cogaen.property.PropertyService;
import org.cogaen.resource.ResourceService;
import org.cogaen.state.GameStateService;
import org.cogaen.task.TaskService;
import org.cogaen.time.Clock;
import org.cogaen.time.TimeService;

import universa.events.GamespeedChangedEvent;
import universa.states.PlayState;

public class App implements EventListener {

	public static void main(String[] args) throws ServiceException {
		App game = new App();
		game.runGame();
	}

	public static final CogaenId END_OFF_APPLICATION = new CogaenId("EndOfApplication");
	private static final String PROPERTY_FILE = "universa-3000.cfg";
	private static final String APP_TITLE = "universa-3000";
	private Core core;
	
	private double gamespeed;

	public App() {
		// initialize core
		this.core = new Core();
		this.core.addService(new ConsoleLogger());
		this.core.addService(new IdService());
		this.core.addService(new TimeService());
		this.core.addService(new PropertyService(PROPERTY_FILE));
		this.core.addService(new EventService());
		this.core.addService(new GameStateService());
		this.core.addService(new ResourceService());
		this.core.addService(new TaskService());
		this.core.addService(new EntityService());
		this.core.addService(new KeyboardService());
		this.core.addService(new SceneService(1024, 768, false, true));
		this.core.addService(new MotionManager());
	}

	private void runGame() throws ServiceException {
		// before services can be accessed, start core
		this.core.startup();
		
		// initialize scene service
		SceneService.getInstance(this.core).setTitle(APP_TITLE);
		
		// catch some events
		EventService evtSrv = EventService.getInstance(this.core);
		evtSrv.addListener(this, SceneService.WINDOW_CLOSE_REQUEST);

		// initialize game states
		GameStateService stateSrv = GameStateService.getInstance(this.core);
		stateSrv.addState(new PlayState(this.core), PlayState.ID);
		stateSrv.setCurrentState(PlayState.ID);
		
		// run the game
		runGameLoop();

		// clean up
		evtSrv.removeListener(this);
		this.core.shutdown();
		
		this.gamespeed = 1d;
	}

	public void runGameLoop() {
		GameStateService stateSrv = GameStateService.getInstance(this.core);
		SceneService scnService = SceneService.getInstance(this.core);
		Clock clock = new Clock();
		
		while(!stateSrv.isEndState()) {
			clock.tick();
			this.core.update(clock.getDelta()/* * this.gamespeed*/);
			scnService.renderScene();
		}
	}

	@Override
	public void handleEvent(Event event) {
		if (event.isOfType(KeyPressedEvent.TYPE_ID)) {
			handleKeyPressedEvent((KeyPressedEvent) event);
		}
	}

	private void handleKeyPressedEvent(KeyPressedEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_V) {
			this.gamespeed *= 2d;
			Event speedEvent = new GamespeedChangedEvent(this.gamespeed);
			EventService.getInstance(this.core).dispatchEvent(speedEvent);
		} else if (event.getKeyCode() == KeyEvent.VK_B) {
			this.gamespeed /= 2d;
			Event speedEvent = new GamespeedChangedEvent(this.gamespeed);
			EventService.getInstance(this.core).dispatchEvent(speedEvent);
		}
	}

}
