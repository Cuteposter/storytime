package storytime;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.Sound;

public class Hub {
	Image background;
	Music music;
	Sound move, decide;
	int sel = 0;
	ArrayList<Hotspot> hotspots = new ArrayList<Hotspot>();
	ThisGame game;
	public Hub(ThisGame game, String hub) throws FileNotFoundException, IOException, SlickException{
		this.game = game;
		
		parseHub(hub);
		if(music != null) {
			game.music = music;
			game.music.loop(1f, 0.5f);
		}
		hotspots.get(0).selected = true;
		
		move = new Sound("./res/sfx/move.wav");
		decide = new Sound("./res/sfx/decide.wav");
	}
	
	public void update(GameContainer gc, int i) throws SlickException, FileNotFoundException, IOException {
		Input input = gc.getInput();
		if (input.isKeyPressed(Input.KEY_UP) && sel > 0) {
			hotspots.get(sel).selected = false;
			sel--;
			hotspots.get(sel).selected = true;
			move.play();
		}

		if (input.isKeyPressed(Input.KEY_DOWN) && sel < hotspots.size() - 1) {
			hotspots.get(sel).selected = false;
			sel++;
			hotspots.get(sel).selected = true;
			move.play();
		}
		
		if (input.isKeyPressed(Input.KEY_SPACE)) {
			decide.play();
			loadLink(hotspots.get(sel));
		}
		
		for(Hotspot h : hotspots) {
			h.update(gc, i);
		}
	}
	
	public void render(GameContainer gc, Graphics g){
		g.drawImage(background, 0, 0);
		
		for(Hotspot h : hotspots) {
			h.render(gc, g);
		}
	}
	
	public void parseHub(String hub) throws FileNotFoundException, IOException, SlickException {
		try(BufferedReader in = new BufferedReader(new FileReader("./res/hub/"+hub))){
		    String line;
		    String[] gamets;
		    Hotspot temp;
		    while((line=in.readLine())!=null){
		    	//TODO Do something
		        if(!line.startsWith("#")) {	
		        	if(line.startsWith("bg")){
		        		background = new Image("./res/bg/"+line.split("=")[1]);
		        	}
		        	
		        	if(line.startsWith("music")){
		        		if(!line.split("=")[1].equals("null")) {
		        			music = game.musicmap.get(line.split("=")[1]);//new Music("./res/mus/"+line.split("=")[1]);
		        		}
		        	}
		        	
		        	//Hotspot
		        	if(line.startsWith("{")){
		        		line = line.replace("{","").replace("}","");
		        		gamets = line.split(",");
		        		
		        		temp = new Hotspot(Integer.parseInt(gamets[0]), Integer.parseInt(gamets[1]), Integer.parseInt(gamets[2]), Integer.parseInt(gamets[3]), gamets[4]);
		        		hotspots.add(temp);
		        	}
		        }
		    }
		}
	}
	
	public void loadLink(Hotspot h) throws SlickException, FileNotFoundException, IOException {
		System.out.println(h.link);
		
		if(h.link.contains(".mod")) {	//module link, load the module
			game.nodemap = new HashMap<String, StoryNode>();
			game.parseModule(h.link);
			game.currentNode.init();
			game.dialog.reset();
			game.state = game.MODULE;
		}else if(h.link.contains(".hub")) {	//hub link
			game.currentHub = new Hub(game, h.link);
		}
	}
}
