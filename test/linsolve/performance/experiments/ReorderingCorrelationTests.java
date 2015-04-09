package linsolve.performance.experiments;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import linsolve.BinarySearchStrategy;
import linsolve.Constraint;
import linsolve.LinearRelaxationAddingConstraints;
import linsolve.LinearRelaxationRemovingSuspicious;
import linsolve.LinearSolver;
import linsolve.LpSolve;
import linsolve.QRSolver;
import linsolve.RelaxationSolverAdapter;
import linsolve.ResultType;
import linsolve.Summand;
import linsolve.Variable;
import linsolve.heuristics.sequenceoptimization.SequenceOptimizer;
import linsolve.performance.TestDataGenerator;
import linsolve.pivots.DeterministicPivotSummandSelector;
import linsolve.pivots.PivotSummandSelector;
import linsolve.pivots.RandomPivotSummandSelector;

import org.junit.Before;
import org.junit.Test;

import alm.LayoutSpec;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**This test was for reordering purpose that did not work for our GUI layout problems.
 * @author njam031
 *
 */
public class ReorderingCorrelationTests {

	private static final char COLUMN_SEPARATOR = '\t';
	private static final int ITERATIONS = 1;
	private static final int PROBLEMS = 100;
	private static final int START_SIZE = 200;
	private static final int END_SIZE = 201;
	
	private String csvFilePath;
	private TestDataGenerator testDataGenerator;
	private CSVWriter csvWriter;
	private CSVReader csvReader;
	
	
	@Before
	public void setUp() throws Exception {
		
	}
	
	@Test
	public void performanceTest() throws IOException{

		long begin, end = 0;
		csvFilePath = "./results.csv";
		int counter = 0;
		int problem = 0;
		testDataGenerator = new TestDataGenerator();
		writeHeader(new String[]{"obs", "size", "problem", "time w/o reorder", "time reorder", "iterations w/o reoder",  "iterations reorder"});
		for(int size = START_SIZE; size < END_SIZE; size ++){
			for(int i = 0; i < PROBLEMS; i++){
				testDataGenerator.configure(1, size);
				while(testDataGenerator.hasNext()){
					PivotSummandSelector selector = new DeterministicPivotSummandSelector();
					BinarySearchStrategy softPlain = new BinarySearchStrategy(selector);
					LayoutSpec ls = testDataGenerator.next();
					ls.setSolver(softPlain);
					ls.solve();

					List<Constraint> conflictFreeConstraints = (List<Constraint>) softPlain.getConstraints();

					for(int iteration = 0; iteration < ITERATIONS; iteration++){
						for(Variable v : ls.getVariables()) v.setValue(0.0d);
						//Collections.shuffle(conflictFreeConstraints);

						TestSolver solver = new TestSolver(selector);
						solver.setLinearSpec(ls);
						solver.setConstraints(conflictFreeConstraints);

						begin = System.currentTimeMillis();
						solver.solve();
						end = System.currentTimeMillis();

						int iterations_wreorder = solver.getRequiredNoIterations();
						long time_wreoder = end - begin;
						
						for(Variable v : ls.getVariables()) v.setValue(0.0d);
						solver.setLinearSpec(ls);
						solver.setConstraints(conflictFreeConstraints);
						solver.reorder();

						begin = System.currentTimeMillis();
						solver.solve();
						end = System.currentTimeMillis();

						int iterations_reorder = solver.getRequiredNoIterations();
						long time_reoder = end - begin;
						
						String[] result = new String[7];
						result[0] = Integer.toString(counter);
						result[1] = Integer.toString(size);
						result[2] = Integer.toString(problem);
						result[3] = Long.toString(time_wreoder);
						result[4] = Long.toString(time_reoder);
						result[5] = Integer.toString(iterations_wreorder);
						result[6] = Integer.toString(iterations_reorder);
						writeResult(result);
						counter ++;
					}
				}
				problem++;
			}
		}
		closeWriter();
	}
	
	protected double heuristic(List<Constraint> list){
		HashSet<Variable> vars = new HashSet();
		double[] results = new double[list.size()];
		
			double sum = 0.0d;
			double result = 0.0d;
			int k = 0;
			for(Constraint c : list){
				vars.add(c.getPivotSummand().getVar());
				for(Summand s: c.getLeftSide()){
					if(!s.equals(c.getPivotSummand())){
						sum += Math.abs(s.getCoeff())*s.getVar().getValue();
					}
				}
				result = sum / Math.abs(c.getPivotSummand().getCoeff());
				c.getPivotSummand().getVar().setValue(result);
			}
		
			
			Variable max = vars.iterator().next();
			for(int j = 0; j < list.size(); j++){
				Variable current = list.get(j).getPivotSummand().getVar();
				if(current.getValue() > max.getValue())
					max = current;
			}
			
			if(Double.isNaN(max.getValue())){
				for(int j = 0; j < list.size(); j++){
					System.out.print(list.get(j).getPivotSummand().getCoeff() + " ");
				}
				System.out.println();
			}
			
			return max.getValue();
	}
	
	protected void writeHeader(String[] header) throws IOException{
		openWriter();
		csvWriter.writeNext(header);
		
	}
	
	protected void writeResult(String[] row) throws IOException{
		System.out.println(stringArrayToString(row));
		csvWriter.writeNext(row);
	}
	
	public String stringArrayToString(String[] a){
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < a.length - 1; i++){
			sb.append(a[i]);
			sb.append(',');
		}
		sb.append(a[a.length - 1]);
		
		return sb.toString();
	}
	
	protected List<String[]> readCSVFile() throws IOException{
		csvReader = new CSVReader(new FileReader(csvFilePath), COLUMN_SEPARATOR);
		return csvReader.readAll();
	}
	
	protected void openWriter() throws IOException{
		this.csvWriter = new CSVWriter(new FileWriter(csvFilePath), COLUMN_SEPARATOR);
	}
	
	protected void closeWriter() throws IOException{
		csvWriter.close();
	}
	
private static class TestSolver extends RelaxationSolverAdapter{
		
		public TestSolver(PivotSummandSelector selector) {
			super(selector);
			// TODO Auto-generated constructor stub
		}

		public void reorder(){
			int counter = 0;
			int size = constraints.size();
			HashSet<Variable> precedingVars = new HashSet();
			
			
			Constraint constraint;
			Summand pivot;
			Constraint c;
			boolean found = false;
			while(counter < size - 1){
				constraint = constraints.get(counter);
				pivot = constraint.getPivotSummand();
				precedingVars.add(pivot.getVar());
				
				for(int i = 0; i < constraints.size(); i++){
					c = constraints.get(i);
					if(constraint != c){
						for(Summand s : c.getLeftSide()){
							if(c.getPivotSummand() != s){
								if(pivot.getVar() == s.getVar()){
									constraints.remove(i);
									constraints.add(counter + 1, c);
									found = true;
									break;
								}
							}
						}
					}
					if(found) break;
				}
				counter++;
				found = false;
			}
			
			
		}
		
		public ResultType solve(){
			return applyRelaxationMethod(1000000);
		}
	}

}
