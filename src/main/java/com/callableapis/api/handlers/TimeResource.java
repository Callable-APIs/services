package com.callableapis.api.handlers;

import com.callableapis.api.time.DateTimeService;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Path("/time")
public class TimeResource {
    private final DateTimeService dateTimeService = new DateTimeService();

    public static class TimeResponse {
        private String iso;
        private long unix;
        private String rfc2822;

        public TimeResponse() {
        }

        public TimeResponse(ZonedDateTime zdt) {
            this.iso = zdt.withZoneSameInstant(ZoneOffset.UTC).toString();
            this.unix = zdt.toEpochSecond();
            this.rfc2822 = zdt.format(DateTimeFormatter.RFC_1123_DATE_TIME);
        }

        public String getIso() {
            return iso;
        }

        public void setIso(String iso) {
            this.iso = iso;
        }

        public long getUnix() {
            return unix;
        }

        public void setUnix(long unix) {
            this.unix = unix;
        }

        public String getRfc2822() {
            return rfc2822;
        }

        public void setRfc2822(String rfc2822) {
            this.rfc2822 = rfc2822;
        }
    }

    @GET
    @Path("/now")
    @Produces(MediaType.APPLICATION_JSON)
    public TimeResponse getCurrentTime() {
        ZonedDateTime now = dateTimeService.nowUtc();
        return new TimeResponse(now);
    }
}
