# **Project Description: PERT, Enumeration of topological orders**

1. Implement enumeration of permutations. Starter code Enumerate.java is available on elearning.
   The class also contains methods for other enumeration problems that are not part of LP4.

2. Implement enumeration of all topological orders of a given directed graph.
   Starter code EnumerateTopological.java is available on elearning.

3. Implement the methods in PERT.java.

	public static PERT pert(Graph g, int[] duration);
	// Run PERT algorithm on graph g. Assume that vertex 1 is s and vertex n is t.
	// You need to add edges from s to all vertices and from all vertices to t.

	boolean pert();  // non-static method called after calling the constructor

   The following methods can be called after running one of the above methods to query the results:

	public int ec(Vertex u);            // Earliest completion time of u
	public int lc(Vertex u);            // Latest completion time of u
	public int slack(Vertex u);         // Slack of u
	public int criticalPath();          // Length of critical path
	public boolean critical(Vertex u);  // Is vertex u on a critical path?
	public int numCritical();           // Number of critical nodes in graph
## **Group Members**:
- Saurav Sharma (sxs179830)
- Sudeep Maity (sdm170530)
- Shiva Prasad Reddy Bitla (sxb180066)

## **Software stack used while developing and running the project**:
- Language: Java 8
- Compiler: jdk1.8.0_171

## **Instructions to compile and run the project from command line**:
- Naviagate to "sxb180066" and open command prompt at this location
- Compile the source file 
   - "javac -cp . sxb180066\Enumerate.java"
- Run the program using following command
   - "java sxb180066.Enumerate"