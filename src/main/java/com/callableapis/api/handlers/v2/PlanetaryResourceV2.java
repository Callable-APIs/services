package com.callableapis.api.handlers.v2;

import com.callableapis.api.time.PlanetaryService;
import com.callableapis.api.time.DateTimeService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Path("/v2/planetary")
public class PlanetaryResourceV2 {

    private final DateTimeService dateTimeService = new DateTimeService();
    private final PlanetaryService planetaryService = new PlanetaryService();

    public static class BaseDateTime {
        Integer year;
        Integer month; // 1-12
        Integer day;
        Integer hour;
        Integer minute;
        Integer second;
        String timezone;

        public BaseDateTime() {}

        public BaseDateTime(ZonedDateTime zdt) {
            this.year = zdt.getYear();
            this.month = zdt.getMonthValue();
            this.day = zdt.getDayOfMonth();
            this.hour = zdt.getHour();
            this.minute = zdt.getMinute();
            this.second = zdt.getSecond();
            this.timezone = zdt.getZone().getId();
        }

        public Integer getYear() { return year; }
        public void setYear(Integer year) { this.year = year; }
        public Integer getMonth() { return month; }
        public void setMonth(Integer month) { this.month = month; }
        public Integer getDay() { return day; }
        public void setDay(Integer day) { this.day = day; }
        public Integer getHour() { return hour; }
        public void setHour(Integer hour) { this.hour = hour; }
        public Integer getMinute() { return minute; }
        public void setMinute(Integer minute) { this.minute = minute; }
        public Integer getSecond() { return second; }
        public void setSecond(Integer second) { this.second = second; }
        public String getTimezone() { return timezone; }
        public void setTimezone(String timezone) { this.timezone = timezone; }
    }

    public static class PlanetaryPositionRequest {
        public String planet;
        public BaseDateTime at;
        public String seed;

        public String getPlanet() { return planet; }
        public void setPlanet(String planet) { this.planet = planet; }
        public BaseDateTime getAt() { return at; }
        public void setAt(BaseDateTime at) { this.at = at; }
        public String getSeed() { return seed; }
        public void setSeed(String seed) { this.seed = seed; }
    }

    public static class PlanetaryPositionResponse {
        public String planetName;
        public double longitudeDeg;
        public double latitudeDeg;
        public double distanceAU;
        public double rightAscensionDeg;
        public double declinationDeg;
        public String astrologicalSign;
        public String astrologicalSymbol;
        public String element;
        public String quality;
        public double magnitude;
        public boolean isRetrograde;
        public double elongationDeg;
        public String generatedAt;
        public String seed;

        // Getters and setters
        public String getPlanetName() { return planetName; }
        public void setPlanetName(String planetName) { this.planetName = planetName; }
        public double getLongitudeDeg() { return longitudeDeg; }
        public void setLongitudeDeg(double longitudeDeg) { this.longitudeDeg = longitudeDeg; }
        public double getLatitudeDeg() { return latitudeDeg; }
        public void setLatitudeDeg(double latitudeDeg) { this.latitudeDeg = latitudeDeg; }
        public double getDistanceAU() { return distanceAU; }
        public void setDistanceAU(double distanceAU) { this.distanceAU = distanceAU; }
        public double getRightAscensionDeg() { return rightAscensionDeg; }
        public void setRightAscensionDeg(double rightAscensionDeg) { this.rightAscensionDeg = rightAscensionDeg; }
        public double getDeclinationDeg() { return declinationDeg; }
        public void setDeclinationDeg(double declinationDeg) { this.declinationDeg = declinationDeg; }
        public String getAstrologicalSign() { return astrologicalSign; }
        public void setAstrologicalSign(String astrologicalSign) { this.astrologicalSign = astrologicalSign; }
        public String getAstrologicalSymbol() { return astrologicalSymbol; }
        public void setAstrologicalSymbol(String astrologicalSymbol) { this.astrologicalSymbol = astrologicalSymbol; }
        public String getElement() { return element; }
        public void setElement(String element) { this.element = element; }
        public String getQuality() { return quality; }
        public void setQuality(String quality) { this.quality = quality; }
        public double getMagnitude() { return magnitude; }
        public void setMagnitude(double magnitude) { this.magnitude = magnitude; }
        public boolean isRetrograde() { return isRetrograde; }
        public void setRetrograde(boolean retrograde) { isRetrograde = retrograde; }
        public double getElongationDeg() { return elongationDeg; }
        public void setElongationDeg(double elongationDeg) { this.elongationDeg = elongationDeg; }
        public String getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(String generatedAt) { this.generatedAt = generatedAt; }
        public String getSeed() { return seed; }
        public void setSeed(String seed) { this.seed = seed; }
    }

    public static class ConstellationRequest {
        public String constellation;
        public BaseDateTime at;
        public Double latitude;
        public Double longitude;
        public String seed;

        public String getConstellation() { return constellation; }
        public void setConstellation(String constellation) { this.constellation = constellation; }
        public BaseDateTime getAt() { return at; }
        public void setAt(BaseDateTime at) { this.at = at; }
        public Double getLatitude() { return latitude; }
        public void setLatitude(Double latitude) { this.latitude = latitude; }
        public Double getLongitude() { return longitude; }
        public void setLongitude(Double longitude) { this.longitude = longitude; }
        public String getSeed() { return seed; }
        public void setSeed(String seed) { this.seed = seed; }
    }

    public static class ConstellationResponse {
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
        public String generatedAt;

        // Getters and setters
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getAbbreviation() { return abbreviation; }
        public void setAbbreviation(String abbreviation) { this.abbreviation = abbreviation; }
        public String getGenitive() { return genitive; }
        public void setGenitive(String genitive) { this.genitive = genitive; }
        public double getRightAscensionHours() { return rightAscensionHours; }
        public void setRightAscensionHours(double rightAscensionHours) { this.rightAscensionHours = rightAscensionHours; }
        public double getDeclinationDeg() { return declinationDeg; }
        public void setDeclinationDeg(double declinationDeg) { this.declinationDeg = declinationDeg; }
        public double getAreaSquareDegrees() { return areaSquareDegrees; }
        public void setAreaSquareDegrees(double areaSquareDegrees) { this.areaSquareDegrees = areaSquareDegrees; }
        public String getBrightestStar() { return brightestStar; }
        public void setBrightestStar(String brightestStar) { this.brightestStar = brightestStar; }
        public double getMagnitude() { return magnitude; }
        public void setMagnitude(double magnitude) { this.magnitude = magnitude; }
        public boolean isVisible() { return isVisible; }
        public void setVisible(boolean visible) { isVisible = visible; }
        public double getElevationDeg() { return elevationDeg; }
        public void setElevationDeg(double elevationDeg) { this.elevationDeg = elevationDeg; }
        public double getAzimuthDeg() { return azimuthDeg; }
        public void setAzimuthDeg(double azimuthDeg) { this.azimuthDeg = azimuthDeg; }
        public String getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(String generatedAt) { this.generatedAt = generatedAt; }
    }

    @GET
    @Path("planets")
    @Produces(MediaType.APPLICATION_JSON)
    public String[] getAvailablePlanets() {
        PlanetaryService.Planet[] planets = PlanetaryService.Planet.values();
        String[] planetNames = new String[planets.length];
        for (int i = 0; i < planets.length; i++) {
            planetNames[i] = planets[i].getName();
        }
        return planetNames;
    }

    @GET
    @Path("signs")
    @Produces(MediaType.APPLICATION_JSON)
    public String[] getAvailableSigns() {
        PlanetaryService.AstrologicalSign[] signs = PlanetaryService.AstrologicalSign.values();
        String[] signNames = new String[signs.length];
        for (int i = 0; i < signs.length; i++) {
            signNames[i] = signs[i].getName();
        }
        return signNames;
    }

    @GET
    @Path("constellations")
    @Produces(MediaType.APPLICATION_JSON)
    public String[] getAvailableConstellations() {
        return new String[]{
            "Aries", "Taurus", "Gemini", "Cancer", "Leo", "Virgo",
            "Libra", "Scorpius", "Sagittarius", "Capricornus", "Aquarius", "Pisces"
        };
    }

    @POST
    @Path("position")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public PlanetaryPositionResponse getPlanetaryPosition(PlanetaryPositionRequest req) {
        if (req == null || req.planet == null) {
            throw new IllegalArgumentException("Planet is required");
        }

        PlanetaryService.Planet planet;
        try {
            planet = PlanetaryService.Planet.valueOf(req.planet.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid planet: " + req.planet);
        }

        ZonedDateTime at = toZoned(req.at);
        PlanetaryService.PlanetaryPosition position = planetaryService.calculatePlanetPosition(planet, at);

        PlanetaryPositionResponse response = new PlanetaryPositionResponse();
        response.planetName = position.getPlanetName();
        response.longitudeDeg = position.getLongitudeDeg();
        response.latitudeDeg = position.getLatitudeDeg();
        response.distanceAU = position.getDistanceAU();
        response.rightAscensionDeg = position.getRightAscensionDeg();
        response.declinationDeg = position.getDeclinationDeg();
        response.astrologicalSign = position.getAstrologicalSign().getName();
        response.astrologicalSymbol = position.getAstrologicalSign().getSymbol();
        response.element = position.getAstrologicalSign().getElement();
        response.quality = position.getAstrologicalSign().getQuality();
        response.magnitude = position.getMagnitude();
        response.isRetrograde = position.isRetrograde();
        response.elongationDeg = position.getElongationDeg();
        response.generatedAt = at.withZoneSameInstant(ZoneOffset.UTC).toString();
        response.seed = req.seed;

        return response;
    }

    @POST
    @Path("constellation")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public ConstellationResponse getConstellationInfo(ConstellationRequest req) {
        if (req == null || req.constellation == null) {
            throw new IllegalArgumentException("Constellation is required");
        }

        ZonedDateTime at = toZoned(req.at);
        Double latitude = req.latitude != null ? req.latitude : 0.0;
        Double longitude = req.longitude != null ? req.longitude : 0.0;

        PlanetaryService.ConstellationInfo info = planetaryService.getConstellationInfo(
            req.constellation, at, latitude, longitude);

        if (info == null) {
            throw new IllegalArgumentException("Unknown constellation: " + req.constellation);
        }

        ConstellationResponse response = new ConstellationResponse();
        response.name = info.getName();
        response.abbreviation = info.getAbbreviation();
        response.genitive = info.getGenitive();
        response.rightAscensionHours = info.getRightAscensionHours();
        response.declinationDeg = info.getDeclinationDeg();
        response.areaSquareDegrees = info.getAreaSquareDegrees();
        response.brightestStar = info.getBrightestStar();
        response.magnitude = info.getMagnitude();
        response.isVisible = info.isVisible();
        response.elevationDeg = info.getElevationDeg();
        response.azimuthDeg = info.getAzimuthDeg();
        response.generatedAt = at.withZoneSameInstant(ZoneOffset.UTC).toString();

        return response;
    }

    @GET
    @Path("position/{planet}")
    @Produces(MediaType.APPLICATION_JSON)
    public PlanetaryPositionResponse getCurrentPlanetaryPosition(@QueryParam("planet") String planet) {
        PlanetaryPositionRequest req = new PlanetaryPositionRequest();
        req.planet = planet;
        return getPlanetaryPosition(req);
    }

    private ZonedDateTime toZoned(BaseDateTime base) {
        if (base == null) {
            return dateTimeService.nowUtc();
        }
        if (base.year == null) {
            base.year = ZonedDateTime.now().getYear();
        }
        if (base.month == null) {
            base.month = ZonedDateTime.now().getMonthValue();
        }
        if (base.day == null) {
            base.day = ZonedDateTime.now().getDayOfMonth();
        }
        if (base.hour == null) {
            base.hour = 0;
        }
        if (base.minute == null) {
            base.minute = 0;
        }
        if (base.second == null) {
            base.second = 0;
        }
        if (base.timezone == null) {
            base.timezone = "UTC";
        }
        
        return ZonedDateTime.of(base.year, base.month, base.day, 
                               base.hour, base.minute, base.second, 0, 
                               ZoneOffset.of(base.timezone));
    }

}
