package linsolve.softconstraints;

import linsolve.AbstractLinearSolver;
import linsolve.LinearSpec;
import linsolve.ResultType;
import linsolve.Variable;

/**
 * Abstract base class for conflict resolution strategies. The actual solver is injected
 * via the provided constructor.
 *
 * @author jmue933
 */
public abstract class AbstractSoftSolver extends AbstractLinearSolver {

    private AbstractLinearSolver solver;

    private double[] rememberedValues = null;

    public AbstractSoftSolver(AbstractLinearSolver solver) {
        this.solver = solver;
    }

    /* (non-Javadoc)
     * @see linsolve.AbstractLinearSolver#setLinearSpec(linsolve.LinearSpec)
     *
     * This class overrides the usage of the private linearSpec in the superclass.
     * The linearSpec is always obtained from the solver now.
     */
    @Override
    public void setLinearSpec(LinearSpec linearSpec) {
        solver.setLinearSpec(linearSpec);
    }

    @Override
    public LinearSpec getLinearSpec() {
        return solver.getLinearSpec();
    }

    @Override
    protected ResultType doSolve() {
        // TODO Auto-generated method stub
        return null;
    }

    protected AbstractLinearSolver getLinearSolver() {
        return solver;
    }

    protected void rememberVariableValues() {
        rememberedValues = new double[getLinearSpec().getVariables().size()];
        int i = 0;
        for (Variable v : getLinearSpec().getVariables()) {
            rememberedValues[i] = v.getValue();
            i++;
        }
    }

    protected void restoreVariableValues() {
        int i = 0;
        for (Variable v : getLinearSpec().getVariables()) {
            v.setValue(rememberedValues[i]);
            i++;
        }
    }
}
