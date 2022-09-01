package model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.Test;
import org.junit.platform.commons.annotation.Testable;

@Testable
public class LocationTest {
    @Test
    public void testGetRowGetCol(){
        Location location = new Location(3, 5);
        int expectedRow = 3;
        int expectedCol = 5;
        assertEquals(expectedRow, location.getRow());
        assertEquals(expectedCol, location.getCol());
    }

    @Test
    public void testEqualityFalse(){
        Location location = new Location(3, 5);
        Location location2 = new Location(3, 6);
        boolean expected = false;
        boolean result = location.equals(location2);
        assertEquals(expected, result);
    }

    @Test
    public void testEqualityTrue(){
        Location location = new Location(3, 5);
        Location location2 = new Location(3, 5);
        boolean expected = true;
        boolean result = location.equals(location2);
        assertEquals(expected, result);
    }

    @Test
    public void testHashCode(){
        Location location = new Location(3, 5);
        int expected = (3*1001)+(5*513);
        int result = location.hashCode();
        assertEquals(expected, result);
    }
}
