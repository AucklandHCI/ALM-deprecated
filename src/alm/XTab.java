package alm;

import linsolve.LinearSpec;
import linsolve.Variable;

/**
 * Vertical grid line (x-tab).
 */
public class XTab extends Variable {
	/**
	* Constructor for class <code>X-Tab</code>.
	* X-Tab defines the vertical grid line.
	* @param ls the desired linear specification.
	* @param edge boolean value (?Not used?)
	*/
	public XTab(LinearSpec ls, boolean edge) {
		super((LinearSpec) ls);
	}

	/**
	 * Property signifying if there is a constraint which relates this tab to a
	 * different tab that is further to the left. Only used for reverse
	 * engineering.
	 */
	boolean leftLink = false;
	/**
	* Returns a string that displays the X-Tab.  
	* X-Tab defines the vertical grid line.
	* @return string identifies the X-Tab
	*/
	public String toString() {
		if (getName() != null)
			return getName();
		return "X" + this.getIndex();
	}
}
