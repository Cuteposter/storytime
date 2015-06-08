package storytime;

import java.util.HashMap;

import org.newdawn.slick.Color;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class Characters {
	public static HashMap<String, String> namedb = new HashMap<String, String>();
	public static HashMap<String, Color> colordb = new HashMap<String, Color>();
	public static HashMap<String, Image> imagedb = new HashMap<String, Image>();
	
	static {
		try{
			namedb.put("2ds", "2DS-tan");
			colordb.put("2ds", new Color(255, 80, 120));
			imagedb.put("2ds", new Image("res/2ds.png"));
			imagedb.put("2ds-question", new Image("res/2ds-question.png"));
			
			namedb.put("vita", "Vita-tan");
			colordb.put("vita", new Color(120, 80, 255));
			imagedb.put("vita", new Image("res/vita.png"));
			imagedb.put("vita-question", new Image("res/vita-question.png"));
		}catch(SlickException e){
			//System.err.println("Missing resource!!!);
		}
	}
}
