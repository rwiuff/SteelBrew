package org.rwiuff.steelbrew.brewer;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;

public class Batch {

    private String name;

    private ArrayList<String> steps = new ArrayList<>();
    private HashMap<String, Signal> signals = new HashMap<>();
    private HashMap<String, ArrayList<String>> assertions = new HashMap<>();
    private int clock = 0;

    public Batch(String name) {
        this.name = name;
        // step();
    }

    public String getName() {
        return name;
    }

    public ArrayList<String> getSteps() {
        return steps;
    }

    public void step() { // Increments the clock by 1
        clock++;
    }

    public void peek(Signal signal) { // Outputs the signal at the given clock cycle
        steps.add("        if(sim_time == " + clock + "){\n");
        steps.add(
                "            std::cout << \"\\n Peek on " + signal.getName() + ": \" << (int)(dut->" + signal.getName()
                        + ") << \" @ simtime: \" << sim_time << std::endl;\n");
        steps.add("             }\n");
    }

    public void poke(Signal signal, BigInteger integer) { // Overwrites signal value
        steps.add("        if(sim_time == " + clock + "){\n");
        steps.add("             dut->" + signal.getName() + " = " + integer.intValue() + ";\n");
        steps.add("             std::cout << \"\\n Poke on " + signal.getName() + ": " + integer.intValue()
                + "\"<< \" @ simtime: \" << sim_time << std::endl;\n");
        steps.add("             }\n");
    }

    public void addSignal(Signal signal) { // Inserts signal as variable
        signals.put(signal.getName(), signal);
        steps.add(signal.getDeclaration());
    }

    public void expect(Signal signal, BigInteger integer) { // Listens for expected value on signal
        steps.add("        if(sim_time == " + clock + "){\n");
        steps.add("             std::cout << \"\\n Expected " + integer.intValue() + " on " + signal.getName()
                + ". Recieved \"  << (int)(dut->"
                + signal.getName() + ") << \" @ simtime: \" << sim_time << std::endl;\n");
        steps.add("             }\n");

    }

    public void Assert(Signal signal, Signal signal2, int cycle, Operator op, Brewer brewer) {
        /*
         * Assertion method
         * Checks if two signals adhere to a logical relationship by comparing them
         */
        ArrayList<String> assertion = new ArrayList<>();
        String methodName = "assert" + signal.getName() + signal2.getName();
        String in = signal.getName();
        String delay = signal.getName() + "_d";
        String exptected = signal2.getName() + "_expected";
        String out = signal2.getName();
        assertion.add("void " + methodName + "(V" + brewer.getDUT() + name + " *dut, vluint64_t &sim_time){\n");
        assertion.add("    static unsigned char " + in + " = 0;\n");
        assertion.add("    static unsigned char " + delay + " = 0;\n");
        assertion.add("    static unsigned char " + exptected + " = 0;\n");
        assertion.add("    if (sim_time >= " + cycle + ") {\n");
        assertion.add("         " + exptected + " = " + delay + ";\n");
        assertion.add("         " + delay + " = " + in + ";\n");
        assertion.add("         " + in + " = dut->" + in + ";\n");
        assertion.add("             if (" + exptected + " " + op.toString() + " dut->" + out + ") {\n");
        assertion.add("             std::cout << \" Assertion error: " + out + " mismatch, \"\n");
        assertion.add("                 << \"exp: \" << (int)(" + exptected + ")\n");
        assertion.add("                 << \" recv: \" << (int)(dut->" + out + ")\n");
        assertion.add("                 << \" @ simtime: \" << sim_time << std::endl;\n");
        assertion.add("             }\n");
        assertion.add("        }\n");
        assertion.add("    }\n");
        assertions.put(methodName + "(dut, sim_time);", assertion);
    }

    public HashMap<String, ArrayList<String>> getAssertions() {
        return assertions;
    }

    public boolean assertions() {
        return !(assertions.isEmpty());
    }

}
