package lisa;

import gurobi.*;

public class GurobiTest {
    public static void main(String[] args) {
        System.out.println("dfdf");

        try {
            example4();
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
}
