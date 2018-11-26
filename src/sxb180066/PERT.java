/* Driver code for PERT algorithm (LP4)
 * @author
 */

// change package to your netid
package sxb180066;

import rbk.Graph;
import rbk.Graph.Vertex;
import rbk.Graph.Edge;
import rbk.Graph.GraphAlgorithm;
import rbk.Graph.Factory;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

public class PERT extends GraphAlgorithm<PERT.PERTVertex> {
	int maxTime;
	int numOfCriticalNodes;
	Vertex s; // dummy start node
	Vertex t; //dummy end node

	/**
	 * PertVertex class
	 */
	public static class PERTVertex implements Factory {
		int duration;
		int ec;
		int lc;
		int slack;
		boolean isCritical;

		public PERTVertex(Vertex u) {
			this.ec = 0;
			this.isCritical = false;
			this.slack = 0;
		}
		public PERTVertex make(Vertex u) { return new PERTVertex(u); }
	}

	/**
	 * PERT Constructor
	 * @param g
	 */
	public PERT(Graph g) {
		super(g, new PERTVertex(null));
		this.maxTime = 0;
		this.numOfCriticalNodes = 0;
		//initializing start and end nodes
		this.s = g.getVertex(1);
		this.t = g.getVertex(g.size());
	}

	/**
	 * Method to set duration
	 * @param u
	 * @param d
	 */
	public void setDuration(Vertex u, int d) {
		get(u).duration = d;
	}

	/**
	 * Returns false if graph is not DAG
	 * @return
	 */
	public boolean pert() {
		//running dfs on the graph
		DFS d = new DFS(g);
		List<Vertex> topologicalOrder = d.topologicalOrder1();

		//if topologicalOrder is null then the graph is not DAG
		if(topologicalOrder == null){
			return false;
		}

		//Calculation of EC for all nodes
		for( Vertex u : topologicalOrder){
			for(Edge e : g.incident(u)){
				Vertex v = e.otherEnd(u);
				if(get(u).ec + get(v).duration > get(v).ec){
					get(v).ec = get(u).ec + get(v).duration;
				}
			}
		}

		//MaxTime is the EC of end node
		maxTime = get(t).ec;

		//setting lc of all the nodes to maxTime
		for(Vertex u : g){
			get(u).lc = maxTime;
		}

		//Calculation of LC
		ListIterator<Vertex> reverseTopological = topologicalOrder.listIterator(topologicalOrder.size()); //using reverse iterator
		while(reverseTopological.hasPrevious()){
			Vertex u = reverseTopological.previous();
			for( Edge e : g.incident(u)){
				Vertex v = e.otherEnd(u);
				if(get(v).lc - get(v).duration < get(u).lc){
					get(u).lc = get(v).lc - get(v).duration;
				}
			}
			//Calculation of slack
			get(u).slack = get(u).lc - get(u).ec;
			if(get(u).slack == 0){
				//if slack is 0 then the node is critical
				get(u).isCritical = true;
				numOfCriticalNodes++;
			}
		}
		return true;
	}
	public int ec(Vertex u) {
		return get(u).ec;
	}

	public int lc(Vertex u) {
		return get(u).lc;
	}

	public int slack(Vertex u) {
		return get(u).slack;
	}

	public int criticalPath() {
		return maxTime;
	}

	public boolean critical(Vertex u) {
		return get(u).isCritical;
	}

	public int numCritical() {
		return numOfCriticalNodes;
	}

	// setDuration(u, duration[u.getIndex()]);
	public static PERT pert(Graph g, int[] duration) {
		PERT pert = new PERT(g);

		//Initializing duration of all nodes and setting up dummy start and end nodes
		for(Vertex u: g) {
			pert.setDuration(u, duration[u.getIndex()]);
			//creating edge from start node to all other nodes
			if(u.getName() != 1){
				g.addEdge(pert.s.getIndex(),u.getIndex(),0);
			}
			//creating edge from all the nodes to end node
			if(u.getName() != g.size()){
				g.addEdge(u.getIndex(),pert.t.getIndex(),0);
			}

		}
		if(pert.pert()){
			return pert;
		}else{
			return null;
		}
	}

	public static void main(String[] args) throws Exception {
		String graph = "11 12   2 4 1   2 5 1   3 5 1   3 6 1   4 7 1   5 7 1   5 8 1   6 8 1   6 9 1   7 10 1   8 10 1   9 10 1      0 3 2 3 2 1 3 2 4 1 0";
		Scanner in;
		// If there is a command line argument, use it as file from which
		// input is read, otherwise use input from string.
		in = args.length > 0 ? new Scanner(new File(args[0])) : new Scanner(graph);
		Graph g = Graph.readDirectedGraph(in);
		g.printGraph(false);

		PERT p = new PERT(g);
		for(Vertex u: g) {
			p.setDuration(u, in.nextInt());
		}
		// Run PERT algorithm.  Returns null if g is not a DAG
		if(p.pert()) {
			System.out.println("Invalid graph: not a DAG");
		} else {
			System.out.println("Number of critical vertices: " + p.numCritical());
			System.out.println("u\tEC\tLC\tSlack\tCritical");
			for(Vertex u: g) {
				System.out.println(u + "\t" + p.ec(u) + "\t" + p.lc(u) + "\t" + p.slack(u) + "\t" + p.critical(u));
			}
		}
	}
}
