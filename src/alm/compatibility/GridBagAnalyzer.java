package alm.compatibility;


import java.awt.GridBagConstraints;

import linsolve.*;
import alm.Column;
import alm.Row;
import alm.XTab;
import alm.YTab;

/**
 *  Analyzes the row and column structure for Java legacy 
 *  layout manager.
 *
 *  @author Yuk-Kwan Chan, Nathan Wood
 */
public interface GridBagAnalyzer  {

	/**
	 * Returns the appropriate row for the given parameters.
	 * 
	 * @param gridy
	 *            the upper row of a component's display area. Symbolic
	 *            constants such as GridBagConstraints.RELATIVE cannot be used.
	 * @param gridheight
	 *            The number of columns in a row occupied by this component.
	 */
	public Row getRow(int gridy, int gridheight, GridBagConstraints con);

	/**
	 * Returns the appropriate column for the given parameters.
	 * 
	 * @param gridx
	 *            the upper row of a component's display area. Symbolic
	 *            constants such as GridBagConstraints.RELATIVE cannot be used.
	 * @param gridwidth
	 *            The number of columns in a row occupied by this component.
	 * @param con
	 * 			  The component's constraints.           
	 */
	public Column getColumn(int gridx, int gridwidth, GridBagConstraints con);
	
	/**
	 * Returns the number of rows in the layout.
	 */
	public int getNumGridBagRows() ;
	
	/**
	 * Returns the number of columns in the layout.
	 */
	public int getNumGridBagColumns() ;
	
	/**
	 * Returns the first XTab in a column.
	 */
	public XTab getGBColumnBeginTab(int columnIndex);
	
	/**
	 * Returns the last XTab in a column.
	 */
	public XTab getGBColumnEndTab(int columnIndex);
	
	/**
	 * Returns the first YTab in a row.
	 */
	public YTab getGBRowBeginTab(int rowIndex);
	
	/**
	 * Returns the last YTab in a row.
	 */
	public YTab getGBRowEndTab(int rowIndex);	
}

