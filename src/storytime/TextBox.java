package storytime;

import java.awt.Font;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Sound;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Input;
import org.newdawn.slick.GameContainer;

public class TextBox {
	public final static int left = 0;
	public final static int center = 1;
	public final static int right  = 2;
	
	ThisGame par;
	String[] paths;
	String[] characters, pflags;
	String emote, sfx;

	TrueTypeFont font;
	Sound print, move, decide, skip, sound;
	Image bg, textBox;
	Image[] portrait;
	SpriteSheet arrows, arrowsr;
	Animation arrow, select;
	Graphics box;
	Color color, tcolor;

	static final int COOLDOWNSET = 20;
	static final int TEXTBOXLENGTH = 40;

	String text, formatted = "";
	HashMap < Integer, Color > colorList = new HashMap < Integer, Color > ();
	int cursor = 1;
	int limiter = 0;
	int speaker = left ;
	int word, line, column, sel = 0;
	String name = "";
	List < String > textQueue = new ArrayList < String > ();
	List < String > nameQueue = new ArrayList < String > ();

	String[] options;
	int[] flags;
	int side;

	public TextBox(ThisGame p, String[] c, int s, String e, String snd, String m, String[] o, int[] f, String[] pf) throws SlickException {
		par = p;
		characters = c;
		speaker = s;
		portrait = new Image[]{null, null, null};
		
		for(int i=0; i<characters.length; i++){
			if(characters[i] != null) {
				portrait[i] = par.chardb.imagedb.get(characters[i]);
			}
		}
		
		emote = e;
		if(emote != null){
			portrait[speaker] = par.chardb.imagedb.get(characters[speaker]+"-"+emote);
		}
		
		color = par.chardb.colordb.get(characters[s]);
		print = new Sound("./res/sfx/text.wav");
		move = new Sound("./res/sfx/move.wav");
		decide = new Sound("./res/sfx/decide.wav");
		skip = new Sound("./res/sfx/skip.wav");
		
		if(sfx != null){
			sound = new Sound("./res/sfx/"+sfx);
		}
		
		//Determine if text color should be white or black based on textbox background color
		float Y = 0.2126f*color.r + 0.7152f*color.g + 0.0722f*color.b;
		System.out.println("Luminence is "+Y);
		tcolor = Y < 0.4 ? Color.white : Color.black;

		options = o;
		flags = f;
		pflags = pf;
		text = m;
		name = par.chardb.namedb.get(characters[s]);
	}
	
	public void init() throws SlickException{
		font = new TrueTypeFont(new Font("Consolas", Font.BOLD, 16), true);
		textBox = new Image("./res/gui/textBox.png");
		bg = new Image("./res/gui/textBox.png");
		arrows = new SpriteSheet("./res/gui/textDown.png", 16, 16);
		arrowsr = new SpriteSheet("./res/gui/textDown.png", 16, 16);
		arrow = new Animation(arrows, new int[] {
			0, 0, 1, 0, 2, 0, 3, 0
		}, new int[] {
			300, 300, 300, 300
		});

		select = new Animation(arrowsr, new int[] {
			0, 0, 1, 0, 2, 0, 3, 0
		}, new int[] {
			300, 300, 300, 300
		});
		for (int i = 0; i < select.getFrameCount(); i++) {
			select.getImage(i).setRotation(270f);
		}
		box = textBox.getGraphics();
		
		System.out.println(formatText());
	}
	
	public void reset() throws SlickException {
		portrait = new Image[]{null, null, null};
		
		for(int i=0; i<characters.length; i++){
			if(characters[i] != null) {
				//System.out.println("Creating portrait from "+par.chardb.database.get(characters[i]).p());
				portrait[i] = par.chardb.imagedb.get(characters[i]);
			}
		}
		
		if(emote != null){
			portrait[speaker] = par.chardb.imagedb.get(characters[speaker]+"-"+emote);
		}
		
		name = par.chardb.namedb.get(characters[speaker]);
		color = par.chardb.colordb.get(characters[speaker]);
		
		if(sfx != null){
			sound = new Sound("./res/sfx/"+sfx);
			sound.play();
		}

		cursor = word = line = column = sel = 0;
		colorList.clear();
		System.out.println(text);
		formatText();
		
		float Y = 0.2126f*color.r + 0.7152f*color.g + 0.0722f*color.b;
		System.out.println("Luminence is "+Y);
		tcolor = Y < 0.7 ? Color.white : Color.black;
		
		//TODO Debug, remove
		if(flags != null){
			System.out.println(Arrays.toString(flags));
		}
		
		if(pflags != null){
			System.out.println(Arrays.toString(pflags));
		}
	}

	public void render(Graphics g, GameContainer gc) throws SlickException {
		//Input input = gc.getInput();
		Input input = gc.getInput();
		String[] words = formatted.split(" ");
		
		if(portrait[left] != null){
			g.drawImage(portrait[left], 32, 48);
		}
		if(portrait[center]!=null){
			g.drawImage(portrait[center], 320-portrait[center].getWidth()/2, 48);
		}
		if(portrait[right]!=null){
			g.drawImage(portrait[right], 608 - portrait[right].getWidth(), 48);
		}
		
		g.drawImage(textBox, 0, 360);
		try {
			//box.clear();
			//box = textBox.getGraphics();
			if (!(cursor > 1)) {
				box.drawImage(bg, 0, 0, color);
				box.setFont(font);
				box.setColor(tcolor);
				box.drawString(name, 8, 4);
			}

			//Typewrite text
			if (cursor < formatted.length() + 1 && limiter == 2) { //Use the limiter here to prevent overdraw and jaggies
				System.out.print(formatted.substring(cursor - 1, cursor));
				if (input.isKeyPressed(Input.KEY_SPACE) && cursor < formatted.length() + 1) {
					skip.play();
					//box.drawImage(bg, 0, 0, new Color(0.7f, 0.25f, 0.25f, 1f));
					box.setFont(font);
					box.setColor(tcolor);
					box.drawString(name, 8, 4);

					for (int i = cursor; i < formatted.length(); i++) {
						//Keep track of word and line
						if (formatted.substring(i - 1, i).equals(" ")) {
							//System.out.println(word);
							if (word < words.length - 1) {
								word++;
								//Word wrap
								//System.out.println((font.getWidth("A") * column) + font.getWidth(words[word] + " "));
								if (24 + (font.getWidth("A") * column) + font.getWidth(words[word] + " ") > 640) {
									line++;
									column = -1;
								}
							}
						} else if (formatted.substring(i - 1, i).equals("\n")) {
							line++;
							column = -1;
						}

						//Get word color
						if (colorList.get(word) != null) {
							box.setColor(colorList.get(word));
						} else {
							box.setColor(tcolor);
						}

						box.drawString(formatted.substring(i - 1, i), 24 + (font.getWidth("A") * column), 24 + (font.getHeight()) * line);
						column++;
					}
					cursor = formatted.length() + 1;
				}

				//Keep track of word and line
				if (formatted.substring(cursor - 1, cursor).equals(" ")) {
					//System.out.println(word);
					if (word < words.length - 1) {
						word++;
						
						//Word wrap
						if ((24 + (font.getWidth("A") * column) + font.getWidth(words[word] + " ")) > 640) {
							line++;
							column = -1;
						}
					}
				} else if (formatted.substring(cursor - 1, cursor).equals("\n")) {
					line++;
					column = -1;
				}

				//Get word color
				if (colorList.get(word) != null) {
					box.setColor(colorList.get(word));
				} else {
					box.setColor(tcolor);
				}
				
				print.play();
				box.drawString(formatted.substring(cursor - 1, cursor), 24 + (font.getWidth("A") * column), 24 + (font.getHeight()) * line);
				column++;
			}

		} catch (Exception e) {}

		box.flush();
		arrow.draw(620, 456);

		if (cursor >= formatted.length() + 1) {
			//Show options
			if (options != null) {
				String l = options[0];
				for(String o : options) {
					if(o.length() > l.length()) {
						l = o;
					}
				}
				
				g.setColor(Color.blue);
				g.fillRect(320 - font.getWidth(l)/2, 64, font.getWidth(l)+16, font.getHeight()*options.length+4);
				g.setColor(Color.white);
				g.drawRect(320 - font.getWidth(l)/2, 64, font.getWidth(l)+16, font.getHeight()*options.length+4);
	
				int oy = 0;
	
				for (String opt: options) {
					oy++;
					g.drawString(opt, 328 - font.getWidth(l)/2, 46 + (font.getLineHeight() * oy));
				}
				
				select.draw(300 - font.getWidth(l)/2, 64 + (font.getLineHeight() * sel));
			}
			
			if (options != null) {
				if (input.isKeyPressed(Input.KEY_UP) && sel > 0) {
					sel--;
					move.play();
				}

				if (input.isKeyPressed(Input.KEY_DOWN) && sel < options.length - 1) {
					sel++;
					move.play();
				}

				if (input.isKeyPressed(Input.KEY_SPACE)) {
					if(paths != null) {
						//Set flags before chaning nodes
						if(flags != null) {
							if (flags[sel] != -1) {
								par.flags.set(flags[sel], true);
							}
						}
						
						if(pflags != null){
							boolean set = true;
							int sf = -1;
							for(int fi = 0; fi < pflags.length; fi++) {
								String[] pf = pflags[fi].split(",");
								for(String pff : pf) {
									System.out.println("Checking "+pff);
									set &= par.flags.get(Integer.parseInt(pff));	//If any one flag is not set, the whole thing is false
									System.out.println(set);
								}
								if(set) {
									sf = fi;
								}
							}
							
							par.currentNode = par.nodemap.get(paths[sf]);
							par.currentNode.init();
						}else{
						//Update current story node to the current branch
							if(paths.length > 1){
								par.currentNode = par.nodemap.get(paths[sel]);
							}else{
								par.currentNode = par.nodemap.get(paths[0]);
							}
						}
						par.currentNode.init();
						decide.play();
					}else{
						 System.exit(1);
					}
				}
			}else if(paths != null) {
				if (input.isKeyPressed(Input.KEY_SPACE)) {
					if (paths.length == 1 && pflags == null) {
						par.currentNode = par.nodemap.get(paths[0]);
						try {
							par.currentNode.init();
						} catch (SlickException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					
					if(pflags != null){
						boolean set;
						int sf = -1;
						for(int fi = 0; fi < pflags.length; fi++) {
							String[] pf = pflags[fi].split(",");
							set = true;
							for(String pff : pf) {
								System.out.println("Checking "+pff);
								set &= par.flags.get(Integer.parseInt(pff));	//If any one flag is not set, the whole thing is false
								System.out.println(set);
							}
							if(set) {
								sf = fi;
							} else {	//Fall through
								if(sf == -1) {
									sf = paths.length-1;
								}
							}
						}
						
						par.currentNode = par.nodemap.get(paths[sf]);
						par.currentNode.init();
					}
				}
			}else{
				if (input.isKeyPressed(Input.KEY_SPACE)) {
					System.exit(1);
				}
			}
		}else{
			if(input.isKeyPressed(Input.KEY_DOWN)){
				input.consumeEvent();
			}
		}
	}

	int formatText() {
		//Find color words in the text
		int i = 0;
		String[] raw = text.split(" ");
		formatted = "";
		
		String pattern, sub;
		// Create a Pattern object
		Pattern p, ps;
		Matcher m, ms;
		for (String s: raw) {
			pattern = "(\\[([A-z]):([A-z]+)\\]|\\[([A-z]):([0-9]+:*+)+([A-z]+),([A-z]+)\\])+([A-z\\p{Punct}]*)";
			p = Pattern.compile(pattern);
			m = p.matcher(s);
			
			if(m.find()){
				if (m.group(2) != null) { //Check for color word
					System.out.println("Color");
					System.out.println(m.group(3));
					switch (m.group(3)) {
						case "blue":
							colorList.put(i, Color.blue);
							break;
						case "red":
							colorList.put(i, Color.red);
							break;
						case "green":
							colorList.put(i, Color.green);
							break;
						case "yellow":
							colorList.put(i, Color.yellow);
							break;
					}
				}
				
				if (m.group(4) != null) { //Check for flag control
					
					sub = "[0-9]+";
					ps = Pattern.compile(sub);
					ms = ps.matcher(m.group(1));
					
					boolean set = true;
					int flag;
					while(ms.find()){
						flag = Integer.parseInt(ms.group(0));
						set &= par.flags.get(flag);	//If any one flag is not set, the whole thing is false
					}
					
					if(set) {
						System.out.println(m.group(6));
						formatted += m.group(6);
					}else{
						System.out.println(m.group(7));
						formatted += m.group(7);
					}
				}
				
				formatted += m.group(8)+" ";
			} else {
				formatted += s + " ";
			}
			i++;
		}
		return colorList.size();
	}

	public int increment() {
		limiter++;

		if (limiter == 3) {
			cursor++;

			if (cursor > formatted.length() + 1) {
				cursor--;
			}

			limiter = 0;
		}

		return cursor;
	}

	/*public void setPaths(StoryNode[] p) {
		paths = p;
	}*/
}