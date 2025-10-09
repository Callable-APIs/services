package com.callableapis.api.handlers.v2;

import com.callableapis.api.time.DateTimeService;
import com.callableapis.api.time.AstronomyService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;

@Path("/v2/calendar")
public class CalendarResourceV2 {

	private final DateTimeService dateTimeService = new DateTimeService();
	private final AstronomyService astronomyService = new AstronomyService();

	public static class DateTimeStruct {
		int year;
		int month; // 1-12 in v2
		int day;
		int hour;
		int minute;
		int second;
		String iso;

		public DateTimeStruct() {
		}

		public DateTimeStruct(ZonedDateTime zdt) {
			this.year = zdt.getYear();
			this.month = zdt.getMonthValue();
			this.day = zdt.getDayOfMonth();
			this.hour = zdt.getHour();
			this.minute = zdt.getMinute();
			this.second = zdt.getSecond();
			this.iso = zdt.withZoneSameInstant(ZoneOffset.UTC).toString();
		}

		public int getYear() { return year; }
		public void setYear(int year) { this.year = year; }
		public int getMonth() { return month; }
		public void setMonth(int month) { this.month = month; }
		public int getDay() { return day; }
		public void setDay(int day) { this.day = day; }
		public int getHour() { return hour; }
		public void setHour(int hour) { this.hour = hour; }
		public int getMinute() { return minute; }
		public void setMinute(int minute) { this.minute = minute; }
		public int getSecond() { return second; }
		public void setSecond(int second) { this.second = second; }
		public String getIso() { return iso; }
		public void setIso(String iso) { this.iso = iso; }
	}

	public static class BaseDateTime {
		// All fields optional; when missing, defaults will be applied
		Integer year;
		Integer month; // 1-12
		Integer day;
		Integer hour;
		Integer minute;
		Integer second;

		public Integer getYear() { return year; }
		public void setYear(Integer year) { this.year = year; }
		public Integer getMonth() { return month; }
		public void setMonth(Integer month) { this.month = month; }
		public Integer getDay() { return day; }
		public void setDay(Integer day) { this.day = day; }
		public Integer getHour() { return hour; }
		public void setHour(Integer hour) { this.hour = hour; }
		public Integer getMinute() { return minute; }
		public void setMinute(Integer minute) { this.minute = minute; }
		public Integer getSecond() { return second; }
		public void setSecond(Integer second) { this.second = second; }
	}

	public static class ShiftRequest {
		BaseDateTime base;
		DateTimeService.Delta delta;

		public BaseDateTime getBase() { return base; }
		public void setBase(BaseDateTime base) { this.base = base; }
		public DateTimeService.Delta getDelta() { return delta; }
		public void setDelta(DateTimeService.Delta delta) { this.delta = delta; }
	}

	public static class DiffRequest {
		BaseDateTime from;
		BaseDateTime to; // optional; if null, diff to now

		public BaseDateTime getFrom() { return from; }
		public void setFrom(BaseDateTime from) { this.from = from; }
		public BaseDateTime getTo() { return to; }
		public void setTo(BaseDateTime to) { this.to = to; }
	}

	public static class DiffResponse {
		long totalSeconds;
		boolean inPast;
		long days;
		int hours;
		int minutes;
		int seconds;

		public DiffResponse() {}

		public DiffResponse(DateTimeService.DiffResult r) {
			this.totalSeconds = r.totalSeconds;
			this.inPast = r.inPast;
			this.days = r.days;
			this.hours = r.hours;
			this.minutes = r.minutes;
			this.seconds = r.seconds;
		}

		public long getTotalSeconds() { return totalSeconds; }
		public void setTotalSeconds(long totalSeconds) { this.totalSeconds = totalSeconds; }
		public boolean isInPast() { return inPast; }
		public void setInPast(boolean inPast) { this.inPast = inPast; }
		public long getDays() { return days; }
		public void setDays(long days) { this.days = days; }
		public int getHours() { return hours; }
		public void setHours(int hours) { this.hours = hours; }
		public int getMinutes() { return minutes; }
		public void setMinutes(int minutes) { this.minutes = minutes; }
		public int getSeconds() { return seconds; }
		public void setSeconds(int seconds) { this.seconds = seconds; }
	}

	@GET
	@Path("date")
	@Produces(MediaType.APPLICATION_JSON)
	public DateTimeStruct getDateTime() {
		ZonedDateTime now = dateTimeService.nowUtc();
		return new DateTimeStruct(now);
	}

	@POST
	@Path("add")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public DateTimeStruct add(ShiftRequest request) {
		ZonedDateTime base = toZoned(request != null ? request.base : null);
		ZonedDateTime out = dateTimeService.shift(base, request != null ? request.delta : null);
		return new DateTimeStruct(out);
	}

	@POST
	@Path("subtract")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public DateTimeStruct subtract(ShiftRequest request) {
		ZonedDateTime base = toZoned(request != null ? request.base : null);
		DateTimeService.Delta d = negate(request != null ? request.delta : null);
		ZonedDateTime out = dateTimeService.shift(base, d);
		return new DateTimeStruct(out);
	}

	@POST
	@Path("diff")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public DiffResponse diff(DiffRequest request) {
		ZonedDateTime from = toZoned(request != null ? request.from : null);
		ZonedDateTime to = request != null && request.to != null ? toZoned(request.to) : dateTimeService.nowUtc();
		DateTimeService.DiffResult r = dateTimeService.diff(from, to);
		return new DiffResponse(r);
	}

	public static class MoonResponse {
		public double phase;
		public double illumination;
		public double ageDays;
		public String phaseName;
		public double phaseAngleDeg;
		public boolean waxing;
		public boolean waning;
		public boolean crescent;
		public boolean gibbous;
		public boolean quarter;
		public boolean full;
		public boolean isNew;
	}

	@GET
	@Path("moon-phase")
	@Produces(MediaType.APPLICATION_JSON)
	public MoonResponse moonPhase() {
		AstronomyService.MoonPhaseResult r = astronomyService.computeMoonPhase(dateTimeService.nowUtc());
		MoonResponse out = new MoonResponse();
		out.phase = r.phase;
		out.illumination = r.illumination;
		out.ageDays = r.ageDays;
		out.phaseName = r.phaseName;
		out.phaseAngleDeg = r.phaseAngleDeg;
		out.waxing = r.waxing;
		out.waning = r.waning;
		out.crescent = r.crescent;
		out.gibbous = r.gibbous;
		out.quarter = r.quarter;
		out.full = r.full;
		out.isNew = r.isNew;
		return out;
	}

	public static class MoonRequest {
		public BaseDateTime at;
	}

	@POST
	@Path("moon-phase")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public MoonResponse moonPhaseAt(MoonRequest req) {
		ZonedDateTime at = toZoned(req != null ? req.at : null);
		AstronomyService.MoonPhaseResult r = astronomyService.computeMoonPhase(at);
		MoonResponse out = new MoonResponse();
		out.phase = r.phase;
		out.illumination = r.illumination;
		out.ageDays = r.ageDays;
		out.phaseName = r.phaseName;
		out.phaseAngleDeg = r.phaseAngleDeg;
		out.waxing = r.waxing;
		out.waning = r.waning;
		out.crescent = r.crescent;
		out.gibbous = r.gibbous;
		out.quarter = r.quarter;
		out.full = r.full;
		out.isNew = r.isNew;
		return out;
	}

	public static class SolarRequest {
		public Double lat;
		public Double lon;
		public BaseDateTime at; // optional, defaults to now
	}

	public static class SolarResponse {
		public double elevationDeg;
		public double azimuthDeg;
		public double intensity;
		public boolean daylight;
		public double dayLengthHours;
		public double nightLengthHours;
	}

	@POST
	@Path("solar")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public SolarResponse solar(SolarRequest req) {
		if (req == null || req.lat == null || req.lon == null) {
			throw new IllegalArgumentException("lat and lon are required");
		}
		ZonedDateTime at = toZoned(req.at);
		AstronomyService.SolarInfoResult si = astronomyService.computeSolarInfo(at, req.lat, req.lon);
		SolarResponse out = new SolarResponse();
		out.elevationDeg = si.elevationDeg;
		out.azimuthDeg = si.azimuthDeg;
		out.intensity = si.intensity;
		out.daylight = si.isDaylight;
		out.dayLengthHours = si.dayLengthHours;
		out.nightLengthHours = si.nightLengthHours;
		return out;
	}

	private ZonedDateTime toZoned(BaseDateTime base) {
		if (base == null) {
			return dateTimeService.nowUtc();
		}
		ZonedDateTime now = dateTimeService.nowUtc();
		int year = base.year != null ? base.year : now.getYear();
		int month = base.month != null ? base.month : now.getMonthValue();
		int day = base.day != null ? base.day : now.getDayOfMonth();
		int hour = base.hour != null ? base.hour : now.getHour();
		int minute = base.minute != null ? base.minute : now.getMinute();
		int second = base.second != null ? base.second : now.getSecond();
		return ZonedDateTime.of(year, month, day, hour, minute, second, 0, ZoneOffset.UTC);
	}

	private static DateTimeService.Delta negate(DateTimeService.Delta d) {
		if (d == null) {
			return new DateTimeService.Delta();
		}
		return new DateTimeService.Delta(-d.years, -d.months, -d.days, -d.hours, -d.minutes, -d.seconds);
	}
}

