package linsolve.pivots;

import linsolve.Constraint;
import linsolve.OperatorType;
import linsolve.Summand;

import java.util.ArrayList;
import java.util.List;

/**
 * Improved version of the twophase selector.
 *
 * @author jmue933
 */
public class ImprovedTwoPhaseSelector extends TwoPhaseSelector {

    /**
     * Slightly improved first phase. Now if for a constraint more than one summand has the
     * max coefficient (i.e. the coefficients of those summands is same) than the summand
     * of this set of summands is selected whose variable was not selected before. If all of the
     * variables of the summands were selected before the first summand is selected.
     */
    protected void firstPhase() {
        Summand[] possiblePivotSummands;
        Summand pivotSummand;
        for (Constraint constraint : unassignedConstraints) {
            possiblePivotSummands = getMaximumAbsCoefficients(constraint);
            // We take the first of the Summands as preliminary candidate. If the
            // Variables of all editor summands is already assigned then this summand
            // will be the pivotSummand.
            pivotSummand = possiblePivotSummands[0];
            for (Summand s : possiblePivotSummands) {
                if (unassignedVariables.contains(s.getVar())) {
                    pivotSummand = s;
                    break;
                }
            }
            constraint.setPivotSummand(pivotSummand);
            pivotSummands.put(constraint, pivotSummand);
            unassignedVariables.remove(pivotSummand.getVar());
            if (constraint.getOp() == OperatorType.EQ) {
                unassignedVariables.remove(pivotSummand.getVar());
            }
        }
    }

    private Summand[] getMaximumAbsCoefficients(Constraint constraint) {
        // TODO check for array boundary
        List<Summand> maxSummands = new ArrayList<Summand>();
        maxSummands.add(constraint.getLeftSide()[0]);
        for (Summand s : constraint.getLeftSide()) {
            if (Math.abs(maxSummands.get(0).getCoeff()) < Math.abs(s.getCoeff())) {
                maxSummands = new ArrayList<Summand>();
                maxSummands.add(s);
            } else if (Math.abs(maxSummands.get(0).getCoeff()) == Math.abs(s.getCoeff())) {
                maxSummands.add(s);
            }

        }
        return maxSummands.toArray(new Summand[2]);
    }
}
