package utils;



import java.util.Timer;
import java.util.TimerTask;

public class RunnerTask extends TimerTask {

	@Override
	public void run() {
		// TODO Auto-generated method stub
		
	}
	
	public RunnerTask(long start, long period) {
		Timer t = new Timer();
		t.scheduleAtFixedRate(this, start, period);
	}
}