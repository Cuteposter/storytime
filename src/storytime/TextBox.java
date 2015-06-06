package storytime;

import java.awt.Font;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.newdawn.slick.Animation;
import org.newdawn.slick.Image;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SpriteSheet;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.Color;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Input;
import org.newdawn.slick.GameContainer;

public class TextBox {
	ThisGame par;
	StoryNode[] paths;

	TrueTypeFont font;
	Image bg;
	Image textBox, portrait;
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
	int word, line, column, sel = 0;
	String name = "";
	List < String > textQueue = new ArrayList < String > ();
	List < String > nameQueue = new ArrayList < String > ();

	String[] options;
	int[] flags;

	public TextBox(ThisGame p, Character c, String m, String[] o, int[] f) throws SlickException {
		par = p;
		color = c.c();
		portrait = new Image(c.p());
		
		//Determine if text color should be white or black based on textbox background color
		float Y = 0.2126f*color.r + 0.7152f*color.g + 0.0722f*color.b;
		//System.out.println("Luminence is "+Y);
		tcolor = Y < 0.5 ? Color.white : Color.black;

		options = o;
		flags = f;
		text = m;
		name = c.n();
	}
	
	public void init() throws SlickException{
		font = new TrueTypeFont(new Font("Consolas", Font.BOLD, 16), true);
		textBox = new Image("res/gui/textBox.png");
		bg = new Image("res/gui/textBox.png");
		arrows = new SpriteSheet("res/gui/textDown.png", 16, 16);
		arrowsr = new SpriteSheet("res/gui/textDown.png", 16, 16);
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
	
	public void reset() {
		cursor = word = line = column = sel = 0;
		colorList.clear();
		System.out.println(text);
		formatText();
	}

	public void render(Graphics g, GameContainer gc) {
		//Input input = gc.getInput();
		Input input = gc.getInput();
		String[] words = formatted.split(" ");
		
		g.drawImage(portrait, 320-portrait.getWidth()/2, 48);
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

						//Word wrap
						if (24 + (font.getWidth("A") * column) + font.getWidth(words[word] + " ") > 640) {
							line++;
							column = 0;
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

				//Word wrap
				if ((24 + (font.getWidth("A") * column) + font.getWidth(words[word] + " ")) > 640) {
					line++;
					column = 0;
				}

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
				}

				if (input.isKeyPressed(Input.KEY_DOWN) && sel < options.length - 1) {
					sel++;
				}

				if (input.isKeyPressed(Input.KEY_SPACE)) {
					try {
						//Set flags before chaning nodes
						if (flags[sel] != -1) {
							par.flags.set(flags[sel], true);
						}
						//Update current story node to the current branch
						par.currentNode = paths[sel];
						par.currentNode.init();
					} catch (Exception e) {}
				}
			}else if(paths != null) {
				if (paths.length == 1) {
					if (input.isKeyPressed(Input.KEY_SPACE)) {
						par.currentNode = paths[0];
						try {
							par.currentNode.init();
						} catch (SlickException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
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
		String first = "";
		String[] raw = text.split(" ");
		String[] parts;
		formatted = "";
		
		String pattern;;
		// Create a Pattern object
		Pattern p;
		Matcher m;
		for (String s: raw) {
			pattern = "(\\[([A-z]):([A-z]+)\\]|\\[([A-z]):([0-9]):([A-z]+),([A-z]+)\\])+([A-z\\p{Punct}]*)";
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
					
					int flag = Integer.parseInt(m.group(5));
					
					if(par.flags.get(flag)) {
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

	public void setPaths(StoryNode[] p) {
		paths = p;
	}
}