package Project.src_1;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * This class implements the main input and output
 */

public class Main {

    public static void main(String [] args) {
        System.out.println("Would you rather enter your own map, or test the actors' power by brute force ?");
        System.out.println("Please press 1 to enter your own map, any key otherwise");
        String choice = (new Scanner(System.in)).nextLine();
        if (choice.equals("1")) {
            userInput();
        }
        else {
            TestAuto.testAuto();
        }
    }

    public static void userInput() {
        List<Position> positions = LibraryUtilities.inputLibrary().stream().map(
                (position -> LibraryUtilities.convert(position, 9))).toList();
        Library library = new Library(9, 9, positions.get(1), positions.get(2),
                positions.get(3), positions.get(4), positions.get(5));

        VisionFieldFactory visionFieldFactory;
        VisionField vision;

        ActionFieldFactory a = new ActionField1Factory();
        ActionField action = a.createActionField(9, 9);

        int scenario = getVisionScenario();
        if (scenario == 1) {
            visionFieldFactory = new VisionField1Factory();
        }
        else {
            visionFieldFactory = new VisionField2Factory();
        }
        vision = visionFieldFactory.createVisionField(9, 9);

        GreedyActor greedyActor = new GreedyActor(vision, action);
        BacktrackingActor backtrackingActor = new BacktrackingActor(vision, action);

        output(library, greedyActor, positions.get(0));
        System.out.println("##################################################");
        System.out.println("##################################################");
        output(library, backtrackingActor, positions.get(0));
    }

    /**
     * this method represents the result of the actor's search in the given library starting
     * from the initial position
     * @param library the library encapsulating the map
     * @param actor the actor object looking for the book in the library
     * @param initialPosition Position object
     */
    private static void output(Library library, Actor actor, Position initialPosition) {
        library = new Library(library);
        System.out.println("Algorithm: " + actor.getName());
        System.out.println();

        long start = System.currentTimeMillis();
        boolean outcome = actor.play(library, initialPosition);
        long end = System.currentTimeMillis();
        System.out.println("Outcome: " + (outcome ? "Win" : "Loss"));
        System.out.println();

        if (outcome) {
            System.out.println("Number of steps: " + actor.path.size());
            // display the path
            System.out.println();

            System.out.println("Would you like to display the positions on a map ? [Y/N]");
            library = new Library(library);
            if ("Y".equals((new Scanner(System.in)).nextLine())) {
                library.displayActorPath(actor);
            }
            else {
                DisplayUtilities.displayList(
                        actor.getPath().stream().map((position -> LibraryUtilities.convert(position, 9))).
                                collect(Collectors.toList()), 8);
            }
            System.out.println();
            System.out.println("Running time: " + (end - start) + " milliseconds");
        }
    }

    /**
     * this method makes sure that the user input one of the valid values: 1 or 2
     * @return values 1 or 2
     */
    public static int getVisionScenario() {
        Scanner input = new Scanner(System.in);
        String s = "1";
        boolean isValid = false;
        while (!isValid) {
            System.out.println("Please enter either 1 or 2");
            s = input.nextLine();
            isValid = s.equals("1") || s.equals("2");
        }
        return Integer.parseInt(s);
    }
}
