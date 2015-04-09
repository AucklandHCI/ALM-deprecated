package alm.tutorial;

import java.awt.Dimension;
import javax.swing.JButton;
import javax.swing.JFrame;

import linsolve.*;
import alm.ALMLayout;
import alm.Area;
import alm.XTab;
import alm.YTab;

class Tutorial extends JFrame {

    public static void main(String [] args ) {
        @SuppressWarnings("unused")
		Tutorial instance = new Tutorial();
    }

    private Tutorial() {
        super("Tutorial");
        
        setSize(300, 300);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        
        JButton button = new JButton();
        button.setText("Hello, World!");

        ALMLayout alm = new ALMLayout();

		setLayout(alm);
		
		XTab x1 = alm.addXTab();
		XTab x2 = alm.addXTab();
		YTab y1 = alm.addYTab();
		YTab y2 = alm.addYTab();

		// Create an area for the button in the layout
		Area a = alm.addArea(x1, y1, x2, y2, button);

		// Constrain the button to be at least 150x150
		a.setMinContentSize(new Dimension(150, 150));

		// Constrain the padding to be the same size on all edges
		alm.addConstraint(1, x1, -1, alm.getLeft(), -1, alm.getRight(), 1, x2, OperatorType.EQ, 0);
		alm.addConstraint(1, x1, -1, alm.getLeft(), -1, y1, 1, alm.getTop(), OperatorType.EQ, 0);
		alm.addConstraint(1, x1, -1, alm.getLeft(), -1, alm.getBottom(), 1, y2, OperatorType.EQ, 0);
		
		setVisible(true);
    }
}