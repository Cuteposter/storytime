package storytime;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;
import org.newdawn.slick.TrueTypeFont;

public class ThisGame extends BasicGame {
	TextBox dialog;
	TrueTypeFont f;
	Image twods, gamestop;
	Music music;
	Characters chardb;
	
	public HashMap<String, StoryNode> nodemap = new HashMap<String,StoryNode>();
	public StoryNode currentNode;
	public String foo = "foo";
	public ArrayList < Boolean > flags;

	//All the story nodes need to be in the global scope to be accessible for flag-dependent changes
	//StoryNode N1B1, N1B2, N1B3, N2BA, N3BA;
	
	public ThisGame(String gamename) {
		super(gamename);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		//Music music = new Music("res/song.ogg");
		
		//System.out.println("Working Directory = " +System.getProperty("user.dir"));
		chardb = new Characters();
		
		//Holy shit modle parser. I don't even want to describe it
		try(BufferedReader in = new BufferedReader(new FileReader("./game.mod"))){
		    String line;
		    float ver;
		    int f = 0;
			flags = new ArrayList < Boolean > ();
			String[] nodes;
			
    		String pattern, sub;
    		// Create a Pattern object
    		Pattern p, ps;
    		Matcher m, ms;
    		pattern = "([A-z]*[0-9]*[A-z]+[0-9]*)+\\((.*)\\)";
			p = Pattern.compile(pattern);
			
			sub = "\\\"(.*?)\\\",";
			ps = Pattern.compile(sub);
			
			StoryNode tempNode;
			String params, name, scene, characters, speaker, emote, sound, dialog, options, flagset, flagbra, children;
			
			String[] charlist, emotes, chars, ops, flagss, flagsb, childs;
			ArrayList<String> namelist = new ArrayList<String>();
			int[] flagssf;
			int speak = 0;
			
		    while((line=in.readLine())!=null){
		        if(line.startsWith("#Version")) {
		        	ver = Float.parseFloat(line.split("=")[1]);
		        }
		        
		        if(!line.startsWith("#")) {		    		
		    		if(line.startsWith("flags")) {
		    			f = Integer.parseInt(line.split("=")[1]);
		    			for (int i = 0; i < f; i++) {
		    				flags.add(false);
		    			}
		    		}else if(line.startsWith("nodes")) {
		    			nodes = line.split("=")[1].split(", ");
		    			for(String n : nodes) {
		    				nodemap.put(n, null);
		    			}
		    		}else if(line.startsWith("characters")) {
		    			charlist = line.split("=")[1].split(", ");
		    			String[] vals;
		    			String temp;
		    			for(String c : charlist) {
		    				temp = c.replace("{", "").replace("}", "");
		    				temp = temp.replace("(", "").replace(")", "");
		    				//System.out.println(temp);
		    				vals = temp.split(",");
		    				
		    				int r = Integer.parseInt(vals[2]);
		    				int g = Integer.parseInt(vals[3]);
		    				int b = Integer.parseInt(vals[4]);
		    				
		    				namelist.add(vals[0]);
		    				chardb.imagedb.put(vals[0], new Image("./res/char/"+vals[0]+".png"));
		    				chardb.namedb.put(vals[0], vals[1]);
		    				chardb.colordb.put(vals[0], new Color(r,g,b));
		    			}
		    		} else if(line.startsWith("emotes")) {
		    			emotes = line.split("=")[1].split(", ");
		    			for(String e : emotes) {
		    				for(String c : namelist) {
		    					chardb.imagedb.put(c+"-"+e, new Image("./res/char/"+c+"-"+e+".png"));
		    				}
		    			}
		    		} else {	//Node?
		    			m = p.matcher(line);
		    			
		    			if(m.find()){
		    				params = m.group(2);
		    				ms = ps.matcher(params);
		    				if(ms.find()){
		    					params = params.replace(ms.group(1), "@");
		    				}
		    				
		    				//System.out.println(params);
		    				
		    				name = m.group(1);
		    				scene = params.split(", ")[0];
		    				characters  = params.split(", ")[1];
		    				speaker = params.split(", ")[2];
		    				emote = params.split(", ")[3];
		    				sound = params.split(", ")[4];
		    				dialog = ms.group(1);
		    				options = params.split(", ")[6];
		    				flagset = params.split(", ")[7];
		    				flagbra = params.split(", ")[8];
		    				children = params.split(", ")[9];
		    				
		    				//Format the data into a usable form
		    				characters = characters.replace("{", "").replace("}", "");
		    				chars = characters.split(",");
		    				for(int i = 0; i<chars.length; i++) {
		    					if(chars[i].equals("null")) {
		    						chars[i] = null;
		    					}
		    				}
		    				
		    				switch(speaker) {
		    					case "left":
		    						speak = TextBox.left;
		    						break;
		    					case "center":
		    						speak = TextBox.center;
		    						break;
		    					case "right":
		    						speak = TextBox.right;
		    						break;
		    				}
		    				
		    				if(emote.equals("null")) {
		    					emote = null;
		    				}
		    				
		    				if(sound.equals("null")) {
		    					sound = null;
		    				}
		    				
		    				dialog = dialog.replace("\"", "").replace("\\n", "\n");
		    				
		    				
		    				if(options.equals("null")) {
		    					ops = null;
		    				} else {
			    				options = options.replace("{", "").replace("}", "");
			    				ops = options.split(",");
			    				for(int i = 0; i<ops.length; i++) {
		    						ops[i] = ops[i].replace("\"", "");;
			    				}
		    				}
		    				

		    				if(flagset.equals("null")) {
		    					flagssf = null;
		    				} else {
			    				flagset = flagset.replace("{", "").replace("}", "");
			    				flagss = flagset.split(",");
			    				flagssf = new int[flagss.length];
			    				for(int i = 0; i<flagss.length; i++) {
			    					flagssf[i] = Integer.parseInt(flagss[i]);
			    				}
		    				}
		    				
		    				if(flagbra.equals("null")) {
		    					flagsb = null;
		    				} else {
			    				flagbra = flagbra.replace("{", "").replace("}", "");
			    				flagsb = flagbra.split("\",");
			    				for(int i = 0; i<flagsb.length; i++) {
			    					if(!flagsb[i].endsWith("\"")){
			    						flagsb[i] += "\"";
			    					}
			    					flagsb[i] = flagsb[i].replace("\"", "");;
			    				}
		    				}
		    				
		    				if(children.equals("null")) {
		    					childs = null;
		    				} else {
			    				children = children.replace("{", "").replace("}", "");
			    				childs = children.split(",");
			    				/*childsf = new StoryNode[childs.length];
			    				for(int i = 0; i<childs.length; i++) {
			    					childsf[i] = nodemap.get(childs[i]);
			    				}*/
		    				}
		    				
		    				tempNode = new StoryNode(this, scene, chars, speak, emote, sound, dialog, ops, flagssf, flagsb, childs);
		    				nodemap.put(name, tempNode);
		    			}
		    		}
		        }
		    }
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println(chardb.toString());
		dialog = new TextBox(this, new String[]{"2ds"}, TextBox.left, null, null, "foo", null, null, null);
		dialog.init();
		
		currentNode = nodemap.get("start");
		currentNode.init();
		f = new TrueTypeFont(new Font("Consolas", Font.BOLD, 16), true);
		//music.play();
	}

	@Override
	public void update(GameContainer gc, int i) throws SlickException {
		Input input = gc.getInput();

		if (input.isKeyPressed(Input.KEY_ESCAPE)) {
			System.exit(0);
		}
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		currentNode.render(gc, g);
		
		g.drawString(flags.toString(), 32, 32);
		
		dialog.render(g, gc);
		dialog.increment();
	}

	public static void main(String[] args) {
		try {
			AppGameContainer appgc;
			appgc = new AppGameContainer(new ThisGame("I AM A BIG MEMER DUDE"));
			appgc.setDisplayMode(640, 480, false);
			//appgc.setShowFPS(false);
			appgc.setVSync(true);
			appgc.start();
		} catch (SlickException ex) {
			Logger.getLogger(ThisGame.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}