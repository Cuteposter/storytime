package storytime;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;

public class StoryNode {
	ThisGame game;
	TextBox text;
	StoryNode[] children;
	
	public StoryNode(ThisGame g, TextBox t, StoryNode[] c) {
		game = g;
		text = t;
		
		text.setPaths(c);
	}
	
	public void render(GameContainer gc, Graphics g){
		text.render(g, gc);
		text.increment();
	}
}
