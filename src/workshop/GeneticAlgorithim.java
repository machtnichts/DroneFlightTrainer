package workshop;

import java.util.ArrayList;
import java.util.Random;
import sandbox.Bot;
import sandbox.SimulationBot;

public class GeneticAlgorithim {

	public ArrayList<Bot> population = new ArrayList<Bot>();

	public final int populationSize = 5000;
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
			double survivalChance = Math.pow(1F - ((double) i / (double) populationSize),2D) ;

			if (random.nextGaussian() < survivalChance) {
				nextPopulation.add(population.get((int) i));
			}
		}
		int survivorCount = nextPopulation.size();

		for (int i = 0; i < populationSize - survivorCount; i++) {
			Bot newBot = nextPopulation.get(i % nextPopulation.size()).clone();
			// newBot.neuralNet.gen = nextPopulation.get(i%survivalCount).neuralNet.gen + 1;
			
			mutateBot(newBot);
			mutateMutation(newBot);

			// net.softMutate(sigma);
			nextPopulation.add(newBot);
		}
		population = nextPopulation;
	}

	public void calcNextUltraBasic() {
		ArrayList<Bot> nextPopulation = new ArrayList<Bot>();
		int survivorCount = populationSize / 3;
		int randomCount = populationSize / 8;
		for (int i = 0; i < survivorCount; i++) {
			nextPopulation.add(population.get(i));
		}
		for (int i = 0; i < populationSize - survivorCount - randomCount; i++) {
			Bot newBot = nextPopulation.get(i % survivorCount).clone();

			// newBot.neuralNet.gen = nextPopulation.get(i%survivalCount).neuralNet.gen + 1;

			mutateBotBasic(newBot, 3, 1);
			

			// net.softMutate(sigma);
			nextPopulation.add(newBot);
		}

		for (int i = 0; i < randomCount; i++) {
			nextPopulation.add(createRandomBot());
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
			mutateMutation(newBot);

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
		
		b.setMutationPower(b.getMutationPower() + (random.nextGaussian()  * 2 - 0.9D) / 5D);
	}

	public void mutateMutationChance(Bot b) {

		b.setMutationChance(clamp(b.getMutationChance() + (random.nextGaussian() * 3 - 1)*b.getMutationPower()  / 100D,0.01D,1D));
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
	public void mutateBotBasic(Bot bot,int mutationCount, double power) {
		double weights[] = bot.getNeuralWeights();

		for (int i = 0; i < mutationCount; i++) {
			int index = (int) (weights.length * random.nextDouble());
				weights[index] += ((random.nextGaussian() * 2D) - 1D) * power;
			
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
