package sandbox;

import utils.Vector2;

public interface Bot extends Comparable<Bot>{

	public Vector2 getPosition();
	public Vector2 getVelocity();
	public double getAngle();
	public double getAngularMomentum();
	
	public double getAveragedScore();
	public double getScore();
	public void setScore(double score);
	public void addScore(double score);
	
	public void setCapsuleWeight(double weight);
	public void addThruster(Thruster t);
	
	public Bot clone();
	public void assemble();
	
	public double[] getNeuralWeights();
	public void setNeuralWeights(double[] weights);
	
	public double getMutationPower();
	public void setMutationPower(double power);
	public double getMutationChance();
	public void setMutationChance(double power);
}