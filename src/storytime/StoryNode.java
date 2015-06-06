package storytime;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;

public class StoryNode {
	ThisGame game;
	Character character;
	String text;
	String[] options;
	int[] flags;
	StoryNode[] paths;
	
	public StoryNode(ThisGame g, Character c, String t, String[] o, int[] f, StoryNode[] p) {
		game = g;
		character = c;
		text = t;
		flags = f;
		options = o;
		
		if (p != null)
			paths = p;
	}
	
	public void init () throws SlickException {
		//text.init();
		game.dialog.text = text;
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
