package wsc.ecj.eda;

import java.util.ArrayList;
import java.util.List;

import com.google.common.primitives.Doubles;

import ec.EvolutionState;
import ec.Individual;
import wsc.data.pool.Service;
import wsc.ecj.ga.SequenceVectorIndividual;
import wsc.ecj.ga.WSCInitializer;
import wsc.nhbsa.NHBSA;

import ec.Subpopulation;
import ec.multiobjective.MultiObjectiveFitness;
import ec.multiobjective.nsga2.NSGA2MultiObjectiveFitness;

public class WSCSampling4Tasks {

	public SequenceVectorIndividual[] sampling4EachTask(EvolutionState state, int samplingSize, int Pa_skill_fac,
			int counter) {

		WSCInitializer init = (WSCInitializer) state.initializer;

		SequenceVectorIndividual[] pop_sampled = new SequenceVectorIndividual[samplingSize];

		int indexOfpopSampled = 0;

		List<int[]> pop_updated = sampleNeighbors(state, samplingSize, Pa_skill_fac);

		for (int[] indi_genome : pop_updated) {

			SequenceVectorIndividual t1 = (SequenceVectorIndividual) (state.population.subpops[0].individuals[0]
					.clone());

			Service[] genome = new Service[WSCInitializer.dimension_size];
			t1.genome = genome;

			updatedIndi(t1.genome, indi_genome);

			t1.evaluated = false;
			pop_sampled[indexOfpopSampled] = t1;

			// Set the skill factor
			t1.setSkillFactor(Pa_skill_fac);

			// Set the skill factor for neighboring tasks
			if (counter == 4) { // task 0 or 1
				double rand4Imitation = init.random.nextDouble();

				if (rand4Imitation < 0.5) {
					t1.setSkillFactor(0);
				} else {
					t1.setSkillFactor(1);
				}

			}

			if (counter == 5) {// task 1 or 2
				double rand4Imitation = init.random.nextDouble();

				if (rand4Imitation < 0.5) {
					t1.setSkillFactor(1);
				} else {
					t1.setSkillFactor(2);
				}

			}

			if (counter == 6) {// task 2 or 3
				double rand4Imitation = init.random.nextDouble();

				if (rand4Imitation < 0.5) {
					t1.setSkillFactor(2);
				} else {
					t1.setSkillFactor(3);
				}
			}

			// Set factor rank to the worst one (Highest value)
			ArrayList<Integer> fR = new ArrayList<>();
			for (int ii = 0; ii < init.TaskNum; ii++)
				fR.add(state.population.subpops[0].individuals.length + 1);
			t1.setFactorial_rank(fR);

			t1.setFitnessVal(0);
			t1.setFitnessTask(null);
			t1.setStrRepresentation(null);
			t1.setScalarFitness(0);

			indexOfpopSampled++;
		}

		return pop_sampled;
	}

	public SequenceVectorIndividual[] sampling4EachTask(EvolutionState state, int samplingSize, int Pa_skill_fac) {

		WSCInitializer init = (WSCInitializer) state.initializer;

		SequenceVectorIndividual[] pop_sampled = new SequenceVectorIndividual[samplingSize];

		int indexOfpopSampled = 0;

		List<int[]> pop_updated = sampleNeighbors(state, samplingSize, Pa_skill_fac);

		for (int[] indi_genome : pop_updated) {

			SequenceVectorIndividual t1 = (SequenceVectorIndividual) (state.population.subpops[0].individuals[0]
					.clone());

			Service[] genome = new Service[WSCInitializer.dimension_size];
			t1.genome = genome;

			updatedIndi(t1.genome, indi_genome);

			t1.evaluated = false;
			pop_sampled[indexOfpopSampled] = t1;

			// set the skill factor
			t1.setSkillFactor(Pa_skill_fac);

			// Set factor rank to the worst one (Highest value)
			ArrayList<Integer> fR = new ArrayList<>();
			for (int ii = 0; ii < init.TaskNum; ii++)
				fR.add(state.population.subpops[0].individuals.length + 1);
			t1.setFactorial_rank(fR);

			t1.setFitnessVal(0);
			t1.setFitnessTask(null);
			t1.setStrRepresentation(null);
			t1.setScalarFitness(0);
			// test begin(remove it after testing)
//			List<Integer> fullSerQueue_before = new ArrayList<Integer>();
//			for (Service ser : t1.genome) {
//				fullSerQueue_before.add(WSCInitializer.serviceIndexBiMap.inverse().get(ser.getServiceID()));
//			}
//			t1.fullSerQueue_before = fullSerQueue_before;
//
//			System.out.println("Sampled:" + t1.fullSerQueue_before);
			// end test

			indexOfpopSampled++;
		}

		return pop_sampled;
	}

	private List<int[]> sampleNeighbors(EvolutionState state, int samplingSize, int Pa_skill_fac) {

		// Get population
//		Subpopulation pop = state.population.subpops[0];

		System.out.println(Pa_skill_fac + "for pool index");

		List<SequenceVectorIndividual> pop = WSCInitializer.pool4Tasks.get(Pa_skill_fac);

		int numIndi4OneTask = pop.size();

		// System.out.println("learn a NHM from a pop size: " + pop.individuals.length);
		NHBSA nhbsa = new NHBSA(numIndi4OneTask, WSCInitializer.dimension_size);
		double penalizedFactor[] = new double[numIndi4OneTask];

		int[][] m_generation = new int[numIndi4OneTask][WSCInitializer.dimension_size];

		for (int m = 0; m < numIndi4OneTask; m++) {
			for (int n = 0; n < WSCInitializer.dimension_size; n++) {
//				m_generation[m][n] = ((SequenceVectorIndividual) (pop.individuals[m])).serQueue.get(n);
				m_generation[m][n] = pop.get(m).serQueue.get(n);

			}
		}

		// initial penalizedFactor based on skill factor level

//		initializePanalizeFactor(pop, Pa_skill_fac, penalizedFactor);

		nhbsa.setM_pop(m_generation);
		nhbsa.setM_L(WSCInitializer.dimension_size);
		nhbsa.setM_N(numIndi4OneTask);
//		nhbsa.setPenalizedFactor(penalizedFactor);

		// Sample numIndi4OneTask number of neighbors
		return nhbsa.sampling4NHBSA(samplingSize, WSCInitializer.random);
	}

	private void initializePanalizeFactor(Subpopulation pop, int pa_skill_fac, double penalizedFactor[]) {
		for (int i = 0; i < pop.individuals.length; i++) {
			SequenceVectorIndividual indi = (SequenceVectorIndividual) (pop.individuals[i]);
			if (indi.getSkillFactor() == pa_skill_fac) {
				penalizedFactor[i] = 1;
			} else {
				penalizedFactor[i] = 0.5;
			}
		}
	}

	private void updatedIndi(Service[] genome, int[] updateIndi) {
		for (int n = 0; n < updateIndi.length; n++) {
			genome[n] = WSCInitializer.Index2ServiceMap.get(updateIndi[n]);
		}
	}

}
