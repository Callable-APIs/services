package com.callableapis.api.handlers.v1;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import java.util.Calendar;
import java.util.TimeZone;


@Path("v1/calendar")
public class CalendarResource {
    private static final TimeZone GMT = TimeZone.getTimeZone("GMT");

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
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeZone(GMT);

        return new DateStruct(
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        );
    }
}
