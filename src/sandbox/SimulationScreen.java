package sandbox;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;

import utils.Vector2;

import workshop.SandboxSettings;

public class SimulationScreen extends Canvas implements KeyListener, MouseListener {

	private static final int RAD = 10;
	private static final int SQR = 10;

	public SimulationScreen() {
		addMouseListener(this);
	}

	public static int i = 0;

	public double disp_scale = 0.3F;

	public void paint(Graphics g) {

		disp_scale = (double) Main.zoomSlider.getValue() / 100D;
		BufferedImage bi = new BufferedImage(1000, 1000, BufferedImage.TYPE_INT_RGB);
		Graphics2D g2d = (Graphics2D) bi.getGraphics();
		g2d.setColor(Color.green);
		drawOval(g2d, SandboxSettings.botGoalPosition.mult(disp_scale), 2);

		int disp = 1000;
		if (!Main.toggleButton.isSelected()) {
			for (Bot botL : Main.geneticAlgorithm.population) {
				SimulationBot bot = (SimulationBot) botL;
				disp -= 1;
				if (disp <= 0)
					break;

				drawBot(bot, bot.getColor(), g2d);

			}
		}

		if (Main.geneticAlgorithm.population.size() > 0)
			drawBot((SimulationBot) Main.geneticAlgorithm.population.get(0), Color.white, g2d);

		g.drawImage(bi, 0, 0, null);

	}

	private void drawBot(SimulationBot bot, Color c, Graphics2D g2d) {
		g2d.setColor(c);
		drawOval(g2d, bot.getPosition().mult(disp_scale), RAD * disp_scale);
		g2d.setColor(Color.GREEN);
		drawOval(g2d, bot.getAbsoluteCenter().mult(disp_scale), 4 * disp_scale);
		g2d.setColor(Color.PINK);
		drawOval(g2d, bot.getAbsoluteCenterOfMass().mult(disp_scale), 4 * disp_scale);

		g2d.setColor(c);

		drawLine(g2d,
				Vector2.add(bot.getPosition(), (new Vector2(bot.getAngle())).getNormalized().mult(-RAD))
						.mult(disp_scale),
				Vector2.add(bot.getPosition(), (new Vector2(bot.getAngle())).getNormalized().mult(RAD))
						.mult(disp_scale));
		double angle = Vector2.getAngle(bot.getDir(), new Vector2(0,1));
		for (Thruster t : bot.getAllTrusters()) {
			Vector2 pos = t.getAbsolutePos2Fast4U(bot.getPosition(),angle);
			// Vector2 ruler = Vector2.turnDeg(new Vector2(0, SQR), Vector2.getAngle(new
			// Vector2(0, 1),t.getDirection()));
			Vector2 ruler = t.getAbsoluteDirection().getNormalized().mult(SQR);
			g2d.setColor(c);

			drawLine(g2d, Vector2.add(pos, Vector2.turnDeg(ruler, -30)).mult(disp_scale),
					Vector2.add(pos, Vector2.turnDeg(ruler, -30)).mult(disp_scale));
			drawLine(g2d, Vector2.add(pos, Vector2.turnDeg(ruler, -30)).mult(disp_scale),
					Vector2.add(pos, Vector2.turnDeg(ruler, -30)).mult(disp_scale));
			drawLine(g2d, Vector2.add(pos, Vector2.turnDeg(ruler, -30)).mult(disp_scale),
					Vector2.add(pos, Vector2.turnDeg(ruler, -150)).mult(disp_scale));
			drawLine(g2d, Vector2.add(pos, Vector2.turnDeg(ruler, -150)).mult(disp_scale),
					Vector2.add(pos, Vector2.turnDeg(ruler, 150)).mult(disp_scale));
			drawLine(g2d, Vector2.add(pos, Vector2.turnDeg(ruler, 30)).mult(disp_scale),
					Vector2.add(pos, Vector2.turnDeg(ruler, 150)).mult(disp_scale));
			Vector2 top = Vector2.add(pos,
					Vector2.add(Vector2.turnDeg(ruler, -150), Vector2.turnDeg(ruler, 150)).mult(0.5));
			// Vector2 top = pos;
			drawLine(g2d, Vector2
					.add(Vector2.add(bot.getPosition().mult(-1), top).getNormalized().mult(RAD), bot.getPosition())
					.mult(disp_scale), top.mult(disp_scale));
			int uga = (int) (255 * t.getCurrentTrust());
			g2d.setColor(new Color(255, 255 - uga, 0));
			drawLine(g2d, pos.mult(disp_scale),
					Vector2.add(pos, ruler.mult((t.getCurrentTrust() * t.getMaxTrust()) / 40)).mult(disp_scale));
		}
	}

	private void drawLine(Graphics2D g2d, Vector2 v1, Vector2 v2) {
		Vector2 shift = new Vector2(getWidth() / 2F, getHeight() / 2F);
		g2d.drawLine((int) v1.getX() + (int) shift.getX(), (int) -v1.getY() + (int) shift.getY(),
				(int) v2.getX() + (int) shift.getX(), (int) -v2.getY() + (int) shift.getY());
	}

	private void drawOval(Graphics2D g2d, Vector2 v1, double rad) {
		Vector2 shift = new Vector2(getWidth() / 2F, getHeight() / 2F);
		g2d.drawOval((int) (v1.getX() + (int) shift.getX() - rad), (int) (-v1.getY() + (int) shift.getY() - rad),
				(int) rad * 2, (int) rad * 2);
	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	public static boolean qPressed = false;

	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getKeyChar() == 'q') {
			qPressed = true;
		}

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getKeyChar() == 'q') {
			qPressed = false;
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {

	}

	boolean holdingMouse = false;

	@Override
	public void mousePressed(MouseEvent e) {

		if (e.getButton() == MouseEvent.BUTTON3) {

			holdingMouse = true;
		}
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		if (e.getButton() == MouseEvent.BUTTON3) {
			holdingMouse = false;
		}
	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}
}
