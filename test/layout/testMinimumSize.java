package layout;

import static org.junit.Assert.*;

import java.awt.Dimension;
import java.util.Arrays;
import java.util.Collection;

import javax.swing.JButton;
import javax.swing.JFrame;

import linsolve.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import alm.ALMLayout;
import alm.Area;
import alm.XTab;
import alm.YTab;

@RunWith(Parameterized.class)
public class testMinimumSize {
	
	private JFrame window;
	
	public testMinimumSize(AbstractLinearSolver solver){
		window = new JFrame();
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JButton button = new JButton();
        button.setText("Button 1");
        button.setMinimumSize(new Dimension(150,150));
        button.setPreferredSize(new Dimension(150,300));
        
        JButton button2 = new JButton();
        button2.setText("Button 2");
        button2.setMinimumSize(new Dimension(150,10));
        button2.setPreferredSize(new Dimension(150,150));
        
        ALMLayout alm = new ALMLayout();

		window.setLayout(alm);
		
		XTab x1 = alm.addXTab();
		XTab x2 = alm.addXTab();
		YTab y1 = alm.addYTab();
		YTab y2 = alm.addYTab();
		
		XTab b1x1 = alm.addXTab();
		XTab b1x2 = alm.addXTab();
		YTab b1y1 = alm.addYTab();
		YTab b1y2 = alm.addYTab();
		
		Area a = alm.addArea(x1, y1, x2, y2, button);
		Area a1 = alm.addArea(b1x1, b1y1, b1x2, b1y2, button2);
		
		alm.addConstraint(1, x2, -1, b1x1, OperatorType.EQ, 0);
		
		window.pack();
		window.setVisible(true);
	}
	
	@Parameters
	public static Collection<Object[]> data() {
		//TODO: add more solvers
	    Object[][] data = new Object[][] { /*0*/{new LpSolve()}, /*1*/{new KaczmarzSolver()}
	    								    };
	    return Arrays.asList(data);
	}

	@Test
	public void testWidth() {
		assertEquals("Incorrect Width", 300, window.getContentPane().getMinimumSize().width);
	}
	
	@Test
	public void testHeight() {
		assertEquals("Incorrect Height", 150, window.getContentPane().getMinimumSize().height);
	}

}
