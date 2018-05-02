package mipl;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

/**
 * Main class
 *
 * @author Manuel Lackenbucher
 **/
public class Main {
    public static void main(String[] args) {

        int node = 20;
        int base = 0;
        int width = 10;
        int nDrones = 2;
        MilpMethod method = MilpMethod.MINIMIZE_MAXIMUM_PATH_LENGTH;


        double[][] t_ij = new double[node][node];

        ArrayList<Integer> grid = new ArrayList<>();

        ä
        int xDist, yDist;
        for (Integer i = 0; i < node; i++)
            if (i != base)
                grid.add(i);

        for (int i = 0; i < node; i++) {
            for (int j = i + 1; j < node; j++) {
                xDist = j % width - i % width;
                yDist = Math.floorDiv(j, width) - Math.floorDiv(i, width);
                t_ij[i][j] = Math.sqrt(Math.pow(xDist, 2) + Math.pow(yDist, 2));
                t_ij[j][i] = t_ij[i][j];
            }
        }

        try {
            ILP ilp = new ILP(nDrones, base, grid, t_ij, "ilp.log", (int) Math.ceil((double) node / 2), "result.sol", method);
            ilp.solveILP();
            ilp.setResult();
            ilp.printResult(width);
            ilp.exportResult(width, "values.js");
            File htmlFile = new File("html/draw.html");
            Desktop.getDesktop().browse(htmlFile.toURI());
            System.out.println();
            ilp.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
