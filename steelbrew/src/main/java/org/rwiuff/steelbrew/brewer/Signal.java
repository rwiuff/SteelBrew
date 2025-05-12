package org.rwiuff.steelbrew.brewer;

public class Signal {

    private String name;
    private String declaration;

    public Signal(String name) {
        this.name = name;
        this.declaration = ("static unsigned char " + name + ";\n");
    }

    public String getName() {
        return name;
    }

    public String getDeclaration() {
        return declaration;
    }

}
