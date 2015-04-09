package alm.compatibility;

import alm.ALMLayout;
import alm.Area;
import alm.Column;
import alm.LayoutSpec;
import alm.Row;
import alm.XTab;
import alm.YTab;

import java.awt.Container;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.LayoutManager2;
import java.awt.Component;
import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JComponent;

import linsolve.*;

/**
 * An implementation of Java GridBagLayout on top of ALM. All
 * GridBagLayout constraints are supported. It is not threadsafe.
 * 
 * @author Yuk-Kwan Chan, Nathan Wood, Habib Naderi
 * 
 */
public class GridBagLayout implements Serializable, LayoutManager2 {
	/**
	 * The Analyzer that is used to translate the GridBag Layout
	 */
	private SpecGridBagAnalyzer analyzer;

	/**
	 * The ALM Layout
	 */
	private ALMLayout almLayout;

	/**
	 * A Map of all components and their GridBagConstraints.
	 */
	ComponentsConstraint componentsConstraints;
	WeightStore ws; // information about weight constraints.
	AreaInfoStore extendedAreaInfo; // more information about ALM areas.
	
	// The variable storing state for the GridBagConstraints.RELATIVE value in  gridy.
	private int lastRow = -1;
	// The variable storing state for the 	GridBagConstraints.RELATIVE value in gridx.
	private int lastColumn = -1;

	 
	/*
	 * Prevents modifications to the layout manager during layout. This is 
	 * needed because on layout, ALM calls Container.add, which calls 
	 * Container.remove, which calls LayoutManager.remove. Then, Container.add
	 * calls LayoutManager.add to re-add things. But doing so invalidates ALM,
	 * which causes it to be reset.
	 */
	private boolean doingLayout = false;

	/**
	 *  constructor
	 */
	public GridBagLayout() {
		componentsConstraints = new ComponentsConstraint();
		initALM();
	}

	/**
	 * Adds a component to the layout. Constraints can be provided in
	 * this call or by calling setConstraints.
	 */
	public void addLayoutComponent(Component comp, Object constraints) {
		if (comp == null || doingLayout) {
			return;
		}

		// If constraints is not provided, check to see if it's set before via
		// setConstraints. 
		if (constraints == null) {
			// ignore it, if it's already in the layout
			if (componentsConstraints.originalContainsKey(comp))
				return;
			if (componentsConstraints.notAddedContainsKey(comp)) {
				constraints = componentsConstraints.removeNotAdded(comp);
			}
		}
		
		// if constraints is not provided in this call and it is not provided before
		// via setConstraints then create a default constraints object. otherwise 
		// make a copy and add it to the layout.
		GridBagConstraints c = constraints != null ? (GridBagConstraints)((GridBagConstraints) constraints).clone() : new GridBagConstraints(); 
		JComponent jComp = (JComponent) comp;

		componentsConstraints.addOriginal(jComp, c);

		// Invalidate ALM because new components have been added
		invalidateLayout();
	}

	/**
	 * Returns 0.5 as alignment in the horizontal direction.
	 */
	public float getLayoutAlignmentX(Container arg0) {
		// 0.5 chosen as it is what GridBagLayout uses
		return 0.5f;
	}

	/**
	 * Returns 0.5 as alignment in the vertical direction.
	 */
	public float getLayoutAlignmentY(Container arg0) {
		// 0.5 chosen as it is what GridBagLayout uses
		return 0.5f;
	}

	/**
	 * Invalidates the layout.
	 */
	public void invalidateLayout(Container arg0) {
	}

	/**
	 * Invalidates the layout.
	 */
	private void invalidateLayout() {
		almLayout = null;
		analyzer = null;
	}

	/**
	 * Returns maximum size for the layout.
	 */
	public Dimension maximumLayoutSize(Container arg0) {
		// GridBagLayout have no maximum size.
		return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
	}
	
	/**
	 * Adds a component to the layout. constraints can be set by using setConstraints.
	 */
	public void addLayoutComponent(String arg0, Component arg1) {
		// As ALM does not use per-component strings, this method has no effect.
		almLayout.addLayoutComponent(arg0, arg1);
	}

	/**
	 * Processes components and their constraints and creates the layout. 
	 */
	public void layoutContainer(Container arg0) {
		if (!isALMReady()) {
			initALM();
			computeConstraints(arg0);
		}
		doingLayout = true;
		almLayout.layoutContainer(arg0);
		doingLayout = false;
	}

	/**
	 * Returns minimum size for the layout.
	 */
	public Dimension minimumLayoutSize(Container arg0) {
		if (!isALMReady()) {
			initALM();
			computeConstraints(arg0);
		}
		return almLayout.minimumLayoutSize(arg0);
	}

	/**
	 * Calculates preferred size for the layout based on components preferred sizes.
	 */
	public Dimension preferredLayoutSize(Container arg0) {
		if (!isALMReady()) {
			initALM();
			computeConstraints(arg0);
		}
		return almLayout.preferredLayoutSize(arg0);
	}

	/**
	 * Removes a component from the layout.
	 */
	public void removeLayoutComponent(Component arg0) {
		if (doingLayout)
			return;
		componentsConstraints.remove((JComponent)arg0);
		if (isALMReady()) {
			// ALM does not remove any components, this does nothing.
			almLayout.removeLayoutComponent(arg0);
			invalidateLayout();
		}
	}

	/**
	 * Returns constraints for a component. 
	 */
	public GridBagConstraints getConstraints(Component comp) {

		return componentsConstraints.getOriginal(comp);
	}

	/**
	 * Associates constraints to a component. 
	 */
	public void setConstraints(Component comp, GridBagConstraints constraints) {
		// If a component has been added before, update its constraints otherwise
		// keep the constraints for the time that it is added to the layout.
		if (componentsConstraints.originalContainsKey(comp)) {
			componentsConstraints.addOriginal(comp, (GridBagConstraints)constraints.clone());
		} else  {
			componentsConstraints.addNotAdded(comp, (GridBagConstraints)constraints.clone());
		}
	}

	/**
	 * Not implemented.
	 */
	public int[][] getLayoutDimensions() {
		// This method is not necessary, not implemented
		return null;
	}

	/**
	 * Not implemented.
	 */
	public Point getLayoutOrigin() {
		// This method is not necessary, not implemented
		return null;
	}

	/**
	 * Not implemented.
	 */
	public double[][] getLayoutWeights() {
		// This method is not necessary, not implemented
		return null;
	}

	/**
	 * Not implemented.
	 */
	public Point location(int x, int y) {
		// This method is not necessary, not implemented
		return null;
	}
	
	/**
	 * Returns underlying ALM object.
	 */
	public ALMLayout getAlmLayout(){
		return almLayout;
	}

	/**
	 * Creates GridBagLayout and its associated ALM layout. For each component, required
	 * rows and columns are created. Then an ALM area is created and associated to the 
	 * component.  
	 */
	private void computeConstraints(Container parent) {
		GridBagConstraints cc;
		boolean weightxExist = false;
		boolean weightyExist = false;
		Set<JComponent> components = componentsConstraints.getOriginalKeySet();
		//If there are no components, there is nothing to compute.
		if(components.size() == 0)
			return;
		for (JComponent jComp : components) {
			almLayout.getLayoutSpec().setDesc(jComp.getName());
			GridBagConstraints c = componentsConstraints.getOriginal(jComp);
			if (c == null)
				continue;
			cc = (GridBagConstraints)c.clone();
			preProcessConstraint(cc, c);
			
			// create GridBag row(s) and column(s), if required.
			int nRow = analyzer.ensureRows(cc.gridy, cc.gridheight);
			int nCol = analyzer.ensureColumns(cc.gridx, cc.gridwidth);
			
			// if new row(s) or column(s) is added, update location and size of 
			// components with RELATIVE or REMINDER which are already in the layout.
			if (nRow != 0 || nCol != 0) {
				updateConstraints(nRow, nCol);
				updateComponentLocation(cc, c);
			}
			
			// create an ALM row for this component, if required.
			Row aRow = analyzer.getRow(cc.gridy, cc.gridheight, c);
			if (aRow == null) {
				aRow = getRow(cc, c);
			}
			
			// create an ALM column for this component, if required.
			Column aColumn = analyzer.getColumn(cc.gridx, cc.gridwidth, c);
			if (aColumn == null) {
				aColumn = getColumn(cc, c);
			}

			// create an ALM area for this component.
			Area a = computeComponentArea(jComp, cc, aRow, aColumn);
			
			// set strict constraints for its preferred size. The constraints are
			// updated later based on weight or location of the component.
			
			// prepare everything for the next component.
			postProcessConstraint(cc, c);
			
			// add the component to the list of processed components.
			componentsConstraints.add(jComp, cc);
			weightxExist = c.weightx > 0 ? true : weightxExist;
			weightyExist = c.weighty > 0 ? true : weightyExist;
		}
		
		almLayout.getLayoutSpec().setDesc("Extra");
		addExtraConstraints(parent);
		
		if (weightxExist || weightyExist) {
			applyWeights();
		} else {
			// if there is an extra space then assign it to the last component in the row and column.
						
		}
	}
	
	/**
	 * Adds extra required constraints after adding all components to the layout.
	 * @param parent
	 */
	private void addExtraConstraints(Container parent) {		
		// Scan through rows and remove zero-height rows.		
		for (int rowIndex = 0; rowIndex < analyzer.getNumGridBagRows(); rowIndex++) {
			Set<JComponent> compInRow = getComponentsInRow(rowIndex);
			if (compInRow.size() == 0) {
				// There are no components in this row
				// force it's size to be zero.
				analyzer.getGBRowBeginTab(rowIndex).isEqual(
						analyzer.getGBRowEndTab(rowIndex));
			}
		}
		
		// Scan through columns and remove zero-length columns.
		for (int colIndex = 0; colIndex < analyzer.getNumGridBagColumns(); colIndex++) {
			Set<JComponent> compInCol = getComponentsInCol(colIndex);
			if (compInCol.size() == 0) {
				analyzer.getGBColumnBeginTab(colIndex).isEqual(
						analyzer.getGBColumnEndTab(colIndex));
			}
		}

		
		//Add Border Constraints.
		almLayout.setInsetConstraints(parent.getInsets());
		
		// add constraints for end-column and end-row to set their values to the last actual column and row.
		almLayout.addConstraint(1, analyzer.getGBColumnEndTab(analyzer.getNumGridBagColumns()), 
				-1, analyzer.getGBColumnEndTab(analyzer.getNumGridBagColumns()-1), OperatorType.EQ, 0);
		almLayout.addConstraint(1, analyzer.getGBRowEndTab(analyzer.getNumGridBagRows()), 
				-1, analyzer.getGBRowEndTab(analyzer.getNumGridBagRows()-1), OperatorType.EQ, 0);

		// add constraints for before-last-row and before-last-col to set their values to the start of last 
		// component in the row and column.
		almLayout.addConstraint(1, analyzer.getGBColumnEndTab(analyzer.getNumGridBagColumns()-2), 
				-1, analyzer.getBeforeLastCol(), OperatorType.EQ, 0);
		almLayout.addConstraint(1, analyzer.getGBRowEndTab(analyzer.getNumGridBagRows()-2), 
				-1, analyzer.getBeforeLastRow(), OperatorType.EQ, 0);


		// centering
		// x-dir
		almLayout
				.addConstraint(1, analyzer.getGBColumnBeginTab(0), -1,
						almLayout.getLeftInset(), -1, almLayout.getRightInset(), 1,
						analyzer.getGBColumnEndTab(analyzer
								.getNumGridBagColumns() - 1), OperatorType.EQ,
						0);
		// y-dir
		almLayout.addConstraint(1, analyzer.getGBRowBeginTab(0), -1, almLayout
				.getTopInset(), -1, almLayout.getBottomInset(), 1, analyzer
				.getGBRowEndTab(analyzer.getNumGridBagRows() - 1),
				OperatorType.EQ, 0);		
	}
	
	/**
	 * Processes constraints before adding the component to the layout.
	 * @param c modifiable version of the constraints.
	 * @param original original version of the constraints.
	 */
	private void preProcessConstraint(GridBagConstraints c, GridBagConstraints original) {
	
		if (c.gridx == GridBagConstraints.RELATIVE) {
			c.gridx = ++lastColumn;
		}
	
		// if both gridx and gridy are set to RELATIVE, then gridx has priority.
		if (c.gridy == GridBagConstraints.RELATIVE) {				
			if (original.gridx == GridBagConstraints.RELATIVE) {
				c.gridy = (lastRow == -1) ? ++lastRow: lastRow;
			} else {
				c.gridy = ++lastRow;
			}					
		}
					
		// set gridwidth/gridheight to 1, if it's set to REMAINDER or RELATIVE. The 
		// real size is not known until all components are added to the layout.
		
		if (c.gridwidth == GridBagConstraints.REMAINDER ||
				c.gridwidth == GridBagConstraints.RELATIVE) {
			c.gridwidth = 1; 
		}
		
		if (c.gridheight == GridBagConstraints.REMAINDER ||
				c.gridheight == GridBagConstraints.RELATIVE) {
			c.gridheight = 1; 
		}		
	}

	/**
	 * Processes constraints after adding the component to the layout. It prepares
	 * everything for the next component.
	 * @param c current component's constraints.
	 * @param original original version of the constraints.
	 */

	private void postProcessConstraint(GridBagConstraints c, GridBagConstraints original) {
		lastRow = c.gridy;
		lastColumn = c.gridx;			
		if (original.gridwidth == GridBagConstraints.REMAINDER) {
			// find the first available row which has room for the next component.
			for (lastRow++; getLastUsedColInRow(lastRow) == analyzer.getNumGridBagColumns()-1; lastRow++)
				;
			// find the first available column in that row. 
			lastColumn = getLastUsedColInRow(lastRow);
		} else if (original.gridwidth == GridBagConstraints.RELATIVE){
			;
		} else {
			// set lastColumn to point to the column right after the recently added component.
			lastColumn += (c.gridwidth-1);
		}
		
		if (original.gridheight == GridBagConstraints.REMAINDER) {
			// find the first available column which has room for the next component. 
			for (lastColumn++; getLastUsedRowInCol(lastColumn) == analyzer.getNumGridBagRows()-1; lastColumn++)
				;
			// find the first available row in that column.
			int r = getLastUsedRowInCol(lastColumn)+1;
			lastRow = r < lastRow ? lastRow : r;
			lastColumn --; // if RELATIVE is used, it will increment it.
		}		
	}
	
	/**
	 * Creates an ALM area for the component.
	 * @param comp GUI component.
	 * @param con component's constraints.
	 * @param row ALM row, created for this component. 
	 * @param col ALM column, created for this component.
	 */

	private Area computeComponentArea(JComponent comp, GridBagConstraints con,
			Row row, Column col) {
		// compute insets tabs
		Row insetRow = computeInsetRow(comp, con, row);
		Column insetColumn = computeInsetColumn(comp, con, col);

		// compute padding tabs
		Row paddingRow = computePaddingRow(comp, con, insetRow);
		Column paddingColumn = computePaddingColumn(comp, con, insetColumn);
		Area componentArea = null;
		// create ALM area
		componentArea = almLayout.addArea(paddingRow, paddingColumn, comp);
		applyPadding(comp, con, componentArea);
		
		// store outer row and column in AreaInfoStore.
		extendedAreaInfo.AddAreaInfo(componentArea, new AreaInfo(row, col), comp);
		return componentArea;
	}
	
	/**
	 * Applies padding constraints.
	 * @param comp GUI component.
	 * @param con constraints for the component.
	 * @param a area which contains the component.
	 */
	private void applyPadding(JComponent comp, GridBagConstraints con, Area a) {
		// The value from Java's GridBagLayout tutorial is WRONG.
		// Use the value from the API doc instead.
		if (con.ipady > 0) {
			double minCompSize = comp.getMinimumSize().getHeight() + con.ipady;
			almLayout.addConstraint(1, a.bottom, -1, a.top,
						OperatorType.GE, minCompSize, a.getShrinkPenaltyHeight());

		}	
		if (con.ipadx > 0) {
			double minCompSize = comp.getMinimumSize().getWidth() + con.ipadx;
			almLayout.addConstraint(1, a.right, -1, a.left,
						OperatorType.GE, minCompSize, a.getShrinkPenaltyWidth());
		}
	}

	/**
	 * Applies inset constraints (for row).
	 * @param comp GUI component.
	 * @param con component's constraints.
	 * @param gridRow ALM row created for the component.
	 * @return a new row with the inset constraints applied.
	 */
	private Row computeInsetRow(JComponent comp, GridBagConstraints con,
			Row gridRow) {
		Insets insets = con.insets;

		YTab insetTop = almLayout.addYTab();
		YTab insetBottom = almLayout.addYTab();
		Row insetRow = null;
		// top constraint
		almLayout.addConstraint(1, insetTop, -1, gridRow.getTop(),
				OperatorType.EQ, insets.top);
		// bottom constraint
		almLayout.addConstraint(1, gridRow.getBottom(), -1, insetBottom,
				OperatorType.EQ, insets.bottom);

		insetRow = almLayout.addRow(insetTop, insetBottom);

		return insetRow;
	}

	/**
	 * Applies inset constraints (for column).
	 * @param comp GUI component.
	 * @param con component's constraints.
	 * @param gridCol ALM column created for the component.
	 * @return a new column with the inset constraints applied.
	 */
	private Column computeInsetColumn(JComponent comp, GridBagConstraints con,
			Column gridCol) {
		Insets insets = con.insets;

		XTab insetLeft = almLayout.addXTab();
		XTab insetRight = almLayout.addXTab();
		Column insetCol = null;
		// left constraint
		almLayout.addConstraint(1, insetLeft, -1, gridCol.getLeft(),
				OperatorType.EQ, insets.left);
		// right constraint
		almLayout.addConstraint(1, gridCol.getRight(), -1, insetRight,
				OperatorType.EQ, insets.right);

		insetCol = almLayout.addColumn(insetLeft, insetRight);

		return insetCol;
	}

	/**
	 * Applies padding constraints (for row).
	 * @param comp GUI component.
	 * @param con component's constraints.
	 * @param insetRow ALM row created for the component with inset constrains applied.
	 * @return a new row with the padding constraints applied.
	 */
	private Row computePaddingRow(JComponent comp, GridBagConstraints con,
			Row insetRow) {

		YTab paddingTop = almLayout.addYTab();
		YTab paddingBottom = almLayout.addYTab();
		Row paddingRow = null;
		
		paddingRow = almLayout.addRow(paddingTop, paddingBottom);

		// anchor
		anchorPaddingRow(comp, con, paddingRow, insetRow);

		return paddingRow;
	}
	
	/**
	 * Applies padding constraints (for column).
	 * @param comp GUI component.
	 * @param con component's constraints.
	 * @param insetColumn ALM column created for the component with inset constrains applied.
	 * @return a new column with the padding constraints applied.
	 */
	private Column computePaddingColumn(JComponent comp,
			GridBagConstraints con, Column insetColumn) {

		XTab paddingLeft = almLayout.addXTab();
		XTab paddingRight = almLayout.addXTab();
		Column paddingColumn = null;
		
		paddingColumn = almLayout.addColumn(paddingLeft, paddingRight);

		// anchor
		anchorPaddingColumn(comp, con, paddingColumn, insetColumn);

		return paddingColumn;
	}

	/**
	 * Generates constraints for anchor constraints which affect component's vertical attributes.
	 * 
	 * @param comp GUI component.
	 * @param con Component's constraints.
	 * @param paddingRow The row after processing padding constraint.
	 * @param insetRow The row after processing insets constraint.
	 */

	private void anchorPaddingRow(JComponent comp, GridBagConstraints con,
			Row paddingRow, Row insetRow) {
		if (con.fill == GridBagConstraints.BOTH
				|| con.fill == GridBagConstraints.VERTICAL) {
			// fill
			almLayout.addConstraint(1, paddingRow.getTop(), -1, insetRow
					.getTop(), OperatorType.EQ, 0);
			almLayout.addConstraint(1, paddingRow.getBottom(), -1, insetRow
					.getBottom(), OperatorType.EQ, 0);
		} else {
			// non-fill
			switch (con.anchor) {
			case GridBagConstraints.CENTER: // falls through
			case GridBagConstraints.LINE_START:
			case GridBagConstraints.WEST:
			case GridBagConstraints.LINE_END:
			case GridBagConstraints.EAST:
				almLayout.addConstraint(1, paddingRow.getTop(), -1, insetRow
						.getTop(), -1, insetRow.getBottom(), 1, paddingRow
						.getBottom(), OperatorType.EQ, 0);
				break;
			case GridBagConstraints.FIRST_LINE_START: // falls through
			case GridBagConstraints.NORTHWEST:
			case GridBagConstraints.PAGE_START:
			case GridBagConstraints.NORTH:
			case GridBagConstraints.FIRST_LINE_END:
			case GridBagConstraints.NORTHEAST:
				almLayout.addConstraint(1, paddingRow.getTop(), -1, insetRow
						.getTop(), OperatorType.EQ, 0);
				break;
			case GridBagConstraints.LAST_LINE_START: // falls through
			case GridBagConstraints.SOUTHWEST:
			case GridBagConstraints.PAGE_END:
			case GridBagConstraints.SOUTH:
			case GridBagConstraints.LAST_LINE_END:
			case GridBagConstraints.SOUTHEAST:
				almLayout.addConstraint(1, paddingRow.getBottom(), -1, insetRow
						.getBottom(), OperatorType.EQ, 0);
				break;
			default:
				// Never reach here.
				break;
			}
		}

		// These apply no matter what:
		// Ensure padding is inside inset
		almLayout.addConstraint(1, paddingRow.getTop(), -1, insetRow.getTop(),
				OperatorType.GE, 0);
		almLayout.addConstraint(1, insetRow.getBottom(), -1, paddingRow
				.getBottom(), OperatorType.GE, 0);
	}
	
	/**
	 * Generates constraints for anchor constraints which affect component's horizontal attributes.
	 * 
	 * @param comp GUI component.
	 * @param con Component's constraints.
	 * @param paddingColumn The column after processing padding constraint.
	 * @param insetColumn The column after processing insets constraint.
	 */

	private void anchorPaddingColumn(JComponent comp, GridBagConstraints con,
			Column paddingColumn, Column insetColumn) {
		if (con.fill == GridBagConstraints.BOTH
				|| con.fill == GridBagConstraints.HORIZONTAL) {
			// fill
			almLayout.addConstraint(1, paddingColumn.getLeft(), -1, insetColumn
					.getLeft(), OperatorType.EQ, 0);
			almLayout.addConstraint(1, paddingColumn.getRight(), -1,
					insetColumn.getRight(), OperatorType.EQ, 0);
		} else {
			// non-fill
			switch (con.anchor) {
			case GridBagConstraints.CENTER: // falls through
			case GridBagConstraints.PAGE_START:
			case GridBagConstraints.NORTH:
			case GridBagConstraints.PAGE_END:
			case GridBagConstraints.SOUTH:
				almLayout.addConstraint(1, paddingColumn.getLeft(), -1,
						insetColumn.getLeft(), -1, insetColumn.getRight(), 1,
						paddingColumn.getRight(), OperatorType.EQ, 0);
				break;
			case GridBagConstraints.FIRST_LINE_START: // falls through
			case GridBagConstraints.NORTHWEST:
			case GridBagConstraints.LINE_START:
			case GridBagConstraints.WEST:
			case GridBagConstraints.LAST_LINE_START:
			case GridBagConstraints.SOUTHWEST:
				almLayout.addConstraint(1, paddingColumn.getLeft(), -1,
						insetColumn.getLeft(), OperatorType.EQ, 0);
				break;
			case GridBagConstraints.FIRST_LINE_END: // falls through
			case GridBagConstraints.NORTHEAST:
			case GridBagConstraints.LINE_END:
			case GridBagConstraints.EAST:
			case GridBagConstraints.LAST_LINE_END:
			case GridBagConstraints.SOUTHEAST:
				almLayout.addConstraint(1, insetColumn.getRight(), -1,
						paddingColumn.getRight(), OperatorType.EQ, 0);
				break;
			default:
				// Never reaches here.
				break;
			}
		}

		// These apply no matter what:
		// Ensure padding is inside inset
		almLayout.addConstraint(1, paddingColumn.getLeft(), -1, insetColumn
				.getLeft(), OperatorType.GE, 0);
		almLayout.addConstraint(1, insetColumn.getRight(), -1, paddingColumn
				.getRight(), OperatorType.GE, 0);
	}

	/**
	 * returns a list of components in a row.
	 * 
	 * @param gridRowIndex row index
	 * @return list of components in the row.
	 */	
	private Set<JComponent> getComponentsInRow(int gridRowIndex) {
		Set<JComponent> compSet = componentsConstraints.getKeySet();
		Set<JComponent> compInRow = new HashSet<JComponent>();
		for (JComponent comp : compSet) {
			GridBagConstraints compCon = componentsConstraints.get(comp);
			if (compCon.gridy <= gridRowIndex
					&& compCon.gridy + compCon.gridheight > gridRowIndex) {
				compInRow.add(comp);
			}
		}
		return compInRow;
	}

	/**
	 * returns a list of components in a column.
	 * 
	 * @param gridColIndex column index
	 * @return list of components in the column
	 */
	private Set<JComponent> getComponentsInCol(int gridColIndex) {
		Set<JComponent> compSet = componentsConstraints.getKeySet();
		Set<JComponent> compInCol = new HashSet<JComponent>();
		for (JComponent comp : compSet) {
			GridBagConstraints compCon = componentsConstraints.get(comp);
			if (compCon.gridx <= gridColIndex
					&& compCon.gridx + compCon.gridwidth > gridColIndex) {
				compInCol.add(comp);
			}
		}
		return compInCol;
	}

	/**
	 * returns the index of last column in the specified row.
	 * @param gridRowIndex row index
	 * @return index of the last column. -1 if the row is empty.
	 */

	private int getLastUsedColInRow(int gridRowIndex) {
		Set<JComponent> compInRow = getComponentsInRow(gridRowIndex);
		int lastX=-1;
		for(JComponent comp:compInRow) {
			GridBagConstraints compCon = componentsConstraints.get(comp);
			if (compCon.gridx > lastX) {
				lastX = compCon.gridx + compCon.gridwidth - 1;
			}
		}		
		return lastX;		
	}
	
	/**
	 * returns the index of last row in the specified column.
	 * @param gridColIndex column index
	 * @return index of the last row. -1 if the column is empty.
	 */
	private int getLastUsedRowInCol(int gridColIndex) {
		Set<JComponent> compInRow = getComponentsInCol(gridColIndex);
		int lastY=-1;
		for(JComponent comp:compInRow) {
			GridBagConstraints compCon = componentsConstraints.get(comp);
			if (compCon.gridy > lastY) {
				lastY = compCon.gridy + compCon.gridheight - 1;
			}
		}		
		return lastY;		
	}

	/**
	 * Checks to see if there is any component in the specified row whose
	 * gridwidth has been set to RELATIVE.
	 * @param gridRowIndex row index.
	 * @return true: if there is such a component.
	 */

	private boolean relativeExistsInRow(int gridRowIndex) {
		Set<JComponent> compInRow = getComponentsInRow(gridRowIndex);
		for(JComponent comp:compInRow) {
			GridBagConstraints compCon = componentsConstraints.getOriginal(comp);
			if (compCon.gridwidth == GridBagConstraints.RELATIVE) {
				return true;
			}
		}		
		return false;				
	}
	
	/**
	 * Checks to see if there is any component in the specified column whose
	 * gridheight has been set to RELATIVE.
	 * @param gridColIndex column index
	 * @return true: if there is such a component.
	 */
	private boolean relativeExistsInCol(int gridColIndex) {
		Set<JComponent> compInRow = getComponentsInCol(gridColIndex);
		for(JComponent comp:compInRow) {
			GridBagConstraints compCon = componentsConstraints.getOriginal(comp);
			if (compCon.gridheight == GridBagConstraints.RELATIVE) {
				return true;
			}
		}		
		return false;				
	}

	/**
	 * Creates and returns a floating row for the area, if REMAINDER or RELAIVE is used for
	 * gridheight. 
	 * @param gridx starting column of the component.
	 * @param gridy stating row of the component.
	 * @param con constraints.
	 * @return The ALM row.
	 */

	private Row getRow(GridBagConstraints con, GridBagConstraints origCon) {
		Point loc = new Point(con.gridy, origCon.gridheight);
		Row aRow = null;
		LayoutSpec ls = almLayout.getLayoutSpec();

		if (origCon.gridheight == GridBagConstraints.REMAINDER) {
			if (relativeExistsInCol(con.gridx)) {
				// if there is a component in this column, with gridheight set to RELATIVE, then this
				// component should start after that. 
				YTab start = ls.addYTab();
				start.isEqual(analyzer.getBeforeLastRow());
				aRow = ls.addRow(start, analyzer.getGBRowEndTab(analyzer.getNumGridBagRows()));
			} else {
				// use endRow as the bottom of new row, if REMAINDER is set.
				aRow = ls.addRow(analyzer.getYTab(con.gridy + 1), analyzer.getGBRowEndTab(analyzer.getNumGridBagRows()));
				con.gridheight = analyzer.getNumGridBagRows() - con.gridy <= 0 ? 1 : analyzer.getNumGridBagRows() - con.gridy;
			}
		} else if (origCon.gridheight == GridBagConstraints.RELATIVE) {
			// this component should end before the last component.
			aRow = ls.addRow(analyzer.getYTab(con.gridy + 1), analyzer.getBeforeLastRow());
			con.gridheight = analyzer.getNumGridBagRows() - con.gridy - 1 <= 0 ? 1 : analyzer.getNumGridBagRows() - con.gridy - 1;
		} 
		analyzer.addToRowStore(loc, aRow);
		return aRow;
	}
	
	/**
	 * Creates and returns a floating column for the area, if REMAINDER or RELAIVE is used for
	 * gridwidth. 
	 * @param gridx starting column of the component.
	 * @param gridy stating row of the component.
	 * @param con constraints.
	 * @return The ALM column.
	 */
	private Column getColumn(GridBagConstraints con, GridBagConstraints origCon) {
		Point loc = new Point(con.gridx, origCon.gridwidth);
		Column aCol = null;
		LayoutSpec ls = almLayout.getLayoutSpec();

		if (origCon.gridwidth == GridBagConstraints.REMAINDER) {
			if (relativeExistsInRow(con.gridy)) {
				// if there is a component in this row, with gridwidth set to RELATIVE, then this
				// component should start after that. 
				XTab start = ls.addXTab();
				start.isEqual(analyzer.getBeforeLastCol());
				aCol = ls.addColumn(start, analyzer.getGBColumnEndTab(analyzer.getNumGridBagColumns()));
			} else {
				// use endColumn as the right side of new column, if REMAINDER is set.
				aCol = ls.addColumn(analyzer.getXTab(con.gridx + 1), analyzer.getGBColumnEndTab(analyzer.getNumGridBagColumns()));
				con.gridwidth = analyzer.getNumGridBagColumns() - con.gridx <= 0 ? 1 : analyzer.getNumGridBagColumns() - con.gridx;
			}
		} else if (origCon.gridwidth == GridBagConstraints.RELATIVE) {
			// this component should end before the last component.
			aCol = ls.addColumn(analyzer.getXTab(con.gridx + 1), analyzer.getBeforeLastCol());
			con.gridwidth = analyzer.getNumGridBagColumns() - con.gridx - 1 <= 0 ? 1 : analyzer.getNumGridBagColumns() - con.gridx - 1;
		} 
		analyzer.addToColStore(loc, aCol);
		return aCol;
	}
	

	/**
	 * Applies weightx to all areas in the layout.
	 * 
	 * @param pSize preferred size of the layout.
	 */

	private void applyWeightx(Dimension pSize) {
		LayoutSpec ls;
		
		ls = almLayout.getLayoutSpec();
		
		// a variable to show the extra space available in horizontal direction.
		Variable hSpace = new Variable(ls);
			
		// Add constraints to calculate extra space.
		almLayout.addConstraint(1, ls.getRight(), -1, ls.getLeft(), -1, hSpace, OperatorType.EQ, pSize.getWidth());

		// go through the areas in the layout and apply weights.
		for (Area a: almLayout.getAreas()) {
			almLayout.getLayoutSpec().setDesc(a.getContent().getName()+" :weightx");
			GridBagConstraints con = componentsConstraints.get(a.getContent());
			
			// apply weightx if there is, at least, one non-zero weightx in this column.
			if (ws.getMaxWeightxInCol(con.gridx) > 0) {
				double maxX = ws.getMaxWeightxInCol(con.gridx);
				double sumMaxX = ws.getSumMaxWeightxInRow()==0 ? maxX : ws.getSumMaxWeightxInRow();
				
				// calculate the weight for this area. If the area spans on more than one column, use sum
				// of weights for all columns as the weight for this area.
				for (int i = 1; i < con.gridwidth; i++) {
					maxX += ws.getMaxWeightxInCol(con.gridx+i);
				}
				
				// Add a constraint to add a proper portion of the extra space to this area. The portion should
				// be added to the inset area not the component area.
				almLayout.addConstraint(1, extendedAreaInfo.getAreaInfo(a).getOuterCol().right, -1, extendedAreaInfo.getAreaInfo(a).getOuterCol().left, 
						-maxX/sumMaxX, hSpace, OperatorType.EQ, 
						extendedAreaInfo.getAreaInfo(a).getOuterCol().right.getValue()-extendedAreaInfo.getAreaInfo(a).getOuterCol().left.getValue());
			}			
		}		
	}
	
	/**
	 * Applies weighty to all areas in the layout.
	 * 
	 * @param pSize preferred size of the layout.
	 */
	private void applyWeighty(Dimension pSize) {
		LayoutSpec ls;
		
		ls = almLayout.getLayoutSpec();
		
		// a variable to show the extra space available in vertical direction.
		Variable vSpace = new Variable(ls);
		
		// Add constraints to calculate extra space.
		almLayout.addConstraint(1, ls.getBottom(), -1, ls.getTop(), -1, vSpace, OperatorType.EQ, pSize.getHeight());
		
		// go through the areas in the layout and apply weights.
		for (Area a: almLayout.getAreas()) {
			almLayout.getLayoutSpec().setDesc(a.getContent().getName()+" :weighty");
			GridBagConstraints con = componentsConstraints.get(a.getContent());

			// apply weighty if there is, at least, one non-zero weighty in this row.
			if (ws.getMaxWeightyInRow(con.gridy) > 0) {
				double maxY = ws.getMaxWeightyInRow(con.gridy);
				double sumMaxY = ws.getSumMaxWeightyInCol()==0 ? maxY : ws.getSumMaxWeightyInCol();				
				
				// calculate the weight for this area. If the area spans on more than one row, use sum
				// of weights for all rows as the weight for this area.

				for (int i = 1; i < con.gridheight; i++) {
					maxY += ws.getMaxWeightyInRow(con.gridy+i);
				}
				
				// Add a constraint to add a proper portion of the extra space to this area. The portion should
				// be added to the inset area not the component area.
				almLayout.addConstraint(1, extendedAreaInfo.getAreaInfo(a).getOuterRow().bottom, -1, extendedAreaInfo.getAreaInfo(a).getOuterRow().top, 
						-maxY/sumMaxY, vSpace, OperatorType.EQ, 
						extendedAreaInfo.getAreaInfo(a).getOuterRow().bottom.getValue()-extendedAreaInfo.getAreaInfo(a).getOuterRow().top.getValue());
			}			
		}		

	}
	/**
	 * Calculates weights calculate weights according to http://vip.cs.utsa.edu/classes/java/tutorial/gridbaglayout.html#Weights
	 * 
	 * If there are components which span more than one row or column, the weights are first determined by 
	 * those components spanning only one row and column. Then each additional component is handled in the 
	 * order it was added. If its weight is less than the total weights of the rows or columns it spans, it 
	 * has no effect. Otherwise, its extra weight is distributed based on the weights of those rows or columns 
	 * it spans.   
	 */
	private void calculateWeights() {
		List<Area> spanned = new ArrayList<Area>();
		
		// Calculate weights according to the components which occupy only one row
		// or one column.
		for (Area a: almLayout.getAreas()) {
			GridBagConstraints c = componentsConstraints.get(a.getContent());
			if (c.gridwidth == 1 && c.gridheight == 1) {
				ws.storeSingle(c);
			} else if (c.gridwidth == 1 && c.gridheight > 1) {
				ws.storeSingleCol(c);
				spanned.add(a);
			} else if (c.gridwidth > 1 && c.gridheight == 1) {
				ws.storeSingleRow(c);
				spanned.add(a);
			} else {
				spanned.add(a);
			}
		}
		
		// Now, apply weights from components which span more than one row or column.
		for (Area a: spanned) {
			GridBagConstraints c = componentsConstraints.get(a.getContent());
			if (c.gridheight == 1) {
				ws.storeColSpanned(c);
			} else if (c.gridwidth == 1) {
				ws.storeRowSpanned(c);
			} else {
				ws.storeSpanned(c);
			}
		}
	}

	/**
	 * update constraints for the components which have already been added to the layout. This
	 * method is called when new rows or columns are added.
	 * 
	 * @param rowAdded number of added rows.
	 * @param colAdded number of added columns.
	 */
	private void updateConstraints(int rowAdded, int colAdded) {
		Set<JComponent> compSet = componentsConstraints.getKeySet();
		for (JComponent comp : compSet) {
			GridBagConstraints compCon = componentsConstraints.get(comp);
			GridBagConstraints origCompCon = componentsConstraints.getOriginal(comp);
			updateComponentConstraints(rowAdded, colAdded, compCon, origCompCon);
		}
		
	}
	
	/**
	 * Updates constraints for a component when new rows or columns are added to the layout. New rows 
	 * or columns can affect components which have RELATIVE or REMAINDER in their constraints.
	 * 
	 * @param rowAdded number of added rows.
	 * @param colAdded number of added columns.
	 * @param con processed constraint.
	 * @param origCon original constraint.
	 */
	
	private void updateComponentConstraints(int rowAdded, int colAdded, GridBagConstraints con, GridBagConstraints origCon) {
		if (origCon.gridheight == GridBagConstraints.RELATIVE) {
			if (con.gridy + con.gridheight < analyzer.getNumGridBagRows()-1) {
				con.gridheight = analyzer.getNumGridBagRows() - con.gridy - 1 <= 0 ? 1 : analyzer.getNumGridBagRows() - con.gridy - 1;
			}
		}		
		
		if (origCon.gridheight == GridBagConstraints.REMAINDER) {
			if (relativeExistsInCol(con.gridx)) {
				con.gridy += rowAdded;
			} else {
				con.gridheight += rowAdded;
			}
		}
		
		if (origCon.gridwidth == GridBagConstraints.RELATIVE) {
			if (con.gridx + con.gridwidth < analyzer.getNumGridBagColumns()-1) {
				con.gridwidth = analyzer.getNumGridBagColumns() - con.gridx - 1 <= 0 ? 1 : analyzer.getNumGridBagColumns() - con.gridx - 1;
			}
		}		
		
		if (origCon.gridwidth == GridBagConstraints.REMAINDER) {
			if (relativeExistsInRow(con.gridy)) {
				con.gridx += colAdded;
			} else {
				con.gridwidth += colAdded;
			}
		}		

	}
	
	/**
	 * Updates location of the component according to the changes in the layout.
	 * 
	 * @param con processed constraints.
	 * @param origCon original constraints.
	 */
	private void updateComponentLocation(GridBagConstraints con, GridBagConstraints origCon) {
		if (origCon.gridx == GridBagConstraints.RELATIVE) {
			con.gridx = getLastUsedColInRow(con.gridy)+1;
		}
		if (origCon.gridy == GridBagConstraints.RELATIVE) {
			if (origCon.gridx != GridBagConstraints.RELATIVE)
				con.gridy = getLastUsedRowInCol(con.gridx)+1;
		}
		updateComponentConstraints(0, 0, con, origCon);
	}
	
	/**
	 * Adjusts width and height for components which have these attributes set to values
	 * bigger than 1. When user explicitly specifies a width or height to be more than
	 * 1, it won't be effective until there is editor components in that rows/columns.
	 */
	private void adjustConstraints() {
		for (Area a: almLayout.getAreas()) {		
			GridBagConstraints origCon = componentsConstraints.getOriginal(a.getContent());
			GridBagConstraints con = componentsConstraints.get(a.getContent());
			if (origCon.gridwidth > 1) {
				con.gridwidth = analyzer.distance(extendedAreaInfo.getAreaInfo(a).getOuterCol().right, extendedAreaInfo.getAreaInfo(a).getOuterCol().left);
			}
			if (origCon.gridheight > 1) {
				con.gridheight = analyzer.distance(extendedAreaInfo.getAreaInfo(a).getOuterRow().top, extendedAreaInfo.getAreaInfo(a).getOuterRow().bottom);
			}
		}
	}
	
	/**
	 * Applies weight constraints.
	 */
	private void applyWeights() {

		relaxPreferredSizeConstraints();
		// Solve the linear system to get the preferred size.		
		Dimension pSize = almLayout.getLayoutSpec().getPreferredSize();
		
		// adjust constraints and calculate weights according to the final layout.
		adjustConstraints();
		calculateWeights();
		
		almLayout.getLayoutSpec().setDesc("Weight");
		
		// apply weightx and witghy to the components in the layout.
		applyWeightx(pSize);
		applyWeighty(pSize);
	}

	/**
	 * Checks whether the underlying ALM object is ready or not.
	 */
	private boolean isALMReady() {
		return almLayout != null;
	}

	/**
	 * Initializes underlying ALM object and editor related objects.
	 */
	private void initALM() {
		if (doingLayout)
			return;
		almLayout = new ALMLayout(new LpSolve());
		analyzer = new SpecGridBagAnalyzer(almLayout.getLayoutSpec());
		ws = new WeightStore(almLayout.getLayoutSpec());
		extendedAreaInfo = new AreaInfoStore();
	}
	
	/**
	 * Reduce priority for the preferred size constraints for the last components 
	 * in rows. It is used when there is no weightx in the layout but there are some 
	 * extra horizontal space. In this case, the extra space should be assigned to 
	 * the last components in the layout's rows.
	 */
	/*private void relaxPreferredSizeConstraintsForLastInRow() {
		for (int i = 0; i < analyzer.getNumGridBagRows(); i++) {
			Component comp = getLastComponentInRow(i);
			if (comp != null) {
				almLayout.getLayoutSpec().setDesc(comp.getName()+" :relax-last-in-row");
				Area a = extendedAreaInfo.getArea(comp);
				a.removePreferredContentSizeConstraints();
				a.setPreferredContentSize(comp.getPreferredSize());
			}
		}
	}
*/
	/**
	 * Reduce priority for the preferred size constraints for the last components in columns.
	 * It is used when there is no weighty in the layout but there are some extra vertical 
	 * space. In this case, the extra space should be assigned to the last components in the 
	 * layout's columns.
	 */
	/*private void relaxPreferredSizeConstraintsForLastInCol() {
		for (int i = 0; i < analyzer.getNumGridBagColumns(); i++) {
			Component comp = getLastComponentInCol(i);
			if (comp != null) {
				almLayout.getLayoutSpec().setDesc(comp.getName()+" :relax-last-in-col");
				Area a = extendedAreaInfo.getArea(comp);
				a.removePreferredContentSizeConstraints();
				a.setPreferredContentSize(comp.getPreferredSize());
			}
		}
	}*/

	/**
	 * Reduce priority for the preferred size constraints for all components in the
	 * layout. It is used when weight constraints are used in the layout and therefore
	 * the extra space should be assigned to components according to their weights.
	 */
	private void relaxPreferredSizeConstraints() {
		for (Area a: almLayout.getAreas()) {
			almLayout.getLayoutSpec().setDesc(a.getContent().getName()+ " :relax");
			GridBagConstraints con = componentsConstraints.get(a.getContent());
			if (con.weightx > 0 || con.weighty > 0) {
				a.setPreferredContentSize(a.getContent().getPreferredSize());
			}
		}
	}

	/**
	 * finds the right most component in a row.
	 * 
	 * @param row row number.
	 * @return the component in the right most location.
	 */
	private Component getLastComponentInRow(int row) {
		Set<JComponent> compInRow = getComponentsInRow(row);
		Component last = null;
		int lastX = -1;
		for(JComponent comp:compInRow) {
			GridBagConstraints compCon = componentsConstraints.get(comp);
			if (compCon.gridx > lastX) {
				lastX = compCon.gridx;
				last = comp;
			}
		}		
		return last;				
	}	

	/**
	 * finds the component at the bottom of a column.
	 * 
	 * @param col column number. 
	 * @return the component at the bottom.
	 */
	private Component getLastComponentInCol(int col) {
		Set<JComponent> compInRow = getComponentsInCol(col);
		Component last = null;
		int lastY = -1;
		for(JComponent comp:compInRow) {
			GridBagConstraints compCon = componentsConstraints.get(comp);
			if (compCon.gridy > lastY) {
				lastY = compCon.gridy;
				last = comp;
			}
		}		
		return last;				
	}	
}
