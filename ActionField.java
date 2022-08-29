package Project.src_1;

import java.util.ArrayList;
import java.util.List;

/**
 *  This class represent the base class for all classes providing different action scenarios
 *  an action scenario represents the set of cells the actor can step on in the next move
 *  This implementation ensures that the code can work with other action scenarios in addition
 *  to the one indicated in the assignment
 */
abstract class ActionField {
    protected int columns;

    protected int rows;

    public ActionField(int columns, int rows) {
        this.rows = rows;
        this.columns = columns;
    }

    public ActionField() {
        this(0, 0);
    }

    public int getColumns() {
        return columns;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        this.rows = rows;
    }

    abstract List<Position> actionField(Position currentPosition);
    public abstract int getMaximumReach();

}

/**
 * this class implements the action scenario indicated by the assignment
 * where the actor can move to any of the adjacent cells
 */

class ActionField1 extends ActionField {


    public ActionField1(int columns, int rows) {
        super(columns, rows);
    }

    @Override
    List<Position> actionField(Position currentPosition) {
        int currentY = currentPosition.y();
        int currentX = currentPosition.x();
        ArrayList<Position> positions = new ArrayList<>();

        for (int y = Math.max(0, currentY - 1); y <= Math.min(currentY + 1, rows - 1); y++) {
            for (int x = Math.max(0, currentX - 1); x <= Math.min(currentX + 1, columns - 1); x++){
                if (x != currentX || y != currentY)
                    positions.add(new Position(x, y));
            }
        }
        return positions;
    }
    @Override
    public int getMaximumReach() {
        return 1;
    }

}

/**
 * a common interface for all ActionField factories
 */
interface ActionFieldFactory {
    ActionField createActionField(int columns, int rows);
}

/**
 * this class implements the action scenario indicated by the assignment
 *  where the actor can move to any of the adjacent cells
 */

class ActionField1Factory implements ActionFieldFactory {
    @Override
    public ActionField createActionField(int columns, int rows) {
        return new ActionField1(columns, rows);
    }

}

