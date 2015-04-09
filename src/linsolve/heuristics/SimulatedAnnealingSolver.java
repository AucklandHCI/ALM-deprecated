package linsolve.heuristics;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;

import linsolve.Constraint;
import linsolve.LinearSolver;
import linsolve.LinearSpec;
import linsolve.ResultType;
import linsolve.Variable;

import org.opt4j.core.Individual;
import org.opt4j.core.optimizer.Archive;
import org.opt4j.genotype.IntegerGenotype;
import org.opt4j.optimizer.ea.EvolutionaryAlgorithmModule;
import org.opt4j.optimizer.sa.CoolingSchedulesModule;
import org.opt4j.optimizer.sa.SimulatedAnnealingModule;
import org.opt4j.start.Opt4JTask;

import com.google.inject.Module;

public class SimulatedAnnealingSolver implements LinearSolver {

	private LinearSpec linearSpec;
	private Opt4JTask task;

	private long start, end;

	private ResultType lastResult = ResultType.SUBOPTIMAL;

	private Type type;
	
	public static enum Type{
		SimulatedAnnealing ,
		Evolutionary
	};
	
	public SimulatedAnnealingSolver(){
		this(Type.Evolutionary);
	}
	
	public SimulatedAnnealingSolver(Type type){
		this.type = type;
	}
	
	@Override
	public LinearSpec getLinearSpec() {
		return linearSpec;
	}

	@Override
	public void setLinearSpec(LinearSpec linearSpec) {
		this.linearSpec = linearSpec;
	}
	
	private void configureOpt4JForEvolutionary() {
		EvolutionaryAlgorithmModule module = new EvolutionaryAlgorithmModule();
		module.setAlpha(100);
		module.setLambda(25);
		module.setMu(25);
		module.setCrossoverRate(0.95d);
		module.setGenerations(200);


		ConstraintProblemModule problemModule = new ConstraintProblemModule();
		problemModule.setProblemDescription(linearSpec);
		
		Collection<Module> modules = new ArrayList<Module>();
		modules.add(problemModule);
		modules.add(module);

		task = new Opt4JTask(false);
		task.init(modules);
	}

	private void configureOpt4JForAnnealing() {
		SimulatedAnnealingModule annealingModule = new SimulatedAnnealingModule();
		annealingModule.setIterations(1000);

		CoolingSchedulesModule coolingModules = new CoolingSchedulesModule();
		coolingModules.setAlpha(0.995);
		coolingModules.setInitialTemperature(5000);
		coolingModules.setFinalTemperature(0);
		coolingModules.setType(org.opt4j.optimizer.sa.CoolingSchedulesModule.Type.HYPERBOLIC);

		ConstraintProblemModule problemModule = new ConstraintProblemModule();
		problemModule.setProblemDescription(linearSpec);
		
		Collection<Module> modules = new ArrayList<Module>();
		modules.add(problemModule);
		modules.add(annealingModule);
		modules.add(coolingModules);

		task = new Opt4JTask(false);
		task.init(modules);
	}

	@Override
	public ResultType solve() {
		
		switch(type){
			case SimulatedAnnealing : 
				configureOpt4JForAnnealing();
				break;
			case Evolutionary : 
				configureOpt4JForEvolutionary();
				break;
		}
		
		ResultType result = ResultType.INFEASIBLE;
		ConstraintProblemPhenotype solution = null;
		try {
			task.execute();
			Archive archive = task.getInstance(Archive.class);
			if (archive.size() > 0) {
				Individual individual = archive.iterator().next();
				ConstraintProblemDecoder decoder = new ConstraintProblemDecoder(
						linearSpec);
			
				solution = decoder.decode((IntegerGenotype) individual
						.getGenotype());
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			task.close();
		}

		return ResultType.OPTIMAL;
	}

	@Override
	public void presolve() {
		// TODO Auto-generated method stub

	}

	@Override
	public void removePresolved() {
		// TODO Auto-generated method stub

	}

	@Override
	public void add(Constraint c) {
		// linearSpec.addConstraint(c);

	}

	@Override
	public void update(Constraint c) {
		// linearSpec.removeConstraint(c);
		// linearSpec.addConstraint(c);

	}

	@Override
	public void remove(Constraint c) {
		// linearSpec.removeConstraint(c);

	}

	@Override
	public void add(Variable v) {
		// linearSpec.addVariable(v);

	}

	@Override
	public void remove(Variable v) {
		// linearSpec.removeVariable(v);

	}

	@Override
	public long getLastSolvingTime() {
		return end - start;
	}

	@Override
	public ResultType getLastSolvingResult() {
		return lastResult;
	}

}
