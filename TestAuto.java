package Project.src_1;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * This class can be used to generate a large number of random libraries and test each actor
 * and display the maps it cannot solve
 */
class TestAuto {
    private static final List<Library> deathList = new ArrayList<>();
    public static void testAuto() {
        VisionFieldFactory f1 = new VisionField1Factory();
        VisionField v1 = f1.createVisionField(9, 9);

        f1 = new VisionField2Factory();
        VisionField v2 = f1.createVisionField(9, 9);

        ActionFieldFactory a = new ActionField1Factory();
        ActionField action1 = a.createActionField(9, 9);

        GreedyActor g1 = new GreedyActor(v1, action1);
        GreedyActor g2 = new GreedyActor(v2, action1);
        BacktrackingActor b1 = new BacktrackingActor(v1, action1);
        BacktrackingActor b2 = new BacktrackingActor(v2, action1);

        System.out.println("TEST " + g1.getName());
        testActor(g1);
        System.out.println();
        System.out.println();

        if (! proceed(g1)) return ;

        System.out.println("TEST " + g2.getName());
        testActor(g2);
        System.out.println();
        System.out.println();


        if (! proceed(g2)) return ;

        System.out.println("TEST " + b1.getName());
        testActor(b1);
        System.out.println();
        System.out.println();


        if (! proceed(b1)) return ;

        System.out.println("TEST " + b2.getName());
        testActor(b2);
        System.out.println();
        System.out.println();

    }
    private static boolean proceed(Actor g){
        System.out.println("This was the performance of actor " + g.getName()
                + " Would you like to proceed with other actors ? Please enter 1");
        return (new Scanner(System.in)).nextLine().equals("1");
    }
    private static void testActor(Actor actor) {
        deathList.clear();
        // the value can be changed according to the user's needs
        for (int i = 0; i < 40000; i++) {
            Library library = LibraryUtilities.generateLibrary(9, 9);

            boolean result = actor.play(library, 0, 8);
            if (!result) {
                deathList.add(library);
            }
        }

        for (Library lib : deathList) {
            if (!lib.isSupervised(new Position(0, 8))){
                System.out.println("#################################################################");
                (new Library(lib)).display();
                System.out.println();
            }
        }
    }
}
