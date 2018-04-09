package com.stone.commons.tools;

import java.lang.ref.SoftReference;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class DateUtils {
	public final static String CN_DATE_FULL = "yyyy-MM-dd HH:mm:ss";
	public final static String CN_DATE_MINUTE = "yyyy-MM-dd HH:mm";
	public final static String CN_DATE_DAY = "yyyy-MM-dd";
	public final static String UN_DATE_DIG_FULL = "yyyyMMddHHmmss";
	public final static String CN_DATE_DIG_MINUTE = "yyyyMMddHHmm";
	public final static String UN_DATE_DIG_SHORT = "yyyyMMdd";
	public final static String CN_DATE_HOUR_MINUTE = "HH:mm";
	public final static String CN_DATE_TIME = "HH:mm:ss";
	
	private static final ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>> THREADLOCAL_FORMATS = new ThreadLocal<SoftReference<Map<String, SimpleDateFormat>>>() {
		@Override
		protected SoftReference<Map<String, SimpleDateFormat>> initialValue() {
			return new SoftReference<Map<String, SimpleDateFormat>>(new HashMap<String, SimpleDateFormat>());
		}
	};
	
	public final static String[] DEFAULT_PATTERN = new String[] { CN_DATE_FULL, CN_DATE_MINUTE, CN_DATE_DAY,
			UN_DATE_DIG_FULL, CN_DATE_DIG_MINUTE, UN_DATE_DIG_SHORT, CN_DATE_TIME, CN_DATE_HOUR_MINUTE};

	private static final Date DEFAULT_TWO_DIGIT_YEAR_START;

	static {
		final Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.set(2000, Calendar.JANUARY, 1, 0, 0, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		DEFAULT_TWO_DIGIT_YEAR_START = calendar.getTime();
	}
	
	public static Date parseDate(String dateValue) {
		return parseDate(dateValue, DEFAULT_PATTERN);
	}
	
	public static Date parseDate(String dateValue, String... patterns) {
		final String[] localDateFormats = patterns != null ? patterns : DEFAULT_PATTERN;
		String v = dateValue;
		// trim single quotes around date if present
		// see issue #5279
		if (v.length() > 1 && v.startsWith("'") && v.endsWith("'")) {
			v = v.substring(1, v.length() - 1);
		}
		
		for (final String dateFormat : localDateFormats) {
			final SimpleDateFormat dateParser = get(dateFormat);
			dateParser.set2DigitYearStart(DEFAULT_TWO_DIGIT_YEAR_START);
			final ParsePosition pos = new ParsePosition(0);
			final Date result = dateParser.parse(v, pos);
			if (pos.getIndex() != 0) {
				return result;
			}
		}
		return null;
	}
	
	public static String formatDate(final Date date, final String pattern) {
		final SimpleDateFormat formatter = get(pattern);
		return formatter.format(date);
	}
	
	public static Date add(Date date, int field, int amount){
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.setTime(date);
		calendar.add(field, amount);
		
		return calendar.getTime();
	}
	
	public static Date add(String date, int field, int amount){
		Date undate = DateUtils.parseDate(date);
		if(undate == null){
			return null;
		}
		
		Calendar calendar = Calendar.getInstance(Locale.CHINA);
		calendar.setTime(undate);
		calendar.add(field, amount);
		
		return calendar.getTime();
	}
	
	private static SimpleDateFormat get(String pattern) {
		final SoftReference<Map<String, SimpleDateFormat>> ref = THREADLOCAL_FORMATS.get();
		Map<String, SimpleDateFormat> formats = ref.get();
		if (formats == null) {
			formats = new HashMap<String, SimpleDateFormat>();
			THREADLOCAL_FORMATS.set(new SoftReference<Map<String, SimpleDateFormat>>(formats));
		}
		
		SimpleDateFormat format = formats.get(pattern);
		if (format == null) {
			format = new SimpleDateFormat(pattern, Locale.CHINA);
			format.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
			formats.put(pattern, format);
		}
		
		return format;
	}
	
	public static void main(String[] args){
		System.out.println(DateUtils.parseDate("08:15"));
		System.out.println(DateUtils.formatDate(DateUtils.add("2018-04-04 08:15", Calendar.MINUTE, -30), DateUtils.CN_DATE_HOUR_MINUTE));
		System.out.println(DateUtils.formatDate(DateUtils.parseDate("08:15"), DateUtils.CN_DATE_FULL));
	}
}
