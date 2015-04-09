package alm;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import linsolve.Constraint;
import linsolve.OperatorType;

/**
 * Represents a row defined by two y-tabs.
 */
public class Row {
	/**
	 * The layout specification this row belongs to.
	 */
	LayoutSpec ls;

	public YTab top;

	public YTab bottom;
	Row previous;

	public Row next;

	/**
	 * The constraint that fixes the top tab of this row to the bottom tab of
	 * the previous row.
	 */
	Constraint previousGlue;
	Constraint height;

	public List<Area> areasContained = new ArrayList<Area>();

	/**
	 * Get the previous glue. The previous glue is the constraint that fixes the 
	 * top tab of this row to the bottom tab of the previous row.
	 * @return the defined previous clue
	 */
	public Constraint getPreviousGlue() {
		return previousGlue;
	}

	/**
	 * The constraint that fixes the bottom tab of this row to the top tab of
	 * the next row.
	 */
	Constraint nextGlue;

	/**
	 * The top boundary of the row.
	 */
	public YTab getTop() {
		return top;
	}
	/**
	 * Set the top boundary of the row.
	 * @param top a Y-tab that defines the desired top boundary
	 */
	public void setTop(YTab top) {
		this.top = top;
	}

	/**
	 * The bottom boundary of the row.
	 */
	public YTab getBottom() {
		return bottom;
	}
	/**
	 * Set the bottom boundary of the row.
	 * @param bottom a Y-tab that defines the desired bottom boundary
	 */
	public void setBottom(YTab bottom) {
		this.bottom = bottom;
	}

	/**
	 * Constraints that are removed when the row is removed.
	 */
	public List<Constraint> constraints = new ArrayList<Constraint>();

	/**
	 * The row directly above this row. May be null.
	 */
	public Row getPrevious() {
		return previous;
	}
	/**
	 * Set the row directly above this row.
	 * @param value Row that defines the desired previous row
	 */
	public void setPrevious(Row value) {
		// if there should be no row directly above this row, then we have to
		// separate any such row and can remove any constraint that was used
		// to glue this row to it
		if (value == null) {
			if (previous == null)
				return;
			previous.next = null;
			previous.nextGlue = null;
			previous = null;
			if (previousGlue != null)
				previousGlue.remove();
			previousGlue = null;
			return;
		}

		// otherwise we have to set up the pointers and the glue constraint
		// accordingly
		if (value.next != null)
			value.setNext(null);
		if (previous != null)
			setPrevious(null);

		previous = value;
		previous.next = this;
		/*
		 * try{ value.bottom.getIndex(); }catch(LinearProgrammingException le){
		 * value.bottom = new YTab(ls); }
		 */
		previousGlue = value.nextGlue = value.bottom.isEqual(top);
		previousGlue.Owner = this;
	}

	/**
	 * The row directly below this row. May be null.
	 */
	public Row getNext() {
		return next;
	}
	/**
	 * Set the row directly below this row.
	 * @param value Row that defines the desired next row
	 */
	public void setNext(Row value) {
		// if there should be no row directly below this row, then we have to
		// separate any such row and can remove any constraint that was used
		// to glue this row to it
		if (value == null) {
			if (next == null)
				return;
			next.previous = null;
			next.previousGlue = null;
			next = null;
			if (nextGlue != null)
				nextGlue.remove();
			nextGlue = null;
			return;
		}

		// otherwise we have to set up the pointers and the glue constraint
		// accordingly
		if (value.previous != null)
			value.setPrevious(null);
		if (next != null)
			setNext(null);

		next = value;
		next.previous = this;
		nextGlue = value.previousGlue = bottom.isEqual(value.top);
		nextGlue.Owner = this;

	}
	/**
	* Returns a string that displays the Row. 
	* A row is defined by two y-tabs.
	* @return string identifies the Row
	*/
	public String toString() {
		String data = "Row(Top: ";
		if (top != null) {
			data += top.toString();
		} else {
			data += "null";
		}
		if (bottom != null) {
			data += ", bottom: " + bottom.toString();
		} else {
			data += ", bottom: null";
		}
		if (next != null) {
			data += " , Next: Row(Top: " + next.top.toString() + ", bottom: "
					+ next.bottom.toString() + ")";
		}
		if (previous != null) {
			data += " , Previous: Row(Top: " + previous.top.toString()
					+ ", bottom: " + previous.bottom.toString();
		}
		data += ")";
		return data;
	}

	/**
	 * Inserts the given row directly above this row.
	 * 
	 * @param r
	 *            the row to insert
	 */
	public void insertBefore(Row r) {
		setPrevious(r.previous);
		setNext(r);
	}
	/**
	* Return if the height of the row is set.
	* @return <b>true</b> if the height is set, <b>false</b> otherwise.
	*/
	public boolean heightNotSet() {
		if (height == null) {
			return true;
		} else {
			return false;
		}
	}
	/**
	* Same as getPreviousGlue()
	*/
	public Constraint getPreviousGlue1() {
		return previousGlue;
	}
	/**
	 * Set the previous glue. The previous glue is the constraint that fixes the 
	 * top tab of this row to the bottom tab of the previous row.
	 * @param previousGlue Constraint which defines the desired previous glue
	 */
	public void setPreviousGlue(Constraint previousGlue) {
		this.previousGlue = previousGlue;
	}
	/**
	* Return the height of the row
	* @return Constraint which defines the height
	*/
	public Constraint getHeight() {
		return height;
	}
	/**
	* Define the height of the row
	* @param height Constraint that will be used as the height constraint
	*/
	public void setHeight(Constraint height) {
		this.height = height;
		height.Owner = this;
		constraints.add(height);
	}
	/**
	* Define the height of the row
	* @param value double that will be used to setup the height constraint
	*/
	public void setHeight(double value) {
		Constraint c = ls.addConstraint(-1, top, 1, bottom, OperatorType.EQ,
				value);
		c.Owner = this;
		constraints.add(c);
		height = c;
	}
	/**
	 * Return nextGlue, the constraint that fixes the bottom tab of this row 
	 * to the top tab of the next row.
	 * @return Constraint which defines the nextGlue
	 */
	public Constraint getNextGlue() {
		return nextGlue;
	}
	/**
	 * Define nextGlue, the constraint that fixes the bottom tab of 
	 * this row to the top tab of the next row.
	 * @param nextGlue the desired Constraint for the nextGlue
	 */
	public void setNextGlue(Constraint nextGlue) {
		this.nextGlue = nextGlue;
	}

	/**
	 * Inserts the given row directly below this row.
	 * 
	 * @param r
	 *            the row to insert
	 */
	public void insertAfter(Row r) {
		setNext(r.next);
		setPrevious(r);
	}

	/**
	 * Constrains this row to have the same height as the given row.
	 * 
	 * @param row
	 *            the row that should have the same height
	 * @return the resulting same-height constraint
	 */
	public Constraint hasSameHeightAs(Row row) {
		Constraint c = ls.addConstraint(-1, top, 1, bottom, 1, row.top, -1,
				row.bottom, OperatorType.EQ, 0);
		c.Owner = this;
		constraints.add(c);
		height = c;
		return c;
	}
	/**
	* Remove the height of the row
	*/
	public void removeHeightContraints() {
		if (height != null) {
			constraints.remove(height);
			height.remove();
		}
	}
    /**
     * Constrains this row so its height multiplied with the given value 
     * will equal the height of the given row. 
     * Height_{this} \cdot value = Height_{row}
     * 
     * @param row
     *            the row that should have the same height
     * @param value
     *            the value used as the factor
     * @return the resulting same-height constraint
     */
	public Constraint hasSameHeightAs(Row row, int value) throws Exception {
		Constraint c = ls.addConstraint(-(value), top, value, bottom, 1,
				row.top, -1, row.bottom, OperatorType.EQ, 0);
		c.Owner = this;
		constraints.add(c);
		height = c;
		return c;
	}
	/**
	 * Constructor for class <code>Row</code>.
	 * @param ls the LayoutSpec that defines the row
	 */
	protected Row(LayoutSpec ls) {
		this.ls = ls;
		top = new YTab(ls, false);
		bottom = new YTab(ls, false);
	}
	/**
	 * Constructor for class <code>Row</code>.
	 * @param ls the LayoutSpec that defines the row
	 * @param x the YTab that defines the top boundary 
	 * @param y the YTab that defines the bottom boundary 
	 */
	Row(LayoutSpec ls, YTab x, YTab y) {
		this.ls = ls;
		top = x;
		bottom = y;
	}
	/**
	 * Set the row to attach to the top of the layout.
	 * @return the created constraint
	 */
	public Constraint setAsTop() {
		if (previousGlue != null) {
			previousGlue.remove();
			previousGlue = null;
		}
		if (previous != null) {
			previous = null;
		}
		Constraint c = ls.addConstraint(1.0, this.top, -1.0, ls.getTop(),
				OperatorType.EQ, 0);
		constraints.add(c);
		c.Owner = this;
		return c;
	}
	/**
	 * Set a value as the top position of the row
	 * 
	 * @param value the desired top position
	 * @return the created constraint
	 */
	public Constraint setAsTop(double value) {
		if (previousGlue != null) {
			previousGlue.remove();
			previousGlue = null;
		}
		if (previous != null) {
			previous = null;
		}
		Constraint c = ls.addConstraint(1.0, this.top, OperatorType.EQ, value);
		constraints.add(c);
		c.Owner = this;
		return c;
	}
	/**
	 * Set a constraint as the top position of the row
	 * 
	 * @param c the constraint specifying the desired top position
	 * @return the created constraint
	 */
	public Constraint setAsTop(Constraint c) {
		if (previousGlue != null) {
			previousGlue.remove();
			previousGlue = null;
		}
		if (previous != null) {
			previous = null;
		}
		constraints.add(c);
		c.Owner = this;
		return c;
	}
	/**
	* Remove the row from the list of rows. This row will not appear after it is reoved.
	*
	*/
	public void unlink() {
		System.out.println("unlink");
		if (previous != null)
			previous.setNext(next);
		else if (next != null)
			next.setPrevious(previous);
	}

	/**
	 * Removes the row from the specification.
	 */
	public void remove() {
		if (previous != null) {
			previous.setNext(next);
		}
		if (next != null) {
			next.setPrevious(null);
		}
		if (nextGlue != null)
			nextGlue.remove();
		for (Constraint c : constraints) {
			c.remove();
		}
		top = null;
		bottom = null;
		ls.getRows().remove(this);
	}
	/**
	 * Set the row to attach to the bottom of the layout.
	 * 
	 */
	public void setAsBottom() {
		if (nextGlue != null) {
			nextGlue.remove();
			nextGlue = null;
		}
		if (next != null) {
			next = null;
		}
		Constraint c = ls.addConstraint(1.0, this.bottom, -1.0, ls.getBottom(),
				OperatorType.EQ, 0);
		constraints.add(c);

	}
	/**
	 * Set a value as the bottom position of the row
	 * 
	 * @param value the desired bottom position
	 * @return the created constraint
	 */
	public Constraint setAsBottom(double value) {
		if (nextGlue != null) {
			nextGlue.remove();
			nextGlue = null;
		}
		if (next != null) {
			next = null;
		}

		// Constraint c = ls.addConstraint(1.0, this.bottom, -1.0,
		// ls.getBottom(), OperatorType.EQ, 0);
		Constraint c = ls.addConstraint(1.0, this.bottom, OperatorType.EQ,
				value);
		constraints.add(c);
		return c;

	}
	/**
	* Get the bounds 
	* @return the bounds of this row in Rectangle as (0, top, 0, height)
	*/
	public Rectangle getBounds() {
		return new Rectangle(0, (int) top.getValue(), 0, (int) (bottom
				.getValue() - top.getValue()));
	}

}
