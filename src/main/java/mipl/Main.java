package mipl;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;

/**
 * main class
 *
 * @author Manuel Lackenbucher
 **/
public class Main {
    public static void main(String[] args) {

        int node = 15;
        int base = 6;
        int width = 5;


        double[][] t_ij = new double[node][node];

        ArrayList<Integer> grid = new ArrayList<>();


        int xdist, ydist;
        for (Integer i = 0; i < node; i++)
            if (i != base)
                grid.add(i);

        for (int i = 0; i < node; i++) {
            for (int j = i + 1; j < node; j++) {
                xdist = j % width - i % width;
                ydist = Math.floorDiv(j, width) - Math.floorDiv(i, width);
                t_ij[i][j] = Math.sqrt(Math.pow(xdist, 2) + Math.pow(ydist, 2));
                t_ij[j][i] = t_ij[i][j];
            }
        }

        try {
            ILP ilp = new ILP(2, base, grid, t_ij, "ilp.log", (int) Math.ceil((double) node / 2), "result.sol");
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
