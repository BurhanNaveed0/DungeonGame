import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.ArrayList;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.util.Arrays;
import java.util.Scanner;
 	
public class MazeProject extends JPanel implements KeyListener, ActionListener
{
	// Constants
	public static final String[] LVL_NAMES = {"maze0.txt", "maze1.txt", "maze2.txt"};

	// GUI and Event Handling
	private JFrame frame;
	private BufferedImage heart;
	private BufferedImage key;
	private BufferedImage portal;
	private Timer t;
	private int width = 1500, height = 1000, size = 30;

	// Maze grid
	private char[][] maze;

	// Maze Elements
	private MazeElement finish;
	private Explorer explorer;
	private ArrayList<Monster> monsters;
	private ArrayList<MazeElement> keys;
	private ArrayList<Portal> portals;


	// State Variables
	private boolean roundOver = false;
	private boolean canTele = false;
	private String state = "";
	private int totalKeys = 0, level = 0;

	public MazeProject() {
		// Initialize made grid array based on current level
		setBoard(LVL_NAMES[level]);

		// Create game frame
		frame = new JFrame("A-Mazing Program");
		frame.setSize(width,height);
		frame.add(this);
		frame.addKeyListener(this);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);

		// Initialize UI Images
		try {
			heart = ImageIO.read(new File("heart.png"));
			key = ImageIO.read(new File("key.png"));
			portal = ImageIO.read(new File("portal.png"));
		} catch (IOException e) {
			System.out.println("UI Images Failed to load!");
		}

		// Trigger action performed for monster movement
		t = new Timer(500, this);
		t.start();
	}

	public void paintComponent(Graphics g)
	{
		super.paintComponent(g);
		Graphics2D g2=(Graphics2D)g;
		g2.setColor(Color.BLACK);
		g2.fillRect(0,0,frame.getWidth(),frame.getHeight());

		// Loop through maze array
		for(int r=0;r<maze.length;r++) {
			for(int c=0;c<maze[0].length;c++){
				g2.setColor(new Color(150, 70, 0));

				// Draw board according to maze array
				// Draw wall
				if (maze[r][c]=='#')
					g2.fillRect(c*size+size,r*size+size,size,size);
				// Draw finish block
				else if(r == finish.getLoc().getR() && c == finish.getLoc().getC())
					g2.drawImage(finish.getImg(), c*size+size,r*size+size,size,size,null, this);
				// Draw explorer block
				else if(r == explorer.getLoc().getR() && c == explorer.getLoc().getC() && !roundOver)
					g2.drawImage(explorer.getImg(), c*size+size,r*size+size,size,size,null, this);
				else {
					// Loop through ArrayList of keys and draw them
					for(MazeElement key : keys) {
						if(r == key.getLoc().getR() && c == key.getLoc().getC()) {
							g2.drawImage(key.getImg(), c*size+size,r*size+size,size,size,null, this);
						}
					}

					// Loop through ArrayList of Monsters and draw them
					for(Monster monster : monsters) {
						if(r == monster.getLoc().getR() && c == monster.getLoc().getC()) {
							g2.drawImage(monster.getImg(), c*size+size,r*size+size,size,size,null, this);
						}
					}

					// Loop through ArrayList of Portal and draw them
					for(Portal portal : portals) {
						if(r == portal.getLoc().getR() && c == portal.getLoc().getC()) {
							g2.drawImage(portal.getImg(), c*size+size,r*size+size,size,size,null, this);
						}
					}

					g2.drawRect(c*size+size,r*size+size,size,size);  // Open
				}
			}
		}

		// Display at bottom of page
		int hor = size;
		int vert = maze.length*size + 2*size;

		try {
			g2.setFont(Font.createFont(Font.TRUETYPE_FONT, new File("PixelGameFont.ttf")).deriveFont(20f));
		} catch (FontFormatException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		g2.setColor(Color.PINK);
		g2.drawString("Steps Taken: " + explorer.getSteps(), hor,vert);

		// Draw hearts depending on lives left
		for(int i = 0; i < explorer.getLives(); i++) {
			g2.drawImage(heart, hor + i*40, vert + 20, size, size, null, null);
		}

		// Draw number of teleports available
		for(int i = 0; i < explorer.getPortals(); i++) {
			g2.drawImage(portal, hor + i*40, vert + 60, size, size, null, null);
		}

		// Draw keys based on keys left
		for(int i = 0; i < explorer.getKeys(); i++) {
			g2.drawImage(key, hor + i*40, vert + 100, size, size, null, null);
		}

		// Display finish text if finished and dead text if player is dead
		if(roundOver && state == "finished") {
			g2.setColor(Color.GREEN);
			// Write different text for the final level
			if (level == LVL_NAMES.length - 1)
				g2.drawString("You won! Wooo! Press space to quit...", hor, vert + 160);
			else
				g2.drawString("Press space to go to the next level!", hor, vert + 160);
		} else if (roundOver && state == "dead") {
			g2.setColor(Color.RED);
			if (explorer.getLives() > 1) {
				g2.drawString("You DIED! Hah! Press space to respawn...", hor, vert + 160);
			} else {
				explorer.decLives();
				g2.drawString("Daaang... you actually suck... hit space to get quit...", hor, vert + 160);
				repaint();
			}
		}
	}

	public void keyPressed(KeyEvent e){
		// Check for space bar press when round over
		if(roundOver) {
			if(e.getKeyCode() == KeyEvent.VK_SPACE){
				roundOver = false;

				// Handle level finished
				if(state == "finished") {
					// Close game after final level
					if (level + 1 > LVL_NAMES.length - 1) {
						frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
						// Otherwise increase the level
					} else {
						level++;
						explorer.setPortals(6);
					}
				} else if(state == "dead") { // Close game if dead and lives are finished
					if(explorer.getLives() < 1)
						frame.dispatchEvent(new WindowEvent(frame, WindowEvent.WINDOW_CLOSING));
					else // Decrease number of lives if lives are above 0
						explorer.decLives();
				}

				// Reset the board
				setBoard(LVL_NAMES[level]);
			}
		} else { // Handle in-game input
			explorer.move(e.getKeyCode(), maze);

			// Handle player reaching finish block with all the keys
			if(explorer.intersects(finish) && explorer.getKeys() == totalKeys) {
				roundOver = true;
				state = "finished";
				repaint();
				return;
			} else {
				// Handle player collision with Monsters
				for (Monster monster : monsters) {
					if(explorer.intersects(monster)) {
						roundOver = true;
						state = "dead";

						repaint();
						return;
					}
				}

				// Handle player collision with Portals
				for (Portal portal : portals) {
					if(explorer.intersects(portal) && canTele && explorer.getPortals() > 0) {
						canTele = false;
						explorer.decPortals();

						// Check if portal is sends you forward do further back in the level
						if(portal.getDestination() != null)
							explorer.getLoc().set(portal.getDestination().getLoc().getR(), portal.getDestination().getLoc().getC());
						else if(portal.getPrevious() != null)
							explorer.getLoc().set(portal.getPrevious().getLoc().getR(), portal.getPrevious().getLoc().getC());

						repaint();
						return;
					}
				}

				// Handle player collision with keys
				for(int i = 0; i < keys.size(); i++) {
					// Check for collision sixth each key
					if (explorer.intersects(keys.get(i))) {
						// Remove key from game and increment num of keys that the player possesses
						keys.remove(i);
						explorer.incKeys();

						// Allow the player to walk over the finish block only if all the keys have been grabbed
						// Sets finish block to ' ' from 'F' on maze array
						// Player move() method checks for the neighboring block to marked as ' ' to be able to move there
						if (explorer.getKeys() == totalKeys) {
							maze[finish.getLoc().getR()][finish.getLoc().getC()] = ' ';
						}

						repaint();
						return;
					}
				}
			}

			// If player teleport is disabled and the player has walked out of the teleport, re-enable teleport abilities
			if(!canTele) {
				canTele = true;
			}
		}

		repaint();
	}

	/*** empty methods needed for interfaces **/
	public void keyReleased(KeyEvent e){}
	public void keyTyped(KeyEvent e){}

	public void actionPerformed(ActionEvent e) {
		// Check if game is ongoing
		if(!roundOver) {
			try {
				// Move each monster when the timer goes off
				for (Monster monster : monsters) {
					// Sets each monster's direction and moves it
					monster.setDir(maze);
					monster.move();

					// Handle player collision with Monsters
					if(explorer.intersects(monster)) {
						roundOver = true;
						state = "dead";
					}

					// Repaint after each move
					repaint();
				}
			} catch (Exception exception) {

			}
		}

	}

	public void setBoard(String fileName) {
		// Attempt to read file
		try {
			// Uses Nested ArrayList to allow for changes to map size on the text file
			ArrayList<ArrayList<Character>> grid = new ArrayList();

			// Initialize/reset monsters and keys ArrayList
			monsters = new ArrayList<>();
			keys = new ArrayList<>();
			portals = new ArrayList<>();
			totalKeys = 0;

			// Read text file and append each line of characters to an ArrayList
			BufferedReader input = new BufferedReader(new FileReader(fileName));
			String text;

			while((text = input.readLine()) != null) {
				// Get array of characters from line on text file
				char[] textArr = text.toCharArray();
				ArrayList<Character> line = new ArrayList<Character>();

				// Add each character to 2D ArrayList grid
				for(int i = 0; i < textArr.length; i++)
					line.add(textArr[i]);
				grid.add(line);
			}

			// Create temporary character array to store grid values
			char[][] temp = new char[grid.size()][grid.get(0).size()];

			// loop through the 2D array list grid
			for(int i = 0; i < grid.size(); i++) {
				for(int j = 0; j < grid.get(0).size(); j++) {

					// Set element in temp[][] to corresponding value in 2D Array list
					temp[i][j] =  grid.get(i).get(j);

					// Initialize game object classes based on markings on grid

					// Initialize Finish Maze Element
					if (temp[i][j] == 'F') {
						finish = new MazeElement(new Location(i,j),size,"finish.png");
					}

					// Initialize Explorer Maze Element
					if (temp[i][j] == 'E') {
						explorer = new Explorer(new Location(i,j), size);
						temp[i][j] = ' ';
					}

					// Add to Monster ArrayList
					if(temp[i][j] == 'M') {
						monsters.add(new Monster(new Location(i, j), size));
						temp[i][j] = ' ';
					}

					// Add to Key ArrayList
					if(temp[i][j] == 'K') {
						keys.add(new MazeElement(new Location(i, j), size, "key.png"));
						temp[i][j] = ' ';
						totalKeys++;
					}

					// Add to Portal ArrayList
					if(temp[i][j] == 'P') {
						portals.add(new Portal(new Location(i, j), size));
					}
				}
			}

			// Set next destination for portal pairs
			for(int i = 0; i < portals.size()-1; i+=2) {
				portals.get(i).setDestination(portals.get(i+1));
			}

			// Set previous portal for portal pairs
			for(int i = portals.size()-1; i > 0; i-=2) {
				portals.get(i).setPrevious(portals.get(i-1));
			}

			// Set maze array to temp array of the grid
			maze = temp;

		} catch (IOException io) { // Catch file exception
			System.err.println("Error reading file => " + io);
		}
	}

	public static void main(String[] args){
		MazeProject app = new MazeProject();
	}
}