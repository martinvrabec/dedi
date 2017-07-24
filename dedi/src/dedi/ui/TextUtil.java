package dedi.ui;

public class TextUtil {
	
	public static String format(double value){
		String s;
		double absvalue = Math.abs(value);
		
		if ((absvalue <= 1000 && absvalue >= 0.01) || absvalue == 0) 
			s = String.format("%7.3f", value);
		else 
			s = String.format("%7.3e", value);
		
		return s;
	}
	
	
	
	public static String format(int value){
		String s;
		int absvalue = Math.abs(value);
		
		if ((absvalue <= 1000 && absvalue >= 0.01) || absvalue == 0) 
			s = String.format("%d", value);
		else 
			s = String.format("%7.3e", value);
		
		return s;
	}
}
