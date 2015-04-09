package linsolve.pivots;

import java.util.List;

import linsolve.Constraint;
import linsolve.LinearSpec;
import linsolve.Summand;
import linsolve.Variable;

public interface PivotSummandSelector {
	
	List<Constraint> init(List<Constraint> constraints, List<Variable> variables, int maxIndex);
	
	List<Constraint> init(LinearSpec linearSpec, int maxIndex);
	
	List<Constraint> init(LinearSpec linearSpec);

	List<Constraint> removeConstraint(List<Constraint> constraints, Constraint c);
	
	Summand selectPivotSummand(Constraint constraint);
}
 