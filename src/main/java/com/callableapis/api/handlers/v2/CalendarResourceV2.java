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

	private static BaseDateTime copyBase(BaseDateTime in) {
		if (in == null) {
			return null;
		}
		BaseDateTime out = new BaseDateTime();
		out.year = in.year;
		out.month = in.month;
		out.day = in.day;
		out.hour = in.hour;
		out.minute = in.minute;
		out.second = in.second;
		return out;
	}

	public static class ShiftRequest {
		BaseDateTime base;
		DateTimeService.Delta delta;

		public BaseDateTime getBase() { return copyBase(base); }
		public void setBase(BaseDateTime base) { this.base = copyBase(base); }
		public DateTimeService.Delta getDelta() { return delta; }
		public void setDelta(DateTimeService.Delta delta) { this.delta = delta; }
	}

	public static class DiffRequest {
		BaseDateTime from;
		BaseDateTime to; // optional; if null, diff to now

		public BaseDateTime getFrom() { return copyBase(from); }
		public void setFrom(BaseDateTime from) { this.from = copyBase(from); }
		public BaseDateTime getTo() { return copyBase(to); }
		public void setTo(BaseDateTime to) { this.to = copyBase(to); }
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

		public double getPhase() { return phase; }
		public void setPhase(double phase) { this.phase = phase; }
		public double getIllumination() { return illumination; }
		public void setIllumination(double illumination) { this.illumination = illumination; }
		public double getAgeDays() { return ageDays; }
		public void setAgeDays(double ageDays) { this.ageDays = ageDays; }
		public String getPhaseName() { return phaseName; }
		public void setPhaseName(String phaseName) { this.phaseName = phaseName; }
		public double getPhaseAngleDeg() { return phaseAngleDeg; }
		public void setPhaseAngleDeg(double phaseAngleDeg) { this.phaseAngleDeg = phaseAngleDeg; }
		public boolean isWaxing() { return waxing; }
		public void setWaxing(boolean waxing) { this.waxing = waxing; }
		public boolean isWaning() { return waning; }
		public void setWaning(boolean waning) { this.waning = waning; }
		public boolean isCrescent() { return crescent; }
		public void setCrescent(boolean crescent) { this.crescent = crescent; }
		public boolean isGibbous() { return gibbous; }
		public void setGibbous(boolean gibbous) { this.gibbous = gibbous; }
		public boolean isQuarter() { return quarter; }
		public void setQuarter(boolean quarter) { this.quarter = quarter; }
		public boolean isFull() { return full; }
		public void setFull(boolean full) { this.full = full; }
		public boolean isNew() { return isNew; }
		public void setNew(boolean aNew) { isNew = aNew; }
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
		public BaseDateTime getAt() { return copyBase(at); }
		public void setAt(BaseDateTime at) { this.at = copyBase(at); }
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
		public Double getLat() { return lat; }
		public void setLat(Double lat) { this.lat = lat; }
		public Double getLon() { return lon; }
		public void setLon(Double lon) { this.lon = lon; }
		public BaseDateTime getAt() { return copyBase(at); }
		public void setAt(BaseDateTime at) { this.at = copyBase(at); }
	}

	public static class SolarResponse {
		public double elevationDeg;
		public double azimuthDeg;
		public double intensity;
		public boolean daylight;
		public double dayLengthHours;
		public double nightLengthHours;
		public double getElevationDeg() { return elevationDeg; }
		public void setElevationDeg(double elevationDeg) { this.elevationDeg = elevationDeg; }
		public double getAzimuthDeg() { return azimuthDeg; }
		public void setAzimuthDeg(double azimuthDeg) { this.azimuthDeg = azimuthDeg; }
		public double getIntensity() { return intensity; }
		public void setIntensity(double intensity) { this.intensity = intensity; }
		public boolean isDaylight() { return daylight; }
		public void setDaylight(boolean daylight) { this.daylight = daylight; }
		public double getDayLengthHours() { return dayLengthHours; }
		public void setDayLengthHours(double dayLengthHours) { this.dayLengthHours = dayLengthHours; }
		public double getNightLengthHours() { return nightLengthHours; }
		public void setNightLengthHours(double nightLengthHours) { this.nightLengthHours = nightLengthHours; }
	}

	public static class MoonlightRequest {
		public Double lat;
		public Double lon;
		public BaseDateTime at; // optional, defaults to now
		public Double getLat() { return lat; }
		public void setLat(Double lat) { this.lat = lat; }
		public Double getLon() { return lon; }
		public void setLon(Double lon) { this.lon = lon; }
		public BaseDateTime getAt() { return copyBase(at); }
		public void setAt(BaseDateTime at) { this.at = copyBase(at); }
	}

	public static class MoonlightResponse {
		public double elevationDeg;
		public double azimuthDeg;
		public double intensity; // 0..1, << 1
		public boolean aboveHorizon;
		public double illumination;
		public double getElevationDeg() { return elevationDeg; }
		public void setElevationDeg(double elevationDeg) { this.elevationDeg = elevationDeg; }
		public double getAzimuthDeg() { return azimuthDeg; }
		public void setAzimuthDeg(double azimuthDeg) { this.azimuthDeg = azimuthDeg; }
		public double getIntensity() { return intensity; }
		public void setIntensity(double intensity) { this.intensity = intensity; }
		public boolean isAboveHorizon() { return aboveHorizon; }
		public void setAboveHorizon(boolean aboveHorizon) { this.aboveHorizon = aboveHorizon; }
		public double getIllumination() { return illumination; }
		public void setIllumination(double illumination) { this.illumination = illumination; }
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

	@POST
	@Path("moonlight")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public MoonlightResponse moonlight(MoonlightRequest req) {
		if (req == null || req.lat == null || req.lon == null) {
			throw new IllegalArgumentException("lat and lon are required");
		}
		ZonedDateTime at = toZoned(req.at);
		AstronomyService.MoonlightInfoResult mi = astronomyService.computeMoonlightInfo(at, req.lat, req.lon);
		MoonlightResponse out = new MoonlightResponse();
		out.elevationDeg = mi.elevationDeg;
		out.azimuthDeg = mi.azimuthDeg;
		out.intensity = mi.intensity;
		out.aboveHorizon = mi.aboveHorizon;
		out.illumination = mi.illumination;
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

