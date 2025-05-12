package org.rwiuff.steelbrew.brewer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import org.rwiuff.steelbrew.forge.Forge;

public class Brewer {
    private ArrayList<Testbench> testbenches = new ArrayList<>();
    private int clocks = 20;
    private String dut;

    public Brewer(String name) {
        System.out.println(name + " is brewing");
        this.dut = name;
        Forge.addBrewer(this);
    }

    public void grind() {
        testbenches.forEach(t -> createFile(t));
        testbenches.forEach(t -> write(t));
    }

    private void write(Testbench testbench) {
        testbench.populate();
        try {
            FileWriter writer = new FileWriter(testbench.getTBName());
            ArrayList<String> text = testbench.getLines();
            text.forEach(s -> {
                try {
                    writer.write(s);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFile(Testbench testbench) {
        File testBenchFile = new File(testbench.getTBName());
        try {
            if (testBenchFile.createNewFile())
                System.out.println("Testbench created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void runDUT() {
        Testbench run = new Testbench(dut, "run");
        run.add("    while (sim_time < MAX_SIM_TIME) {\n");
        run.add("        dut->clk ^= 1;\n");
        run.add("        dut->eval();\n");
        run.add("        if(dut->clk == 1){\n");
        run.add("            posedge_cnt++;\n");
        run.add("        }\n");
        run.add("        m_trace->dump(sim_time);\n");
        run.add("        sim_time++;\n");
        run.add("    }\n");
        run.add("\n");
        run.setClocks(clocks);
        testbenches.add(run);
    }

    public void clocks(int i) {
        this.clocks = i;
    }

    public String getDUT() {
        return dut;
    }

    public ArrayList<Testbench> getTestbenches() {
        return testbenches;
    }

    public void brew(Batch batch) {
        Testbench testbench = new Testbench(dut, batch.getName());
        ArrayList<String> steps = batch.getSteps();
        for (String step : steps) {
            testbench.add(step);
        }
        testbenches.add(testbench);
    }
}
