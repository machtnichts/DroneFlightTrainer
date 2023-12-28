package sandbox;



import utils.Vector2;

public class Physics {

	
	
	

	public static double gravity = -9.81D;

	public static void calcPhysics(SimulationBot bot,double deltaTime) {
	    calcGravity(bot,deltaTime);
		calcTrust(bot,deltaTime);
		bot.move(deltaTime);
	}
	
	
	public static void calcGravity(SimulationBot bot,double deltaTime) {
		bot.addVelocity(new Vector2(0,gravity).mult(deltaTime));
	}
	
	
	public static void calcTrust(SimulationBot bot,double deltaTime) {
		
		double totalAngle = 0;
		Vector2 center = bot.getAbsoluteCenterOfMass();
		
		double totalMomentOfInertia = 0;
		double angle = Vector2.getAngle(bot.getDir(), new Vector2(0,1));
		for (Thruster t : bot.getAllTrusters()) {

			totalMomentOfInertia += t.getWeight() * Math.pow((center.sub(t.getAbsolutePos2Fast4U(bot.getPosition(),angle)).magnitude()),2);

			
			Vector2 vel = t.getAbsoluteDirection().mult(-t.getCurrentTrust()*t.getMaxTrust());
			Vector2 aV = t.getAbsolutePos2Fast4U(bot.getPosition(),angle).sub(center);
			
			if (totalMomentOfInertia != 0)
			  totalAngle += Vector2.crossProduct(vel, aV)/totalMomentOfInertia;
			
		
			bot.addVelocity(vel.mult(deltaTime/bot.getTotalWeight()));
		}
		
		bot.addMomentum(totalAngle*deltaTime);
	}
	
	
	
}
