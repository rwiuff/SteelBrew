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
        // runDUT();
        // steelBrew.burn();
        // peekPokeStep();
        // steelBrew.burn();
        // expect();
        // steelBrew.burn();
        // Assert();
        // steelBrew.burn();
        // concurrency();
        // steelBrew.burn();
        coolDemo();
        // steelBrew.clean();
    }

    private static void coolDemo() {
        Forge.enableWSL(true);
        Brewer alu = new Brewer("alu");
        alu.clocks(20);
        Signal a_in = new Signal("a_in", 1);
        Signal b_in = new Signal("b_in", 1);
        Batch batch1 = new Batch("FirstTest");
        batch1.addSignal(a_in);
        batch1.addSignal(b_in);
        batch1.Assert(a_in, b_in, 1, Operator.neq, alu);
        batch1.peek(a_in);
        batch1.peek(b_in);
        batch1.poke(a_in, BigInteger.TWO);
        batch1.step();
        batch1.peek(a_in);

        Batch batch2 = new Batch("SecondTest");
        batch2.addSignal(a_in);
        batch2.addSignal(b_in);
        batch2.Assert(a_in, b_in, 1, Operator.neq, alu);
        batch2.peek(a_in);
        batch2.peek(b_in);
        batch2.poke(b_in, BigInteger.TWO);
        batch2.step();
        batch2.poke(a_in, BigInteger.TWO);
        batch2.peek(a_in);

        alu.brew(batch1);
        alu.brew(batch2);
        Forge.simulate();
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
