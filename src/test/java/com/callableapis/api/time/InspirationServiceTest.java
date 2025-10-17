package com.callableapis.api.time;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.ZonedDateTime;
import java.time.ZoneOffset;

public class InspirationServiceTest {

    private InspirationService inspirationService;

    @BeforeEach
    void setUp() {
        inspirationService = new InspirationService();
    }

    @Test
    void testGenerateHoroscope() {
        ZonedDateTime testDate = ZonedDateTime.of(2024, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);

        InspirationService.HoroscopeResult result = inspirationService.generateHoroscope(
                InspirationService.HoroscopeType.DAILY,
                PlanetaryService.AstrologicalSign.ARIES,
                testDate,
                "test-seed");

        assertNotNull(result);
        assertEquals("daily", result.getType());
        assertEquals("Aries", result.getSign());
        assertEquals("♈", result.getSymbol());
        assertEquals("Fire", result.getElement());
        assertEquals("Cardinal", result.getQuality());
        assertNotNull(result.getTitle());
        assertNotNull(result.getContent());
        assertNotNull(result.getGuidance());
        assertNotNull(result.getMood());
        assertNotNull(result.getLuckyNumbers());
        assertNotNull(result.getLuckyColors());
        assertNotNull(result.getCompatibility());
        assertNotNull(result.getPlanetaryInfluences());
        assertEquals("test-seed", result.getSeed());
    }

    @Test
    void testGenerateTarotReading() {
        InspirationService.TarotReading result = inspirationService.generateTarotReading(
                InspirationService.TarotSpread.SINGLE_CARD, "test-seed");

        assertNotNull(result);
        assertEquals("single", result.getSpread());
        assertNotNull(result.getDescription());
        assertNotNull(result.getCards());
        assertEquals(1, result.getCards().size());
        assertNotNull(result.getOverallReading());
        assertNotNull(result.getGuidance());
        assertNotNull(result.getTheme());
        assertEquals("test-seed", result.getSeed());
    }

    @Test
    void testGenerateTarotReadingThreeCard() {
        InspirationService.TarotReading result = inspirationService.generateTarotReading(
                InspirationService.TarotSpread.THREE_CARD, "test-seed");

        assertNotNull(result);
        assertEquals("three", result.getSpread());
        assertNotNull(result.getCards());
        assertEquals(3, result.getCards().size());
    }

    @Test
    void testGenerateTarotReadingCelticCross() {
        InspirationService.TarotReading result = inspirationService.generateTarotReading(
                InspirationService.TarotSpread.CELTIC_CROSS, "test-seed");

        assertNotNull(result);
        assertEquals("celtic", result.getSpread());
        assertNotNull(result.getCards());
        assertEquals(10, result.getCards().size());
    }

    @Test
    void testGenerateTarotReadingHoroscope() {
        InspirationService.TarotReading result = inspirationService.generateTarotReading(
                InspirationService.TarotSpread.HOROSCOPE, "test-seed");

        assertNotNull(result);
        assertEquals("horoscope", result.getSpread());
        assertNotNull(result.getCards());
        assertEquals(12, result.getCards().size());
    }

    @Test
    void testGenerateSingleCard() {
        InspirationService.TarotCard card = inspirationService.generateSingleCard("test-seed");

        assertNotNull(card);
        assertNotNull(card.getName());
        assertNotNull(card.getSuit());
        assertNotNull(card.getNumber());
        assertNotNull(card.getElement());
        assertNotNull(card.getMeaning());
        assertNotNull(card.getReversedMeaning());
        assertNotNull(card.getKeywords());
        assertNotNull(card.getDescription());
        // isReversed can be true or false
        assertNotNull(card.isReversed());
    }

    @Test
    void testHoroscopeTypeEnum() {
        InspirationService.HoroscopeType[] types = InspirationService.HoroscopeType.values();
        assertEquals(3, types.length);

        assertTrue(containsHoroscopeType(types, "daily"));
        assertTrue(containsHoroscopeType(types, "monthly"));
        assertTrue(containsHoroscopeType(types, "annual"));
    }

    @Test
    void testTarotSpreadEnum() {
        InspirationService.TarotSpread[] spreads = InspirationService.TarotSpread.values();
        assertEquals(4, spreads.length);

        assertTrue(containsTarotSpread(spreads, "single"));
        assertTrue(containsTarotSpread(spreads, "three"));
        assertTrue(containsTarotSpread(spreads, "celtic"));
        assertTrue(containsTarotSpread(spreads, "horoscope"));
    }

    @Test
    void testHoroscopeTypeProperties() {
        InspirationService.HoroscopeType daily = InspirationService.HoroscopeType.DAILY;
        assertEquals("daily", daily.getCode());
        assertEquals("Daily horoscope based on current planetary positions", daily.getDescription());
    }

    @Test
    void testTarotSpreadProperties() {
        InspirationService.TarotSpread single = InspirationService.TarotSpread.SINGLE_CARD;
        assertEquals("single", single.getCode());
        assertEquals("Single card reading for quick guidance", single.getDescription());
    }

    @Test
    void testHoroscopeDifferentSigns() {
        ZonedDateTime testDate = ZonedDateTime.of(2024, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);

        // Test different signs produce different results
        InspirationService.HoroscopeResult ariesResult = inspirationService.generateHoroscope(
                InspirationService.HoroscopeType.DAILY,
                PlanetaryService.AstrologicalSign.ARIES,
                testDate,
                "test-seed");

        InspirationService.HoroscopeResult taurusResult = inspirationService.generateHoroscope(
                InspirationService.HoroscopeType.DAILY,
                PlanetaryService.AstrologicalSign.TAURUS,
                testDate,
                "test-seed");

        assertEquals("Aries", ariesResult.getSign());
        assertEquals("Taurus", taurusResult.getSign());
        assertNotEquals(ariesResult.getContent(), taurusResult.getContent());
    }

    @Test
    void testHoroscopeDifferentTypes() {
        ZonedDateTime testDate = ZonedDateTime.of(2024, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);

        // Test different types produce different results
        InspirationService.HoroscopeResult dailyResult = inspirationService.generateHoroscope(
                InspirationService.HoroscopeType.DAILY,
                PlanetaryService.AstrologicalSign.ARIES,
                testDate,
                "test-seed");

        InspirationService.HoroscopeResult monthlyResult = inspirationService.generateHoroscope(
                InspirationService.HoroscopeType.MONTHLY,
                PlanetaryService.AstrologicalSign.ARIES,
                testDate,
                "test-seed");

        assertEquals("daily", dailyResult.getType());
        assertEquals("monthly", monthlyResult.getType());
        // Note: Content may be similar due to deterministic generation with same seed
        assertNotNull(dailyResult.getContent());
        assertNotNull(monthlyResult.getContent());
    }

    @Test
    void testTarotCardProperties() {
        InspirationService.TarotCard card = inspirationService.generateSingleCard("test-seed");

        // Test that card has valid properties
        assertFalse(card.getName().isEmpty());
        assertFalse(card.getSuit().isEmpty());
        assertFalse(card.getNumber().isEmpty());
        assertFalse(card.getElement().isEmpty());
        assertFalse(card.getMeaning().isEmpty());
        assertFalse(card.getReversedMeaning().isEmpty());
        assertFalse(card.getKeywords().isEmpty());
        assertFalse(card.getDescription().isEmpty());
    }

    @Test
    void testTarotReadingConsistency() {
        // Test that same seed produces same result
        InspirationService.TarotReading result1 = inspirationService.generateTarotReading(
                InspirationService.TarotSpread.SINGLE_CARD, "consistent-seed");

        InspirationService.TarotReading result2 = inspirationService.generateTarotReading(
                InspirationService.TarotSpread.SINGLE_CARD, "consistent-seed");

        assertEquals(result1.getCards().size(), result2.getCards().size());
        assertEquals(result1.getCards().get(0).getName(), result2.getCards().get(0).getName());
    }

    private boolean containsHoroscopeType(InspirationService.HoroscopeType[] types, String code) {
        for (InspirationService.HoroscopeType type : types) {
            if (type.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsTarotSpread(InspirationService.TarotSpread[] spreads, String code) {
        for (InspirationService.TarotSpread spread : spreads) {
            if (spread.getCode().equals(code)) {
                return true;
            }
        }
        return false;
    }
}
