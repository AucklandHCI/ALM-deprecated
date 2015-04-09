package linsolve.softconstraints;

import linsolve.AbstractLinearSolver;
import linsolve.Constraint;
import linsolve.ResultType;

public class AddingSoftSolver extends AbstractSoftSolver {

    public AddingSoftSolver(AbstractLinearSolver solver) {
        super(solver);
    }

    protected ResultType doSolve() {
        int noConstraints = getLinearSpec().getConstraints().size();
        ResultType result = ResultType.SUBOPTIMAL;
        disableConstraints();
        sortConstraints();

        for (int i = 0; i < noConstraints; i++) {
            //if(isDebug()) System.out.println("Conflict resolution adding iteration " + i);
            rememberVariableValues();
            getLinearSpec().getConstraints().get(i).setEnabled(true);
            result = getLinearSolver().solve();

            if (result != ResultType.OPTIMAL) {
                restoreVariableValues();
                Constraint c = getLinearSpec().getConstraints().get(i);
                c.setEnabled(false);
            }
        }
        if (isDebug()) System.out.println(getLinearSpec());
        return result;
    }

    protected void disableConstraints() {
        for (Constraint constraint : getLinearSpec().getConstraints()) {
            constraint.setEnabled(false);
        }
    }
}
