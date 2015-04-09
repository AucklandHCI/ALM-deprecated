package linsolve;

import org.apache.commons.math3.linear.*;

/**
 * This is one of the direct methods that we implemented for the comparison purpose with iterative solvers.
 *
 * @author jmue933, njam031
 */
public class QRSolver extends AbstractLinearSolver {

    private RealMatrix coeffMatrix;
    private RealVector constants;
    private DecompositionSolver solver;

    private long start, end;

    private ResultType lastResult = ResultType.SUBOPTIMAL;

    @Override
    public ResultType doSolve() {
        start = System.currentTimeMillis();
        createCoefficientMatrix();
        solver = new QRDecomposition(coeffMatrix).getSolver();

        createRHS();
        RealVector solution = solver.solve(constants);
        setVariableValues(solution);
        end = System.currentTimeMillis();

        lastResult = ResultType.OPTIMAL;
        return lastResult;
    }


    private void setVariableValues(RealVector solution) {
        for (int i = 0; i < getLinearSpec().getVariables().size(); i++) {
            getLinearSpec().getVariables().get(i).setValue(solution.getEntry(i));
        }
    }

    private void createRHS() {
        int m = getLinearSpec().getConstraints().size();
        double[] rhs = new double[m];

        for (Constraint c : getLinearSpec().getConstraints()) {
            rhs[c.getIndex() - 1] = c.getRightSide();// * Math.sqrt(c.getPenalty());
        }
        constants = new ArrayRealVector(rhs, true);
    }


    private void createCoefficientMatrix() {
        int m = getLinearSpec().getConstraints().size();
        int n = getLinearSpec().getVariables().size(); // The list of var contains one var which does not belong to any non-zero coefficient. I removed it to have a non-singular matrix

        double[][] coeff = new double[m][n];

        for (int i = 0; i < n; i++) {
            for (Constraint c : getLinearSpec().getConstraints()) {
                coeff[c.getIndex() - 1][i] = getCoeff(getLinearSpec().getVariables().get(i), c);//* Math.sqrt(c.getPenalty());
            }
        }

        coeffMatrix = new Array2DRowRealMatrix(coeff, true);
    }


    private double getCoeff(Variable v, Constraint c) {
        for (Summand s : c.getLeftSide()) {
            if (s.getVar() == v) {
                return s.getCoeff();
            }
        }
        return 0.0d;
    }

    @Override
    public long getLastSolvingTime() {
        return end - start;
    }

    @Override
    public ResultType getLastSolvingResult() {
        return lastResult;
    }
}
