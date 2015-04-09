	package linsolve.heuristics;


import java.util.ArrayList;
import java.util.List;

import linsolve.LinearSpec;
import linsolve.Variable;

import org.opt4j.core.problem.Decoder;
import org.opt4j.genotype.IntegerGenotype;

import com.google.inject.Inject;

public class ConstraintProblemDecoder implements Decoder<IntegerGenotype, ConstraintProblemPhenotype>{

	private LinearSpec problem;
	
	@Inject
	public ConstraintProblemDecoder(LinearSpec problem) {
		this.problem = problem;
	}
	
	@Override
	public ConstraintProblemPhenotype decode(IntegerGenotype genotype) {
		ConstraintProblemPhenotype type = new ConstraintProblemPhenotype();
		List<Variable> clone = new ArrayList<Variable>(problem.getVariables());
		type.setVariables(problem.getVariables());
		Variable v;
		for(int i = 0; i < type.getVariables().size(); i++){
			
			v = type.getVariables().get(i);
			v.setValue(genotype.get(i));
		//	System.out.println(v.getValue() + ", ");
			//System.out.print(genotype.get(i) + ", ");
		}
		
		return type;
	}

}
