package wsc.ecj.ga;

import java.util.ArrayList;
import java.util.List;

import ec.*;
import ec.simple.*;

import wsc.tasks.*;

public class WSCProblem extends Problem implements SimpleProblemForm {
	private static final long serialVersionUID = 1L;

	public void evaluate(final EvolutionState state, final Individual ind, final int subpopulation,
			final int threadnum) {
//		
		if(ind.evaluated == true)
			return;

		if (!(ind instanceof SequenceVectorIndividual))
			state.output.fatal("Whoa!  It's not a SequenceVectorIndividual!!!", null);

		SequenceVectorIndividual ind2 = (SequenceVectorIndividual) ind;
		WSCInitializer init = (WSCInitializer) state.initializer;

		ind2.calculateSequenceFitness(ind2, init, state);

		// compute the fitness for the first generation
		if (state.generation == 0) {

			ArrayList<Double> fitnessTa = new ArrayList<>();

			for (Task task : init.tasks) {
				fitnessTa.add(task.calculateFitness4Tasks(ind2, init));
			}
			ind2.setFitnessTask(fitnessTa);

		} else {

			setFitness4Tasks(init, state, ind2);

		}

	}

	private void setFitness4Tasks(WSCInitializer init, EvolutionState state, SequenceVectorIndividual indi) {

		ArrayList<Double> fitnessTa = new ArrayList<>();

		if (init.hasLS == 0) {
			setFitnessTask4OneTask(fitnessTa, indi, init, state);
		} else if (init.hasLS == 1) {
			setFitnessTask4NeighborTask(fitnessTa, indi, init, state);

		} else if (init.hasLS == 10) {
			setFitnessTaskAllTask(fitnessTa, indi, init, state);
		}

	}

	private void setFitnessTask4OneTask(ArrayList fitnessTa, SequenceVectorIndividual indi, WSCInitializer init,
			EvolutionState state) {

		for (int i = 0; i < init.TaskNum; i++)
			if (i != indi.getSkillFactor())
				fitnessTa.add(init.LIMIT);
			else
				fitnessTa.add(init.tasks.get(i).calculateFitness4Tasks(indi, init));
//		}

		indi.setFitnessTask(fitnessTa);
//		System.out.println();
//		System.out.println("Compre"+ indi.getFitnessVal() + indi.getFitnessTask().get(indi.getSkillFactor()));

	}

	private void setFitnessTaskAllTask(ArrayList fitnessTa, SequenceVectorIndividual indi, WSCInitializer init,
			EvolutionState state) {

		for (int i = 0; i < init.TaskNum; i++) {
			fitnessTa.add(init.tasks.get(i).calculateFitness4Tasks(indi, init));
		}

		indi.setFitnessTask(fitnessTa);

	}

	private void setFitnessTask4NeighborTask(ArrayList fitnessTa, SequenceVectorIndividual indi, WSCInitializer init,
			EvolutionState state) {

		setFitnessTask4OneTask(fitnessTa, indi, init, state);

		int currentindex = indi.getSkillFactor();

		int neghborIndex1 = currentindex - 1;
		int neghborIndex2 = currentindex + 1;

		if (neghborIndex1 < init.TaskNum && neghborIndex1 >= 0) {

			fitnessTa.set(neghborIndex1, init.tasks.get(neghborIndex1).calculateFitness4Tasks(indi, init));

		}

		if (neghborIndex2 < init.TaskNum && neghborIndex2 >= 0) {

			fitnessTa.set(neghborIndex2, init.tasks.get(neghborIndex2).calculateFitness4Tasks(indi, init));

		}

		indi.setFitnessTask(fitnessTa);
	}

}