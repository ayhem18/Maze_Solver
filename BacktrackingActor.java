package Project.src_1;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is a concrete implementation of the Actor class using the Backtracking search approach
 */
class BacktrackingActor extends Actor {

    // the search algorithm's name

    private static final String NAME = "Backtracking";

    private final Set<Position> visitedPositions;

    public BacktrackingActor(VisionField visionField, ActionField actionField) {
        super(visionField, actionField);
        visitedPositions = new HashSet<>();
    }
    @Override
    String getName() {
        return NAME + ": " + visionField.getName();
    }

    @Override
    protected void initialSettings() {
        super.initialSettings();
        visitedPositions.clear();
    }

    private void considerVisiblePositions(Library library) {
        // consider the visible positions
        List<Position> visiblePositions =
                this.visionField.visionField(currentPosition);

        for (Position pos : visiblePositions) {
            // add the supervised positions to the corresponding list
            if (library.isSupervised(pos)) {
                this.supervisedPositions.add(pos);
            }
        }
    }

    private void wearCloak(Library library) {
        // wear the clock if found
        if (library.isCloak(currentPosition) && !isWearingClock) {
            this.isWearingClock = true;
            // decrease the supervision zone in the library
            library.decreaseSupervision();
            // clearing the visitedPositions avoids losing when
            // the cloak is the last cell visited on the map
            this.visitedPositions.clear();
            // at certain situations we can find the book / exit only wearing the cloak
            this.supervisedPositions.clear();
        }
    }

    private boolean[] findBook(int x, int y, Library library) {
        currentPosition = new Position(x, y);
        this.path.add(currentPosition);

        if (library.isSupervised(currentPosition)) {
            return new boolean[] {false, false};
        }

        if (library.isBook(currentPosition)) {
            return new boolean[] {true, true};
        }

        wearCloak(library);

        // mark the current position as Visited by adding it to the corresponding list
        this.visitedPositions.add(currentPosition);


//        library.displayActor(currentPosition);
//        System.out.println("############################");

        considerVisiblePositions(library);
        // consider the positions the actor can enter next
        List<Position> nextPositions = actionField.actionField(currentPosition);

        for (Position pos : nextPositions) {
            // consider only unvisited positions and (possibly) non-supervised positions
            if (!(this.supervisedPositions.contains(pos) || this.visitedPositions.contains(pos)) ) {
                boolean[] results = findBook(pos.x(), pos.y(), library);
                // consider the first possibility where the actor ends up stepping on a supervised position
                if (!results[0]) {return new boolean[] {false, false};
                }
                // consider the possibility where the actor finds the book
                if (results[1]) {
                    return new boolean[] {true, true};
                }
            }
        }

        // if the function loops through all positions without returning, it means that
        // all positions in the action field are either supervised or already visited: dead end
        // such positions should not be considered part of the winning path (if it exists)
        if (!this.path.isEmpty()) {
            path.remove(path.size() - 1);
        }
        return new boolean[] {true, false};
    }

    private boolean[] findExit(Library library, Position startingPosition, Position targetPosition) {
        currentPosition = startingPosition;
        this.path.add(currentPosition);

        if (library.isSupervised(currentPosition)) {
            return new boolean[] {false, false};
        }

        if (currentPosition.equals(targetPosition)) {
            return new boolean[] {true, true};
        }

        // mark the current position as Visited by adding it to the corresponding list
        this.visitedPositions.add(currentPosition);

        // wear the clock if found
        wearCloak(library);

        // consider the visible positions
        considerVisiblePositions(library);

        // consider the positions the actor can enter next
        List<Position> nextPositions = actionField.actionField(currentPosition);

        // sort the position according to their distance to the target position
        nextPositions.sort(Comparator.comparingInt(pos -> MathUtilities.distance(pos, targetPosition)));

        for (Position pos : nextPositions) {
            // consider only unvisited positions and (possibly) non-supervised positions
            if ( !(this.supervisedPositions.contains(pos) || this.visitedPositions.contains(pos)) ) {
                boolean[] results = findExit(library, pos, targetPosition);
                // consider the first possibility where the actor ends up stepping on a supervised position
                if (!results[0]) {
                    return new boolean[] {false, false};
                }
                // consider the possibility where the actor finds the book
                if (results[1]) {
                    return new boolean[] {true, true};
                }
            }
        }

        // if the function loops through all positions without returning, it means that
        // all positions in the action field are either supervised or already visited: dead end
        // such positions should not be considered part of the winning path (if it exists)
        if (!this.path.isEmpty()) {
            path.remove(path.size() - 1);
        }
        return new boolean[] {true, false};
    }
    @Override
    public boolean play(Library library, int startingX, int startingY) {
        initialSettings();
        boolean[] findBook = findBook(startingX, startingY, library);
        if (findBook[1]) {
            // the visited positions should be cleared to make the path
            // as short as possible
            this.visitedPositions.clear();
            return findExit(library, currentPosition, library.getExitPosition())[1];
        }
        return false;
    }

}
