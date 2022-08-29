package Project.src_1;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class is a concrete implementation of the Actor class using the greedy search approach
 */
class GreedyActor extends Actor {

    // The search algorithm's name

    private static final String NAME = "Greedy Search";
    // static values used in the risk assessment functions
    private static final int  VISITED_OVER_DANGEROUS = 3;

    private  static final int REPEAT_VISIT_LIMIT = 7;
    // this value is added to a cell's penalty for every
    private int DANGEROUS_PENALTY;
    // the penalty assigned to a visited cell
    private int VISITED_PENALTY ;
    // the penalty assigned to a supervised cell

    private int SUPERVISED_PENALTY;
    // this array stores the number of times each cell was visited

    private int[] visitsRecord;
    // set of non-supervised positions

    private Set<Position> safePositions;

    public GreedyActor(VisionField visionField, ActionField actionField) {
        super(visionField, actionField);
    }
    @Override
    String getName() {
        return NAME + ": " + visionField.getName();
    }
    // This method sets the important parameter to their initial states

    // avoiding using data left from previous calls

    @Override
    protected void initialSettings() {
        super.initialSettings();
        this.safePositions = new HashSet<>();
    }
    // set the penalties according to the library's dimensions

    private void setPenalties(Library library) {
        DANGEROUS_PENALTY = (library.getColumns() - 1) / 2 + (library.getRows() - 1) / 2 + 1;
        VISITED_PENALTY = DANGEROUS_PENALTY * VISITED_OVER_DANGEROUS;
        SUPERVISED_PENALTY = REPEAT_VISIT_LIMIT * VISITED_PENALTY;
    }
    // this method sets certain parameters according to the library's dimensions

    private void librarySettings(Library library) {
        // set the visionField according to the library's dimensions
        visionField.setColumns(library.getColumns());
        visionField.setRows(library.getRows());

        // set the actionField according to the library's dimensions
        actionField.setColumns(library.getColumns());
        actionField.setRows(library.getRows());

        // set the penalties
        setPenalties(library);

        // set the visits record
        visitsRecord = new int[library.getColumns() * library.getRows()];
    }

    private void considerCurrentPosition(Library library) {
        // add the current position to the path//

        this.path.add(currentPosition);
        // add the current position to the safe positions
        this.safePositions.add(currentPosition);

        // consider the possibility of having the cloak at this position
        if (library.isCloak(currentPosition) && !isWearingClock) {
            this.isWearingClock = true;
            this.supervisedPositions.clear();
            library.decreaseSupervision();
        }

        // increase the number of times the current position was visited
        visitsRecord[currentPosition.x() + currentPosition.y() * library.getRows()] ++;

//         library.displayActor(currentPosition);
//         System.out.println("############################");
    }

    private List<Pair<Position, Integer>> nextPositionsDangerCosts(Library library) {
        List<Position> visiblePositions = visionField.visionField(currentPosition);

        // scan the vision field and add all the new supervised cells
        for (Position pos: visiblePositions) {
            if (library.isSupervised(pos)) {
                this.supervisedPositions.add(pos);
            }
            else {
                this.safePositions.add(pos);
            }
        }

        List<Position> nextPositions = actionField.actionField(currentPosition);
        List<Pair<Position, Integer>> positionsAndPenalties = new ArrayList<>();
        for (Position pos : nextPositions) {
            int x = pos.x();
            int y = pos.y();
            // initialize the cost with the visiting penalties.
            int cost = visitsRecord[x + library.getRows() * y] * VISITED_PENALTY;

            // if the position is known to be supervised, then assign the SUPERVISED_PENALTY
            if (this.supervisedPositions.contains(pos)) {
                cost = SUPERVISED_PENALTY;
            }
            else {
                // Otherwise, estimate how dangerous the position by calculating
                // the number of supervised positions reachable from this position
                for (Position superVisedPos: this.supervisedPositions) {
                    if (MathUtilities.diagonalDistance(pos, superVisedPos) <= actionField.getMaximumReach()) {
                        cost += DANGEROUS_PENALTY;
                    }
                }
            }
            positionsAndPenalties.add(new Pair<>(pos, cost));
        }
        return positionsAndPenalties;
    }

    private Position bestNextPosition(Library library) {
        List<Pair<Position, Integer>> nextPositionsDangerPenalties =
                nextPositionsDangerCosts(library);

        int minCost = Integer.MAX_VALUE;
        Position bestNextPosition = currentPosition;
        // determine the safest cost
        for (Pair<Position, Integer> pair : nextPositionsDangerPenalties) {
            minCost = Math.min(minCost, pair.getValue());
        }
        int finalCost = minCost;
        nextPositionsDangerPenalties.removeIf(pair -> pair.getValue() != finalCost);

        minCost = Integer.MAX_VALUE;
        for (Pair<Position, Integer> minPair: nextPositionsDangerPenalties) {
            minPair.setValue(minPair.getValue() +
                    MathUtilities.convexFactor(library.getColumns(), library.getRows(), minPair.getKey()) -
                    (safePositions.contains(minPair.getKey()) ? DANGEROUS_PENALTY : 0));
            if (minPair.getValue() <= minCost) {
                minCost = minPair.getValue();
                bestNextPosition = minPair.getKey();
            }
        }

        return bestNextPosition;
    }

    public Position bestNextPosition(Library library, Position targetPosition) {
        List<Pair<Position, Integer>> nextPositionsDangerPenalties =
                nextPositionsDangerCosts(library);

        int minCost = Integer.MAX_VALUE;
        Position bestNextPosition = currentPosition;
        // determine the safest cost
        for (Pair<Position, Integer> pair : nextPositionsDangerPenalties) {
            minCost = Math.min(minCost, pair.getValue());
        }
        int finalCost = minCost;
        nextPositionsDangerPenalties.removeIf(pair -> pair.getValue() != finalCost);

        minCost = Integer.MAX_VALUE;
        for (Pair<Position, Integer> minPair: nextPositionsDangerPenalties) {
            minPair.setValue(minPair.getValue() +
                    2 + MathUtilities.distance(minPair.getKey(), targetPosition)
                    - MathUtilities.distance(currentPosition, targetPosition) -
                    (safePositions.contains(minPair.getKey()) ? DANGEROUS_PENALTY : 0));
            if (minPair.getValue() <= minCost) {
                minCost = minPair.getValue();
                bestNextPosition = minPair.getKey();
            }
        }

        return bestNextPosition;
    }

    public boolean findBook(Library library, int startingX, int startingY) {
        // set the current Position
        currentPosition = new Position(startingX, startingY);

        while (!(library.isBook(currentPosition) || library.isSupervised(currentPosition))) {
            considerCurrentPosition(library);
            // the best next position is the one with the minimal cost
            currentPosition = bestNextPosition(library);
        }

        return library.isBook(currentPosition);
    }

    public boolean findTarget(Library library, Position targetPosition) {
        // reset the visitRecords
        visitsRecord = new int[library.getRows() * library.getColumns()];

        while (!(currentPosition.equals(targetPosition) || library.isSupervised(currentPosition))) {
            considerCurrentPosition(library);
            currentPosition = bestNextPosition(library, targetPosition);
        }
        return currentPosition.equals(targetPosition);
    }
    @Override
    public boolean play(Library library, int startingX, int startingY) {
        initialSettings();
        librarySettings(library);

        if (findBook(library, startingX, startingY)) {
            return findTarget(library, library.getExitPosition());
        }
        return false;
    }

}
