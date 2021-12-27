package wsc.ecj.ga;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;

import ec.BreedingPipeline;
import ec.EvolutionState;
import ec.Individual;
import ec.Population;
import ec.Subpopulation;
import ec.simple.SimpleBreeder;
import ec.util.Parameter;
import wsc.data.pool.Service;
import wsc.ecj.eda.WSCSampling4Tasks;

public class MultiTaskingBreeder extends SimpleBreeder {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4304591219117968129L;

	public static Individual[] sampledIndi1;

	// ovrride this method plz ????

	@Override
	public void setup(EvolutionState state, Parameter base) {
		// TODO Auto-generated method stub
		super.setup(state, base);
	}

	/**
	 * Override breedPopulation(). We take the result from the super method in
	 * SimpleBreeder and append it to the old population. Hence, after generation 0,
	 * every subsequent call to <code>NSGA2Evaluator.evaluatePopulation()</code>
	 * will be passed a population of 2x<code>originalPopSize</code> individuals.
	 */
	public Population breedPopulation(EvolutionState state) {

		// if (sampledIndi1 != null) {
		//
		// Individual[] sampledIndi2 = state.population.subpops[0].individuals;
		// Individual[] combined = ObjectArrays.concat(sampledIndi1, sampledIndi2,
		// Individual.class);
		//
		// state.population.subpops[0].individuals = combined;
		// }

		Population oldPop = (Population) state.population;
		Population newPop = (Population) state.population.emptyClone();

		Individual[] oldInds = oldPop.subpops[0].individuals;
		Individual[] newInds = new Individual[0];

		// set newPop from sampling

		// 0. Assign individuals to pools of different tasks
		initialTaskPools(state, WSCInitializer.pool4Tasks);

		// 1. Generate sampled individuals for next generation, but combine later on
		WSCSampling4Tasks cl = new WSCSampling4Tasks();

		// 1.1 Get the overall size of all pool
		double sizeOfAllPools = 0.00;
		for (List<SequenceVectorIndividual> pool : WSCInitializer.pool4Tasks) {
			sizeOfAllPools += pool.size();
		}

//		for (int i=0; i< WSCInitializer.pool4Tasks.size();i++) {
//			if(WSCInitializer.pool4Tasks.get(i)!= null) {
//				for(SequenceVectorIndividual ind: WSCInitializer.pool4Tasks.get(i)) {
//					if(ind.getSkillFactor() != i) {
//						System.out.println("fcunk");
//					}
//
//				}
//				
//			}
//		
//		}

		if (sizeOfAllPools == 60) {
			System.out.println("pool" + sizeOfAllPools);

		}

		// We use predefined sampling size for each distribution
		List<Integer> sampleSizeList = Lists.newArrayList();

		sampleSizeList.add(6);
		sampleSizeList.add(7);
		sampleSizeList.add(7);
		sampleSizeList.add(6);
		sampleSizeList.add(2);
		sampleSizeList.add(2);
		sampleSizeList.add(2);



		int counter4SampleSize = 0;

		for (List<SequenceVectorIndividual> pool : WSCInitializer.pool4Tasks) {

			if (pool.size() != 0) {
//				int samplingSize = (int) java.lang.Math
//						.ceil((pool.size() * WSCInitializer.samplingSize / sizeOfAllPools));

				int samplingSize = sampleSizeList.get(counter4SampleSize);

				System.out.println("pool" + samplingSize);

				// 1.1 Sampling for each task pool
				Individual[] newInds4Pool = new Individual[0];
				
				newInds4Pool = cl.sampling4EachTask(state, samplingSize, pool.get(0).getSkillFactor(), counter4SampleSize);

				newInds = ObjectArrays.concat(newInds, newInds4Pool, Individual.class);

			}

			if (pool.size() == 1) {
				// what about pool size equals 1, let say we keep it to next generation
				newInds[0] = pool.get(0);
			}

			counter4SampleSize++;
		}

		// 2. Assign new pop from combined results
		Individual[] combinedInds = ObjectArrays.concat(oldInds, newInds, Individual.class);

		newPop.subpops[0].individuals = combinedInds;
		return newPop;

	}

	private List<List<SequenceVectorIndividual>> initialTaskPools(EvolutionState state,
			List<List<SequenceVectorIndividual>> pool4Tasks) {

		// Get population
		Subpopulation pop = (Subpopulation) state.population.subpops[0];
		
		pool4Tasks.forEach(listIndiofOnePool -> listIndiofOnePool.clear());


		// Assign pop and check the size and update the best solutions
		for (int i = 0; i < pop.individuals.length; i++) {
			SequenceVectorIndividual ind4pool = (SequenceVectorIndividual) pop.individuals[i];
			int skill_fac = ind4pool.getSkillFactor();

			SequenceVectorIndividual t1 = (SequenceVectorIndividual) (ind4pool.clone());

			pool4Tasks.get(skill_fac).add(t1);

			// 0&1; 1&2, 2&3

			if (skill_fac == 0) {
				SequenceVectorIndividual t2 = (SequenceVectorIndividual) (ind4pool.clone());
				pool4Tasks.get(WSCInitializer.TaskNum).add(t2);
			}

			if (skill_fac == 1) {
				SequenceVectorIndividual t2 = (SequenceVectorIndividual) (ind4pool.clone());
				SequenceVectorIndividual t3 = (SequenceVectorIndividual) (ind4pool.clone());

				pool4Tasks.get(WSCInitializer.TaskNum).add(t2);
				pool4Tasks.get(WSCInitializer.TaskNum + 1).add(t3);

			}

			if (skill_fac == 2) {
				SequenceVectorIndividual t2 = (SequenceVectorIndividual) (ind4pool.clone());
				SequenceVectorIndividual t3 = (SequenceVectorIndividual) (ind4pool.clone());

				pool4Tasks.get(WSCInitializer.TaskNum + 1).add(t2);
				pool4Tasks.get(WSCInitializer.TaskNum + 2).add(t3);
			}

			if (skill_fac == 3) {
				SequenceVectorIndividual t2 = (SequenceVectorIndividual) (ind4pool.clone());
				pool4Tasks.get(WSCInitializer.TaskNum + 2).add(t2);
			}

		}

		// only keep the size of threhold
		for (List<SequenceVectorIndividual> pool : pool4Tasks) {
			if (pool.size() > WSCInitializer.threhold) {
				

				pool.sort((i1, i2) -> {

					Double di1 = ((SequenceVectorIndividual) i1).getScalarFitness();
					Double di2 = ((SequenceVectorIndividual) i2).getScalarFitness();
					return di1.compareTo(di2);
				});
				
				Collections.shuffle(pool);

				// remove the bad ones
				pool.subList(WSCInitializer.threhold, pool.size()).clear();
			}
		}

		return pool4Tasks;
	}

}
