import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Monster extends MazeElement {
    // Constants
    private static final String[][] IMG_NAMES = {{"monsterUp0.png", "monsterUp1.png", "monsterUp2.png", "monsterUp3.png"},
                                                 {"monsterRight0.png", "monsterRight1.png", "monsterRight2.png", "monsterRight3.png"},
                                                 {"monsterDown0.png", "monsterDown1.png", "monsterDown2.png", "monsterDown3.png"},
                                                 {"monsterLeft0.png", "monsterLeft1.png", "monsterLeft2.png", "monsterLeft3.png"}};
    // State Variables
    private int dir;
    private int frame;

    // UI
    private BufferedImage[][] images;

    public Monster(Location loc, int size) {
        super(loc, size, "monster.png");

        this.dir = 0;
        this.frame = 0;

        // Load monster images for each direction
        images = new BufferedImage[IMG_NAMES.length][IMG_NAMES[0].length];
        for(int i = 0; i < images.length; i++) {
            for (int j = 0; j < images[i].length; j++) {
                try {
                    images[i][j] = ImageIO.read(new File(IMG_NAMES[i][j]));
                } catch (IOException e) {
                    System.out.println("Image not loaded");
                }
            }
        }
    }

    public void setDir(char[][] maze) {
        // Initialize array list storing possible directions that the monster can move in
        ArrayList<Integer> space = new ArrayList<Integer>();
        // Get monster position
        int row = getLoc().getR();
        int col = getLoc().getC();

        // Check space around the monster and add possible directions to the space ArrayList
        if(maze[row-1][col] == ' ')
            space.add(0);
        if(maze[row][col+1] == ' ')
            space.add(1);
        if(maze[row+1][col] == ' ')
            space.add(2);
        if(maze[row][col-1] == ' ')
            space.add(3);

        // Set the monster direction to a random available direction
        dir = space.get((int)(Math.random() * space.size()));
    }

    public void move() {
        // Access player location
        int row = getLoc().getR();
        int col = getLoc().getC();

        // Move monster based on current direction
        switch(dir) {
            case(0):
                getLoc().set(row-1, col);
                break;
            case(1):
                getLoc().set(row, col+1);
                break;
            case(2):
                getLoc().set(row+1, col);
                break;
            case(3):
                getLoc().set(row, col-1);
                break;
        }

        // Update monster animation frame
        if(frame < 3)
            frame++;
        else
            frame = 0;
    }

    public BufferedImage getImg()
    {
        return images[dir][frame];
    }

}
