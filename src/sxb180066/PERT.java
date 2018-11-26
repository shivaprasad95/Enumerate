/**
 * Driver code for PERT algorithm (LP4)
 * @author Shiva Prasad Reddy Bitla (sxb180066)
 * @author Sudeep Maity (sdm170530)
 * @author Saurav Sharma (sxs179830)
 */

package sxb180066;

import rbk.Graph;
import rbk.Graph.Vertex;
import rbk.Graph.Edge;
import rbk.Graph.GraphAlgorithm;
import rbk.Graph.Factory;

import java.io.File;
import java.util.List;
import java.util.ListIterator;
import java.util.Scanner;

public class PERT extends GraphAlgorithm<PERT.PERTVertex> {

    private int maxTime;
	private int numOfCriticalNodes;
	private Vertex s; // dummy start node
	private Vertex t; //dummy end node

	/**
	 * PertVertex class
	 */
	public static class PERTVertex implements Factory {

	    private int duration;
		private int ec;
		private int lc;
		private int slack;
		private boolean isCritical;

		public PERTVertex(Vertex u) {
			this.ec = 0;
			this.isCritical = false;
			this.slack = 0;
		}

		public PERTVertex make(Vertex u) { return new PERTVertex(u); }

		public int getDuration() {
		    return this.duration;
        }

        public void setDuration(int duration) {
		    this.duration = duration;
        }

        public int getEc() {
		    return this.ec;
        }

        public void setEc(int ec) {
		    this.ec = ec;
        }

        public int getLc() {
		    return this.lc;
        }

        public void setLc(int lc) {
		    this.lc = lc;
        }

        public int getSlack() {
		    return this.slack;
        }

        public void setSlack(int slack) {
		    this.slack = slack;
        }

        public boolean getCritical() {
		    return this.isCritical;
        }

        public void setCritical(boolean isCritical) {
		    this.isCritical = isCritical;
        }
	}

	/**
	 * PERT Constructor
     * Initializing all member variables
	 * @param g: Graph Object
	 */
	public PERT(Graph g) {

	    super(g, new PERTVertex(null));
		this.maxTime = 0;
		this.numOfCriticalNodes = 0;
		this.s = g.getVertex(1);
		this.t = g.getVertex(g.size());
	}

	/**
	 * Method to set duration
	 * @param u
	 * @param d
	 */
	public void setDuration(Vertex u, int d) {
	    get(u).setDuration(d);
	}

	/**
	 * Returns false if graph is not DAG
	 * @return
	 */
	public boolean pert() {

		// Running DFS on the graph, to get Topological Order
		DFS d = new DFS(g);
		List<Vertex> topologicalOrder = d.topologicalOrder1();

		// If topologicalOrder is null then the graph is not DAG
		if(topologicalOrder == null){
			return false;
		}

		// Calculation of EC for all nodes
		for( Vertex u : topologicalOrder){
			for(Edge e : g.incident(u)){
				Vertex v = e.otherEnd(u);
				if(get(u).getEc() + get(v).getDuration() > get(v).getEc()){
					get(v).setEc(get(u).getEc() + get(v).getDuration());
				}
			}
		}

		// MaxTime is the EC of end node
		this.maxTime = get(t).getEc();

		// Setting lc of all the nodes to maxTime
		for(Vertex u : g){
			get(u).setLc(maxTime);
		}

		// Calculation lc
		ListIterator<Vertex> reverseTopological = topologicalOrder.listIterator(topologicalOrder.size()); //using reverse iterator
		while(reverseTopological.hasPrevious()){
			Vertex u = reverseTopological.previous();
			for( Edge e : g.incident(u)){
				Vertex v = e.otherEnd(u);
				if(get(v).getLc() - get(v).getDuration() < get(u).getLc()){
					get(u).setLc(get(v).getLc() - get(v).getDuration());
				}
			}

			// Calculation of slack
			get(u).setSlack(get(u).getLc() - get(u).getEc());

			if(get(u).getSlack() == 0){
				// if slack is 0 then the node is critical
				get(u).setCritical(true);
				this.numOfCriticalNodes++;
			}
		}
		return true;
	}

    /**
     * Earliest Time u can be completed
     * @param u Vertex
     * @return Earliest Time u can be completed
     */
	public int ec(Vertex u) {
		return get(u).getEc();
	}

    /**
     * Latest Time u can be completed
     * @param u Vertex
     * @return Earliest Time u can be completed
     */
	public int lc(Vertex u) {
		return get(u).getLc();
	}

    /**
     * Slack for Vertex u
     * @param u Vertex
     * @return Slack
     */
	public int slack(Vertex u) {
		return get(u).getSlack();
	}

    /**
     *
     * @return Critical Path for the Graph
     */
	public int criticalPath() {
		return this.maxTime;
	}

    /**
     * Checks whether a Vertex is critical or not i.e. Earliest Time = Latest Time
     * @param u Vertex
     * @return if Vertex is critical or not
     */
	public boolean critical(Vertex u) {
		return get(u).getCritical();
	}

    /**
     *
     * @return number of critical vertex
     */
	public int numCritical() {
		return this.numOfCriticalNodes;
	}

    /**
     * Run Pert algorithm on a given Graph
     * @param g Graph
     * @param duration
     * @return PERT class object storing all information
     */
	public static PERT pert(Graph g, int[] duration) {
		PERT pert = new PERT(g);

		// Initializing duration of all nodes and setting up dummy start and end nodes
		for(Vertex u: g) {

		    pert.setDuration(u, duration[u.getIndex()]);

		    // Creating edge from start node to all other nodes
			if(u.getName() != 1){
				g.addEdge(pert.s.getIndex(),u.getIndex(),0);
			}

			// Creating edge from all the nodes to end node
			if(u.getName() != g.size()){
				g.addEdge(u.getIndex(),pert.t.getIndex(),0);
			}

		}

		if(pert.pert()){
			return pert;
		} else{
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
