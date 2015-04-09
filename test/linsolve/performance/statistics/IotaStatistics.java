package linsolve.performance.statistics;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

import linsolve.performance.TestRunner;
import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class IotaStatistics {
	
	public static List<String> getSolverNames(String csvFilePath, int no) throws IOException{
		CSVReader csvReader = new CSVReader(new FileReader(csvFilePath), TestRunner.COLUMN_SEPARATOR);
		List<String[]>results = csvReader.readAll();
		csvReader.close();
		
		List<String> names = new ArrayList<String>();
		// We are not interested in the heading
		for(int i = 1; i <= no; i++){
			names.add(results.get(i)[1]);
		}
		return names;
	}
	
	public static void calculateStatisticsImpl(String csvFilePath, String resultPath, List<String> solvernames) throws IOException{
		CSVReader csvReader = new CSVReader(new FileReader(csvFilePath), TestRunner.COLUMN_SEPARATOR);
		List<String[]>results = csvReader.readAll();
		csvReader.close();
		
		List<String[]> statistics = new ArrayList<String[]>(); 
		statistics.add(new String[]{"Solver", "Mean", "Var", "Stdv"});
		
		List<BigDecimal> sublist;
		BigDecimal mean;
		BigDecimal var;
		BigDecimal stdv;
		
		BigDecimal log_mean;
		BigDecimal log_var;
		BigDecimal log_stdv;	
		
		for(String s : solvernames){
			sublist = select(s, results, 9);
			mean = BigDecimalMathUtils.mean(sublist, MathContext. DECIMAL128);
			var = BigDecimalMathUtils.var(sublist, true, MathContext.DECIMAL128);
			stdv = BigDecimalMathUtils.stddev(sublist, true, MathContext.DECIMAL128);
			
			log_mean = BigDecimalMathUtils.log(2, mean);
			log_var = BigDecimalMathUtils.log(2, var);
			log_stdv = BigDecimalMathUtils.log(2, stdv);
			
			statistics.add(new String[]{s, log_mean.toPlainString(), log_var.toPlainString(), log_stdv.toPlainString()});
		}
		
		CSVWriter csvWriter = new CSVWriter(new FileWriter(resultPath), TestRunner.COLUMN_SEPARATOR);
		csvWriter.writeAll(statistics);
		csvWriter.close();
	}
	
	public static List<BigDecimal> select(String solver, List<String[]> set, int column){
		
		List<BigDecimal> result = new ArrayList<BigDecimal>();
		
		for(String[] s : set){
			if(s[1].equals(solver)){
				
				result.add(new BigDecimal(s[column]));
			}
		}
		return result;
	}
}
