package com.callableapis.api.time;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.Random;

/**
 * Service for generating mystical inspiration content including horoscopes and
 * tarot readings.
 * Designed to provide AI agents with aspirational and surprising context
 * sources.
 */
public class InspirationService {

    private final RandomService randomService;
    private final PlanetaryService planetaryService;
    private final Random random = new Random();

    public InspirationService() {
        this.randomService = new RandomService();
        this.planetaryService = new PlanetaryService();
    }

    public enum HoroscopeType {
        DAILY("daily", "Daily horoscope based on current planetary positions"),
        MONTHLY("monthly", "Monthly horoscope with longer-term influences"),
        ANNUAL("annual", "Annual horoscope for the year ahead");

        private final String code;
        private final String description;

        HoroscopeType(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    public enum TarotSpread {
        SINGLE_CARD("single", "Single card reading for quick guidance"),
        THREE_CARD("three", "Past, Present, Future spread"),
        CELTIC_CROSS("celtic", "Traditional 10-card Celtic Cross spread"),
        HOROSCOPE("horoscope", "12-card spread representing astrological houses");

        private final String code;
        private final String description;

        TarotSpread(String code, String description) {
            this.code = code;
            this.description = description;
        }

        public String getCode() {
            return code;
        }

        public String getDescription() {
            return description;
        }
    }

    public static class HoroscopeResult {
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
        public ZonedDateTime generatedAt;
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

        public ZonedDateTime getGeneratedAt() {
            return generatedAt;
        }

        public void setGeneratedAt(ZonedDateTime generatedAt) {
            this.generatedAt = generatedAt;
        }

        public String getSeed() {
            return seed;
        }

        public void setSeed(String seed) {
            this.seed = seed;
        }
    }

    public static class TarotCard {
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

    public static class TarotReading {
        public String spread;
        public String description;
        public List<TarotCard> cards;
        public String overallReading;
        public String guidance;
        public String theme;
        public ZonedDateTime generatedAt;
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

        public List<TarotCard> getCards() {
            return cards;
        }

        public void setCards(List<TarotCard> cards) {
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

        public ZonedDateTime getGeneratedAt() {
            return generatedAt;
        }

        public void setGeneratedAt(ZonedDateTime generatedAt) {
            this.generatedAt = generatedAt;
        }

        public String getSeed() {
            return seed;
        }

        public void setSeed(String seed) {
            this.seed = seed;
        }
    }

    /**
     * Generate a horoscope for a specific astrological sign and time period.
     */
    public HoroscopeResult generateHoroscope(HoroscopeType type, PlanetaryService.AstrologicalSign sign,
            ZonedDateTime dateTime, String seed) {
        HoroscopeResult result = new HoroscopeResult();
        result.type = type.getCode();
        result.sign = sign.getName();
        result.symbol = sign.getSymbol();
        result.element = sign.getElement();
        result.quality = sign.getQuality();
        result.generatedAt = dateTime;
        result.seed = seed;

        // Generate content based on type and sign
        generateHoroscopeContent(result, type, sign, dateTime, seed);

        // Add planetary influences
        result.planetaryInfluences = calculatePlanetaryInfluences(sign, dateTime);

        return result;
    }

    /**
     * Generate a tarot reading with the specified spread.
     */
    public TarotReading generateTarotReading(TarotSpread spread, String seed) {
        TarotReading reading = new TarotReading();
        reading.spread = spread.getCode();
        reading.description = spread.getDescription();
        reading.generatedAt = ZonedDateTime.now();
        reading.seed = seed;

        // Generate cards based on spread
        reading.cards = generateCardsForSpread(spread, seed);

        // Generate overall reading
        generateOverallReading(reading);

        return reading;
    }

    /**
     * Generate a single tarot card for quick guidance.
     */
    public TarotCard generateSingleCard(String seed) {
        List<TarotCard> deck = createTarotDeck();
        TarotCard card = randomService.generateRandomChoice(
                RandomService.RandomType.TRULY_UNIFORM, deck, seed);

        // Randomly determine if reversed
        boolean isReversed = randomService.generateRandomBoolean(
                RandomService.RandomType.TRULY_UNIFORM, seed + "_reversed");
        card.setReversed(isReversed);

        return card;
    }

    private void generateHoroscopeContent(HoroscopeResult result, HoroscopeType type,
            PlanetaryService.AstrologicalSign sign,
            ZonedDateTime dateTime, String seed) {
        // Generate title
        result.title = generateHoroscopeTitle(type, sign, dateTime);

        // Generate main content
        result.content = generateHoroscopeText(type, sign, dateTime, seed);

        // Generate guidance
        result.guidance = generateGuidance(sign, type, seed);

        // Generate mood
        result.mood = generateMood(sign, type, seed);

        // Generate lucky numbers and colors
        result.luckyNumbers = generateLuckyNumbers(sign, seed);
        result.luckyColors = generateLuckyColors(sign, seed);

        // Generate compatibility
        result.compatibility = generateCompatibility(sign, seed);
    }

    private String generateHoroscopeTitle(HoroscopeType type, PlanetaryService.AstrologicalSign sign,
            ZonedDateTime dateTime) {
        String[] templates = {
                "Cosmic Guidance for %s",
                "Stellar Wisdom for %s",
                "Celestial Insights for %s",
                "Universal Messages for %s",
                "Astral Guidance for %s"
        };

        String template = templates[Math.abs(sign.getName().hashCode() & Integer.MAX_VALUE) % templates.length];
        return String.format(template, sign.getName());
    }

    private String generateHoroscopeText(HoroscopeType type, PlanetaryService.AstrologicalSign sign,
            ZonedDateTime dateTime, String seed) {
        // This would typically use more sophisticated text generation
        // For now, we'll use template-based generation with randomization

        String[] openingTemplates = {
                "The stars align to bring you %s energy today.",
                "Cosmic forces are %s in your favor.",
                "The universe whispers %s messages to your soul.",
                "Celestial bodies dance in %s harmony for you.",
                "Stellar energies flow %s through your being."
        };

        String[] energyWords = {
                "powerful", "gentle", "transformative", "healing", "creative",
                "mystical", "inspiring", "nurturing", "dynamic", "peaceful"
        };

        String opening = openingTemplates[Math.abs((sign.getName() + seed).hashCode() & Integer.MAX_VALUE)
                % openingTemplates.length];
        String energy = energyWords[Math.abs((sign.getName() + seed + "energy").hashCode() & Integer.MAX_VALUE)
                % energyWords.length];

        return String.format(opening, energy) + " " + generateDetailedGuidance(sign, type, seed);
    }

    private String generateDetailedGuidance(PlanetaryService.AstrologicalSign sign, HoroscopeType type, String seed) {
        // Generate context-aware guidance based on sign and type
        String element = sign.getElement();
        String quality = sign.getQuality();

        Map<String, String> elementGuidance = new HashMap<>();
        elementGuidance.put("Fire", "Your inner flame burns bright with passion and creativity.");
        elementGuidance.put("Earth", "Ground yourself in practical wisdom and steady progress.");
        elementGuidance.put("Air", "Let your thoughts soar on winds of innovation and communication.");
        elementGuidance.put("Water", "Flow with emotional depth and intuitive understanding.");

        Map<String, String> qualityGuidance = new HashMap<>();
        qualityGuidance.put("Cardinal", "Take initiative and lead with confidence.");
        qualityGuidance.put("Fixed", "Stand firm in your convictions and maintain your course.");
        qualityGuidance.put("Mutable", "Adapt gracefully to changing circumstances and opportunities.");

        return elementGuidance.get(element) + " " + qualityGuidance.get(quality);
    }

    private String generateGuidance(PlanetaryService.AstrologicalSign sign, HoroscopeType type, String seed) {
        String[] guidanceTemplates = {
                "Trust your intuition and follow your heart's true calling.",
                "Embrace change as an opportunity for growth and transformation.",
                "Seek balance between your inner and outer worlds.",
                "Connect with others through authentic communication and understanding.",
                "Honor your unique gifts and share them with the world."
        };

        return guidanceTemplates[Math.abs((sign.getName() + type.getCode() + seed).hashCode() & Integer.MAX_VALUE)
                % guidanceTemplates.length];
    }

    private String generateMood(PlanetaryService.AstrologicalSign sign, HoroscopeType type, String seed) {
        String[] moods = {
                "optimistic", "contemplative", "energetic", "peaceful", "creative",
                "focused", "playful", "serious", "mystical", "practical"
        };

        return moods[Math.abs((sign.getName() + "mood" + seed).hashCode() & Integer.MAX_VALUE) % moods.length];
    }

    private String generateLuckyNumbers(PlanetaryService.AstrologicalSign sign, String seed) {
        // Generate 3-5 lucky numbers based on sign and seed
        List<Integer> numbers = new ArrayList<>();
        // Use seeded random for deterministic results
        Random seededRandom = new Random((sign.getName() + seed).hashCode());

        for (int i = 0; i < 4; i++) {
            numbers.add(seededRandom.nextInt(99) + 1);
        }

        return numbers.toString().replaceAll("[\\[\\]\\s]", ", ");
    }

    private String generateLuckyColors(PlanetaryService.AstrologicalSign sign, String seed) {
        Map<String, String> signColors = new HashMap<>();
        signColors.put("Aries", "Red, Orange, Crimson");
        signColors.put("Taurus", "Green, Pink, Earth tones");
        signColors.put("Gemini", "Yellow, Silver, Light Blue");
        signColors.put("Cancer", "Silver, White, Sea Green");
        signColors.put("Leo", "Gold, Orange, Yellow");
        signColors.put("Virgo", "Navy, Brown, Beige");
        signColors.put("Libra", "Pink, Blue, Green");
        signColors.put("Scorpio", "Deep Red, Black, Maroon");
        signColors.put("Sagittarius", "Purple, Deep Blue, Orange");
        signColors.put("Capricorn", "Brown, Black, Dark Green");
        signColors.put("Aquarius", "Electric Blue, Silver, Aqua");
        signColors.put("Pisces", "Sea Green, Aqua, Lavender");

        return signColors.getOrDefault(sign.getName(), "Blue, Silver, White");
    }

    private String generateCompatibility(PlanetaryService.AstrologicalSign sign, String seed) {
        Map<String, String> compatibility = new HashMap<>();
        compatibility.put("Aries", "Leo, Sagittarius, Gemini, Aquarius");
        compatibility.put("Taurus", "Virgo, Capricorn, Cancer, Pisces");
        compatibility.put("Gemini", "Libra, Aquarius, Aries, Leo");
        compatibility.put("Cancer", "Scorpio, Pisces, Taurus, Virgo");
        compatibility.put("Leo", "Aries, Sagittarius, Gemini, Libra");
        compatibility.put("Virgo", "Taurus, Capricorn, Cancer, Scorpio");
        compatibility.put("Libra", "Gemini, Aquarius, Leo, Sagittarius");
        compatibility.put("Scorpio", "Cancer, Pisces, Virgo, Capricorn");
        compatibility.put("Sagittarius", "Aries, Leo, Libra, Aquarius");
        compatibility.put("Capricorn", "Taurus, Virgo, Scorpio, Pisces");
        compatibility.put("Aquarius", "Gemini, Libra, Aries, Sagittarius");
        compatibility.put("Pisces", "Cancer, Scorpio, Taurus, Capricorn");

        return compatibility.getOrDefault(sign.getName(), "All signs have potential for harmony");
    }

    private Map<String, Object> calculatePlanetaryInfluences(PlanetaryService.AstrologicalSign sign,
            ZonedDateTime dateTime) {
        Map<String, Object> influences = new HashMap<>();

        // Calculate planetary positions for the given time
        for (PlanetaryService.Planet planet : PlanetaryService.Planet.values()) {
            if (planet == PlanetaryService.Planet.EARTH) {
                continue; // Skip Earth
            }

            PlanetaryService.PlanetaryPosition position = planetaryService.calculatePlanetPosition(planet, dateTime);

            Map<String, Object> planetInfo = new HashMap<>();
            planetInfo.put("longitude", position.getLongitudeDeg());
            planetInfo.put("sign", position.getAstrologicalSign().getName());
            planetInfo.put("magnitude", position.getMagnitude());
            planetInfo.put("retrograde", position.isRetrograde());

            influences.put(planet.getName().toLowerCase(), planetInfo);
        }

        return influences;
    }

    private List<TarotCard> generateCardsForSpread(TarotSpread spread, String seed) {
        List<TarotCard> deck = createTarotDeck();
        List<TarotCard> selectedCards = new ArrayList<>();

        int numCards = switch (spread) {
            case SINGLE_CARD -> 1;
            case THREE_CARD -> 3;
            case CELTIC_CROSS -> 10;
            case HOROSCOPE -> 12;
        };

        // Shuffle deck and select cards
        List<TarotCard> shuffledDeck = randomService.shuffleList(
                RandomService.RandomType.TRULY_UNIFORM, deck, seed);

        for (int i = 0; i < numCards && i < shuffledDeck.size(); i++) {
            TarotCard card = shuffledDeck.get(i);
            // Randomly determine if reversed
            boolean isReversed = randomService.generateRandomBoolean(
                    RandomService.RandomType.TRULY_UNIFORM, seed + "_card_" + i);
            card.setReversed(isReversed);
            selectedCards.add(card);
        }

        return selectedCards;
    }

    private void generateOverallReading(TarotReading reading) {
        // Generate overall theme and guidance based on the cards
        String theme = generateReadingTheme(reading.cards);
        reading.theme = theme;

        String overallReading = generateOverallText(reading.cards, reading.spread);
        reading.overallReading = overallReading;

        String guidance = generateTarotGuidance(reading.cards, reading.spread);
        reading.guidance = guidance;
    }

    private String generateReadingTheme(List<TarotCard> cards) {
        // Analyze cards to determine overall theme
        long majorArcanaCount = cards.stream().filter(TarotCard::isMajor).count();
        long reversedCount = cards.stream().filter(TarotCard::isReversed).count();

        if (majorArcanaCount > cards.size() / 2) {
            return "Major life transformation and spiritual growth";
        } else if (reversedCount > cards.size() / 2) {
            return "Internal reflection and overcoming challenges";
        } else {
            return "Balanced approach to current life circumstances";
        }
    }

    private String generateOverallText(List<TarotCard> cards, String spread) {
        return "The cards reveal a complex tapestry of influences in your life. " +
                "Each card speaks to different aspects of your journey, offering " +
                "insights and guidance for the path ahead.";
    }

    private String generateTarotGuidance(List<TarotCard> cards, String spread) {
        return "Trust in the wisdom of the cards and your own inner knowing. " +
                "The guidance offered here is a reflection of your current energy " +
                "and the possibilities that lie before you.";
    }

    private List<TarotCard> createTarotDeck() {
        List<TarotCard> deck = new ArrayList<>();

        // Major Arcana
        String[] majorArcana = {
                "The Fool", "The Magician", "The High Priestess", "The Empress", "The Emperor",
                "The Hierophant", "The Lovers", "The Chariot", "Strength", "The Hermit",
                "Wheel of Fortune", "Justice", "The Hanged Man", "Death", "Temperance",
                "The Devil", "The Tower", "The Star", "The Moon", "The Sun",
                "Judgement", "The World"
        };

        for (int i = 0; i < majorArcana.length; i++) {
            TarotCard card = new TarotCard();
            card.setName(majorArcana[i]);
            card.setSuit("Major Arcana");
            card.setNumber(String.valueOf(i));
            card.setMajor(true);
            card.setElement("Spirit");
            card.setMeaning("Universal life lesson and spiritual guidance");
            card.setReversedMeaning("Internal reflection and blocked energy");
            card.setKeywords("Transformation, Growth, Spiritual");
            card.setDescription("A powerful archetypal energy representing " + majorArcana[i].toLowerCase());
            deck.add(card);
        }

        // Minor Arcana - Wands (Fire)
        String[] wands = { "Ace", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten",
                "Page", "Knight", "Queen", "King" };
        for (String wand : wands) {
            TarotCard card = createMinorArcanaCard(wand, "Wands", "Fire", "Creativity, Passion, Energy");
            deck.add(card);
        }

        // Minor Arcana - Cups (Water)
        for (String cup : wands) {
            TarotCard card = createMinorArcanaCard(cup, "Cups", "Water", "Emotions, Intuition, Relationships");
            deck.add(card);
        }

        // Minor Arcana - Swords (Air)
        for (String sword : wands) {
            TarotCard card = createMinorArcanaCard(sword, "Swords", "Air", "Thoughts, Communication, Challenges");
            deck.add(card);
        }

        // Minor Arcana - Pentacles (Earth)
        for (String pentacle : wands) {
            TarotCard card = createMinorArcanaCard(pentacle, "Pentacles", "Earth", "Material, Practical, Grounding");
            deck.add(card);
        }

        return deck;
    }

    private TarotCard createMinorArcanaCard(String name, String suit, String element, String theme) {
        TarotCard card = new TarotCard();
        card.setName(name + " of " + suit);
        card.setSuit(suit);
        card.setNumber(name);
        card.setMajor(false);
        card.setElement(element);
        card.setMeaning("Practical guidance in " + theme.toLowerCase());
        card.setReversedMeaning("Blocked or challenging energy in " + theme.toLowerCase());
        card.setKeywords(theme);
        card.setDescription("A card representing " + theme.toLowerCase() + " in the " + suit + " suit");
        return card;
    }
}
