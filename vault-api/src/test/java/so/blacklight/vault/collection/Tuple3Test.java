package so.blacklight.vault.collection;

import org.junit.Test;

import java.time.Instant;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class Tuple3Test {

    @Test
    public void firstMethodShouldReturnTheFirstElement() {
        final Tuple3<String, Long, Instant> tuple1 = new Tuple3<>("First", 1L, Instant.EPOCH);
        assertNotNull(tuple1.first());
        assertEquals("First", tuple1.first());
    }

    @Test
    public void secondShouldReturnTheSecondElement() {
        final Tuple3<String, Long, Instant> tuple2 = new Tuple3<>("Second", 2L, Instant.EPOCH);
        assertNotNull(tuple2.second());
        assertEquals((Long) 2L, tuple2.second());
    }

    @Test
    public void thirdShouldReturnTheThirdElement() {
        final Tuple3<String, Long, Instant> tuple3 = new Tuple3<>("Third", 3L, Instant.EPOCH);
        assertNotNull(tuple3.third());
        assertEquals(Instant.EPOCH, tuple3.third());
    }
}
