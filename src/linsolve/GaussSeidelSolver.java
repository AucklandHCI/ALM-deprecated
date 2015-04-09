package linsolve;

import linsolve.pivots.PivotSummandSelector;

import java.util.Collections;

public class GaussSeidelSolver extends AbstractLinearSolver {

    final boolean debug = false;
    final boolean debugSolution = false;
    double w = 0.2;
    PivotSummandSelector pivotSummandSelector;
    double lastRelaxationStepTime = Double.NaN;
    static double lastRelaxationStepMaxError = Double.NaN;
    ResultType lastSolvingResult = ResultType.ERROR;
    int requiredNoIterations = -1;
    int maxIterations;

    public GaussSeidelSolver(PivotSummandSelector selector, int maxIterations) {
        super();
        pivotSummandSelector = selector;
        setMaxIterations(maxIterations);
    }

    public int getMaxIterations() {
        return maxIterations;
    }

    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public PivotSummandSelector getPivotSummandSelector() {
        return pivotSummandSelector;
    }

    public void setPivotSummandSelector(PivotSummandSelector pivotSummandSelector) {
        this.pivotSummandSelector = pivotSummandSelector;
    }

    public int getRequiredNoIterations() {
        return requiredNoIterations;
    }

    public ResultType doSolve() {
        int iteration;
        double maxError;

        // these should be initialized somewhere. is it a good place?
        pivotSummandSelector.init(getLinearSpec());
        initVariableValues();

        if (debug) {
            System.err.println("\n" + getLinearSpec().toString());
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            int i = 1;
            for (Constraint c : getLinearSpec().getConstraints()) {
                System.out.println(i + ".  " + c);
                i++;
            }
        }

        if (debugSolution)
            System.err.println(getLinearSpec().getCurrentSolution() + "\n");

        // system iteration loop
        for (iteration = 0; iteration < maxIterations; iteration++) {
            // perform one system iteration
            doRelaxationStep();

            // stop if max error over all constraints is close to 0
            maxError = getLinearSpec().computeCurrentMaxError();
            lastRelaxationStepMaxError = maxError;

            if (Constraint.equalZero(maxError)) {
                lastSolvingResult = ResultType.OPTIMAL;
                requiredNoIterations = iteration;
                return ResultType.OPTIMAL;
            }
        }
        lastSolvingResult = ResultType.INFEASIBLE;


        return ResultType.INFEASIBLE;
    }


    protected void doRelaxationStep() {
        double start = System.nanoTime();

        for (Constraint constraint : getLinearSpec().getConstraints()) {
            if (!constraint.isEnabled())
                continue;
            // check if constraint is an inequality
            // if yes, ignore it if it is satisfied
            // otherwise treat it as equality
            if (constraint.getOp() != OperatorType.EQ
                    && constraint.isSatisfied())
                continue;

            Summand chosenSummand = pivotSummandSelector
                    .selectPivotSummand(constraint);

            double w = determineW(chosenSummand.coeff, constraint.sumOfAllAbsoluteCoefficients());
            // compute the new value for x_i
            chosenSummand.var.setValue(w
                    * constraint.newVarValue(chosenSummand) + (1 - w)
                    * chosenSummand.var.getValue());
        }

        double end = System.nanoTime();
        lastRelaxationStepTime = end - start;
    }

    private double determineW(double chosenSummand, double absCoeffSum) {
        double w;
        if (Math.abs(chosenSummand) > (absCoeffSum - Math.abs(chosenSummand))) {
            w = this.w;
        } else {
            w = calculatesw(chosenSummand, absCoeffSum);
        }
        return w;
    }

    private double calculatesw(double chosenSummand, double absCoeffSum) {
        double w;

        w = Math.abs(chosenSummand) / (absCoeffSum - Math.abs(chosenSummand)) - 0.1;
        return w;

    }

    /**
     * Initialize all variables with 0 if they do not contain a previous value
     */
    public void initVariableValues() {
        for (Variable v : getLinearSpec().getVariables()) {
            if (Double.isNaN(v.getValue())) {
                v.setValue(0.0);
            }
        }
    }

    public void sortConstraintsByDescendingPenalty() {
        Collections.sort(getLinearSpec().constraints, new ConstraintComparatorByPenalty());
    }

    // Already in AbstractConflictResolutionStrategy
    // Remembering the old values.
    double[] rememberedValues = null;

    void rememberVariableValues() {
        rememberedValues = new double[getLinearSpec().getVariables().size()];
        int i = 0;
        for (Variable v : getLinearSpec().getVariables()) {
            rememberedValues[i] = v.value;
            i++;
        }
    }

    void restoreVariableValues() {
        int i = 0;
        for (Variable v : getLinearSpec().getVariables()) {
            v.value = rememberedValues[i];
            i++;
        }
    }
}
