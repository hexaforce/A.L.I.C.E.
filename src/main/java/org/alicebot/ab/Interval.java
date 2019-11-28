package org.alicebot.ab;

import org.joda.time.Chronology;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Months;
import org.joda.time.ReadableInstant;
import org.joda.time.Years;
import org.joda.time.chrono.GregorianChronology;
import org.joda.time.chrono.LenientChronology;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class Interval {

	public static void test() {
		String date1 = "23:59:59.00";
		String date2 = "12:00:00.00";
		String format = "HH:mm:ss.SS";
		int hours = getHoursBetween(date2, date1, format);
		System.out.println("Hours = " + hours);
		date1 = "January 30, 2013";
		date2 = "August 2, 1960";
		format = "MMMMMMMMM dd, yyyy";
		int years = getYearsBetween(date2, date1, format);
		System.out.println("Years = " + years);
	}

	public static int getHoursBetween(String date1, String date2, String format) {
		try {
			DateTimeFormatter fmt = DateTimeFormat.forPattern(format).withChronology((Chronology) LenientChronology.getInstance((Chronology) GregorianChronology.getInstance()));
			return Hours.hoursBetween((ReadableInstant) fmt.parseDateTime(date1), (ReadableInstant) fmt.parseDateTime(date2)).getHours();
		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	public static int getYearsBetween(String date1, String date2, String format) {
		try {
			DateTimeFormatter fmt = DateTimeFormat.forPattern(format).withChronology((Chronology) LenientChronology.getInstance((Chronology) GregorianChronology.getInstance()));

			return Years.yearsBetween((ReadableInstant) fmt.parseDateTime(date1), (ReadableInstant) fmt.parseDateTime(date2)).getYears();

		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	public static int getMonthsBetween(String date1, String date2, String format) {
		try {
			DateTimeFormatter fmt = DateTimeFormat.forPattern(format).withChronology((Chronology) LenientChronology.getInstance((Chronology) GregorianChronology.getInstance()));

			return Months.monthsBetween((ReadableInstant) fmt.parseDateTime(date1), (ReadableInstant) fmt.parseDateTime(date2)).getMonths();

		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}

	public static int getDaysBetween(String date1, String date2, String format) {
		try {
			DateTimeFormatter fmt = DateTimeFormat.forPattern(format).withChronology((Chronology) LenientChronology.getInstance((Chronology) GregorianChronology.getInstance()));

			return Days.daysBetween((ReadableInstant) fmt.parseDateTime(date1), (ReadableInstant) fmt.parseDateTime(date2)).getDays();

		} catch (Exception ex) {
			ex.printStackTrace();
			return 0;
		}
	}
}
