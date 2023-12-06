package sandbox;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;


import neural.NeuralNetworkSimple;
import utils.Vector2;
import workshop.GeneticAlgorithim;
import workshop.SandboxSettings;
import utils.Runnable;

public class Main {


	
	public static SimulationScreen screen;


	public static GeneticAlgorithim geneticAlgorithim = new GeneticAlgorithim();
	static int gen = 0;
	public static ArrayList<Double> plotScores = new ArrayList<Double>(); 
	public static Scatterplot plot;
	
	// Buggs : Concurrent modifier in Simulation screen
	public static Random random;
	public static void main(String[] args) {  
		random = new Random();
		geneticAlgorithim.initPopulation();
		screen = initScreen();

		new Runnable(0, 1) {
			int currentTick = 0;
			int genNumber = 0;
			Vector2 targetVel = new Vector2(0,0);
			
			public void run() {
				
				int fast = shiftSlider.getValue();
				for (int a = 0;a<fast;a++) {

				for (Bot bot : geneticAlgorithim.population) {

				
					double angle = (Vector2.SignedAngle(bot.getDir(), new Vector2(0, 1))) / 180;
					double xToTarget = (bot.getPos().getX() - SandboxSettings.botGoalPosition.getX())/500D;
					double yToTarget = (bot.getPos().getY() - SandboxSettings.botGoalPosition.getY())/500D;
				
					double[] input = new double[] { angle, xToTarget, yToTarget, bot.getVelocity().getX(),
							bot.getVelocity().getY(), bot.getMomentum()};
					
					double[] res = bot.neuralNet.calculate(input);

					for (int i = 0; i < bot.getThrusterCount(); i++) {

						bot.getTruster(i).setCurrentTrust(res[i]);
					}
				
					
					geneticAlgorithim.evaluateBot(bot);

					Physics.calcPhysics(bot, 0.1D);
				
				}
				if (SandboxSettings.targetSetting ==  TargetSetting.CHANGE_TARGET_DURING_RUN) {
					if (currentTick % 300 == 0) {
						SandboxSettings.botGoalPosition = new Vector2((random.nextDouble()*2-1) * 700,(random.nextDouble()*2-1) * 700);
					}
				}
				if (SandboxSettings.targetSetting ==  TargetSetting.CANT_CATCH_ME) {
					if (currentTick % 300 == 0) {
						targetVel = new Vector2(0,0);
						SandboxSettings.botGoalPosition = new Vector2((random.nextDouble()*2-1) * 700,(random.nextDouble()*2-1) * 700);
					}
					
					targetVel = targetVel.add(new Vector2(random.nextDouble()*2-1,random.nextDouble()*2-1).mult(0.1F));
					SandboxSettings.botGoalPosition = SandboxSettings.botGoalPosition.add(targetVel);
					if (SandboxSettings.botGoalPosition.magnitude() > 800) {
						targetVel = new Vector2(0,0);
						SandboxSettings.botGoalPosition = new Vector2((random.nextDouble()*2-1) * 400,(random.nextDouble()*2-1) * 400);
					}
					
				}
				if (SandboxSettings.targetSetting == TargetSetting.MOVE_CIRCULAR) {
					SandboxSettings.botGoalPosition = Vector2.turnDeg(SandboxSettings.botGoalPosition, 0.5F);
				}
				
				if (currentTick > SandboxSettings.simulationSteps + gen * SandboxSettings.additionalSimulationStepsPerGeneration) {
					calculateLastScore();
			
					currentTick = 0;
					genNumber += 1;
					Collections.sort(geneticAlgorithim.population);
					lastBest = geneticAlgorithim.population.get(0);
					geneticAlgorithim.calculateNextPopulation(gen);
					
					if (plot != null) {
						plot.setData(plotScores);
						plot.update();
					}
					
				
					if (SandboxSettings.scoreSetting == ScoreSetting.BASIC_SCORE) {
						plotScores.add(lastBest.score);
						textLabel.setText("Generation "+gen +" | Best Score "+ lastBest.score + " Mutation ["+ lastBest.mutationChance +" | "+ lastBest.mutationPower +"]");
						
					}
					else {
						plotScores.add(lastBest.lastScore);
						textLabel.setText("Generation "+gen +" | Best Score "+ lastBest.lastScore + " Mutation ["+ lastBest.mutationChance +" | "+ lastBest.mutationPower +"]");
					}
					
					gen++;
					resetBots();
					if (SandboxSettings.targetSetting ==  TargetSetting.MOVE_CIRCULAR)
						SandboxSettings.botGoalPosition = Vector2.turnDeg(SandboxSettings.botGoalPosition, Math.random()*360F);
					
					if (SandboxSettings.targetSetting ==  TargetSetting.CHANGE_TARGET_EACH_RUN)
					SandboxSettings.botGoalPosition = new Vector2((random.nextDouble()*2-1) * 400,(random.nextDouble()*2-1) * 400);
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
	
	public static void calculateLastScore() {
		for (Bot bot : geneticAlgorithim.population) {
			if (SandboxSettings.scoreSetting == ScoreSetting.EXPONATIALY_WEIGHTED_SCORE) {
				if (bot.lastScore == 0) {
					bot.lastScore = bot.score;
				}
				else {
					bot.lastScore = bot.lastScore * 0.9F + bot.score*0.1F;
				}
				
			}
			else {
				bot.lastScore = (bot.lastScore * bot.iterations + bot.score)/ (double)(bot.iterations+1D);
			}
			
		}
	}
	public static void resetBots() {
	
		for (Bot bot : geneticAlgorithim.population) {
			bot.iterations += 1;
			bot.score = 0;
			bot.setPos(SandboxSettings.botSpawnPosition);
			bot.setDir(SandboxSettings.botUpVector);
			bot.resetVelocity();
		}
	}
	
	static JSlider shiftSlider;
	static JLabel textLabel;
	public static SimulationScreen initScreen() {
		   SimulationScreen bs = new SimulationScreen();
	        JFrame f = new JFrame("Workshop");

	        JPanel panel = new JPanel(new BorderLayout());
	        
	        shiftSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 1);
	        shiftSlider.setMajorTickSpacing(5);
	        shiftSlider.setMinorTickSpacing(1);
	        shiftSlider.setPaintTicks(true);
	        shiftSlider.setPaintLabels(true);
	    
	        textLabel = new JLabel("Your Text Here", JLabel.CENTER);
	        textLabel.setFont(textLabel.getFont().deriveFont(16.0f)); 
	        panel.add(textLabel, BorderLayout.NORTH);

	        panel.add(shiftSlider, BorderLayout.SOUTH);

	  
	        panel.add(bs, BorderLayout.CENTER);
	        JButton button = new JButton("Plot score");
	        button.addActionListener(e -> generatePlot());

	        // Use FlowLayout for the panel containing the button
	        JPanel buttonPanel = new JPanel(new FlowLayout());
	        buttonPanel.add(button);

	        panel.add(buttonPanel, BorderLayout.WEST);

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
	
	
	public static void generatePlot() {
		
		   double[] array = new double[plotScores.size()];

	        // Copy elements from ArrayList to the array
	        for (int i = 0; i < plotScores.size(); i++) {
	            array[i] = plotScores.get(i);
	        }
	        plot = new Scatterplot(array);
	}
	


	

	
	
}
