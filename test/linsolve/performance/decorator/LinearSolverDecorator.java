package linsolve.performance.decorator;

import java.math.BigInteger;

import linsolve.Constraint;
import linsolve.LinearSolver;
import linsolve.LinearSpec;
import linsolve.ResultType;
import linsolve.Variable;

public class LinearSolverDecorator implements LinearSolver {

	protected LinearSolver body;
	
	public LinearSolver setBody(LinearSolver body){
		this.body = body;
		return this;
	}
	
	@Override
	public LinearSpec getLinearSpec() {
		return body.getLinearSpec();
	}

	@Override
	public void setLinearSpec(LinearSpec linearSpec) {
		body.setLinearSpec(linearSpec);
	}

	@Override
	public void presolve() {
		body.presolve();
	}

	@Override
	public void removePresolved() {
		body.removePresolved();
	}

	@Override
	public void add(Constraint c) {
		body.add(c);
	}

	@Override
	public void update(Constraint c) {
		body.update(c);
	}

	@Override
	public void remove(Constraint c) {
		body.remove(c);
	}

	@Override
	public void add(Variable v) {
		body.add(v);
	}

	@Override
	public void remove(Variable v) {
		body.remove(v);
	}

	@Override
	public ResultType solve() {
		return body.solve();
	}

	@Override
	public long getLastSolvingTime() {
		return body.getLastSolvingTime();
	}

	@Override
	public ResultType getLastSolvingResult() {
		return body.getLastSolvingResult();
	}

}
