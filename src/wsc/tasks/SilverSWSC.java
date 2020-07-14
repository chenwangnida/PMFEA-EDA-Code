package wsc.tasks;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jgrapht.DirectedGraph;
import org.jgrapht.GraphPath;
import org.jgrapht.alg.shortestpath.AllDirectedPaths;
import org.jgrapht.alg.shortestpath.BellmanFordShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import ec.EvolutionState;
import wsc.InitialWSCPool;
import wsc.data.pool.Service;
import wsc.ecj.ga.SequenceVectorIndividual;
import wsc.ecj.ga.WSCInitializer;
import wsc.graph.ServiceEdge;
import wsc.graph.ServiceGraph;

public class SilverSWSC extends Task {

	@Override
	public double calculateFitness4Tasks(SequenceVectorIndividual individual, WSCInitializer init) {

		double fitness4Silver;

		double violation;
		// does individual violate the constrains
		if (individual.getFitness_semantic() <= init.SILVER && individual.getFitness_semantic() > init.BRONZE) {
			fitness4Silver = 0.5 + 0.5 * individual.getFitnessVal();
		} else {

			if (individual.getFitness_semantic() > init.SILVER) {
				violation = individual.getFitness_semantic() - init.SILVER;

			} else {
				violation = init.BRONZE - individual.getFitness_semantic();

			}

			fitness4Silver = 0.5 * individual.getFitnessVal() - 0.5 * violation;
		}
		return fitness4Silver;
	}

}
