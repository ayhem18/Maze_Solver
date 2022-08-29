package Project.src_1;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class represents the abstract actor representing the common fields common between
 * the concrete actors
 */

public abstract class Actor {
    protected VisionField visionField;
    protected ActionField actionField;
    protected Position currentPosition;
    protected boolean isWearingClock;
    protected final Set<Position> supervisedPositions;

    protected final List<Position> path;

    public Actor(VisionField visionField, ActionField actionField) {
        this.visionField = visionField;
        this.actionField = actionField;
        this.isWearingClock = false;
        this.supervisedPositions = new HashSet<>();
        this.path = new ArrayList<>();
    }

    /**
     *  this method will execute the actor's search in the given library
     *      starting from the given coordinates
     * @param library the library to search
     * @param startingX initial x coordinate
     * @param startingY initial y coordinate
     * @return whether the actor found both the book and the exit or not
     */

    abstract boolean play(Library library, int startingX, int startingY);

    boolean play(Library library, Position startingPosition) {
        return play(library, startingPosition.x(), startingPosition.y());
    }

    abstract String getName();

    public List<Position> getPath() {
        return this.path;
    }

    /**
     * this method sets the fields to their initial values making sure that
     * no data is left from previous iterations
     */
    protected void initialSettings() {
        this.path.clear();
        this.supervisedPositions.clear();
        this.isWearingClock = false;
    }

}
