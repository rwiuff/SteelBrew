package org.rwiuff.steelbrew.brewer;

import java.util.ArrayList;

public class Testbench {
    private ArrayList<String> preamble = new ArrayList<>();
    private ArrayList<String> assertions = new ArrayList<>();
    private ArrayList<String> setup = new ArrayList<>();
    private ArrayList<String> loop = new ArrayList<>();
    private ArrayList<String> assertionChecks = new ArrayList<>();
    private ArrayList<String> tests = new ArrayList<>();
    private ArrayList<String> pool = new ArrayList<>();
    private String dut;
    private int clocks = 20;
    private String name;
    private String testName;

    public void setClocks(int clocks) {
        this.clocks = clocks;
    }

    public Testbench(String dut, String name) {
        this.dut = dut;
        this.name = name;
        this.testName = dut + name;
    }

    public Testbench(String dut, String name, int clocks) {
        this.dut = dut;
        this.name = name;
        this.testName = dut + name;
        this.clocks = clocks;
    }

    public void populate() { // Adds the necessary lines to make a testbench runnable
        preamble.add("#include <stdlib.h>\n");
        preamble.add("#include <iostream>\n");
        preamble.add("#include <verilated.h>\n");
        preamble.add("#include <verilated_vcd_c.h>\n");
        preamble.add("#include \"V" + testName + ".h\"\n");
        preamble.add("#include \"V" + testName + "___024unit.h\"\n");
        preamble.add("\n");
        preamble.add("#define MAX_SIM_TIME " + clocks + " \n");
        preamble.add("vluint64_t sim_time = 0;\n");
        preamble.add("vluint64_t posedge_cnt = 0;\n");
        preamble.add("\n");
        setup.add("int main(int argc, char** argv, char** env) {\n");
        setup.add("    V" + testName + " *dut = new V" + testName + ";\n");
        setup.add("\n");
        setup.add("    Verilated::traceEverOn(true);\n");
        setup.add("    VerilatedVcdC *m_trace = new VerilatedVcdC;\n");
        setup.add("    dut->trace(m_trace, 5);\n");
        setup.add("    m_trace->open(\"waveform" + testName + ".vcd\");\n");
        setup.add("\n");
        loop.add("    while (sim_time < MAX_SIM_TIME) {\n");
        loop.add("        dut->clk ^= 1;\n");
        loop.add("        dut->eval();\n");
        loop.add("        if(dut->clk == 1){\n");
        loop.add("            posedge_cnt++;\n");
        loop.add("        }\n");
        pool.add("        m_trace->dump(sim_time);\n");
        pool.add("        sim_time++;\n");
        pool.add("    }\n");
        pool.add("\n");
        pool.add("    m_trace->close();\n");
        pool.add("    dut->final();\n");
        pool.add("    delete dut;\n");
        pool.add("    exit(EXIT_SUCCESS);\n");
        pool.add("}\n");
    }

    public ArrayList<String> getLines() { // Returns lines the the brewer
        ArrayList<String> returnList = new ArrayList<>();
        returnList.addAll(preamble);
        if (!assertions.isEmpty())
            returnList.addAll(assertions);
        returnList.addAll(setup);
        returnList.addAll(loop);
        if (!assertionChecks.isEmpty())
            returnList.addAll(assertionChecks);
        returnList.addAll(tests);
        returnList.addAll(pool);
        return returnList;
    }

    public String getTBName() {
        return "tb_" + dut + name + ".cpp";
    }

    public void add(String string) {
        tests.addLast(string);
    }

    public String getName() {
        return testName;
    }

    public void addPreamble(String string) {
        setup.add(string);
    }

    public void addCheck(String string){
        assertionChecks.add(string);
    }

}
