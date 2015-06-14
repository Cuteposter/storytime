package storytime;

import java.awt.Font;
import java.io.BufferedReader;
import java.io.File;
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
import org.newdawn.slick.TrueTypeFont;

public class ThisGame extends BasicGame {
	static AppGameContainer appgc;
	
	TextBox dialog;
	TrueTypeFont f;
	Image title;
	Music music;
	Characters chardb;
	
	//State constants
	final int TITLE = 0;
	final int HUB = 1;
	final int MODULE = 2;
	
	int state;
	
	public HashMap<String, StoryNode> nodemap = new HashMap<String, StoryNode>();
	public HashMap<String, Music> musicmap = new HashMap<String, Music>();
	//public HashMap<String, HashMap<String, StoryNode>> modulemap = new HashMap<String, HashMap<String, StoryNode>>();
	public StoryNode currentNode;
	public Hub currentHub;
	public String foo = "foo";
	public ArrayList < Boolean > flags;

	int blink = 1;
	Color col = Color.white;
	
	//All the story nodes need to be in the global scope to be accessible for flag-dependent changes
	//StoryNode N1B1, N1B2, N1B3, N2BA, N3BA;
	
	public ThisGame(String gamename) {
		super(gamename);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		state = TITLE;
		title = new Image("./res/gui/title.png");
		chardb = new Characters();
		
		parseInit("init.mod");
		//preload music
		File dir = new File("./res/mus");
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			for (File m : directoryListing) {
				musicmap.put(m.getName(), new Music(m.getPath(), true));
			}
		}
		//InputStream[] musicStream = {ResourceLoader.getResourceAsStream("./res/mus/title.ogg")}
		music = musicmap.get("title.ogg");
		
		appgc.setTitle("I'M A BIG MEMER DUDE");
		 
		//System.out.println("Working Directory = " +System.getProperty("user.dir"));
		dialog = new TextBox(this);
		dialog.init();
		
		//parseModule("./game.mod");
		//currentNode = new StoryNode(this, "Gamestop.jpg", new String[]{"2ds"}, TextBox.left, null, null, null, "foo", null, null, null, null);
		// = new TextBox(this, new String[]{"2ds"}, TextBox.left, null, null, "foo", null, null, null);
		
		
		//currentNode = nodemap.get("start");
		//currentNode.init();
		f = new TrueTypeFont(new Font("Consolas", Font.BOLD, 16), true);
		//music.play();
	}

	@Override
	public void update(GameContainer gc, int i) throws SlickException {
		Input input = gc.getInput();
		
		if(input.isKeyPressed(Input.KEY_R)) {
			music.stop();
			music = musicmap.get("title.ogg");
			state = TITLE;
			for(int f=0; f<flags.size(); f++) {
				flags.set(f, false);
			}
		}
		
		if(state == TITLE) {
			if(!music.playing()) {
				music.loop(1, 0.5f);
			}
			
			if(input.isKeyPressed(Input.KEY_SPACE)) {
				try {
					currentHub = new Hub(this, "maptest.hub");
				} catch (FileNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				state = HUB;
			}
		}
		
		if(state == HUB) {
			try {
				currentHub.update(gc, i);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		if (input.isKeyPressed(Input.KEY_ESCAPE)) {
			System.exit(0);
		}
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException {
		g.setFont(f);
		
		if(state == TITLE) {
			if(blink == 60) {
				if(col == Color.white){col = Color.black;}
				else {col = Color.white;}
				blink = 1;
			}else{
				blink++;
			}
			g.setColor(col);
			g.drawString("Press Space to start", 320-f.getWidth("Press Space to start")/2, 256);
			
			g.setColor(Color.white);
			g.drawImage(title, 320-title.getWidth()/2, 80);
			g.drawString("CONTROLS", 320-f.getWidth("Press Space to start")/2, 324);
			g.drawString("Space: advance, select",320-f.getWidth("Press Space to start")/2,340);
			g.drawString("Arrow keys: move cursor",320-f.getWidth("Press Space to start")/2,356);
			g.drawString("ESC: quit game",320-f.getWidth("Press Space to start")/2,372);
			g.drawString("R: reset",320-f.getWidth("Press Space to start")/2,388);
		}
		
		if(state == HUB) {
			currentHub.render(gc, g);
		}
		
		if(state == MODULE) {
			currentNode.render(gc, g);
			dialog.render(g, gc);
			dialog.increment();
			g.drawString(flags.toString(), 32, 32);
		}
		
	}

	public static void main(String[] args) {
		try {
			appgc = new AppGameContainer(new ThisGame("Loading..."));
			appgc.setDisplayMode(640, 480, false);
			//appgc.setShowFPS(false);
			appgc.setVSync(true);
			appgc.start();
		} catch (SlickException ex) {
			Logger.getLogger(ThisGame.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
	
	public void parseModule(String module) throws SlickException {
		//Holy shit modle parser. I don't even want to describe it
		try(BufferedReader in = new BufferedReader(new FileReader("./res/mod/"+module))){
		    String line;
		    float ver;
		    int f = 0;
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
			String params, name, scene, characters, speaker, emote, sound, music, dialog, options, flagset, flagbra, children;
			
			String[] charlist, emotes, chars, ops, flagss, flagsb, childs;
			int[] flagssf;
			int speak = 0;
			
		    while((line=in.readLine())!=null){
		        if(line.startsWith("#Version")) {
		        	ver = Float.parseFloat(line.split("=")[1]);
		        }
		        
		        if(!line.startsWith("#")) {		    		
		    		if(line.startsWith("nodes")) {
		    			nodes = line.split("=")[1].split(", ");
		    			for(String n : nodes) {
		    				nodemap.put(n, null);
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
		    				music = params.split(", ")[5];
		    				dialog = ms.group(1);
		    				options = params.split(", ")[7];
		    				flagset = params.split(", ")[8];
		    				flagbra = params.split(", ")[9];
		    				children = params.split(", ")[10];
		    				
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
		    				
		    				if(music.equals("null")) {
		    					music = null;
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
		    				
		    				tempNode = new StoryNode(this, scene, chars, speak, emote, sound, music, dialog, ops, flagssf, flagsb, childs);
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
		
		currentNode = nodemap.get("start");
		currentNode.init();
		
	}
	
	public void parseInit(String module) throws SlickException {
		//Holy shit modle parser. I don't even want to describe it
		try(BufferedReader in = new BufferedReader(new FileReader("./res/mod/"+module))){
		    String line;

			String[] charlist, emotes;
			ArrayList<String> namelist = new ArrayList<String>();
		    float ver;
		    int f = 0;
			flags = new ArrayList < Boolean > ();
			
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
		    		} else if(line.startsWith("characters")) {
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
	}
}