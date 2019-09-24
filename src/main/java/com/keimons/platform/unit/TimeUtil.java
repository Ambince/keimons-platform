package com.keimons.platform.unit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalQueries;
import java.util.Calendar;
import java.util.Date;

/**
 * 时间工具
 */
public class TimeUtil {

	/**
	 * 时间偏移量
	 */
	private static long offsetTime;

	/**
	 * 0时 时区时间偏移量<br />
	 * 东加西减 中国UTC+8<br />
	 * 应该在{@link System#currentTimeMillis()}的基础上加8个小时
	 */
	private static long offsetZero0 = 0L;

	/**
	 * 5时 时区时间偏移量<br />
	 * 东加西减 中国UTC+8<br />
	 * 应该在{@link System#currentTimeMillis()}的基础上加8个小时
	 */
	private static long offsetZero5 = Calendar.getInstance().get(Calendar.ZONE_OFFSET) - 5 * 60 * 60 * 1000L;

	/**
	 * 标准程序中标准日期格式
	 */
	private final static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");
	private final static DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

	public static long getOffsetTime() {
		return offsetTime;
	}

	public static void setOffsetTime(long offsetTime) {
		TimeUtil.offsetTime = offsetTime;
	}

	/**
	 * 当前系统时间
	 *
	 * @return 系统当前时间 毫秒值
	 */
	public static long currentTimeMillis() {
		return System.currentTimeMillis() + offsetTime;
	}

	/**
	 * 当前系统时间
	 *
	 * @return 当前的系统日期
	 */
	public static Date currentDate() {
		return new Date(currentTimeMillis());
	}

	/**
	 * 获取当前日期
	 * <p>
	 * 采用的是线程安全的{@link DateTimeFormatter}实现
	 *
	 * @return 当前日期 日期格式："yyyy-MM-dd HH:mm:ss.SSS"
	 */
	public static String getDateTime() {
		return formatter.format(LocalDateTime.ofInstant(
				Instant.ofEpochMilli(currentTimeMillis()), ZoneId.systemDefault()
		));
	}

	/**
	 * 获取当前日期
	 * <p>
	 * 采用的是线程安全的{@link DateTimeFormatter}实现
	 *
	 * @return 当前日期 日期格式："yyyy-MM-dd HH:mm:ss.SSS"
	 */
	public static String getDateTime(long time) {
		return formatter.format(LocalDateTime.ofInstant(
				Instant.ofEpochMilli(time), ZoneId.systemDefault()
		));
	}

	/**
	 * 按照0点划分 判断两个日期是否同一天
	 *
	 * @param time1 时间1
	 * @param time2 时间2
	 * @return 是否同一天
	 */
	public static boolean isSameDay0(long time1, long time2) {
		LocalDate date1 = Instant.ofEpochMilli(time1).query(TemporalQueries.localDate());
		LocalDate date2 = Instant.ofEpochMilli(time2).query(TemporalQueries.localDate());
		return date1.equals(date2);
	}

	/**
	 * 按照5点划分 判断两个日期是否同一天
	 *
	 * @param time1 时间1
	 * @param time2 时间2
	 * @return 是否同一天
	 */
	public static boolean isSameDay5(long time1, long time2) {
		LocalDateTime dateTime1 = LocalDateTime.ofInstant(Instant.ofEpochMilli(time1), ZoneId.systemDefault());
		LocalDateTime dateTime2 = LocalDateTime.ofInstant(Instant.ofEpochMilli(time2), ZoneId.systemDefault());
		LocalDate date1;
		LocalDate date2;
		if (dateTime1.toLocalTime().isBefore(LocalTime.of(5, 0))) {
			date1 = dateTime1.toLocalDate().plusDays(-1);
		} else {
			date1 = dateTime1.toLocalDate();
		}
		if (dateTime2.toLocalTime().isBefore(LocalTime.of(5, 0))) {
			date2 = dateTime2.toLocalDate().plusDays(-1);
		} else {
			date2 = dateTime2.toLocalDate();
		}
		return date1.equals(date2);
	}

	/**
	 * 依赖服务器时区的是否同一天
	 *
	 * @param time1  时间1
	 * @param time2  时间2
	 * @param offset 时间偏移
	 * @return 是否同一天
	 */
	private static boolean isSameDay(long time1, long time2, long offset) {
		return (time1 + offset) / (24 * 60 * 60 * 1000L) == (time2 + offset) / (24 * 60 * 60 * 1000L);
	}

	/**
	 * 时间转化工具
	 *
	 * @param time 毫秒时间
	 * @return 日期字符串
	 */
	public static String convertTime(long time) {
		Instant instant = Instant.ofEpochMilli(time);
		return formatter.format(LocalDateTime.ofInstant(instant, ZoneId.systemDefault()));
	}

	/**
	 * 时间转化工具
	 *
	 * @param date 日期时间 格式：yyyy-MM-dd HH:mm:ss
	 * @return 时间毫秒
	 */
	public static long convertTime(String date) {
		LocalDateTime parse = LocalDateTime.parse(date, dtf);
		return parse.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}


	/**
	 * 获取一天的开始时间
	 * <p>
	 * yyyy-MM-dd 00:00:00.000
	 *
	 * @return 今天的开始时间
	 */
	public static long beginTime(long millis) {
		LocalDate date = Instant.ofEpochMilli(millis).query(TemporalQueries.localDate());
		LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MIN);
		return beginTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	/**
	 * 获取一天的开始时间
	 * <p>
	 * yyyy-MM-dd 23:59:59.999
	 *
	 * @return 今天的结束时间
	 */
	public static long endTime(long millis) {
		LocalDate date = Instant.ofEpochMilli(millis).query(TemporalQueries.localDate());
		LocalDateTime beginTime = LocalDateTime.of(date, LocalTime.MAX);
		return beginTime.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli();
	}

	/**
	 * 分钟转毫秒
	 *
	 * @param minute 分钟
	 * @return 毫秒
	 */
	public static long minuteToMillis(int minute) {
		return minute * 60L * 1000;
	}

	/**
	 * 小时转毫秒
	 *
	 * @param house 小时
	 * @return 毫秒
	 */
	public static long houseToMillis(int house) {
		return house * 60L * 60 * 1000;
	}

	public static void main(String[] args) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

		Calendar instance1 = Calendar.getInstance();
		instance1.setTime(sdf.parse("1991-04-13 03:00:00"));

		System.out.println("当前时间：" + sdf.format(instance1.getTime()));

		instance1.add(Calendar.HOUR_OF_DAY, -2);

		System.out.println("两小时前：" + sdf.format(instance1.getTime()));

		System.out.println();

		Calendar instance2 = Calendar.getInstance();
		instance2.setTimeInMillis(671565600000L);

		System.out.println("当前时间：" + sdf.format(instance2.getTime()));

		instance2.add(Calendar.HOUR_OF_DAY, -2);

		System.out.println("两小时前：" + sdf.format(instance2.getTime()));

		System.out.println();

		Calendar instance3 = Calendar.getInstance();
		instance3.setTime(sdf.parse("1991-04-15 03:00:00"));

		System.out.println("当前时间：" + sdf.format(instance3.getTime()));

		instance3.add(Calendar.DAY_OF_YEAR, -1);

		System.out.println("两小时前：" + sdf.format(instance3.getTime()));
	}
}