package linsolve.pivots;

import linsolve.*;

import java.util.*;

/**
 * This is deterministic pivot selector that selects pivot elements deterministically
 *
 * @author njam031
 */
public class DeterministicPivotSummandSelector implements PivotSummandSelector {
    final static boolean debug = false;

    Dictionary<Constraint, Summand> pivotSummands;

    @Override
    public List<Constraint> init(LinearSpec linearSpec, int maxIndex) {
        return init(linearSpec.getConstraints(), linearSpec.getVariables(), maxIndex);
    }

    /**
     * @see linsolve.pivots.PivotSummandSelector#init(java.util.List, java.util.List, int)
     */
    @Override
    public List<Constraint> init(List<Constraint> constraints, List<Variable> variables, int maxIndex) {

		/* Otherwse the v.constraintWhereMaxDominant would be saved for the next run 
        * and would disturb the result. We would get constraints in the list which ar
		* turned off.
		*/
        for (Variable v : variables) {
            v.setConstraintWhereMaxDominant(null);
        }
		
		/*
		 * Updateable loop variables: List of unassigned constraints: not yet
		 * pivot element decided. Initialized with: all enabled constraints.
		 */
        ArrayList<Constraint> unassignedConstraints = new ArrayList<Constraint>();
        HashSet<Variable> unassignedVariables = new HashSet<Variable>();

        Constraint c1;
        for (int i = 0; i < maxIndex; i++) {
            c1 = constraints.get(i);
            if (c1.isEnabled()) {
                unassignedConstraints.add(c1);
                unassignedVariables.addAll(c1.getVariables());
            }
        }


        // after the while loop below finishes, the unassignedConstraints will
        // be assigned
        ArrayList<Constraint> assignedConstraints = new ArrayList<Constraint>(
                unassignedConstraints);
		
		/*
		 * Initialize number of unassigned variables for each constraint
		 */
        for (Constraint constraint : unassignedConstraints) {
            constraint.setUnassignedVariables(constraint.getLeftSide().length);
        }

        if (debug) {
            for (int i = 0; i < unassignedConstraints.size(); i++) {
                System.out.println("Constraints before pivote:  "
                        + unassignedConstraints.get(i));
            }
        }

        pivotSummands = new Hashtable<Constraint, Summand>();
        while (!unassignedConstraints.isEmpty()) {

            // Sort unassigned constraints according to number of
            // variables in ascending order
            Collections.sort(unassignedConstraints,
                    new ConstraintComparatorByNumOfVars());
            // start with the constraint that has the smallest number of
            // unassigned variables
            Constraint constraint = unassignedConstraints.get(0);

            Summand[] currentSummands = constraint.getLeftSide();
            Summand pivotSummand = null;
            double pivotcoeff = Double.MIN_VALUE;
            double absCoeffSum = 0;

            // choose the unassigned variable with the maximum absolute
            // coefficient
            for (int j = 0; j < currentSummands.length; j++) {
                if ((pivotcoeff < Math.abs(currentSummands[j].getCoeff()) && unassignedVariables
                        .contains(currentSummands[j].getVar()))) {
                    pivotcoeff = currentSummands[j].getCoeff();
                    pivotSummand = currentSummands[j];
                }
                absCoeffSum += Math.abs(currentSummands[j].getCoeff());
            }

            // On-the-fly computation of var.constraintWhereMaxDominant for
            // later use.
            // update optimal pivot constraint for each variable in current
            // constraint
            // i.e. check for each variable if this constraint is more dominant
            for (Summand s : currentSummands) {
                double dominance = Math.abs(s.getCoeff()) / absCoeffSum;
                if (dominance > s.getVar().getMaxDominance()) {
                    s.getVar().setConstraintWhereMaxDominant(constraint);
                    s.getVar().setSummandWhereMaxDominant(s);
                    s.getVar().setMaxDominance(dominance);
                }
            }

            // if there are no unassigned variables, then simply choose the
            // variable with the maximum absolute coefficient
            if (pivotSummand == null) {
                for (int j = 0; j < currentSummands.length; j++) {
                    if (pivotcoeff < Math.abs(currentSummands[j].getCoeff())) {
                        pivotcoeff = currentSummands[j].getCoeff();
                        pivotSummand = currentSummands[j];
                    }
                }
            }

            // register the chosen pivot element
            pivotSummands.put(constraint, pivotSummand);
            constraint.setPivotSummand(pivotSummand);

            // remove variable from list of unassigned variables only if this is
            // an equality
            if (OperatorType.EQ.equals(constraint.getOp())) {
                unassignedVariables.remove(pivotSummand.getVar());
            }

            // Delete that constraint from the list of unassigned constraints.
            unassignedConstraints.remove(0);

            if (debug) {
                System.err.println("Pivot: " + pivotSummand.getCoeff() + "\tvar: "
                        + pivotSummand.getVar());
                System.err.println(pivotSummand);
                System.err.println("summands1.length : "
                        + unassignedVariables.size());
            }

			/*
			 * For all constraints that also have this variable: Update number
			 * of variables: reduce by 1
			 */
            for (Constraint c : unassignedConstraints) {
                for (Summand summand : c.getLeftSide())
                    if (OperatorType.EQ.equals(constraint.getOp()) && summand.getVar().equals(pivotSummand.getVar()))
                        c.setUnassignedVariables(c.getUnassignedVariables() - 1);

            }
        }
        // add most dominant constraints for the variables that have not been
        // selected as pivot variables yet
        for (Variable v : unassignedVariables) {
            duplicateConstraintForVariable(assignedConstraints, v);
        }

        return assignedConstraints;
    }

    /*
     * (non-Javadoc)
     *
     * @see linsolve.PivotSummandSelector#init(linsolve.LinearSpec)
     *
     * This is the pivot element selection strategy for deterministic linear
     * relaxation.
     */
    @Override
    public List<Constraint> init(LinearSpec linearSpec) {
		/*
		 * Updateable loop variables: List of unassigned constraints: not yet
		 * pivot element decided. Initialized with: all enabled constraints.
		 */
        ArrayList<Constraint> unassignedConstraints = new ArrayList<Constraint>();
        HashSet<Variable> unassignedVariables = new HashSet<Variable>();
        for (Constraint constraint : linearSpec.getConstraints()) {
            if (constraint.isEnabled()) {
                unassignedConstraints.add(constraint);
                unassignedVariables.addAll(constraint.getVariables());
            }
        }

        // after the while loop below finishes, the unassignedConstraints will
        // be assigned
        ArrayList<Constraint> assignedConstraints = new ArrayList<Constraint>(
                unassignedConstraints);

		/*
		 * Initialize number of unassigned variables for each constraint
		 */
        for (Constraint constraint : unassignedConstraints) {
            constraint.setUnassignedVariables(constraint.getLeftSide().length);
        }

        if (debug) {
            for (int i = 0; i < unassignedConstraints.size(); i++) {
                System.out.println("Constraints before pivote:  "
                        + unassignedConstraints.get(i));
            }
        }

        pivotSummands = new Hashtable<Constraint, Summand>();
        while (!unassignedConstraints.isEmpty()) {

            // Sort unassigned constraints according to number of
            // variables in ascending order
            Collections.sort(unassignedConstraints,
                    new ConstraintComparatorByNumOfVars());
            // start with the constraint that has the smallest number of
            // unassigned variables
            Constraint constraint = unassignedConstraints.get(0);

            Summand[] currentSummands = constraint.getLeftSide();
            Summand pivotSummand = null;
            double pivotcoeff = Double.MIN_VALUE;
            double absCoeffSum = 0;

            // choose the unassigned variable with the maximum absolute
            // coefficient
            for (int j = 0; j < currentSummands.length; j++) {
                if ((pivotcoeff < Math.abs(currentSummands[j].getCoeff()) && unassignedVariables
                        .contains(currentSummands[j].getVar()))) {
                    pivotcoeff = currentSummands[j].getCoeff();
                    pivotSummand = currentSummands[j];
                }
                absCoeffSum += Math.abs(currentSummands[j].getCoeff());
            }

            // On-the-fly computation of var.constraintWhereMaxDominant for
            // later use.
            // update optimal pivot constraint for each variable in current
            // constraint
            // i.e. check for each variable if this constraint is more dominant
            for (Summand s : currentSummands) {
                double dominance = Math.abs(s.getCoeff()) / absCoeffSum;
                if (dominance > s.getVar().getMaxDominance()) {
                    s.getVar().setConstraintWhereMaxDominant(constraint);
                    s.getVar().setSummandWhereMaxDominant(s);
                    s.getVar().setMaxDominance(dominance);
                }
            }

            // if there are no unassigned variables, then simply choose the
            // variable with the maximum absolute coefficient
            if (pivotSummand == null) {
                for (int j = 0; j < currentSummands.length; j++) {
                    if (pivotcoeff < Math.abs(currentSummands[j].getCoeff())) {
                        pivotcoeff = currentSummands[j].getCoeff();
                        pivotSummand = currentSummands[j];
                    }
                }
            }

            // register the chosen pivot element
            pivotSummands.put(constraint, pivotSummand);
            constraint.setPivotSummand(pivotSummand);

            // remove variable from list of unassigned variables only if this is
            // an equality
            if (OperatorType.EQ.equals(constraint.getOp())) {
                unassignedVariables.remove(pivotSummand.getVar());
            }

            // Delete that constraint from the list of unassigned constraints.
            unassignedConstraints.remove(0);

            if (debug) {
                System.err.println("Pivot: " + pivotSummand.getCoeff() + "\tvar: "
                        + pivotSummand.getVar());
                System.err.println(pivotSummand);
                System.err.println("summands1.length : "
                        + unassignedVariables.size());
            }

			/*
			 * For all constraints that also have this variable: Update number
			 * of variables: reduce by 1
			 */
            for (Constraint c : unassignedConstraints) {
                for (Summand summand : c.getLeftSide())
                    if (OperatorType.EQ.equals(constraint.getOp()) && summand.getVar().equals(pivotSummand.getVar()))
                        c.setUnassignedVariables(c.getUnassignedVariables() - 1);

            }
        }
        // add most dominant constraints for the variables that have not been
        // selected as pivot variables yet
        for (Variable v : unassignedVariables) {
            duplicateConstraintForVariable(assignedConstraints, v);
        }

        return assignedConstraints;
    }

    private List<Constraint> duplicateConstraintForVariable(
            List<Constraint> assignedConstraints, Variable v) {

        // add a duplicate of the most dominant constraint for that variable
        if (v.getConstraintWhereMaxDominant() == null) {
            double coeff = 0.0d;
            Summand max = null;
            Constraint maxC = null;
            for (Constraint c : assignedConstraints) {
                for (Summand s : c.getLeftSide()) {
                    if (s.getVar() == v) {
                        //System.out.println("In Constraint " + c + " is a summnd for var");
                        if (Math.abs(s.getCoeff()) > Math.abs(coeff)) {
                            max = s;
                            coeff = Math.abs(s.getCoeff());
                            maxC = c;
                        }
                    }
                }
            }
            //System.err.println("Variable " + v + " has no constraint. Assign " + maxC);
            v.setConstraintWhereMaxDominant(maxC);
            v.setSummandWhereMaxDominant(max);

            //System.err.println("Variable has no Pivot constraint: " + v);
        }
        Constraint duplicate = v.getConstraintWhereMaxDominant().clone();

        // insert duplicate constraint after the original constraint,
        // so that the ordering by penalty is not disturbed
        int k = assignedConstraints.indexOf(v.getConstraintWhereMaxDominant());
        assignedConstraints.add(k + 1, duplicate);

        // register the chosen pivot element
        pivotSummands.put(duplicate, v.getSummandWhereMaxDominant());
        duplicate.setPivotSummand(v.getSummandWhereMaxDominant());

        return assignedConstraints;
    }

    @Override
    public List<Constraint> removeConstraint(List<Constraint> constraints,
                                             Constraint c) {
        // If there is another enabled constraint c2 that has the same pivot variable
        // as the removed constraint c, then the variable still has a pivot constraint
        // and we don't need to create a duplicate constraint for it.
        Variable pivotVariable = c.getPivotSummand().getVar();
        for (Constraint c2 : constraints)
            if (c2.isEnabled() && c2.getPivotSummand().getVar() == pivotVariable)
                return constraints;

        // The pivot variable of the removed constraint does not have another
        // pivot constraint, therefore we need to duplicate one of the existing
        // constraints to make it the variable's pivot constraint.
        return duplicateConstraintForVariable(constraints,
                c.getPivotSummand().getVar());
    }

    @Override
    public Summand selectPivotSummand(Constraint constraint) {
        return pivotSummands.get(constraint);
    }
}
