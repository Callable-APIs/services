package com.callableapis.api.handlers.v2;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import jakarta.ws.rs.core.Response;
import java.util.Map;
import java.util.HashMap;

public class AffirmationResourceV2Test {

    private AffirmationResourceV2 resource;

    public AffirmationResourceV2Test() {
        this.resource = new AffirmationResourceV2();
    }

    @Test
    void testGenerateAffirmationWithData() {
        AffirmationResourceV2.AffirmationRequest request = new AffirmationResourceV2.AffirmationRequest();
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("name", "TestUser");
        inputData.put("mood", "happy");
        request.setData(inputData);
        request.setSeed("test-seed");

        Response response = resource.generateAffirmation(request);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
        assertTrue(response.getEntity() instanceof AffirmationResourceV2.AffirmationResponse);

        AffirmationResourceV2.AffirmationResponse affirmationResponse = 
            (AffirmationResourceV2.AffirmationResponse) response.getEntity();
        assertNotNull(affirmationResponse.getAffirmation());
        assertFalse(affirmationResponse.getAffirmation().isEmpty());
        assertNotNull(affirmationResponse.getTone());
        assertNotNull(affirmationResponse.getKeywords());
        assertNotNull(affirmationResponse.getGeneratedAt());
        assertEquals("test-seed", affirmationResponse.getSeed());
    }

    @Test
    void testGenerateAffirmationWithNullRequest() {
        Response response = resource.generateAffirmation(null);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void testGenerateAffirmationWithEmptyData() {
        AffirmationResourceV2.AffirmationRequest request = new AffirmationResourceV2.AffirmationRequest();
        request.setData(new HashMap<>());
        request.setSeed("empty-test");

        Response response = resource.generateAffirmation(request);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());

        AffirmationResourceV2.AffirmationResponse affirmationResponse = 
            (AffirmationResourceV2.AffirmationResponse) response.getEntity();
        assertNotNull(affirmationResponse.getAffirmation());
        assertFalse(affirmationResponse.getAffirmation().isEmpty());
    }

    @Test
    void testGenerateAffirmationWithoutSeed() {
        AffirmationResourceV2.AffirmationRequest request = new AffirmationResourceV2.AffirmationRequest();
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("value", "creativity");
        request.setData(inputData);
        request.setSeed(null);

        Response response = resource.generateAffirmation(request);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());

        AffirmationResourceV2.AffirmationResponse affirmationResponse = 
            (AffirmationResourceV2.AffirmationResponse) response.getEntity();
        assertNotNull(affirmationResponse.getAffirmation());
        assertNotNull(affirmationResponse.getSeed());
    }

    @Test
    void testGenerateAffirmationFromQuery() {
        Response response = resource.generateAffirmationFromQuery("query-seed");

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());

        AffirmationResourceV2.AffirmationResponse affirmationResponse = 
            (AffirmationResourceV2.AffirmationResponse) response.getEntity();
        assertNotNull(affirmationResponse.getAffirmation());
    }

    @Test
    void testGenerateAffirmationFromQueryWithoutSeed() {
        Response response = resource.generateAffirmationFromQuery(null);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());
    }

    @Test
    void testGenerateAffirmationWithComplexData() {
        AffirmationResourceV2.AffirmationRequest request = new AffirmationResourceV2.AffirmationRequest();
        Map<String, Object> inputData = new HashMap<>();
        inputData.put("strength", "resilience");
        inputData.put("goal", "success");
        inputData.put("emotion", "grateful");
        inputData.put("number", 42);
        inputData.put("active", true);
        request.setData(inputData);
        request.setSeed("complex-test");

        Response response = resource.generateAffirmation(request);

        assertEquals(Response.Status.OK.getStatusCode(), response.getStatus());
        assertNotNull(response.getEntity());

        AffirmationResourceV2.AffirmationResponse affirmationResponse = 
            (AffirmationResourceV2.AffirmationResponse) response.getEntity();
        assertNotNull(affirmationResponse.getAffirmation());
        assertNotNull(affirmationResponse.getInputData());
        assertEquals(inputData, affirmationResponse.getInputData());
    }
}

