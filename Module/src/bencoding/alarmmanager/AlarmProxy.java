/**
 * benCoding.AlarmManager Project
 * Copyright (c) 2009-2012 by Ben Bahrenburg. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package bencoding.alarmmanager;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.titanium.TiApplication;

import android.app.AlarmManager;
import android.app.PendingIntent;

import android.content.Intent;


@Kroll.proxy(creatableInModule=AlarmmanagerModule.class)
public class AlarmProxy extends KrollProxy {

	public AlarmProxy() {
		super();
	}
	
	@SuppressWarnings("unchecked")
	@Kroll.method
	public void addAlarmService(@SuppressWarnings("rawtypes") HashMap hm){
		
		Log.d(AlarmmanagerModule.MODULE_FULL_NAME, "Start creating Alarm Service Request");		
		KrollDict args = new KrollDict(hm);
		String serviceName = args.getString("service_name");
		Calendar defaultDay = Calendar.getInstance();
		int day = args.optInt("day", defaultDay.get(Calendar.DAY_OF_MONTH));
		int month = args.optInt("month", defaultDay.get(Calendar.MONTH));
		int year = args.optInt("year", defaultDay.get(Calendar.YEAR));
		int hour = args.optInt("hour", defaultDay.get(Calendar.HOUR_OF_DAY));
		int minute = args.optInt("minute", defaultDay.get(Calendar.MINUTE));
		Calendar cal =  new GregorianCalendar(year, month, day);
		cal.add(Calendar.HOUR_OF_DAY, hour);
		cal.add(Calendar.MINUTE, minute);
		
		AlarmManager am = (AlarmManager) TiApplication.getInstance().getApplicationContext().getSystemService(TiApplication.ALARM_SERVICE);
		Intent intent = new Intent(TiApplication.getInstance().getApplicationContext(), AlarmServiceListener.class);
		intent.putExtra("alarm_service_name", serviceName);
		PendingIntent sender = PendingIntent.getBroadcast( TiApplication.getInstance().getApplicationContext(), 192837, intent,  PendingIntent.FLAG_UPDATE_CURRENT );
		am.set(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), sender);		
		Log.d(AlarmmanagerModule.MODULE_FULL_NAME, "Alarm Created");	
		 
	}	
}
