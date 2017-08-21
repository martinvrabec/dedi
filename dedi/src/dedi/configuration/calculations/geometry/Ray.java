package dedi.configuration.calculations.geometry;

import javax.vecmath.Vector2d;


import dedi.configuration.calculations.NumericRange;

public class Ray {
	private Vector2d direction;
	private Vector2d pt;
	
	
	public Ray(Vector2d direction, Vector2d pt) {
		super();
		this.direction = direction;
		this.pt = pt;
	}

	
	public Vector2d getDirection() {
		return direction;
	}

	public void setDirection(Vector2d direction) {
		this.direction = direction;
	}

	public Vector2d getStartingPt() {
		return pt;
	}

	public void setStartingPt(Vector2d pt) {
		this.pt = pt;
	}
	
	public Vector2d getPt(double t){
		if(t < 0) return null;
		Vector2d result = new Vector2d(direction);
		result.scale(t);
		result.add(pt);
		return result;
	}
	
	
	public Vector2d getPtAtDistance(double distance){
		if(direction.length() == 0) return null;
		return getPt(distance/direction.length());
	}
	
	
	public NumericRange getConicIntersectionParameterRange(double coeffOfx2, double coeffOfxy, double coeffOfy2,
														   double coeffOfx, double coeffOfy, double constant){
		
		double t1;
		double t2;
		
		double a = coeffOfx2*Math.pow(direction.x, 2) + coeffOfxy*direction.x*direction.y +
				           coeffOfy2*Math.pow(direction.y, 2);
		double b = 2*coeffOfx2*direction.x*pt.x + coeffOfxy*(direction.x*pt.y + direction.y*pt.x) +
				           2*coeffOfy2*direction.y*pt.y + coeffOfx*direction.x + coeffOfy*direction.y;
		double c = coeffOfx2*Math.pow(pt.x, 2) + coeffOfxy*pt.x*pt.y + coeffOfy2*Math.pow(pt.y, 2) + 
		           coeffOfx*pt.x + coeffOfy*pt.y + constant;
		
		double discriminant = Math.pow(b, 2) - 4*a*c;
		if (discriminant < 0) return null;
		if (a == 0){
			if(b == 0) return (c == 0) ? new NumericRange(0, Double.POSITIVE_INFINITY) : null;
			t1 = -c/b;
			t2 = -c/b;
		} else{
			t1 = 0.5*(-b - Math.sqrt(discriminant))/a;
			t2 = 0.5*(-b + Math.sqrt(discriminant))/a;
		}
		return getParameterRange(t1, t2);
	}
	
	
	
	public NumericRange getEllipseIntersectionParameterRange(double a, double b, Vector2d centre){
		double xcentre = centre.x;
		double ycentre = centre.y;
		
		double coeffOfx2 = 1/Math.pow(a, 2);
		double coeffOfy2 = 1/Math.pow(b, 2);
		double coeffOfx = -2*xcentre/Math.pow(a, 2);
		double coeffOfy = -2*ycentre/Math.pow(b, 2);
		double constant = Math.pow(xcentre, 2)/Math.pow(a, 2) + Math.pow(ycentre, 2)/Math.pow(b, 2) - 1;
		
		return getConicIntersectionParameterRange(coeffOfx2, 0, coeffOfy2, coeffOfx, coeffOfy, constant);
	}
	
	
	public NumericRange getCircleIntersectionParameterRange(double radius, Vector2d centre){
		return getEllipseIntersectionParameterRange(radius, radius, centre);
	}
	
	
	public NumericRange getRectangleIntersectionParameterRange(Vector2d topLeftCorner, double width, double height){
		NumericRange result;
		
		double xmax = topLeftCorner.x + width;
		double xmin = topLeftCorner.x;
		double ymax = topLeftCorner.y;
		double ymin = topLeftCorner.y - height;
		
		if(direction.x == 0){
			if(! new NumericRange(xmin, xmax).contains(direction.x)) return null;
			result = new NumericRange(0, Double.POSITIVE_INFINITY);
		} else 
			result = new NumericRange((xmin-pt.x)/direction.x, (xmax-pt.x)/direction.x);
		
		if(direction.y == 0){
			if(! new NumericRange(ymin, ymax).contains(direction.y)) return null;
			return getParameterRange(result);
		}
		
		result = result.intersect(new NumericRange((ymin-pt.y)/direction.y, (ymax-pt.y)/direction.y));
		
		return getParameterRange(result);
	}
	
	
	private NumericRange getParameterRange(double t1, double t2){
		if(t1 < 0 && t2 < 0) return null;
		
		double t_min = Math.min(t1, t2);
		double t_max = Math.max(t1, t2);
		
		if(t_min < 0) t_min = 0;
		
		return new NumericRange(t_min, t_max); 
	}
	
	
	private NumericRange getParameterRange(NumericRange range) {
		return getParameterRange(range.getMin(), range.getMax());
	}
}
