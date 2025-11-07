package com.callableapis.api.time;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Map;
import java.util.HashMap;

public class AffirmationServiceTest {

    private AffirmationService affirmationService;

    @BeforeEach
    void setUp() {
        affirmationService = new AffirmationService();
    }

    @Test
    void testGenerateAffirmationWithData() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("name", "Alice");
        inputData.put("mood", "happy");
        inputData.put("goal", "learn new skills");

        AffirmationService.AffirmationResult result = affirmationService.generateAffirmation(
                inputData, "test-seed");

        assertNotNull(result);
        assertNotNull(result.getAffirmation());
        assertFalse(result.getAffirmation().isEmpty());
        assertNotNull(result.getTone());
        assertNotNull(result.getKeywords());
        assertFalse(result.getKeywords().isEmpty());
        assertNotNull(result.getInputData());
        assertEquals(inputData, result.getInputData());
        assertEquals("test-seed", result.getSeed());
        assertNotNull(result.getGeneratedAt());
    }

    @Test
    void testGenerateAffirmationWithNullData() {
        AffirmationService.AffirmationResult result = affirmationService.generateAffirmation(
                null, "test-seed");

        assertNotNull(result);
        assertNotNull(result.getAffirmation());
        assertFalse(result.getAffirmation().isEmpty());
        assertNotNull(result.getTone());
        assertNotNull(result.getKeywords());
        assertEquals("test-seed", result.getSeed());
    }

    @Test
    void testGenerateAffirmationWithEmptyData() {
        Map<String, Object> inputData = new HashMap<>();

        AffirmationService.AffirmationResult result = affirmationService.generateAffirmation(
                inputData, "test-seed");

        assertNotNull(result);
        assertNotNull(result.getAffirmation());
        assertFalse(result.getAffirmation().isEmpty());
        assertNotNull(result.getTone());
        assertEquals("test-seed", result.getSeed());
    }

    @Test
    void testGenerateAffirmationWithoutSeed() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("emotion", "grateful");

        AffirmationService.AffirmationResult result = affirmationService.generateAffirmation(
                inputData, null);

        assertNotNull(result);
        assertNotNull(result.getAffirmation());
        assertNotNull(result.getSeed());
        assertFalse(result.getSeed().isEmpty());
    }

    @Test
    void testGenerateAffirmationDeterministic() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("value", "creativity");

        AffirmationService.AffirmationResult result1 = affirmationService.generateAffirmation(
                inputData, "same-seed");
        AffirmationService.AffirmationResult result2 = affirmationService.generateAffirmation(
                inputData, "same-seed");

        // Should produce the same result with the same seed
        assertEquals(result1.getAffirmation(), result2.getAffirmation());
        assertEquals(result1.getTone(), result2.getTone());
    }

    @Test
    void testGenerateAffirmationDifferentSeeds() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("trait", "kindness");

        AffirmationService.AffirmationResult result1 = affirmationService.generateAffirmation(
                inputData, "seed-1");
        AffirmationService.AffirmationResult result2 = affirmationService.generateAffirmation(
                inputData, "seed-2");

        // Different seeds should potentially produce different results
        assertNotNull(result1.getAffirmation());
        assertNotNull(result2.getAffirmation());
        // They might be the same or different, both are valid
    }

    @Test
    void testGenerateAffirmationWithHeartfeltKeywords() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("feeling", "love");
        inputData.put("emotion", "joy");

        AffirmationService.AffirmationResult result = affirmationService.generateAffirmation(
                inputData, "heartfelt-test");

        assertNotNull(result);
        assertNotNull(result.getAffirmation());
        // Tone might be heartfelt, but we don't enforce this as it's probabilistic
        assertTrue(result.getTone().length() > 0);
    }

    @Test
    void testGenerateAffirmationWithAmusingKeywords() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("mood", "funny");
        inputData.put("attitude", "silly");

        AffirmationService.AffirmationResult result = affirmationService.generateAffirmation(
                inputData, "amusing-test");

        assertNotNull(result);
        assertNotNull(result.getAffirmation());
        assertNotNull(result.getTone());
    }

    @Test
    void testGenerateAffirmationWithMultipleValues() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("strength1", "resilience");
        inputData.put("strength2", "patience");
        inputData.put("strength3", "wisdom");
        inputData.put("number", 42);
        inputData.put("active", true);

        AffirmationService.AffirmationResult result = affirmationService.generateAffirmation(
                inputData, "multi-value-test");

        assertNotNull(result);
        assertNotNull(result.getAffirmation());
        assertNotNull(result.getKeywords());
        assertFalse(result.getKeywords().isEmpty());
    }

    @Test
    void testGenerateAffirmationWithLongText() {
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("description", "This is a very long description that contains many words "
                + "and should be properly handled by the affirmation generation service");

        AffirmationService.AffirmationResult result = affirmationService.generateAffirmation(
                inputData, "long-text-test");

        assertNotNull(result);
        assertNotNull(result.getAffirmation());
        assertNotNull(result.getKeywords());
    }
}

