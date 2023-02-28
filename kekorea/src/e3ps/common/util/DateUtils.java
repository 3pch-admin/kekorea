package e3ps.common.util;

import java.sql.Timestamp;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import wt.util.WTProperties;

public class DateUtils {

	private static SimpleDateFormat dateFormat = null;
	private static SimpleDateFormat dateEndFormat = null;
	private static SimpleDateFormat defaultDateFormat = null;
	private static SimpleDateFormat defaultTimeFormat = null;

	static {
		dateFormat = new SimpleDateFormat("yyyy/MM/dd");
		dateEndFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		defaultDateFormat = new SimpleDateFormat("yyyy/MM/dd");
		defaultTimeFormat = new SimpleDateFormat("HH:mm:ss");

		try {
			String timezone = WTProperties.getLocalProperties().getProperty("wt.method.timezone");
			TimeZone zone = null;
			if (!StringUtils.isNull(timezone)) {
				zone = TimeZone.getTimeZone(timezone);
			}

			if (zone == null) {
				zone = TimeZone.getTimeZone("Asia/Seoul");
				// zone = TimeZone.getTimeZone("JST");
			}

			dateFormat.setTimeZone(zone);
			defaultDateFormat.setTimeZone(zone);
			defaultTimeFormat.setTimeZone(zone);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private DateUtils() {

	}

	public static Timestamp getPlanStartDate() {
		return new Timestamp(new Date().getTime());
	}

	public static Timestamp getPlanEndDate() {
		// SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
		Timestamp today = new Timestamp(new Date().getTime());
		Calendar ca = Calendar.getInstance();
		ca.setTime(today);
		ca.add(Calendar.DATE, 1);
		return new Timestamp(ca.getTime().getTime());
	}

	public static String formatTime(Timestamp time) {
		if (time == null) {
			return "";
		}
		return time.toString().substring(0, 10);
	}

	public static int getDuration(Timestamp start, Timestamp end) {

		if (StringUtils.isNull(start) || StringUtils.isNull(end)) {
			return 1;
		}

		Date startDate = new Date(start.getTime());
		Date endDate = new Date(end.getTime());
		return getDuration(startDate, endDate);
	}

	public static int getDuration(Date start, Date end) {
		Date before = null;
		Date after = null;

		if (start == null || end == null) {
			return 0;
		}

		if (start.getTime() < end.getTime()) {
			before = start;
			after = end;
		} else {
			after = start;
			before = end;
		}
		long millis = 24 * 60 * 60 * 1000;
		int day = (int) ((after.getTime() - before.getTime()) / millis);
		return day;
	}

	public static String getDateString(Date date, String type) {
		String param = "";
		if (StringUtils.isNull(date)) {
			return param;
		}

		if (type.equalsIgnoreCase("all")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
			param = sdf.format(date);
		} else if (type.equalsIgnoreCase("date")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
			param = sdf.format(date);
		} else if (type.equalsIgnoreCase("time")) {
			SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss", Locale.KOREA);
			param = sdf.format(date);
		} else if (type.equalsIgnoreCase("year")) {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy", Locale.KOREA);
			param = sdf.format(date);
		} else if (type.equalsIgnoreCase("day")) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd", Locale.KOREA);
			param = sdf.format(date);
		} else if (type.equals("month")) {
			SimpleDateFormat sdf = new SimpleDateFormat("MM", Locale.KOREA);
			param = sdf.format(date);
		} else {
			param = date.toString();
		}
		return param;
	}

	public static String getCurrentDateString(String type) {
		Date currentDate = new Date();

		if (type.equalsIgnoreCase("all") || type.equalsIgnoreCase("a")) {
			return defaultDateFormat.format(currentDate) + " " + defaultTimeFormat.format(currentDate);
		} else if (type.equalsIgnoreCase("date") || type.equalsIgnoreCase("d")) {
			return defaultDateFormat.format(currentDate);
		} else if (type.equalsIgnoreCase("time") || type.equalsIgnoreCase("t")) {
			return defaultTimeFormat.format(currentDate);
		} else if (type.equalsIgnoreCase("year") || type.equalsIgnoreCase("y")) {
			return new SimpleDateFormat("yyyy", Locale.KOREA).format(currentDate);
		} else if (type.equalsIgnoreCase("month") || type.equalsIgnoreCase("m")) {
			return new SimpleDateFormat("yyMM", Locale.KOREA).format(currentDate);
		} else {
			return currentDate.toString();
		}
	}

	public static Timestamp convertDate(String str) {
		if (StringUtils.isNull(str)) {
			return null;
		}
		str = str.trim().replaceAll("-", "/");
		Date date = dateFormat.parse(str + " 12:59:59", new ParsePosition(0));
		Timestamp convertDate = new Timestamp(date.getTime());
		return convertDate;
	}

	public static Timestamp convertStartDate(String str) {
		if (str == null || str.length() == 0)
			return null;
		str = str.trim().replaceAll("-", "/");
		Date date = dateFormat.parse(str, new ParsePosition(0));
		Timestamp startDate = new Timestamp(date.getTime());
		return startDate;
	}

	public static Timestamp convertEndDate(String str) {
		if (str == null || str.length() == 0)
			return null;
		str = str.trim().replaceAll("-", "/");
		Date date = dateEndFormat.parse(str + " 23:59:59", new ParsePosition(0));
		Timestamp endDate = new Timestamp(date.getTime());
		return endDate;
	}

	public static Timestamp getStartDate(String param) {
		Timestamp start = null;
		if (StringUtils.isNull(param)) {
			return start;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.KOREA);
		Date date = sdf.parse(param, new ParsePosition(0));
		start = new Timestamp(date.getTime());
		return start;
	}

	public static Timestamp getEndDate(String param) {
		Timestamp end = null;
		if (StringUtils.isNull(param)) {
			return end;
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.KOREA);
		Date date = sdf.parse(param + " 23:59:59", new ParsePosition(0));
		end = new Timestamp(date.getTime());
		return end;
	}

	public static Timestamp getCurrentTimestamp() {
		Date currentDate = new Date();
		return new Timestamp(currentDate.getTime());
	}

	public static int getPlanDurationHoliday(Timestamp planStart, Timestamp planEnd) {
		if (StringUtils.isNull(planStart) || StringUtils.isNull(planEnd)) {
			return 1;
		}
		int duration = DateUtils.getDuration(new Date(planStart.getTime()), new Date(planEnd.getTime()));
		int holiday = 0;
		try {
			holiday = getDurationWithoutHoliday(planStart, planEnd);
		} catch (Exception e) {
			e.printStackTrace();
		}

		int comp = duration - holiday;
		if (comp <= 0) {
			comp = 1;
		}
		return comp;
	}

	public static int getDurationWithoutHoliday(Timestamp start, Timestamp end) throws Exception {

		Calendar ca = Calendar.getInstance();
		ca.setTime(new Date(start.getTime()));

		Calendar eca = Calendar.getInstance();
		eca.setTime(new Date(end.getTime()));

		int duration = 0;
		// Vector<String> vec = getHolidayList();

		while (DateUtils.getDateString(ca.getTime(), "date")
				.compareTo(DateUtils.getDateString(eca.getTime(), "date")) <= 0) {

			if (isHoliday(ca)) {
				duration++;
			}
			ca.add(Calendar.DATE, 1);
		}
		return duration;
	}

	public static boolean isHoliday(Calendar ca) {
		int day_of_week = ca.get(Calendar.DAY_OF_WEEK);
		boolean isHoliday = false;
		if (day_of_week == Calendar.SATURDAY || day_of_week == Calendar.SUNDAY) {
			isHoliday = true;
		}
		return isHoliday;
	}

	public static String getTodayString() throws Exception {
		return getTodayString(4);
	}

	public static String getTodayString(int index) throws Exception {
		Timestamp today = new Timestamp(new Date().getTime());
		String s = today.toString().substring(0, index).replaceAll("-", "");
		return s;
	}

	public static Timestamp today() throws Exception {
		Date date = new Date();
		return new Timestamp(date.getTime());
	}
}
