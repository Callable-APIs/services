package com.callableapis.api.time;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

import java.time.ZonedDateTime;
import java.time.ZoneOffset;

public class PlanetaryServiceTest {

    private PlanetaryService planetaryService;

    @BeforeEach
    void setUp() {
        planetaryService = new PlanetaryService();
    }

    @Test
    void testCalculatePlanetPosition() {
        ZonedDateTime testDate = ZonedDateTime.of(2024, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        
        PlanetaryService.PlanetaryPosition position = planetaryService.calculatePlanetPosition(
            PlanetaryService.Planet.MARS, testDate);
        
        assertNotNull(position);
        assertEquals("Mars", position.getPlanetName());
        assertTrue(position.getLongitudeDeg() >= 0 && position.getLongitudeDeg() < 360);
        assertTrue(position.getLatitudeDeg() >= -90 && position.getLatitudeDeg() <= 90);
        assertTrue(position.getDistanceAU() > 0);
        assertNotNull(position.getAstrologicalSign());
    }

    @Test
    void testAstrologicalSignFromLongitude() {
        assertEquals(PlanetaryService.AstrologicalSign.ARIES, 
                    PlanetaryService.AstrologicalSign.fromLongitude(0.0));
        assertEquals(PlanetaryService.AstrologicalSign.ARIES, 
                    PlanetaryService.AstrologicalSign.fromLongitude(29.9));
        assertEquals(PlanetaryService.AstrologicalSign.TAURUS, 
                    PlanetaryService.AstrologicalSign.fromLongitude(30.0));
        assertEquals(PlanetaryService.AstrologicalSign.PISCES, 
                    PlanetaryService.AstrologicalSign.fromLongitude(359.9));
    }

    @Test
    void testGetConstellationInfo() {
        ZonedDateTime testDate = ZonedDateTime.of(2024, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        
        PlanetaryService.ConstellationInfo info = planetaryService.getConstellationInfo(
            "Aries", testDate, 40.0, -74.0);
        
        assertNotNull(info);
        assertEquals("Aries", info.getName());
        assertEquals("Ari", info.getAbbreviation());
        assertEquals("Arietis", info.getGenitive());
        assertTrue(info.getRightAscensionHours() >= 0 && info.getRightAscensionHours() < 24);
        assertTrue(info.getDeclinationDeg() >= -90 && info.getDeclinationDeg() <= 90);
    }

    @Test
    void testGetConstellationInfoUnknown() {
        ZonedDateTime testDate = ZonedDateTime.of(2024, 1, 1, 12, 0, 0, 0, ZoneOffset.UTC);
        
        PlanetaryService.ConstellationInfo info = planetaryService.getConstellationInfo(
            "UnknownConstellation", testDate, 40.0, -74.0);
        
        assertNull(info);
    }

    @Test
    void testPlanetEnumValues() {
        PlanetaryService.Planet[] planets = PlanetaryService.Planet.values();
        assertEquals(9, planets.length);
        
        assertTrue(containsPlanet(planets, "Mercury"));
        assertTrue(containsPlanet(planets, "Venus"));
        assertTrue(containsPlanet(planets, "Earth"));
        assertTrue(containsPlanet(planets, "Mars"));
        assertTrue(containsPlanet(planets, "Jupiter"));
        assertTrue(containsPlanet(planets, "Saturn"));
        assertTrue(containsPlanet(planets, "Uranus"));
        assertTrue(containsPlanet(planets, "Neptune"));
        assertTrue(containsPlanet(planets, "Pluto"));
    }

    @Test
    void testAstrologicalSignEnumValues() {
        PlanetaryService.AstrologicalSign[] signs = PlanetaryService.AstrologicalSign.values();
        assertEquals(12, signs.length);
        
        assertTrue(containsSign(signs, "Aries"));
        assertTrue(containsSign(signs, "Taurus"));
        assertTrue(containsSign(signs, "Gemini"));
        assertTrue(containsSign(signs, "Cancer"));
        assertTrue(containsSign(signs, "Leo"));
        assertTrue(containsSign(signs, "Virgo"));
        assertTrue(containsSign(signs, "Libra"));
        assertTrue(containsSign(signs, "Scorpio"));
        assertTrue(containsSign(signs, "Sagittarius"));
        assertTrue(containsSign(signs, "Capricorn"));
        assertTrue(containsSign(signs, "Aquarius"));
        assertTrue(containsSign(signs, "Pisces"));
    }

    @Test
    void testAstrologicalSignProperties() {
        PlanetaryService.AstrologicalSign aries = PlanetaryService.AstrologicalSign.ARIES;
        assertEquals("Aries", aries.getName());
        assertEquals("♈", aries.getSymbol());
        assertEquals("Fire", aries.getElement());
        assertEquals("Cardinal", aries.getQuality());
        assertEquals(0, aries.getStartDegrees());
        assertEquals(30, aries.getEndDegrees());
    }

    @Test
    void testPlanetProperties() {
        PlanetaryService.Planet mars = PlanetaryService.Planet.MARS;
        assertEquals("Mars", mars.getName());
        assertTrue(mars.getSemiMajorAxisAU() > 0);
        assertTrue(mars.getOrbitalPeriodDays() > 0);
        assertTrue(mars.getEccentricity() >= 0 && mars.getEccentricity() < 1);
    }

    private boolean containsPlanet(PlanetaryService.Planet[] planets, String name) {
        for (PlanetaryService.Planet planet : planets) {
            if (planet.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsSign(PlanetaryService.AstrologicalSign[] signs, String name) {
        for (PlanetaryService.AstrologicalSign sign : signs) {
            if (sign.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }
}
