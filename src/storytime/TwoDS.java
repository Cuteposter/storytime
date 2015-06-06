package storytime;

import org.newdawn.slick.Color;

public class TwoDS implements Character{
	public static String n = "2DS-tan";
	public static Color c = new Color(255, 80, 100);
	public static String p = "res/2ds.png";
	
	@Override
	public String n() {
		return n;
	}

	@Override
	public Color c() {
		return c;
	}

	@Override
	public String p(){
		return p;
	}

}
