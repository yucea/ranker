package kr.co.esjee.ranker.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;

public class CalendarUtil {

	public static final String SIMPLE_FORMAT = "yyyyMMdd";
	public static final String DISPLAY_FORMAT = "yyyy.MM.dd";
	public static final String DETAIL_FORMAT = "yyyyMMdd-HHmmss";

	public static int getYear() {
		return Calendar.getInstance().get(Calendar.YEAR);
	}

	public static int getMonth() {
		return Calendar.getInstance().get(Calendar.MONTH) + 1;
	}

	public static String getCurrentDate() {
		return getCurrentDate(SIMPLE_FORMAT);
	}

	public static String getCurrentDateTime() {
		return getCurrentDate(DETAIL_FORMAT);
	}

	public static Calendar getToday() {
		return Calendar.getInstance();
	}

	public static String getCurrentDate(String formatString) {
		if (StringUtils.isNotEmpty(formatString)) {
			return new SimpleDateFormat(formatString).format(Calendar.getInstance().getTime());
		} else {
			return new SimpleDateFormat().format(Calendar.getInstance().getTime());
		}
	}

	public static Date getDate(String source) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(SIMPLE_FORMAT);
		return format.parse(source);
	}

	public static Date getDate(String source, String formatString) throws ParseException {
		SimpleDateFormat format = new SimpleDateFormat(formatString);
		return format.parse(source);
	}

	public static String getPreviousTime(int minute) {
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.MINUTE, -minute);
		return new SimpleDateFormat(DETAIL_FORMAT).format(calendar.getTime());
	}

	public static boolean withinDate(String date, int dist) throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.DAY_OF_MONTH, -dist);

		return getDate(date).compareTo(cal.getTime()) > -1;
	}

	public static boolean within3Months(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -3);

		return date.compareTo(cal.getTime()) > -1;
	}

	public static boolean withinYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -1);

		return date.compareTo(cal.getTime()) > -1;
	}

	public static boolean withinTenYear(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.YEAR, -10);

		return date.compareTo(cal.getTime()) > -1;
	}

	public static long distanceHour(String t1, String t2) throws ParseException {
		Date d1 = getDate(t1, DETAIL_FORMAT);
		Date d2 = getDate(t2, DETAIL_FORMAT);

		return Math.abs(d1.getTime() - d2.getTime()) / (1000 * 60 * 60);
	}

	public static Date nextday(Date date) throws ParseException {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, 1);

		return cal.getTime();
	}

	public static String getString(Date date) {
		return new SimpleDateFormat(SIMPLE_FORMAT).format(date.getTime());
	}

}