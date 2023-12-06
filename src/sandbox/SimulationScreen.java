package sandbox;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;


import utils.Vector2;
import workshop.GeneticAlgorithim;
import workshop.SandboxSettings;

public class SimulationScreen extends Canvas implements KeyListener{
	
	private static final int RAD = 10;
	private static final int SQR = 10;
	
	
	
	public SimulationScreen() {
	
	}
	
	public static int i = 0;

	public static double  disp_scale = 0.5F;
	
	
	public void paint(Graphics g) {
		
	
		BufferedImage bi = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) bi.getGraphics();
		g2d.setColor(Color.green);
		drawOval(g2d, SandboxSettings.botGoalPosition.mult(disp_scale), 5);
		
		
	
			int disp = 1;
		for (Bot bot : Main.geneticAlgorithim.population) {
		
			disp -= 1;
			if (disp <= 0) 
				break;
		
			drawBot(bot,bot.color,g2d);
			
		}
		drawBot(Main.geneticAlgorithim.population.get(0),Color.white,g2d);
	
		
		g.drawImage(bi, 0, 0, null);
		
	}
	
	
	private void drawBot(Bot bot,Color c,Graphics2D g2d) {
		g2d.setColor(c);
		drawOval(g2d, bot.getPos().mult(disp_scale), RAD);
		g2d.setColor(Color.GREEN);
		drawOval(g2d, bot.getAbsoluteCenter().mult(disp_scale), 4);
		g2d.setColor(Color.PINK);
		drawOval(g2d, bot.getAbsoluteCenterOfMass().mult(disp_scale), 4);
		
		g2d.setColor(c);
		
		drawLine(g2d,Vector2.add( bot.getPos().mult(disp_scale), (new Vector2(bot.getAngle())).getNormalized().mult(-RAD)),Vector2.add( bot.getPos(), (new Vector2(bot.getAngle())).getNormalized().mult(RAD)).mult(disp_scale) );
		for (Thruster t : bot.getAllTrusters()) {
			Vector2 pos = t.getAbsolutePos();
			//Vector2 ruler = Vector2.turnDeg(new Vector2(0, SQR), Vector2.getAngle(new Vector2(0, 1),t.getDirection()));
			Vector2 ruler = t.getAbsoluteDirection().getNormalized().mult(SQR);
			g2d.setColor(c);
		
			drawLine(g2d,Vector2.add(pos ,Vector2.turnDeg(ruler, -30)).mult(disp_scale), Vector2.add(pos ,Vector2.turnDeg(ruler, -30)).mult(disp_scale));
			drawLine(g2d,Vector2.add(pos ,Vector2.turnDeg(ruler, -30)).mult(disp_scale), Vector2.add(pos ,Vector2.turnDeg(ruler, -30)).mult(disp_scale));
			drawLine(g2d,Vector2.add(pos ,Vector2.turnDeg(ruler, -30)).mult(disp_scale),Vector2.add(pos ,Vector2.turnDeg(ruler, -150)).mult(disp_scale));
			drawLine(g2d,Vector2.add(pos ,Vector2.turnDeg(ruler, -150)).mult(disp_scale),Vector2.add(pos ,Vector2.turnDeg(ruler, 150)).mult(disp_scale));
			drawLine(g2d,Vector2.add(pos ,Vector2.turnDeg(ruler, 30)).mult(disp_scale),Vector2.add(pos ,Vector2.turnDeg(ruler, 150)).mult(disp_scale));
			Vector2 top = Vector2.add(pos, Vector2.add(Vector2.turnDeg(ruler, -150), Vector2.turnDeg(ruler, 150)).mult(0.5));
			//Vector2 top = pos;
			drawLine(g2d,Vector2.add(Vector2.add(bot.getPos().mult(-1),top).getNormalized().mult(RAD), bot.getPos()).mult(disp_scale), top.mult(disp_scale));
			int uga = (int)(255*t.getCurrentTrust());
			g2d.setColor(new Color(255,255-uga,0));
			drawLine(g2d,pos.mult(disp_scale), Vector2.add(pos ,ruler.mult((t.getCurrentTrust()*t.getMaxTrust())/40)).mult(disp_scale));
		}
	}
	
	private void drawLine(Graphics2D g2d, Vector2 v1,Vector2 v2) {
		Vector2 shift = new Vector2(getWidth()/2F,getHeight()/2F);
		g2d.drawLine((int)v1.getX()+(int)shift.getX(), (int)-v1.getY()+(int)shift.getY(), (int)v2.getX()+(int)shift.getX(), (int)-v2.getY()+(int)shift.getY());
	}

	private void drawOval(Graphics2D g2d, Vector2 v1,double rad) {
		Vector2 shift = new Vector2(getWidth()/2F,getHeight()/2F);
		g2d.drawOval((int)(v1.getX()+(int)shift.getX()-rad) , (int)(-v1.getY()+(int)shift.getY()-rad), (int)rad*2 , (int)rad*2);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
	public static boolean qPressed = false;
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getKeyChar() =='q') {
			qPressed = true;
		}
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getKeyChar() =='q') {
			qPressed = false;
		}
	}
}
