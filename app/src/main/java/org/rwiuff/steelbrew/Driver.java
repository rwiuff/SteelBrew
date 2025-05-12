package org.rwiuff.steelbrew;

import java.math.BigInteger;

import org.rwiuff.steelbrew.brewer.Batch;
import org.rwiuff.steelbrew.brewer.Brewer;
import org.rwiuff.steelbrew.brewer.Operator;
import org.rwiuff.steelbrew.brewer.Signal;
import org.rwiuff.steelbrew.forge.Forge;

public class Driver {
    public static void main(String[] args) {
        // runDUT();
        // peekPokeStep();
        // expect();
        Assert();
    }

    public static void runDUT() {
        SteelBrew steelBrew = new SteelBrew();
        steelBrew.clean();
        Forge.enableWSL(true);
        Brewer alu = new Brewer("alu");
        alu.clocks(40);
        alu.runDUT();
        Forge.simulate();
    }

    public static void peekPokeStep() {
        SteelBrew steelBrew = new SteelBrew();
        steelBrew.clean();
        Forge.enableWSL(true);
        Brewer alu = new Brewer("alu");
        Batch batch = new Batch("PeekPokeStep");
        Signal signal = new Signal("in_valid", 1);
        batch.addSignal(signal);
        batch.peek(signal);
        batch.poke(signal, BigInteger.ONE);
        batch.step();
        batch.peek(signal);
        batch.step();
        batch.poke(signal, BigInteger.ZERO);
        batch.step();
        batch.peek(signal);
        batch.step();
        batch.step();
        alu.brew(batch);
        Forge.simulate();
    }

    private static void expect() {
        SteelBrew steelBrew = new SteelBrew();
        steelBrew.clean();
        Forge.enableWSL(true);
        Brewer alu = new Brewer("alu");
        Batch batch = new Batch("Expect");
        Signal signal = new Signal("in_valid", 1);
        batch.addSignal(signal);
        batch.step();
        batch.expect(signal, BigInteger.ONE);
        batch.peek(signal);
        batch.step();
        alu.brew(batch);
        Forge.simulate();
    }

    private static void Assert() {
        SteelBrew steelBrew = new SteelBrew();
        steelBrew.clean();
        Forge.enableWSL(true);
        Brewer alu = new Brewer("alu2");
        alu.clocks(40);
        Batch batch = new Batch("assertion");
        Signal signal = new Signal("in_valid", 1);
        Signal signal2 = new Signal("out_valid", 1);
        batch.Assert(signal, signal2, 3, Operator.neq, alu);
        alu.brew(batch);
        Forge.simulate();
    }
}
