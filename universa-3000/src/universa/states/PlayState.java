package universa.states;

import org.cogaen.core.Core;
import org.cogaen.entity.EntityManager;
import org.cogaen.motion.Vector;
import org.cogaen.resource.ResourceManager;
import org.cogaen.state.GameState;
import org.cogaen.view.View;

import universa.Planetoid;
import universa.view.PlayView;

public class PlayState implements GameState {

	public static String NAME = "Play";

	private Core core;
	private View view;

	public PlayState(Core core) {
		this.core = core;
		this.view = new PlayView(core);
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void onEnter() {
		ResourceManager.getInstance(this.core).loadGroup(NAME);
		this.view.engage();

		for (int i = 0; i < 7; i++) {
			Vector pos = new Vector(Math.random() * 20 - 10d, Math.random() * 20 - 10d);
			Planetoid planetoid = new Planetoid(core, pos, 0.01);
			EntityManager.getInstance(this.core).addEntity(planetoid);
		}
	}

	@Override
	public void onExit() {
		EntityManager.getInstance(this.core).removeAllEntities();

		this.view.disengage();
		ResourceManager.getInstance(this.core).unloadGroup(NAME);
	}

}
