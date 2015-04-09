package linsolve.heuristics.sequenceoptimization;

import linsolve.Constraint;

import org.opt4j.core.problem.Decoder;
import org.opt4j.genotype.PermutationGenotype;

public class ConstraintListProblemDecoder implements Decoder<PermutationGenotype<Constraint>, ConstraintListProblemPhenotype>{
	
	
	public ConstraintListProblemDecoder() {
		
	}

	
	@Override
	public ConstraintListProblemPhenotype decode(PermutationGenotype<Constraint> genotype) {	
		
		ConstraintListProblemPhenotype type = new ConstraintListProblemPhenotype(genotype);
		
		return type;
	}
}