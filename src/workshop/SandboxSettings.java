package workshop;

import sandbox.SimulationBot;
import sandbox.Bot;
import sandbox.ScoreSetting;
import sandbox.TargetSetting;
import sandbox.Thruster;
import utils.Vector2;

public class SandboxSettings {

	// Settings for Simulation. Have fun!
	/* Coordinates are set up like in standard math */
	
	/* Direction of the Bot when spawned | 0 1 is up */
	public static Vector2 botUpVector = new Vector2(0,1);

	/* Position of the bot when spawned | 0 0 is the middle of the screen */
	public static Vector2 botSpawnPosition = new Vector2(0,0);

	/* Position of the goal when spawned (If it doesnt move) */
	public static Vector2 botGoalPosition = new Vector2(0,400);

	/* Targetsetting can make the target move (harder to solve)*/
	public static TargetSetting targetSetting = TargetSetting.STATIC_TARGET;

	/* How the score is calculated: Basic Score uses the score of the last simulation to sort the population
	 * while Exponetialy weigthed keeps a exponentialy weighted average over all past scores
	 * This helps with moving targets
	*/
	public static ScoreSetting scoreSetting = ScoreSetting.EXPONENTIALY_WEIGHTED_SCORE;

	/* Amount of steps before a simulation ends */
	public static int simulationSteps = 500;

	/* A factor that inscreases the simulationSteps value over the generation count*/
	public static float additionalSimulationStepsPerGeneration = 0.02F;

	/* The scale of the hidden layer of the neural network */
	public static int hiddenLayerSize = 2;
	
	
	
	/*
	 *  This Function creates a new Bot with an empty neural network
	 */
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
