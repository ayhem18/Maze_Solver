package Project.src_1;


import java.util.ArrayList;
import java.util.List;

/**
 * This class represent the base class for all classes providing different vision scenarios
 * Each vision scenario can be modeled as (child of) VisionField class
 * where visionField() method contains the details of the scenario
 */
abstract class VisionField {
    protected int columns;

    protected int rows;

    public VisionField(int columns, int rows) {
        this.rows = rows;
        this.columns = columns;
    }

    public VisionField() {
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

    public abstract List<Position> visionField(Position currentPosition);
    public abstract  String getName() ;

}

/**
 * this class implements the first vision scenario where
 * the actor can perceive all the adjacent cells
 */

class VisionField1 extends VisionField {
    private static final String NAME = "VISION_1";

    public VisionField1(int columns, int rows) {
        super(columns, rows);
    }

    @Override
    public List<Position> visionField(Position currentPosition) {
        ArrayList<Position> positions = new ArrayList<>();
        int currentX = currentPosition.x();
        int currentY = currentPosition.y();

        for (int y = Math.max(0, currentY - 1); y <= Math.min(currentY + 1, rows - 1); y++) {
            for (int x = Math.max(0, currentX - 1); x <= Math.min(currentX + 1, columns - 1); x++){
                if (x != currentX || y != currentY)
                    positions.add(new Position(x, y));
            }
        }
        return positions;
    }
    @Override
    public String getName() {
        return NAME;
    }

}

/**
 * this class implements the second vision scenario
 */

class VisionField2 extends VisionField {

    private static final String NAME = "VISION_2";

    public VisionField2(int columns, int rows) {
        super(columns, rows);
    }

    @Override
    public List<Position> visionField(Position currentPosition) {
        ArrayList<Position> positions = new ArrayList<>();
        int currentX = currentPosition.x();
        int currentY = currentPosition.y();

        for (int y = Math.max(0, currentY - 2); y <= Math.min(currentY + 2, rows - 1); y++) {
            for (int x = Math.max(0, currentX - 2); x <= Math.min(currentX + 2, columns - 1); x++){
                boolean xDis = Math.abs(x - currentX) == 2;
                boolean yDis = Math.abs(y - currentY) == 2;
                if ((xDis || yDis) && !(xDis && yDis)) {
                    positions.add(new Position(x, y));
                }
            }
        }
        return positions;
    }
    @Override
    public String getName() {
        return NAME;
    }

}

/**
 * a common interface for visionField factories
 */
interface VisionFieldFactory {
    VisionField createVisionField(int columns, int rows);
}

/**
 * a concrete factory that generates VisionField1 objects
 */
class VisionField1Factory implements VisionFieldFactory {
    @Override
    public VisionField createVisionField(int columns, int rows) {
        return new VisionField1(columns, rows);
    }

}

/**
 * a concrete factory that generates VisionField2 objects
 */
class VisionField2Factory implements VisionFieldFactory {
    @Override
    public VisionField createVisionField(int columns, int rows) {
        return new VisionField2(columns, rows);
    }

}
