package linsolve.heuristics.sequenceoptimization;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import linsolve.Constraint;
import linsolve.LinearSpec;
import linsolve.ResultType;
import linsolve.Summand;
import linsolve.Variable;
import linsolve.heuristics.ConstraintProblemDecoder;
import linsolve.heuristics.ConstraintProblemPhenotype;
import linsolve.pivots.PivotSummandSelector;

import org.opt4j.core.Individual;
import org.opt4j.core.optimizer.Archive;
import org.opt4j.genotype.IntegerGenotype;
import org.opt4j.genotype.PermutationGenotype;
import org.opt4j.optimizer.ea.EvolutionaryAlgorithmModule;
import org.opt4j.optimizer.sa.CoolingSchedulesModule;
import org.opt4j.optimizer.sa.SimulatedAnnealingModule;
import org.opt4j.start.Opt4JTask;

import com.google.inject.Module;

public class SequenceOptimizer implements PivotSummandSelector {

	private Opt4JTask task;
	
	private boolean sequenceOptimized = false;
	
	private List<Constraint> orderedListOfConstraints;
	
	private long start, end;

	private Type type;
	
	private PivotSummandSelector selector;
	
	public static enum Type{
		SimulatedAnnealing ,
		Evolutionary
	};
	
	public SequenceOptimizer(PivotSummandSelector selector){
		this(Type.SimulatedAnnealing);
		this.selector = selector;
	}
	
	private SequenceOptimizer(Type type){
		this.type = type;
	}
	
	
	public SequenceOptimizer() {
		this(Type.Evolutionary);
	}

	private void configureOpt4JForEvolutionary(List<Constraint> list) {
		EvolutionaryAlgorithmModule module = new EvolutionaryAlgorithmModule();
		module.setAlpha(100);
		module.setLambda(25);
		module.setMu(25);
		module.setCrossoverRate(0.95d);
		module.setGenerations(100);


		ConstraintProblemModule problemModule = new ConstraintProblemModule();
		problemModule.setProblemDescription(list);
		
		Collection<Module> modules = new ArrayList<Module>();
		modules.add(problemModule);
		modules.add(module);

		task = new Opt4JTask(false);
		task.init(modules);
	}

	private void configureOpt4JForAnnealing(List<Constraint> list) {
		SimulatedAnnealingModule annealingModule = new SimulatedAnnealingModule();
		annealingModule.setIterations(100);

		CoolingSchedulesModule coolingModules = new CoolingSchedulesModule();
		coolingModules.setAlpha(0.995);
		coolingModules.setInitialTemperature(5000);
		coolingModules.setFinalTemperature(0);
		coolingModules.setType(org.opt4j.optimizer.sa.CoolingSchedulesModule.Type.HYPERBOLIC);

		ConstraintProblemModule problemModule = new ConstraintProblemModule();
		problemModule.setProblemDescription(list);
		
		Collection<Module> modules = new ArrayList<Module>();
		modules.add(problemModule);
		modules.add(annealingModule);
		modules.add(coolingModules);

		task = new Opt4JTask(false);
		task.init(modules);
	}

	private void configure(List<Constraint> list){
		switch(type){
		case Evolutionary : configureOpt4JForEvolutionary(list); break;
		case SimulatedAnnealing : configureOpt4JForAnnealing(list); break;
		}
	}
	

	public List<Constraint> sort(List<Constraint> list){
		
		ConstraintListProblemPhenotype solution = null;
		try {
			orderedListOfConstraints   = list;
			configure(orderedListOfConstraints);
			task.execute();
			Archive archive = task.getInstance(Archive.class);
			if (archive.size() > 0) {
				Individual individual = archive.iterator().next();
				ConstraintListProblemDecoder decoder = new ConstraintListProblemDecoder();
				solution = decoder.decode((PermutationGenotype<Constraint>) individual.getGenotype());
			}
			return solution.getConstraints();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



		return list;
	}
	
	public List<Constraint> init(List<Constraint> list, int maxIndex){
		return this.sort(list);
	}
	
	
	@Override
	public List<Constraint> init(LinearSpec linearSpec, int maxIndex) {
		return init(selector.init(linearSpec, maxIndex), maxIndex);
	}

	@Override
	public List<Constraint> init(LinearSpec linearSpec) {
		// TODO Auto-generated method stub
		return init(linearSpec, linearSpec.getConstraints().size() - 1);
	}

	@Override
	public List<Constraint> removeConstraint(List<Constraint> constraints,
			Constraint c) {
		// TODO Auto-generated method stub
		return selector.removeConstraint(constraints, c);
	}

	@Override
	public Summand selectPivotSummand(Constraint constraint) {
		// TODO Auto-generated method stub
		return selector.selectPivotSummand(constraint);
	}

	@Override
	public List<Constraint> init(List<Constraint> constraints,
			List<Variable> variables, int maxIndex) {
		// TODO Auto-generated method stub
		return null;
	}
}
