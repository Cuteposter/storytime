package storytime;

import java.util.Arrays;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;

public class StoryNode {
	ThisGame game;
	String[] character;
	Image[] portrait;
	Image scene;
	String emote, sfx, bgm, text;
	String[] options, pflags;
	int speaker;
	int[] flags;
	String[] paths;
	Music music;
	
	public StoryNode(ThisGame g, String sc, String[] c, int s, String e, String snd, String mus, String t, String[] o, int[] f, String pf[], String[] p) throws SlickException {
		game = g;
		character = c;
		speaker = s;
		emote = e;
		sfx = snd;
		bgm = mus;
		text = t;
		flags = f;
		pflags = pf;
		options = o;
		
		scene = new Image("./res/bg/"+sc);
		
		if (p != null)
			paths = p;
		
		//System.out.println(this.toString());
	}
	
	public void init () throws SlickException {
		//text.init();
		game.dialog.emote = this.emote;
		game.dialog.sfx = this.sfx;
		game.dialog.text = this.text;
		game.dialog.speaker = this.speaker;
		game.dialog.characters = this.character;
		game.dialog.paths = this.paths;
		game.dialog.flags = this.flags;
		game.dialog.pflags = this.pflags;
		game.dialog.options = this.options;
		game.dialog.reset();
		
		if(bgm != null) {
			music = game.musicmap.get(bgm);
			if(game.music != null) {
				if(!game.music.equals(music)) {
					game.music = this.music;
					//game.music.
					game.music.loop(1, 0.5f);
				}
			}else{
				game.music = this.music;
				//game.music.
				game.music.loop();
			}
		}
	}
	
	public void render(GameContainer gc, Graphics g){
		//text.render(g, gc);
		//text.increment();
		g.drawImage(scene, 0, 0);
	}
	
	public String toString() {
		return scene.toString() +", "+ Arrays.toString(character) +", "+ speaker +", "+ emote +", "+ sfx +", "+ bgm +", "+ text +", "+ Arrays.toString(options) +", "+ Arrays.toString(flags) +", "+ Arrays.toString(pflags) +", "+ Arrays.toString(paths);
	}
}
