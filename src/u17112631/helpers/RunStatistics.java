package u17112631.helpers;

public class RunStatistics {

    private double startingFitness;
    private long runDuration;
    private String bestPerformer;
    private double bestFitness;

    public void setStartingFitness(double startingFitness) {
        this.startingFitness = startingFitness;
    }

    public void addRunDuration(long duration) {
        this.runDuration = duration;
    }

    public void addBestPerformer(String heuristicCombination) {
        this.bestPerformer = heuristicCombination;
    }

    public void addBestFitness(double scheduleFitness) {
        this.bestFitness = scheduleFitness;
    }

    public void print() {
        double percentageChange = ((startingFitness - bestFitness)/startingFitness) * 100;
        System.out.println("Best performer: " + bestPerformer);
        System.out.println("Starting performance: " + startingFitness + " - End performance: " + bestFitness + " - Change: " +percentageChange + "% better" );
        System.out.println("Runtime: " + runDuration/1000 + " seconds");
    }
}
