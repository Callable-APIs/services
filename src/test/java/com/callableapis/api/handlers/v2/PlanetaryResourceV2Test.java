package com.callableapis.api.handlers.v2;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import static org.junit.jupiter.api.Assertions.*;

public class PlanetaryResourceV2Test {

    private PlanetaryResourceV2 resource;

    @BeforeEach
    void setUp() {
        resource = new PlanetaryResourceV2();
    }

    @Test
    void testGetAvailablePlanets() {
        String[] planets = resource.getAvailablePlanets();

        assertNotNull(planets);
        assertEquals(9, planets.length);

        // Check that all expected planets are present
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
    void testGetAvailableSigns() {
        String[] signs = resource.getAvailableSigns();

        assertNotNull(signs);
        assertEquals(12, signs.length);

        // Check that all astrological signs are present
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
    void testGetAvailableConstellations() {
        String[] constellations = resource.getAvailableConstellations();

        assertNotNull(constellations);
        assertEquals(12, constellations.length);

        // Check that all astrological constellations are present
        assertTrue(containsConstellation(constellations, "Aries"));
        assertTrue(containsConstellation(constellations, "Taurus"));
        assertTrue(containsConstellation(constellations, "Gemini"));
        assertTrue(containsConstellation(constellations, "Cancer"));
        assertTrue(containsConstellation(constellations, "Leo"));
        assertTrue(containsConstellation(constellations, "Virgo"));
        assertTrue(containsConstellation(constellations, "Libra"));
        assertTrue(containsConstellation(constellations, "Scorpius"));
        assertTrue(containsConstellation(constellations, "Sagittarius"));
        assertTrue(containsConstellation(constellations, "Capricornus"));
        assertTrue(containsConstellation(constellations, "Aquarius"));
        assertTrue(containsConstellation(constellations, "Pisces"));
    }

    @Test
    void testGetPlanetaryPosition() {
        PlanetaryResourceV2.PlanetaryPositionRequest request = new PlanetaryResourceV2.PlanetaryPositionRequest();
        request.planet = "Mars";
        request.seed = "test-seed";

        PlanetaryResourceV2.PlanetaryPositionResponse response = resource.getPlanetaryPosition(request);

        assertNotNull(response);
        assertEquals("Mars", response.getPlanetName());
        assertTrue(response.getLongitudeDeg() >= 0 && response.getLongitudeDeg() < 360);
        assertTrue(response.getLatitudeDeg() >= -90 && response.getLatitudeDeg() <= 90);
        assertTrue(response.getDistanceAU() > 0);
        assertNotNull(response.getAstrologicalSign());
        assertNotNull(response.getAstrologicalSymbol());
        assertNotNull(response.getElement());
        assertNotNull(response.getQuality());
        assertTrue(response.getMagnitude() != 0);
        assertNotNull(response.getGeneratedAt());
        assertEquals("test-seed", response.getSeed());
    }

    @Test
    void testGetPlanetaryPositionInvalidPlanet() {
        PlanetaryResourceV2.PlanetaryPositionRequest request = new PlanetaryResourceV2.PlanetaryPositionRequest();
        request.planet = "InvalidPlanet";

        assertThrows(IllegalArgumentException.class, () -> {
            resource.getPlanetaryPosition(request);
        });
    }

    @Test
    void testGetPlanetaryPositionNullPlanet() {
        PlanetaryResourceV2.PlanetaryPositionRequest request = new PlanetaryResourceV2.PlanetaryPositionRequest();
        request.planet = null;

        assertThrows(IllegalArgumentException.class, () -> {
            resource.getPlanetaryPosition(request);
        });
    }

    @Test
    void testGetConstellationInfo() {
        PlanetaryResourceV2.ConstellationRequest request = new PlanetaryResourceV2.ConstellationRequest();
        request.constellation = "Aries";
        request.latitude = 40.0;
        request.longitude = -74.0;
        request.seed = "test-seed";

        PlanetaryResourceV2.ConstellationResponse response = resource.getConstellationInfo(request);

        assertNotNull(response);
        assertEquals("Aries", response.getName());
        assertEquals("Ari", response.getAbbreviation());
        assertEquals("Arietis", response.getGenitive());
        assertTrue(response.getRightAscensionHours() >= 0 && response.getRightAscensionHours() < 24);
        assertTrue(response.getDeclinationDeg() >= -90 && response.getDeclinationDeg() <= 90);
        assertTrue(response.getAreaSquareDegrees() > 0);
        assertNotNull(response.getBrightestStar());
        assertTrue(response.getMagnitude() != 0);
        assertNotNull(response.getGeneratedAt());
    }

    @Test
    void testGetConstellationInfoUnknownConstellation() {
        PlanetaryResourceV2.ConstellationRequest request = new PlanetaryResourceV2.ConstellationRequest();
        request.constellation = "UnknownConstellation";

        assertThrows(IllegalArgumentException.class, () -> {
            resource.getConstellationInfo(request);
        });
    }

    @Test
    void testGetConstellationInfoNullConstellation() {
        PlanetaryResourceV2.ConstellationRequest request = new PlanetaryResourceV2.ConstellationRequest();
        request.constellation = null;

        assertThrows(IllegalArgumentException.class, () -> {
            resource.getConstellationInfo(request);
        });
    }

    @Test
    void testGetCurrentPlanetaryPosition() {
        PlanetaryResourceV2.PlanetaryPositionResponse response = resource.getCurrentPlanetaryPosition("Mars");

        assertNotNull(response);
        assertEquals("Mars", response.getPlanetName());
        assertTrue(response.getLongitudeDeg() >= 0 && response.getLongitudeDeg() < 360);
        assertTrue(response.getLatitudeDeg() >= -90 && response.getLatitudeDeg() <= 90);
        assertTrue(response.getDistanceAU() > 0);
        assertNotNull(response.getAstrologicalSign());
    }

    @Test
    void testBaseDateTimeProperties() {
        PlanetaryResourceV2.BaseDateTime baseDateTime = new PlanetaryResourceV2.BaseDateTime();
        baseDateTime.year = 2024;
        baseDateTime.month = 1;
        baseDateTime.day = 1;
        baseDateTime.hour = 12;
        baseDateTime.minute = 0;
        baseDateTime.second = 0;
        baseDateTime.timezone = "UTC";

        assertEquals(Integer.valueOf(2024), baseDateTime.getYear());
        assertEquals(Integer.valueOf(1), baseDateTime.getMonth());
        assertEquals(Integer.valueOf(1), baseDateTime.getDay());
        assertEquals(Integer.valueOf(12), baseDateTime.getHour());
        assertEquals(Integer.valueOf(0), baseDateTime.getMinute());
        assertEquals(Integer.valueOf(0), baseDateTime.getSecond());
        assertEquals("UTC", baseDateTime.getTimezone());
    }

    @Test
    void testPlanetaryPositionRequestProperties() {
        PlanetaryResourceV2.PlanetaryPositionRequest request = new PlanetaryResourceV2.PlanetaryPositionRequest();
        request.planet = "Mars";
        request.seed = "test-seed";

        assertEquals("Mars", request.getPlanet());
        assertEquals("test-seed", request.getSeed());
    }

    @Test
    void testConstellationRequestProperties() {
        PlanetaryResourceV2.ConstellationRequest request = new PlanetaryResourceV2.ConstellationRequest();
        request.constellation = "Aries";
        request.latitude = 40.0;
        request.longitude = -74.0;
        request.seed = "test-seed";

        assertEquals("Aries", request.getConstellation());
        assertEquals(Double.valueOf(40.0), request.getLatitude());
        assertEquals(Double.valueOf(-74.0), request.getLongitude());
        assertEquals("test-seed", request.getSeed());
    }

    private boolean containsPlanet(String[] planets, String name) {
        for (String planet : planets) {
            if (planet.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsSign(String[] signs, String name) {
        for (String sign : signs) {
            if (sign.equals(name)) {
                return true;
            }
        }
        return false;
    }

    private boolean containsConstellation(String[] constellations, String name) {
        for (String constellation : constellations) {
            if (constellation.equals(name)) {
                return true;
            }
        }
        return false;
    }
}
