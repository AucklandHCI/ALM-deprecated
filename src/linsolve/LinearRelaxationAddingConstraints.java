package linsolve;

import linsolve.pivots.PivotSummandSelector;

/**
 * This is one of the conflict resolution strategy in which we start with an empty set $E$ of enabled constraints.
 * We add constraints incrementally in order of descending priority so that $E$ is conflict-free,
 * until all non-conflicting constraints have been added
 * Iterating through the constraints, we add each constraint tentatively to $E$ (``enabling'' it), and try to solve the resulting specification.
 * Note that whenever a constraint is added, the pivot assignment needs to be recalculated.
 * If a solution is found, we proceed to the next constraint.
 * If no solution is found, the tentatively added constraint is removed again.
 * In that case, the previous solution is restored and we proceed to the next constraint.
 *
 * @author njam031
 */
// TODO this class is re implemented in class ConflictResolutionAddingStrategy
//@Deprecated
public class LinearRelaxationAddingConstraints extends RelaxationSolverAdapter
        implements LinearSolver {

    final boolean debug = false;
    final boolean debugdisabled = false;

    private boolean reset = false;

    long start = 0, end = 0;
    long start2 = 0, end2 = 0;

    public LinearRelaxationAddingConstraints(PivotSummandSelector selector, boolean reset) {
        super(selector);
    }

    public LinearRelaxationAddingConstraints(PivotSummandSelector selector) {
        super(selector);
    }

    public LinearRelaxationAddingConstraints(PivotSummandSelector selector,
                                             double w) {
        this(selector);
        this.w = w;
    }

    public LinearSpec getLinearSpec() {
        return linearSpec;
    }

    public void setLinearSpec(LinearSpec linearSpec) {
        this.linearSpec = linearSpec;
    }

    public void presolve() {
    }

    public void removePresolved() {
    }

    public ResultType solve() {
        if (reset)
            resetVariableValues();
        else
            initVariableValues();

        long start = System.nanoTime();

        // disable all constraints to start with
        for (Constraint constraint : linearSpec.constraints) {
            constraint.setEnabled(false);
        }

        // sort constraints by penalty
        constraints = linearSpec.constraints;
        sortConstraintsByDescendingPenalty();

        int maxIterations = 1000;
        int nrOfConstraints = linearSpec.constraints.size();
        ResultType solverResult = ResultType.INFEASIBLE;

        // loop for adding non-conflicting constraints one by one
        for (int i = 0; i < nrOfConstraints; ++i) {

            // remember current solution so that it can be restored in case
            // we add a conflicting constraint (as the solving process will
            // disturb the solution if conflicting constraints are present)
            rememberVariableValues();

            // enable disabled constraint with highest penalty
            linearSpec.constraints.get(i).setEnabled(true);

            // Use pivot selector to determine pivot element for each
            // constraint.
            // The list of constraints returned by the pivot selector may
            // contain
            // extra constraints for solving.
            constraints = pivotSummandSelector.init(linearSpec);

            // solve this system, using a maximum of maxIterations system
            // iterations
            solverResult = applyRelaxationMethod(maxIterations);
            //System.err.println("nr iterations: "+ iteration);

            // If the result is not optimal, there is probably a conflict.
            // The constraint which was just enabled (with the lowest penalty)
            // is removed again.
            if (solverResult != ResultType.OPTIMAL) {
                restoreVariableValues();
                Constraint c = linearSpec.constraints.get(i);
                c.setEnabled(false);

            }
        }
        solverResult = applyRelaxationMethod(maxIterations);
        /*System.err.println("nr iterations: "+ maxIterations);*/
        long end = System.nanoTime();
        lastSolvingTime = end - start;
        if (debug)
            System.out.println("Time in nano seconds: " + lastSolvingTime);
        return solverResult;

    }
}
