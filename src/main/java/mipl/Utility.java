package mipl;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.util.ArrayList;


public class Utility {


    private static JSONObject readTours(File file) throws IOException, ParseException {
        JSONObject o = null;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String result = br.readLine();
            String json = result.split("=")[1];
            org.json.simple.parser.JSONParser parser = new org.json.simple.parser.JSONParser();
            o = (JSONObject) ((JSONObject) parser.parse(json)).get("Tours");
        }

        return o;
    }


    public static void convertResultFilesToCSV(String fileName) throws IOException, ParseException {
        File dir = new File("./");
        PrintWriter bw = new PrintWriter(new FileWriter("results.csv"));
        bw.println("grid;nDrones;base:method;tours");

        for (File f : dir.listFiles()) {
            if (f.getName().endsWith(".js") && !f.getName().equals("values.js")) {
                JSONObject o = readTours(f);

                JSONArray tours = ((JSONArray) o.get("Tours"));

                String m = f.getName().split("_")[6];

                int method = -1;
                if (m.equals("MAXIMUM"))
                    method = 1;
                else
                    method = 0;

                int base = Integer.parseInt(f.getName().split("_")[4]);//((JSONArray) tours.get(0)).get(0).toString());
                int width = Integer.parseInt(o.get("width").toString());
                int nNodes = (Integer.parseInt((String) o.get("numberOfPoints").toString()));

                bw.print((nNodes / width) + "x" + o.get("width"));
                bw.print(";" + tours.size() + ";" + base + ";" + method + ";" + tours.toJSONString());
                bw.println();
            }
        }
        bw.close();
    }

    public static void convertJsToLatex() throws IOException, ParseException {
        String[] colors = new String[]{"blue", "red", "green", "orange", "yellow"};
        File dir = new File("./");
        PrintWriter bw = new PrintWriter(new FileWriter("test.txt"));
        bw.println("\\documentclass{article}\n" +
                "\n" +
                "\\usepackage{times}\n" +
                "\\usepackage{ucs}\n" +
                "\\usepackage[utf8x]{inputenc}\n" +
                "\\usepackage[T1]{fontenc}\n" +
                "\\usepackage[german, english]{babel}\n" +
                "\\usepackage[sort,numbers]{natbib}\n" +
                "\\thispagestyle{empty}\n" +
                "\\usepackage{amsmath,amssymb,amsthm}\n" +
                "\\usepackage{comment}\n" +
                "\\usepackage{here}\n" +
                "\\usepackage{tikz}\n" +
                "\\usetikzlibrary{calc}\n" +
                "\\usetikzlibrary{decorations.pathmorphing,patterns}\n" +
                "\\usetikzlibrary{calc,patterns,decorations.markings}\n" +
                "\\usepackage{pdflscape}\n" +
                "\\usepackage{tikzscale}\n" +
                "\\usepackage{morefloats}\n" +
                "\\usepackage{filecontents}\n" +
                "\\setlength{\\bibsep}{0.0pt}\n" +
                "\\usepackage[textsize=tiny]{todonotes}\n" +
                "\n" +
                "\\usepackage{pgf}\n" +
                "\\usepackage{graphics}\n" +
                "\\usepackage[margin=1in]{geometry}\n" +
                "\\maxdeadcycles=1000\n" +
                "\\usetikzlibrary{arrows,automata}\n" +
                " %to enable backgrounds in tikz graphics -> edges do not overlay vertices\n" +
                "\\usetikzlibrary{backgrounds}\n" +
                "\\pgfdeclarelayer{myback}\n" +
                "\\pgfsetlayers{background,myback,main}\n" +
                " %to still have the possibility of exploiting the background layer\n" +
                "\n" +
                "\n" +
                "\\begin{document}");

        int cntFigures = 1;
        for (File f : dir.listFiles()) {
            if (f.getName().endsWith(".js") && !f.getName().equals("values.js")) {
                System.out.println(f.getName());
                JSONObject o = readTours(f);

                JSONArray tours = ((JSONArray) o.get("Tours"));


                String m = f.getName().split("_")[6];

                int method = -1;
                if (m.equals("MAXIMUM"))
                    method = 1;
                else
                    method = 0;

                int base = Integer.parseInt(f.getName().split("_")[4]);//((JSONArray) tours.get(0)).get(0).toString());
                int width = Integer.parseInt(o.get("width").toString());
                int nNodes = (Integer.parseInt((String) o.get("numberOfPoints").toString()));

                ArrayList<ArrayList<Integer>> pointsAtHeight = new ArrayList<>();
                bw.println("\\begin{figure}[htp]\n" +
                        "\\begin{center}\n" +
                        "\\resizebox{\\ifdim\\width>\\linewidth.9\\linewidth\\else\\width\\fi}{!}{\n" +
                        "\\begin{tikzpicture}[->,>=stealth',shorten >=1pt,auto,node distance=1.5cm,semithick]\n" +
                        "  \\tikzstyle{every circle}=[fill=white,fill opacity=.85,draw=black,text=black,text opacity=1]");


                bw.println("\\node[circle] (0) {0};");
                pointsAtHeight.add(new ArrayList<Integer>());
                for (int i = 1; i < nNodes; i++) {
                    pointsAtHeight.add(new ArrayList<Integer>());
                    if (i % width == 0) {
                        bw.println("\\node[circle] (" + i + ") [below of=" + (i - width) + "]{" + i + "};");
                    } else
                        bw.println("\\node[circle] (" + i + ") [right of=" + (i - 1) + "]{" + i + "};");
                }

                bw.println("\\begin{pgfonlayer}{myback}");
                int cnt = 0;
                for (Object obj : tours) {
                    JSONArray arr = ((JSONArray) obj);
                    bw.println("\\path[draw=" + colors[cnt] + ",thick,solid]");
                    for (int i = 0; i < arr.size() - 1; i++) {
                        int y = Math.floorDiv((Integer.parseInt(arr.get(i).toString())), width);
                        if (contains(pointsAtHeight.get(y), Integer.parseInt(arr.get(i).toString()), Integer.parseInt(arr.get(i + 1).toString()))) {
                            bw.format("(%d) edge [bend right=20] node {} (%d)%n", arr.get(i), arr.get(i + 1));
                        } else
                            bw.format("(%d) edge node {} (%d)%n", arr.get(i), arr.get(i + 1));
                        pointsAtHeight.get(y).add(Integer.parseInt(arr.get(i).toString()));
                    }
                    bw.println(";");
                    cnt++;
                }
                bw.println("  \\end{pgfonlayer}\\end{tikzpicture}\n" +
                        "}\n");
                bw.format("\\caption[drones503]{drones %d method %d vertices %d base %d }%n\\label{drones %d method %d vertices %d base %d}%n\\end{center}%n\\end{figure}%n%n", tours.size(), method, nNodes, base, tours.size(), method, nNodes, base);

                if (cntFigures % 35 == 0)
                    bw.println("\\clearpage");
                cntFigures++;
            }
        }
        bw.println("\\end{document}");

        bw.close();
    }

    private static void execute(int node, int base, int width, int nDrones, MilpMethod method, String jsFilename) {
		double[][] t_ij = new double[node][node];

        ArrayList<Integer> grid = new ArrayList<>();


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
            ILP ilp = new ILP(nDrones, base, grid, t_ij, "ilp.log", (int) Math.ceil((double) node / 2), "result.lp", method);
            ilp.solveILP();
            ilp.setResult();
            ilp.printResult(width);
            ilp.exportResult(width, jsFilename);
            //File htmlFile = new File("html/draw.html");
            //    Desktop.getDesktop().browse(htmlFile.toURI());
            ilp.dispose();
        } catch (Exception e) {
            e.printStackTrace();
        }
   }
	
    public static void executeMilp(int node, int base, int width, int nDrones, MilpMethod method) {
        execute(node, base, width, nDrones, method, "js_" + nDrones + "_" + node + "_" + width + "_" + base + "_" + method + ".js");
    }

    public static void executeMilp(int node, int base, int width, int nDrones, MilpMethod method, String jsFilename) {
        execute( node,  base,  width,  nDrones,  method,  jsFilename);
    }


    private static boolean contains(ArrayList<Integer> points, int point1, int point2) {
       /* for (Integer i : points) {
            if (point1 < i && i < point2)
                return true;
            if (point1 > i && i > point2)
                return true;
            if(point2 == i || point1 == i)
                return true;
        }*/
        return false;
    }
    
    
    public static void process(String inputFilename) throws Exception {
         ArrayList<ExecutionDetails> executions = readInputFile(inputFilename);
           MilpMethod m;
          for(ExecutionDetails ed : executions) {
              m = (ed.getMethod() == 1 ? MilpMethod.MINIMIZE_MAXIMUM_PATH_LENGTH : MilpMethod.MINIMIZE_TOTAL_PATH_LENGTH);
              executeMilp(ed.getNubferOfNodes(), ed.getBase(),ed.getWidth(), ed.getNubferOfDrones(), m, ed.generateJsFileName());
          }
    }
        
    
    
    public static ArrayList<ExecutionDetails> readInputFile(String inputFilename) throws Exception {
        ArrayList<ExecutionDetails> executions = new ArrayList<ExecutionDetails>();
        try (BufferedReader br = new BufferedReader(new FileReader(inputFilename))) {
           String currentLine;

            int base;
            int nDrones; 
            int nPoints;
            int method;
            int width;
            
			while ((currentLine = br.readLine()) != null) {
                String[] in = currentLine.split(",");
                nPoints = Integer.parseInt(in[0]);
                nDrones = Integer.parseInt(in[1]);
                base = Integer.parseInt(in[2]);
                width = Integer.parseInt(in[3]);
                method = Integer.parseInt(in[4]);
                executions.add(new ExecutionDetails(base,nDrones,nPoints,method,width));
			}
        }
        return executions;
    }
}
