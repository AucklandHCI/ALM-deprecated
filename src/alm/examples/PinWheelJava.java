package alm.examples;

import alm.*;
import linsolve.OperatorType;

import javax.swing.*;

class PinWheelJava extends JFrame {

    public PinWheelJava() {
        setTitle("PinWheelJava");
        setSize(300, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        ALMLayout layout = new ALMLayout();
        XTab x1 = layout.addXTab();
        XTab x2 = layout.addXTab();
        YTab y1 = layout.addYTab();
        YTab y2 = layout.addYTab();

        layout.addArea(layout.getLeft(), layout.getTop(), x2, y1, new JButton("top-left"));
        layout.addArea(x2, layout.getTop(), layout.getRight(), y2, new JButton("top-right"));
        layout.addArea(x1, y2, layout.getRight(), layout.getBottom(), new JButton("bottom-right"));
        layout.addArea(layout.getLeft(), y1, x1, layout.getBottom(), new JButton("bottom-left"));
        layout.addArea(x1, y1, x2, y2, new JTextArea("center"));

        // Add size constraints
        layout.addConstraint(3, x1, -2, layout.getLeft(), -1, layout.getRight(),
                OperatorType.EQ, 0);
        layout.addConstraint(3, y1, -2, layout.getTop(), -1, layout.getBottom(),
                OperatorType.EQ, 0);
        layout.addConstraint(1, x1, -1, layout.getLeft(), -1, layout.getRight(), 1, x2,
                OperatorType.EQ, 0);
        layout.addConstraint(1, y1, -1, layout.getTop(), -1, layout.getBottom(), 1, y2,
                OperatorType.EQ, 0);

        getContentPane().setLayout(layout);
    }

    public static void main(String[] args) {
        new PinWheelJava().setVisible(true);
    }
}
