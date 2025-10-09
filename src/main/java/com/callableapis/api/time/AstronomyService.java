package com.callableapis.api.time;

import java.time.ZonedDateTime;
import java.time.temporal.ChronoField;

public class AstronomyService {

	public static class MoonPhaseResult {
		public double phase; // 0.0 new -> 1.0 next new
		public double illumination; // 0..1 fraction illuminated
		public double ageDays; // days since new moon
		public String phaseName;
		public double phaseAngleDeg; // 0..360, 0=new, 180=full
		public boolean waxing;
		public boolean waning;
		public boolean crescent;
		public boolean gibbous;
		public boolean quarter;
		public boolean full;
		public boolean isNew;
	}

	public static class SolarInfoResult {
		public double elevationDeg; // solar elevation angle
		public double azimuthDeg;   // solar azimuth (0=N)
		public double intensity;    // 0..1 simple proxy = cos(zenith) clipped
		public boolean isDaylight;
		public double dayLengthHours;   // approximate for the date
		public double nightLengthHours; // 24 - dayLength
	}

	public static class MoonlightInfoResult {
		public double elevationDeg; // lunar elevation angle
		public double azimuthDeg;   // lunar azimuth (0=N)
		public double intensity;    // 0..1 proxy << 1, scaled by illumination and elevation
		public boolean aboveHorizon;
		public double illumination; // 0..1 from phase
	}

	// Synodic month length in days
	private static final double SYNODIC_MONTH = 29.530588853;

	// Reference new moon: 2000-01-06 18:14 UTC (JDN 2451550.1)
	private static final double REF_NEW_MOON_JDN = 2451550.1;

	public MoonPhaseResult computeMoonPhase(ZonedDateTime dateTimeUtc) {
		double jdn = toJulianDay(dateTimeUtc);
		double daysSince = jdn - REF_NEW_MOON_JDN;
		double phase = mod(daysSince / SYNODIC_MONTH, 1.0);
		double ageDays = phase * SYNODIC_MONTH;
		// Approx illuminated fraction
		double illumination = 0.5 * (1.0 - Math.cos(2.0 * Math.PI * phase));
		MoonPhaseResult r = new MoonPhaseResult();
		r.phase = phase;
		r.illumination = illumination;
		r.ageDays = ageDays;
		r.phaseName = phaseName(phase);
		r.phaseAngleDeg = (phase * 360.0) % 360.0;
		r.waxing = phase > 0.0 && phase < 0.5;
		r.waning = phase > 0.5 && phase < 1.0;
		r.crescent = (phase > 0.0 && phase < 0.25) || (phase > 0.75 && phase < 1.0);
		r.gibbous = (phase > 0.25 && phase < 0.5) || (phase > 0.5 && phase < 0.75);
		r.quarter = (phase >= 0.22 && phase <= 0.28) || (phase >= 0.72 && phase <= 0.78);
		r.full = (phase >= 0.47 && phase <= 0.53);
		r.isNew = (phase <= 0.03 || phase >= 0.97);
		return r;
	}

	public SolarInfoResult computeSolarInfo(ZonedDateTime dateTimeUtc, double latitudeDeg, double longitudeDeg) {
		// Based on NOAA approximate equations
		int dayOfYear = dateTimeUtc.getDayOfYear();
		double minutes = dateTimeUtc.get(ChronoField.HOUR_OF_DAY) * 60.0 + dateTimeUtc.get(ChronoField.MINUTE_OF_HOUR) + dateTimeUtc.get(ChronoField.SECOND_OF_MINUTE) / 60.0;
		double gamma = 2.0 * Math.PI / 365.0 * (dayOfYear - 1 + (minutes - 720.0) / 1440.0);

		// Solar declination
		double decl = 0.006918
				- 0.399912 * Math.cos(gamma)
				+ 0.070257 * Math.sin(gamma)
				- 0.006758 * Math.cos(2 * gamma)
				+ 0.000907 * Math.sin(2 * gamma)
				- 0.002697 * Math.cos(3 * gamma)
				+ 0.00148  * Math.sin(3 * gamma);

		// Equation of time (minutes)
		double eot = 229.18 * (0.000075
				+ 0.001868 * Math.cos(gamma)
				- 0.032077 * Math.sin(gamma)
				- 0.014615 * Math.cos(2 * gamma)
				- 0.040849 * Math.sin(2 * gamma));

		// True solar time (minutes)
		double tst = minutes + eot + 4.0 * longitudeDeg; // tz offset = 0 for UTC
		double hourAngleDeg = (tst / 4.0) - 180.0;
		// Normalize to [-180, 180]
		hourAngleDeg = normalizeDegrees(hourAngleDeg);

		double latRad = Math.toRadians(latitudeDeg);
		double declRad = decl;
		double hraRad = Math.toRadians(hourAngleDeg);

		double cosZenith = Math.sin(latRad) * Math.sin(declRad) + Math.cos(latRad) * Math.cos(declRad) * Math.cos(hraRad);
		cosZenith = clamp(cosZenith, -1.0, 1.0);
		double zenithRad = Math.acos(cosZenith);
		double elevationDeg = 90.0 - Math.toDegrees(zenithRad);
		// Azimuth calculation
		double sinAz = -Math.sin(hraRad) * Math.cos(declRad) / Math.sin(zenithRad);
		double cosAz = (Math.sin(declRad) - Math.sin(latRad) * Math.cos(zenithRad)) / (Math.cos(latRad) * Math.sin(zenithRad));
		double azimuthRad = Math.atan2(sinAz, cosAz);
		double azimuthDeg = (Math.toDegrees(azimuthRad) + 360.0) % 360.0; // 0=N, 90=E

		// Intensity proxy: cos(zenith) clipped to [0,1]
		double intensity = Math.max(0.0, cosZenith);

		// Day length (hours) using sunrise hour angle formula
		double cosH0 = -Math.tan(latRad) * Math.tan(declRad);
		if (cosH0 > 1.0) {
			cosH0 = 1.0;
		}
		if (cosH0 < -1.0) {
			cosH0 = -1.0;
		}
		double h0 = Math.acos(cosH0); // radians
		double dayLenHours = 2.0 * Math.toDegrees(h0) / 15.0; // 15 deg per hour
		// Handle polar day/night extremes
		if (Double.isNaN(dayLenHours)) {
			dayLenHours = cosH0 <= -1.0 ? 24.0 : 0.0;
		}

		SolarInfoResult r = new SolarInfoResult();
		r.elevationDeg = elevationDeg;
		r.azimuthDeg = azimuthDeg;
		r.intensity = intensity;
		r.isDaylight = elevationDeg > 0.0;
		r.dayLengthHours = dayLenHours;
		r.nightLengthHours = 24.0 - dayLenHours;
		return r;
	}

	public MoonlightInfoResult computeMoonlightInfo(ZonedDateTime dateTimeUtc, double latitudeDeg, double longitudeDeg) {
		// Use sun equations as a base, then shift by lunar phase and inclination to approximate moon position
		MoonPhaseResult mp = computeMoonPhase(dateTimeUtc);
		int dayOfYear = dateTimeUtc.getDayOfYear();
		double minutes = dateTimeUtc.get(ChronoField.HOUR_OF_DAY) * 60.0 + dateTimeUtc.get(ChronoField.MINUTE_OF_HOUR) + dateTimeUtc.get(ChronoField.SECOND_OF_MINUTE) / 60.0;
		double gamma = 2.0 * Math.PI / 365.0 * (dayOfYear - 1 + (minutes - 720.0) / 1440.0);

		// Base solar declination
		double declSun = 0.006918
				- 0.399912 * Math.cos(gamma)
				+ 0.070257 * Math.sin(gamma)
				- 0.006758 * Math.cos(2 * gamma)
				+ 0.000907 * Math.sin(2 * gamma)
				- 0.002697 * Math.cos(3 * gamma)
				+ 0.00148  * Math.sin(3 * gamma);

		// Approximate lunar declination: solar declination plus up to ±5° depending on phase
		double declMoon = declSun + Math.toRadians(5.145) * Math.sin(Math.toRadians(mp.phaseAngleDeg));

		// Equation of time (minutes) for sun
		double eot = 229.18 * (0.000075
				+ 0.001868 * Math.cos(gamma)
				- 0.032077 * Math.sin(gamma)
				- 0.014615 * Math.cos(2 * gamma)
				- 0.040849 * Math.sin(2 * gamma));

		// True solar time (minutes)
		double tst = minutes + eot + 4.0 * longitudeDeg;
		double hourAngleSunDeg = (tst / 4.0) - 180.0;
		hourAngleSunDeg = normalizeDegrees(hourAngleSunDeg);

		// Approximate lunar hour angle: shift sun by phase*180° (new ~ sun, full ~ opposite)
		double hourAngleMoonDeg = normalizeDegrees(hourAngleSunDeg + (mp.phase * 360.0 / 2.0)); // 0.5 phase -> +180°

		double latRad = Math.toRadians(latitudeDeg);
		double declRad = declMoon;
		double hraRad = Math.toRadians(hourAngleMoonDeg);

		double cosZenith = Math.sin(latRad) * Math.sin(declRad) + Math.cos(latRad) * Math.cos(declRad) * Math.cos(hraRad);
		cosZenith = clamp(cosZenith, -1.0, 1.0);
		double zenithRad = Math.acos(cosZenith);
		double elevationDeg = 90.0 - Math.toDegrees(zenithRad);
		// Azimuth
		double sinAz = -Math.sin(hraRad) * Math.cos(declRad) / Math.sin(zenithRad);
		double cosAz = (Math.sin(declRad) - Math.sin(latRad) * Math.cos(zenithRad)) / (Math.cos(latRad) * Math.sin(zenithRad));
		double azimuthRad = Math.atan2(sinAz, cosAz);
		double azimuthDeg = (Math.toDegrees(azimuthRad) + 360.0) % 360.0;

		// Intensity proxy: illumination * cos(zenith) clipped, scaled to ensure < 1.0
		double raw = Math.max(0.0, cosZenith) * mp.illumination;
		// Scale down lunar intensity to a small fraction (moon is far dimmer than sun)
		double intensity = Math.min(0.2, raw * 0.2); // cap at 0.2

		MoonlightInfoResult r = new MoonlightInfoResult();
		r.elevationDeg = elevationDeg;
		r.azimuthDeg = azimuthDeg;
		r.intensity = intensity;
		r.aboveHorizon = elevationDeg > 0.0;
		r.illumination = mp.illumination;
		return r;
	}

	private static String phaseName(double phase) {
		if (phase < 0.03 || phase > 0.97) {
			return "New Moon";
		}
		if (phase < 0.22) {
			return "Waxing Crescent";
		}
		if (phase < 0.28) {
			return "First Quarter";
		}
		if (phase < 0.47) {
			return "Waxing Gibbous";
		}
		if (phase < 0.53) {
			return "Full Moon";
		}
		if (phase < 0.72) {
			return "Waning Gibbous";
		}
		if (phase < 0.78) {
			return "Last Quarter";
		}
		return "Waning Crescent";
	}

	private static double toJulianDay(ZonedDateTime dt) {
		int Y = dt.getYear();
		int M = dt.getMonthValue();
		int D = dt.getDayOfMonth();
		double hour = dt.getHour() + dt.getMinute() / 60.0 + dt.getSecond() / 3600.0;
		if (M <= 2) { Y -= 1; M += 12; }
		int A = Y / 100;
		int B = 2 - A + (A / 4);
		double jd = Math.floor(365.25 * (Y + 4716)) + Math.floor(30.6001 * (M + 1)) + D + B - 1524.5 + hour / 24.0;
		return jd;
	}

	private static double mod(double x, double m) {
		double r = x % m;
		return r < 0 ? r + m : r;
	}

	private static double normalizeDegrees(double deg) {
		double d = deg % 360.0;
		if (d < -180.0) {
			d += 360.0;
		}
		if (d > 180.0) {
			d -= 360.0;
		}
		return d;
	}

	private static double clamp(double v, double lo, double hi) {
		return Math.max(lo, Math.min(hi, v));
	}
}

