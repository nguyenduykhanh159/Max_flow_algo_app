package solver;

public class NetworkFlowSolverContext {
    public static NetworkFlowSolver solver;
    public NetworkFlowSolverContext() {

    }

    public static NetworkFlowSolver getSolver() {
        return solver;
    }

    public static void setSolver(NetworkFlowSolver solver) {
        NetworkFlowSolverContext.solver = solver;
    }
}
