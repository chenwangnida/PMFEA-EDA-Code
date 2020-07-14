package wsc.tasks;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;

import ec.EvolutionState;
import wsc.data.pool.Service;
import wsc.ecj.ga.SequenceVectorIndividual;
import wsc.ecj.ga.WSCInitializer;
import wsc.graph.ServiceEdge;

abstract public class Task {
	public abstract double calculateFitness4Tasks (SequenceVectorIndividual individual, WSCInitializer init);

}
