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
	 Color color;
	 
	 double[] xData;
	 double[] yData;
	 public Scatterplot(double[] data) {
		 color =  Color.getHSBColor((float) Math.random(), 0.9F, 0.7F);
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
	        frame.setSize(WIDTH, HEIGHT+30);

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
     
        //g.drawLine(MARGIN, HEIGHT - MARGIN, MARGIN + xAxisLength, HEIGHT - MARGIN);
        g.drawLine(MARGIN, HEIGHT - MARGIN, MARGIN, HEIGHT - MARGIN - yAxisLength);
        
    	if (xData == null || yData == null || xData.length <= 0 || yData.length <= 0)
			return;
    	
        g.drawString(""+getMin(yData), 60, HEIGHT - 40);
        g.drawString(""+0, 60, 50);
        // Draw data points
        for (int i = 0; i < xData.length; i++) {
            int x = MARGIN + (int) ((xData[i] - getMin(xData)) / (getMax(xData) - getMin(xData)) * xAxisLength);
            int y = MARGIN +(int) ((yData[i] ) / ( getMin(yData)) * yAxisLength);

            g.setColor(color);
            g.fillOval(x - 3, y -3, 6, 6);
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
