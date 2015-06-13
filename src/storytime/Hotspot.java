package storytime;

import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.SpriteSheet;

//Small class to serve as a quadruple for a map hotspot
public class Hotspot {
	int x,y,w,h;
	String link;	//Can either be a module or a hub. Check extension
	boolean selected = false;
	SpriteSheet corner;
	Animation corneranim;
	
	public Hotspot(int x, int y, int w, int h, String link) throws SlickException {
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
		this.link = link;
		
		corner = new SpriteSheet("./res/gui/corner.png", 28, 28);
		corneranim = new Animation(corner, new int[] {
				0, 0, 1, 0
			}, new int[] {
				300, 300
			});
	}
	
	public void update(GameContainer gc, int i) {
		corneranim.update(i);
	}
	
	public void render(GameContainer gc, Graphics g){
		if(selected) {
			corneranim.getCurrentFrame().setRotation(0);
			corneranim.getCurrentFrame().draw(x-8, y-8);
			corneranim.getCurrentFrame().setRotation(90);
			corneranim.getCurrentFrame().draw(x+w-8, y-8);
			corneranim.getCurrentFrame().setRotation(270);
			corneranim.getCurrentFrame().draw(x-8, y+h-8);
			corneranim.getCurrentFrame().setRotation(180);
			corneranim.getCurrentFrame().draw(x+w-8, y+h-8);
		}
	}
}
