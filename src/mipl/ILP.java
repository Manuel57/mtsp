package mipl;

import gurobi.GRBEnv;
import gurobi.GRBException;
import gurobi.GRBModel;

public class ILP {


    GRBEnv env = null;
    GRBModel model = null;

    public ILP(String logFileName) throws GRBException {
        this.env = new GRBEnv(logFileName);
        this.model=new GRBModel(this.env);
    }


}
