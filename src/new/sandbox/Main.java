package sandbox;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.swing.JFrame;

import utils.Vector2;
import utils.Runnable;

public class Main {

	public static Vector2 midpoint = new Vector2(0, 0);
	public static List<Bot> population = new ArrayList<Bot>();
	public static SimulationScreen screen;
	public static Vector2 start = new Vector2(0,-200);
	static int survivalCount = 1;
	static int populationCount = 100;
	static int randomCount = 1;
	
	// Buggs : Concurrent modifier in Simulation screen
	public static void main(String[] args) {
		initPopulation();
		screen = initScreen();
	
	
		System.out.println("NET "+population.get(0).neuralNet);
		new Runnable(0, 1) {
			int currentTick = 0;
			int genNumber = 0;
			public void run() {
				
				boolean botsOnScreen = false;
				int ind= 0;
				for (Bot bot : population) {

					double angle = (Vector2.SignedAngle(bot.getDir(), new Vector2(0, 1))) / 180;
					double xToTarget = (bot.getPos().getX() - midpoint.getX()) / 390;
					double yToTarget = (bot.getPos().getY() - midpoint.getY()) / 390;
				
					
			
					double[] input = new double[] { angle, xToTarget, yToTarget, bot.getVelocity().getX(),
							bot.getVelocity().getY(), bot.getMomentum()};
					
					//double[] input = new double[] { -0.1,-0.1,-0.1,-0.1,-0.1,-0.1};
					double[] res = bot.neuralNet.doSth(input);
					if (ind == 0) {
						
						System.out.println("");
						for (double d : input) {
						
							System.out.print(d+ " ");
						}
						System.out.println(" OUT");
						for (double d : res) {
						
							System.out.print(d+ " ");
						}
						
					}
						
						ind++;

					for (int i = 0; i < bot.getThrusterCount(); i++) {

						bot.getTruster(i).setCurrentTrust(res[i]/21D);
					}
				
					
					//evaluateBot(bot);

					Physics.calcPhysics(bot, 0.1D);
					if (bot.getPos().distance(midpoint) < 1000) {
						botsOnScreen = true;
					}
				}
				
				if (currentTick > 200  || !botsOnScreen) {
					currentTick = 0;
					genNumber += 1;
					//doGenetic(1);
					resetBots();
				}
				
				currentTick++;
				screen.paint(screen.getGraphics());
			}
		};
	
	}

	public static void resetBots() {
		for (Bot bot : population) {
			bot.setPos(start);
			bot.setDir(new Vector2(0,1));
			bot.resetVelocity();

		}
		
	}
	
	public static SimulationScreen initScreen() {
		SimulationScreen bs = new SimulationScreen();
		JFrame f = new JFrame("Workshop");
		f.add(bs);
		f.setSize(1000, 1000);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setResizable(true);
		f.setVisible(true);
		f.setEnabled(true);
		f.setAutoRequestFocus(true);
		f.addKeyListener(bs);
		f.pack();
		return bs;
	}
	

	public static void initPopulation() {
		for (int i = 0;i<populationCount;i++) {
			population.add(createBot());
		}
	}

	public static void evaluateBot(Bot b) {
		// Wird jeden Schritt der Simulation ausgeführt
		b.score += b.getPos().distance(midpoint);
	}

	public static Bot createBot() {

		Vector2 upDirection = new Vector2(0, 1);

		Bot bot = new Bot(start,upDirection);

		bot.addTruster(new Thruster(new Vector2(60, 0), new Vector2(0, -1), 250, 5));
		bot.addTruster(new Thruster(new Vector2(-60, 0), new Vector2(0, -1), 250, 5));
		
		
		bot.assemble();
		return bot;

	}
	
	public static void doGenetic(int genNumber) {
		Collections.sort(population);

		List<Bot> nextPopulation = new ArrayList<Bot>();


		
		for (int i = 0;i<survivalCount;i++) {
			nextPopulation.add(population.get(i));
		}


		for (int i = 0;i<populationCount-survivalCount-randomCount;i++) {
			Bot newBot = nextPopulation.get(i%survivalCount).clone();
			
			double genDoub = genNumber;
			newBot.neuralNet.gen = nextPopulation.get(i%survivalCount).neuralNet.gen + 1;
			if (Math.random() > 0.5F) {
				newBot.neuralNet.mutate(4, 1D);
			}
			else 
			if (Math.random() > 0.1F) {
				newBot.neuralNet.mutate(1, 0.01F);
			}
			else {
				newBot.neuralNet.mutate(10,2);
			}
			
			//net.softMutate(sigma);
			nextPopulation.add(newBot);
		}
		
		for (int i = 0;i<randomCount;i++) {
			nextPopulation.add(createBot());
		}
		
		for (Bot b : nextPopulation) {
			b.score = 0;
		}
		population = nextPopulation;
		
		
	}
}
