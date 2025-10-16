package com.callableapis.api.time;

import java.time.ZonedDateTime;

/**
 * Service for calculating planetary positions and astrological data.
 * Provides positions of planets relative to Earth and astrological
 * constellation information.
 */
public class PlanetaryService {

    public enum Planet {
        MERCURY("Mercury", 0.387, 88.0, 0.2056, 7.0, 48.0),
        VENUS("Venus", 0.723, 224.7, 0.0067, 3.4, 76.0),
        EARTH("Earth", 1.0, 365.25, 0.0167, 0.0, 0.0),
        MARS("Mars", 1.524, 687.0, 0.0934, 1.9, 25.0),
        JUPITER("Jupiter", 5.203, 4331.0, 0.0484, 1.3, 3.1),
        SATURN("Saturn", 9.537, 10747.0, 0.0542, 2.5, 26.7),
        URANUS("Uranus", 19.191, 30589.0, 0.0472, 0.8, 97.8),
        NEPTUNE("Neptune", 30.069, 59800.0, 0.0086, 1.8, 28.3),
        PLUTO("Pluto", 39.482, 90560.0, 0.2488, 17.2, 119.6);

        private final String name;
        private final double semiMajorAxisAU; // Astronomical Units
        private final double orbitalPeriodDays;
        private final double eccentricity;
        private final double inclinationDeg;
        private final double longitudeOfAscendingNodeDeg;

        Planet(String name, double semiMajorAxisAU, double orbitalPeriodDays,
                double eccentricity, double inclinationDeg, double longitudeOfAscendingNodeDeg) {
            this.name = name;
            this.semiMajorAxisAU = semiMajorAxisAU;
            this.orbitalPeriodDays = orbitalPeriodDays;
            this.eccentricity = eccentricity;
            this.inclinationDeg = inclinationDeg;
            this.longitudeOfAscendingNodeDeg = longitudeOfAscendingNodeDeg;
        }

        public String getName() {
            return name;
        }

        public double getSemiMajorAxisAU() {
            return semiMajorAxisAU;
        }

        public double getOrbitalPeriodDays() {
            return orbitalPeriodDays;
        }

        public double getEccentricity() {
            return eccentricity;
        }

        public double getInclinationDeg() {
            return inclinationDeg;
        }

        public double getLongitudeOfAscendingNodeDeg() {
            return longitudeOfAscendingNodeDeg;
        }
    }

    public enum AstrologicalSign {
        ARIES("Aries", "♈", 0, 30, "Fire", "Cardinal"),
        TAURUS("Taurus", "♉", 30, 60, "Earth", "Fixed"),
        GEMINI("Gemini", "♊", 60, 90, "Air", "Mutable"),
        CANCER("Cancer", "♋", 90, 120, "Water", "Cardinal"),
        LEO("Leo", "♌", 120, 150, "Fire", "Fixed"),
        VIRGO("Virgo", "♍", 150, 180, "Earth", "Mutable"),
        LIBRA("Libra", "♎", 180, 210, "Air", "Cardinal"),
        SCORPIO("Scorpio", "♏", 210, 240, "Water", "Fixed"),
        SAGITTARIUS("Sagittarius", "♐", 240, 270, "Fire", "Mutable"),
        CAPRICORN("Capricorn", "♑", 270, 300, "Earth", "Cardinal"),
        AQUARIUS("Aquarius", "♒", 300, 330, "Air", "Fixed"),
        PISCES("Pisces", "♓", 330, 360, "Water", "Mutable");

        private final String name;
        private final String symbol;
        private final double startDegrees;
        private final double endDegrees;
        private final String element;
        private final String quality;

        AstrologicalSign(String name, String symbol, double startDegrees, double endDegrees,
                String element, String quality) {
            this.name = name;
            this.symbol = symbol;
            this.startDegrees = startDegrees;
            this.endDegrees = endDegrees;
            this.element = element;
            this.quality = quality;
        }

        public String getName() {
            return name;
        }

        public String getSymbol() {
            return symbol;
        }

        public double getStartDegrees() {
            return startDegrees;
        }

        public double getEndDegrees() {
            return endDegrees;
        }

        public String getElement() {
            return element;
        }

        public String getQuality() {
            return quality;
        }

        public static AstrologicalSign fromLongitude(double longitudeDeg) {
            double normalized = ((longitudeDeg % 360.0) + 360.0) % 360.0;
            for (AstrologicalSign sign : values()) {
                if (normalized >= sign.startDegrees && normalized < sign.endDegrees) {
                    return sign;
                }
            }
            return ARIES; // Fallback
        }
    }

    public static class PlanetaryPosition {
        public String planetName;
        public double longitudeDeg; // Ecliptic longitude
        public double latitudeDeg; // Ecliptic latitude
        public double distanceAU; // Distance from Sun in AU
        public double rightAscensionDeg; // Right ascension
        public double declinationDeg; // Declination
        public AstrologicalSign astrologicalSign;
        public double magnitude; // Apparent magnitude
        public boolean isRetrograde;
        public double elongationDeg; // Angular distance from Sun

        // Getters and setters
        public String getPlanetName() {
            return planetName;
        }

        public void setPlanetName(String planetName) {
            this.planetName = planetName;
        }

        public double getLongitudeDeg() {
            return longitudeDeg;
        }

        public void setLongitudeDeg(double longitudeDeg) {
            this.longitudeDeg = longitudeDeg;
        }

        public double getLatitudeDeg() {
            return latitudeDeg;
        }

        public void setLatitudeDeg(double latitudeDeg) {
            this.latitudeDeg = latitudeDeg;
        }

        public double getDistanceAU() {
            return distanceAU;
        }

        public void setDistanceAU(double distanceAU) {
            this.distanceAU = distanceAU;
        }

        public double getRightAscensionDeg() {
            return rightAscensionDeg;
        }

        public void setRightAscensionDeg(double rightAscensionDeg) {
            this.rightAscensionDeg = rightAscensionDeg;
        }

        public double getDeclinationDeg() {
            return declinationDeg;
        }

        public void setDeclinationDeg(double declinationDeg) {
            this.declinationDeg = declinationDeg;
        }

        public AstrologicalSign getAstrologicalSign() {
            return astrologicalSign;
        }

        public void setAstrologicalSign(AstrologicalSign astrologicalSign) {
            this.astrologicalSign = astrologicalSign;
        }

        public double getMagnitude() {
            return magnitude;
        }

        public void setMagnitude(double magnitude) {
            this.magnitude = magnitude;
        }

        public boolean isRetrograde() {
            return isRetrograde;
        }

        public void setRetrograde(boolean retrograde) {
            isRetrograde = retrograde;
        }

        public double getElongationDeg() {
            return elongationDeg;
        }

        public void setElongationDeg(double elongationDeg) {
            this.elongationDeg = elongationDeg;
        }
    }

    public static class ConstellationInfo {
        public String name;
        public String abbreviation;
        public String genitive;
        public double rightAscensionHours;
        public double declinationDeg;
        public double areaSquareDegrees;
        public String brightestStar;
        public double magnitude;
        public boolean isVisible;
        public double elevationDeg;
        public double azimuthDeg;

        // Getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAbbreviation() {
            return abbreviation;
        }

        public void setAbbreviation(String abbreviation) {
            this.abbreviation = abbreviation;
        }

        public String getGenitive() {
            return genitive;
        }

        public void setGenitive(String genitive) {
            this.genitive = genitive;
        }

        public double getRightAscensionHours() {
            return rightAscensionHours;
        }

        public void setRightAscensionHours(double rightAscensionHours) {
            this.rightAscensionHours = rightAscensionHours;
        }

        public double getDeclinationDeg() {
            return declinationDeg;
        }

        public void setDeclinationDeg(double declinationDeg) {
            this.declinationDeg = declinationDeg;
        }

        public double getAreaSquareDegrees() {
            return areaSquareDegrees;
        }

        public void setAreaSquareDegrees(double areaSquareDegrees) {
            this.areaSquareDegrees = areaSquareDegrees;
        }

        public String getBrightestStar() {
            return brightestStar;
        }

        public void setBrightestStar(String brightestStar) {
            this.brightestStar = brightestStar;
        }

        public double getMagnitude() {
            return magnitude;
        }

        public void setMagnitude(double magnitude) {
            this.magnitude = magnitude;
        }

        public boolean isVisible() {
            return isVisible;
        }

        public void setVisible(boolean visible) {
            isVisible = visible;
        }

        public double getElevationDeg() {
            return elevationDeg;
        }

        public void setElevationDeg(double elevationDeg) {
            this.elevationDeg = elevationDeg;
        }

        public double getAzimuthDeg() {
            return azimuthDeg;
        }

        public void setAzimuthDeg(double azimuthDeg) {
            this.azimuthDeg = azimuthDeg;
        }
    }

    /**
     * Calculate the position of a planet at a given time.
     * Uses simplified orbital mechanics for approximate positions.
     */
    public PlanetaryPosition calculatePlanetPosition(Planet planet, ZonedDateTime dateTimeUtc) {
        double jd = toJulianDay(dateTimeUtc);
        double daysSinceEpoch = jd - 2451545.0; // J2000.0 epoch

        PlanetaryPosition position = new PlanetaryPosition();
        position.planetName = planet.getName();

        // Calculate mean anomaly
        double meanAnomaly = calculateMeanAnomaly(planet, daysSinceEpoch);

        // Calculate true anomaly using Kepler's equation (simplified)
        double eccentricAnomaly = solveKeplersEquation(meanAnomaly, planet.getEccentricity());
        double trueAnomaly = 2.0 * Math.atan2(
                Math.sqrt(1.0 + planet.getEccentricity()) * Math.sin(eccentricAnomaly / 2.0),
                Math.sqrt(1.0 - planet.getEccentricity()) * Math.cos(eccentricAnomaly / 2.0));

        // Calculate distance from Sun
        double distance = planet.getSemiMajorAxisAU() * (1.0 - planet.getEccentricity() * planet.getEccentricity())
                / (1.0 + planet.getEccentricity() * Math.cos(trueAnomaly));
        position.distanceAU = distance;

        // Calculate ecliptic coordinates
        double longitude = Math.toDegrees(trueAnomaly) + planet.getLongitudeOfAscendingNodeDeg();
        position.longitudeDeg = normalizeDegrees(longitude);
        position.latitudeDeg = planet.getInclinationDeg()
                * Math.sin(Math.toRadians(longitude - planet.getLongitudeOfAscendingNodeDeg()));

        // Determine astrological sign
        position.astrologicalSign = AstrologicalSign.fromLongitude(position.longitudeDeg);

        // Calculate apparent magnitude (simplified)
        position.magnitude = calculateApparentMagnitude(planet, distance);

        // Calculate elongation from Sun (simplified)
        position.elongationDeg = calculateElongation(position.longitudeDeg, daysSinceEpoch);

        // Convert to equatorial coordinates
        double[] equatorial = eclipticToEquatorial(position.longitudeDeg, position.latitudeDeg);
        position.rightAscensionDeg = equatorial[0];
        position.declinationDeg = equatorial[1];

        // Determine if retrograde (simplified check)
        position.isRetrograde = isRetrograde(planet, daysSinceEpoch);

        return position;
    }

    /**
     * Get information about astrological constellations and their visibility.
     */
    public ConstellationInfo getConstellationInfo(String constellationName, ZonedDateTime dateTimeUtc,
            double latitudeDeg, double longitudeDeg) {
        // Simplified constellation data - in a real implementation, this would use a
        // comprehensive database
        ConstellationInfo info = getConstellationData(constellationName);
        if (info == null) {
            return null;
        }

        // Calculate visibility based on time and location
        calculateConstellationVisibility(info, dateTimeUtc, latitudeDeg, longitudeDeg);

        return info;
    }

    private double calculateMeanAnomaly(Planet planet, double daysSinceEpoch) {
        double meanMotion = 360.0 / planet.getOrbitalPeriodDays();
        return normalizeDegrees(meanMotion * daysSinceEpoch);
    }

    private double solveKeplersEquation(double meanAnomaly, double eccentricity) {
        // Newton-Raphson method to solve Kepler's equation
        double eccentricAnomaly = Math.toRadians(meanAnomaly);
        double delta = 1.0;
        int maxIterations = 10;

        for (int i = 0; i < maxIterations && Math.abs(delta) > 1e-6; i++) {
            delta = (eccentricAnomaly - eccentricity * Math.sin(eccentricAnomaly) - Math.toRadians(meanAnomaly))
                    / (1.0 - eccentricity * Math.cos(eccentricAnomaly));
            eccentricAnomaly -= delta;
        }

        return eccentricAnomaly;
    }

    private double calculateApparentMagnitude(Planet planet, double distanceAU) {
        // Simplified magnitude calculation
        double baseMagnitude = switch (planet) {
            case MERCURY -> -0.4;
            case VENUS -> -4.4;
            case MARS -> -1.5;
            case JUPITER -> -2.9;
            case SATURN -> 0.7;
            case URANUS -> 5.5;
            case NEPTUNE -> 7.8;
            case PLUTO -> 14.0;
            default -> 0.0;
        };

        // Distance correction (simplified)
        return baseMagnitude + 5.0 * Math.log10(distanceAU);
    }

    private double calculateElongation(double planetLongitude, double daysSinceEpoch) {
        // Simplified Sun longitude calculation
        double sunLongitude = normalizeDegrees(280.460 + 0.9856474 * daysSinceEpoch);
        return Math.abs(normalizeDegrees(planetLongitude - sunLongitude));
    }

    private boolean isRetrograde(Planet planet, double daysSinceEpoch) {
        // Simplified retrograde detection
        // In reality, this would require calculating the derivative of longitude
        return false; // Placeholder - would need more complex calculation
    }

    private double[] eclipticToEquatorial(double longitudeDeg, double latitudeDeg) {
        // Convert ecliptic coordinates to equatorial coordinates
        double obliquity = 23.4393; // Earth's axial tilt in degrees
        double lonRad = Math.toRadians(longitudeDeg);
        double latRad = Math.toRadians(latitudeDeg);
        double oblRad = Math.toRadians(obliquity);

        double ra = Math.atan2(
                Math.sin(lonRad) * Math.cos(oblRad) - Math.tan(latRad) * Math.sin(oblRad),
                Math.cos(lonRad));
        double dec = Math.asin(
                Math.sin(latRad) * Math.cos(oblRad) + Math.cos(latRad) * Math.sin(oblRad) * Math.sin(lonRad));

        return new double[] {
                normalizeDegrees(Math.toDegrees(ra)),
                Math.toDegrees(dec)
        };
    }

    private void calculateConstellationVisibility(ConstellationInfo info, ZonedDateTime dateTimeUtc,
            double latitudeDeg, double longitudeDeg) {
        // Calculate if constellation is visible based on local sidereal time
        double lst = calculateLocalSiderealTime(dateTimeUtc, longitudeDeg);
        double raHours = info.rightAscensionHours;

        // Simple visibility check - constellation is visible if it's above horizon
        double hourAngle = lst - raHours;
        if (hourAngle < 0) {
            hourAngle += 24;
        }
        if (hourAngle > 24) {
            hourAngle -= 24;
        }

        // Convert to elevation (simplified)
        double elevation = Math.toDegrees(Math.asin(
                Math.sin(Math.toRadians(info.declinationDeg)) * Math.sin(Math.toRadians(latitudeDeg)) +
                        Math.cos(Math.toRadians(info.declinationDeg)) * Math.cos(Math.toRadians(latitudeDeg)) *
                                Math.cos(Math.toRadians(15.0 * hourAngle))));

        info.setElevationDeg(elevation);
        info.setVisible(elevation > 0);

        // Calculate azimuth (simplified)
        double azimuth = Math.toDegrees(Math.atan2(
                -Math.sin(Math.toRadians(15.0 * hourAngle)) * Math.cos(Math.toRadians(info.declinationDeg)),
                Math.cos(Math.toRadians(latitudeDeg)) * Math.sin(Math.toRadians(info.declinationDeg)) -
                        Math.sin(Math.toRadians(latitudeDeg)) * Math.cos(Math.toRadians(info.declinationDeg)) *
                                Math.cos(Math.toRadians(15.0 * hourAngle))));

        info.setAzimuthDeg(normalizeDegrees(azimuth));
    }

    private double calculateLocalSiderealTime(ZonedDateTime dateTimeUtc, double longitudeDeg) {
        double jd = toJulianDay(dateTimeUtc);
        double daysSinceJ2000 = jd - 2451545.0;

        // Greenwich Mean Sidereal Time
        double gmst = 18.697374558 + 24.06570982441908 * daysSinceJ2000;
        gmst = gmst % 24.0;
        if (gmst < 0) {
            gmst += 24.0;
        }

        // Local Sidereal Time
        double lst = gmst + longitudeDeg / 15.0;
        return lst % 24.0;
    }

    private ConstellationInfo getConstellationData(String constellationName) {
        // Comprehensive constellation database for astrological purposes
        return switch (constellationName.toLowerCase()) {
            case "aries" -> createConstellationInfo("Aries", "Ari", "Arietis", 2.5, 20.0, 441, "Hamal", 2.0);
            case "taurus" -> createConstellationInfo("Taurus", "Tau", "Tauri", 4.5, 20.0, 797, "Aldebaran", 0.9);
            case "gemini" -> createConstellationInfo("Gemini", "Gem", "Geminorum", 7.0, 20.0, 514, "Pollux", 1.2);
            case "cancer" -> createConstellationInfo("Cancer", "Cnc", "Cancri", 8.5, 20.0, 506, "Al Tarf", 3.5);
            case "leo" -> createConstellationInfo("Leo", "Leo", "Leonis", 10.5, 15.0, 947, "Regulus", 1.4);
            case "virgo" -> createConstellationInfo("Virgo", "Vir", "Virginis", 13.0, 0.0, 1294, "Spica", 1.0);
            case "libra" -> createConstellationInfo("Libra", "Lib", "Librae", 15.0, -15.0, 538, "Zubeneschamali", 2.6);
            case "scorpius" -> createConstellationInfo("Scorpius", "Sco", "Scorpii", 16.5, -25.0, 497, "Antares", 1.0);
            case "sagittarius" ->
                createConstellationInfo("Sagittarius", "Sgr", "Sagittarii", 19.0, -25.0, 867, "Kaus Australis", 1.8);
            case "capricornus" ->
                createConstellationInfo("Capricornus", "Cap", "Capricorni", 21.0, -20.0, 414, "Deneb Algedi", 2.9);
            case "aquarius" ->
                createConstellationInfo("Aquarius", "Aqr", "Aquarii", 22.5, -10.0, 980, "Sadalsuud", 2.9);
            case "pisces" -> createConstellationInfo("Pisces", "Psc", "Piscium", 0.5, 15.0, 889, "Alrescha", 3.6);
            // Additional constellations for more comprehensive coverage
            case "orion" -> createConstellationInfo("Orion", "Ori", "Orionis", 5.5, 0.0, 594, "Rigel", 0.1);
            case "ursa_major" ->
                createConstellationInfo("Ursa Major", "UMa", "Ursae Majoris", 11.0, 50.0, 1280, "Alioth", 1.8);
            case "cassiopeia" ->
                createConstellationInfo("Cassiopeia", "Cas", "Cassiopeiae", 1.0, 60.0, 598, "Schedar", 2.2);
            case "cygnus" -> createConstellationInfo("Cygnus", "Cyg", "Cygni", 20.5, 40.0, 804, "Deneb", 1.3);
            case "lyra" -> createConstellationInfo("Lyra", "Lyr", "Lyrae", 18.5, 40.0, 286, "Vega", 0.0);
            case "bootes" -> createConstellationInfo("Bootes", "Boo", "Bootis", 14.5, 30.0, 907, "Arcturus", -0.1);
            case "corona_borealis" ->
                createConstellationInfo("Corona Borealis", "CrB", "Coronae Borealis", 15.5, 30.0, 179, "Alphecca", 2.2);
            case "hercules" ->
                createConstellationInfo("Hercules", "Her", "Herculis", 17.0, 30.0, 1225, "Kornephoros", 2.8);
            case "draco" -> createConstellationInfo("Draco", "Dra", "Draconis", 15.0, 70.0, 1083, "Eltanin", 2.2);
            case "cepheus" -> createConstellationInfo("Cepheus", "Cep", "Cephei", 22.0, 70.0, 588, "Alderamin", 2.5);
            default -> null;
        };
    }

    private ConstellationInfo createConstellationInfo(String name, String abbreviation, String genitive,
            double raHours, double decDeg, double area,
            String brightestStar, double magnitude) {
        ConstellationInfo info = new ConstellationInfo();
        info.name = name;
        info.abbreviation = abbreviation;
        info.genitive = genitive;
        info.rightAscensionHours = raHours;
        info.declinationDeg = decDeg;
        info.areaSquareDegrees = area;
        info.brightestStar = brightestStar;
        info.magnitude = magnitude;
        return info;
    }

    private double toJulianDay(ZonedDateTime dt) {
        int Y = dt.getYear();
        int M = dt.getMonthValue();
        int D = dt.getDayOfMonth();
        double hour = dt.getHour() + dt.getMinute() / 60.0 + dt.getSecond() / 3600.0;
        if (M <= 2) {
            Y -= 1;
            M += 12;
        }
        int A = Y / 100;
        int B = 2 - A + (A / 4);
        double jd = Math.floor(365.25 * (Y + 4716)) + Math.floor(30.6001 * (M + 1)) + D + B - 1524.5 + hour / 24.0;
        return jd;
    }

    private double normalizeDegrees(double deg) {
        double d = deg % 360.0;
        if (d < 0) {
            d += 360.0;
        }
        return d;
    }
}
