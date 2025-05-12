package org.rwiuff.steelbrew;

import java.io.IOException;
import java.nio.file.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;

import org.rwiuff.steelbrew.brewer.Brewer;
import org.rwiuff.steelbrew.brewer.Testbench;
import org.rwiuff.steelbrew.forge.Forge;

public class SteelBrew {

    public SteelBrew() {
        System.out.println("+---------------------------------------------------------------------------------------------------------------+");
        System.out.println("|                                             Welcome To SteelBrew                                              |");
        System.out.println("|                                      Servering up your verification needs                                     |");
        System.out.println("+---------------------------------------------------------------------------------------------------------------+");
        Forge.getInstance();
    }

    public void clean() {
        cleanObj();
        cleanAux();
    }

    public void cleanObj() {
        Path dir = Paths.get("obj_dir");
        try {
            Files.walk(dir).sorted(Comparator.reverseOrder()).map(Path::toFile).forEach(File::delete);
        } catch (IOException e) {
            return;
        }
        System.out.println("obj_dir removed");
    }

    public void cleanAux() {
        ArrayList<Brewer> brewers = Forge.getBrewers();
        for (Brewer brewer : brewers) {
            ArrayList<Testbench> testbenches = brewer.getTestbenches();
            for (Testbench testbench : testbenches) {
                String testName = testbench.getName();
                File tb = new File("tb_" + testName + ".cpp");
                File wf = new File("waveform" + testName + ".vcd");
                File stamp = new File(".stamp." + testName + ".verilate");
                if (tb.delete())
                    System.out.println(tb + " deleted");
                if (wf.delete())
                    System.out.println(wf + " deleted");
                if (stamp.delete())
                    System.out.println(stamp + " deleted");
            }
        }
        File makefile = new File("Makefile");
        if (makefile.delete())
            System.out.println("Makefile deleted");
    }
}
