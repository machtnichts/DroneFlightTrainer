package sandbox;

import utils.Vector2;

public class Thruster {
	SimulationBot bot;
	Vector2 pos;
	Vector2 dir;
	double maxTrust;
	double currentTrust = 1;
	double weight;
	
	
	public Thruster(Vector2 pos,Vector2 dir,double maxTrust,double weight) {
		
		
		this.pos = pos;
		this.dir = dir;
		this.maxTrust = maxTrust;
		this.weight = weight;
		
	}
	
	public Thruster clone() {
		return new Thruster(pos,dir,maxTrust,weight);
		
	}
	
	
	public void setBot(SimulationBot bot) {
		this.bot = bot;
	}
	
	public Vector2 getPos() {
		
		return pos;
	}

	public Vector2 getAbsolutePos() {
		Vector2 botPos = bot.getPosition();
		
		return botPos.add(Vector2.turnDeg(pos, Vector2.getAngle(bot.getDir(), new Vector2(0,1))));
	}

	public Vector2 getAbsolutePos2(Vector2 botPosition, double botAngle) {
		return botPosition.add(Vector2.turnDeg(pos, botAngle));
	}

	public Vector2 getDirection() {
		return dir;
	}

	public double getMaxTrust() {
		return maxTrust;
	}

	public double getCurrentTrust() {
		return currentTrust;
	}

	public double getWeight() {
		return weight;
	}

	public void setPos(Vector2 pos) {
		this.pos = pos;	
	}

	public void setDirection(Vector2 dir) {
		this.dir = dir;	
	}

	public void setMaxTrust(double trust) {
		
			maxTrust = trust;	
		
			
	}

	public void setCurrentTrust(double trust) {
		
		if (trust > 1)
			trust = 1;
		if (trust < 0)
			trust = 0;
			currentTrust = trust;
		
			
	}

	public void setWeight(double weight) {
		this.weight = weight;
	}



	public Vector2 getAbsoluteDirection() {
		return Vector2.turnDeg(dir,Vector2.SignedAngle(bot.getDir(), new Vector2(0,1)));
	}

}
