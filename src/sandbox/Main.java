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
	public static Vector2 start = new Vector2(0,-600);
	static int survivalCount = 500;
	static int populationCount = 1000;
	static int randomCount =50;
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
					
				
				boolean botsOnScreen = false;
				int ind= 0;
				for (Bot bot : population) {

					double angle = (Vector2.SignedAngle(bot.getDir(), new Vector2(0, 1))) / 180;
					double xToTarget = (bot.getPos().getX() - midpoint.getX()) / 390;
					double yToTarget = (bot.getPos().getY() - midpoint.getY()) / 390;
				
					
			
					double[] input = new double[] { angle, xToTarget, yToTarget, bot.getVelocity().getX(),
							bot.getVelocity().getY(), bot.getMomentum()};
					
				
					double[] res = bot.neuralNet.process(input);
					
					
	

					for (int i = 0; i < bot.getThrusterCount(); i++) {

						bot.getTruster(i).setCurrentTrust(res[i]);
					}
				
					
					evaluateBot(bot);

					Physics.calcPhysics(bot, 0.1D);
					if (bot.getPos().distance(midpoint) < 600) {
						botsOnScreen = true;
					}
				}
				
				if (currentTick > 1000 || !botsOnScreen) {
					currentTick = 0;
					genNumber += 1;
					doGenetic(gen);
					resetBots();
					gen++;
					textLabel.setText("Generation "+gen);
				}
				
				currentTick++;
				}
				if (fast < 100)
				screen.paint(screen.getGraphics());
			}
		};
	
	}

	public static void resetBots() {
		for (Bot bot : population) {
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

	public static void evaluateBot(Bot b) {
		// Wird jeden Schritt der Simulation ausgeführt
		b.score += b.getPos().distance(midpoint)/1000F;
		b.score += Math.abs(b.momentum)/10F;
		b.score += Math.abs(b.velocity.getX())/10F;
		b.score += Math.abs(b.velocity.getY())/10F;
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
			
			//newBot.neuralNet.gen = nextPopulation.get(i%survivalCount).neuralNet.gen + 1;
			double r = random.nextDouble();
			if (r > 0.5) {
				newBot.neuralNet.mutateWeights(0.001F,1F,random);
			}
			else if (r > 0.1){
				newBot.neuralNet.mutateWeights(0.01F,1F,random);
			}
			else {
				newBot.neuralNet.mutateWeights(0.1F,2F,random);
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
