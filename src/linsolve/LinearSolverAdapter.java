package linsolve;

import java.math.BigInteger;

//@Deprecated
public abstract class LinearSolverAdapter implements LinearSolver {

	public BigInteger getIota(){return new BigInteger("-1");};
	
	public void add(Constraint c) {
	}
	
	public void update(Constraint c) {
	}

	public void remove(Constraint c) {
	}

	public void add(Variable v) {
	}

	public void remove(Variable v) {
	}

	public LinearSpec getLinearSpec() {
		return null;
	}

	public void presolve() {
	}

	public void removePresolved() {
	}

	public void setLinearSpec(LinearSpec linearSpec) {
	}

	public ResultType solve() {
		return null;
	}

	long lastSolvingTime;

	public long getLastSolvingTime() {
		return lastSolvingTime;
	}
	
	ResultType lastSolvingResult = ResultType.ERROR;
	
	public ResultType getLastSolvingResult() {
		return lastSolvingResult;
	}
	

}
