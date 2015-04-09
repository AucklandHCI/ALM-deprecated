package linsolve;

import java.util.Comparator;

/** This is a comparator for sorting constraints according to their penalties (priorities). */
public class ConstraintComparatorByPenalty implements Comparator<Constraint> {
    public int compare(Constraint arg0, Constraint arg1) {
        if (arg0.getPenalty() > arg1.getPenalty())
            return -1;
        if (arg0.getPenalty() < arg1.getPenalty())
            return 1;
        return 0;
    }
}
