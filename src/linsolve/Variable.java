package linsolve;

import java.lang.ref.WeakReference;

import lpsolve.LpSolveException;

/**
 * This class represents a variable in a linear constraint. 
 */

public class Variable {

	public LinearSpec ls; // linear spec that this variable belongs to
	double value = Double.NaN; // value of the variable
	String name; // name of the variable
	protected Summand summandWhereMaxDominant;
	protected double maxDominance = 0;


	/**
	 * The pivot constraint of a variable is the constraint where this variable
	 * is most dominant, as compared with all editor constraints.
	 */
	

	protected Constraint constraintWhereMaxDominant;
	public Constraint getConstraintWhereMaxDominant() {
		return constraintWhereMaxDominant;
	}

	public void setConstraintWhereMaxDominant(Constraint constraintWhereMaxDominant) {
		this.constraintWhereMaxDominant = constraintWhereMaxDominant;
	}

	public Summand getSummandWhereMaxDominant() {
		return summandWhereMaxDominant;
	}

	public void setSummandWhereMaxDominant(Summand summandWhereMaxDominant) {
		this.summandWhereMaxDominant = summandWhereMaxDominant;
	}

	public double getMaxDominance() {
		return maxDominance;
	}

	public void setMaxDominance(double maxDominance) {
		this.maxDominance = maxDominance;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Variable(LinearSpec ls) {
		this.ls = ls;
		this.ls.addVariable(this);
		this.ls.getSolver().add(this);
	}
	
	public Variable(String name, double value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * Gets 1-based index of the variable.
	 * 
	 * @return the index of the variable
	 */
	public int getIndex() {
		int i = ls.getVariables().indexOf(this);
		if (i == -1)
			throw new RuntimeException("Variable not part of ls.variables.");
		return i + 1;
	}

	/**
	 * Returns index of the variable as <code>String</code>. E.g. "Var2"
	 * 
	 * @return the <code>String</code> index of the variable
	 */
	public String toString() {
		if (name != null)
			return name;

		try {
			return "Var" + getIndex();
		} catch (Exception e) {
			return "Exception in getIndex().";
		}
	}

	/**
	 * Adds a constraint that sets this variable equal to the given one.
	 * 
	 * @param v
	 *            variable that should have the same value
	 * @return the new equality constraint
	 */
	public Constraint isEqual(Variable v) {
		return ls.addConstraint(1.0, this, -1.0, v, OperatorType.EQ, 0);
	}

	/**
	 * Adds a constraint that sets this variable smaller or equal to the given
	 * one.
	 * 
	 * @param v
	 *            variable that should have a larger or equal value
	 * @return the new constraint
	 */
	public Constraint isSmallerOrEqual(Variable v) {
		return ls.addConstraint(1.0, this, -1.0, v, OperatorType.LE, 0);
	}

	/**
	 * Adds a constraint that sets this variable greater or equal to the given
	 * one.
	 * 
	 * @param v
	 *            variable that should have a smaller or equal value
	 * @return the new constraint
	 */
	public Constraint isGreaterOrEqual(Variable v) {
		return ls.addConstraint(-1.0, v, 1.0, this, OperatorType.GE, 0);
	}

	/**
	 * Removes the variable from its specification.
	 */
	public void remove() {
		ls.getSolver().remove(this);
		ls.removeVariable(this);
	}

	public void setValue(double value) {
		this.value = value;
	}

	public double getValue() {
		return value;
	}

}
