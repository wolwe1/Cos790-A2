package u17112631.link;

//
//  This is a replica of the implementation of EvoHyp but I had to change the
//  function that generates heuristic combinations from one that uses a random number to
//  one that uses a fixed size heuristic
//


import initialsoln.InitialSoln;
import problemdomain.ProblemDomain;

import java.io.*;
import java.util.Random;

public class CustomGenAlg {
    private int populationSize;
    private int tournamentSize;
    private int noOfGenerations;
    private double mutationRate;
    private double crossoverRate;
    private double reproductionRate;
    private int mutationLength;
    private int initialMaxLength;
    private int offspringMaxLength;
    private boolean allowDuplicates;
    private String heuristics;
    private ProblemDomain problem;
    private final Random ranGen;
    private InitialSoln[] population;
    private boolean print;

    public CustomGenAlg(long seed, String heuristics) {
        this.heuristics = heuristics;
        this.ranGen = new Random(seed);
        this.print = true;
    }

    public void setParameters(String parameterFile) {
        try {
            File f = new File(parameterFile);
            FileInputStream f1 = new FileInputStream(f);
            BufferedReader df = new BufferedReader(new InputStreamReader(f1));
            this.populationSize = (new Double(df.readLine())).intValue();
            this.tournamentSize = (new Double(df.readLine())).intValue();
            this.noOfGenerations = (new Double(df.readLine())).intValue();
            this.mutationRate = new Double(df.readLine());
            this.crossoverRate = new Double(df.readLine());
            this.initialMaxLength = (new Double(df.readLine())).intValue();
            this.offspringMaxLength = (new Double(df.readLine())).intValue();
            this.mutationLength = (new Double(df.readLine())).intValue();
        } catch (IOException var5) {
            System.out.println("The file " + parameterFile + " cannot be found. " + "Please check the details provided.");
        }

    }

    public void setPopulationSize(int populationSize) {
        this.populationSize = populationSize;
    }

    public void setTournamentSize(int tournamentSize) {
        this.tournamentSize = tournamentSize;
    }

    public void setNoOfGenerations(int noOfGenerations) {
        this.noOfGenerations = noOfGenerations;
    }

    public void setMutationRate(double mutationRate) {
        this.mutationRate = mutationRate;
    }

    public void setCrossoverRate(double crossoverRate) {
        this.crossoverRate = crossoverRate;
    }

    public void setInitialMaxLength(int initialMaxLength) {
        this.initialMaxLength = initialMaxLength;
    }

    public void setOffspringMaxLength(int offspringMaxLength) {
        this.offspringMaxLength = offspringMaxLength;
    }

    public void setMutationLength(int mutationLength) {
        this.mutationLength = mutationLength;
    }

    public void setAllowDuplicates(boolean allowDuplicates) {
        this.allowDuplicates = allowDuplicates;
    }

    public void setPrint(boolean print) {
        this.print = print;
    }

    public int getPopulationSize() {
        return this.populationSize;
    }

    public int getTournamentSize() {
        return this.tournamentSize;
    }

    public int getnoOfGenerations() {
        return this.noOfGenerations;
    }

    public double getMutationRate() {
        return this.mutationRate;
    }

    public double getCrossoverRate() {
        return this.crossoverRate;
    }

    public double getReproductionRate() {
        return this.reproductionRate;
    }

    public int getInitialMaxLength() {
        return this.initialMaxLength;
    }

    public int getOffspringMaxLength() {
        return this.offspringMaxLength;
    }

    public int getMutationLength() {
        return this.mutationLength;
    }

    public boolean getAllowDuplicates() {
        return this.allowDuplicates;
    }

    public boolean getPrint() {
        return this.print;
    }

    public void setHeuristics(String heuristics) {
        this.heuristics = heuristics;
    }

    public void setProblem(ProblemDomain problem) {
        this.problem = problem;
    }

    private boolean exists(String heuComb, int pos) {
        for(int count = 0; count < pos; ++count) {
            if (heuComb.compareTo(this.population[count].getHeuCom()) == 0) {
                return true;
            }
        }

        return false;
    }

    private String createComb() {
        StringBuilder heuComb = new StringBuilder();
        int length = this.initialMaxLength;

        for(int count = 1; count <= length; ++count) {
            heuComb.append(this.heuristics.charAt(this.ranGen.nextInt(this.heuristics.length())));
        }

        return heuComb.toString();
    }

    private InitialSoln createPopulation() {
        InitialSoln best = null;
        this.population = new InitialSoln[this.populationSize];

        for(int count = 0; count < this.populationSize; ++count) {
            String ind;
            if (!this.allowDuplicates) {
                do {
                    ind = this.createComb();
                } while(this.exists(ind, count));
            } else {
                ind = this.createComb();
            }

            this.population[count] = this.evaluate(ind);
            this.population[count].setHeuCom(ind);
            if (count == 0) {
                best = this.population[count];
            } else if (this.population[count].fitter(best) == 1) {
                best = this.population[count];
            }

            ind = null;
        }

        return best;
    }

    private void displayPopulation() {
        System.out.println("Population");

        for (InitialSoln initialSoln : this.population) {
            System.out.println(initialSoln.getHeuCom() + " " + initialSoln.getFitness());
        }

    }

    private InitialSoln evaluate(String ind) {
        return this.problem.evaluate(ind);
    }

    private InitialSoln selection() {
        InitialSoln winner = this.population[this.ranGen.nextInt(this.populationSize)];

        for(int count = 2; count <= this.tournamentSize; ++count) {
            InitialSoln current = this.population[this.ranGen.nextInt(this.populationSize)];
            if (current.fitter(winner) == 1) {
                winner = current;
            }
        }

        return winner;
    }

    //Original crossover used random fragment size which could lead to heuristic combinations that are undersized
    //This version ensures equal characters are transferred
    private InitialSoln crossover(InitialSoln parent1, InitialSoln parent2) {
        String p1 = parent1.getHeuCom();
        String p2 = parent2.getHeuCom();
        int point1 = this.ranGen.nextInt(p1.length());
        String frag11 = p1.substring(0, point1);
        String frag12 = p1.substring(point1, p1.length());
        String frag21 = p2.substring(0, point1);
        String frag22 = p2.substring(point1, p2.length());
        String os1 = new String(frag11 + frag22);
        String os2 = new String(frag21 + frag12);
        if (this.offspringMaxLength > 0 && os1.length() > this.offspringMaxLength) {
            os1 = os1.substring(0, this.offspringMaxLength);
        }

        if (this.offspringMaxLength > 0 && os2.length() > this.offspringMaxLength) {
            os2 = os2.substring(0, this.offspringMaxLength);
        }

        InitialSoln offspring1 = this.evaluate(os1);
        offspring1.setHeuCom(os1);
        InitialSoln offspring2 = this.evaluate(os2);
        offspring2.setHeuCom(os2);
        return offspring1.fitter(offspring2) == 1 ? offspring1 : offspring2;
    }

    private String createStr(int limit) {
        StringBuilder str = new StringBuilder();

        for(int count = 0; count < limit; ++count) {
            str.append(this.heuristics.charAt(this.ranGen.nextInt(this.heuristics.length())));
        }

        return str.toString();
    }

    private InitialSoln mutation(InitialSoln parent) {
        String com = parent.getHeuCom();
        int mpoint = this.ranGen.nextInt(com.length());
        int len = this.ranGen.nextInt(this.mutationLength) + 1;
        String hh = this.createStr(len);
        String begin = com.substring(0, mpoint);
        String end = com.substring(mpoint + 1, com.length());
        String tem = begin + hh + end;
        if (tem.length() > 0 && tem.length() > this.offspringMaxLength) {
            tem = tem.substring(0, this.offspringMaxLength);
        }
        InitialSoln offspring = this.evaluate(tem);
        offspring.setHeuCom(tem);
        return offspring;
    }

    private InitialSoln regenerate(InitialSoln bestInd) {
        int minds = (int)(this.mutationRate * (double)this.populationSize);
        int cinds = (int)(this.crossoverRate * (double)this.populationSize);
        if (this.mutationRate + this.crossoverRate < 1.0D) {
            this.reproductionRate = 1.0D - (this.mutationRate + this.crossoverRate);
        }

        int rinds = (int)(this.reproductionRate * (double)this.populationSize);
        if (minds + cinds + rinds != this.population.length) {
            if (cinds != 0) {
                cinds += this.population.length - (minds + cinds + rinds);
            } else if (minds != 0) {
                minds += this.population.length - (minds + cinds + rinds);
            }
        }

        InitialSoln best = bestInd;
        int index = 0;
        InitialSoln[] newPop = new InitialSoln[this.populationSize];

        int ccount;
        for(ccount = 1; ccount <= rinds; ++ccount) {
            newPop[index++] = this.selection();
            if (newPop[index - 1].fitter(best) == 1) {
                best = newPop[index - 1];
            }
        }

        for(ccount = 1; ccount <= minds; ++ccount) {
            newPop[index++] = this.mutation(this.selection());
            if (newPop[index - 1].fitter(best) == 1) {
                best = newPop[index - 1];
            }
        }

        for(ccount = 1; ccount <= cinds; ++ccount) {
            newPop[index++] = this.crossover(this.selection(), this.selection());
            if (newPop[index - 1].fitter(best) == 1) {
                best = newPop[index - 1];
            }
        }

        this.population = newPop;
        newPop = null;
        return best;
    }

    public InitialSoln evolve() {
        if (this.print) {
            System.out.println("Generation 0");
        }

        InitialSoln best = this.createPopulation();
        if (this.print) {
            System.out.println("Best Fitness: " + best.getFitness());
            System.out.println("Heuristic Combination: " + best.getHeuCom());
            System.out.println();
        }

        for(int count = 1; count <= this.noOfGenerations; ++count) {
            if (this.print) {
                System.out.println("Generation " + count);
            }

            InitialSoln ind = this.regenerate(best);
            if (ind.fitter(best) == 1) {
                best = ind;
            }

            if (this.print) {
                System.out.println("Generation Best Fitness: " + ind.getFitness());
                System.out.println("Generation Best Heuristic Combination: " + ind.getHeuCom());
                System.out.println("Overall Best Fitness: " + best.getFitness());
                System.out.println("Overall Best Heuristic Combination: " + best.getHeuCom());
                System.out.println();
            }
        }

        System.out.println("Completed evolving heuristic combination");
        return best;
    }
}