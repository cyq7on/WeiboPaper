package com.xihua.weibopaper.utils;

import android.content.Context;
import android.content.res.Resources;

import com.xihua.weibopaper.activity.R;
import com.xihua.weibopaper.common.MyApplication;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils {

	// yyyy-MM-dd hh:mm:ss 12小时制
	// yyyy-MM-dd HH:mm:ss 24小时制

	public static final String TYPE_01 = "yyyy-MM-dd HH:mm:ss";

	public static final String TYPE_02 = "yyyy-MM-dd";

	public static final String TYPE_03 = "HH:mm:ss";

	public static final String TYPE_04 = "yyyy年MM月dd日";

	public static String formatDate(long time, String format) {
		Calendar cal = Calendar.getInstance();
		cal.setTimeInMillis(time);
		return new SimpleDateFormat(format).format(cal.getTime());
	}

	public static String formatDate(String longStr, String format) {
		try {
			return formatDate(Long.parseLong(longStr), format);
		} catch (Exception e) {
		}
		return "";
	}
	
	public static long formatStr(String timeStr, String pattern) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern);
		try {
			return sdf.parse(timeStr).getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		return 0;
	}

	public static String formatDate(String time) {
		Context context = MyApplication.getInstance();
		Resources res = context.getResources();

		StringBuffer buffer = new StringBuffer();

		Calendar createCal = Calendar.getInstance();
		createCal.setTimeInMillis(Date.parse(time));
		Calendar currentcal = Calendar.getInstance();
		currentcal.setTimeInMillis(System.currentTimeMillis());

		long diffTime = (currentcal.getTimeInMillis() - createCal.getTimeInMillis()) / 1000;

		// 同一月
		if (currentcal.get(Calendar.MONTH) == createCal.get(Calendar.MONTH)) {
			// 同一天
			if (currentcal.get(Calendar.DAY_OF_MONTH) == createCal.get(Calendar.DAY_OF_MONTH)) {
				if (diffTime < 3600 && diffTime >= 60) {
					buffer.append((diffTime / 60) + res.getString(R.string.msg_few_minutes_ago));
				} else if (diffTime < 60) {
					buffer.append(res.getString(R.string.msg_now));
				} else {
					buffer.append(res.getString(R.string.msg_today)).append(" ").append(formatDate(createCal.getTimeInMillis(), "HH:mm"));
				}
			}
			// 前一天
			else if (currentcal.get(Calendar.DAY_OF_MONTH) - createCal.get(Calendar.DAY_OF_MONTH) == 1) {
				buffer.append(res.getString(R.string.msg_yesterday)).append(" ").append(formatDate(createCal.getTimeInMillis(), "HH:mm"));
			}
		}

		if (buffer.length() == 0) {
			buffer.append(formatDate(createCal.getTimeInMillis(), "MM-dd HH:mm"));
		}

		String timeStr = buffer.toString();
		if (currentcal.get(Calendar.YEAR) != createCal.get(Calendar.YEAR)) {
			timeStr = createCal.get(Calendar.YEAR) + " " + timeStr;
		}
		return timeStr;
	}

}
