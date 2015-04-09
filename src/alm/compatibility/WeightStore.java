package alm.compatibility;

import java.awt.GridBagConstraints;
import java.util.HashMap;
import java.util.Map;

import alm.LayoutSpec;

import linsolve.Variable;

/**
 * 
 * This class provides a place for storing weight information. For each row and
 * each column in the layout, maximum weightx and maximum weighty and sum of these
 * maximums are stored in this class. The information can be accessed by row or column
 * number in the GridBag layout.
 *
 * @author hnad002
 */


// this class stores weight info for one row or column.
class Weight {
	public double maxX; // maximum weightx.
	public double sumX; // maximum weighty.
	public double maxY; // sum of maximum wightxs.
	public double sumY; // sum of maximum wightys.
	
	public Weight (LayoutSpec ls, double weightX, double weightY) {
		this.maxX = weightX;
		sumX = weightX;
		this.maxY = weightY;
		sumY = weightY;				
	}
}

// Storage class for storing weight information for rows/columns in the layout.
public class WeightStore {
	
	Map <Integer, Weight> weightRow; // weight information for rows. The key is row index.
	Map <Integer, Weight> weightCol; // weight information for columns. The key is column index.
	LayoutSpec ls;
	
	/**
	 * Constructor.
	 */
	public WeightStore(LayoutSpec ls) {
		weightRow = new HashMap<Integer, Weight>();
		weightCol = new HashMap<Integer, Weight>();
		this.ls = ls;
	}
	
	/**
	 * Returns maximum weightx in a row. 
	 */
	public double getMaxWeightxInRow(int gridy) {
		if (weightRow.containsKey(gridy))
			return weightRow.get(gridy).maxX;
		return 0;		
	}
	
	/**
	 * Returns maximum weighty in a row. 
	 */
	public double getMaxWeightyInRow(int gridy) {
		if (weightRow.containsKey(gridy))
			return weightRow.get(gridy).maxY;
		return 0;		
	}
	
	/**
	 * Returns sum of weightx of all components in a row. 
	 */
	public double getSumWeightxInRow(int gridy) {
		if (weightRow.containsKey(gridy))
			return weightRow.get(gridy).sumX;
		return 0;		
	}

	/**
	 * Returns sum of weighty of all components in a row. 
	 */
	public double getSumWeightyInRow(int gridy) {
		if (weightRow.containsKey(gridy))
			return weightRow.get(gridy).sumY;
		return 0;		
	}

	/**
	 * Returns maximum weightx in a column. 
	 */
	public double getMaxWeightxInCol(int gridx) {
		if (weightCol.containsKey(gridx))
			return weightCol.get(gridx).maxX;
		return 0;		
	}

	/**
	 * Returns maximum weighty in a column. 
	 */
	public double getMaxWeightyInCol(int gridx) {
		if (weightCol.containsKey(gridx))
			return weightCol.get(gridx).maxY;
		return 0;		
	}

	/**
	 * Returns sum of weightx of all components in a column. 
	 */
	public double getSumWeightxInCol(int gridx) {
		if (weightCol.containsKey(gridx))
			return weightCol.get(gridx).sumX;
		return 0;		
	}

	/**
	 * Returns sum of weighty of all components in a column. 
	 */
	public double getSumWeightyInCol(int gridx) {
		if (weightCol.containsKey(gridx))
			return weightCol.get(gridx).sumY;
		return 0;		
	}

	/**
	 * Returns sum of maximum weightx of all columns. 
	 */
	public double getSumMaxWeightxInRow() {
		double sum = 0;
		for (Map.Entry<Integer, Weight> entry : weightCol.entrySet()) {		    
		    Weight value = entry.getValue();
		    sum += value.maxX;
		}
		return sum;		
	}
	
	/**
	 * Returns sum of maximum weighty of all columns. 
	 */	
	public double getSumMaxWeightyInRow() {
		double sum = 0;
		for (Map.Entry<Integer, Weight> entry : weightCol.entrySet()) {		    
		    Weight value = entry.getValue();
		    sum += value.maxY;
		}
		return sum;				
	}
	
	/**
	 * Returns sum of maximum weightx of all rows. 
	 */		
	public double getSumMaxWeightxInCol() {
		double sum = 0;
		for (Map.Entry<Integer, Weight> entry : weightRow.entrySet()) {		    
		    Weight value = entry.getValue();
		    sum += value.maxX;
		}
		return sum;						
	}

	/**
	 * Returns sum of maximum weighty of all rows. 
	 */		
	public double getSumMaxWeightyInCol() {
		double sum = 0;
		for (Map.Entry<Integer, Weight> entry : weightRow.entrySet()) {		    
		    Weight value = entry.getValue();
		    sum += value.maxY;
		}
		return sum;								
	}
	
	/**
	 * Stores weight constraint values for a component which occupies only one row.
	 * 
	 * @param c component's constraints.
	 */
	public void storeSingleRow(GridBagConstraints c) {
		if (!weightRow.containsKey(c.gridy)) {
			weightRow.put(c.gridy, new Weight(ls, c.weightx, c.weighty));
		} else {
			Weight w = weightRow.get(c.gridy);
			w.sumX += c.weightx;
			w.maxX = w.maxX < c.weightx ? c.weightx : w.maxX;
			w.sumY += c.weighty;
			w.maxY = w.maxY < c.weighty ? c.weighty : w.maxY;
			weightRow.put(c.gridy, w);
		}		
	}

	/**
	 * Stores weight constraint values for a component which occupies only one column.
	 * 
	 * @param c component's constraints.
	 */
	public void storeSingleCol(GridBagConstraints c) {
		if (!weightCol.containsKey(c.gridx)) {
			weightCol.put(c.gridx, new Weight(ls, c.weightx, c.weighty));
		} else {
			Weight w = weightCol.get(c.gridx);
			w.sumY += c.weighty;
			w.maxY = w.maxY < c.weighty ? c.weighty : w.maxY;
			w.sumX += c.weightx;
			w.maxX = w.maxX < c.weightx ? c.weightx : w.maxX;
			weightCol.put(c.gridx, w);
		}
	}
	
	/**
	 * updates row and column weight stores according to a new component's
	 * constraints. The component is a single row/single column component.
	 * 
	 * @param c component's constraints.
	 */
	public void storeSingle(GridBagConstraints c) {
		storeSingleRow(c);
		storeSingleCol(c);
	}
	
	/**
	 * Stores weight constraint values for a component which occupies more than one column.
	 * 
	 * @param c component's constraints.
	 */	
	public void storeColSpanned(GridBagConstraints c) {
		double total = 0;
		GridBagConstraints cc = (GridBagConstraints)c.clone();
		cc.weighty = 0;
		for (int i = 0; i < cc.gridwidth; i++) {
			total += getMaxWeightxInCol(cc.gridx+i);
		}
		if (cc.weightx > total) {
			double extra = cc.weightx - total;
			// if total weight for the columns is zero, assign extra space to the
			// last column.
			if (total == 0) {
				cc.gridx += cc.gridwidth-1;
				cc.weightx = extra;
				storeSingle(cc);

			} else {
				for (int i = 0; i < cc.gridwidth; i++) {					
					cc.weightx = getMaxWeightxInCol(cc.gridx) + getMaxWeightxInCol(cc.gridx)/total * extra;
					storeSingle(cc);
					cc.gridx ++;
				}
			}	
		}
	}

	/**
	 * Stores weight constraint values for a component which occupies more than one row.
	 * 
	 * @param c component's constraints.
	 */		
	public void storeRowSpanned(GridBagConstraints c) {
		double total = 0;
		GridBagConstraints cc = (GridBagConstraints)c.clone();
		cc.weightx = 0;
		for (int i = 0; i < cc.gridheight; i++) {
			total += getMaxWeightyInRow(cc.gridy+i);
		}
		if (cc.weighty > total) {
			double extra = cc.weighty - total;
			// if total weight for the rows is zero, assign extra space to the
			// last row.				
			if (total == 0) {
				cc.gridy += cc.gridheight-1;
				cc.weighty = extra;
				storeSingle(cc);
			} else {
				for (int i = 0; i < cc.gridheight; i++) {						
					cc.weighty = getMaxWeightyInRow(cc.gridy) + getMaxWeightyInRow(cc.gridy)/total * extra;
					storeSingle(cc);
					cc.gridy ++;
				}
			}
		}		
	}

	/**
	 * updates row and column weight stores according to the specified component's
	 * constraints. The component is spanned more than one row or column.
	 * 
	 * according to http://vip.cs.utsa.edu/classes/java/tutorial/gridbaglayout.html#Weights
	 * 
	 * If there are components which span more than one row or column, the weights are first 
	 * determined by those components spanning only one row and column. Then each additional 
	 * component is handled in the order it was added. If its weight is less than the total 
	 * weights of the rows or columns it spans, it has no effect. Otherwise, its extra weight 
	 * is distributed based on the weights of those rows or columns it spans.   
	 * 
	 * @param c component's constraints.
	 */

	public void storeSpanned(GridBagConstraints c) {
		if (c.gridwidth > 1) {
			// The component spans more than one column.
			storeColSpanned(c);
		} else { 
			// The component uses only one column.
			GridBagConstraints cc = (GridBagConstraints)c.clone();
			cc.weighty = 0;
			storeSingle(cc);
		}
		
		if (c.gridheight > 1) {
			// The component spans more than one row.
			storeRowSpanned(c);
		} else {
			// The component uses only one row.
			GridBagConstraints cc = (GridBagConstraints)c.clone();
			cc.weightx = 0;
			storeSingle(cc);			
		}
	}
}
