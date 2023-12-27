package sandbox;

import utils.RunnerTask;
import utils.Vector2;
import workshop.GeneticAlgorithm;
import workshop.SandboxSettings;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.*;

public class Main {

  private final static String muyPalabas = "Yo hablo espanol. kind of.";

  private final static Instant start = Instant.now();
  private final static NumberFormat numberFormat = new DecimalFormat("#0.000", new DecimalFormatSymbols(Locale.ROOT));
  private final static NumberFormat numberFormat6 = new DecimalFormat("#0.000000", new DecimalFormatSymbols(Locale.ROOT));
  public static SimulationScreen screen;
  public static GeneticAlgorithm geneticAlgorithim = new GeneticAlgorithm();
  public static ArrayList<Double> plotScores = new ArrayList<>();
  public static ArrayList<Color> plotColors = new ArrayList<>();
  public static Scatterplot plot;
  // Buggs : Concurrent modifier in Simulation screen
  public static Random random;


  static int gen = 0;
  static SimulationBot lastBest;
  /*

    public static void sortPopulation() {
      List<SimulationBot> bots = new ArrayList<SimulationBot>();

      for (Bot b : geneticAlgorithim.population) {
        bots.add((SimulationBot) b);
      }

      Collections.sort(bots);
      geneticAlgorithim.population.clear();
      for (Bot b : bots) {
        geneticAlgorithim.population.add(b);
      }

    }
  */
  static double lastScore;
  static JSlider shiftSlider;
  static JSlider zoomSlider;
  static JLabel textLabel;
  private static Instant lastUpdate = Instant.now();

  public static void main(String[] args) {
    random = new Random();
    geneticAlgorithim.initPopulation();
    screen = initScreen();

    new RunnerTask(0, 1) {
      int currentTick = 0;
      int genNumber = 0;
      Vector2 targetVel = new Vector2(0, 0);

      private static void runSimulationFrame(SimulationBot bot) {
        double angle = (Vector2.SignedAngle(bot.getDir(), new Vector2(0, 1))) / 180;
        double xToTarget = (bot.getPosition().getX() - SandboxSettings.botGoalPosition.getX()) / 500D;
        double yToTarget = (bot.getPosition().getY() - SandboxSettings.botGoalPosition.getY()) / 500D;

        double[] input = new double[]{angle, xToTarget, yToTarget, bot.getVelocity().getX(), bot.getVelocity().getY(), bot.getMomentum()};

        double[] res = bot.neuralNet.calculate(input);

        for (int i = 0; i < bot.getThrusterCount(); i++) {
          bot.getTruster(i).setCurrentTrust(res[i]);
        }

        geneticAlgorithim.evaluateBot(bot);

        Physics.calcPhysics(bot, 0.1D);
      }

      public void run() {

        int fast = shiftSlider.getValue();
        for (int a = 0; a < fast; a++) {

          geneticAlgorithim.population.parallelStream().filter(SimulationBot.class::isInstance).map(SimulationBot.class::cast).forEach(bot -> runSimulationFrame(bot));

          if (SandboxSettings.targetSetting == TargetSetting.CHANGE_TARGET_DURING_RUN) {
            if (currentTick % 300 == 0) {
              SandboxSettings.botGoalPosition = new Vector2((random.nextDouble() * 2 - 1) * 700, (random.nextDouble() * 2 - 1) * 700);
            }
          }
          if (SandboxSettings.targetSetting == TargetSetting.CANT_CATCH_ME) {

            if (currentTick % 300 == 0) {
              SandboxSettings.botGoalPosition = new Vector2((random.nextDouble() * 2 - 1) * 700, (random.nextDouble() * 2 - 1) * 700);
            }
            targetVel = targetVel.add(new Vector2(random.nextDouble() * 2 - 1, random.nextDouble() * 2 - 1).mult(0.1F));
            SandboxSettings.botGoalPosition = SandboxSettings.botGoalPosition.add(targetVel);
            if (SandboxSettings.botGoalPosition.magnitude() > 1200) {
              targetVel = new Vector2(0, 0);
              SandboxSettings.botGoalPosition = new Vector2((random.nextDouble() * 2 - 1) * 400, (random.nextDouble() * 2 - 1) * 400);
            }

          }
          if (SandboxSettings.targetSetting == TargetSetting.MOVE_CIRCULAR) {
            SandboxSettings.botGoalPosition = Vector2.turnDeg(SandboxSettings.botGoalPosition, 0.5F);
          }

          if (currentTick > SandboxSettings.simulationSteps + gen * SandboxSettings.additionalSimulationStepsPerGeneration) {
            if (geneticAlgorithim.population.size() <= 0) return;

            calculateLastScore();

            currentTick = 0;
            genNumber += 1;
            sortPopulation();
            lastBest = (SimulationBot) geneticAlgorithim.population.get(0);
            geneticAlgorithim.calculateNextPopulation(gen);


            updatePlotUI();

            gen++;
            resetBots();
            if (SandboxSettings.targetSetting == TargetSetting.MOVE_CIRCULAR)
              SandboxSettings.botGoalPosition = Vector2.turnDeg(SandboxSettings.botGoalPosition, Math.random() * 360F);

            if (SandboxSettings.targetSetting == TargetSetting.CHANGE_TARGET_EACH_RUN)
              SandboxSettings.botGoalPosition = new Vector2((random.nextDouble() * 2 - 1) * 400, (random.nextDouble() * 2 - 1) * 400);

          }

          currentTick++;
        }
        if (fast < shiftSlider.getMaximum() - 1) screen.paint(screen.getGraphics());
      }

      private void updatePlotUI() {
        if (plot != null) {
          plot.setData(plotScores, plotColors);
          plot.update();
        }

        Instant now = Instant.now();
        long msSinceUpdate = Duration.between(lastUpdate, now).toMillis();
        if (msSinceUpdate >= 1000) //every one second
        {
          lastUpdate = now;
          long durationInMs = Duration.between(start, Instant.now()).toMillis();

          long generationsPerSec = genNumber * 1000 / durationInMs;
          long msPerGeneration = durationInMs / genNumber;
          String addText = msPerGeneration + " ms/gen ( or "+generationsPerSec +" gen/sec)";

          if (SandboxSettings.scoreSetting == ScoreSetting.BASIC_SCORE) {
            plotScores.add(lastBest.getScore());
            plotColors.add(lastBest.getColor());
            textLabel.setText("Generation " + gen + " | Best Score " + numberFormat.format(lastBest.getScore()) + " Mutation [" + numberFormat6.format(lastBest.mutationChance) + " | " + numberFormat6.format(lastBest.mutationPower) + "] " + addText);

          } else {
            plotScores.add(lastBest.getLastScore());
            plotColors.add(lastBest.getColor());
            textLabel.setText("Generation " + gen + " | Best Score " + numberFormat.format(lastBest.getLastScore()) + " Mutation [" + numberFormat6.format(lastBest.mutationChance) + " | " + numberFormat6.format(lastBest.mutationPower) + "] " + addText);
          }
        }
      }
    };

  }

  public static void sortPopulation() {
    List<SimulationBot> bots = new ArrayList<>(geneticAlgorithim.population.stream().filter(SimulationBot.class::isInstance).map(SimulationBot.class::cast).toList());
    try {
      Collections.sort(bots);
    } catch (Throwable t) {
      System.out.println("shit happens:");
      bots.stream().forEach(x -> System.out.println(x.getLastScore()));
    }
    geneticAlgorithim.population = new ArrayList<>(bots);
  }

  public static void calculateLastScore() {
    for (Bot botL : geneticAlgorithim.population) {
      SimulationBot bot = (SimulationBot) botL;
      if (SandboxSettings.scoreSetting == ScoreSetting.EXPONENTIALY_WEIGHTED_SCORE) {
        if (bot.getLastScore() == 0) {
          bot.setLastScore(bot.getScore());
        } else {
          bot.setLastScore(bot.getLastScore() * 0.7F + bot.getScore() * 0.3F);
        }

      } else {
        bot.setLastScore((bot.getLastScore() * bot.getIterations() + bot.getScore()) / (bot.getIterations() + 1D));
      }

    }
  }

  public static void resetBots() {

    for (Bot botL : geneticAlgorithim.population) {
      SimulationBot bot = (SimulationBot) botL;
      bot.setIterations(bot.getIterations() + 1);
      bot.setScore(0);
      bot.setPosition(SandboxSettings.botSpawnPosition);
      bot.setDir(SandboxSettings.botUpVector);
      bot.resetVelocity();
    }
  }

  public static SimulationScreen initScreen() {
    SimulationScreen bs = new SimulationScreen();
    JFrame f = new JFrame("Workshop");

    JPanel panel = new JPanel(new BorderLayout());

    shiftSlider = new JSlider(JSlider.HORIZONTAL, 1, 150, 1);
    shiftSlider.setMajorTickSpacing(0);
    shiftSlider.setMinorTickSpacing(0);
    shiftSlider.setPaintTicks(true);
    shiftSlider.setPaintLabels(true);
    shiftSlider.setValue(shiftSlider.getMaximum());

    textLabel = new JLabel("Waiting for something to happen", JLabel.CENTER);
    textLabel.setFont(textLabel.getFont().deriveFont(16.0f));
    panel.add(textLabel, BorderLayout.NORTH);


    panel.add(bs, BorderLayout.CENTER);


    zoomSlider = new JSlider(JSlider.HORIZONTAL, 1, 80, 45);
    zoomSlider.setMajorTickSpacing(0);
    zoomSlider.setMinorTickSpacing(0);
    zoomSlider.setPaintTicks(true);
    zoomSlider.setPaintLabels(true);
    //zoomSlider.addChangeListener(bs);

    JButton button = new JButton("Plot score");
    button.addActionListener(e -> generatePlot());

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
    panel.add(buttonPanel, BorderLayout.WEST);

    panel.add(bs, BorderLayout.CENTER);

    f.add(panel);
    f.setSize(1000, 800);
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
    // move this to dumpBest();
    var winner = geneticAlgorithim.population.stream().findFirst();
    if (winner.isPresent()) {
      var bot = winner.get();

      try {
        Path f = Files.createTempFile("drone", "winner.weights.txt");
        Files.writeString(f, Arrays.toString(bot.getNeuralWeights()));
        System.out.println("Best bot dumped to " + f);
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
    }

    if (plot == null) {
      double[] array = new double[plotScores.size()];

      // Copy elements from ArrayList to the array
      for (int i = 0; i < plotScores.size(); i++) {
        array[i] = plotScores.get(i);
      }
      plot = new Scatterplot(array, plotColors);
    } else {
      plot.frame.setVisible(true);
      plot.frame.toFront();
    }
  }

}
