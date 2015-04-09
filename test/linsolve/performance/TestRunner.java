package linsolve.performance;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

import alm.LayoutSpec;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

import linsolve.LinearSolver;
import linsolve.ResultType;

public abstract class TestRunner {

	public static final char COLUMN_SEPARATOR = ',';
	
	private String csvFilePath;
	private int iterations;
	private int from;
	private int to;
	private int step;
	private Map<String,LinearSolver> solvers;
	
	private TestDataGenerator testDataGenerator;
	private CSVWriter csvWriter;
	private CSVReader csvReader;
	
	private Result result;
	
	
	public int getIterations() {
		return iterations;
	}

	public int getFrom() {
		return from;
	}

	public int getTo() {
		return to;
	}

	public int getStep() {
		return step;
	}

	public Map<String, LinearSolver> getSolvers() {
		return solvers;
	}
	
	public TestRunner(){
		testDataGenerator = new TestDataGenerator();
		
	}
	
	protected TestDataGenerator getTestDataGenerator(){
		return this.testDataGenerator;
	}
	
	protected String getCSVFilePath(){
		return csvFilePath;
	}
	
	/**
	 * configures a PerformanceTestRunner.
	 * 
	 * @param path the path to the csv file which contains the results of a performance measurement
	 * @param iterations the number of iterations each problem size should be solved
	 * @param maxSize the problem sizes which have to be solved max size is the maximum of the sizes. The sizes start with 1 and end at maxsize
	 * @param solvers the solvers which have to be evaluated
	 */
	public void configure(String path, int iterations, int from, int to, int step, Map<String, LinearSolver> solvers){	this.csvFilePath =  path;
		this.csvFilePath = path;
		this.iterations = iterations;
		this.from = from;
		this.to = to;
		this.step = step;
		this.solvers = solvers;
	}
	
	public final void start() throws IOException, Exception{
		try{
			doPerformanceTest();
		
		}finally{
			closeWriter();
		}
	}
	
	public void doPerformanceTest() throws IOException, Exception{
		writeHeader(result);
		int obs = 0;
		int problem = 0;
		for(int i = from; i < to; i = i + step){
			getTestDataGenerator().configure(iterations, i);
			while(getTestDataGenerator().hasNext()){
				//System.out.println("The following problem is the testdata for the next runs;");
				//System.out.println(getTestDataGenerator().next());
				for(String name : solvers.keySet()){
					result.resetEntries();
					//System.out.println("Test " + name + " with problem size " + size);
					LinearSolver solver = configureLinearSolver(solvers.get(name));
			
					LayoutSpec ls = getTestDataGenerator().next();
					
					ls.setSolver(solver);
					ls.solve();
					
					compileResult(solver, name, ls, i, iterations, problem, obs);
					writeResult(result);
					obs++;
				}
				problem++;
			}
		}
	}
	
	/**
	 * This method has to be overriden for specific tests where all the measured attributes and the names of their
	 * categories are configured.
	 * 
	 * @param solver
	 * @param size
	 * @param iteration
	 * @param problem
	 */
	public abstract void compileResult(LinearSolver solver, String name, LayoutSpec ls, int size, int iteration, int problem, int obs);
	
	
	/**
	 * In case some configuration has to be done at the linear solver under test this is the hook to 
	 * do so.
	 * 
	 * Eg some decorators can be added.
	 * 
	 * The default implementation just returns the original solver.
	 * 
	 * @param solver the unconfigured solver
	 * @return the configured solver.
	 */
	public LinearSolver configureLinearSolver(LinearSolver solver){
		return solver;
	}
	/**
	 * inject a configured result object with all required categories.
	 * 
	 * @param result
	 */
	public void setResultCategories(Result result){
		this.result = result;
	}
	
	public Result getResult(){
		return result;
	}
	
	protected void writeHeader(Result result) throws IOException{
		openWriter();
		csvWriter.writeNext(result.getEntryHeader());
		
	}
	
	protected void writeResult(Result result) throws IOException{
		System.out.println(result.toString());
		csvWriter.writeNext(result.toStringArray());
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
}
