package cogaenfix;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Path2D;
import java.util.LinkedList;

import org.cogaen.java2d.Visual;

public class SplineVisual extends Visual {
	
	private Path2D.Double shape;
	private Color color = Color.CYAN;
	private float strokeWidth;
	
	private LinkedList<Vector2f> vertices;
	
	public SplineVisual(float strokeWidth) {
		this.strokeWidth = strokeWidth;
		this.shape = new Path2D.Double();
		this.vertices = new LinkedList<Vector2f>();
	}
	
	public void setColor(Color color) {
		this.color = color;
	}
	
	public void addVertex(Vector2f vector) {
		// Filter by distance
		if( vertices.size() < 2 || vertices.get(vertices.size()-2).sub(vertices.getLast()).length() > this.strokeWidth * 4 )
		{
			vertices.addLast(vector);
			if(vertices.size() > 50)	// Buffer
				vertices.removeFirst();
		}

		if( vertices.size() > 0 ) {
			// Move the last vertex to planetoid position
			vertices.set(vertices.size()-1, vector);
		}
		
		// Update vertices in Shape
		this.shape.reset();
		this.shape.moveTo(vertices.get(0).getX(), -vertices.get(0).getY());
		for (int i = 1; i < vertices.size(); ++i) {
			this.shape.lineTo(vertices.get(i).getX(), -vertices.get(i).getY());
		}
	}
	
	@Override
	public void render(Graphics2D g) {
		if(vertices.size() > 0) {
			g.setColor(this.color);
			g.setStroke(new BasicStroke(this.strokeWidth, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER));
			
			g.draw(this.shape);
		}
	}
}