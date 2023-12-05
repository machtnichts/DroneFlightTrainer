package workshop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import sandbox.Bot;
import utils.Vector2;

public class GeneticAlgorithim {

	
	public ArrayList<Bot> population = new ArrayList<Bot>();
	
	public final int populationSize = 100;
	public final int survivorCount = 30;
	public final int randomCount = 10;
	
	public void initPopulation() {
		for (int i = 0;i< populationSize; i++) {
			population.add(SandboxSettings.createBot());
		}
	}
	
	public void evaluateBot(Bot b) {
		b.score -= (b.getPos().distance(SandboxSettings.botGoalPosition)* b.getPos().distance(SandboxSettings.botGoalPosition))/1000F;
	}
	
	
	public void calculateNextPopulation(int genNumber) {
		Collections.sort(population);
		Random random = new Random();
		// genNumber is the which generation this algorithim is at, if you want you can use this to alter the mutation values
		
		// Delete the worst ones
		
		
		// Create slightly altered copies of the best ones
		
		List<Bot> nextPopulation = new ArrayList<Bot>();

	
		for (int i = 0;i<survivorCount;i++) {
			nextPopulation.add(population.get(i));
		}
	


		for (int i = 0;i<populationSize-survivorCount-randomCount;i++) {
			Bot newBot = nextPopulation.get(i%survivorCount).clone();
			
			//newBot.neuralNet.gen = nextPopulation.get(i%survivalCount).neuralNet.gen + 1;
			double r = random.nextDouble();
			mutateBot(newBot);
			if (r > 0.8) {
				newBot.mutateMutation(random);
			}
			
			
			//net.softMutate(sigma);
			nextPopulation.add(newBot);
		}
		
	
		

	
		for (int i = 0;i<randomCount;i++) {
			nextPopulation.add(SandboxSettings.createBot());
		}
		population = (ArrayList<Bot>) nextPopulation;
		for (Bot b : population) {
			b.score = 0;
		}
		
	}

		
		
	
	
	public void mutateBot(Bot bot) {
		Random random = new Random();
		for (int i = 0; i < bot.neuralNet.weights.length;i++) {
			if (random.nextDouble() < bot.mutationChance) {
				bot.neuralNet.weights[i] += ((random.nextDouble()*2)-1D)* bot.mutationPower;
			}
		}
	}
	
	
	public void mutateBotEmpty(Bot bot) {
		// mutationChance: chance for an mutation to occur for each neuron
		// mutationPower: strength of a mutation if it occurs
		
		// bot.neuralNet will give you an instance of the neural net
		// use neuralNet.weights to get a double array of all the weights
		
		// Well then, change some weights around
		// If you don't want to you don't have to use the mutationChance and mutationPower
	}
	
	

}
