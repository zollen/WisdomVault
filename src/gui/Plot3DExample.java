package gui;

import java.awt.Color;

import javax.swing.JFrame;

import org.math.plot.Plot3DPanel;

public class Plot3DExample {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		double[][] data = {
				{0, 1, 2, 3}, 
				{0, 1, 1, 2}, 
				{0, 1, 2, 3}
				};
	
			
		double[] x = data[0];
		double[] y = data[1];
		double[] z = data[2];
		
		// create your PlotPanel (you can use it as a JPanel)
		Plot3DPanel plot3D = new Plot3DPanel();
		plot3D.addScatterPlot("Testing", Color.RED, x, y, z);
		plot3D.addLegend("North");
		

		// put the PlotPanel in a JFrame, as a JPanel
		JFrame frame3D = new JFrame("A 3D Plot Panel");
		frame3D.setContentPane(plot3D);
		frame3D.setBounds(100, 100, 500, 500);
		frame3D.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame3D.setVisible(true);
	}

}