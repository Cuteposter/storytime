package storytime;

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

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
		Music music = new Music("res/song.ogg");
		
		//Init flag database
		//Flag -1 is no flag
		flags = new ArrayList < Boolean > ();
		for (int i = 0; i < 5; i++) {
			flags.add(false);
		}

		//All branching story nodes, and their text need to be defined here
		//Game needs to be defined in reverse order
		dialog = new TextBox(this, new String[]{"2ds"}, TextBox.left, null, null, "foo", null, null);
		dialog.init();
		
		//NODE END
		StoryNode NE = new StoryNode(this, new String[]{null,"vita"}, TextBox.center, null, null, "Thanks for helping me make a tasty [C:green]sandwich. I really liked it!", null, null, null);
		
		
		//NODE 3, FROM NODE 2
		StoryNode N3BA = new StoryNode(this, new String[]{null,"vita"}, TextBox.center,  null, "yay.wav", "Yummy! A [C:green][F:3:ham,turkey] on [C:blue][F:1:white,wheat] sandwich!", null, null, new StoryNode[] {
			NE
		});
		
		StoryNode[] N3 = new StoryNode[] {
				N3BA, N3BA
		};

		StoryNode N2BA = new StoryNode(this, new String[]{"2ds",null,"vita"}, TextBox.left, "question", "wonder.wav", "What should the [C:blue]meat be?", new String[] {
			"Ham", "Turkey"
		}, new int[] {
			3, 4
		}, N3);

		StoryNode[] N2 = new StoryNode[] {
			N2BA, N2BA
		};

		//NODE 1
		StoryNode N1B1 = new StoryNode(this, new String[]{"2ds",null,"vita"}, TextBox.right , "question", "wonder.wav", "What should the [C:green]bread be?", new String[] {
			"White", "Wheat"
		}, new int[] {
			1, 2
		}, N2);
		StoryNode N1B2 = new StoryNode(this, new String[]{"2ds"}, TextBox.left, null, null, "HOLY SHIT I FUCKING HATE YOU [C:green]/v/", null, null, null);
		StoryNode N1B3 = new StoryNode(this, new String[]{"2ds"}, TextBox.left,  null, null, "[C:red]AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA", null, null, null);

		StoryNode[] N1 = new StoryNode[] {
			N1B1, N1B2, N1B3
		};

		//Set first node
		currentNode = new StoryNode(this, new String[] {null,"2ds"}, TextBox.center, null, null, "I'm pretty [C:blue]hungry today. Let's make a [C:green]sandwich! \nDo you want to help me make a [C:green]sandwich?", new String[] {
			"Yes", "No", "MOOOOOOOOOOODS"
		}, new int[] {
			0, -1, -1
		}, N1);
		currentNode.init();
		f = new TrueTypeFont(new Font("Consolas", Font.BOLD, 16), true);

		gamestop = new Image("res/Gamestop.jpg");
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
		//text.arrow.update(60);

		//if(currentNode == null) {
		//System.exit(0);
		//}

		g.drawImage(gamestop, 0, 0);
		g.drawString(flags.toString(), 32, 32);

		//currentNode.render(gc, g);

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