package linsolve.performance;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JButton;

import linsolve.OperatorType;
import linsolve.Summand;
import linsolve.Variable;
import alm.Area;
import alm.LayoutSpec;
import alm.XTab;
import alm.YTab;

public class TestDataGenerator {
	
	private static final int MAX_PENALTY = 1000;
	
	private int iterations = 0;
	private int size = 0;
	
	private int counter = 0;
	
	private LayoutSpec spec;
	
	private Random rnd = new Random(1);
	
	public void configure(int iterations, int size){
		this.iterations = iterations;
		this.size = size;
		this.counter  = 0;
	}
	
	public boolean hasNext(){
		spec = generate(size, 0);
		return counter++ < iterations;
	}
	
	public LayoutSpec next(){
		return spec;
	}
	
	private LayoutSpec generate(int n_areas, int n_constraints){
		
		LayoutSpec l = new LayoutSpec();
		l.addArea(l.getLeft(), l.getTop(), l.getRight(), l.getBottom(), null,
				new Dimension(0, 0));
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

		for (Area a : l.getAreas()) {
			JButton b = new JButton();
			b.setText("" + (l.getAreas().indexOf(a) + 1));
			a.setContent(b);
			a.setPreferredContentSize(new Dimension(16 + rnd.nextInt(80),
					16 + rnd.nextInt(80)));
			// a.ExpandRigidity = new SizeF((float)rnd.NextDouble(),
			// (float)rnd.NextDouble());
			// a.ShrinkRigidity = new SizeF((float)rnd.NextDouble(),
			// (float)rnd.NextDouble());
		
			// TODO OLD, remove when tested: a.setShrinkPenalties(new Dimension(rnd.nextInt(MAX_PENALTY), rnd.nextInt(MAX_PENALTY)));
            a.widthPenalty = rnd.nextInt(MAX_PENALTY);
            a.heightPenalty = rnd.nextInt(MAX_PENALTY);

			// a.MinContentSize = new Dimension(rnd.Next(8 + 800 / n_areas),
			// rnd.Next(8 + 600 / n_areas));
			// if (rnd.NextDouble() < 0.05)
			// {
			// a.MaximumSize = new Dimension(
			// a.MinimumSize.Width + rnd.Next(800),
			// a.MinimumSize.Height + rnd.Next(600));
			// }
		}	
				
		for (int i = 0; i < n_constraints; i++) {
			int length = 1 + rnd.nextInt(4);
			Summand[] summands = new Summand[length];
			List<Variable> vars = new ArrayList<Variable>();
			for (int k = 0; k < length; k++) {
				summands[k].setCoeff(0.5 - rnd.nextDouble());
				Variable v;
				do {
					v = (Variable) l.getVariables().toArray()[rnd.nextInt(l
							.getVariables().size())];
				} while (vars.contains(v));
				summands[k].setVar(v);
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
}
