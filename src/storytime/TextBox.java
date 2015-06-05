package storytime;

import java.awt.Font;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

import org.newdawn.slick.Animation;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Image;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Input;
import org.newdawn.slick.GameContainer;

public class TextBox{
	ThisGame par;
	StoryNode[] paths;
	
   TrueTypeFont trueTypeFont;
   Image bg;
   Image textBox;
   SpriteSheet arrows, arrowsr;
   Animation arrow, select;
   Graphics box;

   static final int COOLDOWNSET = 20;
   static final int TEXTBOXLENGTH = 40;
   
   String text, formatted = "";
   HashMap<Integer,Color> colorList = new HashMap<Integer,Color>();
   int cursor = 1;
   int limiter = 0;
   int word, line, column, sel = 0;
   String name = ""; 
   List <String> textQueue = new ArrayList<String>();
   List <String> nameQueue = new ArrayList<String>(); 
   
   String[] options;
   int[] flags;
   
   public TextBox(ThisGame p, String n, String m, String[] o, int[] f){
      try{
    	  par = p;
    	  
         trueTypeFont = new TrueTypeFont(new Font("Consolas", Font.BOLD, 16), true);
         textBox = new Image("res/gui/textBox.png");
         bg = new Image("res/gui/textBox.png");
         arrows = new SpriteSheet("res/gui/textDown.png",16,16);
         arrowsr = new SpriteSheet("res/gui/textDown.png",16,16);
         arrow = new Animation(arrows, new int[]{0,0,1,0,2,0,3,0}, new int[]{300,300,300,300});
         
         select = new Animation(arrowsr, new int[]{0,0,1,0,2,0,3,0}, new int[]{300,300,300,300});
         for(int i=0; i<select.getFrameCount(); i++){
        	 select.getImage(i).setRotation(270f);
         }
         
         options = o;
         flags = f;
         text = m;
         System.out.println(formatText());
         name = n;
         
         box = textBox.getGraphics();
         //box = new Graphics();//textBox.getGraphics();
      }catch(SlickException e){}
   }
   
   public void render(Graphics g, GameContainer gc){
      //Input input = gc.getInput();
      Input input = gc.getInput();
      String[] words = formatted.split(" ");
      g.drawImage(textBox, 0, 360);
		try{
			//box.clear();
			//box = textBox.getGraphics();
			if(!(cursor > 1))
			{
				box.drawImage(bg, 0, 0, new Color(0.25f, 0.25f, 0.7f, 1f));
				box.setFont(trueTypeFont);
				box.setColor(Color.white);
				box.drawString(name, 8, 4);
			}
			
			//Typewrite text
			if(cursor < formatted.length()+1 && limiter == 2) {	//Use the limiter here to prevent overdraw and jaggies
				
				if (input.isKeyPressed(Input.KEY_SPACE) && cursor < formatted.length()+1){
					//box.drawImage(bg, 0, 0, new Color(0.7f, 0.25f, 0.25f, 1f));
					box.setFont(trueTypeFont);
					box.setColor(Color.white);
					box.drawString(name, 8, 4);
					
					for (int i = cursor; i < formatted.length(); i++){
						//Keep track of word and line
						if(formatted.substring(i-1, i).equals(" ")) {
							System.out.println(word);
							if(word<words.length-1) {
								word++;
							}
						}else if(formatted.substring(i-1, i).equals("\n")) {
							line++;
							column = -1;
						}
						
						//Get word color
						if(colorList.get(word) != null){
							box.setColor(colorList.get(word));
						}else{
							box.setColor(Color.white);
						}
						
						//Word wrap
						if(24+(trueTypeFont.getWidth("A")*column)+trueTypeFont.getWidth(words[word]+" ") > 640) {
							line++;
							column = 0;
						}
						
						box.drawString(formatted.substring(i-1, i), 24+(trueTypeFont.getWidth("A")*column), 24+(trueTypeFont.getHeight())*line);
						column++;
						}
					cursor = formatted.length()+1;
					}
				
				//box.drawImage(textDown, 620, 96, new Color(0.0f, 0.0f, 1f, 1f));
				//box.drawAnimation(arrow, 620, 96, Color.blue);
				//Keep track of word and line
				if(formatted.substring(cursor-1, cursor).equals(" ")) {
					System.out.println(word);
					if(word<words.length-1) {
						word++;
					}
				}else if(formatted.substring(cursor-1, cursor).equals("\n")) {
					line++;
					column = -1;
				}
				
				//Get word color
				if(colorList.get(word) != null){
					box.setColor(colorList.get(word));
				}else{
					box.setColor(Color.white);
				}
				
				//Word wrap
				if(24+(trueTypeFont.getWidth("A")*column)+trueTypeFont.getWidth(words[word]+" ") > 640) {
					line++;
					column = 0;
				}
				
				box.drawString(formatted.substring(cursor-1, cursor), 24+(trueTypeFont.getWidth("A")*column), 24+(trueTypeFont.getHeight())*line);
				column++;
				}
			
		}catch(Exception e){}

		 box.flush();
		arrow.draw(620, 456);
		
		if(cursor >= formatted.length()+1){
			if(options != null){
				if (input.isKeyPressed(Input.KEY_UP) && sel > 0){
					sel--;
				}
				
				if (input.isKeyPressed(Input.KEY_DOWN) && sel < options.length-1){
					sel++;
				}
			
			
				if (input.isKeyPressed(Input.KEY_SPACE)){
					//par.foo = options[sel];
					try{
						par.currentNode = paths[sel];
						if(flags[sel] != -1) {
							par.flags.set(flags[sel], true);
						}
					}catch(Exception e){}
					System.out.println(options[sel]);
				}
			}
			
			//Show options
			g.drawRect(64, 64, 128, 64);
			
			int oy = 0;
			if(options != null){
				for(String opt : options) {
					oy++;
					g.drawString(opt, 72, 46+(trueTypeFont.getLineHeight()*oy));
				}
			}
			select.draw(44,64+(trueTypeFont.getLineHeight()*sel));
		}
		
		
   }
   
   public void newMessage(String text, String name){
	   this.name = name;
	   this.text = text;
	   
	   //formatText();
	   //System.out.println(formatText());
   }
   
   int formatText() {
	   //Find color words in the text
	   int i = 0;
	   String[] raw = text.split(" ");
	   String[] parts;
	   formatted = "";

	   for(String s : raw) {
		   System.out.println(s);
		   	if(s.startsWith("[C:")){	//Check for color word
		   		//Strip off brackets from text
		   		s = s.replace("[C:", "");
		   		parts = s.split("]");
		   		System.out.println(parts[0]);
		   		switch(parts[0]) {
		   			case "blue": colorList.put(i, Color.blue);
		   				break;
		   			case "red": colorList.put(i, Color.red);
	   					break;
		   			case "green": colorList.put(i, Color.green);
	   					break;
		   			case "yellow": colorList.put(i, Color.yellow);
	   					break;
		   		}
		   		
		   		formatted += parts[1]+" ";
		   	}else{
		   		formatted += s+" ";
		   	}
		   	i++;
	   }
	   
	   return colorList.size();
   }
   
   public int increment() {
	   limiter++;
	   
	   if(limiter == 3){
		   cursor++;
		   
		   if(cursor > formatted.length()+1) {
			   cursor--;
		   }
		   
		   limiter = 0;
	   }
	   
	   return cursor;
   }
   
   public void setPaths(StoryNode[] p){
	   paths = p;
   }
}
