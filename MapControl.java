import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.Hashtable;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.Timer;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class MapControl extends JPanel implements ActionListener, ChangeListener, ItemListener {
	public Grid world;
	public SearchAgent smith;
	public Timer timer;
	public JButton searchButton, resetButton;
	public JCheckBox animation;
	
	//GUI for the user to make changes to the search and the map itself
	public MapControl() {
		super(new BorderLayout());
		this.setPreferredSize(new Dimension(560, 720));

		ActionListener taskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				checkButtonAvailable();
			}
		};
		timer = new Timer(10, taskPerformer);
		timer.start();

		// creating the options of searches
		JRadioButton aStarButton = new JRadioButton("A Star Search");
		aStarButton.setActionCommand("a star");
		aStarButton.setSelected(true);
		JRadioButton greedyButton = new JRadioButton("Greedy Search");
		greedyButton.setActionCommand("greedy");
		JRadioButton ucsButton = new JRadioButton("UCS");
		ucsButton.setActionCommand("ucs");

		// grouping the search options together for a logical structure
		ButtonGroup group = new ButtonGroup();
		group.add(aStarButton);
		group.add(greedyButton);
		group.add(ucsButton);
		aStarButton.addActionListener(this);
		greedyButton.addActionListener(this);
		ucsButton.addActionListener(this);

		// the search button itself
		searchButton = new JButton("Start Search");
		searchButton.setPreferredSize(new Dimension(80, 20));
		searchButton.setMaximumSize(getSize());

		searchButton.setActionCommand("start");
		searchButton.addActionListener(this);
		searchButton.setEnabled(false);

		resetButton = new JButton("Reset");
		resetButton.setActionCommand("reset");
		resetButton.addActionListener(this);
		resetButton.setEnabled(true);

		// checkbox that turns animation on and off
		animation = new JCheckBox("Animation");
		animation.setSelected(true);
		animation.addItemListener(this);

		// the panel that will contain all the search options
		JPanel searchList = new JPanel(new GridLayout(3, 0, 0, 0));
		// searchList.setPreferredSize(new Dimension(560, 720));
		searchList.add(aStarButton);
		searchList.add(greedyButton);
		searchList.add(ucsButton);

		// slider that's in charge of the zoom of the grid on the left
		JSlider zoom = new JSlider(JSlider.HORIZONTAL, 0, 8, 4);
		zoom.addChangeListener(this);
		zoom.setMajorTickSpacing(1);
		zoom.setPaintTicks(true);
		//labels on the slider
		Hashtable table = new Hashtable();
		table.put(0, new JLabel("Out"));
		table.put(8, new JLabel("In"));
		zoom.setLabelTable(table);
		zoom.setPaintLabels(true);

		// loading all the components into the container
		// emptySpaces for aesthetics

		JPanel smallContainer = new JPanel(new BorderLayout());
		smallContainer.setPreferredSize(new Dimension(300, 150));
		smallContainer.add(searchList, BorderLayout.CENTER);
		smallContainer.add(resetButton, BorderLayout.PAGE_END);
		smallContainer.add(animation, BorderLayout.PAGE_START);

		JPanel container = new JPanel(new BorderLayout());
		container.setPreferredSize(new Dimension(300, 240));
		container.add(zoom, BorderLayout.PAGE_END);
		container.add(smallContainer, BorderLayout.CENTER);
		container.add(searchButton, BorderLayout.PAGE_START);

		JPanel topEmptySpace = new JPanel();
		topEmptySpace.setPreferredSize(new Dimension(560, 240));
		JPanel bottomEmptySpace = new JPanel();
		bottomEmptySpace.setPreferredSize(new Dimension(560, 240));
		JPanel leftEmptySpace = new JPanel();
		leftEmptySpace.setPreferredSize(new Dimension(130, 240));
		JPanel rightEmptySpace = new JPanel();
		rightEmptySpace.setPreferredSize(new Dimension(130, 240));

		add(topEmptySpace, BorderLayout.PAGE_START);
		add(container, BorderLayout.CENTER);
		add(bottomEmptySpace, BorderLayout.PAGE_END);
		add(rightEmptySpace, BorderLayout.LINE_END);
		add(leftEmptySpace, BorderLayout.LINE_START);
	}

	//set some buttons to be unavailable once the search has started
	public void checkButtonAvailable() {
		if (smith != null) {
			if (smith.start != null && smith.end != null && !smith.running) {
				searchButton.setEnabled(true);
			} else {
				searchButton.setEnabled(false);
			}
			if (smith.running)
				resetButton.setEnabled(false);
			else
				resetButton.setEnabled(true);
		}
	}

	// handles all the things done to the buttons
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().equals("start") && !smith.running) {
			smith.search(smith.start, smith.end, smith.mode);
		} else if (e.getActionCommand().equals("reset")) {
			Object[] possibilities = {"Entire Map", "Just Obstacles", "Previous Search Track"};
			String resetType = (String) JOptionPane.showInputDialog(this, "What do you want to reset?",
					"Choose Reset Options", JOptionPane.PLAIN_MESSAGE, null, possibilities, "Previous Search Track");
			if (resetType != null) {
				if (resetType.equals("Entire Map"))
					resetType = "all";
				else if (resetType.equals("Just Obstacles"))
					resetType = "obstacle";
				smith.reset(resetType);
			}
		} else
			smith.mode = e.getActionCommand();
	}

	//handles the slider
	@Override
	public void stateChanged(ChangeEvent e) {
		JSlider source = (JSlider) e.getSource();
		if (!source.getValueIsAdjusting()) {
			int zoomLevel = (int) source.getValue();
			if (zoomLevel >= 0 && zoomLevel <= 8) {
				if (zoomLevel == 0) {
					world.sideLengthPixel = 5;
				} else if (zoomLevel == 1) {
					world.sideLengthPixel = 10;
				} else if (zoomLevel == 2) {
					world.sideLengthPixel = 16;
				} else if (zoomLevel == 3) {
					world.sideLengthPixel = 24;
				} else if (zoomLevel == 4) {
					world.sideLengthPixel = 36;
				} else if (zoomLevel == 5) {
					world.sideLengthPixel = 45;
				} else if (zoomLevel == 6) {
					world.sideLengthPixel = 48;
				} else if (zoomLevel == 7) {
					world.sideLengthPixel = 60;
				} else if (zoomLevel == 8) {
					world.sideLengthPixel = 72;
				}
				if (world.topleftY + world.sideLength / world.sideLengthPixel > world.sideLength)
					world.topleftY = world.sideLength - world.sideLength / world.sideLengthPixel;
				if (world.topleftX + world.sideLength / world.sideLengthPixel > world.sideLength)
					world.topleftX = world.sideLength - world.sideLength / world.sideLengthPixel;
			}
		}
	}

	//handles the checkbox
	@Override
	public void itemStateChanged(ItemEvent e) {
		// TODO Auto-generated method stub
		Object source = e.getItemSelectable();
		if (e.getStateChange() == ItemEvent.DESELECTED) {
			if (source == animation) {
		        smith.animate = false;
		    }
		}else if (e.getStateChange() == ItemEvent.SELECTED) {
			if (source == animation) {
		        smith.animate = true;
		    }
		}
	}
}