package com.callableapis.api.time;

import java.time.Duration;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

public class DateTimeService {

	public static class Delta {
		public int years;
		public int months;
		public int days;
		public int hours;
		public int minutes;
		public int seconds;

		public Delta() {
			this(0, 0, 0, 0, 0, 0);
		}

		public Delta(int years, int months, int days, int hours, int minutes, int seconds) {
			this.years = years;
			this.months = months;
			this.days = days;
			this.hours = hours;
			this.minutes = minutes;
			this.seconds = seconds;
		}
	}

	public static class DiffResult {
		public long totalSeconds;
		public boolean inPast;
		public long days;
		public int hours;
		public int minutes;
		public int seconds;

		public DiffResult() {}

		public DiffResult(long totalSeconds, boolean inPast, long days, int hours, int minutes, int seconds) {
			this.totalSeconds = totalSeconds;
			this.inPast = inPast;
			this.days = days;
			this.hours = hours;
			this.minutes = minutes;
			this.seconds = seconds;
		}
	}

	public ZonedDateTime nowUtc() {
		return ZonedDateTime.now(ZoneOffset.UTC);
	}

	public ZonedDateTime shift(ZonedDateTime baseUtc, Delta delta) {
		if (baseUtc == null) {
			baseUtc = nowUtc();
		}
		ZonedDateTime shifted = baseUtc
			.plusYears(delta != null ? delta.years : 0)
			.plusMonths(delta != null ? delta.months : 0)
			.plusDays(delta != null ? delta.days : 0)
			.plusHours(delta != null ? delta.hours : 0)
			.plusMinutes(delta != null ? delta.minutes : 0)
			.plusSeconds(delta != null ? delta.seconds : 0);
		return shifted;
	}

	public DiffResult diff(ZonedDateTime fromUtc, ZonedDateTime toUtc) {
		if (fromUtc == null || toUtc == null) {
			throw new IllegalArgumentException("fromUtc and toUtc must be non-null");
		}
		long seconds = Duration.between(fromUtc, toUtc).getSeconds();
		boolean inPast = seconds < 0;
		long abs = Math.abs(seconds);
		long days = abs / 86400L;
		long rem = abs % 86400L;
		int hours = (int)(rem / 3600L);
		rem = rem % 3600L;
		int minutes = (int)(rem / 60L);
		int secs = (int)(rem % 60L);
		return new DiffResult(seconds, inPast, days, hours, minutes, secs);
	}
}

