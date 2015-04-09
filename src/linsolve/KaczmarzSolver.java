package linsolve;

public class KaczmarzSolver extends AbstractLinearSolver {

    // TODO defaults should be moved to AbstractLinearSolver
    public static final int GUI_ITERATION = 500;
    public static final double DEFAULT_LAMBDA = 1.0d;
    public static final double DEFAULT_TOLERANCE = 0.01;//1.0d;

    private int iteration = GUI_ITERATION;
    private double lambda = DEFAULT_LAMBDA;
    private double tolerance = DEFAULT_TOLERANCE;

    @Override
    protected ResultType doSolve() {
        lambda = DEFAULT_LAMBDA;
        initVariableValues();

        double maxError = Double.MAX_VALUE;
        for (int i = 0; i < iteration && maxError > tolerance; i++) {
            doIteration();
            maxError = this.getLinearSpec().computeCurrentMaxError();
        }

        if (maxError <= tolerance)
            return ResultType.OPTIMAL;
        return ResultType.SUBOPTIMAL;
    }

    protected void doIteration() {
        for (Constraint c : this.getLinearSpec().getConstraints()) {
            if (c.isEnabled() && (c.getOp() == OperatorType.EQ || !c.isSatisfied())) {
                projectConstraint(c, lambda);
            }
        }
    }

    /**
     * Constraint c has to have at least one summand with coefficient not zero
     *
     * @param c
     * @param lambda
     */
    protected void projectConstraint(Constraint c, double lambda) {
        double b = c.getRightSide();
        Summand[] A = c.getLeftSide();

        // calculate projection parameter (b_i - A_i*x)/|A_i|^2
        double p = (b - scalarProduct(A)) / euclidianNorm(A);

        double x = 0.0d;
        for (Summand s : A) {
            x = s.getVar().getValue();
            // Actual projection formula
            x = x + lambda * p * s.getCoeff();
            s.getVar().setValue(x);
        }
    }

    protected double scalarProduct(Summand[] summands) {
        double ret = 0.0d;
        for (Summand s : summands)
            ret += s.getCoeff() * s.getVar().getValue();
        return ret;
    }

    protected double euclidianNorm(Summand[] summands) {
        double ret = 0.0d;
        for (Summand s : summands)
            ret += s.getCoeff() * s.getCoeff();
        return ret;
    }
}
