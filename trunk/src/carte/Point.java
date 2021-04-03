package carte;

public class Point {
	
	private float x;
	private float y;
	
	public Point(float x, float y) {
		this.x = x;
		this.y = y;
	}
	
	public float getX() {
		return(this.x);
	}
	
	void setX(float x) {
		this.x = x;
	}
	
	public float getY() {
		return(this.y);
	}
	
	void setY(float y) {
		this.y = y;
	}
	
	public float distance(Point A) {
		return((float) Math.sqrt(Math.pow(this.x-A.getX(), 2)+Math.pow(this.y-A.getY(),2)));
	}
	
}