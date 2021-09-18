import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class Reproduction {
	
	
	//Probability of an allele getting deleted during mutation
	static double DELETION_MUTATION_CONSTANT = 0.005;
	
	//Probability of an allele getting  transmuted during mutation
	static double CHANGE_MUTATION_CONSTANT = 0.025;
	
	//Probability of an allele being added to the genome
	static double ADDITION_MUTATION_CONSTANT = 0.1; 
	
	//Probability of a child having a bigger genome than the parents
	static double FAVOR_BIGGER_ORGANISM_CONSTANT = 0.2; 
	
	
	//The maximum number of alleles an organism can have/ the maximum complexity of an organism
	static double MAXIMUM_ALLELE_CONSTANT = 30;
	
	

	
	//Getting parents with multiple Food Source Points
	public static int[] getNextParentsIndexWithMultipleFSP(ArrayList<Organism>children, ArrayList<Point>FOOD_SOURCE_POINTS) {
		
		//Creating an array of average of minimal distance of multiple points from food source points
		Integer[] avgDistance = new Integer[children.size()];
		
		//Iterating through the children
		for(int i =0; i<children.size(); i++) {
			int sum = 0;
			//Iterating through the multiple food source points and summing up avg distance
			for(int j=0; j<FOOD_SOURCE_POINTS.size(); j++) {
				sum+= getMinimalSquaredDistance(children.get(i).gene, FOOD_SOURCE_POINTS.get(j));
			}
			
			avgDistance[i] =(int) (sum/FOOD_SOURCE_POINTS.size()+Math.pow(children.get(i).gene.size(),1));

			
			
		}
		
		
		ArrayIndexComparator comparator = new ArrayIndexComparator(avgDistance);
		Integer[] indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);
		
		int [] parentIndexes = {indexes[0], indexes[1]};
		
		return parentIndexes;
	}
	
	public static int getAvgDistance(ArrayList<Point>sp, Point dp) {
		int sum = 0;
		for(int i =0; i<sp.size(); i++) {
			sum+=getDistance(sp.get(i),dp);
		}
		return sum/sp.size();
		
	}
	
	public static int getMinimalSquaredDistance(ArrayList<Point>sp, Point dp) {
		int minDist = (int) Math.pow(getDistance(sp.get(0),dp),2);
		for(int i=0; i<sp.size(); i++) {
			int sqDist = (int) (Math.pow(sp.get(i).x-dp.x, 2)+Math.pow(sp.get(i).y-dp.y, 2));
			if(sqDist<minDist) {
				minDist=sqDist;
			}
		}
		return minDist;
	}
	
	
	
	//Calculates the area using an a set of Point objects supplied as an array list - not used currently.
	public static int getArea(ArrayList<Point>point) {
		 // Initialze area 
        int area = 0; 
        int n = point.size();
        
        //Init X and Y arrays
        int X[] = new int[n];
        int Y[] = new int[n];
        
        for(int i=0; i<n; i++) {
        	X[i] = point.get(i).x;
        	Y[i] = point.get(i).y;
        }
      
        // Calculate value of shoelace formula 
        int j = n - 1; 
        for (int i = 0; i < n; i++) 
        { 
            area += (X[j] + X[i]) * (Y[j] - Y[i]); 
              
            // j is previous vertex to i 
            j = i;  
        } 
      
        // Return absolute value 
        return Math.abs(area / 2); 
		
	}
	
	public static int getDistance(Point p1, Point p2){
		return (int)Math.sqrt(Math.pow(p1.x-p2.x, 2)+Math.pow(p1.y-p2.y, 2));
	}
	
	
	
	
	
	//MEIOSIS 
	@SuppressWarnings({ "unused", "unchecked" })
	public static ArrayList<Organism> meiosis(Organism A, Organism B){
		ArrayList <Organism> children = new ArrayList<Organism>();
		for(int i =0; i<8; i++) {
			//COPYING THE PARENT GENES
			ArrayList<Point> geneA = (ArrayList<Point>) A.gene.clone();
			ArrayList<Point> geneB = (ArrayList<Point>) B.gene.clone();
			
			
			//CROSSING THE PARENT GENES
			ArrayList<Point> childGene = cross(geneA,geneB);

			
			//SORTING THE CHILD GENE IN COUNTER CLOCKWISE ORDER
			if(childGene.size()>2) {
				childGene = sortCounterClockwise(childGene);
			}
			
			children.add(new Organism(childGene , new Parents(A,B), A.generation+1));
		}
		
		return children;
		
	}
	
	//CROSS
	public static ArrayList<Point> cross(ArrayList<Point> geneA, ArrayList<Point> geneB){
		int maxAllele = Math.max(geneA.size(), geneB.size());
		ArrayList<Point> childGene = new ArrayList<Point>();
		for(int i =0; i<maxAllele; i++) {
			double favor = Math.random();
			if(favor<0.5) {
				if(geneA.size()>i) {
					childGene.add(geneA.get(i));
				} else if(Math.random()< FAVOR_BIGGER_ORGANISM_CONSTANT) {
					childGene.add(geneB.get(i));
				}
			} else {
				if(geneB.size()>i) {
					childGene.add(geneB.get(i));
				} else if (Math.random()< FAVOR_BIGGER_ORGANISM_CONSTANT){
					childGene.add(geneA.get(i));
				}
			}
		}
		
		//MUTATE CHILD
		childGene = mutate(childGene);
		
		//BACKWARD CHECKS TO CREATE UNIQUE GENE
		childGene = eliminateDuplicates(childGene);
		
//		System.out.println(childGene.size());
		return childGene;
		
	}
	
	//MUTATION
	@SuppressWarnings("unchecked")
	public static ArrayList<Point> mutate (ArrayList<Point> gene) {
		int complexity = gene.size();
		Random rand = new Random();
		//DELETION MUTATION
		if(Math.random()<DELETION_MUTATION_CONSTANT && gene.size()>1) {
			//Pick a random allele and delete
			int i = rand.nextInt(gene.size());
			gene.remove(i);
		}
		
		//ADDITION MUTATION 
		if(Math.random()<ADDITION_MUTATION_CONSTANT+(0.1/complexity) && gene.size()<MAXIMUM_ALLELE_CONSTANT) {
			int low = -100;
			int high = 101;
			int x = rand.nextInt(high-low)+low;
			int y = rand.nextInt(high-low)+low;
			gene.add(new Point(x,y,Color.white));
		}
		
		//CHANGE MUTATION
		if(Math.random()<CHANGE_MUTATION_CONSTANT+(complexity/100)) {
			int changeIndex = rand.nextInt(gene.size());
			int low = -100;
			int high = 101;
			int x = rand.nextInt(high-low)+low;
			int y = rand.nextInt(high-low)+low;
			
			gene.set(changeIndex, new Point(x,y, Color.white));
		}
		
		return gene;
	}
	
	
	//ELIMINATE DUPLICATES
	public static ArrayList<Point> eliminateDuplicates (ArrayList<Point> gene){
		ArrayList<Point> newGene = new ArrayList<Point>();
		HashMap <Integer, Integer> map = new HashMap<Integer, Integer>();
		for(int i =0; i<gene.size(); i++) {
			int x = gene.get(i).x;
			int y = gene.get(i).y;
			if(gene.size()>2){
				if(!map.containsKey(x)) {
					newGene.add(new Point(x,y,Color.white));
				} else if(map.get(x)!=y) {
					newGene.add(new Point(x,y,Color.white));
				}
			} else {
				newGene.add(new Point(x,y,Color.white));
			}
			map.put(x, y);
		}
		return newGene;
	}
	
	
	
	//Counter clockwise sort
	public static ArrayList<Point> sortCounterClockwise(ArrayList<Point> gene){
		int centerX = 0;
		int centerY =0;
		ArrayList <Point> newGene = new ArrayList<Point>();
		Integer a[] = new Integer[gene.size()];
		for(int i=0; i<a.length; i++) {
			Point p =  gene.get(i);
			a[i] = (int) Math.atan2(p.y - centerY, p.x - centerX);
		}
		
		ArrayIndexComparator comparator = new ArrayIndexComparator(a);
		Integer[] indexes = comparator.createIndexArray();
		Arrays.sort(indexes, comparator);
		
		for(int i=0; i<gene.size(); i++) {
			newGene.add(new Point(gene.get(indexes[i]).x,gene.get(indexes[i]).y,Color.white));
		}
		
		//System.out.println("OLD GENE IS ");
		//Organism.printGenome(gene);
		//System.out.println("NEW GENE IS ");
		//Organism.printGenome(newGene);
		
		return newGene;
	}
	
	//Get Evolutionary Deviation
	public static int getEvolutionaryDeviation(Organism organism, ArrayList<Point>FOOD_SOURCE_POINTS) {
		int deviation = 0;
		int sum = 0;
		
		for(int j=0; j<FOOD_SOURCE_POINTS.size(); j++) {
			sum+= getMinimalSquaredDistance(organism.gene, FOOD_SOURCE_POINTS.get(j));
		}
		
		deviation = (int) (sum/FOOD_SOURCE_POINTS.size()+Math.pow(organism.gene.size(),1));
		
		return deviation;
	}
}


