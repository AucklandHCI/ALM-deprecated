package linsolve.performance;

import java.math.BigInteger;
import java.util.Collections;

import linsolve.Constraint;
import linsolve.ConstraintComparatorByPenalty;
import linsolve.LinearSpec;

public class TestHelper {
	
	public static BigInteger getIota(LinearSpec ls) {
		BigInteger result  = new BigInteger("0");
		BigInteger counter = new BigInteger("1");
		BigInteger two     = new BigInteger("2");
		
		Collections.sort(ls.getConstraints(), new ConstraintComparatorByPenalty());
		Collections.reverse(ls.getConstraints());
		
		Constraint c;
		for(int i = 0; i < ls.getConstraints().size(); i++){
			c = ls.getConstraints().get(i);
			if(!Constraint.equalZero(c.error())){
				result = result.add(counter);
			}
			counter = counter.multiply(two);
		}
		//System.out.println(this);
		return result;
	}

	public static int getDisabledConstraintsCount(LinearSpec ls){
		int result = 0;
		Constraint c;
		for(int i = 0; i < ls.getConstraints().size(); i++){
			c = ls.getConstraints().get(i);
			if(!c.isEnabled()){
				result++;
			}
		}
		return result;
	}

}
