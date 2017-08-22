package dedi.ui;

import java.util.Objects;

public class TextUtil {
	
	private TextUtil() {
		throw new IllegalStateException("This class is not meant to be instantiated.");
	}
	
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
	
	
	/**
	 * @return Returns true if the two strings are equal or if they represent the same numerical value (although maybe formatted differently).
	 *         Returns false otherwise.
	 */
	public static boolean equals(String s1, String s2){
		if(Objects.equals(s1, s2)) return true;
		try{
			double d1 = Double.parseDouble(s1);
			double d2 = Double.parseDouble(s2);
			return d1 == d2;
		} catch(NumberFormatException | NullPointerException e){
			return false;
		}
	}
	
	
	
	/**
	 * "Fail safe" method for parsing a String - returns null in case the String is null or cannot be interpreted as a double
	 * rather than throwing an exception like Double.parseDouble() does.
	 * 
	 * @param s - the string to parse.
	 */
	public Double parseDouble(String s) {
		Double result;
		try {
			result = Double.parseDouble(s);
		} catch(NumberFormatException | NullPointerException e) {
			result = null;
		}
		
		return result;
	}
}
