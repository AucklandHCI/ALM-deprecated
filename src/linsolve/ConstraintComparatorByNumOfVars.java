package linsolve;

import java.util.Comparator;

/**
 * This is a comparator for sorting unassigned constraints according to number of variables.
 */
public class ConstraintComparatorByNumOfVars implements Comparator<Constraint> {
    public int compare(Constraint arg1, Constraint arg0) {
        if (arg0.getUnassignedVariables() > arg1.getUnassignedVariables())
            return -1;
        if (arg0.getUnassignedVariables() < arg1.getUnassignedVariables())
            return 1;
        return 0;
    }
}
