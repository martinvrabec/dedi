package dedi.configuration.calculations.geometry;

public class Conic {
	private double coeffOfx2;
	private double coeffOfxy;
	private double coeffOfy2;
	private double coeffOfx;
	private double coeffOfy;
	private double constant;
	
	
	
	public Conic(double coeffOfx2, double coeffOfxy, double coeffOfy2, double coeffOfx, double coeffOfy,
			double constant) {
		super();
		this.coeffOfx2 = coeffOfx2;
		this.coeffOfxy = coeffOfxy;
		this.coeffOfy2 = coeffOfy2;
		this.coeffOfx = coeffOfx;
		this.coeffOfy = coeffOfy;
		this.constant = constant;
	}
	
	
	public double getCoeffOfx2() {
		return coeffOfx2;
	}
	public void setCoeffOfx2(double coeffOfx2) {
		this.coeffOfx2 = coeffOfx2;
	}
	public double getCoeffOfxy() {
		return coeffOfxy;
	}
	public void setCoeffOfxy(double coeffOfxy) {
		this.coeffOfxy = coeffOfxy;
	}
	public double getCoeffOfy2() {
		return coeffOfy2;
	}
	public void setCoeffOfy2(double coeffOfy2) {
		this.coeffOfy2 = coeffOfy2;
	}
	public double getCoeffOfx() {
		return coeffOfx;
	}
	public void setCoeffOfx(double coeffOfx) {
		this.coeffOfx = coeffOfx;
	}
	public double getCoeffOfy() {
		return coeffOfy;
	}
	public void setCoeffOfy(double coeffOfy) {
		this.coeffOfy = coeffOfy;
	}
	public double getConstant() {
		return constant;
	}
	public void setConstant(double constant) {
		this.constant = constant;
	}
	
	
}
