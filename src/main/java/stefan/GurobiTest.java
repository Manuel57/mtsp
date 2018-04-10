package stefan;

import gurobi.*;

public class GurobiTest {
    public static void main(String[] args) {
        try {
            example4();
        } catch (GRBException e) {
            e.printStackTrace();
        }
    }

    static void example4() throws GRBException {
        GRBEnv env = new GRBEnv("example.log");
        GRBModel model = new GRBModel(env);

        double Angebot[] = new double[]{900, 500, 1200};
        double Nachfrage[] = new double[]{900, 1100, 600};
        double Transportkosten[][] = new double[][]{
                {37, 27, 24},
                {14, 9, 14},
                {11, 12, 17}
        };
        int Angebotanzahl = Angebot.length;
        int Nachfrageanzahl = Nachfrage.length;

        model.set(GRB.StringAttr.ModelName, "Transport");

        GRBVar[] open = new GRBVar[Angebotanzahl];
        for (int i = 0; i < Angebotanzahl; i++) {
            open[i] = model.addVar(0, 1, 0, GRB.BINARY, "open" + i);
        }
        GRBVar[][] Transport = new GRBVar[Nachfrageanzahl][Angebotanzahl];
        for (int w = 0; w < Nachfrageanzahl; w++) {
            for (int p = 0; p < Angebotanzahl; p++) {
                Transport[w][p] = model.addVar(0, GRB.INFINITY, Transportkosten[w][p], GRB.CONTINUOUS, "Transport von" + p + "nach" + w);
            }
        }
        model.set(GRB.IntAttr.ModelSense, GRB.MINIMIZE);

        for (int p = 0; p < Angebotanzahl; p++) {
            GRBLinExpr ptot = new GRBLinExpr();
            for (int w = 0; w < Nachfrageanzahl; w++) {
                ptot.addTerm(1.0, Transport[w][p]);
            }
            GRBLinExpr Limit = new GRBLinExpr();
            Limit.addTerm(Angebot[p], open[p]);
            model.addConstr(ptot, GRB.EQUAL, Limit, "Angebot" + p);
        }
        for (int p = 0; p < Nachfrageanzahl; p++) {
            GRBLinExpr dtot = new GRBLinExpr();
            for (int w = 0; w < Angebotanzahl; w++) {
                dtot.addTerm(1.0, Transport[p][w]);
            }
            model.addConstr(dtot, GRB.EQUAL, Nachfrage[p], "Nachfrage" + p);
        }
        for (int p=0;p<Angebotanzahl;p++){
            open[p].set(GRB.DoubleAttr.Start,1.0);
        }
        model.optimize();
        model.write("example4.sol");
        model.dispose();
        env.dispose();
    }
}
