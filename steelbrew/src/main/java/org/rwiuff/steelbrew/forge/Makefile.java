package org.rwiuff.steelbrew.forge;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

import org.rwiuff.steelbrew.brewer.Brewer;

public class Makefile {

    private HashMap<String, ArrayList<String>> menu = new HashMap<>(); // Map of all the tests indexed by DUT

    public Makefile(ArrayList<Brewer> brewers) { // Method that populates the menu
        for (Brewer brewer : brewers) {
            ArrayList<String> tests = new ArrayList<>();
            brewer.getTestbenches().forEach(t -> tests.add(t.getName()));
            menu.put(brewer.getDUT(), tests);
        }
        createFile(); // Create makefile
        write(); // Write recipes for all the tests
    }

    private void write() {
        try {
            FileWriter writer = new FileWriter("Makefile");
            Set<String> duts = menu.keySet();
            for (String dut : duts) {
                ArrayList<String> tests = menu.get(dut);
                for (String test : tests) {
                    writer.write(".PHONY: " + test + "\n");
                    writer.write("\n");
                    writer.write(test + ": waveform" + test + ".vcd\n");
                    writer.write("\n");
                }
                for (String test : tests) {
                    writer.write("waveform" + test + ".vcd: ./obj_dir/V" + test + "\n");
                    writer.write("\t@./obj_dir/V" + test + "\n");
                    writer.write("\n");
                    writer.write("./obj_dir/V" + test + ": .stamp." + test + ".verilate\n");
                    writer.write("\t@echo \"### Building executable ###\"\n");
                    writer.write("\tmake -C obj_dir -f V" + test + ".mk V" + test + "\n");
                    writer.write("\n");
                    writer.write(".stamp." + test + ".verilate: " + dut + ".sv " + "tb_" + test + ".cpp\n");
                    writer.write("\t@echo \"### VERILATING ###\"\n");
                    writer.write("\tverilator --trace -cc " + dut + ".sv --exe tb_" + test + ".cpp --prefix V"
                            + test + "\n");
                    writer.write("\t@touch .stamp." + test + ".verilate\n");
                    writer.write("\n");
                }
            }
            writer.write(".PHONY: clean\n");
            writer.write("clean:\n");
            writer.write("\trm -rf .stamp.*;\n");
            writer.write("\trm -rf ./obj_dir\n");
            writer.write("\trm -rf *.vcd\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createFile() {
        File makefile = new File("Makefile");
        try {
            if (makefile.createNewFile())
                System.out.println("Makefile created");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
