package com.callableapis.api.handlers.v1;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import com.callableapis.api.time.DateTimeService;
import java.time.ZonedDateTime;


@Path("/v1/calendar")
public class CalendarResource {
    private final DateTimeService dateTimeService = new DateTimeService();

    public static class DateStruct {
        int year;
        int month;
        int day;

        public DateStruct(int year, int month, int day) {
            this.year = year;
            this.month = month;
            this.day = day;
        }
        public DateStruct() {
            this(0, 0, 0);
        }

        public int getYear() {
            return year;
        }

        public void setYear(int year) {
            this.year = year;
        }

        public int getMonth() {
            return month;
        }

        public void setMonth(int month) {
            this.month = month;
        }

        public int getDay() {
            return day;
        }

        public void setDay(int day) {
            this.day = day;
        }
    }

    @Path("date")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public DateStruct getDateAsStruct() {
        ZonedDateTime now = dateTimeService.nowUtc();
        int zeroBasedMonth = now.getMonthValue() - 1; // preserve v1 0-based month
        return new DateStruct(
            now.getYear(),
            zeroBasedMonth,
            now.getDayOfMonth()
        );
    }
}
