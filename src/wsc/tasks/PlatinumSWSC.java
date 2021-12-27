package wsc.tasks;

import wsc.ecj.ga.SequenceVectorIndividual;
import wsc.ecj.ga.WSCInitializer;

public class PlatinumSWSC extends Task {

	@Override
	public double calculateFitness4Tasks(SequenceVectorIndividual individual, WSCInitializer init) {
		// does individual violate the constrains

		double fitness4Platinum;
		// does individual violate the constrains
		if (individual.getFitness_semantic() <= init.PLATINUM && individual.getFitness_semantic() > init.GOLD) {
			fitness4Platinum = 0.5 + 0.5 * individual.getFitnessVal();
		} else {

			double violation = init.GOLD - individual.getFitness_semantic();
			fitness4Platinum = 0.5 * individual.getFitnessVal() - 0.5 * violation;
		}
		return fitness4Platinum;

	}

}
