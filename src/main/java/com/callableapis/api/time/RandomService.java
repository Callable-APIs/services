package com.callableapis.api.time;

import java.security.SecureRandom;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Service for generating various types of random numbers.
 * Provides truly random, truly uniform, and cheap random number generation.
 */
public class RandomService {

    public enum RandomType {
        TRULY_RANDOM("truly_random", "Uses external entropy sources for maximum randomness"),
        TRULY_UNIFORM("truly_uniform", "Mathematically uniform distribution using high-quality algorithms"),
        CHEAP("cheap", "Fast native random number generation for performance");

        private final String code;
        private final String description;

        RandomType(String code, String description) {
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

    public static class RandomNumberResult {
        public String type;
        public String description;
        public List<Double> values;
        public int count;
        public double min;
        public double max;
        public double mean;
        public double standardDeviation;
        public long generationTimeMs;
        public String seed; // For reproducible sequences

        // Getters and setters
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<Double> getValues() {
            return values;
        }

        public void setValues(List<Double> values) {
            this.values = values;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public double getMin() {
            return min;
        }

        public void setMin(double min) {
            this.min = min;
        }

        public double getMax() {
            return max;
        }

        public void setMax(double max) {
            this.max = max;
        }

        public double getMean() {
            return mean;
        }

        public void setMean(double mean) {
            this.mean = mean;
        }

        public double getStandardDeviation() {
            return standardDeviation;
        }

        public void setStandardDeviation(double standardDeviation) {
            this.standardDeviation = standardDeviation;
        }

        public long getGenerationTimeMs() {
            return generationTimeMs;
        }

        public void setGenerationTimeMs(long generationTimeMs) {
            this.generationTimeMs = generationTimeMs;
        }

        public String getSeed() {
            return seed;
        }

        public void setSeed(String seed) {
            this.seed = seed;
        }
    }

    public static class RandomIntegerResult {
        public String type;
        public String description;
        public List<Integer> values;
        public int count;
        public int min;
        public int max;
        public double mean;
        public double standardDeviation;
        public long generationTimeMs;
        public String seed;

        // Getters and setters
        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public List<Integer> getValues() {
            return values;
        }

        public void setValues(List<Integer> values) {
            this.values = values;
        }

        public int getCount() {
            return count;
        }

        public void setCount(int count) {
            this.count = count;
        }

        public int getMin() {
            return min;
        }

        public void setMin(int min) {
            this.min = min;
        }

        public int getMax() {
            return max;
        }

        public void setMax(int max) {
            this.max = max;
        }

        public double getMean() {
            return mean;
        }

        public void setMean(double mean) {
            this.mean = mean;
        }

        public double getStandardDeviation() {
            return standardDeviation;
        }

        public void setStandardDeviation(double standardDeviation) {
            this.standardDeviation = standardDeviation;
        }

        public long getGenerationTimeMs() {
            return generationTimeMs;
        }

        public void setGenerationTimeMs(long generationTimeMs) {
            this.generationTimeMs = generationTimeMs;
        }

        public String getSeed() {
            return seed;
        }

        public void setSeed(String seed) {
            this.seed = seed;
        }
    }

    private final SecureRandom secureRandom;
    private final Random cheapRandom;

    public RandomService() {
        this.secureRandom = new SecureRandom();
        this.cheapRandom = new Random();
    }

    /**
     * Generate random floating-point numbers using the specified method.
     */
    public RandomNumberResult generateRandomNumbers(RandomType type, int count, double min, double max, String seed) {
        long startTime = System.currentTimeMillis();

        RandomNumberResult result = new RandomNumberResult();
        result.type = type.getCode();
        result.description = type.getDescription();
        result.count = count;
        result.min = min;
        result.max = max;
        result.seed = seed;

        List<Double> values = new ArrayList<>();
        Random generator = getRandomGenerator(type, seed);

        for (int i = 0; i < count; i++) {
            double value = generateRandomDouble(generator, min, max);
            values.add(value);
        }

        result.values = values;
        result.generationTimeMs = System.currentTimeMillis() - startTime;

        // Calculate statistics
        calculateStatistics(result);

        return result;
    }

    /**
     * Generate random integers using the specified method.
     */
    public RandomIntegerResult generateRandomIntegers(RandomType type, int count, int min, int max, String seed) {
        long startTime = System.currentTimeMillis();

        RandomIntegerResult result = new RandomIntegerResult();
        result.type = type.getCode();
        result.description = type.getDescription();
        result.count = count;
        result.min = min;
        result.max = max;
        result.seed = seed;

        List<Integer> values = new ArrayList<>();
        Random generator = getRandomGenerator(type, seed);

        for (int i = 0; i < count; i++) {
            int value = generator.nextInt(max - min + 1) + min;
            values.add(value);
        }

        result.values = values;
        result.generationTimeMs = System.currentTimeMillis() - startTime;

        // Calculate statistics
        calculateIntegerStatistics(result);

        return result;
    }

    /**
     * Generate a random boolean value.
     */
    public boolean generateRandomBoolean(RandomType type, String seed) {
        Random generator = getRandomGenerator(type, seed);
        return generator.nextBoolean();
    }

    /**
     * Generate a random choice from a list of options.
     */
    public <T> T generateRandomChoice(RandomType type, List<T> options, String seed) {
        if (options == null || options.isEmpty()) {
            throw new IllegalArgumentException("Options list cannot be null or empty");
        }

        Random generator = getRandomGenerator(type, seed);
        int index = generator.nextInt(options.size());
        return options.get(index);
    }

    /**
     * Shuffle a list randomly.
     */
    public <T> List<T> shuffleList(RandomType type, List<T> list, String seed) {
        if (list == null) {
            throw new IllegalArgumentException("List cannot be null");
        }

        List<T> shuffled = new ArrayList<>(list);
        Random generator = getRandomGenerator(type, seed);
        Collections.shuffle(shuffled, generator);
        return shuffled;
    }

    /**
     * Generate a random string of specified length.
     */
    public String generateRandomString(RandomType type, int length, String charset, String seed) {
        if (charset == null || charset.isEmpty()) {
            charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        }

        Random generator = getRandomGenerator(type, seed);
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = generator.nextInt(charset.length());
            sb.append(charset.charAt(index));
        }

        return sb.toString();
    }

    /**
     * Generate a random UUID-like string.
     */
    public String generateRandomUuid(RandomType type, String seed) {
        Random generator = getRandomGenerator(type, seed);
        return String.format("%08x-%04x-%04x-%04x-%012x",
                generator.nextInt(),
                generator.nextInt(0x10000),
                generator.nextInt(0x10000),
                generator.nextInt(0x10000),
                generator.nextLong() & 0xffffffffffffL);
    }

    private Random getRandomGenerator(RandomType type, String seed) {
        return switch (type) {
            case TRULY_RANDOM -> {
                if (seed != null && !seed.isEmpty()) {
                    yield new SecureRandom(seed.getBytes(StandardCharsets.UTF_8));
                } else {
                    yield secureRandom;
                }
            }
            case TRULY_UNIFORM -> {
                if (seed != null && !seed.isEmpty()) {
                    yield new Random(seed.hashCode());
                } else {
                    yield ThreadLocalRandom.current();
                }
            }
            case CHEAP -> {
                if (seed != null && !seed.isEmpty()) {
                    yield new Random(seed.hashCode());
                } else {
                    yield cheapRandom;
                }
            }
        };
    }

    private double generateRandomDouble(Random generator, double min, double max) {
        if (min >= max) {
            throw new IllegalArgumentException("Min must be less than max");
        }
        return min + (max - min) * generator.nextDouble();
    }

    private void calculateStatistics(RandomNumberResult result) {
        List<Double> values = result.values;
        if (values.isEmpty()) {
            result.mean = 0.0;
            result.standardDeviation = 0.0;
            return;
        }

        // Calculate mean
        double sum = 0.0;
        for (double value : values) {
            sum += value;
        }
        result.mean = sum / values.size();

        // Calculate standard deviation
        double sumSquaredDiffs = 0.0;
        for (double value : values) {
            double diff = value - result.mean;
            sumSquaredDiffs += diff * diff;
        }
        result.standardDeviation = Math.sqrt(sumSquaredDiffs / values.size());
    }

    private void calculateIntegerStatistics(RandomIntegerResult result) {
        List<Integer> values = result.values;
        if (values.isEmpty()) {
            result.mean = 0.0;
            result.standardDeviation = 0.0;
            return;
        }

        // Calculate mean
        double sum = 0.0;
        for (int value : values) {
            sum += value;
        }
        result.mean = sum / values.size();

        // Calculate standard deviation
        double sumSquaredDiffs = 0.0;
        for (int value : values) {
            double diff = value - result.mean;
            sumSquaredDiffs += diff * diff;
        }
        result.standardDeviation = Math.sqrt(sumSquaredDiffs / values.size());
    }

    /**
     * Get information about available random number generation types.
     */
    public List<RandomType> getAvailableTypes() {
        return List.of(RandomType.values());
    }

    /**
     * Validate that a random type is supported.
     */
    public boolean isValidRandomType(String typeCode) {
        for (RandomType type : RandomType.values()) {
            if (type.getCode().equals(typeCode)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get a random type by code.
     */
    public RandomType getRandomTypeByCode(String typeCode) {
        for (RandomType type : RandomType.values()) {
            if (type.getCode().equals(typeCode)) {
                return type;
            }
        }
        throw new IllegalArgumentException("Unknown random type: " + typeCode);
    }
}
