package linsolve.performance.experiments;


import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.ParseException;
import java.text.ParsePosition;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import linsolve.BinarySearchStrategy;
import linsolve.LinearRelaxationAddingConstraints;
import linsolve.LinearRelaxationRemovingSuspicious;
import linsolve.LinearSolver;
import linsolve.ResultType;
import linsolve.performance.Result;
import linsolve.performance.TestHelper;
import linsolve.performance.TestRunner;
import linsolve.performance.decorator.LinearSolverMemoryDecorator;
import linsolve.performance.decorator.LinearSolverTimerDecorator;
import linsolve.performance.statistics.IotaStatistics;
import linsolve.pivots.DeterministicPivotSummandSelector;
import linsolve.pivots.RandomPivotSummandSelector;

import org.junit.Before;
import org.junit.Test;

import alm.LayoutSpec;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**Here we implement performance test for speed that we currently use.
 * @author njam031
 *
 */
public class PerformanceTests {

	public static final String CSV_FILE_PATH = "/Users/jo/Documents/Uni/postdoc/publications/current/LinearRelaxation-Constraints2011/Evaluation/results.csv";
	//public static final String CSV_FILE_PATH = "./results.csv";
	//public static final String CSV_FILE_PATH = "C:\\Downloads\\results.csv";
	
	@Before
	public void setUp() throws Exception {
		
	}
	
	
	@Test
	public void test() throws Exception{
		TestRunner runner = configureTestRunner();
		Map<String, LinearSolver> solvers = new HashMap<String, LinearSolver>();
		solvers.put("constraint insertion with deterministic pivot assignment", new LinearRelaxationAddingConstraints(new DeterministicPivotSummandSelector()));		
		solvers.put("constraint insertion with random pivot assignment", new LinearRelaxationAddingConstraints(new RandomPivotSummandSelector()));
	    solvers.put("constraint removal with random pivot assignment",	new LinearRelaxationRemovingSuspicious(new RandomPivotSummandSelector()));
	    solvers.put("constraint removal with deterministic pivot assignment",	new LinearRelaxationRemovingSuspicious(new DeterministicPivotSummandSelector()));
		solvers.put("binary search with random pivot assignment", new BinarySearchStrategy(new RandomPivotSummandSelector()));
        solvers.put("binary search with deterministic pivot assignment", new BinarySearchStrategy(new DeterministicPivotSummandSelector()));
       // solvers.put("KaczmarzSolver", new ConflictResolutionAddingStrategy(new KaczmarzSolver()));
		//solvers.put("Lp-solve", new LpSolve());
	//	solvers.put("Linopt", new MatlabLinProgSolver());
		//solvers.put("QRSolver", new QRSolver());
		
		//repititions and areas
		runner.configure(CSV_FILE_PATH, 10, 1, 100, 1, solvers);
		runner.start();
		
		System.out.println("post process");
		postProcessDisabledConstraints(CSV_FILE_PATH, solvers.size());
		System.out.println("calculate statistics");
		calculateStatistics();
	}
	
	private TestRunner configureTestRunner(){
		TestRunner runner = new TestRunner(){ 
			public void compileResult(LinearSolver solver, String name, LayoutSpec ls,int size, int iteration, int problem, int obs){
				Result rslt = this.getResult();
				
				try {
					rslt.addEntry("obs", obs);
					rslt.addEntry("solver", name);
					rslt.addEntry("time", solver.getLastSolvingTime());
					rslt.addEntry("memory", ((LinearSolverMemoryDecorator) solver).getRequiredMemory());
					rslt.addEntry("size", size);
					rslt.addEntry("constraints", ls.getConstraints().size());
					rslt.addEntry("problem", problem);
					rslt.addEntry("optimal", solver.getLastSolvingResult() == ResultType.OPTIMAL ||solver.getLastSolvingResult() == ResultType.SUBOPTIMAL? 1 : 0);
					rslt.addEntry("disabledConstraints", TestHelper.getDisabledConstraintsCount(ls));
					rslt.addEntry("iota", TestHelper.getIota(ls));
					rslt.addEntry("iota_rel", 0);
					rslt.addEntry("iota_ordered", 0);
					
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			public LinearSolver configureLinearSolver(LinearSolver solver){
				LinearSolverMemoryDecorator memoryMeasurement = new LinearSolverMemoryDecorator();
				memoryMeasurement.setBody(solver);
			
				return memoryMeasurement;
			}
		};
			
			Result rslt = new Result();
			rslt.addEntryCategory("obs");
			rslt.addEntryCategory("solver");
			rslt.addEntryCategory("time");
			rslt.addEntryCategory("memory");
			rslt.addEntryCategory("size");
			rslt.addEntryCategory("constraints");
			rslt.addEntryCategory("problem");
			rslt.addEntryCategory("optimal");
			rslt.addEntryCategory("disabledConstraints");
			rslt.addEntryCategory("iota");
			rslt.addEntryCategory("iota_rel");
			rslt.addEntryCategory("iota_ordered");
			
			runner.setResultCategories(rslt);
			
			return runner;
	}
	
	
	
	//@Test
	public void postProcessResult() throws IOException, ParseException{
		postProcessDisabledConstraints(CSV_FILE_PATH, 8);
	}
	
	// Mark it as test to run the statistics calculations for iota
	//@Test
	public void calculateStatistics() throws IOException{
		IotaStatistics.calculateStatisticsImpl(CSV_FILE_PATH, "statistics_iota.csv",IotaStatistics.getSolverNames(CSV_FILE_PATH, 8));
	}
	
	
	
	private BigInteger createBigInteger(String in){
		final DecimalFormat dform = (DecimalFormat)DecimalFormat.getInstance(Locale.ENGLISH);
		final DecimalFormatSymbols newSymbols = new DecimalFormatSymbols(Locale.ENGLISH);
	    newSymbols.setExponentSeparator("E+");
	    dform.setDecimalFormatSymbols(newSymbols);
	    dform.applyLocalizedPattern("0.##E+0");
	    dform.setParseBigDecimal(true);
	    dform.setParseIntegerOnly(true);
	    final ParsePosition pp = new ParsePosition(0);
	    final Number no = dform.parse(in, pp);
	    final DecimalFormat formatter = new DecimalFormat("###.#####");
	    formatter.setMaximumIntegerDigits(1000000);
	    final String out = formatter.format(no);
	   // System.out.println(in + " / PP: " + pp.toString() + "/" + out + " " + no.getClass());
	    
		BigInteger i = new BigInteger(out);
		//System.out.println(i);
		return i;
	}
	
	/**
	 * Postprocesses a csv file that contains a iota value (see Constraints paper for details)
	 * in column 9 and calculates a normalized iota value (column 10) and a ordinal representation of 
	 * the measured values (column 11).
	 * 
	 * The normalized value has the form iota_rel = iota / testrun_min - 1
	 * Testrun_min is the minimum iota over all results of all tested solvers for one specific
	 * test problem.
	 * 
	 * The ordinal representation of the iota values is a integer number i in 0..N representing 
	 * the order of the iota values over all tested solvers for one specific test problem. The 
	 * lowest iota is i = 0, the next greater iota is i = 1 and so on. 
	 * 
	 * The reason to post process the iota values is that the measured iota values cannot be evaluated
	 * with statistical packages. Even for moderately sized test problems of area size 100 does iota 
	 * has in the worst case already a size 2^400 but e.g. R can only process numbers up to 2^64.
	 * 
	 * @param csvFilePath path to the csv file, which has to be postprocessed
	 * @param noOfSolvers the number of solvers whose test results are in the csv file at csvFilePath
	 * @throws IOException if the csv file at csvFilePath cannot be read
	 */
	private void postProcessDisabledConstraints(String csvFilePath, int noOfSolvers) throws IOException{
		CSVReader csvReader = new CSVReader(new FileReader(csvFilePath), TestRunner.COLUMN_SEPARATOR);
		List<String[]>results = csvReader.readAll();
		csvReader.close();
		
		BigInteger min;
		BigInteger current;
		BigInteger normalized;
		BigInteger one = new BigInteger("1");
		
		// we start at position 1 because of the headings
		for(int i = 1; i < results.size(); i += noOfSolvers){
			Collections.sort(results.subList(i, i + noOfSolvers), new Comparator<String[]>(){
				@Override
				public int compare(String[] arg0, String[] arg1) {
					// TODO Auto-generated method stub
					// Order is big iota high rank small iota low rank
					// low rank is better result
					return createBigInteger(arg0[9]).compareTo(createBigInteger(arg1[9]));
				}});
		
			
			min = createBigInteger(results.get(i)[9]);
			
			int order = 0;
			int start_tie = 0;
			boolean tie = false;
			double tie_value = 0.0d;
			for(int j = i; j < i + noOfSolvers; j++){
				current = createBigInteger(results.get(j)[9]);
				
				normalized = (current.subtract(min)).divide(min.compareTo(one) < 0 ? one : min);
				results.get(j)[10] = normalized.toString();

				if(j < i + noOfSolvers - 1 && 
						createBigInteger(results.get(j + 1)[9]).equals(current)) {
					if(!tie) start_tie = order;
					tie = true;
				}else{
					if(!tie) start_tie = order;
					tie_value = calculateTieValue(start_tie, order);
					writeResult(results, i + start_tie, j, tie_value);
					tie = false;
				}
				order++;
			}
		}
		
		CSVWriter csvWriter = new CSVWriter(new FileWriter(csvFilePath), TestRunner.COLUMN_SEPARATOR);
		csvWriter.writeAll(results);
		csvWriter.close();	
	}
	
	private double calculateTieValue(int start_tie, int order){
		int tie_length = 0;
		double tie_value = 0.0d;
		int beginning = start_tie - 1; // 
		tie_length = order - beginning;
		double to_beginning = (beginning*(beginning + 1)/2);
		double to_order = (order*(order + 1)/2);
		tie_value = ( to_order - to_beginning ) / tie_length;
		return tie_value;
	}
	
	private void writeResult(List<String[]>target, int from, int to, double value){
		for(int i = from; i <= to; i++)
		target.get(i)[11] = new Double(value).toString();
	}
}
