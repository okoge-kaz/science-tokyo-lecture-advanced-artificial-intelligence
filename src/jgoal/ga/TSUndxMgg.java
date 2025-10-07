package jgoal.ga;

import java.io.Serializable;
import java.util.Collections;
import jgoal.ga.reproduction.TCUndx;
import jgoal.ga.reproductionSelection.ICReproductionSelection;
import jgoal.ga.reproductionSelection.TCRandomSelectionWithoutReplacement;
import jgoal.ga.survivalSelection.ICSurvivalSelection;
import jgoal.ga.survivalSelection.TCBestAndRankBasedRouletteSelectionFromFamily;
import jgoal.solution.TCSolutionSet;
import jgoal.solution.TSRealSolution;
import jgoal.solution.comparator.ICComparator;
import jgoal.solution.comparator.TSEvaluationValueComparator;
import jssf.di.ACParam;
import jssf.random.ICRandom;

/**
 * UNDX+MGG
 * 
 * @author isao
 * @param <X>
 */
public class TSUndxMgg implements Serializable {

	private static final long serialVersionUID = 1L;
	
	/** Dimension */
	private int fDimension;
	
	/** The population */
	private TCSolutionSet<TSRealSolution> fPopulation;
	
	/** The population size */
	private int fPopulationSize;
	
	/** The parent set */
	private TCSolutionSet<TSRealSolution> fParents;
	
	/** The offspring set */
	private TCSolutionSet<TSRealSolution> fKids;
	
	/** The number of offspring */
	private int fNoOfKids;

	/** The mating selection operator */
	private ICReproductionSelection<TSRealSolution> fReproductionSelection;

	/** UNDX */
	private TCUndx<TSRealSolution> fUndx;
	
	/** The survival selection operator */
	private ICSurvivalSelection<TSRealSolution> fSurvivalSelection;
	
	/** The random number generator */
	private ICRandom fRandom;
	
	/** The comparator of individuals */
	private ICComparator<TSRealSolution> fComparator;
	
	/** The solution template */
	private TSRealSolution fSolutionTemplate;
	
	/**
	 * Constructor
	 * @param solution the solution template
	 * @param noOfParents the number of parents
	 * @param noOfKids the number of offspring
	 * @param reproductionSelection the mating selection operator
	 * @param reproduction the crossover operator
	 * @param survivalSelection the survival selection operator
	 */
	public TSUndxMgg(
			@ACParam(key="Minimization") boolean minimization,
			@ACParam(key="Dimension") int dimension,
			@ACParam(key="PopulationSize") int populationSize,
			@ACParam(key="NoOfKids") int noOfKids,
			@ACParam(key="random") ICRandom random
	) {
		fDimension = dimension;
		fPopulationSize = populationSize;
		fRandom = random;
		fNoOfKids = noOfKids;
		fSolutionTemplate = new TSRealSolution(fDimension);
		fComparator = new TSEvaluationValueComparator<TSRealSolution>(minimization);
	}

	/**
	 * Initialization
	 * @return the initial population
	 */
	public TCSolutionSet<TSRealSolution> initialize() {
		fParents = new TCSolutionSet<TSRealSolution>(fSolutionTemplate);
		fKids = new TCSolutionSet<TSRealSolution>(fSolutionTemplate);
		fReproductionSelection = new TCRandomSelectionWithoutReplacement<TSRealSolution>(fRandom);
		fUndx = new TCUndx<TSRealSolution>(fRandom);
		fSurvivalSelection = new TCBestAndRankBasedRouletteSelectionFromFamily<TSRealSolution>(fComparator, fRandom);
		fPopulation = new TCSolutionSet<TSRealSolution>(fSolutionTemplate);
		fPopulation.resize(fPopulationSize);
		return fPopulation;
	}
	
	/**
	 * Returns the current population
	 * @return the current population 
	 */
	public TCSolutionSet<TSRealSolution> getPopulation() {
		return fPopulation;
	}
	
	/**
	 * Returns the offspring set
	 * @return the offspring set
	 */
	public TCSolutionSet<TSRealSolution> makeOffspring() {
		fParents.clear(); //Removes all the individuals from the parent set.
		fKids.clear(); //Removes all the individuals from the offspring set.
		fReproductionSelection.doIt(fPopulation, fUndx.getNoOfParents(), fParents); //Executes mating selection.
		fUndx.makeOffspring(fParents, fNoOfKids, fKids); //Executes UNDX to generates offspring.
		return fKids;
	}
	
	/**
	 * Executes survival selection
	 */
	public void nextGeneration() {
		fSurvivalSelection.doIt(fPopulation, fParents, fKids); //Executes survival selection.
	}
	
	/**
	 * Returns the best individual in the population.
	 * @return the best individual in the population
	 */
	public TSRealSolution getBestIndividual() {
		Collections.sort(fPopulation, fComparator);
		return fPopulation.get(0);
	}
	
	/**
	 * Returns the best evaluation value in the population.
	 * @return the best evaluation value in the population
	 */
	public double getBestEvaluationValue() {
		return getBestIndividual().getEvaluationValue();
	}
	
}
