package org.rwiuff.steelbrew;

import java.math.BigInteger;

import org.rwiuff.steelbrew.brewer.Batch;
import org.rwiuff.steelbrew.brewer.Brewer;
import org.rwiuff.steelbrew.brewer.Operator;
import org.rwiuff.steelbrew.brewer.Signal;
import org.rwiuff.steelbrew.forge.Forge;

public class Driver {
    public static void main(String[] args) {
        SteelBrew steelBrew = new SteelBrew();
        steelBrew.clean();
        runDUT();
        steelBrew.burn();
        peekPokeStep();
        steelBrew.burn();
        expect();
        steelBrew.burn();
        Assert();
        steelBrew.burn();
        concurrency();
        steelBrew.clean();
    }

    public static void runDUT() {
        Forge.enableWSL(true);
        Brewer alu = new Brewer("alu");
        alu.clocks(40);
        alu.runDUT();
        Forge.simulate();
    }

    public static void peekPokeStep() {
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
        Forge.enableWSL(true);
        Brewer alu = new Brewer("alu");
        Batch batch = new Batch("Expect");
        Signal signal = new Signal("in_valid", 1);
        batch.addSignal(signal);
        batch.step();
        batch.peek(signal);
        batch.expect(signal, BigInteger.ONE);
        batch.peek(signal);
        batch.step();
        alu.brew(batch);
        Forge.simulate();
    }

    private static void Assert() {
        Forge.enableWSL(true);
        Brewer alu = new Brewer("alu2");
        alu.clocks(40);
        Batch batch = new Batch("assertion");
        Signal signal = new Signal("in_valid", 1);
        Signal signal2 = new Signal("out_valid", 1);
        batch.peek(signal);
        batch.peek(signal2);
        batch.Assert(signal, signal2, 3, Operator.neq, alu);
        alu.brew(batch);
        Forge.simulate();
    }

    private static void concurrency() {
        Forge.enableWSL(true);
        Brewer alu = new Brewer("alu");
        Brewer alu2 = new Brewer("alu2");
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
        alu2.brew(batch);
        Forge.simulate();
    }
}
