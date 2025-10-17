package com.callableapis.api.handlers.v2;

import com.callableapis.api.time.RandomService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import java.util.List;

@Path("/v2/random")
public class RandomResourceV2 {

    private final RandomService randomService = new RandomService();

    public static class RandomNumberRequest {
        public String type;
        public Integer count;
        public Double min;
        public Double max;
        public String seed;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Double getMin() {
            return min;
        }

        public void setMin(Double min) {
            this.min = min;
        }

        public Double getMax() {
            return max;
        }

        public void setMax(Double max) {
            this.max = max;
        }

        public String getSeed() {
            return seed;
        }

        public void setSeed(String seed) {
            this.seed = seed;
        }
    }

    public static class RandomIntegerRequest {
        public String type;
        public Integer count;
        public Integer min;
        public Integer max;
        public String seed;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getCount() {
            return count;
        }

        public void setCount(Integer count) {
            this.count = count;
        }

        public Integer getMin() {
            return min;
        }

        public void setMin(Integer min) {
            this.min = min;
        }

        public Integer getMax() {
            return max;
        }

        public void setMax(Integer max) {
            this.max = max;
        }

        public String getSeed() {
            return seed;
        }

        public void setSeed(String seed) {
            this.seed = seed;
        }
    }

    public static class RandomBooleanRequest {
        public String type;
        public String seed;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSeed() {
            return seed;
        }

        public void setSeed(String seed) {
            this.seed = seed;
        }
    }

    public static class RandomStringRequest {
        public String type;
        public Integer length;
        public String charset;
        public String seed;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public Integer getLength() {
            return length;
        }

        public void setLength(Integer length) {
            this.length = length;
        }

        public String getCharset() {
            return charset;
        }

        public void setCharset(String charset) {
            this.charset = charset;
        }

        public String getSeed() {
            return seed;
        }

        public void setSeed(String seed) {
            this.seed = seed;
        }
    }

    public static class RandomUuidRequest {
        public String type;
        public String seed;

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getSeed() {
            return seed;
        }

        public void setSeed(String seed) {
            this.seed = seed;
        }
    }

    public static class RandomNumberResponse {
        public String type;
        public String description;
        public List<Double> values;
        public int count;
        public double min;
        public double max;
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

    public static class RandomIntegerResponse {
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

    public static class RandomBooleanResponse {
        public String type;
        public String description;
        public boolean value;
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

        public boolean isValue() {
            return value;
        }

        public void setValue(boolean value) {
            this.value = value;
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

    public static class RandomStringResponse {
        public String type;
        public String description;
        public String value;
        public int length;
        public String charset;
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

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public int getLength() {
            return length;
        }

        public void setLength(int length) {
            this.length = length;
        }

        public String getCharset() {
            return charset;
        }

        public void setCharset(String charset) {
            this.charset = charset;
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

    public static class RandomUuidResponse {
        public String type;
        public String description;
        public String value;
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

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
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

    @GET
    @Path("types")
    @Produces(MediaType.APPLICATION_JSON)
    public String[] getAvailableTypes() {
        return randomService.getAvailableTypes().stream()
                .map(RandomService.RandomType::getCode)
                .toArray(String[]::new);
    }

    @POST
    @Path("numbers")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RandomNumberResponse generateNumbers(RandomNumberRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("Request is required");
        }

        String type = req.type != null ? req.type : "truly_uniform";
        int count = req.count != null ? req.count : 1;
        double min = req.min != null ? req.min : 0.0;
        double max = req.max != null ? req.max : 1.0;
        String seed = req.seed;

        if (count < 1 || count > 1000) {
            throw new IllegalArgumentException("Count must be between 1 and 1000");
        }
        if (min >= max) {
            throw new IllegalArgumentException("Min must be less than max");
        }

        RandomService.RandomType randomType = randomService.getRandomTypeByCode(type);
        RandomService.RandomNumberResult result = randomService.generateRandomNumbers(
                randomType, count, min, max, seed);

        RandomNumberResponse response = new RandomNumberResponse();
        response.type = result.getType();
        response.description = result.getDescription();
        response.values = result.getValues();
        response.count = result.getCount();
        response.min = result.getMin();
        response.max = result.getMax();
        response.mean = result.getMean();
        response.standardDeviation = result.getStandardDeviation();
        response.generationTimeMs = result.getGenerationTimeMs();
        response.seed = result.getSeed();

        return response;
    }

    @POST
    @Path("integers")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RandomIntegerResponse generateIntegers(RandomIntegerRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("Request is required");
        }

        String type = req.type != null ? req.type : "truly_uniform";
        int count = req.count != null ? req.count : 1;
        int min = req.min != null ? req.min : 0;
        int max = req.max != null ? req.max : 100;
        String seed = req.seed;

        if (count < 1 || count > 1000) {
            throw new IllegalArgumentException("Count must be between 1 and 1000");
        }
        if (min >= max) {
            throw new IllegalArgumentException("Min must be less than max");
        }

        RandomService.RandomType randomType = randomService.getRandomTypeByCode(type);
        RandomService.RandomIntegerResult result = randomService.generateRandomIntegers(
                randomType, count, min, max, seed);

        RandomIntegerResponse response = new RandomIntegerResponse();
        response.type = result.getType();
        response.description = result.getDescription();
        response.values = result.getValues();
        response.count = result.getCount();
        response.min = result.getMin();
        response.max = result.getMax();
        response.mean = result.getMean();
        response.standardDeviation = result.getStandardDeviation();
        response.generationTimeMs = result.getGenerationTimeMs();
        response.seed = result.getSeed();

        return response;
    }

    @POST
    @Path("boolean")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RandomBooleanResponse generateBoolean(RandomBooleanRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("Request is required");
        }

        String type = req.type != null ? req.type : "truly_uniform";
        String seed = req.seed;

        RandomService.RandomType randomType = randomService.getRandomTypeByCode(type);
        long startTime = System.currentTimeMillis();
        boolean value = randomService.generateRandomBoolean(randomType, seed);
        long generationTime = System.currentTimeMillis() - startTime;

        RandomBooleanResponse response = new RandomBooleanResponse();
        response.type = randomType.getCode();
        response.description = randomType.getDescription();
        response.value = value;
        response.generationTimeMs = generationTime;
        response.seed = seed;

        return response;
    }

    @POST
    @Path("string")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RandomStringResponse generateString(RandomStringRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("Request is required");
        }

        String type = req.type != null ? req.type : "truly_uniform";
        int length = req.length != null ? req.length : 10;
        String charset = req.charset;
        String seed = req.seed;

        if (length < 1 || length > 1000) {
            throw new IllegalArgumentException("Length must be between 1 and 1000");
        }

        RandomService.RandomType randomType = randomService.getRandomTypeByCode(type);
        long startTime = System.currentTimeMillis();
        String value = randomService.generateRandomString(randomType, length, charset, seed);
        long generationTime = System.currentTimeMillis() - startTime;

        RandomStringResponse response = new RandomStringResponse();
        response.type = randomType.getCode();
        response.description = randomType.getDescription();
        response.value = value;
        response.length = length;
        response.charset = charset != null ? charset : "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        response.generationTimeMs = generationTime;
        response.seed = seed;

        return response;
    }

    @POST
    @Path("uuid")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public RandomUuidResponse generateUuid(RandomUuidRequest req) {
        if (req == null) {
            throw new IllegalArgumentException("Request is required");
        }

        String type = req.type != null ? req.type : "truly_uniform";
        String seed = req.seed;

        RandomService.RandomType randomType = randomService.getRandomTypeByCode(type);
        long startTime = System.currentTimeMillis();
        String value = randomService.generateRandomUuid(randomType, seed);
        long generationTime = System.currentTimeMillis() - startTime;

        RandomUuidResponse response = new RandomUuidResponse();
        response.type = randomType.getCode();
        response.description = randomType.getDescription();
        response.value = value;
        response.generationTimeMs = generationTime;
        response.seed = seed;

        return response;
    }

    @GET
    @Path("number")
    @Produces(MediaType.APPLICATION_JSON)
    public RandomNumberResponse generateSingleNumber(
            @QueryParam("type") String type,
            @QueryParam("min") Double min,
            @QueryParam("max") Double max,
            @QueryParam("seed") String seed) {

        RandomNumberRequest req = new RandomNumberRequest();
        req.type = type;
        req.count = 1;
        req.min = min;
        req.max = max;
        req.seed = seed;

        return generateNumbers(req);
    }

    @GET
    @Path("integer")
    @Produces(MediaType.APPLICATION_JSON)
    public RandomIntegerResponse generateSingleInteger(
            @QueryParam("type") String type,
            @QueryParam("min") Integer min,
            @QueryParam("max") Integer max,
            @QueryParam("seed") String seed) {

        RandomIntegerRequest req = new RandomIntegerRequest();
        req.type = type;
        req.count = 1;
        req.min = min;
        req.max = max;
        req.seed = seed;

        return generateIntegers(req);
    }

    @GET
    @Path("boolean")
    @Produces(MediaType.APPLICATION_JSON)
    public RandomBooleanResponse generateSingleBoolean(
            @QueryParam("type") String type,
            @QueryParam("seed") String seed) {

        RandomBooleanRequest req = new RandomBooleanRequest();
        req.type = type;
        req.seed = seed;

        return generateBoolean(req);
    }
}
