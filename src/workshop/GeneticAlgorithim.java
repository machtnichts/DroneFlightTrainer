package workshop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import sandbox.Bot;
import sandbox.SimulationBot;
import utils.Vector2;

public class GeneticAlgorithim {

	public ArrayList<Bot> population = new ArrayList<Bot>();

	public final int populationSize = 100;
	Random random = new Random();

	public void initPopulation() {
		for (int i = 0; i < populationSize; i++) {
			Bot bot = createRandomBot();
			population.add(bot);

		}
	}

	public Bot createRandomBot() {

		Bot bot = SandboxSettings.createBot();
		double[] weights = bot.getNeuralWeights();
		for (int w = 0; w < weights.length; w++) {
			weights[w] = ((random.nextGaussian() * 2) - 1D);
		}
		return bot;

	}

	/*
	 * Gets called for every Bot in every frame of the simulation
	 */
	public void evaluateBot(SimulationBot b) {
		// SandboxSettings.botGoalPosition is the position the Bot needs to go to
		//
		// b.addScore(-Math.abs(b.getAngle()/1000F));
		b.addScore(-(b.getPosition().distance(SandboxSettings.botGoalPosition)) / 1000F);
	}

	public void calculateNextPopulation(int genNumber) {

		/*
		 * genNumber tells you which generation this algorithm is at, if you want you
		 * can use this to alter the chance and power of
		 * mutations, so they start of strong but get weaker with time
		 */

		// Delete the worst ones

		// Create slightly altered copies of the best ones
		// calcNextBasic();
		calcNextBasic();
		/*
		
		*/

	}

	public void calcNextStochastic() {
		ArrayList<Bot> nextPopulation = new ArrayList<Bot>();

		for (int i = 0; i < populationSize; i++) {
			float survivalChance = (1F - ((float) i / (float) populationSize)) / 2F;

			if (random.nextFloat() < survivalChance) {
				nextPopulation.add(population.get((int) i));
			}
		}
		int survivorCount = nextPopulation.size();

		for (int i = 0; i < populationSize - survivorCount; i++) {
			Bot newBot = nextPopulation.get(i % nextPopulation.size()).clone();
			// newBot.neuralNet.gen = nextPopulation.get(i%survivalCount).neuralNet.gen + 1;
			double r = random.nextDouble();
			mutateBot(newBot);
			mutateMutation(newBot);

			// net.softMutate(sigma);
			nextPopulation.add(newBot);
		}
		population = nextPopulation;
	}

	public void calcNextBasic() {
		ArrayList<Bot> nextPopulation = new ArrayList<Bot>();
		int survivorCount = populationSize / 3;
		int randomCount = populationSize / 8;
		for (int i = 0; i < survivorCount; i++) {
			nextPopulation.add(population.get(i));
		}
		for (int i = 0; i < populationSize - survivorCount - randomCount; i++) {
			Bot newBot = nextPopulation.get(i % survivorCount).clone();

			// newBot.neuralNet.gen = nextPopulation.get(i%survivalCount).neuralNet.gen + 1;

			mutateBot(newBot);
			mutateMutationChance(newBot);

			// net.softMutate(sigma);
			nextPopulation.add(newBot);
		}

		for (int i = 0; i < randomCount; i++) {
			nextPopulation.add(createRandomBot());
		}
		
		population = nextPopulation;
	}

	public void mutateMutation(Bot b) {
		if (b.getMutationChance() > random.nextDouble()) {
			mutateMutationPower(b);
		}
		if (b.getMutationChance() > random.nextDouble()) {
			mutateMutationChance(b);
		}

	}

	public void mutateMutationPower(Bot b) {
		
		b.setMutationPower(b.getMutationPower() + (random.nextGaussian()  * 2 - 1) * b.getMutationPower() / 10D);
	}

	public void mutateMutationChance(Bot b) {

		b.setMutationChance(clamp(b.getMutationChance() + (random.nextGaussian() * 3 - 1)*b.getMutationPower()  / 10D,0.01D,1D));
	}

	public double clamp(double value, double min, double max) {
		return Math.min(max, Math.max(min, value));
	}

	public void mutateBot(Bot bot) {
		double weights[] = bot.getNeuralWeights();

		for (int i = 0; i < weights.length; i++) {
			if (random.nextDouble() < bot.getMutationChance()) {
				weights[i] += ((random.nextGaussian() * 2D) - 1D) * 2D * bot.getMutationPower();
			}
		}
	}

	public void mutateBotEmpty(SimulationBot bot) {
		// mutationChance: chance for an mutation to occur for each neuron
		// mutationPower: strength of a mutation if it occurs

		// bot.neuralNet will give you an instance of the neural net
		// use neuralNet.weights to get a double array of all the weights

		// Well then, change some weights around
		// If you don't want to you don't have to use the mutationChance and
		// mutationPower
	}

}
