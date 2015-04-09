package linsolve.heuristics;

import java.util.List;

import linsolve.Constraint;
import linsolve.LinearSpec;

import org.opt4j.core.problem.ProblemModule;

import com.google.inject.Provides;

public class ConstraintProblemModule extends ProblemModule{

	private LinearSpec problem = new LinearSpec();
	
	
	@Provides
	public LinearSpec getProblemDescription() {
		return problem;
	}

	@Provides
	public List<Constraint> getListOfConstraints() {
		return problem.getConstraints();
	}
	
	public void setProblemDescription(LinearSpec problem) {
		this.problem = problem;
	}
	
	@Override
	protected void config() {
		bindProblem(ConstraintProblemCreator.class, 
				ConstraintProblemDecoder.class, 
				ConstraintProblemEvaluator.class);
	}

	
	
}
