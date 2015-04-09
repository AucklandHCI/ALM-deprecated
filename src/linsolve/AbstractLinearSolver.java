/**
 * Created at 03.10.2012
 * File: AbstractLinearSolver.java
 * For project: aim-java
 */
package linsolve;

/**
 * An abstract implementation of a LinearSolver. All solvers extends this abstract class.
 *
 * @author jo
 */
public abstract class AbstractLinearSolver implements LinearSolver {

    private LinearSpec linearSpec;
    private ResultType lastSolvingResult = ResultType.INFEASIBLE;
    private long start = 0l, end = 0l;

    /**
     * Indicates whether debug output should be printed during solving.
     */
    private boolean debug = false;

    /**
     * Enable/Disable debug messages.
     */
    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    /**
     * Tests if debug is enabled.
     */
    public boolean isDebug() {
        return debug;
    }

    /**
     * @link linsolve.LinearSolver#getLinearSpec()
     */
    @Override
    public LinearSpec getLinearSpec() {
        return linearSpec;
    }

    /**
     * @link linsolve.LinearSolver#setLinearSpec()
     */
    @Override
    public void setLinearSpec(LinearSpec linearSpec) {
        this.linearSpec = linearSpec;
    }

    /**
     * @link linsolve.LinearSolver#presolve()
     */
    @Override
    public void presolve() {

    }

    /**
     * Initialize all variables with 0 if they do not contain a previous value
     */
    public void initVariableValues() {
        for (Variable v : this.getLinearSpec().getVariables()) {
            if (Double.isNaN(v.getValue())) {
                v.setValue(0.0);
            }
        }
    }

    /**
     * sort constraints in linear spec descending according to their penalties.
     */
    protected void sortConstraints() {
        getLinearSpec().sortConstraintsByDescendingPenalty();
    }

    /**
     * @link linsolve.LinearSolver#removePresolved()
     */
    @Override
    public void removePresolved() {

    }

    /**
     * @link linsolve.LinearSolver#add(Constraint c)
     */
    @Override
    public void add(Constraint c) {
    }

    /**
     * @link linsolve.LinearSolver#update(Constraint c)
     */
    @Override
    public void update(Constraint c) {
    }

    /**
     * @link linsolve.LinearSolver#remove(Constraint c)
     */
    @Override
    public void remove(Constraint c) {
        getLinearSpec().getConstraints().remove(c);
    }

    /**
     * @link linsolve.LinearSolver#add(Variable v)
     */
    @Override
    public void add(Variable v) {
    }

    /**
     * @link linsolve.LinearSolver#remove(Variable v)
     */
    @Override
    public void remove(Variable v) {
    }

    /**
     * Solves the linear system which is presented in the linear spec which is used by the solver.
     */
    @Override
    public final ResultType solve() {
        //System.out.println(getLinearSpec().toString());
        if (commons.Constants.generateTest) {
            System.out.println(getLinearSpec().toTestMethod());
        }
        start = System.currentTimeMillis();
        lastSolvingResult = doSolve();
        if (debug) {
            System.out.println(getLinearSpec().toString());
            System.out.println(getLinearSpec().getCurrentSolution());
            System.out.println("max error: " + getLinearSpec().computeCurrentMaxError());
        }
        end = System.currentTimeMillis();

        return lastSolvingResult;
    }

    /**
     * @link linsolve.LinearSolver#doSolve()
     */
    protected abstract ResultType doSolve();

    /**
     * @link linsolve.LinearSolver#getLastSolvingTime()
     */
    @Override
    public long getLastSolvingTime() {
        return end - start;
    }

    /**
     * @link linsolve.LinearSolver#getLastSolvingResult()
     */
    @Override
    public ResultType getLastSolvingResult() {
        return lastSolvingResult;
    }

    /**
     * resets all variables, used in the linear spec, to 0.
     */
    protected void resetVariableValues() {
        for (Variable v : getLinearSpec().variables) {
            v.setValue(0.0d);
        }
    }

}
