package alm.examples;

import alm.*;
import linsolve.OperatorType;
import linsolve.Summand;
import linsolve.Variable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

class RandomLayoutJava extends JFrame {

    public RandomLayoutJava() {
        setTitle("RandomLayoutJava");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ALMLayout layout = new ALMLayout();
        getContentPane().setLayout(layout);
        layout.setLayoutSpec(generate(10, 1));
    }

    /**
     * Generate a random layout consisting of JButtons.
     *
     * @param n_areas       The number of random areas to generate.
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
            ArrayList<Variable> vars = new ArrayList<Variable>();
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

    public static void main(String[] args) {
        new RandomLayoutJava().setVisible(true);
    }
}