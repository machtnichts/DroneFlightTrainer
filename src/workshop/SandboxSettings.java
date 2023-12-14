package workshop;

import sandbox.SimulationBot;
import sandbox.Bot;
import sandbox.ScoreSetting;
import sandbox.TargetSetting;
import sandbox.Thruster;
import utils.Vector2;

public class SandboxSettings {

	// Settings for Simulation. Have fun!
	public static Vector2 botUpVector = new Vector2(0,1);
	public static Vector2 botSpawnPosition = new Vector2(0,0);
	public static Vector2 botGoalPosition = new Vector2(0,400);
	public static TargetSetting targetSetting = TargetSetting.CANT_CATCH_ME;
	public static ScoreSetting scoreSetting = ScoreSetting.EXPONENTIALY_WEIGHTED_SCORE;
	public static int simulationSteps = 500;
	public static float additionalSimulationStepsPerGeneration = 0.02F;
	public static int hiddenLayerSize = 2;
	
	
	
	
	public static Bot createBot() {


		Bot bot = new SimulationBot(botSpawnPosition,botUpVector);
		
		bot.setWeight(1);
		// Basic drone with two thrusters
		bot.addThruster(new Thruster(new Vector2(60, 0), new Vector2(0, -1), 150, 5));
		bot.addThruster(new Thruster(new Vector2(-60, 0), new Vector2(0, -1), 150, 5));
		
		
		/*
		 //Balance Drone
		bot.addTruster(new Thruster(new Vector2(0, -70), new Vector2(0, -1), 900, 50));
		bot.addTruster(new Thruster(new Vector2(20, -150), new Vector2(1, 0), 85, 5));
		bot.addTruster(new Thruster(new Vector2(-20,-150), new Vector2(-1, 0), 85, 5));
		*/
	
		/*
		  //Spinning drone
		Vector2 ruler = new Vector2(0,65);
		for (int i = 360;i > 0;i -= 360/6) {
			bot.addTruster(new Thruster(Vector2.turnDeg(ruler, i), Vector2.turnDeg(ruler, i+90), 305, 5));
			
		}
	 	*/

		bot.assemble();
		return bot;

	}
}
