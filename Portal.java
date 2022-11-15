import java.awt.image.BufferedImage;

public class Portal extends MazeElement {
    // Constants
    private static final String IMG_NAME = "portal.png";

    // Field to store destination portal
    private Portal destination;
    private Portal previous;

    public Portal(Location loc, int size) {
        super(loc, size, IMG_NAME);
        this.destination = null;
    }

    public void setDestination(Portal destination) {
        this.destination = destination;
    }

    public Portal getDestination() {
        return destination;
    }

    public void setPrevious(Portal previous) {
        this.previous = previous;
    }

    public Portal getPrevious() {
        return this.previous;
    }
}
