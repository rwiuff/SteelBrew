package org.rwiuff.steelbrew.brewer;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.rwiuff.steelbrew.forge.Forge;

public class Brewer {
    private ArrayList<Testbench> testbenches = new ArrayList<>();
    private int clocks = 20;
    private String dut;

    public Brewer(String name) {
        System.out.println(name + " is brewing");
        this.dut = name;
        Forge.addBrewer(this); // Adds the brewer to the Forge's field
    }

    public void grind() { // Creates and fills out testbenches
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

    public void runDUT() { // Function to easily run a DUT without any tests
        Testbench run = new Testbench(dut, "run", clocks);
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
        testbenches.add(run);
    }

    public void clocks(int i) { // Set max number of cycles in simulation
        this.clocks = i;
    }

    public String getDUT() {
        return dut;
    }

    public ArrayList<Testbench> getTestbenches() {
        return testbenches;
    }

    public void brew(Batch batch) { // Adds batched tests to a testbench
        Testbench testbench = new Testbench(dut, batch.getName(), clocks);
        if (batch.assertions()) { // If there are assertions, adds the methods and method calls in a loop that
                                  // runs until max number of cycles
            HashMap<String, ArrayList<String>> assertions = batch.getAssertions();
            Set<String> functions = assertions.keySet();
            functions.forEach(f -> assertions.get(f).forEach(s -> testbench.addFunc(s)));
            testbench.add("    while (sim_time < MAX_SIM_TIME) {\n");
            testbench.add("        dut->clk ^= 1;\n");
            testbench.add("        dut->eval();\n");
            testbench.add("        if(dut->clk == 1){\n");
            testbench.add("            posedge_cnt++;\n");
            testbench.add("        }\n");
            functions.forEach(f -> testbench.add(f + "\n"));
            testbench.add("        m_trace->dump(sim_time);\n");
            testbench.add("        sim_time++;\n");
            testbench.add("    }\n");
            testbench.add("\n");
        }
        ArrayList<String> steps = batch.getSteps();
        for (String step : steps) {
            testbench.add(step);
        }
        testbenches.add(testbench);
    }
}
