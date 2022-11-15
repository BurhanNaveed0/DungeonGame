import java.awt.Color;
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.awt.image.*;
import java.awt.event.*;
import javax.imageio.ImageIO;

public class Explorer extends MazeElement
{
	// Constants
	public static final String[] IMG_NAMES = {"expUp.png", "expRight.png", "expDown.png", "expLeft.png"};

	// State Variables
	private static int lives = 3;
	private static int portals = 6;
	private int dir;
	private int steps;
	private int numKeys;

	// UI
	private BufferedImage[] images;

	public Explorer(Location loc, int size) {
		super(loc, size);

		// Initialize steps and initial direction(right)
		this.dir = 1;
		this.steps = 0;
		this.numKeys = 0;

		// Load player images for each direction
		images = new BufferedImage[IMG_NAMES.length];
		for(int i = 0; i < images.length; i++) {
			try {
				images[i] = ImageIO.read(new File(IMG_NAMES[i]));
			} catch (IOException e) {
				System.out.println("Image not loaded");
			}
		}
	}

	public int getKeys() {
		return this.numKeys;
	}

	public int getSteps() {
		return this.steps;
	}

	public int getLives() { return this.lives; }

	public int getPortals() { return this.portals; }

	public void setPortals(int count) { this.portals = count; }

	public void incKeys() {
		this.numKeys++;
	}

	public void decLives() { this.lives--; }

	public void decPortals() { this.portals--; }

	@Override
	public BufferedImage getImg()
	{
		// Return image corresponding to direction
		return images[dir];
	}

	@Override
	public void move(int key, char[][] maze) {
		// Get player location
		int r = getLoc().getR();
		int c = getLoc().getC();

		// Increment direction(Turn right) if right arrow is pressed
		if(key == KeyEvent.VK_RIGHT) {
			if(dir < 3)
				dir++;
			else
				dir = 0;
		} else if(key == KeyEvent.VK_LEFT) { // Decrement direction(Turn left) if left arrow is pressed
			if(dir > 0)
				dir--;
			else
				dir = 3;
		} else if(key == KeyEvent.VK_UP) { // Move in current direction if up arrow is pressed
			// Handle movement based on direction
			switch(dir) {
				// Handle upwards movement
				case(0):
					// Check for space on the left of player
					if(maze[r-1][c] == ' ' || maze[r-1][c] == 'P') {
						getLoc().set(r-1, c);
						steps++;
					}

					break;

				// Handle right-facing movement
				case(1):
					// Check for space on the right of player
					if(maze[r][c+1] == ' ' || maze[r][c+1] == 'P') {
						getLoc().set(r, c+1);
						steps++;
					}

					break;

				// Handle downwards movement
				case(2):
					// Check for space below player
					if(maze[r+1][c] == ' ' || maze[r+1][c] == 'P') {
						getLoc().set(r+1, c);
						steps++;
					}

					break;

				// Handle left facing movement
				case(3):
					// Check for space on the left of player
					if(maze[r][c-1] == ' ' || maze[r][c-1] == 'P') {
						getLoc().set(r, c-1);
						steps++;
					}

					break;
			}
		}
	}
}