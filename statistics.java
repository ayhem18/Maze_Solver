package Project.src_1;
/**
 * This class was used to generate and collect the statistical data included in the report
 */

class StatisticalDataGathering {

    public static void main(String[] args) {
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


        compareTwoActors(g1, b1, 20000);
        System.out.println("#############################################################");
        System.out.println();
        System.out.println();

        compareTwoActors(g1, b2, 20000);
        System.out.println("#############################################################");
        System.out.println();
        System.out.println();

        compareTwoActors(g2, b1, 20000);
        System.out.println("#############################################################");
        System.out.println();
        System.out.println();

        compareTwoActors(g2, b2, 20000);
    }

    public static void compareTwoActors(Actor actor1, Actor actor2, int sampleSize) {
        long start, end;
        int solved1not2 = 0;
        int solved2not1 = 0;
        int faster1 = 0, faster2 = 0, shorterPath1 = 0, shorterPath2 = 0;
        int equalTime = 0;
        int equalLength = 0;
        for (int i = 0; i < sampleSize; i++) {
            Library library = LibraryUtilities.generateLibrary(9, 9);
            start = System.currentTimeMillis();
            boolean out1 = actor1.play(library, new Position(0, 8));
            end = System.currentTimeMillis();

            long time1 = end - start;
            library = new Library(library);
            start = System.currentTimeMillis();
            boolean out2 = actor2.play(library, new Position(0, 8));
            end = System.currentTimeMillis();
            long time2 = end - start;

            if (out1 && !out2){
                solved1not2++;
            }
            else if (!out1 && out2) {
                solved2not1++;
            }
            else  if (out1) {
                faster1 += time1 < time2 ? 1 : 0;
                faster2 += time1 > time2 ? 1 : 0;
                shorterPath1 += actor1.getPath().size() < actor2.getPath().size() ? 1 : 0;
                shorterPath2 += actor1.getPath().size() > actor2.getPath().size() ? 1 : 0;
                equalLength += actor1.getPath().size() == actor2.getPath().size() ? 1 : 0;
                equalTime += time1 == time2 ? 1 : 0;
            }
        }
        int total = faster1 + faster2 + equalTime;
        double faster1per = 100 * (double) faster1 / (total);
        double faster2per = 100 * (double) faster2 / (total);
        double equalTimePer = 100 * (double) equalTime / total;

        double equalLengthPer = 100 * (double) equalLength / total;
        double shorter1per = 100 * (double) shorterPath1 / (total);
        double shorter2per = 100 * (double) shorterPath2 / (total);

        double solve1not2per = 100 *(double) solved1not2 / sampleSize;
        double solve2not1per = 100 * (double) solved2not1 / sampleSize;
        double bothSolvePer = 100 * (double) total / sampleSize;

        System.out.println("Comparison between " + actor1.getName() + " and  " + actor2.getName());

        System.out.println("both actors solved " + bothSolvePer + "% of the maps generated");
        System.out.println(String.format("%.2f",solve1not2per) + "% of maps are solved by " + actor1.getName() + " but not by " +
                actor2.getName());
        System.out.println(String.format("%.2f",solve2not1per) + "% of maps are solved by " + actor2.getName() + " but not by " +
                actor1.getName());


        System.out.println(actor1.getName() + " is faster in " + String.format("%.2f",faster1per) + "% of maps");
        System.out.println(actor2.getName() + " is faster in " + String.format("%.2f",faster2per)  + "% of maps");

        System.out.println(actor1.getName() + " found a shorter path in " + String.format("%.2f",shorter1per) + "% of maps");
        System.out.println(actor2.getName() + " found a shorter path in " + String.format("%.2f",shorter2per) + "% of maps");

        System.out.println("the two actors run for the same amount of time in " + String.format("%.2f",equalTimePer) + "% of maps");
        System.out.println("the actors found paths of the same length in " + String.format("%.2f",equalLengthPer) + "% of maps") ;

    }

}
