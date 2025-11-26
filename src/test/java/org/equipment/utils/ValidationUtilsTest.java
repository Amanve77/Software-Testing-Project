package org.equipment.utils;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ValidationUtilsTest {

    @Test
    public void normalizesNameAndPhone() {
        assertEquals("Jane Smith", ValidationUtils.normalizeName("  Jane Smith  "));
        assertEquals("123-456-7890", ValidationUtils.normalizePhone("123-456-7890"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsShortNames() {
        ValidationUtils.normalizeName("A");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNamesWithExactlyOneCharacter() {
        ValidationUtils.normalizeName("A");
    }

    @Test
    public void acceptsNamesWithExactlyTwoCharacters() {
        assertEquals("AB", ValidationUtils.normalizeName("AB"));
        assertEquals("AB", ValidationUtils.normalizeName("  AB  "));
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullName() {
        ValidationUtils.normalizeName(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsBadPhones() {
        ValidationUtils.normalizePhone("12345");
    }

    @Test(expected = IllegalArgumentException.class)
    public void rejectsNullPhone() {
        ValidationUtils.normalizePhone(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void requirePositiveIdRejectsZero() {
        ValidationUtils.requirePositiveId(0, "ID");
    }

    @Test(expected = IllegalArgumentException.class)
    public void requirePositiveIdRejectsNegative() {
        ValidationUtils.requirePositiveId(-1, "ID");
    }

    @Test
    public void requirePositiveIdAcceptsPositive() {
        ValidationUtils.requirePositiveId(1, "ID");
        ValidationUtils.requirePositiveId(100, "ID");
    }

    @Test(expected = IllegalArgumentException.class)
    public void requirePositiveIdRejectsZeroWithCustomLabel() {
        ValidationUtils.requirePositiveId(0, "Equipment ID");
    }
}



