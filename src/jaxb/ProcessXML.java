package jaxb;

import java.util.*;

import javax.swing.JComponent;

import linsolve.*;
import alm.ALMLayout;
import alm.Area;
import alm.LayoutSpec;
import alm.XTab;
import alm.YTab;

public class ProcessXML {
	private ALMLayout aLMEngine;
	private Map<String, Variable> map;
	private LayoutSpec newls;
	private List<Summand> summands;

	public LayoutSpec getNewLayoutSpec() {
	    return newls;
	}
	
	public ProcessXML(ALMLayout aLMEngine) {
		this.aLMEngine = aLMEngine;
		newls = new LayoutSpec();
		summands = new ArrayList<Summand>();
		map = new TreeMap();
		map.put("left", newls.getLeft());
		map.put("top", newls.getTop());
		map.put("right", newls.getRight());
		map.put("bottom", newls.getBottom());
		map.put("left inset", newls.getLeftInset());
		map.put("top inset", newls.getTopInset());
		map.put("right inset", newls.getRightInset());
		map.put("bottom inset", newls.getBottomInset());
	}

	public void parseArea(ALMSchema.Area area) {
		// Find control from saved controls
		JComponent control = null;
		// Variable left, top, right, bottom;
		XTab left, right;
		YTab top, bottom;
		for (int i = 0; i < aLMEngine.savedControls.size(); i++) {
			control = aLMEngine.savedControls.get(i);
			if (area.getName().equals(control.getName())) {
				break;
			}
		}
		// If left tab found in map use it, otherwise add it to map
		if (map.containsKey(area.getLeft())) {
			left = (XTab) map.get(area.getLeft());
		} else {
			left = newls.addXTab();
			map.put(area.getLeft(), left);
		}

		// If top tab found in map use it, otherwise add it to map
		if (map.containsKey(area.getTop())) {
			top = (YTab) map.get(area.getTop());
		} else {
			top = newls.addYTab();
			map.put(area.getTop(), top);
		}

		// If right tab found in map use it, otherwise add it to map
		if (map.containsKey(area.getRight())) {
			right = (XTab) map.get(area.getRight());
		} else {
			right = newls.addXTab();
			map.put(area.getRight(), right);
		}

		// If bottom tab found in map use it, otherwise add it to map
		if (map.containsKey(area.getBottom())) {
			bottom = (YTab) map.get(area.getBottom());
		} else {
			bottom = newls.addYTab();
			map.put(area.getBottom(), bottom);
		}
		control.setVisible(true);
		// Add area to layout specification
		Area newArea = newls.addArea(left, top, right, bottom, control);

		// Set margins
		if (area.getLeftinset() != null)
			newArea.setLeftInset(area.getLeftinset().intValue());
		if (area.getTopinset() != null)
			newArea.setTopInset(area.getTopinset().intValue());
		if (area.getRightinset() != null)
			newArea.setRightInset(area.getRightinset().intValue());
		if (area.getBottominset() != null)
			newArea.setBottomInset(area.getBottominset().intValue());

		// Set alignments
		if (area.getHorizontalalignment() != null) {		
			newArea.setHorizontalAlignment(area.getHorizontalalignment());
		}
		if (area.getVerticalalignment() != null)
			newArea.setVerticalAlignment(area.getVerticalalignment());
	}

	public void parseConstraint(ALMSchema.Constraint constraint) {
		// Add constraint to layout specification
		List<ALMSchema.Constraint.Leftside> leftsideList = constraint
				.getLeftside();
		for (int i = 0; i < leftsideList.size(); i++) {
			parseLeftside(leftsideList.get(i));
		}
		//Store the names of the left side variables.
		String[] names = new String[2];
		names[0] = summands.get(0).getVar().getName();
		try{
			names[1] = summands.get(1).getVar().getName();
		} catch (Exception ex){
			names[1] = null;
		}
		boolean potentialEdge = summands.size() == 1 && names[0] != null;
		boolean potentialBorder = summands.size() == 2 && names[0] != null && names[1] != null;
		if (constraint.getOp().equals("EQ"))
			//Ensure that the GUI boundary and inset constraints are not duplicated.
			if(potentialEdge
					&& names[0].equals("left")){
				newls.setLeft(constraint.getRightside().doubleValue());
			} else if(potentialEdge
					&& names[0].equals("right")){
				newls.setRight(constraint.getRightside().doubleValue());
			} else if(potentialEdge
					&& names[0].equals("top")){
				newls.setTop(constraint.getRightside().doubleValue());
			} else if(potentialEdge
					&& names[0].equals("bottom")){
				newls.setBottom(constraint.getRightside().doubleValue());
			} else if (potentialBorder
					&& names[0].equals("left inset") 
					&& names[1].equals("left")){
				newls.setLeft(constraint.getRightside().doubleValue());
			} else if (potentialBorder
					&& names[0].equals("top inset") 
					&& names[1].equals("top")){
				newls.setTop(constraint.getRightside().doubleValue());
			} else if (potentialBorder
					&& names[0].equals("right") 
					&& names[1].equals("right inset")){
				newls.setRight(constraint.getRightside().doubleValue());
			} else if (potentialBorder
					&& names[0].equals("bottom") 
					&& names[1].equals("bottom inset")){
				newls.setBottom(constraint.getRightside().doubleValue());
			} else {
				newls.addConstraint((Summand[]) summands.toArray(new Summand[summands.size()]),
					OperatorType.EQ, constraint.getRightside().doubleValue());
			}
		else if (constraint.getOp().equals("LE"))
			newls.addConstraint((Summand[]) summands.toArray(new Summand[summands.size()]),
					OperatorType.LE, constraint.getRightside().doubleValue());
		else if (constraint.getOp().equals("GE"))
			newls.addConstraint((Summand[]) summands.toArray(new Summand[summands.size()]),
					OperatorType.GE, constraint.getRightside().doubleValue());

		summands.clear();
		
	}

	void parseLeftside(ALMSchema.Constraint.Leftside leftside) {
		List<jaxb.ALMSchema.Constraint.Leftside.Summand> summandList = leftside
				.getSummand();		
		for (int i = 0; i < summandList.size(); i++) {
			parseSummand(summandList.get(i));
		}
	}

	void parseSummand(ALMSchema.Constraint.Leftside.Summand summand) {
		Variable var = null;
		// Add summand to summands list which is used when parsing constraint
		if (map.containsKey(summand.getVar())){
			var = map.get(summand.getVar());
		}else{
			if (summand.getType().equals("X-Tab")){
				var = newls.addXTab();
				map.put(summand.getVar(), var);
			}else if(summand.getType().equals("Y-Tab")){
				var = newls.addYTab();
				map.put(summand.getVar(), var);
			} else {
				var = newls.addVariable();
				map.put(summand.getVar(), var);
			}
			
		}
		Summand newSummand = new Summand(summand.getCoeff().doubleValue(), var);
		summands.add(newSummand);
	}
}
