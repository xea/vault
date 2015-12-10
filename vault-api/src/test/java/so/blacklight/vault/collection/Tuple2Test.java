package so.blacklight.vault.collection;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Tuple2Test {

    @Test
    public void firstMethodShouldReturnTheFirstElement() {
        final Tuple2<String, Long> tuple1 = new Tuple2<>("First", 1L);
        assertNotNull(tuple1.first());
        assertEquals("First", tuple1.first());
    }

    @Test
    public void secondShouldReturnTheSecondElement() {
        final Tuple2<String, Long> tuple2 = new Tuple2<>("Second", 2L);
        assertNotNull(tuple2.second());
        assertEquals((Long) 2L, tuple2.second());
    }
}
