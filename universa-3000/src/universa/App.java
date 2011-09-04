package universa;

import javax.swing.JFrame;
import javax.swing.WindowConstants;

import org.cogaen.core.Core;
import org.cogaen.input.InputManager;
import org.cogaen.java2d.SceneManager;
import org.cogaen.java2d.Screen;
import org.cogaen.java2d.WindowedScreen;
import org.cogaen.logging.LoggingService;
import org.cogaen.resource.ResourceManager;
import org.cogaen.state.GameStateManager;
import org.cogaen.time.Clock;

import universa.states.PlayState;

public class App {

	public static void main(String[] args) throws InterruptedException {
		Screen screen = new WindowedScreen(1000, 700);
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

	public App(Screen screen) {
		this.core = Core.createCoreWithStandardServices(LoggingService.DEBUG);
		this.core.installService(new SceneManager(screen));
		this.core.installService(new InputManager(screen.getComponent()));
		this.core.installService(new ResourceManager());

		initializeGameStates();
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
			this.core.update(clock.getDelta());
			scnMngr.renderScene();
			Thread.sleep(1);
		}
	}

}