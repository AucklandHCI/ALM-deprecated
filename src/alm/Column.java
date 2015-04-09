package alm;

import java.util.ArrayList;
import java.util.List;

import linsolve.Constraint;
import linsolve.OperatorType;

/**
 * Represents a column defined by two x-tabs.
 */
public class Column {
	/**
	 * The layout specification this column belongs to.
	 */
	LayoutSpec ls;

	public XTab left;

	public XTab right;
	Column previous;

	public Column next;

	/**
	 * The constraint that fixes the left tab of this column to the right tab of
	 * the previous column.
	 */
	Constraint previousGlue;
	/**
	* Get the previous glue. The previous glue defines the constraint that 
	* fixes the left tab of this column to the right tab of the previous column.
	* @return <b>Constraint</b> the constraint.
	*/
	public Constraint getPreviousGlue() {
		return previousGlue;
	}
	/**
	* Set the previous glue. The previous glue defines the constraint that 
	* fixes the left tab of this column to the right tab of the previous column.
	* @param previousGlue the desired constraint.
	*/
	public void setPreviousGlue(Constraint previousGlue) {
		this.previousGlue = previousGlue;
	}
	/**
	* Get the next glue. The next glue defines the constraint that 
	* fixes the right tab of this column to the left tab of the next column.
	* @return <b>Constraint</b> the constraint.
	*/
	public Constraint getNextGlue() {
		return nextGlue;
	}
	/**
	* Get the next glue. The next glue defines the constraint that 
	* fixes the right tab of this column to the left tab of the next column.
	* @param nextGlue the desired constraint.
	*/
	public void setNextGlue(Constraint nextGlue) {
		this.nextGlue = nextGlue;
	}

	/**
	 * The constraint that fixes the right tab of this column to the left tab of
	 * the next column.
	 */
	Constraint nextGlue;

	/**
	 * Constraint that fixes the width of the column
	 */
	Constraint width;
	/**
	* Get the width. The width defines the constraint that 
	* fixes the width of the column.
	* @return <b>Constraint</b> the constraint.
	*/
	public Constraint getWidth() {
		return width;
	}
	/**
	* Set the width. The width defines the constraint that 
	* fixes the width of the column.
	* @param width the desired width in Constraint.
	*/
	public void setWidth(Constraint width) {
		this.width = width;
	}
	/**
	* Set the width. The width defines the constraint that 
	* fixes the width of the column.
	* @param value the desired width in double.
	*/
	public void setWidth(double value) {
		Constraint c = ls.addConstraint(1.0, this.right, -1.0, this.left,
				OperatorType.EQ, value);
		c.Owner = this;
		constraints.add(c);
		this.width = c;
	}

	/**
	 * The left boundary of the column.
	 */
	public XTab getLeft() {
		return left;
	}

	/**
	 * The right boundary of the column.
	 */
	public XTab getRight() {
		return right;
	}

	/**
	 * Constraints that are removed when the column is removed.
	 */
	public List<Constraint> constraints = new ArrayList<Constraint>();

	public List<Area> areasContained = new ArrayList<Area>();

	/**
	 * The column directly to the left of this column. May be null.
	 */
	public Column getPrevious() {
		return previous;
	}
	/**
	 * Set a hard left position of the column
	 * 
	 * @param value the desired left position
	 * @throws LinearProgrammingException
	 */
	public void setAsLeft(double value) {
		if (previousGlue != null) {
			previousGlue.remove();
			previousGlue = null;
		}
		if (previous != null) {
			previous = null;
		}
		Constraint c = ls.addConstraint(1.0, this.left, OperatorType.EQ, value);
		c.Owner = this;
		constraints.add(c);
	}
	/**
	* If there should be no column directly to the left of this column, 
	* then we have to separate any such column and can remove any 
	* constraint that was used to glue this column to it. 
	* Otherwise we have to set up the pointers and the glue constraint 
	* accordingly.
	* @param value Column that defines the desired pointers and the glue constraint.
	*/
	public void setPrevious(Column value) {
		// if there should be no column directly left of this column, then we
		// have to
		// separate any such column and can remove any constraint that was used
		// to glue this column to it
		if (value == null) {
			if (previous == null)
				return;
			previous.next = null;
			previous.nextGlue = null;
			previous = null;
			if (previousGlue != null) {
				previousGlue.remove();
				previousGlue = null;
			}
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
		previousGlue = value.nextGlue = value.right.isEqual(left);

		if (nextGlue != null)
			nextGlue.Owner = this;
	}

	/**
	 * The column directly to the right of this column. May be null.
	 */
	public Column getNext() {
		return next;
	}
	/** 
	* If there should be no column directly right of this column, 
	* then we have to separate any such column and can remove any 
	* constraint that was used to glue this column to it.
	* Otherwise we have to set up the pointers and the glue constraint 
	* accordingly.
	* @param value Column that defines the desired pointers and the glue constraint.
	*/
	public void setNext(Column value) {
		// if there should be no column directly right of this column, then we
		// have to
		// separate any such column and can remove any constraint that was used
		// to glue this column to it
		if (value == null) {
			if (next == null)
				return;
			next.previous = null;
			next.previousGlue = null;
			next = null;
			if (nextGlue != null) {
				nextGlue.remove();
				nextGlue = null;
			}
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
		nextGlue = value.previousGlue = right.isEqual(value.left);
	}
	/**
	* Returns a string that displays the area's layout specification
	* @return string identifies the column's specification
	*/
	public String toString() {
		String data = "Column (Left: ";
		if (left != null) {
			data += left.toString();
		} else {
			data += "null";
		}
		if (right != null) {
			data += ", right: " + right.toString();
		} else {
			data += ", bottom: null";
		}
		if (next != null) {
			data += " , Next: Column(left: " + next.left.toString()
					+ ", right: " + next.right.toString() + ")";
		}
		if (previous != null) {
			data += " , Previous: Column(left: " + previous.left.toString()
					+ ", right: " + previous.right.toString();
		}
		data += ")";
		return data;
	}

	/**
	 * Inserts the given column directly to the left of this column.
	 * 
	 * @param c
	 *            the column to insert
	 */
	public void insertBefore(Column c) {
		setPrevious(c.previous);
		setNext(c);
	}

	/**
	 * Inserts the given column directly to the right of this column.
	 * 
	 * @param c
	 *            the column to insert
	 */
	public void insertAfter(Column c) {
		setNext(c.next);
		setPrevious(c);
	}

	/**
	 * Constrains this column to have the same width as the given column.
	 * 
	 * @param column
	 *            the column that should have the same width
	 * @return the resulting same-width constraint
	 */
	public Constraint hasSameWidthAs(Column column) {
		Constraint c = ls.addConstraint(-1, left, 1, right, 1, column.left, -1,
				column.right, OperatorType.EQ, 0);
		c.Owner = this;
		constraints.add(c);
		return c;
	}
	/**
	* Construct a Column based on the given layout specification.
	* @param ls The layout specification which defines the column.
	*/
	protected Column(LayoutSpec ls) {
		this.ls = ls;
		left = new XTab(ls, false);
		right = new XTab(ls, false);
	}
	/**
	* Detach the column from the list of columns, This column will not appear after it is removed.
	*
	*/
	public void unlink() {
		if (previous != null)
			previous.setNext(next);
		else if (next != null)
			next.setPrevious(previous);
	}

	/**
	 * Set a value as the right position of the column
	 * 
	 * @param value
	 * @throws LinearProgrammingException
	 */
	public void setAsRight(double value) {
		if (nextGlue != null) {
			nextGlue.remove();
			nextGlue = null;
		}
		if (next != null) {
			next = null;
		}
		Constraint c = ls
				.addConstraint(1.0, this.right, OperatorType.EQ, value);
		c.Owner = this;
		constraints.add(c);
	}

	/**
	 * Removes the column from the specification.
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
		left = null;
		right = null;
		ls.getColumns().remove(this);
	}

}
