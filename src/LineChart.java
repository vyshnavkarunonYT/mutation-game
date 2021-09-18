import javax.swing.JFrame;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

public class LineChart extends JFrame {
	public LineChart(DefaultCategoryDataset dataset, String title, String chartTitle, String xAxisLabel, String yAxisLabel) {
		super(title);
		
		JFreeChart chart = ChartFactory.createLineChart(chartTitle, xAxisLabel, yAxisLabel, dataset);
		ChartPanel panel = new ChartPanel(chart);  
	    setContentPane(panel);  
	}
}
