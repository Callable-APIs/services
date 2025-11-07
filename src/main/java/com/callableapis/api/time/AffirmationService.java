package com.callableapis.api.time;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Service for generating personalized affirmations based on arbitrary input
 * dictionaries.
 * Uses templates and pre-defined vocabulary to create unique, amusing, or
 * heartfelt affirmations.
 */
public class AffirmationService {

    public AffirmationService() {
    }

    public static class AffirmationResult {
        private String affirmation;
        private String tone;
        private List<String> keywords;
        private Map<String, Object> inputData;
        private ZonedDateTime generatedAt;
        private String seed;

        public String getAffirmation() {
            return affirmation;
        }

        public void setAffirmation(String affirmation) {
            this.affirmation = affirmation;
        }

        public String getTone() {
            return tone;
        }

        public void setTone(String tone) {
            this.tone = tone;
        }

        public List<String> getKeywords() {
            return keywords;
        }

        public void setKeywords(List<String> keywords) {
            this.keywords = keywords;
        }

        public Map<String, Object> getInputData() {
            return inputData;
        }

        public void setInputData(Map<String, Object> inputData) {
            this.inputData = inputData;
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
     * Generate an affirmation based on the input dictionary.
     * Values from the dictionary are used to seed and personalize the affirmation.
     */
    public AffirmationResult generateAffirmation(Map<String, Object> inputData, String seed) {
        AffirmationResult result = new AffirmationResult();
        result.inputData = inputData != null ? new HashMap<>(inputData) : new HashMap<>();
        result.generatedAt = ZonedDateTime.now();
        result.seed = seed;

        // Generate seed from input data if not provided
        String effectiveSeed = seed != null ? seed : generateSeedFromInput(inputData);
        result.seed = effectiveSeed;

        // Extract values from input dictionary
        List<String> inputValues = extractValues(inputData);

        // Determine tone based on input
        String tone = determineTone(inputData, effectiveSeed);
        result.tone = tone;

        // Extract keywords from input values
        List<String> keywords = extractKeywords(inputValues);
        result.keywords = keywords;

        // Generate the affirmation text
        String affirmation = generateAffirmationText(inputData, inputValues, tone, effectiveSeed);
        result.affirmation = affirmation;

        return result;
    }

    /**
     * Generate a seed string from the input data for deterministic randomness.
     */
    private String generateSeedFromInput(Map<String, Object> inputData) {
        if (inputData == null || inputData.isEmpty()) {
            return String.valueOf(System.currentTimeMillis());
        }
        return inputData.entrySet().stream()
                .map(e -> e.getKey() + "=" + e.getValue())
                .collect(Collectors.joining("|"));
    }

    /**
     * Extract all string values from the input dictionary.
     */
    private List<String> extractValues(Map<String, Object> inputData) {
        List<String> values = new ArrayList<>();
        if (inputData == null) {
            return values;
        }
        for (Object value : inputData.values()) {
            if (value != null) {
                values.add(value.toString());
            }
        }
        return values;
    }

    /**
     * Extract keywords from input values.
     */
    private List<String> extractKeywords(List<String> inputValues) {
        List<String> keywords = new ArrayList<>();
        for (String value : inputValues) {
            // Simple keyword extraction - take first few words or short phrases
            String[] words = value.split("\\s+");
            if (words.length <= 3) {
                keywords.add(value);
            } else {
                // Take first 2-3 meaningful words
                keywords.add(words[0] + " " + words[1]);
            }
        }
        return keywords.stream().limit(5).collect(Collectors.toList());
    }

    /**
     * Determine the tone (amusing, heartfelt, etc.) based on input.
     */
    private String determineTone(Map<String, Object> inputData, String seed) {
        if (inputData == null || inputData.isEmpty()) {
            return selectTone("default", seed);
        }

        // Analyze input to determine tone
        String inputString = inputData.values().stream()
                .map(Object::toString)
                .collect(Collectors.joining(" "))
                .toLowerCase();

        // Keywords that suggest different tones
        if (inputString.contains("happy") || inputString.contains("joy") ||
                inputString.contains("love") || inputString.contains("heart")) {
            return selectTone("heartfelt", seed);
        }
        if (inputString.contains("fun") || inputString.contains("funny") ||
                inputString.contains("laugh") || inputString.contains("silly")) {
            return selectTone("amusing", seed);
        }
        if (inputString.contains("work") || inputString.contains("job") ||
                inputString.contains("career") || inputString.contains("professional")) {
            return selectTone("motivational", seed);
        }
        if (inputString.contains("sad") || inputString.contains("struggle") ||
                inputString.contains("challenge") || inputString.contains("difficult")) {
            return selectTone("encouraging", seed);
        }

        // Default: use seed to deterministically select tone
        return selectTone("balanced", seed);
    }

    /**
     * Select a tone deterministically based on seed.
     */
    private String selectTone(String baseTone, String seed) {
        String[] tones = { "heartfelt", "amusing", "motivational", "encouraging", "playful", "inspiring" };
        int index = Math.abs((seed + baseTone).hashCode() % tones.length);
        return tones[index];
    }

    /**
     * Generate the affirmation text using templates and input values.
     */
    private String generateAffirmationText(Map<String, Object> inputData, List<String> inputValues,
            String tone, String seed) {
        // Build a seed hash for deterministic selection
        long seedHash = (seed + tone).hashCode();

        // Select template based on tone
        String template = selectTemplate(tone, seedHash);

        // Select vocabulary words based on tone
        List<String> vocabulary = selectVocabulary(tone, seedHash);

        // Incorporate input values into the affirmation
        String affirmation = personalizeTemplate(template, inputValues, vocabulary, seedHash);

        return affirmation;
    }

    /**
     * Select a template based on tone.
     */
    private String selectTemplate(String tone, long seedHash) {
        Map<String, String[]> templatesByTone = new HashMap<>();

        templatesByTone.put("heartfelt", new String[] {
                "You are %s, and that makes you absolutely %s.",
                "In this moment, remember that you are %s, and that is %s.",
                "Your %s shines through in everything you do, and that is %s.",
                "Today, embrace the fact that you are %s, which is truly %s.",
                "You have %s within you, and that makes you %s.",
                "There is something deeply beautiful about the way you embody %s. It reminds the world that %s is not just possible, but already present in you.",
                "When I think about your %s, I am reminded that some of the most precious gifts in life come wrapped in moments of %s. You carry that gift with grace.",
                "Your journey with %s has shaped you into someone who understands the true meaning of %s. That wisdom flows from you like a gentle stream, touching everyone around you.",
                "In a world that often overlooks the quiet miracles, your %s stands as a testament to the power of %s. Never underestimate the light you bring.",
                "Every part of you—your %s, your dreams, your struggles—is woven into a story that speaks of %s. And that story is still being written, still becoming more beautiful."
        });

        templatesByTone.put("amusing", new String[] {
                "You're %s, and honestly, that's pretty %s!",
                "Let's be real: you're %s, and that's actually %s.",
                "Fun fact: you're %s, and that makes you delightfully %s.",
                "Here's the thing: you've got %s, which is hilariously %s.",
                "Plot twist: you're %s, and honestly, that's wonderfully %s.",
                "Okay, so here's the deal: you've got this whole %s situation going on, which honestly makes you ridiculously %s. Like, unfairly %s. The universe clearly stacked the deck in your favor.",
                "Fun fact of the day: you're out here being %s like it's no big deal, when in reality that level of %s should probably require a permit or at least some kind of safety warning.",
                "Breaking news: local human demonstrates extraordinary levels of %s, leaving bystanders both confused and impressed. Scientists are baffled. You're just out here being %s.",
                "Plot twist in your life story: turns out you're not just %s, you're spectacularly %s. This was not in the original script, but honestly? The improv is working.",
                "So I've done the math, and mathematically speaking, your combination of %s and %s creates this perfect storm of %s that frankly shouldn't be legal. But here we are, and I'm here for it."
        });

        templatesByTone.put("motivational", new String[] {
                "You possess %s, and with it comes incredible %s.",
                "Your %s is a superpower that grants you %s.",
                "Today, channel your %s to achieve %s.",
                "You have the %s to create %s in your life.",
                "Let your %s guide you toward %s."
        });

        templatesByTone.put("encouraging", new String[] {
                "Even though things feel %s, you have %s within you.",
                "You're facing %s, but remember you also have %s.",
                "It's okay to feel %s; you also have the strength of %s.",
                "Your %s doesn't define you; your %s does.",
                "In moments of %s, know that your %s will guide you forward."
        });

        templatesByTone.put("playful", new String[] {
                "Hey you, with all that %s? Yeah, you're pretty %s!",
                "So here's the deal: you're %s, and that's delightfully %s.",
                "Fun alert: your %s makes you wonderfully %s.",
                "Quick reminder: you've got %s, and that's genuinely %s.",
                "Breaking news: you're %s, and honestly, that's %s!"
        });

        templatesByTone.put("inspiring", new String[] {
                "You carry %s in your spirit, and it brings you %s.",
                "The universe recognizes your %s and responds with %s.",
                "Your journey is marked by %s, which reveals your %s.",
                "In every step, your %s manifests as %s.",
                "You are a vessel of %s, filled with boundless %s."
        });

        // Default templates (balanced)
        templatesByTone.put("balanced", new String[] {
                "You are %s, and that brings you %s.",
                "Your %s makes you uniquely %s.",
                "Today, honor your %s and the %s it brings.",
                "You have %s, which is beautifully %s.",
                "Your %s contributes to making you %s."
        });

        String[] templates = templatesByTone.getOrDefault(tone, templatesByTone.get("balanced"));
        int index = (int) (Math.abs(seedHash) % templates.length);
        return templates[index];
    }

    /**
     * Select vocabulary words based on tone.
     */
    private List<String> selectVocabulary(String tone, long seedHash) {
        Map<String, String[]> vocabularyByTone = new HashMap<>();

        vocabularyByTone.put("heartfelt", new String[] {
                "beautiful", "precious", "cherished", "beloved", "worthy", "loved",
                "valued", "treasured", "special", "unique", "gifted", "blessed",
                "irreplaceable", "incomparable", "magnificent", "radiant", "remarkable",
                "extraordinary", "genuine", "authentic", "tender", "compassionate", "kind",
                "graceful", "noble", "sincere", "devoted", "steadfast", "gentle", "warm"
        });

        vocabularyByTone.put("amusing", new String[] {
                "awesome", "fantastic", "hilarious", "brilliant", "epic", "legendary",
                "spectacular", "wonderful", "amazing", "incredible", "outstanding", "terrific",
                "ridiculous", "absurdly good", "unfairly talented", "dangerously charming",
                "suspiciously perfect", "questionably excellent", "suspiciously delightful",
                "absurdly wonderful", "unreasonably cool", "improbably awesome", "illegally fun",
                "unfairly amusing", "questionably brilliant"
        });

        vocabularyByTone.put("motivational", new String[] {
                "power", "strength", "excellence", "success", "achievement", "progress",
                "growth", "opportunity", "potential", "greatness", "determination", "victory"
        });

        vocabularyByTone.put("encouraging", new String[] {
                "resilience", "courage", "hope", "strength", "perseverance", "fortitude",
                "determination", "patience", "wisdom", "compassion", "gentleness", "kindness"
        });

        vocabularyByTone.put("playful", new String[] {
                "fun", "joyful", "silly", "whimsical", "cheerful", "bright",
                "lively", "spirited", "energetic", "zesty", "spunky", "vibrant"
        });

        vocabularyByTone.put("inspiring", new String[] {
                "light", "wisdom", "grace", "beauty", "harmony", "peace",
                "clarity", "purpose", "vision", "dreams", "possibilities", "potential"
        });

        // Default vocabulary
        vocabularyByTone.put("balanced", new String[] {
                "wonderful", "unique", "special", "valuable", "meaningful", "significant",
                "remarkable", "notable", "admirable", "commendable", "impressive", "noteworthy"
        });

        String[] vocab = vocabularyByTone.getOrDefault(tone, vocabularyByTone.get("balanced"));

        // Select 2-3 words from vocabulary based on seed
        List<String> selected = new ArrayList<>();
        Random seededRandom = new Random(seedHash);
        int numWords = 2 + seededRandom.nextInt(2); // 2-3 words

        for (int i = 0; i < numWords; i++) {
            int index = seededRandom.nextInt(vocab.length);
            String word = vocab[index];
            if (!selected.contains(word)) {
                selected.add(word);
            }
        }

        return selected;
    }

    /**
     * Personalize the template with input values and vocabulary.
     */
    private String personalizeTemplate(String template, List<String> inputValues,
            List<String> vocabulary, long seedHash) {
        Random seededRandom = new Random(seedHash);

        // Count how many placeholders we need
        long placeholderCount = template.chars().filter(ch -> ch == '%').count();

        // Prepare values from input data
        List<String> preparedInputs = new ArrayList<>();
        if (!inputValues.isEmpty()) {
            for (String value : inputValues) {
                String phrase = extractMeaningfulPhrase(value, seededRandom);
                if (phrase != null && !phrase.isEmpty()) {
                    preparedInputs.add(phrase);
                }
            }
            // If we need more inputs than we have, reuse with variations
            while (preparedInputs.size() < placeholderCount && !inputValues.isEmpty()) {
                String value = inputValues.get(seededRandom.nextInt(inputValues.size()));
                String phrase = extractMeaningfulPhrase(value, seededRandom);
                if (phrase != null && !phrase.isEmpty() && !preparedInputs.contains(phrase)) {
                    preparedInputs.add(phrase);
                }
            }
        }

        // Prepare vocabulary words
        List<String> preparedVocab = new ArrayList<>(vocabulary);
        if (preparedVocab.isEmpty()) {
            preparedVocab.add("special");
            preparedVocab.add("wonderful");
            preparedVocab.add("amazing");
        }

        // Build arguments array for String.format
        List<String> args = new ArrayList<>();
        
        // For each placeholder, alternate between input values and vocabulary
        for (int i = 0; i < placeholderCount; i++) {
            if (i % 2 == 0 && !preparedInputs.isEmpty()) {
                // Use input value
                args.add(preparedInputs.get(seededRandom.nextInt(preparedInputs.size())));
            } else {
                // Use vocabulary word
                args.add(preparedVocab.get(seededRandom.nextInt(preparedVocab.size())));
            }
        }

        // Handle templates with different placeholder counts
        if (placeholderCount == 3) {
            return String.format(template, args.get(0), args.get(1), args.get(2));
        } else if (placeholderCount == 2) {
            return String.format(template, args.get(0), args.get(1));
        } else if (placeholderCount == 1) {
            return String.format(template, args.get(0));
        } else if (placeholderCount == 0) {
            return template;
        } else {
            // Fallback for 4+ placeholders (use Object array)
            return String.format(template, args.toArray());
        }
    }

    /**
     * Extract a meaningful phrase from a value string.
     */
    private String extractMeaningfulPhrase(String value, Random random) {
        if (value == null || value.trim().isEmpty()) {
            return "yourself";
        }

        String trimmed = value.trim();
        String[] words = trimmed.split("\\s+");

        // If it's short, use it as-is
        if (words.length <= 3) {
            return trimmed.toLowerCase();
        }

        // Extract a meaningful phrase (2-4 words)
        int start = random.nextInt(Math.max(1, words.length - 2));
        int length = 2 + random.nextInt(3); // 2-4 words
        int end = Math.min(start + length, words.length);

        StringBuilder phrase = new StringBuilder();
        for (int i = start; i < end; i++) {
            if (phrase.length() > 0) {
                phrase.append(" ");
            }
            phrase.append(words[i]);
        }

        return phrase.toString().toLowerCase();
    }
}
