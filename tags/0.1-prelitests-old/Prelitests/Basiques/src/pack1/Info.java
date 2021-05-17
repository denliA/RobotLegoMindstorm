package pack1;

public class Info {
	int nombre = 0;
	public Info(int n) {
		this.nombre = n;
	}
	public Info() {
		this(0);
	}
	public void incrementer() {
		nombre++;
	}
	public void decrementer() {
		nombre--;
	}
	public int getValue() {
		return(nombre);
	}
	public void setValue(int n) {
		this.nombre = n;
	}
}