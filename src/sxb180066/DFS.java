/**
 * Project: Implementation of topological sorting of a directed acyclic graph
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
import rbk.Graph.Timer;

import java.io.File;
import java.util.List;
import java.util.LinkedList;
import java.util.Scanner;

enum Color {
    WHITE, GRAY, BLACK;
}

public class DFS extends GraphAlgorithm<DFS.DFSVertex> {

    static LinkedList<Vertex> finishList = null;
    static boolean isCyclic = false;

    public static class DFSVertex implements Factory {

        int cno;
        Vertex parent;
        Color color;

        public DFSVertex(Vertex u) {
            parent = null;
        }

        @Override
        public DFSVertex make(Vertex u) {
            return new DFSVertex(u);
        }
    }

    public DFS(Graph g) {
        super(g, new DFSVertex(null));
    }

    public static DFS depthFirstSearch(Graph g) {
        DFS d = new DFS(g);
        d.dfs(g);
        return d;
    }

    /**
     * Helper function to perform depth first search
     * @param g Graph
     */
    private void dfs(Graph g) {
        finishList = new LinkedList<>();

        for (Vertex u : g) {
            get(u).parent = null;
            get(u).color = Color.WHITE;
        }

        for (Vertex u : g) {
            if (!isCyclic && get(u).color == Color.WHITE) {
                dfsVisit(u, g);
            }
        }
    }

    /**
     * Function to visit nodes in the graphs recursively
     * @param u Current vertex
     * @param g Graph
     */
    private void dfsVisit(Vertex u, Graph g) {
        if (isCyclic) {
            return;
        }

        get(u).color = Color.GRAY;
        for (Edge e : g.incident(u)) {
            Vertex v = e.otherEnd(u);
            if (get(v).color == Color.WHITE) {
                get(v).parent = u;
                dfsVisit(v, g);
            } else if (get(v).color == Color.GRAY) {
                isCyclic = true;
            }
        }
        get(u).color = Color.BLACK;
        finishList.addFirst(u);
    }

    // Member function to find topological order
    public List<Vertex> topologicalOrder1() {
        if (!g.isDirected()) {
            return null;
        }

        dfs(g);

        if (isCyclic) {
            return null;
        }
        return finishList;
    }

    // After running the connected components algorithm, the component no of each vertex can be queried.
    public int cno(Vertex u) {
        return get(u).cno;
    }

    // Find topological oder of a DAG using DFS. Returns null if g is not a DAG.
    public static List<Vertex> topologicalOrder1(Graph g) {
        return new DFS(g).topologicalOrder1();
    }

    public static void main(String[] args) throws Exception {
        String string = "7 6   1 2 2   1 3 3   2 4 5   3 4 4   4 5 1   6 7 1 ";
        Scanner in;
        // If there is a command line argument, use it as file from which
        // input is read, otherwise use input from string.
        in = args.length > 0 ? new Scanner(new File(args[0])) : new Scanner(string);

        // Read graph from input
        Graph g = Graph.readGraph(in, true);
        g.printGraph(true);

        DFS d = new DFS(g);
        List<Vertex> l = DFS.topologicalOrder1(g);
        if(l == null) {
            System.out.println("Given graph is not a DAG");
        } else {
            System.out.println("Topological order: " + l);
        }
    }
}