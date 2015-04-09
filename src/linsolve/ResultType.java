package linsolve;

/**
 * The possible results of a solving attempt, mainly for the LpSolve solver.
 */
public enum ResultType {
    NOMEMORY(-2), ERROR(-1), OPTIMAL(0), SUBOPTIMAL(1), INFEASIBLE(2), UNBOUNDED(3),
    DEGENERATE(4), NUMFAILURE(5), USERABORT(6), TIMEOUT(7), PRESOLVED(9), PROCFAIL(10),
    PROCBREAK(11), FEASFOUND(12), NOFEASFOUND(13);

    @SuppressWarnings("unused")
    private int value;

    ResultType(int value) {
        this.value = value;
    }

    /**
     * Returns ResultType according to given value.
     */
    public static ResultType getResultType(int value) {
        switch (value) {
            case -2:
                return NOMEMORY;
            case -1:
                return ERROR;
            case 0:
                return OPTIMAL;
            case 1:
                return SUBOPTIMAL;
            case 2:
                return INFEASIBLE;
            case 3:
                return UNBOUNDED;
            case 4:
                return DEGENERATE;
            case 5:
                return NUMFAILURE;
            case 6:
                return USERABORT;
            case 7:
                return TIMEOUT;
            case 9:
                return PRESOLVED;
            case 10:
                return PROCFAIL;
            case 11:
                return PROCBREAK;
            case 12:
                return FEASFOUND;
            case 13:
                return NOFEASFOUND;
            default:
                return ERROR;
        }
    }
}
