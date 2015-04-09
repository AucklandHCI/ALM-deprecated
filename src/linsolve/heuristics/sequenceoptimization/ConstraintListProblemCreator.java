package linsolve.heuristics.sequenceoptimization;

import java.util.List;
import java.util.Random;

import linsolve.Constraint;
import linsolve.LinearSpec;

import org.opt4j.core.problem.Creator;
import org.opt4j.genotype.PermutationGenotype;

import com.google.inject.Inject;

/**
 * Creates a Genome representing the List of Constraints.
 * 
 * @author jo
 *
 */
public class ConstraintListProblemCreator implements Creator<PermutationGenotype<Constraint>>{
	private List<Constraint> list;
	
	@Inject
	public ConstraintListProblemCreator(List<Constraint> list) {
		this.list = list;
	}

	@Override
	public PermutationGenotype create() {
		PermutationGenotype<Constraint> genotype = new PermutationGenotype<Constraint>();
		for(Constraint c : list) genotype.add(c);
		
		return genotype;
	}
}