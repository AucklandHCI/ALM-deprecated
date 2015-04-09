package linsolve;

import linsolve.pivots.PivotSummandSelector;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * The BinarySearchStrategy is one possible strategy to handle conflicts in a LinearSpec
 * for which a solution has to be found. The strategy starts by trying to solve one the
 * problem for all given constraints. If the problem cannot be solved, i.e. the error of
 * some of the constraints does not converge to 0.0, it divides the sorted list of constraints
 * in two parts. One with constraints with higher penalties (l1) and
 * one with smaller penalties (l2). Than it tries to find a solution for the list with the
 * higher penalties. If a solution can be found for l1 the algorithm starts from the l1+1 element
 * and tries to solve. If possible it the size of the new list grows exponentially. If it is not solvable
 * the size shrinks.
 *
 * @author Johannes M�ller (jmue933@aucklanduni.co.nz)
 */
//@Deprecated
public class BinarySearchStrategy extends RelaxationSolverAdapter {

    int counter = 0;
    long t_begin, t_end = 0;
    boolean reset = false;

    public BinarySearchStrategy(PivotSummandSelector selector, boolean reset) {
        super(selector);
        this.reset = reset;
    }

    /**
     * Constructor. Requires a reference to a PivotSummandSelector
     *
     * @param selector
     */
    public BinarySearchStrategy(PivotSummandSelector selector) {
        super(selector);
    }

    public BinarySearchStrategy(PivotSummandSelector selector, double w) {
        super(selector, w);
    }

    /**
     * Returns the current linearSpec
     */
    public LinearSpec getLinearSpec() {
        return linearSpec;
    }

    /**
     * Sets the linearSpec
     */
    public void setLinearSpec(LinearSpec linearSpec) {
        this.linearSpec = linearSpec;
    }

    /**
     * Sorts Constraints of linearSpec
     */
    protected void sortConstraintsByDescendingPenalty(List<Constraint> constraints) {
        Collections.sort(constraints, new Comparator<Constraint>() {

            @Override
            public int compare(Constraint o1, Constraint o2) {
                if (o1.getPenalty() > o2.getPenalty())
                    return -1;
                if (o1.getPenalty() < o2.getPenalty())
                    return 1;
                return 0;
            }
        });
    }

    /**
     * Solves the linear problem according to a sort of binary algorithm. It first
     * tries to solve the whole system. If that is not possible it divides the whole
     * system in two equal parts, deactivates the constraints of the upper part and tries
     * to solve the system. If the system is solvable, it saves this index, b, and
     * starts to add half of the remaining list to the set of active constraints.
     * If it is not solvable it divides the list again and tries to solve then.
     * if the algorithm reaches through separating the position 1 + b it is clear that
     * this constraint disturbes the editor constraints. It's reference is saved in a list
     * and exclued from the problem. b is set to b + 1 and the algorithm increases the size
     * of the set of disabled constraints exponentially (1, 2, 4 ...) up to the upper bound
     * of the list.
     */
    @Override
    public ResultType solve() {
        t_begin = System.currentTimeMillis();
        int maxIterations = 1000;

        if (reset) {
            resetVariableValues();
        } else {
            initVariableValues();
        }
        sortConstraintsByDescendingPenalty(linearSpec.getConstraints());
        constraints = pivotSummandSelector.init(linearSpec, linearSpec.getConstraints().size());

        // init
        int upperbound = linearSpec.getConstraints().size();
        // control variable of the growth rate of the search space
        int delta = 1;
        // lower bound of the search space
        int beginning = 0;
        // upper bound of the search space
        int end = 2 * upperbound;// 2* because the algorithm calculates in the first round the search space for the first try, which should be the whole list
        // Result type of the solving attempt
        ResultType solverResult = ResultType.INFEASIBLE;
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
                    disable(end - 1, linearSpec.getConstraints());
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
            enable(beginning, end, linearSpec.getConstraints());
            // All constraints above t are not considered in the next attempt to solve. Hence, they are
            // deactivated.
            //disable(end, upperbound, linearSpec.getConstraints());
            solverResult = trySolve(end, linearSpec, maxIterations);
            counter++;
        }
        t_end = System.currentTimeMillis();
        return trySolve(end, linearSpec, maxIterations);
    }

    protected void enable(int from, int to, List<Constraint> constraints) {
        setTo(true, from, to, constraints);
    }

    protected void disable(int pos, List<Constraint> constraints) {
        disable(pos, pos + 1, constraints);
    }

    protected void disable(int from, int to, List<Constraint> constraints) {
        setTo(false, from, to, constraints);
    }

    private void setTo(boolean setTo, int from, int to, List<Constraint> constraints) {
        if (to > constraints.size())
            throw new IllegalArgumentException("To is " + to + " is greater than number of constraints (" + constraints.size() + ")");
        if (debug) System.err.println(setTo + "able from " + from + " to " + to);

        for (int i = from; i < to; i++) {
            constraints.get(i).setEnabled(setTo);
        }
    }

    protected ResultType trySolve(int upperbound, LinearSpec linearSpec, int maxIterations) {
        constraints = pivotSummandSelector.init(linearSpec, upperbound);
        // calculate result for the given linearSpec

        ResultType applyRelaxationMethod = applyRelaxationMethod(maxIterations);
        if (debug)
            System.err.println("(binarysearch) nr iterations: " + iteration);
        return applyRelaxationMethod;
    }

    @Override
    public long getLastSolvingTime() {
        return t_end - t_begin;
    }
}
