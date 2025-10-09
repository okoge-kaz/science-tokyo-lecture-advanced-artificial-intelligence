package report03;

import java.io.IOException;
import jgoal.ga.TSRexNJgg;
import jgoal.solution.ICSolution.Status;
import jgoal.solution.TCSolutionSet;
import jgoal.solution.TSRealSolution;
import jssf.log.TCTable;
import jssf.math.TCMatrix;
import jssf.random.ICRandom;
import jssf.random.TCJava48BitLcg;

/**
 * This program executes REX/JGG three trials, changing a random seed. It saves a transition of the
 * best evaluation value at each generation in a log file. The log file is a CSV (comma separated
 * values) file. The experimental settings is as follows. - Benchmark function: k-tablet (k=n/4). -
 * Dimension: n=20. - Initial region: [-5,+5]^n， - Population size: 14n - The number of offspring:
 * 5n - The maximum number of evaluation: 4n x 1e4 - The evaluation value for termination: 1.0 x
 * 1e-7 - Log filename: RexJggKTabletP14K5.csv
 *
 * @author isao
 */
public class TSRexNJggM {

  /**
   * This method initializes the population.
   *
   * @param population the population.
   * @param min the minimum value of the initialization area.
   * @param max the maximum value of the initialization area.
   * @param random the random generator.
   */
  private static void initializePopulation(
      TCSolutionSet<TSRealSolution> population, double min, double max, ICRandom random) {
    for (TSRealSolution s : population) {
      s.getVector()
          .rand(random)
          .times(max - min)
          .add(
              min); // Initializes the coordinates of the individual with random numbers with the
                    // range of [min, max]^n．
    }
  }

  /**
   * This method evaluates all the individuals in the population.
   *
   * @param population the population
   */
  private static void evaluate(TCSolutionSet<TSRealSolution> population) {
    for (TSRealSolution s : population) {
      double eval = ktablet(s.getVector()); // Obtains an evaluation value of k-tablet function.
      s.setEvaluationValue(eval); // Sets the evaluation value to the individual.
      s.setStatus(Status.FEASIBLE); // Sets the status of the individual to "feasible".
    }
  }

  /**
   * k-tablet function (k=n/4)
   *
   * @param x n-dimensional real-valued vector
   */
  private static double ktablet(TCMatrix x) {
    int k = (int) ((double) x.getDimension() / 4.0); // k=n/4
    double result = 0.0; // Initializes the result evaluation value as zero.
    for (int i = 0; i < x.getDimension(); ++i) {
      double xi = x.getValue(i); // i-th element of x.
      if (i < k) {
        result += xi * xi;
      } else {
        result += 10000.0 * xi * xi;
      }
    }
    return result;
  }

  /**
   * This method outputs the best evaluation value to the log table.
   *
   * @param log the log table
   * @param trialName the trial name, which is used for the label of the log table.
   * @param trialNo the trial number, which is used for the label of the log table.
   * @param index index of line number.
   * @param noOfEvals the number of evaluation.
   * @param bestEvaluationValue the best evaluation value.
   */
  private static void putLogData(
      TCTable log,
      String trialName,
      int trialNo,
      int index,
      long noOfEvals,
      double bestEvaluationValue) {
    log.putData(index, "NoOfEvals", noOfEvals);
    log.putData(index, trialName + "_" + trialNo, bestEvaluationValue);
  }

  /**
   * This method executes one trial.
   *
   * @param ga GA
   * @param maxEvals Maximum number of evaluation for termination.
   * @param log log table
   * @param trialName trial name, which is used for the label of the log table.
   * @param trialNo trial number, which is used for the label of the log table.
   */
  private static void executeOneTrial(
      TSRexNJgg ga, long maxEvals, TCTable log, String trialName, int trialNo) {
    long noOfEvals = 0; // Initializes the number of evaluation.
    double best =
        ga.getBestEvaluationValue(); // Obtains the best evaluation value in the population.
    int logIndex = 0; // Initializes the line index of the log table.
    putLogData(
        log, trialName, trialNo, logIndex, noOfEvals,
        best); // Save the information of the initial population in the log table.
    ++logIndex; // Increments the line index of the log table.
    int loopCount = 0; // Initializes the loop counter.
    while (best > 1e-7
        && noOfEvals
            < maxEvals) { // the termination condition : the best evaluation value is smaller than
                          // 10^-7 or the number of evaluation is larger than the maximum number of
                          // evaluation.
      TCSolutionSet<TSRealSolution> offspring = ga.makeOffspring(); // Generates offspring.
      evaluate(offspring); // Evaluates the offspring.
      noOfEvals += offspring.size(); // Updates the number of evaluation.
      ga.nextGeneration(); // Executes survival selection.
      best = ga.getBestEvaluationValue(); // Obtains the best evaluation value in the population.
      if (loopCount % 10
          == 0) { // Saves the best evaluation value in the log table at every 10 steps.
        putLogData(log, trialName, trialNo, logIndex, noOfEvals, best);
        ++logIndex; // Increment the line number of the log table.
      }
      ++loopCount; // Increment the loop counter.
    }
    putLogData(
        log, trialName, trialNo, logIndex, noOfEvals,
        best); // Save the information of the last population in the log table.
    System.out.println(
        "TrialNo:"
            + trialNo
            + ", NoOfEvals:"
            + noOfEvals
            + ", Best:"
            + best); // Displays the trial number, the number of evaluation and the best evaluation
                     // value.
  }

  /**
   * Main method.
   *
   * @param args none
   */
  public static void main(String[] args) throws IOException {
    boolean minimization = true; // Minimizes the objective function.
    int dimension = 20; // Dimension
    int populationSize = 14 * dimension; // The population size
    int noOfKids = 5 * dimension; // The number of offspring
    double min = -5.00; // The minimum value of the initialization area.
    double max = +5.00; // The maximum value of the initialization area.
    long maxEvals = (long) (4 * dimension * 1e4); // The maximum number of evaluation
    int maxTrials = 3; // The number of trials
    String trialName = "RexJggKTabletP14K5"; // The trial name, which is used for the log filename.
    String logFilename = "output/report03/" + trialName + ".csv"; // The log filename.

    ICRandom random = new TCJava48BitLcg(); // Random generator.
    TSRexNJgg ga =
        new TSRexNJgg(minimization, dimension, populationSize, noOfKids, random); // REX/JGG
    TCTable log = new TCTable(); // The log table.
    for (int trial = 0; trial < maxTrials; ++trial) {
      TCSolutionSet<TSRealSolution> population = ga.initialize(); // Obtains a population.
      initializePopulation(population, min, max, random); // Initializes the population.
      evaluate(population); // Evaluates the population.
      executeOneTrial(ga, maxEvals, log, trialName, trial); // Execute one trial.
    }
    log.writeTo(
        logFilename); // Output the log table containing the results of the three trials to the log
                      // file.
  }
}
