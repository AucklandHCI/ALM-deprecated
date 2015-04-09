package linsolve.softconstraints;

import linsolve.AbstractLinearSolver;
import linsolve.ResultType;

public class GroupingSoftSolver extends AbstractSoftSolver {

    public GroupingSoftSolver(AbstractLinearSolver solver) {
        super(solver);
    }

    public ResultType doSolve() {
        sortConstraints();
        // init
        int upperbound = getLinearSpec().getConstraints().size();

        int delta = 1; // control variable of the growth rate of the search space

        int beginning = 0; // lower bound of the search space

        // upper bound of the search space: 2* because the algorithm calculates in the first round the search space for
        // the first try, which should be the whole list
        int end = 2 * upperbound;

        ResultType solverResult = ResultType.INFEASIBLE; // Result type of the solving attempt
        int counter = 0;

        // The Pivot selector only returns a list of active constraints. SO the size of the constraints list chagens
        // while the algorithm runs.
        // stop if we reach with the lower bound the last element in the list
        rememberVariableValues();
        while (beginning < upperbound) {
            // if result of the previous iteration is optimal set
            // the lower bound of the search space (b) to the current upper bound and
            // increase the upper bound by delta. Results in a new search space of size
            // delta
            if (solverResult == ResultType.OPTIMAL) {
                if (isDebug()) System.err.println("binary search: result optimal.");
                rememberVariableValues();
                beginning = end;
                end = delta + end > upperbound ? upperbound : delta + end;
                delta = delta << 1;
                // If the result of the previous iteration is not optimal two cases can happen.
                // Either the search space is of size 1 or it is greater.
            } else {
                restoreVariableValues();
                // If the search space is of size 1 we have identified the conflicting constraints.
                // We disable this constraint and move the search space to its position. We can
                // do this because it is disabled now anyway. We set the new upper bound delta
                // positions further to have a search space of delta.
                if (end == beginning + 1) {
                    disable(end - 1);
                    beginning = end;
                    delta = 1;
                    end = delta + beginning > upperbound ? upperbound : delta + beginning;
                    delta = delta << 1;
                    // if the search space is not of size one, we have to decrease the size of the
                    // current search space to check in the next iteration whether we have now a
                    // solvable subset.
                } else {
                    end = beginning + ((end - beginning) >>> 1);
                    delta = 1;
                }
            }

            // Now the new search space is defined and we can try to solve the problem within its
            // boundaries.
            // We enable all constraints within the boundaries of the search space. Since all constraints
            // below b do not conflict and they are already activated, we do not need to activate the constraints
            // below b again and can ignore this region.
            enable(beginning, end);

            // All constraints above t are not considered in the next attempt to solve. Hence, they are
            // deactivated.
            disable(end, upperbound);
            solverResult = getLinearSolver().solve();
            if (isDebug()) System.err.println("binary search: " + solverResult);
        }
        if (isDebug()) System.out.println(getLinearSpec());
        return getLinearSolver().solve();
    }

    protected void enable(int from, int to) {
        setTo(true, from, to);
    }

    protected void disable(int pos) {
        setTo(false, pos, pos + 1);
    }

    protected void disable(int from, int to) {
        setTo(false, from, to);
    }

    private void setTo(boolean setTo, int from, int to) {
        if (to > getLinearSpec().getConstraints().size())
            throw new IllegalArgumentException("To is " + to + " is greater than number of constraints (" + getLinearSpec().getConstraints().size() + ")");
        if (isDebug()) System.err.println(setTo + "able from " + from + " to " + to);

        for (int i = from; i < to; i++)
            getLinearSpec().getConstraints().get(i).setEnabled(setTo);
    }
}
