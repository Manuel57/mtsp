package lisa;

import gurobi.*;

public class GurobiTest {
    public static void main(String[] args) {
        System.out.println("dfdf");

        try {
            example4();
           // example5();
        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    private static void example4() throws GRBException {

        GRBEnv env = new GRBEnv("example4.log");
        GRBModel model = new GRBModel(env);
        double angebot[] = new double[]{900, 500, 1200};
        double nachfrage[] = new double[]{900, 1100, 600};
        double transportkosten[][] = new double[][]
                {
                        {37, 27, 24},
                        {14, 9, 14},
                        {11, 12, 17}
                };

        int numberOfOffers = angebot.length;
        int numberOfDemands = nachfrage.length;

        model.set(GRB.StringAttr.ModelName, "Transport");

        GRBVar[] open = new GRBVar[numberOfOffers];
        for (int p = 0; p < numberOfOffers; p++) {
            open[p] = model.addVar(0, 1, 0, GRB.BINARY, "Open" + p);
        }
        GRBVar[][] transport = new GRBVar[numberOfDemands][numberOfOffers];
        for (int v = 0; v < numberOfDemands; v++) {
            for (int p = 0; p < numberOfOffers; p++) {
                transport[v][p] = model.addVar(0, GRB.INFINITY, transportkosten[v][p], GRB.CONTINUOUS, "Transport von" + p + "nach" + v);

            }
        }
        model.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);

        for (int p = 0; p < numberOfOffers; p++) {
            GRBLinExpr numberp = new GRBLinExpr();
            for (int v = 0; v < numberOfDemands; v++) {
                numberp.addTerm(1.0, transport[v][p]);
            }

            GRBLinExpr limit = new GRBLinExpr();
            limit.addTerm(angebot[p], open[p]);
            model.addConstr(numberp, GRB.EQUAL, limit, "Angebot" + p);
        }

        for (int v = 0; v < numberOfDemands; v++) {
            GRBLinExpr numberd = new GRBLinExpr();
            for (int p = 0; p < numberOfOffers; p++) {
                numberd.addTerm(1.0, transport[v][p]);
            }
            model.addConstr(numberd, GRB.EQUAL, nachfrage[v], "Nachfrage" + v);


        }
        for (int p = 0; p < numberOfOffers; p++) {
            open[p].set(GRB.DoubleAttr.Start, 1.0);
        }

        model.optimize();
        model.write("example4.sol");
        model.dispose();
        env.dispose();


    }


    static void example5() throws GRBException {
        double[][] cap = new double[][] {
                {0,3,2,4,0,0,0,0},
                {0,0,1,0,5,0,0,0},
                {0,0,0,2,0,6,0,0},
                {0,0,0,0,0,8,1,0},
                {0,0,3,0,0,0,0,1},
                {0,0,0,0,0,0,0,5},
                {0,0,0,0,0,0,0,3},
                {0,0,0,0,0,0,0,0}
        };
        calculateOilPlan(8,1,8, cap);
    }

    static void calculateOilPlan(int n, int s, int t, double capacities[][]) throws GRBException {
        GRBEnv env = new GRBEnv("calculateOilPlan.log");
        GRBModel model = new GRBModel(env);

        model.set(GRB.StringAttr.ModelName, "oilproblem");

        GRBVar[][] transport = new GRBVar[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                transport[i][j] = model.addVar(0, capacities[i][j], 0, GRB.INTEGER, "Transport " + (i + 1) + "to" + (j + 1));
            }
        }

        GRBLinExpr expr = new GRBLinExpr();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                expr.addTerm(1, transport[i][j]);
            }
        }
        model.setObjective(expr, GRB.MAXIMIZE);

        GRBLinExpr expr1;
        GRBLinExpr expr2;

        for (int i = 0; i < n; i++) {
            if (i != s - 1 && i != t - 1) {
                expr1 = new GRBLinExpr();
                expr2 = new GRBLinExpr();
                for (int j = 0; j < n; j++) {
                    expr1.addTerm(1, transport[i][j]);
                    expr2.addTerm(1, transport[j][i]);
                }
                model.addConstr(expr1, GRB.EQUAL, expr2, "equality" + i);
            }
        }

        model.optimize();

        int sum=0;
        for(int i =0; i < n;i++) {
            sum+= transport[i][t-1].get(GRB.DoubleAttr.X);
        }
        System.out.println(sum + " liters oil transported");
        model.write("calculateOilPlan.sol");
        model.dispose();
        env.dispose();
    }

}
