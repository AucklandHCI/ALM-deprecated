package linsolve.pivots;

import linsolve.Constraint;
import linsolve.LinearSpec;
import linsolve.Summand;
import linsolve.Variable;

import java.util.Hashtable;
import java.util.List;
import java.util.Random;

/**
 * The random algorithm assigns the pivot variable for each constraint randomly in each iteration.
 * This means that in general the pivot assignment is changed in each iteration.
 *
 * @author njam031
 */
public class RandomPivotSummandSelector implements PivotSummandSelector {
    Random rand = new Random(System.currentTimeMillis());

    @Override
    public List<Constraint> init(LinearSpec linearSpec) {
        return linearSpec.getConstraints();
    }

    @Override
    public List<Constraint> init(List<Constraint> constraints, List<Variable> variables, int maxIndex) {
        for (Constraint c : constraints)
            selectPivotSummand(c);
        return constraints.subList(0, maxIndex);
    }

    @Override
    public List<Constraint> init(LinearSpec linearSpec, int maxIndex) {
        return init(linearSpec.getConstraints(), linearSpec.getVariables(), maxIndex);
    }

    @Override
    public List<Constraint> removeConstraint(List<Constraint> constraints,
                                             Constraint c) {
        return constraints;
    }

    @Override
    public Summand selectPivotSummand(Constraint constraint) {
        Summand chosenSummand = null;
        Summand[] summands = constraint.getLeftSide();

        // TODO Why drawing three times a random number?
        for (int t = 0; t < 3; t++) {
            int chosenSummandIndex = rand.nextInt(summands.length);
            chosenSummand = summands[chosenSummandIndex];
            constraint.setPivotSummand(chosenSummand);
            // choose summand if coefficient is sufficiently non-zero
            if (!Constraint.equalZero(chosenSummand.getCoeff()))
                break;
        }
        return chosenSummand;
    }

    Hashtable<Integer, Boolean> selectedConstraints = null;

    private int getEnabledConstraintsCount() {
        int count = 0;

        if (selectedConstraints == null)
            return count;

        for (int i = 0; i < selectedConstraints.size(); i++) {
            if (selectedConstraints.get(i))
                count++;
        }

        return count;
    }

    public int selectRandomConstraint(List<Constraint> constraints) {
        int size = constraints.size();

        // init the hashtable for keeping track of previosuly selected constraints
        if (selectedConstraints == null) {
            selectedConstraints = new Hashtable<Integer, Boolean>();
            for (int i = 0; i < size; i++)
                selectedConstraints.put(i, false);
        }

        int chosenConstraintIndex = rand.nextInt(size);
        while (true) {
            // all constraints are enabled
            if (getEnabledConstraintsCount() == size)
                break;

            // ignore previously selected constraints
            if (selectedConstraints.get(chosenConstraintIndex)) {
                chosenConstraintIndex = rand.nextInt(size);
                continue;
            }

            // enable a constraint and mark the index as selected
            else {
                selectedConstraints.put(chosenConstraintIndex, true);
                break;
            }
        }
        return chosenConstraintIndex;
    }
}
