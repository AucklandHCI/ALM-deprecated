package linsolve;

/**
 * An interface for linear solvers. AbstractLinearSolver implements this interface. New solvers
 * should extend AbstractLinearSolver.
 */
public interface LinearSolver {

    /**
     * returns linear spec which is used by this solver.
     */
    public LinearSpec getLinearSpec();

    /**
     * Replaces the linear spec which is used by this solver.
     */
    public void setLinearSpec(LinearSpec linearSpec);

    public void presolve();

    /**
     * Remove a cached presolved model, if existent.
     * This is automatically done each time after the model has been changed,
     * to avoid an old cached presolved model getting out of sync.
     */
    public void removePresolved();

    /**
     * Adds a constraint to this solver. Some solvers keep their own set of constraints.
     */

    public void add(Constraint c);

    /**
     * Updates a constraint in this solver. Some solvers keep their own set of constraints.
     */
    public void update(Constraint c);

    /**
     * Removes a constraint from this solver. Some solvers keep their own set of constraints.
     */
    public void remove(Constraint c);

    /**
     * Adds a variable to this solver.
     */
    public void add(Variable v);

    /**
     * Removes a variable from this solver.
     */
    public void remove(Variable v);

    /**
     * Tries to solve the linear problem.
     * If a cached simplified version of the problem exists, it is used instead.
     *
     * @return the result of the solving attempt
     */
    public ResultType solve();

    /**
     * Total time of last solving attempt in milliseconds.
     *
     * @return time in milliseconds
     */
    public long getLastSolvingTime();

    /**
     * Result of last solving attempt (e.g. OPTIMAL, INFEASIBLE, ...).
     *
     * @return result
     */
    public ResultType getLastSolvingResult();
}
