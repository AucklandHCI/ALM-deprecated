package linsolve;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a linear constraint. The constraint can be in form
 * of a equality or an inequality. A penalty can be associated to the constraint
 * which specifies its priority. Higher priority means less chance to get
 * violated.
 */
public class Constraint implements Cloneable {

    public static final double MIN_PENALTY = 0.0;
    public static final double PREFERRED_SIZE_PENALTY = 0.5;
    public static final double MAX_PENALTY = 1.0;

    /**
     * The standard tolerance should be close to the maximum precision of double
     * here it is set to 12 decimal digits
     * <p/>
     * TODO: must take largest coefficient into account
     * if this is bigger than 1000, then the precision must be adjusted
     */
    public static final double DEFAULT_TOLERANCE = 0.0000000000001;

    /**
     * For GUI problems 0.001 tolerance should be enough. If we want to test general problems then
     * we should increase tolerance.
     */
    public static final double GUI_TOLERANCE = 0.01;
    /**
     * The tolerance value used by the relaxation solver.
     * This can be set by programs.
     */
    private static double tolerance = GUI_TOLERANCE;

    private LinearSpec ls; // linear spec which holds this constraint
    private Summand[] leftSide; // left side of the constraint
    private OperatorType op; // constraint operator (i.e. =, <-, >=)
    private boolean enabled = true; // is constraint enabled?
    private int unassignedVariables = 0; // number of unassigned variables
    private Summand pivotSummand;
    private Summand dNegSummand, dPosSummand;    // negative and positive slack variables. Only used in solver that needs it
    private double rightSide; // right side of the constraint
    private double penalty = Double.POSITIVE_INFINITY; // penalty, if the constraint gets violated
    private String name; // a string which specifies the constraint. useful for debugging
    public Object Owner;

    /**
     * Constructor.
     */
    public Constraint(Constraint c2) {
        this(c2.ls, c2.leftSide, c2.op, c2.rightSide, c2.getPenalty());
    }

    /**
     * Constructor.
     */
    public Constraint(LinearSpec ls, Summand[] summands, OperatorType op,
                      double rightSide, double penalty) {
        this.ls = ls;
        this.leftSide = summands;
        this.op = op;
        this.rightSide = rightSide;
        this.penalty = penalty;
        checkConstraint();
        ls.addConstraint(this);
        ls.getSolver().add(this);
    }

    /**
     * Returns the number of unassigned variables.
     */
    public int getUnassignedVariables() {
        return unassignedVariables;
    }

    /**
     * Sets the number of unassigned variables.
     */
    public void setUnassignedVariables(int unassignedVariables) {
        this.unassignedVariables = unassignedVariables;
    }

    /**
     * Sets the tolerance for errors.
     */
    public static void setTolerance(double tolerance) {
        Constraint.tolerance = tolerance;
    }

    /**
     * Returns the tolerance for errors.
     */
    public static double getTolerance() {
        return tolerance;
    }

    public Summand getPivotSummand() {
        return pivotSummand;
    }

    public void setPivotSummand(Summand pivotSummand) {
        this.pivotSummand = pivotSummand;
    }

    /**
     * Retruns constraint's name.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets constraint's name.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the summand for negative slack variables.
     */
    public void setdNegSummand(Summand dNegSummand) {
        this.dNegSummand = dNegSummand;
    }

    /**
     * Sets the summand for positive slack variables.
     */
    public void setdPosSummand(Summand dPosSummand) {
        this.dPosSummand = dPosSummand;
    }

    /**
     * Returns the summand for negative slack variables.
     */
    public Summand getdNegSummand() {
        return dNegSummand;
    }

    /**
     * Returns the summand for positive slack variables.
     */
    public Summand getdPosSummand() {
        return dPosSummand;
    }

    /**
     * Is this constraint enabled?
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Makes this constraint enabled/disabled.
     */

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * Gets the index of the constraint.
     *
     * @return the index of the constraint or -1 if not found
     */
    public int getIndex() {
        int i = ls.getConstraints().indexOf(this);
        if (i == -1)
            throw new RuntimeException("Constraint not part of ls.constraints.");
        return i + 1;
    }

    /**
     * Converts the constraint to a string. useful for debugging.
     */
    @Override
    public String toString() {
        StringBuffer strBuf = new StringBuffer();
        Summand[] summands = this.getLeftSide();
        for (int i = 0; i < summands.length - 1; i++) {
            if (summands[i].equals(pivotSummand))
                strBuf.append("#");
            strBuf.append(summands[i]);
            strBuf.append(" + ");
        }

        if (summands[summands.length - 1].equals(pivotSummand))
            strBuf.append("#");
        strBuf.append(summands[summands.length - 1]);
        strBuf.append(" ");
        strBuf.append(OperatorToString(this.getOp()));
        strBuf.append(" ");
        strBuf.append(this.getRightSide());
        strBuf.append("\tpenalty=");
        strBuf.append(this.getPenalty());
        strBuf.append("\terror=" + error());
        //strBuf.append("\tCurrentSolution=" + ls.getCurrentSolution());
        if (name != null)
            strBuf.append("\tname=" + name);
        if (!isEnabled())
            strBuf.append("\tDISABLED");
        return strBuf.toString();
    }

    /**
     * Converts the constraint to a string. useful for generating test cases.
     */
    public String toCommand() {
        StringBuffer strBuf = new StringBuffer();
        Summand[] summands = this.getLeftSide();
        strBuf.append("ls.addConstraint(");
        for (int i = 0; i < summands.length - 1; i++) {
            if (summands[i].equals(pivotSummand))
                strBuf.append("#");
            strBuf.append(summands[i].toCommand());
            strBuf.append(", ");
        }

        if (summands[summands.length - 1].equals(pivotSummand))
            strBuf.append("#");
        strBuf.append(summands[summands.length - 1].toCommand());
        strBuf.append(", ");
        strBuf.append(OperatorToCommand(this.getOp()));
        strBuf.append(", ");
        strBuf.append(this.getRightSide());
        strBuf.append(", ");
        if (this.getPenalty() == Double.POSITIVE_INFINITY)
            strBuf.append("Double.POSITIVE_INFINITY);");
        else
            strBuf.append(this.getPenalty() + ");");
        return strBuf.toString();
    }


    /**
     * Converts an operator to a string (e.g. OperatorType.GE -> ">=")
     */
    public String OperatorToString(OperatorType o) {
        if (o == OperatorType.EQ) {
            return "=";
        } else if (o == OperatorType.GE) {
            return ">=";
        } else if (o == OperatorType.LE) {
            return "<=";
        } else {
            return "UNKNOWN OPERATOR";
        }
    }

    /**
     * Converts an operator to its equivalent string (e.g. OperatorType.GE -> "OperatorType.GE")
     */
    public String OperatorToCommand(OperatorType o) {
        if (o == OperatorType.EQ) {
            return "OperatorType.EQ";
        } else if (o == OperatorType.GE) {
            return "OperatorType.GE";
        } else if (o == OperatorType.LE) {
            return "OperatorType.LE";
        } else {
            return "UNKNOWN OPERATOR";
        }
    }

    /**
     * Converts a string to its equivalent operator (e.g. ">=" -> OperatorType.GE)
     */
    public OperatorType StringToOperator(String s) {
        if (s.equals("=")) {
            return OperatorType.EQ;
        } else if (s.equals(">=")) {
            return OperatorType.GE;
        } else if (s.equals("<=")) {
            return OperatorType.LE;
        } else {
            System.out
                    .print("INCORRECT STRING PASSED INTO Constraint.StringToOperator()");
            return OperatorType.EQ;
        }
    }

    /**
     * Returns the number of positive coefficients in the left side of the constraint.
     */
    public int getNumberOfPositiveCoefficients() {
        int count = 0;
        for (Summand s : leftSide) {
            if (s.getCoeff() > 0)
                count++;
        }
        return count;
    }

    /**
     * Returns the number of negative coefficients in the left side of the constraint.
     */
    public int getNumberOfNegativeCoefficients() {
        int count = 0;
        for (Summand s : leftSide) {
            if (s.getCoeff() < 0)
                count++;
        }
        return count;
    }

    /**
     * Returns summands with positive coefficients.
     */
    public Summand[] getLeftSideWithPositiveCoefficients() {
        Summand[] positiveLeftSide = new Summand[getNumberOfPositiveCoefficients()];
        int positiveIndex = 0;
        for (Summand s : leftSide) {
            if (s.getCoeff() > 0) {
                positiveLeftSide[positiveIndex] = s;
                positiveIndex++;
            }
        }
        return positiveLeftSide;
    }

    /**
     * Returns summands with negative coefficients.
     */
    public Summand[] getLeftSideWithNegativeCoefficients() {
        Summand[] negativeLeftSide = new Summand[getNumberOfNegativeCoefficients()];
        int negativeIndex = 0;
        for (Summand s : leftSide) {
            if (s.getCoeff() < 0) {
                negativeLeftSide[negativeIndex] = s;
                negativeIndex++;
            }
        }
        return negativeLeftSide;
    }

    /**
     * Returns coefficients of the left side of the constraint in form of an array.
     */
    public double[] getLeftSideCoefficients() {
        double[] coefficients = new double[leftSide.length];
        for (int i = 0; i < leftSide.length; i++) {
            coefficients[i] = leftSide[i].getCoeff();
        }
        return coefficients;
    }

    /**
     * Gets the left side of the constraint.
     *
     * @return the summands on the left side of the constraint
     */
    public Summand[] getLeftSide() {
        return leftSide;
    }

    /**
     * Sets the summands on the left side of the constraint. The old summands
     * are NOT deleted.
     *
     * @param summands an array of Summand objects that make up the new left side
     */
    public void setLeftSide(Summand[] summands) {
        leftSide = summands;
    }

    /**
     * Checks to see whether a variable (v) has been appeared in the left side of
     * the constraint.
     */
    public boolean leftSideContains(Variable v) {
        for (Summand s : leftSide) {
            if (s.getVar() == v) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sum of the absolute value of all coefficients in the left side of the constraint.
     */
    public double sumOfAllAbsoluteCoefficients() {
        double sum = 0.0d;
        for (Summand s : leftSide) {
            sum += Math.abs(s.coeff);
        }

        return sum;
    }

    /**
     * Replaces left side of the constraint with a new constraint with
     * one summand.
     */
    public void setLeftSide(double coeff1, Variable var1) {
        for (int i = 0; i < leftSide.length; i++)
            leftSide[i].remove();
        leftSide = new Summand[1];
        leftSide[0] = new Summand(coeff1, var1);
        ls.getSolver().update(this);
    }

    /**
     * Replaces left side of the constraint with a new constraint with
     * two summands.
     */
    public void setLeftSide(double coeff1, Variable var1, double coeff2,
                            Variable var2) {
        for (int i = 0; i < leftSide.length; i++)
            leftSide[i].remove();
        leftSide = new Summand[2];
        leftSide[0] = new Summand(coeff1, var1);
        leftSide[1] = new Summand(coeff2, var2);
        ls.getSolver().update(this);
    }

    /**
     * Replaces left side of the constraint with a new constraint with
     * three summands.
     */
    public void setLeftSide(double coeff1, Variable var1, double coeff2,
                            Variable var2, double coeff3, Variable var3) {
        for (int i = 0; i < leftSide.length; i++)
            leftSide[i].remove();
        leftSide = new Summand[3];
        leftSide[0] = new Summand(coeff1, var1);
        leftSide[1] = new Summand(coeff2, var2);
        leftSide[2] = new Summand(coeff3, var3);
        ls.getSolver().update(this);
    }

    /**
     * Replaces left side of the constraint with a new constraint with
     * four summands.
     */
    public void setLeftSide(double coeff1, Variable var1, double coeff2,
                            Variable var2, double coeff3, Variable var3, double coeff4,
                            Variable var4) {
        for (int i = 0; i < leftSide.length; i++)
            leftSide[i].remove();
        leftSide = new Summand[4];
        leftSide[0] = new Summand(coeff1, var1);
        leftSide[1] = new Summand(coeff2, var2);
        leftSide[2] = new Summand(coeff3, var3);
        leftSide[3] = new Summand(coeff4, var4);
        ls.getSolver().update(this);
    }

    /**
     * Gets the operator used for this constraint.
     *
     * @return the operator used for this constraint
     */
    public OperatorType getOp() {
        return op;
    }

    /**
     * Sets the operator used for this constraint.
     *
     * @param value operator
     */
    public void setOp(OperatorType value) {
        op = value;
        ls.getSolver().update(this);
    }

    /**
     * Gets the constant value that is on the right side of the operator.
     *
     * @return the constant value that is on the right side of the operator
     */
    public double getRightSide() {
        return rightSide;
    }

    /**
     * Sets the constant value that is on the right side of the operator.
     *
     * @param value constant value that is on the right side of the operator
     */
    public void setRightSide(double value) {
        rightSide = value;
        ls.getSolver().update(this);
    }

    /**
     * Gets the coefficient of positive summand.
     *
     * @return the coefficient of positive summand.
     */
    public double getPenalty() {
        return penalty;
    }

    /**
     * The penalty coefficient for deviations from the soft constraint's exact
     * solution.
     *
     * @param value coefficient of penalty <code>double</code>
     */
    public void setPenalty(double value) {
        penalty = value;
        ls.getSolver().update(this);
    }

    /**
     * This is the termination criteria for Gauss-Seidel method
     */
    public boolean isSatisfied() {
        return Math.abs(residual()) < tolerance;
    }

    /**
     * Here we calculate residual for Linear relaxation.
     */
    public double residual() {
        double leftSideSum = 0;
        for (Summand summand : leftSide)
            leftSideSum += summand.coeff * summand.var.getValue();
        if (op == OperatorType.GE && leftSideSum >= rightSide)
            return 0;
        if (op == OperatorType.LE && leftSideSum <= rightSide)
            return 0;
        return rightSide - leftSideSum;
    }

    public double error() {
        return Math.abs(residual());
    }

    /**
     * Removes the constraint from its specification.
     */
    public void remove() {
        ls.getSolver().remove(this);
    }

    public void writeXML(OutputStreamWriter out) {
        try {
            out.write("\n");
            out.write("\t<constraint>\n");
            out.write("\t\t<leftside>\n");
            Summand[] summands = this.getLeftSide();
            for (Summand s : summands) {
                s.writeXML(out);
            }
            out.write("\t\t</leftside>\n");
            out.write("\t\t<op>" + this.getOp() + "</op>\n");
            out.write("\t\t<rightside>" + this.getRightSide()
                    + "</rightside>\n");
            out.write("\t\t<penaltypos>" + this.getPenalty()
                    + "</penaltypos>\n");
            out.write("\t</constraint>\n");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Checks the constraint to make sure the data are correct.
     * <p/>
     * NOTE: at the moment it only checks to make sure each variable appears only
     * once in a constraint. Duplicate variables causes JVM to crash with LpSolve.
     */
    private void checkConstraint() {
        for (int i = 0; i < leftSide.length - 1; i++) {
            for (int j = i + 1; j < leftSide.length; j++) {
                if (leftSide[i].getVar().toString().equals(leftSide[j].getVar().toString())) {
                    throw new RuntimeException("Variable '" + leftSide[i].getVar().toString() +
                            "' has been used twice in: " + toString());
                }
            }
        }
    }

    /**
     * Calculates the value for a variable in the constraint.
     */
    public double newVarValue(Summand chosenSummand) {
        double sumOfOtherSummands = 0;
        for (Summand summand : getLeftSide()) {

            // keep chosen summand on the left side
            if (chosenSummand == summand)
                continue;
            sumOfOtherSummands += summand.coeff * summand.var.getValue();
        }

        return (rightSide - sumOfOtherSummands) / chosenSummand.coeff;
    }

    /**
     * Checks whether a value is smaller than tolerance.
     */
    public static boolean equalZero(double x) {
        return Math.abs(x) < tolerance;
    }

    /**
     * Checks whether the difference of two values is smaller than tolerance.
     */
    public static boolean isEqual(double x, double y) {
        return equalZero(y - x);
    }

    /**
     * creates a clone of the constraint.
     */
    public Constraint clone() {
        try {
            return (Constraint) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns a list of variables in this constraint.
     */
    public List<Variable> getVariables() {
        List<Variable> variables = new ArrayList<Variable>();
        for (Summand s : leftSide)
            variables.add(s.var);
        return variables;
    }
}
