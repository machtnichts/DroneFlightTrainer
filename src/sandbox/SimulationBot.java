package sandbox;

import java.awt.Color;
import java.util.ArrayList;
import neural.NeuralNetworkSimple;
import utils.Vector2;
import workshop.SandboxSettings;

public class SimulationBot implements Bot {

	private final static Vector2 yVector = new Vector2(0,1);
	private Color color = Color.getHSBColor((float) Math.random(), 1, 0.8F);
	private Vector2 pos;
	private Vector2 startPos;
	private Vector2 dir;
	private Vector2 velocity;
	private double momentum;
	private double weight;
	private double totalWeight;
	private double lastScore = 0;
	private double score = 0;
	private double iterations = 0;

	ArrayList<Thruster> trusters = new ArrayList<Thruster>();
	public NeuralNetworkSimple neuralNet;

	public double mutationChance = 0.5;
	public double mutationPower = 1;

	public SimulationBot(Vector2 position, Vector2 upDirection) {
		this.pos = position.clone();
		this.dir = upDirection.clone();
		this.weight = 1;

		totalWeight = calcTotalWeight();
		velocity = new Vector2(0, 0);

	}

	public void assemble() {
		calcNeuralNet();
	}

	public Bot clone() {
		SimulationBot bot = new SimulationBot(pos, dir);
		bot.score = score;
		bot.lastScore = lastScore;
		bot.iterations = iterations;
		bot.mutationChance = mutationChance;
		bot.mutationPower = mutationPower;
		bot.color = shiftHue(color, 0.02F);
		for (Thruster thru : trusters) {
			bot.addThruster(((Thruster) thru).clone());
		}

		bot.neuralNet = neuralNet.clone();

		return bot;
	}

	public void turnAroundMiddle(double d) {
		Vector2 cent = getAbsoluteCenterOfMass();
		Vector2 turn = pos.sub(cent);
		turn = Vector2.turnDeg(turn, d);
		pos = turn.add(cent);
		dir = Vector2.turnDeg(dir, d);
	}

	public void addVelocity(Vector2 vel) {
		if (velocity.add(vel).magnitude() < 100) {
			velocity = velocity.add(vel);
		}
	}

	public void addMomentum(double mom) {
		momentum += mom;
	}

	public void move(double deltaTime) {
		pos = pos.add(velocity.mult(deltaTime));
		turnAroundMiddle(Math.toDegrees(momentum * deltaTime));
	}

	public void resetVelocity() {
		velocity = new Vector2(0, 0);
		momentum = 0;
	}

	public Vector2 getPosition() {

		return pos;
	}

	public void setPosition(Vector2 pos) {
		// System.out.println(""+pos.toString());
		this.pos = pos.clone();

	}

	public Vector2 getDir() {
		return dir;
	}

	public void setDir(Vector2 dir) {
		this.dir = dir.clone();
	}

	public double getWeight() {
		return weight;
	}

	public void setCapsuleWeight(double d) {
		weight = d;

	}

	public int getThrusterCount() {
		return trusters.size();
	}

	public Thruster getTruster(int index) {
		return trusters.get(index);
	}

	public ArrayList<Thruster> getAllTrusters() {

		return trusters;
	}

	public void removeTruster(int index) {
		trusters.remove(index);
		totalWeight = calcTotalWeight();

	}

	public void removeTruster(Thruster truster) {
		trusters.remove(truster);
		totalWeight = calcTotalWeight();

	}

	public void addThruster(Thruster truster) {

		truster.setBot(this);
		trusters.add(truster);
		totalWeight = calcTotalWeight();

	}

	public double getTotalWeight() {

		return totalWeight;

	}

	public double calcTotalWeight() {
		double wt = weight;

		for (Thruster t : getAllTrusters()) {

			wt += t.getWeight();

		}
		return wt;
	}

	public void calcNeuralNet() {
		// new NeuralNetworkOld(new int[] {6,10,10,10,getThrusterCount()}
		// neuralNet = new NeuralNetwork(6, 1, 6,getThrusterCount());

		neuralNet = new NeuralNetworkSimple(6, SandboxSettings.hiddenLayerSize, getThrusterCount());
	}

	public Vector2 getCenter() {
		Vector2 center = new Vector2(0, 0);
		for (Thruster t : getAllTrusters()) {

			center = center.add(t.getPos());
		}
		return center.div(getAllTrusters().size() + 1);
	}

	public Vector2 getAbsoluteCenter() {

		Vector2 center = pos;
		double angle = Vector2.getAngle(getDir(), yVector);
		for (Thruster t : getAllTrusters()) {
			center = center.add(t.getAbsolutePos2Fast4U(pos,angle));
		}
		return center.div(getAllTrusters().size() + 1);

	}

	public Vector2 getAbsoluteCenterOfMass() {
		Vector2 subVec = getAbsoluteCenter();
		Vector2 centerOfMass = subVec;

		centerOfMass = centerOfMass.add(pos.sub(subVec).mult((getWeight() / totalWeight)));

		double angle = Vector2.getAngle(getDir(), yVector);
		for (Thruster t : getAllTrusters()) {

			centerOfMass = centerOfMass.add(t.getAbsolutePos2Fast4U(getPosition(),angle).sub(subVec).mult((t.getWeight() / totalWeight)));
		}

		return centerOfMass;
	}

	public Vector2 getVelocity() {
		return velocity;
	}

	public double getMomentum() {
		return momentum;
	}

	public void setWeight(float weight) {
		this.weight = weight;
	}

	@Override
	public int compareTo(Bot other) {
		if (SandboxSettings.scoreSetting != ScoreSetting.BASIC_SCORE) {
			if (other.getAveragedScore() > getAveragedScore()) {
				return 1;
			}
			if (other.getAveragedScore() < getAveragedScore()) {
				return -1;
			}
			return 0;
		} else {
			if (other.getScore() > getScore()) {
				return 1;
			}
			if (other.getScore() < getScore()) {
				return -1;
			}
			return 0;
		}
	}

	public static Color shiftHue(Color originalColor, float hueShift) {

		float[] hsb = Color.RGBtoHSB(
				originalColor.getRed(),
				originalColor.getGreen(),
				originalColor.getBlue(),
				null);

		float newHue = (hsb[0] + hueShift) % 1.0f;
		if (newHue < 0) {
			newHue += 1.0f;
		}

		return Color.getHSBColor(newHue, hsb[1], hsb[2]);
	}

	public Vector2 getStartPos() {
		return startPos;
	}

	public void setStartPos(Vector2 startPos) {
		this.startPos = startPos;
	}

	public double getLastScore() {
		return lastScore;
	}

	public void setLastScore(double lastScore) {
		if (Main.insideSort.get()) {
			System.out.println("the evil thread: " + Thread.currentThread());
			new Exception("Evil stack trace").printStackTrace();
		}
		this.lastScore = lastScore;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public double getIterations() {
		return iterations;
	}

	public void setIterations(double iterations) {
		this.iterations = iterations;
	}

	public ArrayList<Thruster> getTrusters() {
		return trusters;
	}

	public void setTrusters(ArrayList<Thruster> trusters) {
		this.trusters = trusters;
	}

	public NeuralNetworkSimple getNeuralNet() {
		return neuralNet;
	}

	public void setNeuralNet(NeuralNetworkSimple neuralNet) {
		this.neuralNet = neuralNet;
	}

	public double getMutationChance() {
		return mutationChance;
	}

	public void setMutationChance(double mutationChance) {
		this.mutationChance = mutationChance;
	}

	public double getMutationPower() {
		return mutationPower;
	}

	public void setMutationPower(double mutationPower) {
		this.mutationPower = mutationPower;
	}

	public void setVelocity(Vector2 velocity) {
		this.velocity = velocity;
	}

	public void setMomentum(double momentum) {
		this.momentum = momentum;
	}

	public void setTotalWeight(double totalWeight) {
		this.totalWeight = totalWeight;
	}

	@Override
	public double getAngle() {
		return Vector2.SignedAngle(dir, new Vector2(0, 1));
	}

	@Override
	public double getAngularMomentum() {
		return momentum;
	}

	@Override
	public void addScore(double score) {
		this.score += score;

	}

	public double getScore() {
		return score;
	}

	public void setScore(double score) {
		this.score = score;
	}

	@Override
	public double[] getNeuralWeights() {
		return neuralNet.weights;
	}

	@Override
	public void setNeuralWeights(double[] weights) {
		neuralNet.weights = weights;

	}

	@Override
	public double getAveragedScore() {
		return lastScore;
	}

}
