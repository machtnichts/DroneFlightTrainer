package workshop;

import java.util.ArrayList;
import java.util.Collections;

import sandbox.Bot;

public class GeneticAlgorithim {

	
	public static ArrayList<Bot> population = new ArrayList<Bot>();
	
	
	
	
	public static void calculateNextPopulation(int genNumber) {
		Collections.sort(population);
	}

		
		
	
	
	
	
	
	public void mutateBot(Bot bot, double mutationChance, double mutationPower) {
		// mutationChance: chance for an mutation to occur for each neuron
		// mutationPower: strength of a mutation if it occurs
		
		// bot.neuralNet will give you an instance of the neural net
		// use neuralNet.weights to get a double array of all the weights
		
		// Well then, change some weights around
		// If you don't want to you don't have to use the mutationChance and mutationPower
	}
}
