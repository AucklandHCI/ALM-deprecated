package linsolve;

import java.util.ArrayList;
import java.util.List;

import linsolve.softconstraints.AbstractSoftSolver;

/*1. We use one of our soft constraint algorithms and end up with information about which constraint is enabled and
     which is disabled. All the disabled constraints become /soft/ constraints (this should be like a marker for each
     constraint, which says whether it is hard or soft).
  2. Next we go through the list of all constraints and ensure the following: if a constraint with a certain penalty p
     was marked as soft in step 1, then all other constraints with the same penalty also need to be marked as soft.
  3. Now we solve the system of all constraints in the following manner:
    If a constraint is soft, you solve it using Kaczmarz with cooling and using its penalty as weight.
    If a constraint is hard (i.e. not soft), then you solve it using the normal Kaczmarz without cooling or weight.

Step 1 makes sure that problematic constraints are always solved with least squares, so they will compromise if need be.
Step 2 makes sure that if soft constraints of the same penalty are somehow affected by a conflict, they will share the
error equally.*/
public class LeastSquaresKacz extends AbstractSoftSolver {
    public static final int GUI_ITERATION = 1000;
    public static final double DEFAULT_LAMBDA = 1.9d;
    private static final double PREFERRED_SIZE_PENALTY = 0.5;

    double maxError = Double.MAX_VALUE;
    double tolerance;
    List<Constraint> list = new ArrayList<Constraint>();

    public LeastSquaresKacz(AbstractLinearSolver solver, double tolerance) {
        super(solver);
        setTolerance(tolerance);
    }

    public double getTolerance() {
        return tolerance;
    }

    public void setTolerance(double tolerance) {
        this.tolerance = tolerance;
    }

    protected ResultType doSolve() {
        int noConstraints = getLinearSpec().getConstraints().size();
        ResultType result = ResultType.SUBOPTIMAL;
        // Here we disable all constraints
        disableConstraints();
        // Here we sort them according to penalties
        sortConstraints();

        for (int i = 0; i < noConstraints; i++) {

            Constraint c = getLinearSpec().getConstraints().get(i);

            rememberVariableValues();

            getLinearSpec().getConstraints().get(i).setEnabled(true);
            result = getLinearSolver().solve();
            if (c.getPenalty() == PREFERRED_SIZE_PENALTY) {
                restoreVariableValues();
                c.setEnabled(false);
            }
        }
        if (isDebug())
            System.out.println(getLinearSpec());
        return result;
    }

    protected void disableConstraints() {
        for (Constraint constraint : getLinearSpec().getConstraints()) {
            constraint.setEnabled(false);
        }
    }
}
