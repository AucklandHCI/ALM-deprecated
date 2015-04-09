package linsolve;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import lpsolve.LpSolveException;

// TODO use AbstractLinearSolver as super class
// TODO move method solve to doSolve()
// TODO rename to LpSolver
public class LpSolve extends AbstractLinearSolver {
	private lpsolve.LpSolve lp = null;
	private lpsolve.LpSolve lpPresolved = null;
	final static int MAX_TRIES = 10;

	/**
	 * The summands of the objective function.
	 */
	private List<Summand> objFunctionSummands = new ArrayList<Summand>();
	
	long lastSolvingTime;
	ResultType lastSolvingResult = ResultType.ERROR;

	public long getLastSolvingTime() {
		return lastSolvingTime;
	}
	
	public ResultType getLastSolvingResult() {
		return lastSolvingResult;
	}
	
	/**
	 * presolve linear problem
	 */
	public void presolve() {
		long start, end;
		start = System.nanoTime();
		try {
			if (lpPresolved == null) {
				lpPresolved = lp.copyLp();
				lpPresolved.setPresolve(lpsolve.LpSolve.PRESOLVE_ROWS
						| lpsolve.LpSolve.PRESOLVE_COLS
						| lpsolve.LpSolve.PRESOLVE_LINDEP, lpPresolved
						.getPresolveloops());
			}

			setResult(ResultType.getResultType(lpPresolved.solve()));

			if (getResult() == ResultType.OPTIMAL) {
				for (Variable v : getLinearSpec().getVariables())
					v.setValue(lpPresolved.getVarPrimalresult(lpPresolved
							.getNorigRows()
							+ v.getIndex()));
			}

		} catch (LpSolveException e) {
			System.err.println("LPSolver: Could not presolve.");
			e.printStackTrace();
		}
		end = System.nanoTime();
		lastSolvingTime = end - start;
	}

	/**
	 * Sets the result status
	 * 
	 * @param result
	 */
	private void setResult(ResultType result) {
		this.lastSolvingResult = result;
	}

	/**
	 * Get return result type of solution
	 * 
	 * @return ResultType
	 */
	private ResultType getResult() {
		return this.lastSolvingResult;
	}

	public void removePresolved() {
		if (lpPresolved == null)
			return;
		lpPresolved.deleteLp();
		lpPresolved = null;
	}

	public void setLinearSpec(LinearSpec linearSpec) {
//		this.linearSpec = linearSpec; RF
		super.setLinearSpec(linearSpec);
		try {
			// delete old lp_solve specification
			if (lp != null)
				lp.deleteLp();
				
			lp = lpsolve.LpSolve.makeLp(0, 0);
			if (lp.getLp() == 0)
				throw new RuntimeException("Couldn't construct a new model.");
			lp.setVerbose(1); // only print critical messages

			for (Variable v : linearSpec.getVariables())
				add(v);
			for (Constraint c : linearSpec.getConstraints())
				add(c);

		} catch (LpSolveException e) {
			throw new RuntimeException("Error in lp_solve", e);
		}
	}

	public ResultType doSolve() {
		try {
			if (lpPresolved != null) {
				presolve();
				return getResult();
			}

			long start, end;
			start = System.nanoTime();

			// try to solve several times because sometimes lp_solve fails
			// numerically ("NUMFAILURE")
			int tries = 0;
			lastSolvingResult = ResultType.NUMFAILURE;
			do {
				lastSolvingResult = ResultType.getResultType(lp.solve());
				lp.defaultBasis();
				tries++;
			} while (lastSolvingResult == ResultType.NUMFAILURE
					&& tries < MAX_TRIES);

			if (lastSolvingResult == ResultType.OPTIMAL) {
				double[] x = new double[getLinearSpec().getVariables().size()];
				try {
					lp.getVariables(x);
				} catch (LpSolveException e) {
					System.err.println("Error in getVariables.");
				}
				int i = 0;
				for (Variable v : getLinearSpec().getVariables())
					v.setValue(x[i++]);
			}
			end = System.nanoTime();
			lastSolvingTime = end - start;
		} catch (LpSolveException e) {
			throw new RuntimeException("Error in lp_solve", e);
		}
		return lastSolvingResult;
	}

	public void add(Constraint c) {
		Summand[] leftSide = c.getLeftSide();
		double[] coeffs = new double[leftSide.length + 2];
		int[] vindexes = new int[leftSide.length + 2];

		int numCoeffs = setupCoefficients(c, coeffs, vindexes);

		try {
			lp.addConstraintex(numCoeffs, coeffs, vindexes,
					((c.getOp() == OperatorType.EQ) ? lpsolve.LpSolve.EQ : (c
							.getOp() == OperatorType.GE) ? lpsolve.LpSolve.GE
							: lpsolve.LpSolve.LE), c.getRightSide());
		} catch (LpSolveException e) {
			throw new RuntimeException("Error in lp_solve:addConstraintex", e);
		}

		updateObjFunction();
		removePresolved();
	}

	public void remove(Constraint c) {
		for (Summand s : c.getLeftSide())
			s.remove();
		if (c.getdNegSummand() != null) {
			objFunctionSummands.remove(c.getdNegSummand());
			c.getdNegSummand().getVar().remove();
			c.getdNegSummand().remove();
		}
		if (c.getdPosSummand() != null) {
			objFunctionSummands.remove(c.getdPosSummand());
			c.getdPosSummand().getVar().remove();
			c.getdPosSummand().remove();
		}
		try {
			lp.delConstraint(c.getIndex());
		} catch (LpSolveException e) {
			throw new RuntimeException("Error in lp_solve:delConstraint", e);
		}
		getLinearSpec().getConstraints().remove(c);
		removePresolved();
	}

	public void update(Constraint c) {
		updateLeftSide(c);
		updateOp(c);
		updateRight(c);
		updateObjFunction();
		removePresolved();
	}

	private void updateLeftSide(Constraint c) {
		Summand[] leftSide = c.getLeftSide();
		double[] coeffs = new double[leftSide.length + 2];
		int[] vindexes = new int[leftSide.length + 2];

		int numCoeffs = setupCoefficients(c, coeffs, vindexes);

		try {
			lp.setRowex(c.getIndex(), numCoeffs, coeffs, vindexes);
		} catch (LpSolveException e) {
			throw new RuntimeException("Error in lp_solve:setRowex", e);
		}
	}

	private int setupCoefficients(Constraint c, double[] coeffs, int[] vindexes) {
		int i = 0;

		// add constraint summands
		for (Summand s : c.getLeftSide()) {
			coeffs[i] = s.getCoeff();
			vindexes[i] = s.getVar().getIndex();
			i++;
		}

		if (c.getPenalty() == Double.POSITIVE_INFINITY)
			return i;

		// for soft constraints, add extra summands to constraint and objective
		// function
		if (c.getOp() != OperatorType.LE) {
			// see if constraint has already a penalty summand in the objective
			// function
			Summand dPos = c.getdPosSummand();
			if (dPos == null) {
				// create new summand with slack variable for objective function
				Variable slack = new Variable(getLinearSpec());
				// set lower bound to 0 for slack variables.
				setLowbo(slack, 0);
				dPos = new Summand(c.getPenalty(), slack);
				c.setdPosSummand(dPos);
				objFunctionSummands.add(dPos);
			} else {
				dPos.setCoeff(c.getPenalty());
			}

			// add slack variable to constraint summands
			vindexes[i] = dPos.getVar().getIndex();
			coeffs[i] = 1.0;
			i++;
		}

		if (c.getOp() != OperatorType.GE) {
			// see if constraint has already a penalty summand in the objective
			// function
			
			Summand dNeg = c.getdNegSummand();
			if (dNeg == null) {
				// create new summand with slack variable for objective function
				Variable slack = new Variable(getLinearSpec());
				// set lower bound to 0 for slack variables.
				setLowbo(slack, 0);
				dNeg = new Summand(c.getPenalty(), slack);
				c.setdNegSummand(dNeg);
				objFunctionSummands.add(dNeg);
			}

			// add slack variable to constraint summands
			vindexes[i] = dNeg.getVar().getIndex();
			coeffs[i] = -1.0;
			i++;
		}
		return i;
	}

	private void updateObjFunction() {
		double[] coeffs = new double[objFunctionSummands.size()];
		int[] vindexes = new int[objFunctionSummands.size()];
		int i = 0;
		for (Summand s : objFunctionSummands) {
			coeffs[i] = s.getCoeff();
			vindexes[i] = s.getVar().getIndex();
			i++;
		}
		try {
			lp.setObjFnex(objFunctionSummands.size(), coeffs, vindexes);
		} catch (LpSolveException e) {
			throw new RuntimeException("Error in lp_solve:setObjFnex", e);
		}
	}

	private void updateOp(Constraint c) {
		try {
			lp.setConstrType(c.getIndex(),
					((c.getOp() == OperatorType.EQ) ? lpsolve.LpSolve.EQ : (c
							.getOp() == OperatorType.GE) ? lpsolve.LpSolve.GE
							: lpsolve.LpSolve.LE));
		} catch (LpSolveException e) {
			throw new RuntimeException("Error in lp_solve:setConstrType", e);
		}
	}

	private void updateRight(Constraint c) {
		try {
			lp.setRh(c.getIndex(), c.getRightSide());
		} catch (LpSolveException e) {
			throw new RuntimeException("Error in lp_solve:setRh", e);
		}
	}
	
	private void setLowbo(Variable v, double lb) {
		try {
			lp.setLowbo(v.getIndex(), lb);
		} catch (LpSolveException e) {
			throw new RuntimeException("Error in lp_solve:setLowBond", e);
		}
	}

	public void add(Variable v) {
		if (getLinearSpec().getVariables().size() > 0) {
			double[] d = { 0 };
			int[] i = { 0 };
			try {
				lp.addColumnex(0, d, i);
				// set lower bound to -infinity. default is 0.
				setLowbo(v, Double.NEGATIVE_INFINITY);
			} catch (LpSolveException e) {
				throw new RuntimeException("Error in lp_solve:addColumnex", e);
			}
		}
	}

	public void remove(Variable v) {
		try {
			lp.delColumn(v.getIndex());
		} catch (LpSolveException e) {
			throw new RuntimeException("Error in lp_solve:delColumn", e);
		}
	}	
	
}
