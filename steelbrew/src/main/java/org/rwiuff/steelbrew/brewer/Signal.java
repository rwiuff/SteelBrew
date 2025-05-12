package org.rwiuff.steelbrew.brewer;

public class Signal {

    private String name;
    private String declaration;
    private int size;

    public Signal(String name, int size) {
        this.name = name;
        this.size = size;
        this.declaration = ("static unsigned char " + name + ";\n");
    }

    public String getName() {
        return name;
    }

    public String getDeclaration() {
        return declaration;
    }

    public int getSize(){
        return size;
    }

}
