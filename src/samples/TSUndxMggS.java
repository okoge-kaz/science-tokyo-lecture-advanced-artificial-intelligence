package samples;

import jgoal.ga.TSUndxMgg;
import jgoal.solution.TCSolutionSet;
import jgoal.solution.TSRealSolution;
import jgoal.solution.ICSolution.Status;
import jssf.math.TCMatrix;
import jssf.random.ICRandom;
import jssf.random.TCJava48BitLcg;

/**
 * This program executes UNDX+MGG one trial.
 * - Benchmark function: k-tablet (k=n/4).
 * - Dimension: n=20.
 * - Initial region: [-5,+5]^n，
 * - Population size: 14n
 * - The number of offspring: 5n
 * - The maximum number of evaluation: 4n x 1e4
 * - The evaluation value for termination: 1.0 x 1e-7
 * 
 * @author isao
 *
 */
public class TSUndxMggS {
	
	/**
	 * This method initializes the population.
	 * @param population the population.
	 * @param min the minimum value of the initialization area.
	 * @param max the maximum value of the initialization area.
	 * @param random the random generator.
	 */
	private static void initializePopulation(TCSolutionSet<TSRealSolution> population, double min, double max, ICRandom random) {
		for (TSRealSolution s: population) {
			s.getVector().rand(random).times(max - min).add(min); //Initializes the coordinates of the individual with random numbers with the range of [min, max]^n．
		}
	}

	/**
	 * This method evaluates all the individuals in the population.
	 * @param population the population
	 */
	private static void evaluate(TCSolutionSet<TSRealSolution> population) {
		for (TSRealSolution s: population) {
			double eval = ktablet(s.getVector()); //Obtains an evaluation value of k-tablet function.
			s.setEvaluationValue(eval); //Sets the evaluation value to the individual.
			s.setStatus(Status.FEASIBLE); //Sets the status of the individual to "feasible".
		}
	}
	
	/**
	 * k-tablet function (k=n/4)
	 * @param x n-dimensional real-valued vector
	 */
	private static double ktablet(TCMatrix x) {
		int k = (int)((double)x.getDimension() /4.0); //k=n/4
		double result = 0.0; //Initializes the result evaluation value as zero.
		for (int i = 0; i < x.getDimension(); ++i) {
			double xi = x.getValue(i); //i-th element of x.
			if (i < k) {
				result += xi * xi;				
			} else {
				result += 10000.0 * xi * xi;				
			}
		}
		return result;
	}
		
	/**
	 * Main method.
	 * @param args none
	 */
	public static void main(String[] args) {
		boolean minimization = true; //Minimizes the objective function.
		int dimension = 20; //Dimension
		int populationSize = 14 * dimension; //The population size
		int noOfKids = 5 * dimension; //The number of offspring
		double min = -5.00; //The minimum value of the initialization area.
		double max = +5.00; //The maximum value of the initialization area.
		long maxEvals = (long)(4 * dimension * 1e4); //The maximum number of evaluation
		ICRandom random = new TCJava48BitLcg(); //Random generator.
		TSUndxMgg ga = new TSUndxMgg(minimization, dimension, populationSize, noOfKids, random); //UNDX+MGG
		
		TCSolutionSet<TSRealSolution> population = ga.initialize(); //Obtains a population.
		initializePopulation(population, min, max, random); //Initializes the population.
		evaluate(population); //Evaluates the population.
		
		int noOfEvals = 0; //Initializes the number of evaluation.
		double best = ga.getBestEvaluationValue(); //Obtains the best evaluation value in the population.
		System.out.println(noOfEvals + " " + best); //Displays the best evaluation value in the population.
		while (best > 1e-7 && noOfEvals < maxEvals) { //the termination condition : the best evaluation value is smaller than 10^-7 or the number of evaluation is larger than the maximum number of evaluation.
			TCSolutionSet<TSRealSolution> offspring = ga.makeOffspring(); //Generates offspring.
			evaluate(offspring); //Evaluates the offspring.
			noOfEvals += offspring.size(); //Updates the number of evaluation.
			ga.nextGeneration(); //Executes survival selection.
			best = ga.getBestEvaluationValue(); //Obtains the best evaluation value in the population.
			System.out.println(noOfEvals + " " + best); //Displays the best evaluation value in the population. 
		}
		
	}

}
