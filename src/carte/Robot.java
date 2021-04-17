package carte;

public class Robot extends Rectangle {
	private Point position;
	final float longueur = 29f;
	final float largeur = 16f;
	private float direction;
	private Rectangle roueDroite;  //A voir quoi faire des roues exactement mais je cherche comme faire tourner le robot (le rectangle) et quand je l'aurai 
	private Rectangle roueGauche;  //on pourra faire tourner les roue pour toujours savoir ou est quel �l�ment, mais en vrai elles ne me semble pas indispensable
	private Cercle capteurCouleur;
	
	public Robot(Point position, float direction) {
		super(new Point(position.getX()-8f, position.getY()-14.5f), new Point(position.getX()+8f, position.getY()+14.5f));
		this.position = position;
		this.direction = direction;
		this.roueDroite = new Rectangle(new Point(position.getX()+6.6f,position.getY()-10.3f),new Point(position.getX()+9.4f,position.getY()-4.7f));
		this.roueGauche = new Rectangle(new Point(position.getX()-9.4f,position.getY()-10.3f),new Point(position.getX()+6.6f,position.getY()-4.7f));
		this.capteurCouleur = new Cercle(new Point(position.getX(), position.getY()+10.5f), 0.5f);
	}
	
	static Robot robotUsuel = new Robot(Point.INCONNU, Float.NaN);
	
	public Point getPosition() {
		return(this.position);
	}
	
	public float getDirection() {
		return(this.direction);
	}
	
	public Cercle getCapteurCouleur() {
		return(this.capteurCouleur);
	}
	
	public void setPosition(float x, float y) {
		this.position.setX(x);
		this.position.setY(y);
		this.getBasGauche().setX(x-8f);
		this.getBasGauche().setY(y-14.5f);
		this.roueGauche.setPosition(x-8f, y-7.5f);
		this.roueDroite.setPosition(x+8f, y-7.5f);
		this.capteurCouleur.setCentre(x, y+10.5f);
		this.getHautDroite().setX(x+8);
		this.getHautDroite().setY(y+14.5f);
	}
	
	public void setDirection(float angle) {
		this.direction = angle;
	}
	
	private void tourner(float angle) {
		this.direction = (direction + angle)%360;
	}
	
	private void avancer(float distance) {
		this.setPosition((float) (this.position.getX()+(Math.cos(direction)*distance)),(float) (this.position.getY()+(Math.sin(direction)*distance)));
	}
	
	public void deplacer(float x, float y, float directionRelative) {
		this.tourner(directionRelative);
		this.avancer(position.distance(new Point(x,y)));
	}
	
	public void deplacer(float distance, float directionRelative) {
		this.tourner(directionRelative);
		this.avancer(distance);
	}
	
	public String toString() {
		return "Position : " + position + ". Direction : "+direction; 
	}
	
}