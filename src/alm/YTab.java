package alm;

import linsolve.LinearSpec;
import linsolve.Variable;

/**
 * Horizontal grid line (y-tab).
 */
public class YTab extends Variable {
	/**
	* Constructor for class <code>Y-Tab</code>.
	* Y-Tab defines the horizontal grid line.
	* @param ls the desired linear specification.
	* @param edge boolean value (?Not used?)
	*/
	public YTab(LinearSpec ls, boolean edge) {
		super((LinearSpec) ls);
	}

	/**
	 * Property signifying if there is a constraint which relates this tab to a
	 * different tab that is further to the top. Only used for reverse
	 * engineering.
	 */
	boolean topLink = false;
	/**
	* Returns a string that displays the Y-Tab
	* Y-Tab defines the horizontal grid line.
	* @return string identifies the Y-Tab
	*/
	public String toString() {
		if (getName() != null)
			return getName();
		return "Y" + this.getIndex();
	}
}
