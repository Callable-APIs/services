package com.callableapis.api.handlers.v2;

import com.callableapis.api.time.AffirmationService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;

/**
 * REST resource for generating personalized affirmations based on arbitrary input dictionaries.
 */
@Path("/v2/affirmation")
public class AffirmationResourceV2 {

    private final AffirmationService affirmationService = new AffirmationService();

    public static class AffirmationRequest {
        private Map<String, Object> data;
        private String seed;

        public Map<String, Object> getData() {
            return data != null ? new java.util.HashMap<>(data) : null;
        }

        public void setData(Map<String, Object> data) {
            this.data = data;
        }

        public String getSeed() {
            return seed;
        }

        public void setSeed(String seed) {
            this.seed = seed;
        }
    }

    public static class AffirmationResponse {
        private String affirmation;
        private String tone;
        private List<String> keywords;
        private Map<String, Object> inputData;
        private String generatedAt;
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
            return inputData != null ? new java.util.HashMap<>(inputData) : null;
        }

        public void setInputData(Map<String, Object> inputData) {
            this.inputData = inputData;
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

    /**
     * Generate an affirmation based on input dictionary.
     * 
     * @param request The request containing input data dictionary and optional seed
     * @return Response containing the generated affirmation
     */
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateAffirmation(AffirmationRequest request) {
        try {
            // Handle null request
            if (request == null) {
                request = new AffirmationRequest();
            }

            // Extract data and seed
            Map<String, Object> inputData = request.getData();
            String seed = request.getSeed();

            // Generate affirmation
            AffirmationService.AffirmationResult result = affirmationService.generateAffirmation(
                    inputData, seed);

            // Convert to response
            AffirmationResponse response = new AffirmationResponse();
            response.setAffirmation(result.getAffirmation());
            response.setTone(result.getTone());
            response.setKeywords(result.getKeywords());
            response.setInputData(result.getInputData());
            response.setGeneratedAt(result.getGeneratedAt()
                    .withZoneSameInstant(ZoneOffset.UTC).toString());
            response.setSeed(result.getSeed());

            return Response.ok(response).build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity(Map.of("error", "Failed to generate affirmation: " + e.getMessage()))
                    .build();
        }
    }

    /**
     * Generate an affirmation using query parameters (convenience endpoint).
     * 
     * @param seed Optional seed for deterministic generation
     * @return Response containing the generated affirmation
     */
    @POST
    @Produces(MediaType.APPLICATION_JSON)
    public Response generateAffirmationFromQuery(@QueryParam("seed") String seed) {
        AffirmationRequest request = new AffirmationRequest();
        request.setData(null); // Empty data
        request.setSeed(seed);
        return generateAffirmation(request);
    }
}

