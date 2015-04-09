package linsolve;

import java.util.ArrayList;
import java.util.Collections;


public class KaczmarzLeastSquares extends AbstractLinearSolver {
    public static final int GUI_MAXITERATION = 1000;
    public static final double DEFAULT_LAMBDA = 1.9d;
    public static final double tolerance = 0.01;
    public static final double COOLING_FACTOR = 0.9;
    private double lambda = DEFAULT_LAMBDA;

    double maxError = Double.MAX_VALUE;

    @Override
    protected ResultType doSolve() {
        lambda = DEFAULT_LAMBDA;
        initVariableValues();

        for (int i = 0; i < GUI_MAXITERATION && maxError > tolerance; i++) {
            doIteration();
        }
        if (maxError <= tolerance)
            return ResultType.OPTIMAL;
        return ResultType.SUBOPTIMAL;
    }

    protected void doIteration() {
        lambda = lambda * COOLING_FACTOR;
        double lambda1 = 1.0;
        for (Constraint c : this.getLinearSpec().getConstraints()) {

            // If constraint is enabled and operator is equalty or constraint is
            // not satisfied then apply projection and use normal kaczmarz
            if (c.isEnabled()
                    && (c.getOp() == OperatorType.EQ || !c.isSatisfied())) {
                projectConstraint(c, c.getPenalty() * lambda1);

            }
            // Otherwise use Kaczmarz with cooling
            else {
                if (!c.isEnabled()) {
                    projectConstraint(c, c.getPenalty() * lambda);
                }
            }
        }
    }

    /**
     * Constraint c has to have at least one summand with coefficient not zero
     *
     * @param c
     * @param lambda
     */
    protected double projectConstraint(Constraint c, double lambda) {
        // if Kaczmarz is used with COOLING_FACTOR, then constraints are
        // weighted according to their penalty.
        // Penalty is then expected to be between 1.0 (highest priority
        // constraint) and 0.0

        // calculate projection parameter (b_i - A_i*x)/|A_i|^2
        double b = c.getRightSide();
        Summand[] A = c.getLeftSide();
        double p = (b - scalarProduct(A)) / euclidianNorm(A);

        double x = 0.0d;
        double y = 0.0d;
        ArrayList<Double> residual = new ArrayList<Double>();
        for (Summand s : A) {
            x = s.getVar().getValue();
            y = x;

            // Actual projection formula
            x = x + lambda * p * s.getCoeff();
            s.getVar().setValue(x);

            // difference between previous and current variables values
            residual.add(Math.abs(x - y));
        }
        // collecting maximum between previous and current variables values
        return Collections.max(residual);
    }

    protected double scalarProduct(Summand[] summands) {
        double ret = 0.0d;
        for (Summand s : summands) {
            ret += s.getCoeff() * s.getVar().getValue();
        }
        return ret;
    }

    protected double euclidianNorm(Summand[] summands) {
        double ret = 0.0d;
        for (Summand s : summands) {
            ret += s.getCoeff() * s.getCoeff();
        }
        return ret;
    }
}
