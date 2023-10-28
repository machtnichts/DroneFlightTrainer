package sandbox;

import java.util.ArrayList;

import utils.Vector2;

public class Bot implements Comparable<Bot> {
	
	Vector2 pos;
	Vector2 startPos;
	Vector2 dir;
	Vector2 velocity;
	double momentum;
	double weight;
	double totalWeight;
	double score = 0;
	
	ArrayList<Thruster> trusters = new ArrayList<Thruster>();
	NeuralNetwork neuralNet;
	
	public Bot(Vector2 position,Vector2 upDirection) {
		this.pos = position.clone();
		this.dir = upDirection.clone();
		this.weight = 5;
		totalWeight = calcTotalWeight();
		velocity = new Vector2(0,0);
		
	}
	
	public void assemble() {
		calcNeuralNet();
	}

	public Bot clone() {
		Bot bot = new Bot(pos,dir);
		for (Thruster thru : trusters) {
			bot.addTruster(((Thruster)thru).clone());
		}
		
		bot.neuralNet = new NeuralNetwork(neuralNet.toString());
		
		return bot;
	}
	
	public void turnAroundMiddle(double d) {
		Vector2 cent = getAbsoluteCenterOfMass();
		Vector2 turn = pos.sub(cent);
		turn = Vector2.turnDeg(turn, d);
		pos = turn.add(cent);
		dir = Vector2.turnDeg(dir,d);
	}

	public void addVelocity(Vector2 vel) {
		if (velocity.add(vel).magnitude()< 100) {
			velocity = velocity.add(vel);
		}
	}
	
	public void addMomentum(double mom) {
		momentum += mom;
	}
	
	public void move(double deltaTime) {
		pos = pos.add(velocity.mult(deltaTime));
		turnAroundMiddle(Math.toDegrees(momentum*deltaTime));
	}
	
	public void resetVelocity() {
		velocity = new Vector2(0,0);
		momentum = 0;
	}
	
	public Vector2 getPos() {
		
		return pos;
	}

	public void setPos(Vector2 pos) {
		//System.out.println(""+pos.toString());
		this.pos = pos.clone();
		
	}

	public Vector2 getDir() {
		return dir;
	}

	public void setDir(Vector2 dir) {
		this.dir = dir.clone();	
	}

	public double getWeight() {
		// TODO Auto-generated method stub
		return weight;
	}

	
	public void setWeight(double d) {
		weight = d;
		
	}


	public int getThrusterCount() {
		// TODO Auto-generated method stub
		return trusters.size();
	}


	public Thruster getTruster(int index) {
		// TODO Auto-generated method stub
		return trusters.get(index);
	}

	public ArrayList<Thruster> getAllTrusters() {
		// TODO Auto-generated method stub
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

	public void addTruster(Thruster truster) {
		// TODO Auto-generated method stub
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
		neuralNet = new NeuralNetwork(new int[] {6,10,10,10,getThrusterCount()});
	}
	
	

	public double getAngle() {
		return Vector2.SignedAngle(dir, new Vector2(0,1));
	}
	
	public Vector2 getCenter() {
		Vector2 center = new  Vector2(0,0);
		for (Thruster t : getAllTrusters()) {
			
			center = center.add(t.getPos());
		}
		return center.div(getAllTrusters().size()+1);
	}
	public Vector2 getAbsoluteCenter() {
		
		
		Vector2 center =pos;
		for (Thruster t : getAllTrusters()) {
			
			center = center.add(t.getAbsolutePos());
		}
		return center.div(getAllTrusters().size()+1);
		
	}
	
	public Vector2 getAbsoluteCenterOfMass() {
		Vector2 subVec = getAbsoluteCenter();
		Vector2 centerOfMass = getAbsoluteCenter();
		
		
		centerOfMass = centerOfMass.add(pos.sub(subVec).mult((getWeight()/totalWeight)));
		
		for (Thruster t : getAllTrusters()) {
			
			centerOfMass = centerOfMass.add(t.getAbsolutePos().sub(subVec).mult((t.getWeight()/totalWeight)));
		}
		
		return centerOfMass;
	}
	
	public Vector2 getVelocity() {
		return velocity;
	}
	public double getMomentum() {
		return momentum;
	}

	
	@Override
	public int compareTo(Bot other) {
		if (other.score > score) {
			return 1;
		}
		if (other.score < score) {
			return -1;
		}
		return 0;
		
	}

}
