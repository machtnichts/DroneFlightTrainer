package sandbox;

import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class Scatterplot {
	
	
	 private static final int WIDTH = 500;
	 private static final int HEIGHT = 400;
	 private static final int MARGIN = 50;
	 
	 double[] xData;
	 double[] yData;
	 public Scatterplot(double[] data) {
		yData = data;
		xData = new double[data.length];
		for (int i = 0;i< xData.length;i++) {
			xData[i] = i;
		}
		createAndShowGUI();
	 }
	 
	 public void setData(ArrayList<Double> data) {
		 double[] array = new double[data.size()];

	        // Copy elements from ArrayList to the array
	        for (int i = 0; i < data.size(); i++) {
	            array[i] = data.get(i);
	        }
	        yData = array;
			xData = new double[array.length];
			for (int i = 0;i< xData.length;i++) {
				xData[i] = i;
			}
	 }
	    
	 JPanel panel;
	    private void createAndShowGUI() {
	        JFrame frame = new JFrame("Score over Generations");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.setSize(WIDTH, HEIGHT);

	         panel = new JPanel() {
	            @Override
	            protected void paintComponent(Graphics g) {
	                super.paintComponent(g);
	                drawScatterPlot(g);
	            }
	        };

	        frame.getContentPane().add(panel);
	        frame.setVisible(true);
	        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
	    }
	    
	    public void update() {
	    	panel.repaint();
	    }
	    
	private void drawScatterPlot(Graphics g) {
        int xAxisLength = WIDTH - 2 * MARGIN;
        int yAxisLength = HEIGHT - 2 * MARGIN;

        // Draw axes
        g.drawLine(MARGIN, HEIGHT - MARGIN, MARGIN + xAxisLength, HEIGHT - MARGIN);
        g.drawLine(MARGIN, HEIGHT - MARGIN, MARGIN, HEIGHT - MARGIN - yAxisLength);

        // Draw data points
        for (int i = 0; i < xData.length; i++) {
            int x = MARGIN + (int) ((xData[i] - getMin(xData)) / (getMax(xData) - getMin(xData)) * xAxisLength);
            int y = HEIGHT - MARGIN - (int) ((yData[i] - getMin(yData)) / (getMax(yData) - getMin(yData)) * yAxisLength);

            g.setColor(Color.BLUE);
            g.fillOval(x - 2, y - 2, 4, 4);
        }
    }

    private double getMin(double[] array) {
        double min = array[0];
        for (double value : array) {
            if (value < min) {
                min = value;
            }
        }
        return min;
    }

    private double getMax(double[] array) {
        double max = array[0];
        for (double value : array) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }
}
