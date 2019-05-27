//
// Decompiled by Procyon v0.5.30
//

package me.onenrico.morevaultplus.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeManager {
	public static String formatTime(long second) {
		int day = 0;
		int hour = 0;
		int minute = 0;
		while (second >= 86400L) {
			++day;
			second -= 86400L;
		}
		while (second >= 3600L) {
			++hour;
			second -= 3600L;
		}
		while (second >= 60L) {
			++minute;
			second -= 60L;
		}
		final StringBuilder build = new StringBuilder();
		if (day > 0) {
			build.append(String.valueOf(day) + " Day");
			if (day > 1) {
				build.append('s');
			}
			build.append(" ");
		}
		if (hour > 0) {
			build.append(String.valueOf(hour) + " Hour");
			if (hour > 1) {
				build.append('s');
			}
			build.append(" ");
		}
		if (minute > 0) {
			build.append(String.valueOf(minute) + " Minute");
			if (minute > 1) {
				build.append('s');
			}
			build.append(" ");
		}
		if (second > 0L) {
			build.append(String.valueOf(second) + " Second");
			if (second > 1L) {
				build.append('s');
			}
		}
		return build.toString();
	}

	public static Date fromString(String date) {
		final DateFormat df = new SimpleDateFormat("dd/MM/yyyy H:mm:ss:SSS");
		Date startDate = null;
		try {
			if (date == null || date.equalsIgnoreCase("null")) {
				date = toString(new Date());
			}
			startDate = df.parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return startDate;
	}

	public static Date getNow() {
		return new Date();
	}

	public static String toString(final Date date) {
		final DateFormat df = new SimpleDateFormat("dd/MM/yyyy H:mm:ss:SSS");
		return df.format(date);
	}

	public static int getDay(final Date date1, final Date date2) {
		final long diff = getDifferent(date1, date2);
		final int days = (int) (diff / 86400L);
		return days;
	}

	public static long getSecond(final Date date1, final Date date2) {
		final long diff = getDifferent(date1, date2);
		return diff / 1000L;
	}

	public static long getDifferent(final Date date1, final Date date2) {
		final long diff = date2.getTime() - date1.getTime();
		return diff;
	}

	public static Date getFuture(final Date date, final int day) {
		final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy H:mm:ss:SSS");
		final Calendar c = Calendar.getInstance();
		c.setTime(date);
		c.add(5, day);
		final String output = df.format(c.getTime());
		return fromString(output);
	}
}
