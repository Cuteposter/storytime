package storytime;

import java.awt.Font;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.TrueTypeFont;

public class ThisGame extends BasicGame
{
	TextBox text;
	TrueTypeFont f;
	Image twods, gamestop;
	public StoryNode currentNode;
	public String foo = "foo";
	public ArrayList<Boolean> flags;
	
	public ThisGame(String gamename)
	{
		super(gamename);
	}

	@Override
	public void init(GameContainer gc) throws SlickException {
		//Init flag database
		//Flag -1 is no flag
		flags = new ArrayList<Boolean>();
		for(int i=0; i<5; i++) {
			flags.add(false);
		}
		
		//All branching story nodes, and their text need to be defined here
		//Game needs to be defined in reverse order
		
		//NODE 2, FROM NODE 1 BRANCH 1
		TextBox tN2B1 = new TextBox(this, "2DS-tan", "What should the [C:blue]meat be?", new String[]{"Ham", "Turkey"}, new int[]{3, 4});
		
		StoryNode N2B1 = new StoryNode(this, tN2B1, null);
		
		StoryNode[] N2 = new StoryNode[]{N2B1, N2B1};
		
		//NODE 1
		TextBox tN1B1 = new TextBox(this, "2DS-tan", "What should the [C:blue]bread be?", new String[]{"White", "Wheat"}, new int[]{1, 2});
		TextBox tN1B2 = new TextBox(this, "2DS-tan", "HOLY SHIT I FUCKING HATE YOU [C:green]/v/", null, null);
		TextBox tN1B3 = new TextBox(this, "2DS-tan", "[C:red]AAAAAAAAAAAAAAAAAAAAAAAAAAA", null, null);
		
		StoryNode N1B1 = new StoryNode(this, tN1B1, N2);
		StoryNode N1B2 = new StoryNode(this, tN1B2, null);
		StoryNode N1B3 = new StoryNode(this, tN1B3, null);
		
		StoryNode[] N1 = new StoryNode[]{N1B1, N1B2, N1B3};
		
		text = new TextBox(this, "Vita-tan", "W-what do [C:green]you want with a legacy failure console like me?" ,new String[]{"Hugs, of course", "I wasn't talking to you, trash."}, new int[]{0, -1, -1});
		
		//Set first node
		currentNode = new StoryNode(this, text, N1);
		f = new TrueTypeFont(new Font("Consolas", Font.BOLD, 16), true);
		
		twods = new Image("res/vita.png");
		gamestop = new Image("res/Gamestop.jpg");
		
	}

	@Override
	public void update(GameContainer gc, int i) throws SlickException {
		Input input = gc.getInput();
		
		if(input.isKeyPressed(input.KEY_ESCAPE)) {
			System.exit(0);
		}
	}

	@Override
	public void render(GameContainer gc, Graphics g) throws SlickException
	{
		//text.arrow.update(60);
		
		//if(currentNode == null) {
			//System.exit(0);
		//}
		
		g.drawImage(gamestop, 0, 0);
		g.drawImage(twods, 248, 48);
		g.drawString(flags.toString(), 32, 32);
		
		currentNode.render(gc, g);
		
		//text.render(g, gc);
		//text.increment();
	}

	public static void main(String[] args)
	{
		try
		{
			AppGameContainer appgc;
			appgc = new AppGameContainer(new ThisGame("I AM A BIG MEMER DUDE"));
			appgc.setDisplayMode(640, 480, false);
			//appgc.setShowFPS(false);
			appgc.setVSync(true);
			appgc.start();
		}
		catch (SlickException ex)
		{
			Logger.getLogger(ThisGame.class.getName()).log(Level.SEVERE, null, ex);
		}
	}
}