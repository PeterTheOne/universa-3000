package universa.representation;

import org.cogaen.core.Core;
import org.cogaen.lwjgl.scene.SceneNode;
import org.cogaen.lwjgl.scene.SceneService;
import org.cogaen.name.CogaenId;
import org.cogaen.view.EntityRepresentation;

public class BaseRepresentation extends EntityRepresentation {

	private SceneNode node;

	public BaseRepresentation(Core core, CogaenId entityId) {
		super(core, entityId);
	}

	@Override
	public void engage() {
		super.engage();
		
		SceneService scnSrv = SceneService.getInstance(getCore());
		this.node = scnSrv.createNode();
		scnSrv.getRootNode().addNode(this.node);
	}

	@Override
	public void disengage() {
		SceneService.getInstance(getCore()).destroyNode(this.node);
		super.disengage();
	}

	public void setPose(double x, double y) {
		this.node.setPose(x, y, 0);
	}
	
	public SceneNode getNode() {
		return this.node;
	}

}