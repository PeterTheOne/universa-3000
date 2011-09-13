package cogaenfix;

public class Vector2f {

	private double x;
	private double y;
	
	public Vector2f() {
		this(0.0, 0.0);
	}
	
	public Vector2f(double x, double y) {
		this.x = x;
		this.y = y;
	}
	
	public Vector2f(Vector2f v) {
		this.x = v.getX();
		this.y = v.getY();
	}
	
	public double getX() {
		return this.x;
	}
	
	public void setX(double x) {
		this.x = x;
	}
	
	public double getY() {
		return this.y;
	}
	
	public void setY(double y) {
		this.y = y;
	}
	
	public Vector2f normalize() {
		double length = this.length();
		if (length == 0.0) {
			return new Vector2f(this);
		}
		return new Vector2f(this.div(length));
	}
	
	public double length() {
		return Math.sqrt(lengthSquared());
	}
	
	public double lengthSquared() {
		return this.x * this.x + this.y * this.y;
	}
	
	public Vector2f add(Vector2f v) {
		return new Vector2f(this.x + v.x, this.y + v.y);
	}
	
	public Vector2f add(double s) {
		return new Vector2f(this.x + s, this.y + s);
	}
	
	public Vector2f sub(Vector2f v) {
		return new Vector2f(this.x - v.x, this.y - v.y);
	}
	
	public Vector2f sub(double s) {
		return new Vector2f(this.x - s, this.y - s);
	}
	
	public Vector2f multi(Vector2f v) {
		return new Vector2f(this.x * v.x, this.y * v.y);
	}
	
	public Vector2f multi(double s) {
		return new Vector2f(this.x * s, this.y * s);
	}
	
	public Vector2f div(Vector2f v) {
		return new Vector2f(this.x / v.x, this.y / v.y);
	}
	
	public Vector2f div(double s) {
		return new Vector2f(this.x / s, this.y / s);
	}
	
	public Vector2f negate() {
		return new Vector2f(-this.x, -this.y);
	}
	
	public Vector2f interpolate(Vector2f v) {
		return interpolate(v, 0.5d);
	}
	
	public Vector2f interpolate(Vector2f v1, Vector2f v2) {
		return interpolate(v1, 1 / 3d, v2, 1 / 3d);
	}
	
	public Vector2f interpolate(Vector2f v, double s) {
		return this.multi(1 - s).add(v.multi(s));
	}
	
	public Vector2f interpolate(Vector2f v1, double s1, Vector2f v2, double s2) {
		return this.multi(1 - s1 - s2).add(v1.multi(s1)).add(v2.multi(s2));
	}
	
	public String toString() {
		return "[x:" + this.x + ", y:" + this.y + "]";
	}
	
}
