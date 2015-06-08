package storytime;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;

public class StoryNode {
	ThisGame game;
	String[] character;
	Image[] portrait;
	String emote, sfx, text;
	String[] options;
	int speaker;
	int[] flags;
	StoryNode[] paths;
	
	public StoryNode(ThisGame g, String[] c, int s, String e, String snd, String t, String[] o, int[] f, StoryNode[] p) {
		game = g;
		character = c;
		speaker = s;
		emote = e;
		sfx = snd;
		text = t;
		flags = f;
		options = o;
		
		if (p != null)
			paths = p;
	}
	
	public void init () throws SlickException {
		//text.init();
		game.dialog.emote = this.emote;
		game.dialog.sfx = this.sfx;
		game.dialog.text = this.text;
		game.dialog.speaker = this.speaker;
		game.dialog.characters = this.character;
		game.dialog.setPaths(paths);
		game.dialog.flags = this.flags;
		game.dialog.options = this.options;
		game.dialog.reset();
		
	}
	
	public void render(GameContainer gc, Graphics g){
		//text.render(g, gc);
		//text.increment();
	}
}
