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
	Random random = new Random();
	
	public void initPopulation() {
		for (int i = 0;i< populationSize; i++) {
			population.add(SandboxSettings.createBot());
		}
	}
	/*
	 * Gets called for every Bot in every frame of the simulation
	 */
	public void evaluateBot(Bot b) {
	

	// b.score -= Math.abs(b.getAngle())*Math.abs(b.getAngle())/30F;
		b.score -= (b.getPos().distance(SandboxSettings.botGoalPosition))/1000F;
	}
	
	
	public void calculateNextPopulation(int genNumber) {
		Collections.sort(population);
		
		// genNumber is the which generation this algorithim is at, if you want you can use this to alter the mutation values
		
		// Delete the worst ones
		
		
		// Create slightly altered copies of the best ones
		//calcNextBasic();
		calcNextBasic();
		/*

	*/
	
	}

		
		
	public void calcNextStochastic() {
		List<Bot> nextPopulation = new ArrayList<Bot>();

		
		for (int i = 0;i<populationSize;i++) {
			float survivalChance = (1F- ((float)i/(float)populationSize))/2F;
			
			if (random.nextFloat() < survivalChance) {
				nextPopulation.add(population.get((int) i));
			}
		}
		int survivorCount = nextPopulation.size();
		
		for (int i = 0;i<populationSize-survivorCount;i++) {
			Bot newBot = nextPopulation.get(i%nextPopulation.size()).clone();
			//newBot.neuralNet.gen = nextPopulation.get(i%survivalCount).neuralNet.gen + 1;
			double r = random.nextDouble();
			mutateBot(newBot);
			if (r > 0.8) {
				newBot.mutateMutation(random);
			}
			
			
			//net.softMutate(sigma);
			nextPopulation.add(newBot);
		}
		population = (ArrayList<Bot>) nextPopulation;
	}
	public void calcNextBasic() {
		List<Bot> nextPopulation = new ArrayList<Bot>();
		int survivorCount = populationSize/3;
		int randomCount = populationSize/8;
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
	}
	public void mutateBot(Bot bot) {

		for (int i = 0; i < bot.neuralNet.weights.length;i++) {
			if (random.nextDouble() < bot.mutationChance) {
				bot.neuralNet.weights[i] += ((random.nextGaussian()*2)-1D)* bot.mutationPower;
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
