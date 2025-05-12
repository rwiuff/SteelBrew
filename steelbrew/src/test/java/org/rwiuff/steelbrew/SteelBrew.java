package org.rwiuff.steelbrew;

import org.junit.jupiter.api.Test;
import org.rwiuff.steelbrew.forge.Forge;
import org.junit.jupiter.api.Assertions;

class SteelBrew {
    @Test void testForgeConstructor() {
        Assertions.assertNotNull(Forge.getInstance());
    }
}
