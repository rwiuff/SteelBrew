package org.rwiuff.steelbrew;

import java.math.BigInteger;

import org.rwiuff.steelbrew.brewer.Batch;
import org.rwiuff.steelbrew.brewer.Brewer;
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
        Signal signal = new Signal("b_in");
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
        alu2.brew(batch);
        Forge.simulate();
        // steelBrew.cleanObj();
        // steelBrew.cleanAux();
        // steelBrew.clean();
        // steelBrew.cleanTestbench();
        // steelBrew.cleanObj();
        // steelBrew.cleanWaveform();
    }
}
