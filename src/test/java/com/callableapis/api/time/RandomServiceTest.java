package com.callableapis.api.time;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class RandomServiceTest {

    private RandomService randomService;

    @BeforeEach
    void setUp() {
        randomService = new RandomService();
    }

    @Test
    void testGenerateRandomNumbers() {
        RandomService.RandomNumberResult result = randomService.generateRandomNumbers(
            RandomService.RandomType.TRULY_UNIFORM, 10, 0.0, 1.0, "test-seed");
        
        assertNotNull(result);
        assertEquals("truly_uniform", result.getType());
        assertEquals(10, result.getCount());
        assertEquals(0.0, result.getMin());
        assertEquals(1.0, result.getMax());
        assertEquals(10, result.getValues().size());
        assertTrue(result.getGenerationTimeMs() >= 0);
        assertEquals("test-seed", result.getSeed());
        
        // Check that all values are within range
        for (Double value : result.getValues()) {
            assertTrue(value >= 0.0 && value <= 1.0);
        }
    }

    @Test
    void testGenerateRandomIntegers() {
        RandomService.RandomIntegerResult result = randomService.generateRandomIntegers(
            RandomService.RandomType.CHEAP, 5, 1, 10, "test-seed");
        
        assertNotNull(result);
        assertEquals("cheap", result.getType());
        assertEquals(5, result.getCount());
        assertEquals(1, result.getMin());
        assertEquals(10, result.getMax());
        assertEquals(5, result.getValues().size());
        assertTrue(result.getGenerationTimeMs() >= 0);
        assertEquals("test-seed", result.getSeed());
        
        // Check that all values are within range
        for (Integer value : result.getValues()) {
            assertTrue(value >= 1 && value <= 10);
        }
    }

    @Test
    void testGenerateRandomBoolean() {
        boolean result = randomService.generateRandomBoolean(
            RandomService.RandomType.TRULY_RANDOM, "test-seed");
        
        // Boolean can be true or false, just verify it's a valid boolean
        assertNotNull(result);
    }

    @Test
    void testGenerateRandomChoice() {
        List<String> options = List.of("option1", "option2", "option3");
        
        String result = randomService.generateRandomChoice(
            RandomService.RandomType.TRULY_UNIFORM, options, "test-seed");
        
        assertNotNull(result);
        assertTrue(options.contains(result));
    }

    @Test
    void testGenerateRandomChoiceEmptyList() {
        List<String> emptyList = List.of();
        
        assertThrows(IllegalArgumentException.class, () -> {
            randomService.generateRandomChoice(
                RandomService.RandomType.TRULY_UNIFORM, emptyList, "test-seed");
        });
    }

    @Test
    void testGenerateRandomChoiceNullList() {
        assertThrows(IllegalArgumentException.class, () -> {
            randomService.generateRandomChoice(
                RandomService.RandomType.TRULY_UNIFORM, null, "test-seed");
        });
    }

    @Test
    void testShuffleList() {
        List<String> originalList = List.of("a", "b", "c", "d", "e");
        List<String> shuffledList = randomService.shuffleList(
            RandomService.RandomType.TRULY_UNIFORM, originalList, "test-seed");
        
        assertNotNull(shuffledList);
        assertEquals(originalList.size(), shuffledList.size());
        assertTrue(shuffledList.containsAll(originalList));
    }

    @Test
    void testShuffleListNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            randomService.shuffleList(RandomService.RandomType.TRULY_UNIFORM, null, "test-seed");
        });
    }

    @Test
    void testGenerateRandomString() {
        String result = randomService.generateRandomString(
            RandomService.RandomType.CHEAP, 10, "ABC123", "test-seed");
        
        assertNotNull(result);
        assertEquals(10, result.length());
        
        // Check that all characters are from the charset
        for (char c : result.toCharArray()) {
            assertTrue("ABC123".contains(String.valueOf(c)));
        }
    }

    @Test
    void testGenerateRandomStringDefaultCharset() {
        String result = randomService.generateRandomString(
            RandomService.RandomType.TRULY_UNIFORM, 8, null, "test-seed");
        
        assertNotNull(result);
        assertEquals(8, result.length());
    }

    @Test
    void testGenerateRandomUuid() {
        String result = randomService.generateRandomUuid(
            RandomService.RandomType.TRULY_RANDOM, "test-seed");
        
        assertNotNull(result);
        assertTrue(result.matches("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
    }

    @Test
    void testGetAvailableTypes() {
        List<RandomService.RandomType> types = randomService.getAvailableTypes();
        
        assertNotNull(types);
        assertEquals(3, types.size());
        assertTrue(types.contains(RandomService.RandomType.TRULY_RANDOM));
        assertTrue(types.contains(RandomService.RandomType.TRULY_UNIFORM));
        assertTrue(types.contains(RandomService.RandomType.CHEAP));
    }

    @Test
    void testIsValidRandomType() {
        assertTrue(randomService.isValidRandomType("truly_random"));
        assertTrue(randomService.isValidRandomType("truly_uniform"));
        assertTrue(randomService.isValidRandomType("cheap"));
        assertFalse(randomService.isValidRandomType("invalid"));
        assertFalse(randomService.isValidRandomType(null));
    }

    @Test
    void testGetRandomTypeByCode() {
        assertEquals(RandomService.RandomType.TRULY_RANDOM, 
                    randomService.getRandomTypeByCode("truly_random"));
        assertEquals(RandomService.RandomType.TRULY_UNIFORM, 
                    randomService.getRandomTypeByCode("truly_uniform"));
        assertEquals(RandomService.RandomType.CHEAP, 
                    randomService.getRandomTypeByCode("cheap"));
    }

    @Test
    void testGetRandomTypeByCodeInvalid() {
        assertThrows(IllegalArgumentException.class, () -> {
            randomService.getRandomTypeByCode("invalid");
        });
    }

    @Test
    void testRandomTypeEnumProperties() {
        RandomService.RandomType trulyRandom = RandomService.RandomType.TRULY_RANDOM;
        assertEquals("truly_random", trulyRandom.getCode());
        assertEquals("Uses external entropy sources for maximum randomness", trulyRandom.getDescription());
    }

    @Test
    void testGenerateRandomNumbersInvalidRange() {
        assertThrows(IllegalArgumentException.class, () -> {
            randomService.generateRandomNumbers(
                RandomService.RandomType.TRULY_UNIFORM, 5, 10.0, 5.0, "test-seed");
        });
    }

    @Test
    void testGenerateRandomIntegersInvalidRange() {
        assertThrows(IllegalArgumentException.class, () -> {
            randomService.generateRandomIntegers(
                RandomService.RandomType.CHEAP, 5, 10, 5, "test-seed");
        });
    }

    @Test
    void testStatisticsCalculation() {
        RandomService.RandomNumberResult result = randomService.generateRandomNumbers(
            RandomService.RandomType.TRULY_UNIFORM, 1000, 0.0, 1.0, "test-seed");
        
        // Mean should be close to 0.5 for uniform distribution
        assertTrue(result.getMean() > 0.4 && result.getMean() < 0.6);
        
        // Standard deviation should be reasonable
        assertTrue(result.getStandardDeviation() > 0.1 && result.getStandardDeviation() < 0.5);
    }
}
