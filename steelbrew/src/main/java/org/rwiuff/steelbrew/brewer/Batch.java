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

    public void Assert(Signal signal, BigInteger value, int cycle, Operator op, Brewer brewer) {
        ArrayList<String> assertion = new ArrayList<>();
        String methodName = "assert" + signal.getName() + Integer.toString(value.intValue());
        String lhs = signal.getName();
        String rhs = Integer.toString(value.intValue());
        assertion.add("void " + methodName + "(V" + brewer.getDUT() + name + " *dut, vluint64_t &sim_time){\n");
        assertion.add("    static unsigned char " + lhs + " = 0;\n");
        assertion.add("    static unsigned char value = 0;\n");
        assertion.add("    if (sim_time >= " + cycle + ") {\n");
        assertion.add("         " + lhs + " = dut->" + lhs + ";\n");
        assertion.add("             if (!(" + lhs + " " + op.toString() + " " + rhs + ")) {\n");
        assertion.add(
                "             std::cout << \" Assertion error: " + lhs + " " + op.toString() + " " + rhs + ", \"\n");
        assertion.add("                 << \"" + lhs + " \" << (int)(" + lhs + ")\n");
        assertion.add("                 << \" Expected: " + op.toString() + rhs + " \"\n");
        assertion.add("                 << \" @ simtime: \" << sim_time << std::endl;\n");
        assertion.add("             }\n");
        assertion.add("        }\n");
        assertion.add("    }\n");
        assertions.put(methodName + "(dut, sim_time);", assertion);
    }

    public void Assert(Signal signal, Signal signal2, int cycle, Operator op, Brewer brewer) {
        /*
         * Assertion method
         * Checks if two signals adhere to a logical relationship by comparing them
         */
        ArrayList<String> assertion = new ArrayList<>();
        String methodName = "assert" + signal.getName() + signal2.getName();
        String lhs = signal.getName();
        String rhs = signal2.getName();
        assertion.add("void " + methodName + "(V" + brewer.getDUT() + name + " *dut, vluint64_t &sim_time){\n");
        assertion.add("    static unsigned char " + lhs + " = 0;\n");
        assertion.add("    static unsigned char " + rhs + " = 0;\n");
        assertion.add("    if (sim_time >= " + cycle + ") {\n");
        assertion.add("         " + rhs + " = dut->" + rhs + ";\n");
        assertion.add("         " + lhs + " = dut->" + lhs + ";\n");
        assertion.add("             if (!(" + lhs + " " + op.toString() + " " + rhs + ")) {\n");
        assertion.add(
                "             std::cout << \" Assertion error: " + lhs + " " + op.toString() + " " + rhs + ", \"\n");
        assertion.add("                 << \"" + lhs + " \" << (int)(" + lhs + ")\n");
        assertion.add("                 << \" " + rhs + " \" << (int)(" + rhs + ")\n");
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
