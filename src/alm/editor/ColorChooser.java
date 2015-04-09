package alm.editor;

import java.awt.Color;

import linsolve.Variable;

/**
 * Class that gets a color for a given Variable.
 */
public class ColorChooser {

	private static ColorChooser instance;

	private ColorChooser() {

	}

	/**
	 * Gets an instantiated version of ColorChooser.
	 * 
	 * @return The ColorChooser object.
	 */
	public static ColorChooser getInstance() {
		if (instance == null)
			instance = new ColorChooser();
		return instance;
	}

	/**
	 * Get a color for the given variable.
	 * 
	 * @param v The Variable to get a color for.
	 * @return The color for the variable.
	 */
	public Color getColor(Variable v) {		
		if (v == null)
			return Color.black;
		
		return getColor(v.getIndex());
	}

	/**
	 * Get the color for the given index.
	 * 
	 * @param i The index to get a color for.
	 * @return The color for the index.
	 */
	public Color getColor(int i) {
		Color c = null;
		switch (i % 6) {
		case 0:
			c = new Color(255, 0, 0);
			break;
		case 1:
			c = new Color(0, 255, 0);
			break;
		case 2:
			c = new Color(0, 0, 255);
			break;
		case 3:
			c = new Color(255, 255, 0);
			break;
		case 4:
			c = new Color(0, 255, 255);
			break;
		case 5:
			c = new Color(255, 0, 255);
			break;
		}
		
		int div6 = (i / 6) + 1;
		
		int r = (int) (c.getRed() * Math.pow(0.80, div6));
		int g = (int) (c.getGreen() * Math.pow(0.80, div6));
		int b = (int) (c.getBlue() * Math.pow(0.80, div6));
		return new Color(r, g, b);
	}

}
