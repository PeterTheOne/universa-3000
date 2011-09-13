package universa;

import java.awt.event.KeyEvent;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.cogaen.core.Core;
import org.cogaen.event.Event;
import org.cogaen.event.EventListener;
import org.cogaen.event.EventManager;
import org.cogaen.input.KeyPressedEvent;
import org.cogaen.java2d.SceneManager;
import org.cogaen.java2d.Screen;
import org.cogaen.java2d.WindowedScreen;
import org.cogaen.logging.LoggingService;
import org.cogaen.resource.ResourceManager;
import org.cogaen.state.GameStateManager;
import org.cogaen.time.Clock;

import cogaenfix.InputManager;

import universa.events.GamespeedChangedEvent;
import universa.states.PlayState;

public class App implements EventListener {

	public static void main(String[] args) throws InterruptedException {
		Screen screen = new WindowedScreen(500, 500);
		JFrame frame = new JFrame("Game States");
		frame.add(screen.getComponent());
		frame.setResizable(false);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		screen.waitUntilReady();

		App app = new App(screen);
		app.runGameLoop();
	}

	private Core core;
	private double gamespeed;

	public App(Screen screen) {
		this.core = Core.createCoreWithStandardServices(LoggingService.DEBUG);
		this.core.installService(new SceneManager(screen));
		this.core.installService(new InputManager(screen.getComponent()));
		this.core.installService(new ResourceManager());
		this.core.installService(new MotionManager());

		EventManager.getInstance(this.core).setFastEventDispatch(false);
		
		LoggingService.getInstance(this.core).setLevel(LoggingService.INFO);

		initializeGameStates();
		
		EventManager.getInstance(this.core).addListener(this, KeyPressedEvent.TYPE);
		gamespeed = 1d;
	}

	private void initializeGameStates() {
		GameStateManager stateManager = GameStateManager.getInstance(this.core);

		stateManager.addState(new PlayState(this.core));
		stateManager.setCurrentState(PlayState.NAME);

		//stateManager.addTransition();
	}

	public void runGameLoop() throws InterruptedException {
		Clock clock = new Clock();
		SceneManager scnMngr = SceneManager.getInstance(this.core);

		// LoggingService.getInstance(this.core).logDebug("GameApp",
		// "Gameloop started.");

		while (true) {
			clock.tick();
			//LoggingService.getInstance(this.core).logInfo("GameApp", "fps: " + 1 / clock.getAvgDelta());
			this.core.update(clock.getDelta() * this.gamespeed);
			scnMngr.renderScene();
			Thread.sleep(1);
		}
	}

	@Override
	public void handleEvent(Event event) {
		if (event.isOfType(KeyPressedEvent.TYPE)) {
			handleKeyPressedEvent((KeyPressedEvent) event);
		}
	}

	private void handleKeyPressedEvent(KeyPressedEvent event) {
		if (event.getKeyCode() == KeyEvent.VK_V) {
			this.gamespeed *= 2d;
			Event speedEvent = new GamespeedChangedEvent(this.gamespeed);
			EventManager.getInstance(this.core).enqueueEvent(speedEvent);
		} else if (event.getKeyCode() == KeyEvent.VK_B) {
			this.gamespeed /= 2d;
			Event speedEvent = new GamespeedChangedEvent(this.gamespeed);
			EventManager.getInstance(this.core).enqueueEvent(speedEvent);
		}
	}

}
