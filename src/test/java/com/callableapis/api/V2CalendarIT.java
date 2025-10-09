package com.callableapis.api;

import com.callableapis.api.security.ApiKeyService;
import org.glassfish.jersey.test.JerseyTest;
import org.junit.Test;

import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Application;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import static org.junit.Assert.*;

public class V2CalendarIT extends JerseyTest {

	@Override
	protected Application configure() {
		return new APIApplication();
	}

	private String bearer() {
		String apiKey = ApiKeyService.getInstance().getOrCreateApiKeyForIdentity("github:testuser");
		return "Bearer " + apiKey;
	}

	@Test
	public void testAuthRequiredForV2() {
		Response r = target("v2/calendar/date").request().get();
		assertEquals(401, r.getStatus());
	}

	@Test
	public void testDateAndShiftAndDiff() {
		Response r1 = target("v2/calendar/date").request().header("Authorization", bearer()).get();
		assertEquals(200, r1.getStatus());

		String addPayload = "{\n" +
				"  \"base\": { \"year\": 2025, \"month\": 10, \"day\": 8, \"hour\": 12, \"minute\": 0, \"second\": 0 },\n" +
				"  \"delta\": { \"days\": 1, \"hours\": 2 }\n" +
				"}";
		Response r2 = target("v2/calendar/add").request().header("Authorization", bearer()).post(Entity.entity(addPayload, MediaType.APPLICATION_JSON));
		assertEquals(200, r2.getStatus());

		String diffPayload = "{\n" +
				"  \"from\": { \"year\": 2025, \"month\": 10, \"day\": 8, \"hour\": 12, \"minute\": 0, \"second\": 0 },\n" +
				"  \"to\":   { \"year\": 2025, \"month\": 10, \"day\": 9, \"hour\": 14, \"minute\": 0, \"second\": 0 }\n" +
				"}";
		Response r3 = target("v2/calendar/diff").request().header("Authorization", bearer()).post(Entity.entity(diffPayload, MediaType.APPLICATION_JSON));
		assertEquals(200, r3.getStatus());
		String body = r3.readEntity(String.class);
		assertTrue(body.contains("\"totalSeconds\""));
	}

	@Test
	public void testMoonPhaseDeterministic() {
		String payload = "{ \n" +
				"  \"at\": { \"year\": 2000, \"month\": 1, \"day\": 6, \"hour\": 18, \"minute\": 14, \"second\": 0 }\n" +
				"}";
		Response r = target("v2/calendar/moon-phase").request().header("Authorization", bearer()).post(Entity.entity(payload, MediaType.APPLICATION_JSON));
		assertEquals(200, r.getStatus());
		String json = r.readEntity(String.class);
		// Around reference new moon -> illumination near 0 and isNew true
		assertTrue(json.contains("\"illumination\""));
		assertTrue(json.contains("\"isNew\":true") || json.contains("\"isNew\" : true"));
	}

	@Test
	public void testSolarBasics() {
		String payload = "{ \n" +
				"  \"lat\": 37.7749, \"lon\": -122.4194, \n" +
				"  \"at\": { \"year\": 2025, \"month\": 6, \"day\": 21, \"hour\": 12, \"minute\": 0, \"second\": 0 }\n" +
				"}";
		Response r = target("v2/calendar/solar").request().header("Authorization", bearer()).post(Entity.entity(payload, MediaType.APPLICATION_JSON));
		assertEquals(200, r.getStatus());
		String json = r.readEntity(String.class);
		assertTrue(json.contains("\"intensity\""));
		assertTrue(json.contains("\"daylight\""));
	}
}

