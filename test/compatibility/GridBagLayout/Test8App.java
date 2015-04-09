package compatibility.GridBagLayout;

import commons.Utils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.LinkedList;

public class Test8App extends JPanel implements ActionListener {
	private IntTextField savingsField = new IntTextField(0, 10);
	private IntTextField contribField = new IntTextField(5000, 10);
	private IntTextField incomeField = new IntTextField(40000, 10);
	private IntTextField currentAgeField = new IntTextField(30, 4);
	private IntTextField retireAgeField = new IntTextField(65, 4);
	private IntTextField deathAgeField = new IntTextField(85, 4);
	private IntTextField inflationPercentField = new IntTextField(3, 4);
	private IntTextField investPercentField = new IntTextField(8, 4);
	private RetireCanvas retireCanvas = new RetireCanvas();
	private JTextArea text = new JTextArea(10, 25);
	private JScrollPane retirePane = new JScrollPane(text);

	public Test8App(LM gbl) {
		JButton help, compute;
		setLayout(gbl.gbl == null ? gbl.agbl : gbl.gbl);
		GridBagConstraints gbc = new GridBagConstraints();
		
		gbc.weightx = 0;
		gbc.weighty = 0;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		add("Current Savings", new JLabel(" Current Savings"), gbl, gbc, 0, 0, 1, 1);
		add("savingsField", savingsField, gbl, gbc, 1, 0, 1, 1);
		add("Annual Contribution", new JLabel(" Annual Contribution"), gbl, gbc, 2, 0, 1, 1);
		add("contribField", contribField, gbl, gbc, 3, 0, 1, 1);
		add("Retirement Income", new JLabel(" Retirement Income"), gbl, gbc, 4, 0, 1, 1);
		add("incomeField", incomeField, gbl, gbc, 5, 0, 1, 1);
		// -------------------------------------------------------------------
		add("Current Age", new JLabel(" Current Age"), gbl, gbc, 0, 1, 1, 1);
		add("currentAgeField", currentAgeField, gbl, gbc, 1, 1, 1, 1);
		add("Retirement Age", new JLabel(" Retirement Age"), gbl, gbc, 2, 1, 1, 1);
		add("retireAgeField", retireAgeField, gbl, gbc, 3, 1, 1, 1);
		add("Life Expectancy", new JLabel(" Life Expectancy"), gbl, gbc, 4, 1, 1, 1);
		add("deathAgeField", deathAgeField, gbl, gbc, 5, 1, 1, 1);
		// -------------------------------------------------------------------
		add("Percent Inflation", new JLabel(" Percent Inflation"), gbl, gbc, 0, 2, 1, 1);
		add("inflationPercentField", inflationPercentField, gbl, gbc, 1, 2, 1, 1);
		add("Prct Invest Return", new JLabel(" Prct Invest Return"), gbl, gbc, 2, 2, 1, 1);
		add("investPercentField", investPercentField, gbl, gbc, 3, 2, 1, 1);
		

		add("Compute", compute = new JButton("Compute"), gbl, gbc, 4, 2, 1, 1);
		compute.addActionListener(this);
		
		add("Help", help = new JButton("Help"), gbl, gbc, 5, 2, 1, 1);
		help.addActionListener(this);
		
		gbc.fill = GridBagConstraints.BOTH;
		gbc.weightx = 100;
		gbc.weighty = 100;
		add("retireCanvas", retireCanvas, gbl, gbc, 0, 3, 4, 8);
		add("retirePane", retirePane, gbl, gbc, 4, 3, 2, 8);
		text.setEditable(false);
		text.setFont(new Font("Monospaced", Font.PLAIN, 10));
	}

	private void add(String name, Component c, LM gbl,
			GridBagConstraints gbc, int x, int y, int w, int h) {
		gbc.gridx = x;
		gbc.gridy = y;
		gbc.gridwidth = w;
		gbc.gridheight = h;
		gbl.setConstraints(c, gbc);
		c.setName(name);
		add(c);
	}

	public void actionPerformed(ActionEvent evt) {
		String arg = evt.getActionCommand();
		if (arg.equals("Compute")) {
			RetireInfo info = new RetireInfo();
			info.savings = savingsField.getValue();
			info.contrib = contribField.getValue();
			info.income = incomeField.getValue();
			info.currentAge = currentAgeField.getValue();
			info.retireAge = retireAgeField.getValue();
			info.deathAge = deathAgeField.getValue();
			info.inflationPercent = inflationPercentField.getValue();
			info.investPercent = investPercentField.getValue();
			text.setText("");
			for (int a = info.currentAge; a <= info.deathAge; a++) {
				text.append(formatAge(a) + formatBalance(info.getBalance(a))
						+ "\n");
			}
			retireCanvas.redraw(info);
		} else if (arg.equals("Help")) {
			text.setText("This is a modified version\n"
					+ "of code by Cornell-Horstmann\n(Core Java).\n\n"
					+ "Click Compute button \nafter entering valid data \n"
					+ "in textfields.\n\nRetirement income and \n"
					+ "annual contribution are\nadjusted to reflect \n "
					+ "yearly inflation.\n\nBalances shown in table \n"
					+ "and graph are given in \nactual dollars. \n");
		}
	}

	String formatAge(int a) {
		String val = "" + a;
		return "Age: " + " ".substring(0, 3 - val.length()) + val;
	}

	String formatBalance(int b) {
		NumberFormat nf = new DecimalFormat("##,###,##0;-##,###,##0");
		String val = nf.format(b);
		return " Balance:" + val;
		// + " ".substring(0,11-val.length()) + val;
	}
	
	/**
	 * Create the GUI and show it. For thread safety, this method should be
	 * invoked from the event dispatch thread.
	 */
	public static Component[] createAndShowGUI(String title, LayoutManager lm,
			Dimension d, boolean show) {
		// Create and set up the window.
		JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Add content to the window.
		if (lm instanceof GridBagLayout)
			frame.add(new Test8App(new LM((GridBagLayout) lm)));
		else
			frame.add(new Test8App(new LM((alm.compatibility.GridBagLayout) lm)));

		frame.setPreferredSize(d);
		// Display the window.
		frame.pack();
		if (show) {
			frame.setVisible(true);
		}
		LinkedList <Component> result = Utils.getComponentList(((JPanel) (frame.getContentPane().getComponent(0))).getComponents());
		return result.toArray(new Component[result.size()]);
	}

	public static void main(String[] args) {
		Component c[] = createAndShowGUI("GridBagLayout-Test8", new GridBagLayout(),
				new Dimension(700, 500), true);
		Utils.printComponents("GridBagLayout-Test8", c);
		c = createAndShowGUI("ALMGridBagLayout-Test8",
				new alm.compatibility.GridBagLayout(), new Dimension(
						700, 500), true);
		Utils.printComponents("ALMGridBagLayout-Test8", c);
		Utils.generateGBTest("Test8", c);
	}
}

/*******************************************************************/
class IntTextField extends JTextField {
	IntTextField(int defval, int size) {
		super("" + defval, size);
		lastValue = "" + defval;
	}

	private void checkValue() {
		try {
			Integer.parseInt(getText().trim() + "0");
			lastValue = getText();
		}
		// GridBagLayout Copyright � 2000 by Ken Slonneger 13
		catch (NumberFormatException e) {
			setText(lastValue);
		}
	}

	int getValue() {
		checkValue();
		try {
			return Integer.parseInt(getText().trim());
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	private String lastValue;
}

/*******************************************************************/
class RetireInfo { // Works correctly only
	int getBalance(int year) // if called for an increasing
	{ // series of consecutive
		if (year == currentAge) // years starting
			balance = savings; // with currentAge
		else if (year <= retireAge) {
			balance = (int) (balance * (1 + investPercent / 100.0));
			balance = balance
					+ (int) (contrib * Math.pow((1 + inflationPercent / 100.0),
							year - currentAge - 1));
		} else {
			balance = balance
					- (int) (income * Math.pow((1 + inflationPercent / 100.0),
							year - currentAge - 1));
			balance = (int) (balance * (1 + investPercent / 100.0));
		}
		// 14 Copyright � 2000 by Ken Slonneger GridBagLayout
		return balance;
	}

	int savings, contrib, income, currentAge;
	int retireAge, deathAge, inflationPercent;
	int investPercent, balance;
}

/*******************************************************************/
class RetireCanvas extends JPanel {
	void redraw(RetireInfo newInfo) {
		info = newInfo;
		repaint();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		if (info == null)
			return;
		int minValue = 0, maxValue = 0;
		for (int yr = info.currentAge; yr <= info.deathAge; yr++) {
			int v = info.getBalance(yr);
			if (minValue > v)
				minValue = v;
			if (maxValue < v)
				maxValue = v;
		}
		if (maxValue == minValue)
			return; // avoid div by 0
		Dimension d = getSize();
		int barWidth = d.width / (info.deathAge - info.currentAge + 1);
		double scale = (double) d.height / (maxValue - minValue);
		// GridBagLayout Copyright � 2000 by Ken Slonneger 15
		for (int yr = info.currentAge; yr <= info.deathAge; yr++) {
			int x1 = (yr - info.currentAge) * barWidth + 1;
			int y1, height;
			int v = info.getBalance(yr);
			int yOrigin = (int) (maxValue * scale);
			if (v >= 0) {
				y1 = (int) ((maxValue - v) * scale);
				height = yOrigin - y1;
			} else {
				y1 = yOrigin;
				height = (int) (-v * scale);
			}
			if (yr <= info.retireAge)
				g.setColor(Color.blue);
			else if (v >= 0)
				g.setColor(Color.green);
			else
				g.setColor(Color.red);
			g.fillRect(x1, y1, barWidth - 2, height);
			g.setColor(Color.black);
			g.drawRect(x1, y1, barWidth - 2, height);
		} // end for yr
	}

	private RetireInfo info = null;
}