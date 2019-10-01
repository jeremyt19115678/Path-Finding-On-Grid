public class Node implements Comparable<Node> {
	public int c, r;
	//for searching and fringe 
	public double f, h, g;
	//for backtrack
	public Node parent;
	//for drawing and searching
	public boolean obstacle, start, end, fringe, visited, solution;

	public Node(int a, int b) {
		c = a;
		r = b;
		parent = null;
		obstacle = false;
		start = false;
		end = false;
		fringe = false;
		visited = false;
		solution = false;
		f = 0;
		h = 0;
		g = 0;
	}

	public double getDist(Node stuff) {
		return (Math.sqrt(Math.pow(c - stuff.c, 2) + Math.pow(r - stuff.r, 2)));
	}

	public int compareTo(Node stuff) {
		return (int) ((this.f - stuff.f) * 100);
	}

	public void reset(String type) {
		parent = null;
		f = 0;
		h = 0;
		g = 0;
		fringe = false;
		visited = false;
		solution = false;
		if (type.equals("all")) {
			obstacle = false;
			start = false;
			end = false;
		}else if (type.equals("obstacle")) {
			obstacle = false;
		}
	}
}