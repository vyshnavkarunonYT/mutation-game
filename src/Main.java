import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import javax.swing.Timer;
import javax.swing.WindowConstants;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.ui.ApplicationFrame;
import org.jfree.ui.RefineryUtilities;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;


public class Main extends JPanel implements ActionListener, MouseListener {
	JFrame frame;
	Timer timer = new Timer(100, this);

	// CONSTANTS
	static Point FOOD_SOURCE_POINT = new Point(-10, 50, Color.red);
	static ArrayList<Point> FOOD_SOURCE_POINTS = new ArrayList<Point>();

	// EVOLUTION CONTROLS
	static boolean ACTIVE_NATURAL_SELECTION = true;

	// Buttons
	static JButton generateButton;
	static JButton removeFoodSourceButton;
	static JButton addFoodSourceButton;
	static JButton displayGraphButton;

	// Parent Container Boxes
	int parentAboxStartX;
	int parentAboxStartY;
	int parentBboxStartX;
	int parentBboxStartY;

	// Organism Container Boxes
	int boxStartX = 450;
	int boxStartY = 200;
	static int boxWidth = 200;
	static int boxHeight = 200;

	// Parent Organism Pointers
	static Organism parentA;
	static Organism parentB;

	// Children Organisms
	static ArrayList<Organism> children;

	// Evolutionary Success Graph Variables
	static ArrayList<Point> GraphPoints = new ArrayList<Point>();

	public Main() {
		timer.start();
		this.addMouseListener(this);
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame();
		frame.setTitle("The Mutation Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setBounds(0, 0, screenDim.width, screenDim.height);
		frame.setResizable(false);
		Main main = new Main();
		main.setBounds(0, 0, screenDim.width, screenDim.height);

		// Generating the first parents
		ArrayList<Point> starterGene = new ArrayList<Point>();
		starterGene.add(new Point(0, 0, Color.black));
//		Organism.printGenome(starterGene);
		parentA = new Organism(starterGene, new Parents(null, null), 0);
		parentB = new Organism(starterGene, new Parents(null, null), 0);

		// Generating the first children
		children = new ArrayList<Organism>();
		for (int i = 0; i < 8; i++) {
			Organism child = new Organism(new ArrayList<Point>(), new Parents(parentA, parentB), 1);
			child.gene.add(new Point(0, 0, Color.white));
			children.add(child);
		}

		// Initializig FOOD_SOURCE_POINTS
//		FOOD_SOURCE_POINTS.add(new Point(-20,30,Color.yellow));
//		FOOD_SOURCE_POINTS.add(new Point(-10,-25,Color.yellow));
//		FOOD_SOURCE_POINTS.add(new Point(50,35,Color.yellow));

//		children.get(1).gene.add(new Point(40,40,Color.white));
//		children.get(1).gene.add(new Point(-10,40,Color.white));
//		children.get(2).gene.add(new Point(41,54,Color.white));
//		children.get(2).gene.add(new Point(57,54,Color.white));
//		children.get(1).gene.set(1, new Point(-10,30,Color.white));
//		children.get(1).gene.set(1, new Point(45,40,Color.white));
//		children.get(1).printGenotype();
//		
//		parentA.printGenotype();
//		parentB.printGenotype();

//		System.out.println("Parent Indexes are " + Arrays.toString(Reproduction.getNextParentsIndexWithMP(children, FOOD_SOURCE_POINT)));

		// Button Init
		// Start/Stop Natural Selection Button
		generateButton = new JButton("Natural Selection: " + (ACTIVE_NATURAL_SELECTION ? "ON" : "OFF"));
		main.setLayout(null);
		generateButton.setBounds(1050, 650, 200, 25);
		generateButton.setFocusable(false);
		main.add(generateButton);
		generateButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ACTIVE_NATURAL_SELECTION = !ACTIVE_NATURAL_SELECTION;
				generateButton.setText("Natural Selection: " + (ACTIVE_NATURAL_SELECTION ? "ON" : "OFF"));

			}
		});

		// Adding a food source button
		addFoodSourceButton = new JButton("Add Food Source");
		addFoodSourceButton.setFocusable(false);
		addFoodSourceButton.setBounds(800, 650, 200, 25);
		main.add(addFoodSourceButton);
		addFoodSourceButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Random rand = new Random();
				int x = rand.nextInt(boxWidth / 2 + 1 - (-boxWidth / 2)) - (boxWidth / 2) + 5;
				int y = rand.nextInt((boxHeight / 2 + 1) + (boxHeight / 2)) - (boxHeight / 2);
				FOOD_SOURCE_POINTS.add(new Point(x, y, Color.red));
				removeFoodSourceButton.setEnabled(true);
			}
		});

		// Remove food source button ( last added food source )
		removeFoodSourceButton = new JButton("Remove Food Source");
		removeFoodSourceButton.setFocusable(false);
		removeFoodSourceButton.setBounds(800, 700, 200, 25);
		main.add(removeFoodSourceButton);
		removeFoodSourceButton.setEnabled(false);
		removeFoodSourceButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				FOOD_SOURCE_POINTS.remove(FOOD_SOURCE_POINTS.size() - 1);
				if (FOOD_SOURCE_POINTS.size() == 0) {
					removeFoodSourceButton.setEnabled(false);
				}
			}
		});

		// Shows the evolutionary success graph
		displayGraphButton = new JButton("Display Graph");
		displayGraphButton.setBounds(1050, 700, 200, 25);
		displayGraphButton.setFocusable(false);
		main.add(displayGraphButton);
	
		displayGraphButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				
				//Generate Category Dataset from the graph points available
				 DefaultCategoryDataset dataset = new DefaultCategoryDataset( );
				 for(int i =0; i<GraphPoints.size(); i+=5) {
					 dataset.addValue(GraphPoints.get(i).x, "Deviation", ""+GraphPoints.get(i).y);
				 }
				 
				 SwingUtilities.invokeLater(() -> {  
				      LineChart example = new LineChart(dataset, "Evolutionary Deviation","Drop in deviation with subsequent generations","Generation", "Deviation");  
				      example.setAlwaysOnTop(true);  
				      example.pack();  
				      example.setSize(600, 400);  
				      example.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);  
				      example.setVisible(true);  
				    });  
			}
		});
		

		frame.add(main);
		frame.setVisible(true);
	}

	public void paintComponent(Graphics g) {
		super.paintComponents(g);
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(Color.black);
		Dimension screenDim = Toolkit.getDefaultToolkit().getScreenSize();
		g2.fillRect(0, 0, screenDim.width, screenDim.height);

		// Printing the Organism boxes
		g2.setColor(Color.white);
		g2.setStroke(new BasicStroke(3));
		// Box Horizontal Lines
		for (int i = 0; i < 3; i++) {
			g2.drawLine(boxStartX, boxStartY + boxHeight * i, boxStartX + boxWidth * 4, boxStartY + boxHeight * i);
		}
		// Box Vertical Lines
		for (int i = 0; i < 5; i++) {
			g2.drawLine(boxStartX + boxWidth * i, boxStartY, boxStartX + boxWidth * i, boxStartY + boxHeight * 2);
		}

		// Printing the Parent Boxes
		// Parent A Box
		g2.drawRect(boxStartX - boxWidth * 2, boxStartY - boxHeight / 2, boxWidth, boxHeight);
		// Parent B Box
		g2.drawRect(boxStartX - boxWidth * 2, boxStartY + (int) (boxHeight * 1.5), boxWidth, boxHeight);

		// Parent - Child Link
		g2.setStroke(new BasicStroke(1));
		g2.drawLine(boxStartX, boxStartY + boxHeight, boxStartX - boxWidth, boxStartY + boxHeight / 2);
		g2.drawLine(boxStartX, boxStartY + boxHeight, boxStartX - boxWidth, boxStartY + (int) (boxHeight * 1.5));

		// Drawing the parents

		g2.setStroke(new BasicStroke(3));

		// Parent A Box Origin Point
		int originXPA = boxStartX - boxWidth * 2 + boxWidth / 2;
		int originYPA = boxStartY;

		// Painting Parent A
		int XPA[] = new int[parentA.gene.size()];
		int YPA[] = new int[parentA.gene.size()];
		for (int i = 0; i < parentA.gene.size(); i++) {
			XPA[i] = parentA.gene.get(i).x + originXPA;
			YPA[i] = parentA.gene.get(i).y + originYPA;
		}

		if (parentA.gene.size() == 1) {
			g2.drawLine(XPA[0], YPA[0], XPA[0], YPA[0]);
		} else if (parentA.gene.size() == 2) {
			g2.drawLine(XPA[0], YPA[0], XPA[1], YPA[1]);
		} else if (parentA.gene.size() > 2) {
			g2.fillPolygon(XPA, YPA, parentA.gene.size());
		}

		// Parent B Box Origin Point
		int originXPB = boxStartX - boxWidth * 2 + boxWidth / 2;
		int originYPB = boxStartY + boxHeight * 2;

		// Painting Parent B
		int XPB[] = new int[parentB.gene.size()];
		int YPB[] = new int[parentB.gene.size()];
		for (int i = 0; i < parentB.gene.size(); i++) {
			XPB[i] = parentB.gene.get(i).x + originXPB;
			YPB[i] = parentB.gene.get(i).y + originYPB;
		}

		if (parentB.gene.size() == 1) {
			g2.drawLine(XPB[0], YPB[0], XPB[0], YPB[0]);
		} else if (parentB.gene.size() == 2) {
			g2.drawLine(XPB[0], YPB[0], XPB[1], YPB[1]);
		} else if (parentB.gene.size() > 2) {
			g2.fillPolygon(XPB, YPB, parentB.gene.size());
		}

		// Drawing the children
		g2.setColor(Color.white);
		for (int i = 0; i < children.size(); i++) {
			int rowLimiter = children.size() / 2;
			int originXC = boxStartX + (boxWidth * (i % rowLimiter)) + boxWidth / 2;
			int originYC = boxStartY + (boxHeight * (i / rowLimiter)) + boxHeight / 2;
			Organism child = children.get(i);
			int size = child.gene.size();
			int X[] = new int[size];
			int Y[] = new int[size];
			for (int j = 0; j < child.gene.size(); j++) {
				X[j] = originXC + child.gene.get(j).x;
				Y[j] = originYC + child.gene.get(j).y;
			}
//			System.out.println(i);
//			System.out.println(Arrays.toString(X));
//			System.out.println(Arrays.toString(Y));
			g2.setPaint(Color.white);
			if (size > 2) {
				g2.fillPolygon(X, Y, size);
			} else if (size == 2) {
				g2.drawLine(X[0], Y[0], X[1], Y[1]);
			} else if (size == 1) {
				g2.drawLine(X[0], Y[0], X[0], Y[0]);
			}

			g2.setPaint(Color.yellow);
			for (int j = 0; j < X.length; j++) {
				g2.drawLine(X[j], Y[j], X[j], Y[j]);
			}

		}

		// Drawing the FOOD_SOURCE_POINT in the parent boxes
//		g2.setColor(FOOD_SOURCE_POINT.color);
//		g2.drawLine(originXPA+FOOD_SOURCE_POINT.x, originYPA+FOOD_SOURCE_POINT.y, originXPA+FOOD_SOURCE_POINT.x, originYPA+FOOD_SOURCE_POINT.y);
//		g2.drawLine(originXPB+FOOD_SOURCE_POINT.x, originYPB+FOOD_SOURCE_POINT.y, originXPB+FOOD_SOURCE_POINT.x, originYPB+FOOD_SOURCE_POINT.y);

		// Drawing the FOOD_SOURCE_POINTS in the parent boxes
		g2.setStroke(new BasicStroke(5));
		for (int i = 0; i < FOOD_SOURCE_POINTS.size(); i++) {
			Point point = FOOD_SOURCE_POINTS.get(i);
			g2.setColor(point.color);
			g2.drawLine(originXPA + point.x, originYPA + point.y, originXPA + point.x, originYPA + point.y);
			g2.drawLine(originXPB + point.x, originYPB + point.y, originXPB + point.x, originYPB + point.y);
		}

		// Drawing FOOD_SOURCE_POINT in the children boxes
//		g2.setColor(FOOD_SOURCE_POINT.color);
//		for(int i=0; i<children.size(); i++) {
//			int rowLimiter = children.size()/2;
//			int originXC = boxStartX+ (boxWidth*(i%rowLimiter))+boxWidth/2;
//			int originYC = boxStartY + (boxHeight*(i/rowLimiter))+boxHeight/2;
//			int x = FOOD_SOURCE_POINT.x + originXC;
//			int y = FOOD_SOURCE_POINT.y + originYC;
//			g2.drawLine(x, y, x, y);
//		}

		// Drawing _FOOD_SOURCE_POINTS in the children boxes
		for (int i = 0; i < children.size(); i++) {
			for (int j = 0; j < FOOD_SOURCE_POINTS.size(); j++) {
				Point point = FOOD_SOURCE_POINTS.get(j);
				g2.setColor(point.color);
				int rowLimiter = children.size() / 2;
				int originXC = boxStartX + (boxWidth * (i % rowLimiter)) + boxWidth / 2;
				int originYC = boxStartY + (boxHeight * (i / rowLimiter)) + boxHeight / 2;
				int x = point.x + originXC;
				int y = point.y + originYC;
				g2.drawLine(x, y, x, y);
			}
		}

		// DETAILS
		// GENERATION NUMBER
		g2.setColor(Color.white);
		g2.setFont(new Font("Times New Roman", Font.PLAIN, 30));
		g2.drawString("GENERATION " + children.get(0).generation, 450, 150);
		// ORGANISM COMPLEXITY AND SIMPLICITY
		int complexity = 0;
		int simplicity = 50;
		for (int i = 0; i < children.size(); i++) {
			if (children.get(i).gene.size() > complexity) {
				complexity = children.get(i).gene.size();
			}
			if (children.get(i).gene.size() < simplicity) {
				simplicity = children.get(i).gene.size();
			}
		}
//		g2.drawString("SIMPLICITY " + complexity, 750,150);
		g2.drawString("COMPLEXITY " + complexity, 1050, 150);

		g2.drawString("SOURCE " + FOOD_SOURCE_POINTS.size(), 750, 150);

		// MISC DETAILS
		g2.drawString("PARENT A", 85, 75);
		g2.drawString("PARENT B", 85, 475);
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		// TODO Auto-generated method stub
		// Getting two potential parents from the children
		if (ACTIVE_NATURAL_SELECTION && FOOD_SOURCE_POINTS.size() > 0) {

			int[] parentsIndexes = Reproduction.getNextParentsIndexWithMultipleFSP(children, FOOD_SOURCE_POINTS);

			// Selecting the two best children of the previous generation.

			Organism cpa = children.get(parentsIndexes[0]);
			Organism cpb = children.get(parentsIndexes[1]);

			// Making the two best children of the previous generation the parents of the
			// next generation
			parentA = new Organism(cpa.gene, new Parents(cpa.parents.parentA, cpa.parents.parentB), cpa.generation);
//			parentA.printOrganismDetails();
			parentB = new Organism(cpb.gene, new Parents(cpb.parents.parentA, cpb.parents.parentB), cpb.generation);
//			parentB.printOrganismDetails();

			// Getting the avgDistance to check plot the evolutionary Graph
//			System.out.println(parentA.generation);
//			System.out.println(Reproduction.getEvolutionaryDeviation(parentA, FOOD_SOURCE_POINTS));
			GraphPoints.add(new Point(Reproduction.getEvolutionaryDeviation(parentA, FOOD_SOURCE_POINTS), parentA.generation, Color.black ));

			// Getting the children of the next generation by crossing the parents
			children = Reproduction.meiosis(parentA, parentB);
		}
		repaint();
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub

//		System.out.println("x - "+ e.getX());
//		System.out.println("y - "+ e.getY());

		// Getting the location of the click on the GUI

		// Parent A - Left Upper Point
		int XPA = boxStartX - boxWidth * 2;
		int YPA = boxStartY - boxHeight / 2;
		int x = e.getX();
		int y = e.getY();
		int OXPA = boxStartX - boxWidth * 2 + boxWidth / 2;
		int OYPA = boxStartY;

		if (e.getButton() == MouseEvent.BUTTON1) {
			if (x > XPA && x < XPA + boxWidth && y > YPA && y < YPA + boxHeight) {
				FOOD_SOURCE_POINTS.add(new Point(e.getX() - OXPA, e.getY() - OYPA, Color.red));
				removeFoodSourceButton.setEnabled(true);
			}
		}

		else if (e.getButton() == MouseEvent.BUTTON3) {

			System.out.println("Right Clicked");
			if (x > XPA && x < XPA + boxWidth && y > YPA && y < YPA + boxHeight) {
				// Searching through FOOD_SOURCE_POINTS if a point at a right clicked location
				// exists
				int clickedX = x - OXPA;
				int clickedY = y - OYPA;
				for (int i = 0; i < FOOD_SOURCE_POINTS.size(); i++) {
					if (clickedX >= FOOD_SOURCE_POINTS.get(i).x - 1 && clickedX <= FOOD_SOURCE_POINTS.get(i).x + 1) {
						if (clickedY >= FOOD_SOURCE_POINTS.get(i).y - 1
								&& clickedY <= FOOD_SOURCE_POINTS.get(i).y + 1) {
							FOOD_SOURCE_POINTS.remove(i);
							break;
						}
					}
				}
			}
		}

	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub

	}
}
