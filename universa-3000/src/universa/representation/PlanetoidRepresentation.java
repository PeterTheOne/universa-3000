package universa.representation;

import org.cogaen.core.Core;
import org.cogaen.lwjgl.scene.CircleVisual;
import org.cogaen.lwjgl.scene.Color;
import org.cogaen.lwjgl.scene.Visual;
import org.cogaen.name.CogaenId;

public class PlanetoidRepresentation extends BaseRepresentation {
	
	private double radius;

	public PlanetoidRepresentation(Core core, CogaenId entityId, double radius) {
		super(core, entityId);
		this.radius = radius;
	}

	@Override
	public void engage() {
		super.engage();
		Visual debugVsl = new CircleVisual(this.radius);
		debugVsl.setColor(Color.RED);
		getNode().addVisual(debugVsl);
	}

}
