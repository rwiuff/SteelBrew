package org.rwiuff.steelbrew;

import java.io.IOException;
import java.nio.file.*;
import java.io.File;
import java.util.Comparator;

import org.rwiuff.steelbrew.forge.Forge;

public class SteelBrew {

    public SteelBrew() {
        System.out.println("+---------------------------------------------------------------------------------------------------------------+");
        System.out.println("|                                             Welcome To SteelBrew                                              |");
        System.out.println("|                                      Servering up your verification needs                                     |");
        System.out.println("+---------------------------------------------------------------------------------------------------------------+");
        Forge.getInstance(); // Instantiate the Forge singleton
    }

    public void clean() { // Clean all objects created during simulations
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
        File workingDirectory = new File(System.getProperty("user.dir"));
        if(workingDirectory.isDirectory()){
            File[] files = workingDirectory.listFiles((dir, name)-> name.endsWith(".cpp")|| name.endsWith(".vcd")||name.endsWith(".verilate"));
            if(files != null){
                for (File file: files){
                    if(file.delete()){
                        System.out.println("Deleted: " + file.getName());
                    }
                }
            }
        }
        File makefile = new File("Makefile");
        if (makefile.delete())
            System.out.println("Makefile deleted");
    }

    public void burn(){
        Forge.reset();
    }
}
