package wsc.ecj.ga;

import java.util.ArrayList;
import java.util.Collections;

import com.google.common.collect.Lists;

import ec.EvolutionState;
import ec.Individual;
import ec.Initializer;
import ec.Population;
import ec.Subpopulation;
import ec.simple.SimpleEvaluator;
import ec.util.Parameter;

public class MultiTaskingEvaluator extends SimpleEvaluator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1009651871986649640L;

	/**
	 * The original population size is stored here so we knows how large to create
	 * the archive (it's the size of the original population -- keep in mind that
	 * MultiTaskingBreeder had made the population larger to include the children.
	 */

	public int originalPopSize[];

	@Override
	public void setup(EvolutionState state, Parameter base) {
		super.setup(state, base);

		Parameter p = new Parameter(Initializer.P_POP);
		int subpopsLength = state.parameters.getInt(p.push(Population.P_SIZE), null, 1);
		Parameter p_subpop;
		originalPopSize = new int[subpopsLength];
		for (int i = 0; i < subpopsLength; i++) {
			p_subpop = p.push(Population.P_SUBPOP).push("" + i).push(Subpopulation.P_SUBPOPSIZE);
			originalPopSize[i] = state.parameters.getInt(p_subpop, null, 1);
		}
	}

	@Override
	public void evaluatePopulation(EvolutionState state) {

		WSCInitializer init = (WSCInitializer) state.initializer;

		super.evaluatePopulation(state);

		// set the factorial ranks and scalar fitness
		updateRankPopulation(state, init);

		// Select N fittest members to form the next generation
		for (int x = 0; x < state.population.subpops.length; x++)
			state.population.subpops[x].individuals = selectFittest(state, x);

		// realign skill factor of individuals
		// if (state.generation == 0) {
		// state.population.subpops[0].individuals = realignment4SkillFactor(state,
		// init);
		// }
		
		//this block for trace the fitness values of each solution
		ArrayList<Double> fitvalLst = new ArrayList<Double>();
		for(Individual indiv :state.population.subpops[0].individuals) {
			double fitval = ((SequenceVectorIndividual) indiv).getFitnessVal();
			fitvalLst.add(fitval);
		}
		WSCInitializer.fitvalLog.add(fitvalLst);

		// test for see number of taskskill in generations
		 printSizeOfSkillPop(state, init);

	}

	private void printSizeOfSkillPop(EvolutionState state, WSCInitializer init) {
		ArrayList<ArrayList<SequenceVectorIndividual>> pops4SkillFactors = Lists.newArrayList();

		for (int i = 0; i < init.TaskNum; i++) {
			ArrayList<SequenceVectorIndividual> pop4SkillFactor = Lists.newArrayList();
			pops4SkillFactors.add(pop4SkillFactor);
		}

		for (Individual indi : state.population.subpops[0].individuals) {
			SequenceVectorIndividual vectorIndi = (SequenceVectorIndividual) indi;
			pops4SkillFactors.get(vectorIndi.getSkillFactor()).add(vectorIndi);
		}

//		System.out.println(pops4SkillFactors.get(0).size() + ";" + (pops4SkillFactors.get(1).size() + ";"
//				+ pops4SkillFactors.get(2).size() + ";" + pops4SkillFactors.get(3).size()));
	}

	// update the factorial ranks and scalar fitness based on factorial ranks
	void updateRankPopulation(EvolutionState state, WSCInitializer init) {

		int nIndividual = state.population.subpops[0].individuals.length; // number of supopulations

		ArrayList<ArrayList<SequenceVectorIndividual>> rankInTask = new ArrayList<>();

		for (int i = 0; i < init.TaskNum; i++) {
			ArrayList<SequenceVectorIndividual> lstIndividualInTask = new ArrayList<>();
			rankInTask.add(lstIndividualInTask);
		}

		for (int i_in = 0; i_in < nIndividual; i_in++) {
			SequenceVectorIndividual ind = (SequenceVectorIndividual) state.population.subpops[0].individuals[i_in];
			for (int i = 0; i < init.TaskNum; i++) {
				ArrayList<SequenceVectorIndividual> lstIndividualInTask = rankInTask.get(i);
				boolean check = true;
				for (int j = 0; j < lstIndividualInTask.size(); j++) {
					if (lstIndividualInTask.get(j).getFitnessTask().get(i) < ind.getFitnessTask().get(i)) {
						lstIndividualInTask.add(j, ind);
						check = false;
						break;
					}
				}
				if (check) {
					lstIndividualInTask.add(ind);
				}
				rankInTask.set(i, lstIndividualInTask);
			}
		}

		for (int i = 0; i < nIndividual; i++) {
			SequenceVectorIndividual ind = (SequenceVectorIndividual) state.population.subpops[0].individuals[i];

			ArrayList<Integer> factorial_rank = new ArrayList<>();
			int min_rank = nIndividual + 2;
			int task_rank_min = -1;
			for (int j = 0; j < init.TaskNum; j++) {
				int rankJ = rankInTask.get(j).indexOf(ind) + 1;
				factorial_rank.add(rankJ);
				if (rankJ < min_rank) {
					min_rank = rankJ;
					task_rank_min = j;
				}
			}

			ind.setFactorial_rank(factorial_rank);
			ind.setSkillFactor(task_rank_min);
			ind.setScalarFitness(1.0 / (min_rank));

			// if (state.generation == 0) {// set skill factorial level
			// ArrayList<Integer> factorial_rank4sort = Lists.newArrayList(factorial_rank);
			// Collections.sort(factorial_rank4sort);
			//
			// ArrayList<Integer> skillFactorlevel = Lists.newArrayList();
			// for (int level : factorial_rank4sort) {
			// int skillFactor = factorial_rank.indexOf(level);
			// skillFactorlevel.add(skillFactor);
			// }
			//
			// ind.setSkillFactorLevel(skillFactorlevel);
			// }
		}

	}

	/**
	 * reduce the subpopulation to just the archive based on the scalar fitness.
	 */
	public Individual[] selectFittest(EvolutionState state, int subpop) {
		Individual[] dummy = new Individual[0];

		ArrayList<Individual> newSubpopulation = new ArrayList<Individual>();

		ArrayList<Individual> listIndi = Lists.newArrayList(state.population.subpops[0].individuals);

		// debug the order
		listIndi.sort((i1, i2) -> {

			Double di1 = ((SequenceVectorIndividual) i1).getScalarFitness();
			Double di2 = ((SequenceVectorIndividual) i2).getScalarFitness();
			return di1.compareTo(di2);
		});

		Collections.reverse(listIndi);

		int m = originalPopSize[subpop] - newSubpopulation.size();

		for (int j = 0; j < m; j++)
			newSubpopulation.add(listIndi.get(j));

		Individual[] fittestMembers = (Individual[]) (newSubpopulation.toArray(dummy));

		// for(int i = 0 ; i < fittestMembers.length; i++)
		// fittestMembers[i].evaluated = false;

		return fittestMembers;
	}

}
