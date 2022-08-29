package Project.src_1;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * This is generic container class. The first element is denoted as key
 * The second is denoted as value
 * @param <K> the type of the key element
 * @param <V> the type of the value element
 */
class Pair<K, V> {
    private K key;
    private V value;

    public Pair(K key, V value) {
        this.key = key;
        this.value = value;
    }

    // getters and setters
    public K getKey() {
        return key;
    }

    public void setKey(K key) {
        this.key = key;
    }

    public V getValue() {
        return value;
    }
    public void setValue(V value) {
        this.value = value;
    }

}

/***
 * This class is a class used to facilitate the communication between the actor and the library objects
 * It stores the x and y coordinates simultaneously
 */
record Position(int x, int y) {

    @Override
    public boolean equals(Object another) {
        if (another instanceof Position)
            return ((Position) another).x() == this.x && ((Position) another).y() == this.y;
        return false;
    }

    public int hashCode() {
        return ((Integer) x).hashCode() + ((Integer) y).hashCode();
    }

    @Override
    public String toString() {
        return "[" + this.x + "," + this.y + "]";
    }

}

/**
 * This class is used to encapsulate the actual map where the actor will search for the book and the exit
 * It offers several methods preventing the actor from interacting directly with the map.
 */
class Library {

    public static final int ACTOR = 1;
    public static final int EMPTY = 0;
    public static final int SUPERVISED = -1;
    public static final int BOOK = 2;
    public static final int CLOAK = 3;

    public static final int EXIT = 4;
    public static final int ARGUS_VISION = 2;
    public static final int NORRIS_VISION = 1;
    // library dimensions
    private final int rows, columns;
    // different actors' positions
    private final Position argusPosition;
    private final Position norrisPosition;
    private final Position bookPosition;
    private final Position cloakPosition;
    private final Position exitPosition;
    // the map
    private final int[][] maze;

    // different constructors for different use-cases
    public Library(int rows, int columns, int[][] maze,
                   Position argusPosition, Position norrisPosition,
                   Position bookPosition, Position cloakPosition,
                   Position exitPosition) {
        this.rows = rows;
        this.columns = columns;
        this.argusPosition = argusPosition;
        this.norrisPosition = norrisPosition;
        this.bookPosition = bookPosition;
        this.cloakPosition = cloakPosition;
        this.exitPosition = exitPosition;
        this.maze = maze;
    }

    public Library(int rows, int columns,
                   Position argusPosition, Position norrisPosition,
                   Position bookPosition, Position cloakPosition,
                   Position exitPosition) {
        this(rows, columns, new int[rows][columns], argusPosition, norrisPosition,
                bookPosition,cloakPosition, exitPosition);
        LibraryUtilities.setSupervisionZone(argusPosition, ARGUS_VISION, this.maze, SUPERVISED);
        LibraryUtilities.setSupervisionZone(norrisPosition, NORRIS_VISION, this.maze, SUPERVISED);
        this.maze[bookPosition.y()][bookPosition.x()] = Library.BOOK;
        this.maze[cloakPosition.y()][cloakPosition.x()] = Library.CLOAK;
        this.maze[exitPosition.y()][exitPosition.x()] = Library.EXIT;
    }

    public Library (Library library) {
        this(library.getRows(), library.getColumns(), library.getArgusPosition(),
                library.getNorrisPosition(), library.getBookPosition(),
                library.getCloakPosition(), library.getExitPosition());
    }

    // getters for different fields
    public int getRows() {
        return rows;
    }

    public int getColumns() {
        return columns;
    }

    public Position getExitPosition() {
        return exitPosition;
    }

    public Position getArgusPosition() {
        return argusPosition;
    }

    public Position getNorrisPosition() {
        return norrisPosition;
    }

    public Position getBookPosition() {
        return bookPosition;
    }

    public Position getCloakPosition() {
        return cloakPosition;
    }

    /**
     * This method is used to set the inspectors' zone to only their original cells
     */
    public void decreaseSupervision() {
        LibraryUtilities.setSupervisionZone(norrisPosition, NORRIS_VISION, this.maze, EMPTY);
        LibraryUtilities.setSupervisionZone(argusPosition, ARGUS_VISION, this.maze, EMPTY);
        this.maze[norrisPosition.y()][norrisPosition.x()] = SUPERVISED;
        this.maze[argusPosition.y()][argusPosition.x()] = SUPERVISED;
    }

    public boolean isSupervised(Position position) {
        return this.maze[position.y()][position.x()] == Library.SUPERVISED;
    }

    public boolean isBook(Position position) {
        return position.equals(bookPosition);
    }

    public boolean isCloak(Position position) {
        return position.equals(cloakPosition);
    }

    /**
     * this method display the map to the console
     */
    public void display() {
        DisplayUtilities.display2DArray(this.maze, this.rows, this.columns, 2);
    }

    /**
     * this method will display the map assuming an actor is at the passed position
     * @param position the position occupied by an actor
     */
    public void displayActor(Position position) {
        if (isCloak(position)) {
            decreaseSupervision();
        }
        int oldValue = this.maze[position.y()][position.x()];
        this.maze[position.y()][position.x()] = Library.ACTOR;
        display();
        this.maze[position.y()][position.x()] = oldValue == Library.EXIT ? oldValue: Library.EMPTY;
    }

    /**
     * given an actor, this method displays the map with the actor at each position of its current path
     * @param actor the actor exploring the map
     */
    public void displayActorPath(Actor actor) {
        for (Position pos : actor.getPath()) {
            displayActor(pos);
            System.out.println("############################################");
        }
    }

}

/**
 * This class provides different functionalities related to the Library class
 * but should not be included in the Library class itself
 */

class LibraryUtilities {

    private static final Random generator = new Random();
    // generating the library will start with generating Argus's position
    // then Norris's position
    // then the book
    // then the exit
    // then the cloak

    /**
     * this method generates a random library as follows:
     *  Argus's position, then Norris's position, then the book, then the exit and then the cloak
     * @param rows numbers of rows in the produced library
     * @param columns numbers of columns in the produced library
     * @return a library with the specified dimensions respecting the assignment conditions
     */
    public static Library generateLibrary(int rows, int columns) {
        int[][] maze = new int[rows][columns];
        // generate the first supervisor's position
        Position argus = generateArgus(rows, columns, maze);
        // generate the cat
        Position norris = generateNorris(rows, columns, maze);
        // generate the book
        Position book = generateBook(rows, columns, maze, argus, norris);
        // generate the exit
        Position exit = generateExit(rows, columns, maze, argus, norris, book);
        // generate the cloak
        Position cloak = generateCloak(rows, columns, maze, argus, norris);
        return new Library(rows, columns, maze, argus, norris, book, cloak, exit);
    }
    // this method generates Argus position. Taking into consideration that he is
    // the inspector with the widest perception, it is compelling to generate it first

    private static Position generateArgus(int rows, int columns, int[][] maze) {
        Position argusPosition = new Position(generator.nextInt(columns), generator.nextInt(rows));
        setSupervisionZone(argusPosition, Library.ARGUS_VISION, maze, Library.SUPERVISED);
        maze[argusPosition.y()][argusPosition.x()] = Library.SUPERVISED;
        return argusPosition;
    }
    // this method generates the cat's position with no constraint

    private static Position generateNorris(int rows, int columns, int[][] maze) {
        // generate the cat
        Position norrisPosition = new Position(generator.nextInt(columns), generator.nextInt(rows));
        setSupervisionZone(norrisPosition, Library.NORRIS_VISION, maze, Library.SUPERVISED);
        maze[norrisPosition.y()][norrisPosition.x()] = Library.SUPERVISED;
        return norrisPosition;
    }
    // this method generates the book's position in such a way that it is
    // not located the inspectors' perception

    private static Position generateBook(int rows, int columns, int[][] maze, Position argus, Position norris) {
        Position book = null ;
        boolean isSupervised = true;

        while (isSupervised) {
            book = new Position(generator.nextInt(columns), generator.nextInt(rows));
            isSupervised = positionWithinPosition(book, argus, Library.ARGUS_VISION) ||
                    positionWithinPosition(book, norris, Library.NORRIS_VISION);
        }
        // set the book on the maze
        maze[book.y()][book.x()] = Library.BOOK;
        return book;
    }
    // the exit is generated under the constraint that it cannot be in the same position as the book
    // and out of the inspectors' supervision zone

    private static Position generateExit(int rows, int columns, int[][] maze,
                                         Position argus, Position norris, Position book) {
        Position exit = null;
        boolean isValid = false;
        while (!isValid) {
            exit = new Position(generator.nextInt(columns), generator.nextInt(rows));
            isValid = ! (exit.equals(book) || positionWithinPosition(exit, norris, Library.NORRIS_VISION) ||
                    positionWithinPosition(exit, argus, Library.ARGUS_VISION));
        }
        maze[exit.y()][exit.x()] = Library.EXIT;
        return exit;
    }

    private static Position generateCloak(int rows, int columns, int[][] maze,
                                          Position argus, Position norris) {
        Position cloak = null;
        boolean isSupervised = true;
        while (isSupervised) {
            cloak = new Position(generator.nextInt(columns), generator.nextInt(rows));
            isSupervised = positionWithinPosition(cloak, norris, Library.NORRIS_VISION) ||
                    positionWithinPosition(cloak, argus, Library.ARGUS_VISION);
        }
        maze[cloak.y()][cloak.x()] = Library.CLOAK;
        return cloak;
    }

    // this method converts a position to and from the position where it is actually stored in memory
    public static Position convert(Position position, int rows) {
        return new Position(position.x(), rows - 1 - position.y());
    }

    // return whether firstPos can be seen from secondPos given with a vision range "vision"
    private static boolean positionWithinPosition(Position firstPos, Position secondPos, int vision) {
        return MathUtilities.diagonalDistance(firstPos, secondPos) <= vision;
    }

    public static void setSupervisionZone(Position pos, int vision, int[][] maze, int value) {
        int rows = maze.length;
        int columns = maze[0].length;
        int x = pos.x();
        int y = pos.y();
        for (int x1 = Math.max(0, x - vision); x1 <= Math.min(columns - 1, x + vision); x1++) {
            for (int y1 = Math.max(0, y - vision); y1 <= Math.min(rows - 1, y + vision); y1++) {
                maze[y1][x1] = value;
            }
        }
    }
    // the input string should be of the from [x,y] [x,y] [x,y] [x,y] [x,y] [x,y]

    /**
     * this method generates a library through input.
     * The input is checked against the assignment's description
     * @return a library that respects the assignment's conditions
     */
    public static List<Position> inputLibrary() {
        Scanner input = new Scanner(System.in);
        String[] stringPositions;
        List<Position> positions = new ArrayList<>();
        boolean isValidInput = false;
        while (!isValidInput) {
            try {
                System.out.println("Please enter the coordinates according to the following pattern:");
                System.out.println("[x,y] [x,y] [x,y] [x,y] [x,y] [x,y]");

                positions.clear();
                stringPositions = input.nextLine().split(" ");
                // no further processing with the wrong number of arguments
                if (stringPositions.length != 6) {
                    System.out.println("PLEASE MAKE SURE TO ENTER 6 positions with integer values");
                    continue;
                }
                for (String strPos : stringPositions) {
                    String[] coordinates = strPos.substring(1, strPos.length() - 1).split(",");
                    int x = Integer.parseInt(coordinates[0]);
                    int y = Integer.parseInt(coordinates[1]);
                    positions.add(new Position(x, y));
                }
                // check if the book is in the inspector's zone
                if (positionWithinPosition(positions.get(3), positions.get(1), Library.ARGUS_VISION) ||
                        positionWithinPosition(positions.get(3), positions.get(2), Library.NORRIS_VISION)) {
                    System.out.println("PLEASE MAKE SURE THE BOOK IS OUT OF THE INSPECTORS' ZONE");
                    continue;
                }
                if (positionWithinPosition(positions.get(4), positions.get(1), Library.ARGUS_VISION) ||
                        positionWithinPosition(positions.get(4), positions.get(2), Library.NORRIS_VISION)) {
                    System.out.println("PLEASE MAKE SURE THE VISIBILITY CLOAK IS OUT OF THE INSPECTORS' ZONE");
                    continue;
                }


                if (positionWithinPosition(positions.get(5), positions.get(1), Library.ARGUS_VISION) ||
                        positionWithinPosition(positions.get(5), positions.get(2), Library.NORRIS_VISION)) {
                    System.out.println("PLEASE MAKE SURE THE EXIT IS OUT OF THE INSPECTORS' ZONE");
                    continue;
                }

                if (positions.get(3).equals(positions.get(5))) {
                    System.out.println("PLEASE MAKE SURE THE BOOK AND THE EXIT ARE IN DIFFERENT CELLS");
                    continue;
                }
                boolean inBounds = true;
                for (Position pos : positions) {
                    if ((pos.x() > 8 || pos.x() < 0 || pos.y() > 8 || pos.y() < 0)) {
                        System.out.println("Please make sure that all positions entered belong to the interval (0, 8)");
                        inBounds = false;
                        break;
                    }
                }
                isValidInput = inBounds;
            } catch(Exception e) {}
        }
        return positions;
    }

}

/**
 * This class is used to store the functionalities used for a better visual display on the console
 */
class DisplayUtilities {

    public static void display2DArray(int[][] array, int rows, int columns, int maxLength) {
        for (int y1 = 0; y1 < rows; y1++) {
            for (int x1 = 0; x1 < columns - 1; x1++) {
                String s = String.valueOf(array[y1][x1]);
                for (int i = 0; i < maxLength - s.length(); i++)
                    System.out.print(" ");
                System.out.print(s + " ");
            }
            String s = String.valueOf(array[y1][columns - 1]);
            for (int i = 0; i < maxLength - s.length(); i++)
                System.out.print(" ");
            System.out.println(array[y1][columns - 1]);
        }
    }
    public static <T> void displayList(List<T> list, int elementsPerLine) {
        int size = list.size();
        for (int i = 0; i < size; i++) {
            if (i % elementsPerLine == elementsPerLine - 1) {
                System.out.println(list.get(i));
            }
            else {
                System.out.print(list.get(i) + " ");
            }
        }
    }
}

class MathUtilities {

    public static int diagonalDistance(Position position1, Position position2) {
        return Math.max(Math.abs(position1.x() - position2.x()),
                Math.abs(position1.y() - position2.y()));
    }
    public static int distance(Position position1, Position position2) {
        return Math.abs(position1.x() - position2.x()) +
                Math.abs(position1.y() - position2.y());
    }
    public static int convexFactor(int columns, int rows, Position position) {
        int x = position.x();
        int y = position.y();
        return Math.min(columns - x - 1, x) + Math.min(rows - 1 -y, y);
    }
    public static int signFunction(Number number) {
        if (number.doubleValue() > 0)
            return 1;
        if (number.doubleValue() == 0)
            return 0;
        return -1;
    }

    public static double calculateSum(List<Number> numbers) {
        double sum = 0.0;
        for (Number number : numbers) {
            sum += number.doubleValue();
        }
        return sum;
    }

    public static double calculateMean(List<Number> numbers) {
        return numbers.isEmpty() ? 0 : calculateSum(numbers) / numbers.size();
    }

    public static double calculateUnbiasedVariance(List<Number> numbers) {
        double mean = calculateMean(numbers);
        double variance = 0;
        for (Number number: numbers) {
            variance += Math.pow(mean - number.doubleValue(), 2);
        }
        return numbers.size() > 1 ? variance / (numbers.size() - 1) : 0;
    }

    public static double calculateMedian(List<Number> numbers) {
        numbers.sort((n1, n2) -> signFunction(n1.doubleValue() - n2.doubleValue()));
        return numbers.get(numbers.size() / 2).doubleValue();
    }

    public static double calculateMode(List<Number> numbers) {
        numbers.sort((n1, n2) -> signFunction(n1.doubleValue() - n2.doubleValue()));
        int max_occurrence = 0;
        int current_occurrence = 0;
        double mode = numbers.get(0).doubleValue();
        double last = mode;

        for (Number number: numbers) {
            if (number.doubleValue() != last) {
                if (current_occurrence > max_occurrence) {
                    mode = last;
                    max_occurrence = current_occurrence;
                }
                last = number.doubleValue();
                current_occurrence = 1;
            }
            else {
                current_occurrence ++;
            }
        }

        if (current_occurrence > max_occurrence) {
            mode = last;
        }

        return mode;
    }
    public static void descriptiveStatistics(List<Number> numbers) {
        System.out.println("Mean :" + MathUtilities.calculateMean(numbers));
        System.out.println();

        System.out.println("Variance: " + MathUtilities.calculateUnbiasedVariance(numbers));
        System.out.println();

        System.out.println("Median: " + MathUtilities.calculateMedian(numbers));
        System.out.println();

        System.out.println("Mode: " + MathUtilities.calculateMode(numbers));
        System.out.println();
    }

}
