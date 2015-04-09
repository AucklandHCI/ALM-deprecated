package linsolve.heuristics;

import java.util.List;
import java.util.Random;

import linsolve.BinarySearchStrategy;
import linsolve.Constraint;
import linsolve.LinearSolver;
import linsolve.LinearSolverAdapter;
import linsolve.LinearSpec;
import linsolve.OperatorType;
import linsolve.RelaxationSolverAdapter;
import linsolve.pivots.RandomPivotSummandSelector;

import org.opt4j.core.problem.Creator;
import org.opt4j.genotype.DoubleGenotype;
import org.opt4j.genotype.IntegerBounds;
import org.opt4j.genotype.IntegerGenotype;
import org.opt4j.genotype.PermutationGenotype;

import com.google.inject.Inject;

public class ConstraintProblemCreator implements Creator<PermutationGenotype<Constraint>>{

	//private LinearSpec problem;
	private Random random = new Random(); 
	private List<Constraint> problem;
	
	@Inject
	public ConstraintProblemCreator(List<Constraint> problem) {
		this.problem = problem;
	}

	@Override
	public PermutationGenotype<Constraint> create() {
		PermutationGenotype<Constraint> genotype = new PermutationGenotype<Constraint>();
		for(Constraint c : problem)
			genotype.add(c);
		return genotype;
	}
}
