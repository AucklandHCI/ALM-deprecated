package linsolve.heuristics.sequenceoptimization;

import java.util.ArrayList;
import java.util.List;

import linsolve.Constraint;

import org.opt4j.core.Phenotype;
import org.opt4j.genotype.PermutationGenotype;

public class ConstraintListProblemPhenotype implements Phenotype  {
	private List<Constraint> constraints;
	
	public List<Constraint> getConstraints() {
		return constraints;
	}

	public void setConstraints(List<Constraint> constraints) {
		this.constraints = constraints;
	}

	public ConstraintListProblemPhenotype(PermutationGenotype<Constraint> genotype){
		constraints = new ArrayList<Constraint>();
		for(Constraint c : genotype )
			constraints.add(c);
		
	}
}