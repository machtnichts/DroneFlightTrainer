package sandbox;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.Point;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JToggleButton;

import utils.Vector2;
import workshop.GeneticAlgorithm;
import workshop.SandboxSettings;
import utils.RunnerTask;

public class Main {

	public static SimulationScreen screen;

	public static GeneticAlgorithm geneticAlgorithm = new GeneticAlgorithm();
	static int gen = 0;
	public static ArrayList<Double> plotScores = new ArrayList<Double>();
	public static ArrayList<Color> plotColors = new ArrayList<Color>();
	public static Scatterplot plot;
	private final static DecimalFormatSymbols dfs = new DecimalFormatSymbols(Locale.ROOT);
	private final static DecimalFormat df = new DecimalFormat("#0.000");
	private static Instant start = Instant.now();
	private static Instant lastUpdate = Instant.now();

	public final static AtomicBoolean insideSort = new AtomicBoolean(false);
	// Buggs : Concurrent modifier in Simulation screen
	public static Random random;

	public static void main(String[] args) {
		random = new Random();
		geneticAlgorithm.initPopulation();
		screen = initScreen();

		new RunnerTask(0, 1) {
			int currentTick = 0;
			int genNumber = 0;
			Vector2 targetVel = new Vector2(0, 0);

			public void simulateFrame(SimulationBot bot) {
				double angle = (Vector2.SignedAngle(bot.getDir(), new Vector2(0, 1)));
				double xToTarget = (bot.getPosition().getX() - SandboxSettings.botGoalPosition.getX());
				double yToTarget = (bot.getPosition().getY() - SandboxSettings.botGoalPosition.getY());

				double[] input = new double[] { angle / 180D, xToTarget / 1000D, yToTarget / 1000D,
						bot.getVelocity().getX() / 10D,
						bot.getVelocity().getY() / 10D, bot.getMomentum() / 180D };

				double[] res = bot.neuralNet.calculate(input);

				for (double r : res) {
					if (Double.isNaN(r)) {
						System.out.println("----");
						System.out.println("In " + Arrays.toString(input));
						System.out.println("Res " + Arrays.toString(res));
						System.out.println("Wei " + Arrays.toString(bot.getNeuralWeights()));
						System.exit(-1);
						break;

					}
				}
				for (int i = 0; i < bot.getThrusterCount(); i++) {
					// System.out.println(res[i]);
					bot.getTruster(i).setCurrentTrust(res[i]);
				}

				geneticAlgorithm.evaluateBot(bot);

				Physics.calcPhysics(bot, 0.1D);
			}

			public void run() {

				int fast = shiftSlider.getValue();

				if (fast < 50) {
					try {
						Thread.sleep((50 - fast) * 1);
					} catch (InterruptedException e) {

						e.printStackTrace();
					}
				}
				int steps = Math.max(fast - 50, 1);
				for (int a = 0; a < steps; a++) {

					geneticAlgorithm.population.parallelStream()
							.filter(SimulationBot.class::isInstance).map(SimulationBot.class::cast)
							.forEach(bot -> simulateFrame(bot));

					moveTargetDuringSimulation(targetVel, currentTick);

					if (currentTick > SandboxSettings.simulationSteps
							+ gen * SandboxSettings.additionalSimulationStepsPerGeneration) {
						if (geneticAlgorithm.population.size() <= 0)
							return;

						calculateLastScore();

						currentTick = 0;
						genNumber += 1;
						sortPopulation();
						lastBest = (SimulationBot) geneticAlgorithm.population.get(0);
						geneticAlgorithm.calculateNextPopulation(gen);

						updateUI();

						gen++;
						resetBots();
						if (SandboxSettings.targetSetting == TargetSetting.MOVE_CIRCULAR)
							SandboxSettings.botGoalPosition = Vector2.turnDeg(SandboxSettings.botGoalPosition,
									Math.random() * 360F);

						if (SandboxSettings.targetSetting == TargetSetting.CHANGE_TARGET_EACH_RUN
								|| SandboxSettings.targetSetting == TargetSetting.CANT_CATCH_ME) {
							SandboxSettings.botGoalPosition = new Vector2((random.nextDouble() * 2 - 1) * 500,
									(random.nextDouble() * 2 - 1) * 500);
						}
						if (SandboxSettings.targetSetting == TargetSetting.CANT_CATCH_ME) {
							targetVel = new Vector2(0, 0);
							SandboxSettings.botGoalPosition = new Vector2((random.nextDouble() * 2 - 1) * 500,
									(random.nextDouble() * 2 - 1) * 500);
						}
					}

					currentTick++;
				}
				if (fast < shiftSlider.getMaximum() - 1)
					screen.paint(screen.getGraphics());
			}

			private void updateUI() {
				long genPerSecond = (genNumber * 1000L * geneticAlgorithm.population.size())
							/ Duration.between(start, Instant.now()).toMillis();

				String text;
					if (SandboxSettings.scoreSetting == ScoreSetting.BASIC_SCORE) {
						plotScores.add(lastBest.getScore());
						plotColors.add(lastBest.getColor());
						text = "Generation " + gen + " | Best Score " + df.format(lastBest.getScore())
								+ " Mutation [" + df.format(lastBest.mutationChance) + " | "
								+ df.format(lastBest.mutationPower) + "] " + " SPS: " + genPerSecond;
					} else {
						text = "Generation " + gen + " | Best Score " + df.format(lastBest.getLastScore())
								+ " Mutation [" + df.format(lastBest.mutationChance) + " | "
								+ df.format(lastBest.mutationPower) + "] " + " SPS: " + genPerSecond;
						plotScores.add(lastBest.getLastScore());
						plotColors.add(lastBest.getColor());
					}

				if (Duration.between(lastUpdate, Instant.now()).toMillis() > 250) {
					lastUpdate = Instant.now();

					
					if (plot != null) {
						plot.setData(plotScores, plotColors);
						plot.update();
					}
					textLabel.setText(text);
	
				}
			}
		};

	}

	public static void moveTargetDuringSimulation(Vector2 targetVel, int currentTick) {

		if (simulationScreen.holdingMouse) {

			Point mousePosition = MouseInfo.getPointerInfo().getLocation();
			Point canvasLocation = simulationScreen.getLocationOnScreen();

			// Calculate the mouse position relative to the canvas
			int mouseX = (int) (((mousePosition.x - canvasLocation.x) - simulationScreen.getWidth() / 2)
					/ simulationScreen.disp_scale);
			int mouseY = -(int) (((mousePosition.y - canvasLocation.y) - simulationScreen.getHeight() / 2)
					/ simulationScreen.disp_scale);
			SandboxSettings.botGoalPosition.setX(mouseX);
			SandboxSettings.botGoalPosition.setY(mouseY);
			return;
		}
		if (SandboxSettings.targetSetting == TargetSetting.CHANGE_TARGET_DURING_RUN) {
			if (currentTick % 300 == 0) {
				SandboxSettings.botGoalPosition = new Vector2((random.nextDouble() * 2 - 1) * 700,
						(random.nextDouble() * 2 - 1) * 700);
			}
		}
		if (SandboxSettings.targetSetting == TargetSetting.CANT_CATCH_ME) {

			if (currentTick % 300 == 0) {
				SandboxSettings.botGoalPosition = new Vector2((random.nextDouble() * 2 - 1) * 700,
						(random.nextDouble() * 2 - 1) * 700);
			}
			targetVel = targetVel
					.add(new Vector2(random.nextDouble() * 2 - 1, random.nextDouble() * 2 - 1).mult(0.1F));
			SandboxSettings.botGoalPosition = SandboxSettings.botGoalPosition.add(targetVel);

		}
		if (SandboxSettings.targetSetting == TargetSetting.MOVE_CIRCULAR) {
			SandboxSettings.botGoalPosition = Vector2.turnDeg(SandboxSettings.botGoalPosition, 0.5F);
		}
	}

	public static void sortPopulation() {
		List<SimulationBot> bots = new ArrayList<>(geneticAlgorithm.population.stream()
				.filter(SimulationBot.class::isInstance).map(SimulationBot.class::cast).collect(Collectors.toList()));
		insideSort.set(true);
		try {
			Collections.sort(bots);
		} catch (Throwable t) {
			System.out.println("sdf");
		}
		insideSort.set(false);
		geneticAlgorithm.population = new ArrayList<>(bots);
	}

	static SimulationBot lastBest;
	static double lastScore;

	public static void calculateLastScore() {
		for (Bot botL : geneticAlgorithm.population) {
			SimulationBot bot = (SimulationBot) botL;
			if (SandboxSettings.scoreSetting == ScoreSetting.EXPONENTIALY_WEIGHTED_SCORE) {
				if (bot.getLastScore() == 0) {
					bot.setLastScore(bot.getScore());
				} else {
					bot.setLastScore(bot.getLastScore() * 0.9F + bot.getScore() * 0.1F);
				}

			} else {
				bot.setLastScore((bot.getLastScore() * (double) bot.getIterations() + bot.getScore())
						/ ((double) ((double) bot.getIterations() + 1D)));
			}

		}
	}

	public static void resetBots() {

		for (Bot botL : geneticAlgorithm.population) {
			SimulationBot bot = (SimulationBot) botL;
			bot.setIterations(bot.getIterations() + 1);
			bot.setScore(0);
			bot.setPosition(SandboxSettings.botSpawnPosition);
			bot.setDir(SandboxSettings.botUpVector);
			bot.resetVelocity();
		}
	}

	static JSlider shiftSlider;
	static JSlider zoomSlider;
	static JLabel textLabel;
	static JToggleButton toggleButton;
	static SimulationScreen simulationScreen;

	public static SimulationScreen initScreen() {
		simulationScreen = new SimulationScreen();

		JFrame f = new JFrame("Workshop");

		JPanel panel = new JPanel(new BorderLayout());

		shiftSlider = new JSlider(JSlider.HORIZONTAL, 1, 100, 1);
		shiftSlider.setMajorTickSpacing(0);
		shiftSlider.setMinorTickSpacing(0);
		shiftSlider.setPaintTicks(true);
		shiftSlider.setPaintLabels(true);

		textLabel = new JLabel("Waiting for something to happen", JLabel.CENTER);
		textLabel.setFont(textLabel.getFont().deriveFont(16.0f));
		panel.add(textLabel, BorderLayout.NORTH);

		panel.add(simulationScreen, BorderLayout.CENTER);

		zoomSlider = new JSlider(JSlider.HORIZONTAL, 1, 80, 45);
		zoomSlider.setMajorTickSpacing(0);
		zoomSlider.setMinorTickSpacing(0);
		zoomSlider.setPaintTicks(true);
		zoomSlider.setPaintLabels(true);
		// zoomSlider.addChangeListener(bs);

		JButton button = new JButton("Plot score");
		button.addActionListener(e -> generatePlot());

		  toggleButton = new JToggleButton("Best only");
        toggleButton.addActionListener(e -> {
            // Add your toggle button action here
            if (toggleButton.isSelected()) {
                // Toggle button is selected
                textLabel.setText("Toggle button is selected");
            } else {
                // Toggle button is not selected
                textLabel.setText("Toggle button is not selected");
            }
        });

		// Use FlowLayout for the panel containing the button
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));

		buttonPanel.add(button);
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(new JLabel("Zoom"));
		buttonPanel.add(zoomSlider);
		buttonPanel.add(new JLabel(" "));
		buttonPanel.add(new JLabel("Simulation Speed"));
		buttonPanel.add(shiftSlider);
		buttonPanel.add(toggleButton);
		panel.add(buttonPanel, BorderLayout.WEST);

		panel.add(simulationScreen, BorderLayout.CENTER);

		f.add(panel);
		f.setSize(1000, 800);
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setResizable(true);
		f.setVisible(true);
		f.setEnabled(true);
		f.setAutoRequestFocus(true);
		f.addKeyListener(simulationScreen);
		f.pack();

		return simulationScreen;
	}

	public static void generatePlot() {
		if (plot != null) {
			plot.frame.setVisible(true);
			plot.frame.toFront();
			return;
		}
		double[] array = new double[plotScores.size()];

		// Copy elements from ArrayList to the array
		for (int i = 0; i < plotScores.size(); i++) {
			array[i] = plotScores.get(i);
		}
		plot = new Scatterplot(array, plotColors);
	}

}
