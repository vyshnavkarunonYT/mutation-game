
import java.util.ArrayList;

public class Organism {
	ArrayList<Point> gene;
	int generation = 0;
	Parents parents = null;
	Organism parent = null;
	
	public Organism(ArrayList<Point> gene, Parents parents, int generation) {
		this.gene = gene;
		this.parents = parents;
		this.generation = generation;
	}
	
	public Organism getParent() {
		return this.parent;
	}
	

	
	public void printGenotype() {
		System.out.println("Generation " + generation);
		for(int i =0; i<gene.size(); i++) {
			Point allele =  gene.get(i);
			System.out.println(allele.x+" , "+allele.y);
		}
	}
	
	
	public void printParents() {
		Organism organism = this;
		while(organism!=null) {
			organism.printGenotype();
			organism = organism.getParent();
		}
	}
	
	
	public void printOrganismDetails() {
		System.out.println("ORGANISM GENERATION - "+this.generation);
		System.out.println("ORGANISM GENOME - ");
		printGenome(this.gene);
	}
	
	//Misc Methods
	public static void printGenome(ArrayList<Point> gene) {
		for(int i=0; i<gene.size(); i++) {
			System.out.println(gene.get(i).x+" "+gene.get(i).y);
		}
	}

}
