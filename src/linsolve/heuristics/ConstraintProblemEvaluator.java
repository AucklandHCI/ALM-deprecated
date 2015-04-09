package linsolve.heuristics;

import linsolve.Constraint;
import linsolve.LinearSpec;
import linsolve.OperatorType;

import org.opt4j.core.Objective;
import org.opt4j.core.Objectives;
import org.opt4j.core.Objective.Sign;
import org.opt4j.core.problem.Evaluator;

import com.google.inject.Inject;

public class ConstraintProblemEvaluator implements Evaluator<ConstraintProblemPhenotype>{

	private LinearSpec problem;
	private Objective error = new Objective("error", Sign.MIN);
	
	@Inject
	public ConstraintProblemEvaluator(LinearSpec problem) {
		this.problem = problem;
	}
	
	@Override
	public Objectives evaluate(ConstraintProblemPhenotype arg0) {
		double obj = 0;
		double penalty = 0.0d;
		for(Constraint c : problem.getConstraints()){
			// We do not need to distinguish the cases of ge le and eq because this
			// is already done in the calculation of the error
			penalty = Math.abs(Double.isInfinite(c.getPenalty()) ? Double.MAX_VALUE : c.getPenalty())/problem.getMaxRealPenalty();
			obj += Math.pow(Math.abs(c.error()), 2)*(Math.abs(penalty));

		}
		//System.out.println(problem.getCurrentSolution());
	//	System.out.println(obj);
		Objectives objectives = new Objectives();
		objectives.add(error, obj);
		return objectives;
	}
	
}
