package linsolve;

import linsolve.pivots.PivotSummandSelector;

/**
 * This is one of the conflict resolution algorithm which starts with all  constraints
 * It tries to solve the specification, if it is conflict free then it returns the solution otherwise
 * It removes suspicious constraint with the lowest priority.
 *
 * @author njam031
 */
// TODO this class does not have an implementation for the new architecture yet
//@Deprecated
public class LinearRelaxationRemovingSuspicious extends RelaxationSolverAdapter {
    final boolean debugdisabled = false;

    public LinearRelaxationRemovingSuspicious(PivotSummandSelector selector) {
        super(selector);
    }

    public LinearRelaxationRemovingSuspicious(PivotSummandSelector selector,
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

    public ResultType solve() {
        long start = System.nanoTime();
        initVariableValues();
        // calculate maximum number of system iterations
        int maxIterations = 1000;
        // enable all constraints to start with
        for (Constraint constraint : linearSpec.constraints) {
            constraint.setEnabled(true);
        }

        // sort constraints by penalty
        constraints = linearSpec.constraints;
        sortConstraintsByDescendingPenalty();

        // select pivot elements
        //
        // We have to do this in every iteration because a constraint might have
        // been removed in the previous iteration as it was conflicting.
        // As the removed constraint may have been the pivot constraint of a
        // variable,
        // the pivot selection algorithm needs to make sure that this variable
        // gets a
        // new pivot constraint. This may be optimized in the future by only
        // finding a
        // new pivot constraint once a constraint has been removed below.
        //
        // No resorting by penalties afterwards is required
        // because this preserves the penalty order of the constraints.
        // Any duplicate constraints that are generated are inserted
        // after the original constraints.
        constraints = pivotSummandSelector.init(linearSpec);
        ResultType solverResult = ResultType.INFEASIBLE;

        // loop for removing suspicious constraints one by one
        for (int i = 0; i < constraints.size(); ++i) {
            // calculate maximum number of system iterations
            //maxIterations = 100*i;
            // solve this system, using a maximum of maxIterations system
            // iterations
            solverResult = applyRelaxationMethod(maxIterations);
            sortConstraintsByDescendingPenalty();

            // if the system is already solved, i.e. it has no conflicts, then
            // stop
            if (solverResult == ResultType.OPTIMAL)
                break;

            // remove the non-optimal constraint with the lowest penalty
            // We only need to iterate from nrOfConstraints - 1 - i,
            // i.e. for each removing-loop the next constraint from the bottom
            // is either disabled or found as non-conflicting.
            for (int k = constraints.size() - 1 - i; k >= 0; k--) {
                Constraint c = constraints.get(k);

                // if this constraint is still enabled and has an error,
                // then it is conflicting
                if (c.isEnabled() && !Constraint.equalZero(c.error())) {
                    c.setEnabled(false);
                    constraints = pivotSummandSelector.removeConstraint(constraints, c);
                    break;
                }
            }
        }

        long end = System.nanoTime();
        lastSolvingTime = end - start;
        if (debug)
            System.out.println("Time in nano seconds: " + lastSolvingTime);
        return solverResult;
    }

}
