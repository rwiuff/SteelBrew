package org.rwiuff.steelbrew.brewer;

import java.util.ArrayList;

public class Testbench {
    private ArrayList<String> preamble = new ArrayList<>();
    private ArrayList<String> func = new ArrayList<>();
    private ArrayList<String> main = new ArrayList<>();
    private ArrayList<String> end = new ArrayList<>();
    private ArrayList<String> test = new ArrayList<>();
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
        main.add("int main(int argc, char** argv, char** env) {\n");
        main.add("    V" + testName + " *dut = new V" + testName + ";\n");
        main.add("\n");
        main.add("    Verilated::traceEverOn(true);\n");
        main.add("    VerilatedVcdC *m_trace = new VerilatedVcdC;\n");
        main.add("    dut->trace(m_trace, 5);\n");
        main.add("    m_trace->open(\"waveform" + testName + ".vcd\");\n");
        main.add("\n");
        end.add("    m_trace->close();\n");
        end.add("    dut->final();\n");
        end.add("    delete dut;\n");
        end.add("    exit(EXIT_SUCCESS);\n");
        end.add("}\n");
    }

    public ArrayList<String> getLines() { // Returns lines the the brewer
        ArrayList<String> returnList = new ArrayList<>();
        returnList.addAll(preamble);
        returnList.addAll(func);
        returnList.addAll(main);
        returnList.addAll(test);
        returnList.addAll(end);
        return returnList;
    }

    public String getTBName() {
        return "tb_" + dut + name + ".cpp";
    }

    public void add(String string) {
        test.add(string);
    }

    public String getName() {
        return testName;
    }

    public void addFunc(String string) {
        func.add(string);
    }
}
