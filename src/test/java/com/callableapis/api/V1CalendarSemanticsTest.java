package com.callableapis.api;

import com.callableapis.api.handlers.v1.CalendarResource;
import org.junit.Test;
import static org.junit.Assert.*;

public class V1CalendarSemanticsTest {
	@Test
	public void testMonthIsZeroBased() {
		CalendarResource r = new CalendarResource();
		CalendarResource.DateStruct ds = r.getDateAsStruct();
		assertTrue("month should be 0..11", ds.getMonth() >= 0 && ds.getMonth() <= 11);
	}
}

