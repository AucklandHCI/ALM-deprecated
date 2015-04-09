package linsolve.pivots;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

import linsolve.Constraint;
import linsolve.LinearSpec;
import linsolve.OperatorType;
import linsolve.Summand;
import linsolve.Variable;

/**TwoPhaseSelector is a pivot selector algorithm that tries to find best constraint for a variable
 * and best variable for a constraint. 
 * @author njam031
 *
 */
public class TwoPhaseSelector implements PivotSummandSelector{

	private LinearSpec linearSpec;
	protected ArrayList<Constraint> unassignedConstraints;
	protected HashSet<Variable> unassignedVariables;
	Dictionary<Constraint, Summand> pivotSummands = new Hashtable<Constraint, Summand>();
	Dictionary<Summand, Double> influences = new Hashtable<Summand, Double>();
	
	@Override
	public List<Constraint> init(LinearSpec linearSpec) {
		unassignedConstraints = new ArrayList<Constraint>();
		unassignedVariables = new HashSet<Variable>();
		this.linearSpec = linearSpec;

		// unassignVariables stores all variables
		for (Constraint constraint : linearSpec.getConstraints()) {
			unassignedConstraints.add(constraint);
			calculateInfluences(constraint);
			unassignedVariables.addAll(constraint.getVariables());
		}


		
		firstPhase();
		
		ArrayList<Constraint> assignedConstraints = secondPhase();
		assignedConstraints = shuffleConstraints(assignedConstraints);
	
		return assignedConstraints;
	}
	
	private ArrayList<Constraint> shuffleConstraints(ArrayList<Constraint> assignedConstraints){
		Collections.shuffle(assignedConstraints);
		
		int distance = 3;
		
		Constraint previous = assignedConstraints.get(0);
		for(int i = 1; i < assignedConstraints.size(); i++){
			if(previous.getPivotSummand().getVar() == assignedConstraints.get(i).getPivotSummand().getVar()){
				//System.out.println("found " + assignedConstraints.get(i));
				assignedConstraints.add((i + distance) >= assignedConstraints.size() ? assignedConstraints.size() - i : i + distance, assignedConstraints.get(i));
				assignedConstraints.remove(i);
			}
			previous = assignedConstraints.get(i);
		}
		
//		for(Constraint c : assignedConstraints)
//		{
//			System.out.println(c);
//		}	
		return assignedConstraints;
	}

	// Method for calculating the influence of all summand of a constraint
	private void calculateInfluences(Constraint constraint) {
		for (Summand s : constraint.getLeftSide()) {
			influences.put(s, calculateInfluence(s, constraint.getLeftSide()));
		}
	}

	protected void firstPhase() {
		Summand pivotSummand;
		for (Constraint constraint : unassignedConstraints) {
			pivotSummand = identifyMaximumAbsCoeff(constraint);
			pivotSummands.put(constraint, pivotSummand);
			constraint.setPivotSummand(pivotSummand);
			if(constraint.getOp() == OperatorType.EQ){
				unassignedVariables.remove(pivotSummand.getVar());
			}
		}
	}

	private ArrayList<Constraint> secondPhase() {
		
		ArrayList<Constraint> assignedConstraints = new ArrayList<Constraint>(
				unassignedConstraints);
		Constraint maxInfluenceConstraint = null;
		double maxInfluence = Double.MIN_VALUE;
		double currentInfluence = maxInfluence;
		Summand pivotSummand = null;
		
		ArrayList<Variable> variables  = new ArrayList<Variable>(unassignedVariables);
		
		for (Variable var : variables) {
			// TODO check for boundary error
			maxInfluenceConstraint = null;
			maxInfluence = Double.MIN_VALUE;
			pivotSummand = null;

			for (Constraint constraint : unassignedConstraints) {
				// Compare the influences for each constraint
				pivotSummand = getSummandforVar(constraint, var);
				if (pivotSummand == null)
					continue;
				//System.out.println(pivotSummand.toString());
				//System.out.println(influences.get(pivotSummand).toString());
				currentInfluence = influences.get(pivotSummand);
				if (currentInfluence > maxInfluence) {
					maxInfluence = currentInfluence;
					maxInfluenceConstraint = constraint;
				}
			}

			// if(maxInfluenceConstraint == null) throw new Exception();

			if(var != maxInfluenceConstraint.getPivotSummand().getVar()){
				Constraint clone = maxInfluenceConstraint.clone();
				pivotSummand = getSummandforVar(clone, var);
				//System.out.println(pivotSummand);
				
				int k = assignedConstraints.indexOf(maxInfluenceConstraint);
				assignedConstraints.add(k + 1, clone);
				
				var.setConstraintWhereMaxDominant(clone);
				var.setSummandWhereMaxDominant(pivotSummand);
				var.setMaxDominance(influences.get(pivotSummand));
				
				clone.setPivotSummand(pivotSummand);
				pivotSummands.put(clone, var.getSummandWhereMaxDominant());
			}
			unassignedVariables.remove(var);
			
		}
		
		
		return assignedConstraints;
	}

	
	private Summand getSummandforVar(Constraint constraint, Variable var) {
		for (Summand s : constraint.getLeftSide())
			if (s.getVar() == var)
				return s;
		return null;

	}

	private Summand identifyMaximumAbsCoeff(Constraint constraint) {
		// TODO check for array boundary
		Summand maxCoeff = constraint.getLeftSide()[0];
		for (Summand s : constraint.getLeftSide()) {
			if (Math.abs(maxCoeff.getCoeff()) < Math.abs(s.getCoeff()))
				maxCoeff = s;
		}
		return maxCoeff;
	}

	private double calculateInfluence(Summand summand, Summand[] summands) {

		double absCoeffSum = 0.0;
		for (Summand s : summands)
			absCoeffSum += Math.abs(s.getCoeff());

		return Math.abs(summand.getCoeff()) / absCoeffSum;
	}

	public List<Constraint> removeConstraint(List<Constraint> constraints,
			Constraint c) {
		// TODO Auto-generated method stub
		return unassignedConstraints;
	}

	public Summand selectPivotSummand(Constraint constraint) {

		return pivotSummands.get(constraint);
	}

	@Override
	public List<Constraint> init(LinearSpec linearSpec, int maxIndex) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Constraint> init(List<Constraint> constraints,
			List<Variable> variables, int maxIndex) {
		// TODO Auto-generated method stub
		return null;
	}

}
