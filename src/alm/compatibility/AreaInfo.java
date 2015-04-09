package alm.compatibility;


import alm.Column;
import alm.Row;
import alm.YTab;

/**
 * This class stores some extra information about an ALM area in the layout. For each area three rows and columns
 * are created by GridBagLayout. One for outer margin, one for insets and one for paddings. paddings is stored in 
 * the ALM area. We need the outer row and column for applying weights.
 * @author hnad002
 *
 */
public class AreaInfo {

	private Row outerRow; 	// ALM area contains padding row. outer row is also required for applying weights.
	private Column outerCol;// ALM area contains padding column. outer column is also required for applying weights.
	
	
	public AreaInfo(Row row, Column col) {
		this.outerRow = row;
		this.outerCol = col;
	}
	
	/**
	 * Returns outer row for this area.
	 */
	public Row getOuterRow() {
		return outerRow;
	}
	
	/**
	 * Stores outer row for this area.
	 */
	public void setOuterRow(Row outerRow) {
		this.outerRow = outerRow;
	}
	
	/**
	 * Returns outer column for this area.
	 */
	public Column getOuterCol() {
		return outerCol;
	}
	
	/**
	 * Stores outer column for this area.
	 */	
	public void setOuterCol(Column outerCol) {
		this.outerCol = outerCol;
	}

}

