package alm.examples.old;

import java.awt.Component;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;

import alm.*;
import commons.Utils;
import linsolve.*;
import alm.editor.ALMPanel;

public class RandomLayout extends ALMPanel {
	ALMLayout le = new ALMLayout();
	public RandomLayout() {
		setLayout(le);
		le.setLayoutSpec(generate(1, 1));
	}
	
	/**
	 * Generate a random layout consisting of JButtons.
	 * @param n_areas The number of random areas to generate.
	 * @param n_constraints The number of random constraints to generate.
	 * @return The resulting random Layout Specification.
	 */
	LayoutSpec generate(int n_areas, int n_constraints) {
		Random rnd = new Random();
		LayoutSpec l = new LayoutSpec();
		l.addArea(l.getLeft(), l.getTop(), l.getRight(), l.getBottom(), null,
				new Dimension(0, 0));
		//Generate n_areas areas by splitting random existing areas in half.
		while (l.getAreas().size() < n_areas) {
			Area a = (Area) l.getAreas().toArray()[rnd.nextInt(l.getAreas()
					.size())];
			a.remove();
			if (rnd.nextDouble() < 0.5) {
				XTab x = l.addXTab();
				l.addArea(a.getLeft(), a.getTop(), x, a.getBottom(), null,
						new Dimension(0, 0));
				l.addArea(x, a.getTop(), a.getRight(), a.getBottom(), null,
						new Dimension(0, 0));
			} else {
				YTab y = l.addYTab();
				l.addArea(a.getLeft(), a.getTop(), a.getRight(), y, null,
						new Dimension(0, 0));
				l.addArea(a.getLeft(), y, a.getRight(), a.getBottom(), null,
						new Dimension(0, 0));
			}
		}
		//Put a JButton in each area.
		for (Area a : l.getAreas()) {
			JButton b = new JButton();
			b.setText("" + (l.getAreas().indexOf(a) + 1));
			b.setName("" + (l.getAreas().indexOf(a) + 1));
			add(b);
			a.setContent(b);
			a.setPreferredContentSize(new Dimension(16 + rnd.nextInt(80),
					16 + rnd.nextInt(80)));
		}
		//Create n_constraints random constraints using existing variables in the specification.
		//Due to random nature the constraints may not be able to be satisfied.
		for (int i = 0; i < n_constraints; i++) {
			int length = 1 + rnd.nextInt(4);
			Summand[] summands = new Summand[length];
			List<Variable> vars = new ArrayList<Variable>();
			for (int k = 0; k < length; k++) {
				Variable v;
				do {
					v = (Variable) l.getVariables().toArray()[rnd.nextInt(l
							.getVariables().size())];
				} while (vars.contains(v));
				summands[k] = new Summand(0.5 - rnd.nextDouble(), v); 
				vars.add(v);
			}
			l.addConstraint(summands,
					((rnd.nextInt(2) == 0) ? OperatorType.EQ
							: (rnd.nextInt(2) == 0) ? OperatorType.GE
									: OperatorType.LE), 0.5 - rnd.nextDouble(),
					rnd.nextDouble());
		}
		return l;
	}

	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	public static Component[] createAndShowGUI(String title, Dimension d, boolean show) {
		// Create and set up the window.
		JFrame frame = new JFrame(title);		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		frame.getContentPane().add(new RandomLayout());

		frame.setPreferredSize(d);
		// Display the window.
		frame.pack();
		if (show) {
			frame.setVisible(true);
		}
		LinkedList <Component> result = Utils.getComponentList(((JPanel) (frame.getContentPane().getComponent(0))).getComponents());
		return result.toArray(new Component[result.size()]);
	}
	
	public static void main(String[] args) throws Exception {
		Component [] c = createAndShowGUI("RandomLayout", new Dimension(500, 500), true);		
		Utils.printComponents("RandomLayout", c);
		Utils.generateGBTest("RandomLayout", c);
	}
}