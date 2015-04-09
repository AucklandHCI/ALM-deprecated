package linsolve;

import linsolve.softconstraints.AbstractSoftSolver;

import java.util.ArrayList;
import java.util.List;

/**
 * This strategy enables constraints with the same penalty solve them and then enable another groups of constraints with the same penalty
 * and solve them up to so on until it finds all groups of constraints with the same penalty.
 *
 * @author njam031
 */
public class LeastSquareSoftSolver extends AbstractSoftSolver {
    public static final int GUI_ITERATION = 500;
    public static final double DEFAULT_LAMBDA = 1.9d;

    double maxError = Double.MAX_VALUE;
    double tolerance;
    List<Constraint> list = new ArrayList<Constraint>();

    public LeastSquareSoftSolver(AbstractLinearSolver solver, double tolerance) {
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
        //Here we disable all constraints
        disableConstraints();
        //Here we sort them according to penalties
        sortConstraints();
        boolean enabledconstraints = true;
        double penalty2 = 0;
        double penalty1 = 0;

        Constraint c;
        Constraint d;

        //we enable first constraint
        c = getLinearSpec().getConstraints().get(0);
        c.setEnabled(true);
        for (int i = 0; i < (noConstraints - 1); i++) {
            d = getLinearSpec().getConstraints().get(i + 1);
            penalty1 = c.getPenalty();
            penalty2 = d.getPenalty();
            //if constraints penalty are same we enable constraints
            if (penalty1 == penalty2) {
                d.setEnabled(true);
                c = d;
                enabledconstraints = true;
            } else {
                if (enabledconstraints == true) {
                    enabledconstraints = false;
                    result = getLinearSolver().solve();
                    d.setEnabled(true);
                    c = d;
                }
            }
            //{System.out.println("Nothing Enabled");}
            //System.out.println(c);
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
