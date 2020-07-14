package wsc.ecj.ga;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import ec.EvolutionState;
import ec.multiobjective.MultiObjectiveFitness;
import ec.simple.SimpleFitness;
import ec.util.Parameter;
import ec.vector.VectorIndividual;
import wsc.InitialWSCPool;
import wsc.data.pool.Service;
import wsc.graph.ServiceGraph;

public class SequenceVectorIndividual extends VectorIndividual {

	private static final long serialVersionUID = 1L;

	private double availability;
	private double reliability;
	private double time;
	private double cost;
	private double matchingType;
	private double semanticDistance;

	private String strRepresentation; // a string of graph-based representation
	
	private int splitPosition;


	public Service[] genome; // before encoding
	public List<Integer> fullSerQueue_before = new ArrayList<Integer>();// before encoding

	
	
	public List<Integer> serQueue = new ArrayList<Integer>(); // after encoding
	
	public List<Service> relevantList;

	// Multitasking attributes
	List<Integer> factorial_rank;
	private double scalarFitness;
	private int skillFactor;
	private double fitnessVal; // fitness value before penalizing violation of constrains
	private List<Double> fitnessTask; // fitness values after before penalizing violation of constrains for each task
	
	private double fitness_semantic;  //MT+SIM with range (0,1]

	public double getAvailability() {
		return availability;
	}

	public void setAvailability(double availability) {
		this.availability = availability;
	}

	public double getReliability() {
		return reliability;
	}

	public void setReliability(double reliability) {
		this.reliability = reliability;
	}

	public double getTime() {
		return time;
	}

	public void setTime(double time) {
		this.time = time;
	}

	public double getCost() {
		return cost;
	}

	public void setCost(double cost) {
		this.cost = cost;
	}

	public double getMatchingType() {
		return matchingType;
	}

	public void setMatchingType(double matchingType) {
		this.matchingType = matchingType;
	}

	public double getSemanticDistance() {
		return semanticDistance;
	}

	public void setSemanticDistance(double semanticDistance) {
		this.semanticDistance = semanticDistance;
	}

	public String getStrRepresentation() {
		return strRepresentation;
	}

	public void setStrRepresentation(String strRepresentation) {
		this.strRepresentation = strRepresentation;
	}

	public List<Service> getRelevantList() {
		return relevantList;
	}

	public void setRelevantList(List<Service> relevantList) {
		this.relevantList = relevantList;
	}

	public List<Integer> getFactorial_rank() {
		return factorial_rank;
	}

	public void setFactorial_rank(List<Integer> factorial_rank) {
		this.factorial_rank = factorial_rank;
	}

	public double getScalarFitness() {
		return scalarFitness;
	}

	public void setScalarFitness(double scalarFitness) {
		this.scalarFitness = scalarFitness;
	}

	public int getSkillFactor() {
		return skillFactor;
	}

	public void setSkillFactor(int skillFactor) {
		this.skillFactor = skillFactor;
	}

	public List<Double> getFitnessTask() {
		return fitnessTask;
	}

	public void setFitnessTask(List<Double> fitnessTask) {
		this.fitnessTask = fitnessTask;
	}

	public double getFitnessVal() {
		return fitnessVal;
	}

	public void setFitnessVal(double fitnessVal) {
		this.fitnessVal = fitnessVal;
	}

	
	public double getFitness_semantic() {
		return fitness_semantic;
	}

	public void setFitness_semantic(double fitness_semantic) {
		this.fitness_semantic = fitness_semantic;
	}
	
	

	public int getSplitPosition() {
		return splitPosition;
	}

	public void setSplitPosition(int splitPosition) {
		this.splitPosition = splitPosition;
	}

	@Override
	public Parameter defaultBase() {
		return new Parameter("sequencevectorindividual");
	}

	@Override
	/**
	 * Initializes the individual.
	 */
	public void reset(EvolutionState state, int thread) {
		WSCInitializer init = (WSCInitializer) state.initializer;
		List<Service> relevantList = init.initialWSCPool.getServiceSequence();
		Collections.shuffle(relevantList, init.random);

		genome = new Service[relevantList.size()];
		relevantList.toArray(genome);
		this.evaluated = false;
	}

	@Override
	public boolean equals(Object ind) {
		boolean result = false;

		if (ind != null && ind instanceof SequenceVectorIndividual) {
			result = true;
			SequenceVectorIndividual other = (SequenceVectorIndividual) ind;

			for (int i = 0; i < genome.length; i++) {
				if (!genome[i].equals(other.genome[i])) {
					result = false;
					break;
				}

			}
		}
		return result;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(genome);
	}

	@Override
	public String toString() {
		return strRepresentation;
	}

	// public String toGraphString(EvolutionState state) {
	// WSCInitializer init = (WSCInitializer) state.initializer;
	//
	// // set the service candidates according to the sampling
	// InitialWSCPool.getServiceCandidates().clear();
	//
	// InitialWSCPool.setServiceCandidates(relevantList);
	//
	// ServiceGraph graph = init.graGenerator.generateGraphBySerQueue();
	// return graph.toString();
	// }
	
	public void calculateSequenceFitness(SequenceVectorIndividual ind2, WSCInitializer init, EvolutionState state) {

		InitialWSCPool.getServiceCandidates().clear();
		List<Service> serviceCandidates = new ArrayList<Service>(Arrays.asList(ind2.genome));
		InitialWSCPool.setServiceCandidates(serviceCandidates);

		List<Integer> fullSerQueue = new ArrayList<Integer>();
		for (Service ser : ind2.genome) {
			fullSerQueue.add(WSCInitializer.serviceIndexBiMap.inverse().get(ser.getServiceID()));
		}
		
//		System.out.println(fullSerQueue);


		List<Integer> usedSerQueue = new ArrayList<Integer>();

		ServiceGraph graph = init.graGenerator.generateGraphBySerQueue();
		List<Integer> usedQueue = init.graGenerator.usedQueueofLayers("startNode", graph, usedSerQueue);
		
		
		System.out.println(usedQueue);
		
		ind2.setSplitPosition(usedQueue.size());
		// add unused queue to form a complete a vector-based individual
		List<Integer> serQueue = init.graGenerator.completeSerQueueIndi(usedQueue, fullSerQueue);

		// set the serQueue to the updatedIndividual
		ind2.serQueue = serQueue;

		ind2.setStrRepresentation(graph.toString());
		// evaluate updated updated_graph
		init.eval.aggregationAttribute(ind2, graph);
		double f = init.eval.calculateFitness(this);
		this.setFitnessVal(f);
		
		ind2.evaluated = true;
	}
	
	
//
//	public void calculateSequenceFitness(Service[] sequence, WSCInitializer init, EvolutionState state) {
//		// generate DAG corresponding to vector
//		InitialWSCPool.getServiceCandidates().clear();
//
//		relevantList = new ArrayList<Service>();
//
//		for (Service s : sequence) {
//			relevantList.add(s);
//		}
//
//		InitialWSCPool.setServiceCandidates(relevantList);
//		ServiceGraph graph = init.graGenerator.generateGraphBySerQueue();
//
//		// set DAG string to individual
//		this.setStrRepresentation(graph.toString());
//
//		// evaluation
//		init.eval.aggregationAttribute(this, graph);
//		double f = init.eval.calculateFitness(this);
//		// set fitness to individual
//		this.setFitnessVal(f);
//
////		((SimpleFitness) fitness).setFitness(state, f, false); // XXX Move this inside the other one
//		this.evaluated = true;
//
//	}
}
