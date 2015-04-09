package linsolve;

import linsolve.pivots.PivotSummandSelector;

import java.util.Collections;
import java.util.List;

/**
 * @author njam031
 *         Relaxation Solver Adapter implements all common steps of relaxation solver.
 */
public class RelaxationSolverAdapter extends LinearSolverAdapter {
    final boolean debug = false;
    final boolean debugSolution = false;
    final boolean debugError = false;
    final boolean debugmaxError = false;
    protected LinearSpec linearSpec;
    protected double w = 0.2;
    protected int iterations = -1;
    protected double maxError = 0.0d;
    protected int requiredNoIterations = -1;


    public int getRequiredNoIterations() {
        return requiredNoIterations;
    }

    public void setRequiredNoIterations(int requiredNoIterations) {
        this.requiredNoIterations = requiredNoIterations;
    }

    public void setNumberOfIterations(int iterations) {
        this.iterations = iterations;
    }

    public void setW(double w) {
        this.w = w;
    }

    public double getMaxError() {
        return maxError;
    }

    protected PivotSummandSelector pivotSummandSelector;
    int iteration;

    public PivotSummandSelector getPivotSummandSelector() {
        return pivotSummandSelector;
    }

    double lastRelaxationStepTime = Double.NaN;
    static double lastRelaxationStepMaxError = Double.NaN;

    /**
     * The constraints that are used during the solving. They might already be
     * presolved, as compared to linearSpec.constraints
     */
    protected List<Constraint> constraints;

    public List<Constraint> getConstraints() {
        return constraints;
    }

    public void setConstraints(List<Constraint> constraints) {
        this.constraints = constraints;
    }

    public RelaxationSolverAdapter(PivotSummandSelector selector) {
        super();
        pivotSummandSelector = selector;
    }

    public RelaxationSolverAdapter(PivotSummandSelector selector, double w) {
        this(selector);
        this.w = w;
    }

    // TODO this becomes doSolve in GaussSeidelSolver
    public ResultType applyRelaxationMethod(int maxIterations) {
        // to set the number of iterations in the testrunner
        if (iterations != -1) {
            maxIterations = iterations;
        }

        if (debug) {
            //Debug.err.println("\n" + linearSpec.toString());
            System.err.println("\n" + linearSpec.toString());
            try {
                Thread.sleep(50);
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            int i = 1;
            for (Constraint c : constraints) {
                System.out.println(i + ".  " + c);
                i++;
            }
        }

        if (debugSolution)
            System.err.println(linearSpec.getCurrentSolution() + "\n");

        // system iteration loop

        // TODO this is the heart of the method. Copy this into the new doSolve()
        for (iteration = 0; iteration < maxIterations; iteration++) {
            //System.err.println("nr iterations: "+ iteration);
            // perform one system iteration
            doRelaxationStep();

            // stop if max error over all constraints is close to 0
            maxError = computeMaxError();
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

    // TODO copy as is into GaussSeidelSolver
    protected void doRelaxationStep() {
        double start = System.nanoTime();

        for (Constraint constraint : constraints) {
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

            // TODO remove this line
            //System.out.println("Summand" +chosenSummand + "Constraint"+constraint);
            double w = determineW(chosenSummand.coeff, constraint.sumOfAllAbsoluteCoefficients());
            // compute the new value for x_i
            chosenSummand.var.setValue(w
                    * constraint.newVarValue(chosenSummand) + (1 - w)
                    * chosenSummand.var.getValue());

        }


        double end = System.nanoTime();
        lastRelaxationStepTime = end - start;
    }

    // TODO move method to AbstractLinearSolver
    // TODO use getter and setter to access object attributes
    protected void resetVariableValues() {
        for (Variable v : linearSpec.variables) {
            v.setValue(0.0d);
        }
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

    // TODO check whether the impl in LinearSpec can be used instead
    protected double computeMaxError() {
        double maxError = 0;
        for (Constraint constraint : constraints) {
            if (!constraint.isEnabled())
                continue;
            double error = constraint.error();
            maxError = Math.max(maxError, error);
            if (debugError)
                System.err.println(Math.round(error) + "\t");
        }
        if (debugmaxError)
            System.out.println(+iteration + "\t" + maxError);
        return maxError;
    }

    /**
     * Initialize all variables with 0 if they do not contain a previous value
     */
    public void initVariableValues() {
        for (Variable v : linearSpec.variables) {
            if (Double.isNaN(v.getValue())) {
                v.setValue(0.0);
            }
        }
    }

    public void sortConstraintsByDescendingPenalty() {
        Collections.sort(constraints, new ConstraintComparatorByPenalty());
    }

    // Already in AbstractConflictResolutionStrategy
    // Remembering the old values.
    double[] rememberedValues = null;

    void rememberVariableValues() {
        rememberedValues = new double[linearSpec.variables.size()];
        int i = 0;
        for (Variable v : linearSpec.variables) {
            rememberedValues[i] = v.value;
            i++;
        }
    }

    void restoreVariableValues() {
        int i = 0;
        for (Variable v : linearSpec.variables) {
            v.value = rememberedValues[i];
            i++;
        }
    }
}