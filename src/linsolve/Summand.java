package linsolve;

import alm.XTab;
import alm.YTab;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.DecimalFormat;

/**
 * A summand of the objective function.
 */
public class Summand {

    // TODO remove unused attributes
    // TODO implement information hiding
    public double lastValue;
    public double lastButOne;
    double coeff;
    Variable var;

    /**
     * Gets the summmand's coefficient.
     *
     * @return the summand's coefficient
     */
    public double getCoeff() {
        return coeff;
    }

    /**
     * Sets the summmand's coefficient.
     *
     * @param value coefficient
     */
    public void setCoeff(double value) {
        coeff = value;
    }

    public String toString() {
        DecimalFormat twoDForm = new DecimalFormat("#.##");

        return twoDForm.format(coeff) + "(" + var + ")";
    }

    public String toCommand() {
        DecimalFormat twoDForm = new DecimalFormat("#.##");

        return twoDForm.format(coeff) + ", " + var;
    }

    /**
     * Gets the summand's variable.
     *
     * @return the summand's variable
     */
    public Variable getVar() {
        return var;
    }

    /**
     * Sets the summand's variable.
     *
     * @param value variable
     */
    public void setVar(Variable value) {
        var = value;
    }

    public Summand(double coeff, Variable var) {
        this.coeff = coeff;
        this.var = var;
    }

    /**
     * Removes the summand from the objective function.
     */
    public void remove() {
    }

    public void writeXML(OutputStreamWriter out) {
        try {
            out.write("\t\t\t<summand>\n");
            out.write("\t\t\t\t<coeff>" + this.getCoeff() + "</coeff>\n");
            out.write("\t\t\t\t<var>" + this.getVar() + "</var>\n");
            if (this.getVar() instanceof XTab) {
                out.write("\t\t\t\t<type>X-Tab</type>\n");
            } else if (this.getVar() instanceof YTab) {
                out.write("\t\t\t\t<type>Y-Tab</type>\n");
            } else {
                out.write("\t\t\t\t<type>Var</type>\n");
            }
            out.write("\t\t\t</summand>\n");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
