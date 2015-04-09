package alm.compatibility;

import alm.Column;
import alm.LayoutSpec;
import alm.Row;
import alm.XTab;
import alm.YTab;

import java.awt.GridBagConstraints;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import linsolve.*;

/**
 * Analyzes the row and column structure for Java legacy layout managers. 
 * 
 * @author Gerald 
 */
public class SpecGridBagAnalyzer implements GridBagAnalyzer {
	/**
	 * The layout specification this Analyzer belongs to.
	 */
	private LayoutSpec ls;

	// Stores the rows in a map. The Point object represent the ordered
	// pair of (gridy, gridheight).
	private Map<Point, Row> rowStore;
	private Map<Point, Column> colStore;

	// The current number of rows/cols. 
	private int numRow = 0;
	private int numCol = 0;
	
	// list of XTabs and YTabs defined in the layout
	private List<XTab> xTabs;
	private List<YTab> yTabs;
	
	// row/column before the last components in the layout in x and y directions. 
	XTab beforeLastCol;
	YTab beforeLastRow;

	/**
	 * The constructor sets up the tabs that represent the virtual gridlines of
	 * the gridbag Layout.
	 */
	public SpecGridBagAnalyzer(LayoutSpec ls) {
		this.ls = ls;
		rowStore = new HashMap<Point, Row>();
		colStore = new HashMap<Point, Column>();

		xTabs = new ArrayList<XTab>();
		yTabs = new ArrayList<YTab>();

		// At the beginning, there are 4 tabs specifying the left margin, first column,
		// last column and right margin. beforeLastCol will be set to the column before
		// last column in the layout. it is used to implement RELATIVE for gridwidth 
		// constraint.
		XTab left = ls.getLeft();
		XTab beginCol = ls.addXTab();
		beforeLastCol = ls.addXTab();
		XTab endCol = ls.addXTab();
		XTab right = ls.getRight();

		// order the tabs
		beginCol.isGreaterOrEqual(left);
		beforeLastCol.isGreaterOrEqual(beginCol);
		endCol.isGreaterOrEqual(beforeLastCol);
		right.isGreaterOrEqual(endCol);



		// add the tabs
		xTabs.add(left);
		xTabs.add(beginCol);
		xTabs.add(endCol);
		xTabs.add(right);

		// At the beginning, there are 4 tabs specifying the top margin, first row,
		// last row and bottom margin. beforeLastRow will be set to the row before
		// last row in the layout. it is used to implement RELATIVE for gridheight 
		// constraint.

		YTab top = ls.getTop();
		YTab beginRow = ls.addYTab();
		beforeLastRow = ls.addYTab();
		YTab endRow = ls.addYTab();
		YTab bottom = ls.getBottom();

		beginRow.isGreaterOrEqual(top);
		beforeLastRow.isGreaterOrEqual(beginRow);
		endRow.isGreaterOrEqual(beginRow);
		bottom.isGreaterOrEqual(endRow);
		
		yTabs.add(top);
		yTabs.add(beginRow);
		yTabs.add(endRow);
		yTabs.add(bottom);
	}

	/** 
	 * Creates a new row, if the row is not already in the layout. REMAINDER
	 * and RELATIVE are handled in GridBagLayout.getRow.
	 * 
	 */
	public Row getRow(int gridy, int gridheight, GridBagConstraints con) {
		
		if (con.gridheight == GridBagConstraints.REMAINDER || 
				con.gridheight == GridBagConstraints.RELATIVE) {
			return null;
		}
		
		Point loc = new Point(gridy, con.gridheight);
		Row aRow = null;
		
		// We haven't seen this row before
		if (!rowStore.containsKey(loc)) {
			// Rows are created on demand 
			aRow = ls.addRow(yTabs.get(gridy + 1), yTabs.get(gridy + 1
				+ gridheight));
			rowStore.put(loc, aRow);
			return aRow;
		}
		return rowStore.get(loc);
	}

	/**
	 * Returns an entry from array xTabs from location index. 
	 */
	public XTab getXTab(int index) {
		return xTabs.get(index);
	}

	/**
	 * Returns an entry from array yTabs from location index. 
	 */
	public YTab getYTab(int index) {
		return yTabs.get(index);
	}

	/** 
	 * Creates a new column, if the column is not already in the layout. REMAINDER
	 * and RELATIVE are handled in GridBagLayout.getColumn.
	 */
	public Column getColumn(int gridx, int gridwidth, GridBagConstraints con) {

		if (con.gridwidth == GridBagConstraints.REMAINDER || 
				con.gridwidth == GridBagConstraints.RELATIVE) {
			return null;
		}
		
		Point loc = new Point(gridx, con.gridwidth);		
		Column aCol = null;
		// We haven't seen this column before
		if (!colStore.containsKey(loc)) {
			// Columns are created on demand			
			aCol = ls.addColumn(xTabs.get(gridx + 1), xTabs.get(gridx + 1
				+ gridwidth));			
			colStore.put(loc, aCol);
			return aCol;
		}
		return colStore.get(loc);
	}

	/**
	 * Returns the XTab before the last component in the horizontal direction.
	 */
	public XTab getBeforeLastCol() {
		return beforeLastCol;
	}

	/**
	 * Returns the YTab before the last component in the the vertical direction.
	 */
	public YTab getBeforeLastRow() {
		return beforeLastRow;
	}

	/**
	 * Adds one or more YTabs to the layout, if the new component needs new row(s) to be
	 * added to the layout.
	 */
	public int ensureRows(int gridy, int gridheight) {
		int rowAdded = 0;
		if (gridy + gridheight > numRow) {
			int newNumRow = gridy + gridheight;
			// add tabs
			for (int i = numRow; i < newNumRow; i++) {
				YTab r = ls.addYTab();
				// size-3 will give the one from last; last is the boundary
				YTab rPrev = yTabs.get(yTabs.size() - 3); 
				YTab rlast = yTabs.get(yTabs.size() - 2);
				r.isGreaterOrEqual(rPrev); // order rows
				rlast.isGreaterOrEqual(r);
				yTabs.add(yTabs.size() - 2, r);
			}

			rowAdded = newNumRow - numRow;
			numRow = newNumRow;
		}
		return rowAdded;
	}

	/**
	 * Adds one or more XTabs to the layout, if the new component needs new column(s) to be
	 * added to the layout.
	 */
	public int ensureColumns(int gridx, int gridwidth) {
		int colAdded = 0;
		if (gridx + gridwidth > numCol) {
			int newNumCol = gridx + gridwidth;
			// add tabs
			for (int i = numCol; i < newNumCol; i++) {
				XTab c = ls.addXTab();
				// size-3 will give the one from last; last is the boundary.
				XTab cPrev = xTabs.get(xTabs.size() - 3); 
				XTab clast = xTabs.get(xTabs.size() - 2);
				c.isGreaterOrEqual(cPrev); // order columns
				clast.isGreaterOrEqual(c);
				xTabs.add(xTabs.size() - 2, c);
			}
			colAdded = newNumCol - numCol;
			numCol = newNumCol;
		}
		return colAdded;
	}

	/**
	 * @link alm.compatibility.GridBagAnalyzer.getGBColumnBeginTab(int)
	 */
	public XTab getGBColumnBeginTab(int columnIndex) {
		return xTabs.get(columnIndex + 1);
	}

	/**
	 * @link alm.compatibility.GridBagAnalyzer.getGBColumnEndTab()
	 */
	public XTab getGBColumnEndTab(int columnIndex) {
		return xTabs.get(columnIndex + 2);
	}

	/**
	 * @link alm.compatibility.GridBagAnalyzer.getGBRowBeginTab()
	 */
	public YTab getGBRowBeginTab(int rowIndex) {
		return yTabs.get(rowIndex + 1);
	}

	/**
	 * @link alm.compatibility.GridBagAnalyzer.getGBRowEndTab()
	 */
	public YTab getGBRowEndTab(int rowIndex) {
		return yTabs.get(rowIndex + 2);
	}

	/**
	 * @link alm.compatibility.GridBagAnalyzer.getNumGridBagColumns()
	 */
	public int getNumGridBagColumns() {
		return numCol;
	}
	
	/**
	 * Returns the number of rows in the layout.
	 */
	public int getNumGridBagRows() {
		return numRow;
	}

	/**
	 * Adds a column to the column store. 
	 */
	public void addToColStore(Point loc, Column col) {
		colStore.put(loc, col);
	}
	
	/**
	 * Adds a row to the row store. 
	 */	
	public void addToRowStore(Point loc, Row row) {
		rowStore.put(loc, row);
	}
	
	/**
	 * creates a compact version of row array with no duplicate entry.
	 * @return the compact version.
	 *  
	 * NOTE: the linear system should be solved before.
	 */
	private List<YTab> getCompactRowArray() {
		List <YTab> compact = new ArrayList<YTab>(yTabs);
		int i = 0;
		while (i < compact.size()-1) {
			if (Math.round(compact.get(i).getValue()) == Math.round(compact.get(i+1).getValue())) {
				compact.remove(i);				
			} else {
				i = i + 1;
			}
		}
		return compact;					
	}

	/**
	 * creates a compact version of column array with no duplicate entry.
	 * @return the compact version.
	 * 
	 * NOTE: the linear system should be solved before.
	 */

	private List<XTab> getCompactColArray() {
		List <XTab> compact = new ArrayList<XTab>(xTabs);
		int i = 0;
		while (i < compact.size()-1) {
			if (Math.round(compact.get(i).getValue()) == Math.round(compact.get(i+1).getValue())) {
				compact.remove(i);				
			} else {
				i = i + 1;
			}
		}
		return compact;					
	}
	
	/**
	 * finds a an XTab in a array of XTabs.
	 * @param l list to search.
	 * @param x1 item to be searched in the list.
	 * @return location of the item. -1 if not found.
	 * 
	 * NOTE: the linear system should be solved before.
	 */
	
	private int find(List <XTab> l, XTab x1) {	
		for (int i = 0; i < l.size(); i++ ) {
			if (Math.round(l.get(i).getValue()) == Math.round(x1.getValue())) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * finds a an YTab in a array of YTabs.
	 * @param l list to search.
	 * @param y1 item to be searched in the list.
	 * @return location of the item. -1 if not found.
	 * 
	 * NOTE: the linear system should be solved before.
	 */

	private int find(List <YTab> l, YTab y1) {	
		for (int i = 0; i < l.size(); i++ ) {
			if (Math.round(l.get(i).getValue()) == Math.round(y1.getValue())) {
				return i;
			}
		}
		return -1;
	}
	
	/**
	 * Calculates how many rows exist between two YTabs in the layout. 
	 * @param y1 first YTab.
	 * @param y2 second YTab.
	 * @return number of rows exists between y1 and y2. -1 if y1 or y2 or both not found
	 * in the layout.
	 * 
	 * NOTE: the linear system should be solved before.
	 */
	
	public int distance(YTab y1, YTab y2) {
		List <YTab> l = getCompactRowArray();
		int start = find(l, y1);
		int end = find (l, y2);
		if (start >= 0 && end >= 0) {
			return Math.abs(end - start);
		}
		return -1;
	}

	/**
	 * Calculates how many columns exist between two XTabs in the layout. 
	 * @param x1 first XTab.
	 * @param x2 second YTab.
	 * @return number of rows exists between x1 and x2. -1 if x1 or x2 or both not found
	 * in the layout.
	 * 
	 * NOTE: the linear system should be solved before.
	 */

	public int distance(XTab x1, XTab x2) {
		List <XTab> l = getCompactColArray();
		int start = find(l, x1);
		int end = find (l, x2);
		if (start >= 0 && end >= 0) {
			return Math.abs(end - start);
		}
		return -1;
	}
}
