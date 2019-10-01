import java.util.PriorityQueue;
import javax.swing.Timer;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;

public class SearchAgent extends JPanel implements MouseListener, MouseMotionListener, KeyListener {
	public Grid world;
	public Timer timer;
	public Node start, end;
	public String mode;
	public boolean running, animate;
	public int expanded;

	public SearchAgent() {
		super();
		running = false;
		animate = true;
		timer = new Timer(40, new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				repaint();
			}
		});
		timer.start();
		start = null;
		end = null;
		mode = "a star";
		world = new Grid();
		this.setPreferredSize(new Dimension(world.sideLength + 1, world.sideLength));
		addMouseMotionListener(this);
		addMouseListener(this);
		addKeyListener(this);
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(Color.WHITE);
		drawMap(g);
	}

	public void drawMap(Graphics g) {
		// the window is a square, so the dimensions are the same
		g.setColor(Color.BLACK);
		// only drawing those that are in the viewing frame
		for (int i = 0; i < world.sideLength / world.sideLengthPixel; i++) {
			for (int j = 0; j < world.sideLength / world.sideLengthPixel; j++) {
				//color representation
				if (world.grid[world.topleftX + i][world.topleftY + j].obstacle)
					g.setColor(Color.GRAY);
				else if (world.grid[world.topleftX + i][world.topleftY + j].start)
					g.setColor(Color.YELLOW);
				else if (world.grid[world.topleftX + i][world.topleftY + j].end)
					g.setColor(Color.RED);
				else if (world.grid[world.topleftX + i][world.topleftY + j].solution)
					g.setColor(Color.GREEN);
				else if (world.grid[world.topleftX + i][world.topleftY + j].fringe)
					g.setColor(Color.CYAN);
				else if (world.grid[world.topleftX + i][world.topleftY + j].visited)
					g.setColor(Color.MAGENTA);
				else
					g.setColor(Color.WHITE);
				g.fillRect(i * world.sideLengthPixel, j * world.sideLengthPixel, world.sideLengthPixel,
						world.sideLengthPixel);
				g.setColor(Color.BLACK);
				g.drawRect(i * world.sideLengthPixel, j * world.sideLengthPixel, world.sideLengthPixel,
						world.sideLengthPixel);
			}
		}
	}

	//does the searching and the animation
	public void search(Node start, Node end, String type) {
		running = true;
		timer.stop();
		reset("search");
		expanded = 0;
		if (type.toLowerCase().trim().equals("a star") || type.toLowerCase().trim().equals("greedy")
				|| type.toLowerCase().trim().equals("ucs")) {
			PriorityQueue<Node> queue = new PriorityQueue<Node>();
			ArrayList<Node> visited = new ArrayList<Node>();
			start.h = start.getDist(end);
			start.g = 0;
			if (type.equals("a star"))
				start.f = start.h + start.g;
			else if (type.equals("greedy"))
				start.f = start.h;
			else if (type.equals("ucs"))
				start.f = start.g;
			queue.add(start);
			if (animate) {
				//if animation is true, timer is used to ensure animation happens
				Timer animation = new Timer(10, new ActionListener() {
					public void actionPerformed(ActionEvent evt) {
						expanded++;
						if (queue.size() > 0) {
							//allow user to halt searches that are heading to nowhere
							if (expanded % 10000 == 0) {
								int n = dialog();
								if (n == 1) {
									running = false;
									timer.start();
									showHaltedMessage();
									((Timer) evt.getSource()).stop();
									return;
								}
							}
							Node q = queue.poll();
							q.fringe = false;
							if (q.equals(end)) {
								ArrayList<Node> list = new ArrayList<>();
								list = backTrack(q, list);
								list.add(0, q);
								Collections.reverse(list);
								for (int i = 0; i < list.size(); i++) {
									list.get(i).solution = true;
								}
								repaint();
								//just a way to not use the console
								showSuccessDialog(expanded);
								running = false;
								timer.start();
								((Timer) evt.getSource()).stop();
								return;
							}
							visited.add(q);
							q.visited = true;
							// all the neighbors
							for (int i = -1; i < 2; i++) {
								for (int j = -1; j < 2; j++) {
									int currR = i + q.r;
									int currC = j + q.c;
									if (currR >= world.sideLength || currR < 0)
										continue;
									if (currC >= world.sideLength || currC < 0)
										continue;
									Node child = world.grid[currC][currR];
									if (child.obstacle == true)
										continue;
									double currF = 0;
									if (type.equals("a star"))
										currF = child.getDist(end) + q.g + child.getDist(q);
									else if (type.equals("greedy"))
										currF = child.getDist(end);
									else if (type.equals("ucs"))
										currF = q.g + child.getDist(q);
									if (visited.contains(child)) // expanded this place
										continue;
									else if (queue.contains(child) && currF >= child.f) {
										continue;
									} else {
										if (!child.obstacle) { // if the child isn't an obstacle
											child.g = q.g + child.getDist(q);
											child.h = child.getDist(end);
											if (type.equals("a star"))
												child.f = child.h + child.g;
											if (type.equals("greedy"))
												child.f = child.h;
											if (type.equals("ucs"))
												child.f = child.g;
											queue.add(child);
											child.parent = q;
											child.fringe = true;
										}
									}
								}
							}
							repaint();
						} else {
							running = false;
							timer.start();
							//show the user that the search has failed.
							showNoSolutionDialog();
							return;
						}
					}
				});
				animation.start();
			} else {
				//essentially the same code. I have no idea how to condense it down.
				//for those who want fast solutions.
				repaint();
				while (queue.size() > 0) {
					expanded++;
					if (expanded % 50000 == 0) {
						int n = dialog();
						if (n == 1) {
							running = false;
							timer.start();
							showHaltedMessage();
							return;
						}
					}
					Node q = queue.poll();
					q.fringe = false;
					if (q.equals(end)) {
						ArrayList<Node> list = new ArrayList<>();
						list = backTrack(q, list);
						list.add(0, q);
						Collections.reverse(list);
						for (int i = 0; i < list.size(); i++) {
							list.get(i).solution = true;
						}
						repaint();
						showSuccessDialog(expanded);
						running = false;
						timer.start();
						return;
					}
					visited.add(q);
					q.visited = true;
					// all the neighbors
					for (int i = -1; i < 2; i++) {
						for (int j = -1; j < 2; j++) {
							int currR = i + q.r;
							int currC = j + q.c;
							if (currR >= world.sideLength || currR < 0)
								continue;
							if (currC >= world.sideLength || currC < 0)
								continue;
							Node child = world.grid[currC][currR];
							if (child.obstacle == true)
								continue;
							double currF = 0;
							if (type.equals("a star"))
								currF = child.getDist(end) + q.g + child.getDist(q);
							else if (type.equals("greedy"))
								currF = child.getDist(end);
							else if (type.equals("ucs"))
								currF = q.g + child.getDist(q);
							if (visited.contains(child)) // expanded this place
								continue;
							else if (queue.contains(child) && currF >= child.f) {
								continue;
							} else {
								if (!child.obstacle) { // if the child isn't an obstacle
									child.g = q.g + child.getDist(q);
									child.h = child.getDist(end);
									if (type.equals("a star"))
										child.f = child.h + child.g;
									if (type.equals("greedy"))
										child.f = child.h;
									if (type.equals("ucs"))
										child.f = child.g;
									queue.add(child);
									child.parent = q;
									child.fringe = true;
								}
							}
						}
					}
				}
				showNoSolutionDialog();
				running = false;
				timer.start();
			}
		}
	}

	//poorly named function that determines whether the user wants to halt the search or not
	public int dialog() {
		int n = JOptionPane.showConfirmDialog(this, "Expanded for a long time. Continue?", "Warning", JOptionPane.YES_NO_OPTION);
		return n;
	}
	
	//dialog of search failure.
	public void showHaltedMessage() {
		JOptionPane.showMessageDialog(this,
			    "Search halted by user.\nNo solution found.",
			    "No Solution",
			    JOptionPane.WARNING_MESSAGE);
	}
	
	//another type of search failure.
	public void showNoSolutionDialog() {
		JOptionPane.showMessageDialog(this, "Fringe fully expanded.\nNo solution found.", "Search Failure", JOptionPane.PLAIN_MESSAGE);
	}
	
	//success dialog.
	public void showSuccessDialog( int n) {
		JOptionPane.showMessageDialog(this, "Solution found.\n" + n + " nodes expanded.", "Search Success", JOptionPane.PLAIN_MESSAGE);
	}
	
	//does various type of reset. references to the reset defined in the class "Node"
	public void reset(String type) {
		if (type.equals("all")) {
			start = null;
			end = null;
		}
		for (int i = 0; i < world.grid.length; i++)
			for (int j = 0; j < world.grid[0].length; j++)
				world.grid[i][j].reset(type);
	}

	//backtrack for the solution
	public ArrayList backTrack(Node child, ArrayList list) {
		if (child.parent == null) {
			return list;
		} else {
			ArrayList ans = list;
			ans.add(child.parent);
			return backTrack(child.parent, ans);
		}
	}

	//changing the field of view based on direction keys
	@Override
	public void keyPressed(KeyEvent e) {
		// TODO Auto-generated method stub
		if (e.getKeyCode() == KeyEvent.VK_DOWN)
			if (world.topleftY + world.sideLength / world.sideLengthPixel < world.sideLength)
				world.topleftY++;
		if (e.getKeyCode() == KeyEvent.VK_UP)
			if (world.topleftY > 0)
				world.topleftY--;
		if (e.getKeyCode() == KeyEvent.VK_RIGHT)
			if (world.topleftX + world.sideLength / world.sideLengthPixel < world.sideLength)
				world.topleftX++;
		if (e.getKeyCode() == KeyEvent.VK_LEFT)
			if (world.topleftX > 0)
				world.topleftX--;
		repaint();
	}

	// setting and removing obstacles with mouse
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		if (!running) {
			if (e.getY() <= world.sideLength && e.getY() >= 0 && e.getX() <= world.sideLength && e.getY() >= 0) {
				int c = (int) (e.getY() / world.sideLengthPixel + world.topleftY);
				int r = (int) (e.getX() / world.sideLengthPixel + world.topleftX);
				reset("eh");
				if (!world.grid[r][c].start && !world.grid[r][c].end && SwingUtilities.isLeftMouseButton(e)) {
					world.grid[r][c].obstacle = true;
				} else if (!world.grid[r][c].start && !world.grid[r][c].end && SwingUtilities.isRightMouseButton(e)) {
					world.grid[r][c].obstacle = false;
				}
			}
		}
	}

	// setting starting points and ending points
	@Override
	public void mouseClicked(MouseEvent e) {
		// TODO Auto-generated method stub
		// c and r's are messed up... too lazy to fix
		int c = (int) (e.getY() / world.sideLengthPixel + world.topleftY);
		int r = (int) (e.getX() / world.sideLengthPixel + world.topleftX);
		if (!running) {
			if (e.getButton() == MouseEvent.BUTTON1) {
				if (end == null || !(end.c == r && end.r == c)) {
					reset("eh");
					if (start != null)
						start.start = false;
					world.grid[r][c].start = true;
					start = world.grid[r][c];
				}
			}
			if (e.getButton() == MouseEvent.BUTTON3) {
				if (start == null || !(start.c == r && start.r == c)) {
					reset("eh");
					if (end != null)
						end.end = false;
					world.grid[r][c].end = true;
					end = world.grid[r][c];
				}
			}
		}
		requestFocusInWindow();
	}

	//a bunch of not-used classes that had to be implemented for compiling the code
	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	//request focus on the component when mouse is inside
	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub
		setFocusable(true);
		requestFocusInWindow();
	}

	//set non-focusable when the mouse is outside
	//the MapControl instance needs the focus instead
	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub
		setFocusable(false);
	}
}