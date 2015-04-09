package linsolve.heuristics.sequenceoptimization;

import java.util.ArrayList;
import java.util.List;

import linsolve.Constraint;
import linsolve.LinearSpec;
import linsolve.Variable;

import org.opt4j.core.problem.ProblemModule;

import com.google.inject.Provides;

public class ConstraintProblemModule extends ProblemModule{
	
	private List<Constraint> problem = new ArrayList<Constraint>();


	@Provides
	public List<Constraint> getProblemDescription() {
		return problem;
	}

	public void setProblemDescription(List<linsolve.Constraint> problem) {
		this.problem = problem;
	}
	
	@Override
	protected void config() {
		bindProblem(ConstraintListProblemCreator.class, 
				ConstraintListProblemDecoder.class, 
				ConstraintListProblemEvaluator.class);
	}
}