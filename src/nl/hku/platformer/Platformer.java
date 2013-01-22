package nl.hku.platformer;

import processing.core.*;
import java.awt.event.KeyEvent;
import ddf.minim.*;

public class Platformer extends PApplet {
	private static final long serialVersionUID = 1L;

	// for storing and referencing animation frames for the player character
	PImage guy_stand, guy_run1, guy_run2;

	// music and sound effects
	AudioPlayer music; // AudioPlayer uses less memory. Better for music.
	AudioSample sndJump, sndCoin; // AudioSample plays more respnosively. Better
									// for sound effects.

	// we use this to track how far the camera has scrolled left or right
	double cameraOffsetX;

	Keyboard theKeyboard;
	Player thePlayer;
	World theWorld;

	PFont font;

	// we use these for keeping track of how long player has played
	int gameStartTimeSec, gameCurrentTimeSec;

	private Minim minim;

	public void setup() {

		
		ResourceManager m = ResourceManager.initialize(this);

		m.loadSample("jump", "jump.wav");
		m.loadSample("coin", "coin.wav");

		m.loadImage("guy_stand", "guy.png");
		m.loadImage("guy_run1", "run1.png");
		m.loadImage("guy_run2", "run2.png");

		font = m.loadFont("font", "SansSerif-20.vlw");

		theKeyboard = new Keyboard();
		thePlayer = new Player(this, theKeyboard);
		theWorld = new World(this);
		
		
		size(600, 480);

		cameraOffsetX = 0.0;

		frameRate(24); // this means draw() will be called 24 times per second

		resetGame(); // sets up player, game level, and timer
	}
	
	public Player getPlayer() {
		return thePlayer;
	}
	
	public World getWorld() {
		return theWorld;
	}
 

	void resetGame() {
		// This function copies start_Grid into worldGrid, putting coins back
		// multiple levels could be supported by copying in a different start
		// grid

		thePlayer.reset(); // reset the coins collected number, etc.

		theWorld.reload(); // reset world map

		// reset timer in corner
		gameCurrentTimeSec = gameStartTimeSec = millis() / 1000; // dividing by
																	// 1000 to
																	// turn
																	// milliseconds
																	// into
																	// seconds
	}

	public Boolean gameWon() { // checks whether all coins in the level have
								// been collected
		return (thePlayer.coinsCollected == theWorld.coinsInStage);
	}

	private void outlinedText(String sayThis, float atX, float atY) {
		textFont(font); // use the font we loaded
		fill(0); // white for the upcoming text, drawn in each direction to make
					// outline
		text(sayThis, atX - 1, atY);
		text(sayThis, atX + 1, atY);
		text(sayThis, atX, atY - 1);
		text(sayThis, atX, atY + 1);
		fill(255); // white for this next text, in the middle
		text(sayThis, atX, atY);
	}

	void updateCameraPosition() {
		int rightEdge = World.GRID_UNITS_WIDE * World.GRID_UNIT_SIZE - width;
		// the left side of the camera view should never go right of the above
		// number
		// think of it as "total width of the game world"
		// (World.GRID_UNITS_WIDE*World.GRID_UNIT_SIZE)
		// minus "width of the screen/window" (width)

		cameraOffsetX = thePlayer.position.x - width / 2;
		if (cameraOffsetX < 0) {
			cameraOffsetX = 0;
		}

		if (cameraOffsetX > rightEdge) {
			cameraOffsetX = rightEdge;
		}
	}

	public void draw() { // called automatically, 24 times per second because of
							// setup()'s call to frameRate(24)
		pushMatrix(); // lets us easily undo the upcoming translate call
		translate((float) -cameraOffsetX, 0); // affects all upcoming graphics
												// calls, until popMatrix

		updateCameraPosition();

		theWorld.render();

		thePlayer.inputCheck();
		thePlayer.move();
		thePlayer.draw();

		popMatrix(); // undoes the translate function from earlier in draw()

		if (focused == false) { // does the window currently not have keyboard
								// focus?
			textAlign(CENTER);
			outlinedText(
					"Click this area to play.\n\nUse arrows to move.\nSpacebar to jump.",
					width / 2, height - 90);
		} else {
			textAlign(LEFT);
			outlinedText("Coins:" + thePlayer.coinsCollected + "/"
					+ theWorld.coinsInStage, 8, height - 10);

			textAlign(RIGHT);
			if (gameWon() == false) { // stop updating timer after player
										// finishes
				gameCurrentTimeSec = millis() / 1000; // dividing by 1000 to
														// turn milliseconds
														// into seconds
			}
			int minutes = (gameCurrentTimeSec - gameStartTimeSec) / 60;
			int seconds = (gameCurrentTimeSec - gameStartTimeSec) % 60;
			if (seconds < 10) { // pad the "0" into the tens position
				outlinedText(minutes + ":0" + seconds, width - 8, height - 10);
			} else {
				outlinedText(minutes + ":" + seconds, width - 8, height - 10);
			}

			textAlign(CENTER); // center align the text

			if (gameWon()) {
				outlinedText("All Coins Collected!\nPress R to Reset.",
						width / 2, height / 2 - 12);
			}
		}
	}

	public void keyPressed(KeyEvent evt) {
		theKeyboard.pressKey(evt.getKeyCode());
	}

	public void keyReleased(KeyEvent evt) {
		theKeyboard.releaseKey(evt.getKeyCode());
	}

	public void stop() { // automatically called when program exits. here we'll
							// stop and unload sounds.
		music.close();
		sndJump.close();
		sndCoin.close();

		minim.stop();

		super.stop(); // tells program to continue doing its normal ending
						// activity
	}


	public static void main(String args[]) {
		//PApplet.main(new String[] { "--present", "nl.hku.platformer.Platformer" });
		PApplet.main(new String[] { "nl.hku.platformer.Platformer" });
	}
}
