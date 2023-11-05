package sandbox;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;

import utils.Vector2;
import utils.Runnable;

public class Main {

	public static Vector2 midpoint = new Vector2(0, 0);
	public static List<Bot> population = new ArrayList<Bot>();
	public static SimulationScreen screen;
	public static Vector2 start = new Vector2(0,0);
	static int survivalCount = 40;
	static int populationCount = 100;
	static int randomCount =5;
	static int gen = 0;
	
	// Buggs : Concurrent modifier in Simulation screen
	public static Random random;
	public static void main(String[] args) {  
		random = new Random();
		initPopulation();
		screen = initScreen();
	
	
		System.out.println("NET "+population.get(0).neuralNet);
		new Runnable(0, 1) {
			int currentTick = 0;
			int genNumber = 0;
			public void run() {
				
				int fast = shiftSlider.getValue();
				for (int a = 0;a<fast;a++) {

				for (Bot bot : population) {

				
					double angle = (Vector2.SignedAngle(bot.getDir(), new Vector2(0, 1))) / 180;
					double xToTarget = (bot.getPos().getX() - midpoint.getX())/500D ;
					double yToTarget = (bot.getPos().getY() - midpoint.getY())/500D ;
				
					
			
					double[] input = new double[] { angle, xToTarget, yToTarget, bot.getVelocity().getX(),
							bot.getVelocity().getY(), bot.getMomentum()};
					
				
					double[] res = bot.neuralNet.process(input);
					
					
	

					for (int i = 0; i < bot.getThrusterCount(); i++) {

						bot.getTruster(i).setCurrentTrust(res[i]);
					}
				
					
					evaluateBot(bot,currentTick);

					Physics.calcPhysics(bot, 0.1D);
				
				}
				
				if (currentTick > 500) {
					currentTick = 0;
					genNumber += 1;
					resetBots();
					doGenetic(gen);
					textLabel.setText("Generation "+gen +" | Best Score "+ lastBest.lastScore + " Mutation ["+ lastBest.mutationChance +" | "+ lastBest.mutationPower +"]");
	
				
					gen++;
					midpoint = new Vector2((random.nextDouble()*2-1) * 400,(random.nextDouble()*2-1) * 400);
				}
				
				currentTick++;
				}
				if (fast < 100)
				screen.paint(screen.getGraphics());
			}
		};
	
	}

	static Bot lastBest;
	static double lastScore;
	public static void resetBots() {
		for (Bot bot : population) {
			bot.lastScore = (bot.lastScore * bot.iterations + bot.score)/ (double)(bot.iterations+1D);
			bot.iterations += 1;
			bot.score = 0;
			bot.setPos(start);
			bot.setDir(new Vector2(0,1));
			bot.resetVelocity();
		}
		
	}
	
	static JSlider shiftSlider;
	static JLabel textLabel;
	public static SimulationScreen initScreen() {
		   SimulationScreen bs = new SimulationScreen();
	        JFrame f = new JFrame("Workshop");

	        JPanel panel = new JPanel(new BorderLayout()); // Use BorderLayout for the panel

	        shiftSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 1);
	        shiftSlider.setMajorTickSpacing(5);
	        shiftSlider.setMinorTickSpacing(1);
	        shiftSlider.setPaintTicks(true);
	        shiftSlider.setPaintLabels(true);
	    
	        textLabel = new JLabel("Your Text Here", JLabel.CENTER); // Replace "Your Text Here" with your desired text
	        textLabel.setFont(textLabel.getFont().deriveFont(16.0f)); 
	        panel.add(textLabel, BorderLayout.NORTH);
	        // Add the slider to the SOUTH (bottom) of the panel
	        panel.add(shiftSlider, BorderLayout.SOUTH);

	        // Add the simulation screen to the CENTER of the panel
	        panel.add(bs, BorderLayout.CENTER);

	        f.add(panel);

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

	public static void evaluateBot(Bot b,int tick) {
		// Wird jeden Schritt der Simulation ausgeführt
	
		
		b.score -= (b.getPos().distance(midpoint))/1000F;
		
		
		//b.score += Math.abs(b.momentum);
		double deg = (Vector2.SignedAngle(b.getDir(), new Vector2(0, -1))/180)*2;
		//b.score +=  (deg*deg) *10;
		
		//b.score += Math.abs(b.velocity.getX())/10F;
		//b.score += Math.abs(b.velocity.getY())/10F;
	
	}

	public static Bot createBot() {

		Vector2 upDirection = new Vector2(0, 1);

		Bot bot = new Bot(start,upDirection);

		bot.addTruster(new Thruster(new Vector2(60, 0), new Vector2(0, -1), 150, 5));
		bot.addTruster(new Thruster(new Vector2(-60, 0), new Vector2(0, -1), 150, 5));
		

		bot.assemble();
		return bot;

	}
	

	public static void doGenetic(int genNumber) {

		
		Collections.sort(population);
	
		lastBest = population.get(0);

		List<Bot> nextPopulation = new ArrayList<Bot>();

	
		for (int i = 0;i<survivalCount;i++) {
			nextPopulation.add(population.get(i));
		}
	


		for (int i = 0;i<populationCount-survivalCount-randomCount;i++) {
			Bot newBot = nextPopulation.get(i%survivalCount).clone();
			
			//newBot.neuralNet.gen = nextPopulation.get(i%survivalCount).neuralNet.gen + 1;
			double r = random.nextDouble();
			newBot.neuralNet.mutateWeights(newBot.mutationChance,newBot.mutationPower,random);
			if (r > 0.8) {
				newBot.mutateMutation(random);
			}
			
			
			//net.softMutate(sigma);
			nextPopulation.add(newBot);
		}
		
	
		

	
		for (int i = 0;i<randomCount;i++) {
			nextPopulation.add(createBot());
		}
		population = nextPopulation;
		for (Bot b : population) {
			b.score = 0;
		}
		
	}
}
