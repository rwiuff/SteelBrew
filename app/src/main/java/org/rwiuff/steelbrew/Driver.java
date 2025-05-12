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
        Forge.enableWSL(true);
        Brewer alu = new Brewer("alu");
        alu.clocks(40);
        alu.runDUT();
        Brewer alu2 = new Brewer("alu2");
        alu2.runDUT();
        Batch batch = new Batch("PeekPokeStep");
        batch.step();
        batch.step();
        Signal signal = new Signal("in_valid", 1);
        Signal signal2 = new Signal("out_valid", 1);
        batch.addSignal(signal);
        batch.peek(signal);
        batch.poke(signal, BigInteger.ONE);
        batch.step();
        batch.peek(signal);
        batch.step();
        batch.poke(signal, BigInteger.ZERO);
        batch.expect(signal, BigInteger.ONE);
        batch.step();
        batch.peek(signal);
        batch.step();
        Batch batch2 = new Batch("assertion");
        batch2.addSignal(signal);
        batch2.addSignal(signal2);
        batch2.Assert(signal, signal2, 1, Operator.neq, alu);
        alu2.brew(batch);
        alu.brew(batch2);
        Forge.simulate();
        steelBrew.clean();
    }
}
