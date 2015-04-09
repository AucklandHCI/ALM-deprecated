package linsolve.heuristics;

import java.util.List;

import linsolve.Variable;

import org.opt4j.core.Phenotype;

public class ConstraintProblemPhenotype implements Phenotype  {
	private List<Variable> variables;

	public void setVariables(List<Variable> variables) {
		this.variables = variables;
	}

	public List<Variable> getVariables() {
		return variables;
	}
	
}
