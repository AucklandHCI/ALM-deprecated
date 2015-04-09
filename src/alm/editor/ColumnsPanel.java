package alm.editor;

import java.awt.Dimension;

import javax.swing.JPanel;

import alm.ALMException;
import alm.ALMLayout;

import linsolve.OperatorType;

/**
 * Panel for use as the Column tab in the Properties Window.
 */
public class ColumnsPanel extends JPanel {

    private ALMLayout alm;


    public ColumnsPanel(PropertiesPanel p) throws ALMException {
        setSize(new Dimension(400, 200));
        
        
        alm = new ALMLayout();
        alm.setFromPropertyWindow(true);
        
        setLayout(alm);
        
        alm.addConstraint(2.0,  alm.addXTab(), -1.0, alm.getRight(), OperatorType.EQ, 0.0);
    }
}
