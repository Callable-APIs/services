package com.callableapis.api.handlers.v2;

import com.callableapis.api.time.InspirationService;
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
import java.util.List;
import java.util.Map;

@Path("/v2/inspiration")
public class InspirationResourceV2 {

    private final DateTimeService dateTimeService = new DateTimeService();
    private final InspirationService inspirationService = new InspirationService();

    public static class BaseDateTime {
        Integer year;
        Integer month; // 1-12
        Integer day;
        Integer hour;
        Integer minute;
        Integer second;
        String timezone;

        public BaseDateTime() {
        }

        public BaseDateTime(ZonedDateTime zdt) {
            this.year = zdt.getYear();
            this.month = zdt.getMonthValue();
            this.day = zdt.getDayOfMonth();
            this.hour = zdt.getHour();
            this.minute = zdt.getMinute();
            this.second = zdt.getSecond();
            this.timezone = zdt.getZone().getId();
        }

        public Integer getYear() {
            return year;
        }

        public void setYear(Integer year) {
            this.year = year;
        }

        public Integer getMonth() {
            return month;
        }

        public void setMonth(Integer month) {
            this.month = month;
        }

        public Integer getDay() {
            return day;
        }

        public void setDay(Integer day) {
            this.day = day;
        }

        public Integer getHour() {
            return hour;
        }

        public void setHour(Integer hour) {
            this.hour = hour;
        }

        public Integer getMinute() {
            return minute;
        }

        public void setMinute(Integer minute) {
            this.minute = minute;
        }

        public Integer getSecond() {
            return second;
        }

        public void setSecond(Integer second) {
            this.second = second;
        }

        public String getTimezone() {
            return timezone;
        }

        public void setTimezone(String timezone) {
            this.timezone = timezone;
        }
    }

    public static class HoroscopeRequest {
        public String type;
        public String sign;
        public BaseDateTime at;
        public String seed;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public BaseDateTime getAt() {
            return at;
        }

        public void setAt(BaseDateTime at) {
            this.at = at;
        }

        public String getSeed() {
            return seed;
        }

        public void setSeed(String seed) {
            this.seed = seed;
        }
    }

    public static class HoroscopeResponse {
        public String type;
        public String sign;
        public String symbol;
        public String element;
        public String quality;
        public String title;
        public String content;
        public String guidance;
        public String mood;
        public String luckyNumbers;
        public String luckyColors;
        public String compatibility;
        public Map<String, Object> planetaryInfluences;
        public String generatedAt;
        public String seed;

        // Getters and setters
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }

        public String getSymbol() {
            return symbol;
        }

        public void setSymbol(String symbol) {
            this.symbol = symbol;
        }

        public String getElement() {
            return element;
        }

        public void setElement(String element) {
            this.element = element;
        }

        public String getQuality() {
            return quality;
        }

        public void setQuality(String quality) {
            this.quality = quality;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getGuidance() {
            return guidance;
        }

        public void setGuidance(String guidance) {
            this.guidance = guidance;
        }

        public String getMood() {
            return mood;
        }

        public void setMood(String mood) {
            this.mood = mood;
        }

        public String getLuckyNumbers() {
            return luckyNumbers;
        }

        public void setLuckyNumbers(String luckyNumbers) {
            this.luckyNumbers = luckyNumbers;
        }

        public String getLuckyColors() {
            return luckyColors;
        }

        public void setLuckyColors(String luckyColors) {
            this.luckyColors = luckyColors;
        }

        public String getCompatibility() {
            return compatibility;
        }

        public void setCompatibility(String compatibility) {
            this.compatibility = compatibility;
        }

        public Map<String, Object> getPlanetaryInfluences() {
            return planetaryInfluences;
        }

        public void setPlanetaryInfluences(Map<String, Object> planetaryInfluences) {
            this.planetaryInfluences = planetaryInfluences;
        }

        public String getGeneratedAt() {
            return generatedAt;
        }

        public void setGeneratedAt(String generatedAt) {
            this.generatedAt = generatedAt;
        }

        public String getSeed() {
            return seed;
        }

        public void setSeed(String seed) {
            this.seed = seed;
        }
    }

    public static class TarotRequest {
        public String spread;
        public String seed;

        public String getSpread() {
            return spread;
        }

        public void setSpread(String spread) {
            this.spread = spread;
        }

        public String getSeed() {
            return seed;
        }

        public void setSeed(String seed) {
            this.seed = seed;
        }
    }

    public static class TarotCardResponse {
        public String name;
        public String suit;
        public String number;
        public boolean isMajor;
        public String element;
        public String meaning;
        public String reversedMeaning;
        public String keywords;
        public String description;
        public boolean isReversed;

        // Getters and setters
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSuit() {
            return suit;
        }

        public void setSuit(String suit) {
            this.suit = suit;
        }

        public String getNumber() {
            return number;
        }

        public void setNumber(String number) {
            this.number = number;
        }

        public boolean isMajor() {
            return isMajor;
        }

        public void setMajor(boolean major) {
            isMajor = major;
        }

        public String getElement() {
            return element;
        }

        public void setElement(String element) {
            this.element = element;
        }

        public String getMeaning() {
            return meaning;
        }

        public void setMeaning(String meaning) {
            this.meaning = meaning;
        }

        public String getReversedMeaning() {
            return reversedMeaning;
        }

        public void setReversedMeaning(String reversedMeaning) {
            this.reversedMeaning = reversedMeaning;
        }

        public String getKeywords() {
            return keywords;
        }

        public void setKeywords(String keywords) {
            this.keywords = keywords;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public boolean isReversed() {
            return isReversed;
        }

        public void setReversed(boolean reversed) {
            isReversed = reversed;
        }
    }

    public static class TarotReadingResponse {
        public String spread;
        public String description;
        public List<TarotCardResponse> cards;
        public String overallReading;
        public String guidance;
        public String theme;
        public String generatedAt;
        public String seed;

        // Getters and setters
        public String getSpread() {
            return spread;
        }

        public void setSpread(String spread) {
            this.spread = spread;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<TarotCardResponse> getCards() {
            return cards;
        }

        public void setCards(List<TarotCardResponse> cards) {
            this.cards = cards;
        }

        public String getOverallReading() {
            return overallReading;
        }

        public void setOverallReading(String overallReading) {
            this.overallReading = overallReading;
        }

        public String getGuidance() {
            return guidance;
        }

        public void setGuidance(String guidance) {
            this.guidance = guidance;
        }

        public String getTheme() {
            return theme;
        }

        public void setTheme(String theme) {
            this.theme = theme;
        }

        public String getGeneratedAt() {
            return generatedAt;
        }

        public void setGeneratedAt(String generatedAt) {
            this.generatedAt = generatedAt;
        }

        public String getSeed() {
            return seed;
        }

        public void setSeed(String seed) {
            this.seed = seed;
        }
    }

    @GET
    @Path("horoscope-types")
    @Produces(MediaType.APPLICATION_JSON)
    public String[] getHoroscopeTypes() {
        return new String[] { "daily", "monthly", "annual" };
    }

    @GET
    @Path("tarot-spreads")
    @Produces(MediaType.APPLICATION_JSON)
    public String[] getTarotSpreads() {
        return new String[] { "single", "three", "celtic", "horoscope" };
    }

    @GET
    @Path("signs")
    @Produces(MediaType.APPLICATION_JSON)
    public String[] getAstrologicalSigns() {
        PlanetaryService.AstrologicalSign[] signs = PlanetaryService.AstrologicalSign.values();
        String[] signNames = new String[signs.length];
        for (int i = 0; i < signs.length; i++) {
            signNames[i] = signs[i].getName();
        }
        return signNames;
    }

    @POST
    @Path("horoscope")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public HoroscopeResponse generateHoroscope(HoroscopeRequest req) {
        if (req == null || req.sign == null) {
            throw new IllegalArgumentException("Sign is required");
        }

        String type = req.type != null ? req.type : "daily";
        String sign = req.sign;
        ZonedDateTime at = toZoned(req.at);
        String seed = req.seed;

        // Validate type
        if (!type.equals("daily") && !type.equals("monthly") && !type.equals("annual")) {
            throw new IllegalArgumentException("Invalid horoscope type. Must be daily, monthly, or annual");
        }

        // Validate sign
        PlanetaryService.AstrologicalSign astrologicalSign;
        try {
            astrologicalSign = PlanetaryService.AstrologicalSign.valueOf(sign.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid astrological sign: " + sign);
        }

        // Convert type
        InspirationService.HoroscopeType horoscopeType = switch (type) {
            case "daily" -> InspirationService.HoroscopeType.DAILY;
            case "monthly" -> InspirationService.HoroscopeType.MONTHLY;
            case "annual" -> InspirationService.HoroscopeType.ANNUAL;
            default -> InspirationService.HoroscopeType.DAILY;
        };

        InspirationService.HoroscopeResult result = inspirationService.generateHoroscope(
                horoscopeType, astrologicalSign, at, seed);

        HoroscopeResponse response = new HoroscopeResponse();
        response.type = result.getType();
        response.sign = result.getSign();
        response.symbol = result.getSymbol();
        response.element = result.getElement();
        response.quality = result.getQuality();
        response.title = result.getTitle();
        response.content = result.getContent();
        response.guidance = result.getGuidance();
        response.mood = result.getMood();
        response.luckyNumbers = result.getLuckyNumbers();
        response.luckyColors = result.getLuckyColors();
        response.compatibility = result.getCompatibility();
        response.planetaryInfluences = result.getPlanetaryInfluences();
        response.generatedAt = result.getGeneratedAt().withZoneSameInstant(ZoneOffset.UTC).toString();
        response.seed = result.getSeed();

        return response;
    }

    @POST
    @Path("tarot")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public TarotReadingResponse generateTarotReading(TarotRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("Request is required");
        }

        String spread = req.spread != null ? req.spread : "single";
        String seed = req.seed;

        // Validate spread
        if (!spread.equals("single") && !spread.equals("three") &&
                !spread.equals("celtic") && !spread.equals("horoscope")) {
            throw new IllegalArgumentException("Invalid tarot spread. Must be single, three, celtic, or horoscope");
        }

        // Convert spread
        InspirationService.TarotSpread tarotSpread = switch (spread) {
            case "single" -> InspirationService.TarotSpread.SINGLE_CARD;
            case "three" -> InspirationService.TarotSpread.THREE_CARD;
            case "celtic" -> InspirationService.TarotSpread.CELTIC_CROSS;
            case "horoscope" -> InspirationService.TarotSpread.HOROSCOPE;
            default -> InspirationService.TarotSpread.SINGLE_CARD;
        };

        InspirationService.TarotReading result = inspirationService.generateTarotReading(tarotSpread, seed);

        TarotReadingResponse response = new TarotReadingResponse();
        response.spread = result.getSpread();
        response.description = result.getDescription();
        response.overallReading = result.getOverallReading();
        response.guidance = result.getGuidance();
        response.theme = result.getTheme();
        response.generatedAt = result.getGeneratedAt().withZoneSameInstant(ZoneOffset.UTC).toString();
        response.seed = result.getSeed();

        // Convert cards
        response.cards = result.getCards().stream()
                .map(this::convertTarotCard)
                .toList();

        return response;
    }

    @GET
    @Path("tarot/single")
    @Produces(MediaType.APPLICATION_JSON)
    public TarotCardResponse generateSingleCard(@QueryParam("seed") String seed) {
        InspirationService.TarotCard card = inspirationService.generateSingleCard(seed);
        return convertTarotCard(card);
    }

    @GET
    @Path("horoscope/{sign}")
    @Produces(MediaType.APPLICATION_JSON)
    public HoroscopeResponse generateDailyHoroscope(
            @QueryParam("sign") String sign,
            @QueryParam("type") String type,
            @QueryParam("seed") String seed) {

        HoroscopeRequest req = new HoroscopeRequest();
        req.sign = sign;
        req.type = type;
        req.seed = seed;

        return generateHoroscope(req);
    }

    private TarotCardResponse convertTarotCard(InspirationService.TarotCard card) {
        TarotCardResponse response = new TarotCardResponse();
        response.name = card.getName();
        response.suit = card.getSuit();
        response.number = card.getNumber();
        response.isMajor = card.isMajor();
        response.element = card.getElement();
        response.meaning = card.getMeaning();
        response.reversedMeaning = card.getReversedMeaning();
        response.keywords = card.getKeywords();
        response.description = card.getDescription();
        response.isReversed = card.isReversed();
        return response;
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
