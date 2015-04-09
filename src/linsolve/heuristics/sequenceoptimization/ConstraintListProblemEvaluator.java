package linsolve.heuristics.sequenceoptimization;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import linsolve.Constraint;
import linsolve.LinearSpec;
import linsolve.Summand;
import linsolve.Variable;

import org.opt4j.core.Objective;
import org.opt4j.core.Objectives;
import org.opt4j.core.Objective.Sign;
import org.opt4j.core.problem.Evaluator;

import com.google.inject.Inject;

public class ConstraintListProblemEvaluator implements Evaluator<ConstraintListProblemPhenotype>{
	private Objective error = new Objective("error", Sign.MIN);
	private List<Constraint> problem;
	
	@Inject
	public ConstraintListProblemEvaluator(List<Constraint> problem) {
		this.problem   = problem;

	}
	
	@Override
	public Objectives evaluate(ConstraintListProblemPhenotype phenotype) {
		double obj = 0;
		List<Constraint> list = phenotype.getConstraints();

		HashSet<Variable> vars = new HashSet<Variable>();
		
		for(Constraint c : list){
			vars.add(c.getPivotSummand().getVar());
			c.getPivotSummand().getVar().setValue(0.0d);
		}
		
		for(int i = 0; i < 10; i++){
		double sum = 0.0d;
		double result = 0.0d;
		for(Constraint c : list){
			vars.add(c.getPivotSummand().getVar());
			for(Summand s: c.getLeftSide()){
				if(!s.equals(c.getPivotSummand())){
					sum += s.getCoeff()*s.getVar().getValue();
				}
			}
			result = sum / c.getPivotSummand().getVar().getValue();
			c.getPivotSummand().getVar().setValue(result);
		}
		}
		
		Variable max = vars.iterator().next();
		for(Variable v : vars){
			if(v.getValue() > max.getValue())
				max = v;
		}
		obj = max.getValue();
		
		Objectives objectives = new Objectives();
		objectives.add(error, obj);
		
		for(Variable v : vars){
			v.setValue(0.0d);
		}
		
		//System.out.println("Objective " + obj);
		//for(int i = 0; i < problem.size(); i++)	System.out.println(list.get(i));
		
		return objectives;
	}
}