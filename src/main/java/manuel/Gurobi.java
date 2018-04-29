package manuel;

import gurobi.*;

public class Gurobi {
    public static void main(String[] args) {
        double capacity[][] = new double[][]{
                {0, 3, 2, 4, 0, 0, 0, 0},
                {0, 0, 1, 0, 5, 0, 0, 0},
                {0, 0, 0, 2, 0, 6, 0, 0},
                {0, 0, 0, 0, 0, 8, 1, 0},
                {0, 0, 3, 0, 0, 0, 0, 1},
                {0, 0, 0, 0, 0, 0, 0, 5},
                {0, 0, 0, 0, 0, 0, 0, 3},
                {0, 0, 0, 0, 0, 0, 0, 0}
        };
        try {
            calculateMaximumOilTransport(8, 1, 8, capacity, "oil.sol");
        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    /**
     * Calculates the maximum number of oil (in liters) which can be transported from s to t
     *
     * @param n              number of vertices
     * @param s              the source (0,...,n)
     * @param t              the sink (0,....n), != s
     * @param capacities     two dimensional array containing the capacities from vertex i to vertex j
     * @param resultFileName name of the file where the result should be saved
     * @throws GRBException
     */
    private static void calculateMaximumOilTransport(int n, int s, int t, double[][] capacities, String resultFileName) throws GRBException {
        GRBEnv env = new GRBEnv("oil.log");
        GRBModel model = new GRBModel(env);

        //set the name of the model
        model.set(GRB.StringAttr.ModelName, "optimize");


        //creates new variables
        //transport (transport[i][j]... amount of oil (in liters) to be transported from vertex i to vertex j)
        GRBVar[][] transport = new GRBVar[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                transport[i][j] =
                        model.addVar(0, capacities[i][j], 0, GRB.INTEGER,
                                "Trans" + (i + 1) + "." + (j + 1));
            }
        }

        //set objective
        GRBLinExpr expr = new GRBLinExpr();
        for (int j = 0; j < n; j++) {
            expr.addTerm(1, transport[s - 1][j]);
        }
        model.setObjective(expr, GRB.MAXIMIZE);

        GRBLinExpr exp1;
        GRBLinExpr exp2;

        // add constraints
        // amount of oil flowing in vertex i and flowing out vertex i has to be the same
        for (int i = 0; i < n; i++) {
            if (i != (s - 1) && i != (t - 1)) {
                exp1 = new GRBLinExpr();
                exp2 = new GRBLinExpr();
                for (int j = 0; j < n; j++) {
                    exp1.addTerm(1, transport[i][j]);
                }
                for (int j = 0; j < n; j++) {
                    exp2.addTerm(1, transport[j][i]);
                }
                model.addConstr(exp1, GRB.EQUAL, exp2, "equality_" + i);
            }
        }

        model.optimize();
        System.out.println();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                System.out.println("Transport from " + (i + 1) + " to " + (j + 1) + " - " + transport[i][j].get(GRB.DoubleAttr.X));
            }
        }

        model.write("solution5.sol");
        model.dispose();
        env.dispose();

    }
}
