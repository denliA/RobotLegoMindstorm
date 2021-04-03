package carte;

public class Carte {
	private Robot robot;
	private Ligne[] lignes;
	private Rectangle[] intersections;
	private Rectangle terrain;
	Palet[] palets;
	
	public Carte(Robot robot, Ligne[] lignes, Rectangle terrain, Palet[] palets) {
		this.robot = robot;
		this.lignes = lignes;
		this.terrain = terrain;
		this.palets = palets;
		this.intersections = this.intersections();
	}
	
	private Rectangle[] intersections() {
		Rectangle[] inter = new Rectangle[9];
		int nb = 0;
		for(int i = 0; i<9; i++) {
			for(int j = 0; j<9; j++) {
				if(i!=j && lignes[i].intersect(lignes[j])) {
					inter[nb] = lignes[i].intersection(lignes[j]);
					nb++;
				}
			}
		}
		return(inter);
	}
	
	public Robot getRobot() {
		return(robot);
	}
	
	public Rectangle[] getIntersections() {
		return(intersections);
	}
	
	public Rectangle getTerrain() {
		return(terrain);
	}
	
}
