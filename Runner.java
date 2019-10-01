import java.awt.FlowLayout;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class Runner {
	public static void main(String args[]) {
		JFrame frame = new JFrame("Maze Solver");
		frame.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.add(Map.getInstance());
		SearchAgent smith = new SearchAgent();
		MapControl controlPad = new MapControl();
		controlPad.world = smith.world;
		controlPad.smith = smith;
		controlPad.setOpaque(true);
		frame.add(smith);
		frame.add(controlPad);

		frame.setSize(1280, 720);
		frame.pack();
		frame.setVisible(true);
		frame.setResizable(false);
		smith.setFocusable(true);
	}
}
