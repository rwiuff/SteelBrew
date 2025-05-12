package org.rwiuff.steelbrew.brewer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Batch {

    private String name;

    private ArrayList<String> steps = new ArrayList<>();
    private HashMap<String, Signal> signals = new HashMap<>();

    public Batch(String name) {
        this.name = name;
        step();
    }

    public void step() {
        steps.add("dut->clk ^= 1;\n");
        steps.add("dut->eval();\n");
        steps.add("m_trace->dump(sim_time);\n");
        steps.add("sim_time++;\n");
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getSteps() {
        return steps;
    }

    public void peek(Signal signal) {
        steps.add("std::cout << \"\\n Peek on " + signal.getName() + ": \" << (int)(dut->" + signal.getName()
                + ") << \" @ simtime: \" << sim_time << std::endl;\n");
    }

    public void poke(Signal signal, BigInteger one) {
        steps.add("dut->" + signal.getName() + " = " + one.intValue() + ";\n");
    }

    public void addSignal(Signal signal) {
        signals.put(signal.getName(), signal);
        steps.add(signal.getDeclaration());
    }

    public void expect(Signal signal, BigInteger one) {
        steps.add("std::cout << \"\\n Exptected " + one.intValue() + " on " + signal.getName()
                + ". Recieved \"  << (int)(dut->"
                + signal.getName() + ") << \" @ simtime: \" << sim_time << std::endl;\n");
    }

}
