package alm;

import java.awt.Dimension;
import java.awt.Insets;
import java.awt.Point;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.table.JTableHeader;

import linsolve.Constraint;
import linsolve.OperatorType;
import commons.Penalties;

/**
 * Rectangular area in the GUI, defined by a tab on each side.
 */
public class Area {
	/**
	 * Minimum possible size. Use this if there is no lower bound.
	 */
	public static Dimension MIN_SIZE = new Dimension(0, 0);

	/**
	 * Maximum possible size. Use this if there is no upper bound.
	 */
	public static Dimension MAX_SIZE = new Dimension(Integer.MAX_VALUE,
			Integer.MAX_VALUE);

	/**
	 * Undefined size. Used if a certain size constraint is not set.
	 */
	public static Dimension UNDEFINED_SIZE = new Dimension(-1, -1);

	/**
	 * The layout specification this area belongs to.
	 */
	LayoutSpec ls;

	// the boundaries (tabs) of the area
	public XTab left;
	public XTab right;
	public YTab top;
	public YTab bottom;
	
	public Row row;
	Column column;
	
	/**
	 * Widget that is shown in the area.
	 */
	public JComponent content;

	/**
	 * A list of constraints which are removed form the speciifcation when the
	 * area is removed.
	 */
	List<Constraint> constraints = new ArrayList<Constraint>();

	Dimension minContentSize = new Dimension(0, 0);
	Dimension maxContentSize = MAX_SIZE;

	/**
	 * Size constraint for the content. Valid even if the content is actually in
	 * a child area.
	 */
	Constraint minContentWidth, maxContentWidth, minContentHeight,
			maxContentHeight;

	public Dimension preferredContentSize = UNDEFINED_SIZE;

    // TODO remove
	//Dimension shrinkPenalties = new Dimension(2, 2);
	public double widthPenalty;
    public double heightPenalty;

    Dimension growPenalties = new Dimension(1, 1);


	double contentAspectRatio = Double.NaN;
	Constraint contentAspectRatioC;

	/**
	 * Determines if the PreferredContentSize is set automatically. Manual
	 * changes of PreferredContentSize are ignored unless set to false.
	 */
	private boolean autoPreferredContentSize = false;

	/**
	 * The soft constraints used for setting a preferred width.
	 */
	Constraint preferredContentWidth;

	/**
	 * The soft constraints used for setting a preferred height.
	 */
	Constraint preferredContentHeight;

	/**
	 * This area is used if the borders of the control are not the same as the
	 * borders of the area, i.e. if the control is smaller than the area due to
	 * margins or alignment. In case a childArea is present, many of the
	 * operations (such as those for setting content sizes) are delegated to the
	 * childArea.
	 */
	public Area childArea; //pointer to child area Made it public

    /**
     * Set the child area
     *
     * @param child
     */
    public void setChildArea(Area child) {
        this.childArea= child;
    }

	HorizontalAlignment horizontalAlignment = HorizontalAlignment.FILL;
	VerticalAlignment vAlignment = VerticalAlignment.FILL;
	public int leftInset = 0;
	int topInset = 0;
	int rightInset = 0;
	int bottomInset = 0;

	/**
	 * The constraint for setting inset and alignment on the left.
	 */
	Constraint leftConstraint;

	/**
	 * The constraint for setting inset and alignment at the top.
	 */
	Constraint topConstraint;

	/**
	 * The constraint for setting inset and alignment on the right.
	 */
	Constraint rightConstraint;

	/**
	 * The constraint for setting inset and alignment at the bottom.
	 */
	Constraint bottomConstraint;

	/**
	 * Left tab of the area.
	 */
	public XTab getLeft() {
		return left;
	}

	/**
	* Set the left tab of the area
	* @param value The tab specification to be used.
	*/
	public void setLeft(XTab value) {
		left = value;
		column = null; // since we changed an individual x-tab we do not align
		// to a Column anymore

		if (childArea == null) {
			// the respective minimum constraint needs to use the new tab
			minContentWidth.setLeftSide(-1, left, 1, right);

			// if a maximum constraint was set, then it nees to use the new tab
			if (maxContentWidth != null)
				maxContentWidth.setLeftSide(-1, left, 1, right);
		} else
			updateHorizontal(); // the constraints between outer area and
		// childArea need to use the new tab
		ls.invalidateLayout();
	}

	/**
	 * Right tab of the area.
	 */
	public XTab getRight() {
		return right;
	}

	/**
	* Set the rigth tab of the area
	* @param value The tab specification to be used.
	*/
	public void setRight(XTab value) {
		right = value;
		column = null; // since we changed an individual x-tab we do not align
		// to a Column anymore

		if (childArea == null) {
			// the respective minimum constraint needs to use the new tab
			minContentWidth.setLeftSide(-1, left, 1, right);

			// if a maximum constraint was set, then it nees to use the new tab
			if (maxContentWidth != null)
				maxContentWidth.setLeftSide(-1, left, 1, right);
		} else
			updateHorizontal(); // the constraints between outer area and
		// childArea need to use the new tab
		ls.invalidateLayout();
	}

	/**
	 * Top tab of the area.
	 */
	public YTab getTop() {
		return top;
	}

	/**
	* Set the top tab of the area
	* @param value the tab specification to be used.
	*/
	public void setTop(YTab value) {
		top = value;
		row = null; // since we changed an individual y-tab we do not align to a
		// Row anymore

		if (childArea == null) {
			// the respective minimum constraint needs to use the new tab
			minContentHeight.setLeftSide(-1, top, 1, bottom);

			// if a maximum constraint was set, then it nees to use the new tab
			if (maxContentHeight != null)
				maxContentHeight.setLeftSide(-1, top, 1, bottom);
		} else
			updateVertical(); // the constraints between outer area and
		// childArea need to use the new tab
		ls.invalidateLayout();
	}

	/**
	 * Bottom tab of the area.
	 */
	public YTab getBottom() {
		return bottom;
	}

	/**
	* Set the bottom tab of the area
	* @param value the tab specification to be used.
	*/
	public void setBottom(YTab value) {
		bottom = value;
		row = null; // since we changed an individual y-tab we do not align to a
		// Row anymore

		if (childArea == null) {
			// the respective minimum constraint needs to use the new tab
			minContentHeight.setLeftSide(-1, top, 1, bottom);

			// if a maximum constraint was set, then it nees to use the new tab
			if (maxContentHeight != null)
				maxContentHeight.setLeftSide(-1, top, 1, bottom);
		} else
			updateVertical(); // the constraints between outer area and
		// childArea need to use the new tab
		ls.invalidateLayout();
	}

	/**
	 * The row that defines the top and bottom tabs. May be null.
	 */
	public Row getRow() {
		return row;
	}

	/**
	* Set the row that defines the top and bottom tabs.
	* @param value the row specification to be used.
	*/
	public void setRow(Row value) {
		setTop(value.getTop());
		setBottom(value.getBottom());
		row = value;
		ls.invalidateLayout();
	}

	/**
	 * The column that defines the left and right tabs. May be null.
	 */
	public Column getColumn() {
		return column;
	}

	/**
	* Set the column that defines the left and right tabs.
	* @param value the column specification to be used.
	*/
	public void setColumn(Column value) {
		setLeft(value.getLeft());
		setRight(value.getRight());
		column = value;
		ls.invalidateLayout();
	}

	/**
	 * The control that is the content of the area.
	 */
	public JComponent getContent() {
		// if we are using a childArea then it contains the control,
		// otherwise this area contains the control directly
		return (childArea == null) ? content : childArea.getContent();
	}

	/**
	* Setup the control that is the content of the area.
	* @param value the JComponent that will be used.
	*/
	public void setContent(JComponent value) {
		if (childArea == null)
			content = value;
		else
			childArea.content = value;
		ls.invalidateLayout();
	}

	/**
	 * Left tab of the area's content. May be different from the left tab of the
	 * area.
	 */
	public XTab getContentLeft() {
		return (childArea == null) ? left : childArea.left;
	}

	/**
	 * Top tab of the area's content. May be different from the top tab of the
	 * area.
	 */
	public YTab getContentTop() {
		return (childArea == null) ? top : childArea.top;
	}

	/**
	 * Right tab of the area's content. May be different from the right tab of
	 * the area.
	 */
	public XTab getContentRight() {
		return (childArea == null) ? right : childArea.right;
	}

	/**
	 * Bottom tab of the area's content. May be different from the bottom tab of
	 * the area.
	 */
	public YTab getContentBottom() {
		return (childArea == null) ? bottom : childArea.bottom;
	}

	/**
	 * Minimum size of the area's content. May be different from the minimum
	 * size of the area.
	 */
	public Dimension getMinContentSize() {
		return (childArea == null) ? minContentSize : childArea.minContentSize;
	}
	/**
	* Set the minimum size of the area's content.
	* @param value Dimension that defines the desired minimum size.
	*/
	public void setMinContentSize(Dimension value) {
		if (childArea == null) {
			minContentSize = value;
			minContentWidth.setRightSide(minContentSize.getWidth());
			minContentHeight.setRightSide(minContentSize.getHeight());
		} else
			childArea.setMinContentSize(value);
		ls.invalidateLayout();
	}

	/**
	 * Maximum size of the area's content. May be different from the maximum
	 * size of the area.
	 */
	public Dimension getMaxContentSize() {
		return (childArea == null) ? maxContentSize : childArea.maxContentSize;
	}
	/**
	* Set the maximal size of the area's content.
	* @param value Dimension that defines the desired maximal size.
	*/
	public void setMaxContentSize(Dimension value) {
		if (childArea == null) {
			maxContentSize = value;
			if (maxContentWidth == null) { // no max constraints set yet
				maxContentWidth = ls.addConstraint(-1, left, 1, right,
						OperatorType.LE, maxContentSize.getWidth());
				maxContentWidth.Owner = this;
				constraints.add(maxContentWidth);
				maxContentHeight = ls.addConstraint(-1, top, 1, bottom,
						OperatorType.LE, maxContentSize.getHeight());
				maxContentHeight.Owner = this;
				constraints.add(maxContentHeight);
			} else {
				maxContentWidth.setRightSide(maxContentSize.getWidth());
				maxContentHeight.setRightSide(maxContentSize.getHeight());
			}
		} else {
			childArea.setMaxContentSize(value);
		}
		ls.invalidateLayout();
	}

	/**
	 * Preferred size of the area's content. May be different from the preferred
	 * size of the area. Manual changes of PreferredContentSize are ignored
	 * unless autoPreferredContentSize is set to false.
	 */
	public Dimension getPreferredContentSize() {
		return (childArea == null) ? preferredContentSize
				: childArea.preferredContentSize;
	}
	/**
	* Set the prefered size of the area's content. Manual changes
	* of PreferredContentSize are ignored unless
	* autoPreferredContentSize is set to false.
	* @param value Dimension that defines the prefered size.
	*/
	public void setPreferredContentSize(Dimension value) {
		if (childArea == null) {
			preferredContentSize = value;
			if (preferredContentWidth == null) { // no pref constraints set yet
				// TODO: consider also grow penalties by adding two inequalities
				// instead of just one equality?
				// e.g. growPenalties.getHeight() / .getWidth()
				preferredContentWidth = ls.addConstraint(-1, left, 1, right,
						OperatorType.EQ, preferredContentSize.getWidth(),
						widthPenalty);
				preferredContentWidth.Owner = this;
				constraints.add(preferredContentWidth);
				preferredContentHeight = ls.addConstraint(-1, top, 1, bottom,
						OperatorType.EQ, preferredContentSize.getHeight(),
						heightPenalty);

				preferredContentHeight.Owner = this;
				constraints.add(preferredContentHeight);
			} else {
				preferredContentWidth.setRightSide(value.getWidth());
				preferredContentHeight.setRightSide(value.getHeight());
			}
		} else
			childArea.setPreferredContentSize(value);
		ls.invalidateLayout();
	}

	/**
	 * The reluctance with which the area's content shrinks below its preferred
	 * size. The bigger the less likely is such shrinking.
	 */
	public double getShrinkPenaltyWidth() {
		return (childArea == null) ? widthPenalty
				: childArea.widthPenalty;
	}

    public double getShrinkPenaltyHeight() {
        return (childArea == null) ? heightPenalty
                : childArea.heightPenalty;
    }

	/**
	* Set the reluctance with which the area's content shrinks below its preferred
	* size. The bigger the less likely is such shrinking.
	*/
	public void setShrinkPenalties(double width, double height) {
		if (childArea == null) {
			widthPenalty = width;
            heightPenalty = height;

			// penalties are only relevant if a preferred size is set
			if (preferredContentWidth != null) {
				// TODO: was previously setPenaltyNeg; maybe use two
				// inequalities for preferredContentWidth
				preferredContentWidth.setPenalty(width);
				preferredContentHeight.setPenalty(height);
			}
		} else
			childArea.setShrinkPenalties(width, height);
		ls.invalidateLayout();
	}

	/**
	 * The reluctance with which the area's content grows over its preferred
	 * size. The bigger the less likely is such expansion.
	 */
	public Dimension getGrowPenalties() {
		return (childArea == null) ? growPenalties : childArea.growPenalties;
	}
	/**
	* Set the reluctance with which the area's content grows over its preferred
	* size. The bigger the less likely is such expansion.
	* @param value Dimension that defines the grow penalties.
	*/
	public void setGrowPenalties(Dimension value) {
		if (childArea == null) {
			growPenalties = value;
			if (preferredContentWidth != null) { // penalties are only relevant
				// if a
				// preferred size is set
				preferredContentWidth.setPenalty(value.getWidth());
				preferredContentHeight.setPenalty(value.getHeight());
			}
		} else
			childArea.setGrowPenalties(value);
		ls.invalidateLayout();
	}

	/**
	 * Aspect ratio of the area's content. May be different from the aspect
	 * ratio of the area.
	 */
	public double getContentAspectRatio() {
		return (childArea == null) ? contentAspectRatio
				: childArea.contentAspectRatio;
	}
	/**
	* Set the aspect ratio of the area's content. May be different from the aspect
	* ratio of the area.
	* @param value double that defines the desired aspect ratio.
	*/
	public void setContentAspectRatio(double value) {
		if (childArea == null) {
			contentAspectRatio = value;
			if (contentAspectRatioC == null) { // no aspect ratio constraint set
				// yet
				contentAspectRatioC = ls.addConstraint(-1, left, 1, right,
						value, top, -value, bottom, OperatorType.EQ, 0);
				contentAspectRatioC.Owner = this;
				constraints.add(contentAspectRatioC);
			} else {
				contentAspectRatioC.setLeftSide(-1, left, 1, right, value, top,
						-value, bottom);
			}
		} else
			childArea.setContentAspectRatio(value);
		ls.invalidateLayout();
	}

	/**
	 * Horizontal alignment of the content in its area.
	 */
	public HorizontalAlignment getHorizontalAlignment() {
		return horizontalAlignment;
	}
	/**
	 * Set the horizontal alignment of the content in its area.
	 * @param value HorizontalAlignment to set
	 */
	public void setHorizontalAlignment(HorizontalAlignment value) {
		horizontalAlignment = value;
		updateHorizontal();
		ls.invalidateLayout();
	}
	/**
	 * Set the horizontal alignment of the content in its area.
	 * @param value string that defines the HorizontalAlignment to set
	 */
	public void setHorizontalAlignment(String value) {
		if (value.equals("LEFT"))
			horizontalAlignment = HorizontalAlignment.LEFT;
		if (value.equals("RIGHT"))
			horizontalAlignment = HorizontalAlignment.RIGHT;
		if (value.equals("CENTER"))
			horizontalAlignment = HorizontalAlignment.CENTER;
		if (value.equals("FILL"))
			horizontalAlignment = HorizontalAlignment.FILL;
		if (value.equals("NONE"))
			horizontalAlignment = HorizontalAlignment.NONE;
		updateHorizontal();
		ls.invalidateLayout();
	}

	/**
	 * Vertical alignment of the content in its area.
	 */
	public VerticalAlignment getVerticalAlignment() {
		return vAlignment;
	}
	/**
	 * Set the vertical alignment of the content in its area.
	 * @param value VerticalAlignment to set
	 */
	public void setVerticalAlignment(VerticalAlignment value) {
		vAlignment = value;
		updateVertical();
		ls.invalidateLayout();
	}
	/**
	 * Set the vertical alignment of the content in its area.
	 * @param value string that defines the VerticalAlignment to set
	 */
	public void setVerticalAlignment(String value) {
		if (value.equals("TOP"))
			vAlignment = VerticalAlignment.TOP;
		if (value.equals("BOTTOM"))
			vAlignment = VerticalAlignment.BOTTOM;
		if (value.equals("CENTER"))
			vAlignment = VerticalAlignment.CENTER;
		if (value.equals("FILL"))
			vAlignment = VerticalAlignment.FILL;
		if (value.equals("NONE"))
			vAlignment = VerticalAlignment.NONE;
		updateVertical();
		ls.invalidateLayout();
	}

	/**
	 * Left inset between area and its content.
	 */
	public int getLeftInset() {
		return leftInset;
	}
	/**
	 * Set the left inset between area and its content.
	 * @param value int that defines the left inset
	 */
	public void setLeftInset(int value) {
		leftInset = value;
		updateHorizontal();
		ls.invalidateLayout();
	}

	/**
	 * Top inset between area and its content.
	 */
	public int getTopInset() {
		return topInset;
	}
	/**
	 * Set the top inset between area and its content.
	 * @param value int that defines the top inset
	 */
	public void setTopInset(int value) {
		topInset = value;
		updateVertical();
		ls.invalidateLayout();
	}

	/**
	 * Right inset between area and its content.
	 */
	public int getRightInset() {
		return rightInset;
	}
	/**
	 * Set the right inset between area and its content.
	 * @param value int that defines the right inset
	 */
	public void setRightInset(int value) {
		rightInset = value;
		updateHorizontal();
		ls.invalidateLayout();
	}

	/**
	 * Bottom inset between area and its content.
	 */
	public int getBottomInset() {
		return bottomInset;
	}
	/**
	 * Set the bottom inset between area and its content.
	 * @param value int that defines the bottom inset
	 */
	public void setBottomInset(int value) {
		bottomInset = value;
		updateVertical();
		ls.invalidateLayout();
	}

	/**
	 * Adds a childArea to this area, together with constraints that specify the
	 * relative location of the childArea within this area. It is called when
	 * such a childArea becomes necessary, i.e. when the user requests insets or
	 * special alignment.
	 */
	void initChildArea() {
		// add a child area with new tabs,
		// and add constraints that set its tabs to be equal to the
		// coresponding tabs of this area (for a start)
		childArea = new Area(ls, new XTab(ls, false), new YTab(ls, false),
				new XTab(ls, false), new YTab(ls, false), content,
				new Dimension(0, 0));
		leftConstraint = getLeft().isEqual(childArea.getLeft());
		leftConstraint.Owner = this;
		constraints.add(leftConstraint);
		topConstraint = getTop().isEqual(childArea.getTop());
		topConstraint.Owner = this;
		constraints.add(topConstraint);
		rightConstraint = childArea.getRight().isEqual(getRight());
		rightConstraint.Owner = this;
		constraints.add(rightConstraint);
		bottomConstraint = childArea.getBottom().isEqual(getBottom());
		bottomConstraint.Owner = this;
		constraints.add(bottomConstraint);

		// remove the minimum content size constraints from this area
		// and copy the minimum content size setting to the childArea
		constraints.remove(minContentWidth);
		minContentWidth.remove();
		minContentWidth = childArea.minContentWidth;
		constraints.remove(minContentHeight);
		minContentHeight.remove();
		minContentHeight = childArea.minContentHeight;
		childArea.setMinContentSize(minContentSize);

		// if there are maximum content size constraints on this area,
		// change them so that they refer to the tabs of the childArea
		// and copy the minimum content size settings to the childArea
		if (maxContentWidth != null) {
			childArea.maxContentSize = maxContentSize;
			childArea.maxContentWidth = maxContentWidth;
			maxContentWidth.setLeftSide(-1, childArea.getLeft(), 1, childArea
					.getRight());
			childArea.maxContentHeight = maxContentHeight;
			maxContentHeight.setLeftSide(-1, childArea.getTop(), 1, childArea
					.getBottom());
		}

		// if there are preferred content size constraints on this area,
		// change them so that they refer to the tabs of the childArea
		// and copy the preferred content size settings to the childArea
		if (preferredContentHeight != null) {
			childArea.preferredContentSize = preferredContentSize;
			childArea.widthPenalty = widthPenalty;
            childArea.heightPenalty = heightPenalty;
			childArea.growPenalties = growPenalties;
			childArea.preferredContentWidth = preferredContentWidth;
			preferredContentWidth.setLeftSide(-1, childArea.getLeft(), 1,
					childArea.getRight());

			childArea.preferredContentHeight = preferredContentHeight;
			preferredContentHeight.setLeftSide(-1, childArea.getTop(), 1,
					childArea.getBottom());

		}
	}

	/**
	 * Update the constraints for horizontal insets and alignment.
	 */
	void updateHorizontal() {
		// if the area does not have a childAdrea yet, this is the time to add
		// it
		if (childArea == null)
			initChildArea();

		// change the constraints leftConstraint and rightConstraint so that the
		// horizontal alignment
		// and insets of the childArea within this area are as specified by the
		// user
		if (horizontalAlignment == HorizontalAlignment.LEFT) {
			leftConstraint.setLeftSide(-1, getLeft(), 1, childArea.getLeft());
			leftConstraint.setOp(OperatorType.EQ);
			leftConstraint.setRightSide(leftInset);
			rightConstraint
					.setLeftSide(-1, childArea.getRight(), 1, getRight());
			rightConstraint.setOp(OperatorType.GE);
			rightConstraint.setRightSide(rightInset);
		} else if (horizontalAlignment == HorizontalAlignment.RIGHT) {
			leftConstraint.setLeftSide(-1, getLeft(), 1, childArea.getLeft());
			leftConstraint.setOp(OperatorType.GE);
			leftConstraint.setRightSide(leftInset);
			rightConstraint
					.setLeftSide(-1, childArea.getRight(), 1, getRight());
			rightConstraint.setOp(OperatorType.EQ);
			rightConstraint.setRightSide(rightInset);
		} else if (horizontalAlignment == HorizontalAlignment.CENTER) {
			leftConstraint.setLeftSide(-1, getLeft(), 1, childArea.getLeft());
			leftConstraint.setOp(OperatorType.GE);
			leftConstraint.setRightSide(Math.max(leftInset, rightInset));
			rightConstraint.setLeftSide(-1, getLeft(), 1, childArea.getLeft(),
					1, childArea.getRight(), -1, getRight());
			rightConstraint.setOp(OperatorType.EQ);
			rightConstraint.setRightSide(0);
		} else if (horizontalAlignment == HorizontalAlignment.FILL) {
			leftConstraint.setLeftSide(-1, getLeft(), 1, childArea.getLeft());
			leftConstraint.setOp(OperatorType.EQ);
			leftConstraint.setRightSide(leftInset);
			rightConstraint
					.setLeftSide(-1, childArea.getRight(), 1, getRight());
			rightConstraint.setOp(OperatorType.EQ);
			rightConstraint.setRightSide(rightInset);
		} else if (horizontalAlignment == HorizontalAlignment.NONE) {
			leftConstraint.setLeftSide(-1, getLeft(), 1, childArea.getLeft());
			leftConstraint.setOp(OperatorType.GE);
			leftConstraint.setRightSide(leftInset);
			rightConstraint
					.setLeftSide(-1, childArea.getRight(), 1, getRight());
			rightConstraint.setOp(OperatorType.GE);
			rightConstraint.setRightSide(rightInset);
		}
	}

	/**
	 * Update the constraints for vertical insets and alignment.
	 */
	void updateVertical() {
		// if the area does not have a childAdrea yet, this is the time to add
		// it
		if (childArea == null)
			initChildArea();

		// change the constraints topConstraint and bottomConstraint so that the
		// vertical alignment
		// and margins of the childArea within this area are as specified by the
		// user
		if (vAlignment == VerticalAlignment.TOP) {
			topConstraint.setLeftSide(-1, getTop(), 1, childArea.getTop());
			topConstraint.setOp(OperatorType.EQ);
			topConstraint.setRightSide(topInset);
			bottomConstraint.setLeftSide(-1, childArea.getBottom(), 1,
					getBottom());
			bottomConstraint.setOp(OperatorType.GE);
			bottomConstraint.setRightSide(bottomInset);
		} else if (vAlignment == VerticalAlignment.BOTTOM) {
			topConstraint.setLeftSide(-1, getTop(), 1, childArea.getTop());
			topConstraint.setOp(OperatorType.GE);
			topConstraint.setRightSide(topInset);
			bottomConstraint.setLeftSide(-1, childArea.getBottom(), 1,
					getBottom());
			bottomConstraint.setOp(OperatorType.EQ);
			bottomConstraint.setRightSide(bottomInset);
		} else if (vAlignment == VerticalAlignment.CENTER) {
			topConstraint.setLeftSide(-1, getTop(), 1, childArea.getTop());
			topConstraint.setOp(OperatorType.GE);
			topConstraint.setRightSide(Math.max(topInset, bottomInset));
			bottomConstraint.setLeftSide(-1, getTop(), 1, childArea.getTop(),
					1, childArea.getBottom(), -1, getBottom());
			bottomConstraint.setOp(OperatorType.EQ);
			bottomConstraint.setRightSide(0);
		} else if (vAlignment == VerticalAlignment.FILL) {
			topConstraint.setLeftSide(-1, getTop(), 1, childArea.getTop());
			topConstraint.setOp(OperatorType.EQ);
			topConstraint.setRightSide(topInset);
			bottomConstraint.setLeftSide(-1, childArea.getBottom(), 1,
					getBottom());
			bottomConstraint.setOp(OperatorType.EQ);
			bottomConstraint.setRightSide(bottomInset);
		} else if (vAlignment == VerticalAlignment.NONE) {
			topConstraint.setLeftSide(-1, getTop(), 1, childArea.getTop());
			topConstraint.setOp(OperatorType.GE);
			topConstraint.setRightSide(topInset);
			bottomConstraint.setLeftSide(-1, childArea.getBottom(), 1,
					getBottom());
			bottomConstraint.setOp(OperatorType.GE);
			bottomConstraint.setRightSide(bottomInset);
		}
	}

	/**
	 * Sets the preferred size according to the content's PreferredSize method,
	 * and the penalties according to heuristics.
	 */
	public void setDefaultBehavior() {
		if (getContent() == null) {
			setGrowPenalties(new Dimension(0, 0));
			return;
		}

		if (getPreferredContentSize() != getContent().getPreferredSize()) {
			setPreferredContentSize(getContent().getPreferredSize());
			ls.invalidateLayout();
		}

		// use heuristics for penalties: controls with naturally constant
		// content size are less likely to change their size

        setShrinkPenalties(Constraint.PREFERRED_SIZE_PENALTY, Constraint.PREFERRED_SIZE_PENALTY);
//		if (getContent() instanceof JButton
//				|| getContent() instanceof JRadioButton
//				|| getContent() instanceof JCheckBox
//				|| getContent() instanceof JLabel
//				|| getContent() instanceof JProgressBar) {
//			setShrinkPenalties(new Dimension(4, 4));
//			setGrowPenalties(new Dimension(3, 3));
//		} else {
//			setShrinkPenalties(new Dimension(2, 2));
//			setGrowPenalties(new Dimension(1, 1));
//		}
	}
	/**
	* Place the area content accordantly to the solved layout specifications.
	* You should never need to call it by your self.
	*/
	void doLayout() {
		if (getContent() == null)
			return; // empty areas need no layout

		// if there is a childArea, then it is the childArea that actually
		// contains the content
		Area a = (childArea != null) ? childArea : this;

		// set content location and size
		a.getContent().setLocation(
				(new Point((int) Math.round(a.getLeft().getValue()), (int) Math
						.round(a.getTop().getValue()))));
		int width = (int) Math.round(a.getRight().getValue()
				- a.getLeft().getValue());
		int height = (int) Math.round(a.getBottom().getValue()
				- a.getTop().getValue());
		a.getContent().setSize(width, height);
	}
	/**
	* Returns a string that displays the area's layout specification
	* @return string identifies the area's layout specification
	*/
	public String toString() {
		return "Area(" + left.toString() + "," + top.toString() + ","
				+ right.toString() + "," + bottom.toString() + ")";
	}

	/**
	 * Sets the width of the area to be the same as the width of the given area.
	 *
	 * @param a
	 *            the area that should have the same width
	 * @return the same-width constraint
	 */
	public Constraint hasSameWidthAs(Area a) {
		return ls.addConstraint(-1, left, 1, right, 1, a.left, -1, a.right,
				OperatorType.EQ, 0);
	}

	/**
	 * Sets the height of the area to be the same as the height of the given
	 * area.
	 *
	 * @param a
	 *            the area that should have the same height
	 * @return the same-height constraint
	 */
	public Constraint hasSameHeightAs(Area a) {
		return ls.addConstraint(-1, top, 1, bottom, 1, a.top, -1, a.bottom,
				OperatorType.EQ, 0);
	}

	/**
	 * Sets the size of the area to be the same as the size of the given area.
	 *
	 * @param a
	 *            the area that should have the same size
	 * @return a list containing a same-width and same-height constraint
	 */
	public List<Constraint> hasSameSizetAs(Area a) {
		List<Constraint> l = new ArrayList<Constraint>();
		l.add(this.hasSameWidthAs(a));
		l.add(this.hasSameHeightAs(a));
		return l;
	}
	/**
	* Construct the area based on the given tabs
	* @param ls the layout specification
	* @param left the left vertical grid line
	* @param top the top horziontal grid line
	* @param right the right vertical grid line
	* @param bottom the bottom horziontal grid line
	* @param content a JComponent that will be used in this area
	* @param minContentSize the minimal size of this area
	*/
	Area(LayoutSpec ls, XTab left, YTab top, XTab right, YTab bottom,
			JComponent content, Dimension minContentSize) {
		//TODO why is minContentSize passed in? component already has getMinSize()
		this.ls = ls;
		this.left = left;
		this.right = right;
		this.top = top;
		this.bottom = bottom;
		setContent(content);
		this.minContentSize = minContentSize;

		// adds the two essential constraints of the area that make sure that
		// the left x-tab is really
		// to the left of the right x-tab, and the top y-tab really above the
		// bottom y-tab
		// TODO: the preferred size constraints need to have a smaller penalty
		// (not INFINITY as per default)
		minContentWidth = ls.addConstraint(-1, left, 1, right, OperatorType.GE,
				minContentSize.getWidth());
		minContentWidth.Owner = this;
		minContentWidth.setName("minContentWidth");
		constraints.add(minContentWidth);
		minContentHeight = ls.addConstraint(-1, top, 1, bottom,
				OperatorType.GE, minContentSize.getHeight());
		minContentHeight.Owner = this;
		minContentHeight.setName("minContentHeight");
		constraints.add(minContentHeight);
	}
	/**
	* Construct the area based on the given tabs
	* @param ls the layout specification
	* @param row the row this Area will reside
	* @param column the column this Area will reside
	* @param content a JComponent that will be used in this area
	* @param minContentSize the minimal size of this area
	*/
	Area(LayoutSpec ls, Row row, Column column, JComponent content,
			Dimension minContentSize) {
		this(ls, column.getLeft(), row.getTop(), column.getRight(), row
				.getBottom(), content, minContentSize);
		this.row = row;
		this.column = column;
	}

	/**
	 * Removes the area from its specification.
	 */
	public void remove() {
		if (childArea != null)
			childArea.remove();
		for (Constraint c : constraints)
			c.remove();
		ls.getAreas().remove(this);
	}
	/**
	* Export the specification of this Area into XML
	*
	*/ 
	public void writeXML(OutputStreamWriter out) {
		try {
			out.write("\n");
			out.write("\t<area>\n");
			out.write("\t\t<name>" + this.content.getName() + "</name>\n");
			out.write("\t\t<left>" + this.left + "</left>\n");
			out.write("\t\t<top>" + this.top + "</top>\n");
			out.write("\t\t<right>" + this.right + "</right>\n");
			out.write("\t\t<bottom>" + this.bottom + "</bottom>\n");
			if (this.leftInset != 0)
				out
						.write("\t\t<leftinset>" + this.leftInset
								+ "</leftinset>\n");
			if (this.topInset != 0)
				out.write("\t\t<topinset>" + this.topInset + "</topinset>\n");
			if (this.rightInset != 0)
				out.write("\t\t<rightinset>" + this.rightInset
						+ "</rightinset>\n");
			if (this.bottomInset != 0)
				out.write("\t\t<bottominset>" + this.bottomInset
						+ "</bottominset>\n");
			if (this.horizontalAlignment != HorizontalAlignment.FILL)
				out
						.write("\t\t<horizontalalignment>"
								+ this.horizontalAlignment
								+ "</horizontalalignment>\n");
			if (this.vAlignment != VerticalAlignment.FILL)
				out.write("\t\t<verticalalignment>" + this.vAlignment
						+ "</verticalalignment>\n");
			out.write("\t</area>\n");
		} catch (IOException e) {
			System.out.println(e.getMessage());
		}
	}
	/**
	* Set if the preferred content size can be set automatically
	* @param autoPreferredContentSize true allows the preferred content size can be set automatically, false otherwise
	*/

	public void setAutoPreferredContentSize(boolean autoPreferredContentSize) {
		this.autoPreferredContentSize = autoPreferredContentSize;
	}
	/**
	* Get if the preferred content size can be set automatically
	* @return true if the preferred content size can be set automatically, false otherwise
	*/

	public boolean isAutoPreferredContentSize() {
		return autoPreferredContentSize;
	}

	public void setInsets(Insets insets) {
		// TODO Auto-generated method stub
		
	}

	public Insets getInsets() {
		// TODO Auto-generated method stub
		return null;
	}

	public boolean hasChildArea() {
		// TODO Auto-generated method stub
		return false;
	}

	public ALMLayout getChildArea() {
		// TODO Auto-generated method stub
		return null;
	}

}
