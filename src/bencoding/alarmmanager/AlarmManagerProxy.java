/**
 * benCoding.AlarmManager Project
 * Copyright (c) 2009-2012 by Ben Bahrenburg. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package bencoding.alarmmanager;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import java.util.HashMap;

import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollProxy;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUIHelper;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.net.Uri;

import bencoding.alarmmanager.AlarmmanagerModule;

@Kroll.proxy(creatableInModule=AlarmmanagerModule.class)
public class AlarmManagerProxy extends KrollProxy {
	NotificationManager mNotificationManager;


	public AlarmManagerProxy() {
		super();
	}

	private Calendar getSecondBasedCalendar(KrollDict args){
		int interval = args.getInt("second");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.SECOND, interval);
		return cal;
	}

	private Calendar getMinuteBasedCalendar(KrollDict args){
		int interval = args.getInt("minute");
		Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MINUTE, interval);
		return cal;
	}

	private Calendar getFullCalendar(KrollDict args){
		Calendar defaultDay = Calendar.getInstance();
		int day = args.optInt("day", defaultDay.get(Calendar.DAY_OF_MONTH));
		int month = args.optInt("month", defaultDay.get(Calendar.MONTH));
		int year = args.optInt("year", defaultDay.get(Calendar.YEAR));
		int hour = args.optInt("hour", defaultDay.get(Calendar.HOUR_OF_DAY));
		int minute = args.optInt("minute", defaultDay.get(Calendar.MINUTE));
		int second = args.optInt("second", defaultDay.get(Calendar.SECOND));
		Calendar cal =  new GregorianCalendar(year, month, day);
		cal.add(Calendar.HOUR_OF_DAY, hour);
		cal.add(Calendar.MINUTE, minute);
		cal.add(Calendar.SECOND, second);
		return cal;
	}

	private Intent createAlarmNotifyIntent(KrollDict args,int requestCode){
		int notificationIcon = 0;
		String contentTitle = "";
		String contentText = "";
		String notificationSound = "";
		boolean playSound = optionIsEnabled(args,"playSound");
		boolean doVibrate = optionIsEnabled(args,"vibrate");
		boolean showLights = optionIsEnabled(args,"showLights");
		if (args.containsKeyAndNotNull(TiC.PROPERTY_CONTENT_TITLE)
				|| args.containsKeyAndNotNull(TiC.PROPERTY_CONTENT_TEXT))
			{
				if (args.containsKeyAndNotNull(TiC.PROPERTY_CONTENT_TITLE)) {
					contentTitle = TiConvert.toString(args, TiC.PROPERTY_CONTENT_TITLE);
				}
				if (args.containsKeyAndNotNull(TiC.PROPERTY_CONTENT_TEXT)) {
					contentText = TiConvert.toString(args, TiC.PROPERTY_CONTENT_TEXT);
				};
			}

		if (args.containsKey(TiC.PROPERTY_ICON)) {
			Object icon = args.get(TiC.PROPERTY_ICON);
			if (icon instanceof Number) {
				notificationIcon = ((Number)icon).intValue();
			} else {
				String iconUrl = TiConvert.toString(icon);
				String iconFullUrl = resolveUrl(null, iconUrl);
				notificationIcon = TiUIHelper.getResourceId(iconFullUrl);
				if (notificationIcon == 0) {
					utils.debugLog("No image found for " + iconUrl);
					utils.debugLog("Default icon will be used");
				}
			}
		}

		if (args.containsKey(TiC.PROPERTY_SOUND)){
		    notificationSound = resolveUrl(null, TiConvert.toString(args, TiC.PROPERTY_SOUND));
		}

		Intent intent = new Intent(TiApplication.getInstance().getApplicationContext(), AlarmNotificationListener.class);
		//Add some extra information so when the alarm goes off we have enough to create the notification
		intent.putExtra("notification_title", contentTitle);
		intent.putExtra("notification_msg", contentText);
		intent.putExtra("notification_has_icon", (notificationIcon!=0));
		intent.putExtra("notification_icon", notificationIcon);
		intent.putExtra("notification_sound", notificationSound);
		intent.putExtra("notification_play_sound", playSound);
		intent.putExtra("notification_vibrate", doVibrate);
		intent.putExtra("notification_show_lights", showLights);
		intent.putExtra("notification_requestcode", requestCode);
		intent.putExtra("notification_root_classname", AlarmmanagerModule.rootActivityClassName);
		intent.putExtra("notification_request_code", requestCode);

		// As of API 19 setRepeating == setInexactRepeating, see also:
		// http://developer.android.com/reference/android/app/AlarmManager.html#setRepeating(int, long, long, android.app.PendingIntent)
		if (android.os.Build.VERSION.SDK_INT >= 19 && hasRepeating(args)) {
			intent.putExtra("notification_repeat_ms", repeatingFrequency(args));
			Calendar cal = getFullCalendar(args);
			intent.putExtra("notification_year", cal.get(Calendar.YEAR));
			intent.putExtra("notification_month", cal.get(Calendar.MONTH));
			intent.putExtra("notification_day", cal.get(Calendar.DAY_OF_MONTH));
			intent.putExtra("notification_hour", cal.get(Calendar.HOUR_OF_DAY));
			intent.putExtra("notification_minute", cal.get(Calendar.MINUTE));
			intent.putExtra("notification_second", cal.get(Calendar.SECOND));
		}

        if((args.containsKeyAndNotNull("customData"))) {
			String customData = (String)args.get("customData");
			intent.putExtra("customData", customData);
		}

		intent.setData(Uri.parse("alarmId://" + requestCode));
		return intent;
	}

	@Kroll.method
	public String findStartActivityName(){
		return TiApplication.getInstance().getApplicationContext().getPackageManager()
             	.getLaunchIntentForPackage( TiApplication.getInstance().getApplicationContext().getPackageName() ).getClass().getName();
	}

	@Kroll.method
	public void cancelAlarmNotification(@Kroll.argument(optional=true) Object requestCode){
		// To cancel an alarm the signature needs to be the same as the submitting one.
		utils.debugLog("Cancelling Alarm Notification");
		//Set the default request code
		int intentRequestCode = AlarmmanagerModule.DEFAULT_REQUEST_CODE;
		//If the optional code was provided, cast accordingly
		if(requestCode != null){
			if (requestCode instanceof Number) {
				intentRequestCode = ((Number)requestCode).intValue();
			}
		}

		utils.debugLog(String.format("Cancelling requestCode = {%d}", intentRequestCode));

		//Create a placeholder for the args value
		HashMap<String, Object> placeholder = new HashMap<String, Object>(0);
		KrollDict args = new KrollDict(placeholder);

		//Create the Alarm Manager
		AlarmManager am = (AlarmManager) TiApplication.getInstance().getApplicationContext().getSystemService(TiApplication.ALARM_SERVICE);
		Intent intent = createAlarmNotifyIntent(args,intentRequestCode);
		PendingIntent sender = PendingIntent.getBroadcast( TiApplication.getInstance().getApplicationContext(), intentRequestCode, intent,  PendingIntent.FLAG_UPDATE_CURRENT );
		am.cancel(sender);
		utils.debugLog("Alarm Notification Canceled");
	}
	private boolean optionIsEnabled(KrollDict args,String paramName){
		if (args.containsKeyAndNotNull(paramName)){
			Object value = args.get(paramName);
			return TiConvert.toBoolean(value);
		}else{
			return false;
		}
	}
	private boolean hasRepeating(KrollDict args){
		boolean results = (args.containsKeyAndNotNull("repeat"));
		utils.debugLog("Repeat Frequency enabled: " + results);
		return results;
	}
	private long repeatingFrequency(KrollDict args){
		long freqResults = utils.DAILY_MILLISECONDS;
		Object repeat = args.get("repeat");
		if (repeat instanceof Number) {
			utils.debugLog("Repeat value provided in milliseconds found");
			freqResults = ((Number)repeat).longValue();
		} else {
			String repeatValue = TiConvert.toString(repeat);
			utils.debugLog("Repeat value of " + repeatValue + " found");
			if(repeatValue.toUpperCase()=="HOURLY"){
				freqResults=utils.HOURLY_MILLISECONDS;
			}
			if(repeatValue.toUpperCase()=="WEEKLY"){
				freqResults=utils.WEEKLY_MILLISECONDS;
			}
			if(repeatValue.toUpperCase()=="MONTHLY"){
				freqResults=utils.MONTHLY_MILLISECONDS;
			}
			if(repeatValue.toUpperCase()=="YEARLY"){
				freqResults=utils.YEARLY_MILLISECONDS;
			}
		}
		utils.debugLog("Repeat Frequency in milliseconds is " + freqResults);
		return freqResults;
	}
	@Kroll.method
	public void addAlarmNotification(@SuppressWarnings("rawtypes") HashMap hm){
		@SuppressWarnings("unchecked")
		KrollDict args = new KrollDict(hm);
		if(!args.containsKeyAndNotNull("minute") && !args.containsKeyAndNotNull("second")){
			throw new IllegalArgumentException("The minute or second field is required");
		}
		if(!args.containsKeyAndNotNull(TiC.PROPERTY_CONTENT_TITLE)){
			throw new IllegalArgumentException("The context title field (contentTitle) is required");
		}
		if(!args.containsKeyAndNotNull(TiC.PROPERTY_CONTENT_TEXT)){
			throw new IllegalArgumentException("The context text field (contentText) is required");
		}
		Calendar calendar = null;
		boolean isRepeating = hasRepeating(args);
		long repeatingFrequency = 0;
		if(isRepeating){
			repeatingFrequency=repeatingFrequency(args);
		}

		//If seconds are provided but not years, we just take the seconds to mean to add seconds until fire
		boolean secondBased = (args.containsKeyAndNotNull("second") && !args.containsKeyAndNotNull("year"));

		//If minutes are provided but not years, we just take the minutes to mean to add minutes until fire
		boolean minuteBased = (args.containsKeyAndNotNull("minute") && !args.containsKeyAndNotNull("year"));

		//Based on what kind of duration we build our calendar
		if(secondBased) {
			calendar=getSecondBasedCalendar(args);
        }
		else if(minuteBased) {
			calendar=getMinuteBasedCalendar(args);
		}
        else {
			calendar=getFullCalendar(args);
		}

		//Get the requestCode if provided, if none provided
		//we use 192837 for backwards compatibility
		int requestCode = args.optInt("requestCode", AlarmmanagerModule.DEFAULT_REQUEST_CODE);

        String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		utils.debugLog("Creating Alarm Notification for: "  + sdf.format(calendar.getTime()));

		//Create the Alarm Manager
		AlarmManager am = (AlarmManager) TiApplication.getInstance().getApplicationContext().getSystemService(TiApplication.ALARM_SERVICE);
		Intent intent = createAlarmNotifyIntent(args,requestCode);
		PendingIntent sender = PendingIntent.getBroadcast( TiApplication.getInstance().getApplicationContext(), requestCode, intent,  PendingIntent.FLAG_UPDATE_CURRENT );

		if(isRepeating && !intent.hasExtra("notification_repeat_ms")){
			utils.debugLog("Setting Alarm to repeat");
			am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), repeatingFrequency, sender);
		}else{
			utils.debugLog("Setting Alarm for a single run");
			am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
		}

		utils.infoLog("Alarm Notification Created");
	}

	private Intent createAlarmServiceIntent(KrollDict args){
		String serviceName = args.getString("service");
		Intent intent = new Intent(TiApplication.getInstance().getApplicationContext(), AlarmServiceListener.class);
		intent.putExtra("alarm_service_name", serviceName);
		//Pass in flag if we need to restart the service on each call
		intent.putExtra("alarm_service_force_restart", (optionIsEnabled(args,"forceRestart")));
		//Check if the user has selected to use intervals
		boolean hasInterval = (args.containsKeyAndNotNull("interval"));
		long intervalValue = 0;
		if(hasInterval){
			Object interval = args.get("interval");
			if (interval instanceof Number) {
				intervalValue = ((Number)interval).longValue();
			}else{
				hasInterval=false;
			}
		}
		intent.putExtra("alarm_service_has_interval", hasInterval);
		if(hasInterval){
			intent.putExtra("alarm_service_interval", intervalValue);
		}

		if((args.containsKeyAndNotNull("customData"))) {
			String customData = (String)args.get("customData");
			intent.putExtra("customData", customData);
		}

		utils.debugLog("created alarm service intent for " + serviceName
            + "(forceRestart: "
            + (optionIsEnabled(args,"forceRestart") ? "true" : "false")
            + ", intervalValue: "
            + intervalValue
            + ")");

		return intent;
	}
	@Kroll.method
	public void cancelAlarmService(@Kroll.argument(optional=true) Object requestCode){
		// To cancel an alarm the signature needs to be the same as the submitting one.
		utils.infoLog("Cancelling Alarm Service");
		int intentRequestCode = AlarmmanagerModule.DEFAULT_REQUEST_CODE;
		if(requestCode != null){
			if (requestCode instanceof Number) {
				intentRequestCode = ((Number)requestCode).intValue();
			}
		}

		//Create a placeholder for the args value
		HashMap<String, Object> placeholder = new HashMap<String, Object>(0);
		KrollDict args = new KrollDict(placeholder);

		//Create the Alarm Manager
		AlarmManager am = (AlarmManager) TiApplication.getInstance().getApplicationContext().getSystemService(TiApplication.ALARM_SERVICE);
		Intent intent = createAlarmServiceIntent(args);
		PendingIntent sender = PendingIntent.getBroadcast( TiApplication.getInstance().getApplicationContext(), intentRequestCode, intent,  PendingIntent.FLAG_UPDATE_CURRENT );
		am.cancel(sender);
		sender.cancel();
		utils.infoLog("Alarm Service Canceled");
	}
	@Kroll.method
	public void addAlarmService(@SuppressWarnings("rawtypes") HashMap hm){
		@SuppressWarnings("unchecked")
		KrollDict args = new KrollDict(hm);
		if(!args.containsKeyAndNotNull("service")){
			throw new IllegalArgumentException("Service name (service) is required");
		}
		if(!args.containsKeyAndNotNull("minute") && !args.containsKeyAndNotNull("second")){
			throw new IllegalArgumentException("The minute or second field is required");
		}
		Calendar calendar = null;
		boolean isRepeating = hasRepeating(args);
		long repeatingFrequency = 0;
		if(isRepeating){
			repeatingFrequency=repeatingFrequency(args);
		}

		//If seconds are provided but not years, we just take the seconds to mean to add seconds until fire
		boolean secondBased = (args.containsKeyAndNotNull("second") && !args.containsKeyAndNotNull("year"));

		//If minutes are provided but not years, we just take the minutes to mean to add minutes until fire
		boolean minuteBased = (args.containsKeyAndNotNull("minute") && !args.containsKeyAndNotNull("year"));

		//Based on what kind of duration we build our calendar
		if(secondBased) {
			calendar=getSecondBasedCalendar(args);
        }
		else if(minuteBased) {
			calendar=getMinuteBasedCalendar(args);
		}
        else {
			calendar=getFullCalendar(args);
		}

		//Get the requestCode if provided, if none provided
		//we use 192837 for backwards compatibility
		int requestCode = args.optInt("requestCode", AlarmmanagerModule.DEFAULT_REQUEST_CODE);

        String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		utils.debugLog("Creating Alarm Notification for: " + sdf.format(calendar.getTime()));

		AlarmManager am = (AlarmManager) TiApplication.getInstance().getApplicationContext().getSystemService(TiApplication.ALARM_SERVICE);
		Intent intent = createAlarmServiceIntent(args);

		if(isRepeating){
			utils.debugLog("Setting Alarm to repeat at frequency " + repeatingFrequency);
		    PendingIntent pendingIntent = PendingIntent.getBroadcast( TiApplication.getInstance().getApplicationContext(), requestCode, intent,  PendingIntent.FLAG_UPDATE_CURRENT );
		    am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), repeatingFrequency, pendingIntent);
		}else{
			PendingIntent sender = PendingIntent.getBroadcast( TiApplication.getInstance().getApplicationContext(), requestCode, intent,  PendingIntent.FLAG_UPDATE_CURRENT );
			utils.debugLog("Setting Alarm for a single run");
			am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
		}

		utils.infoLog("Alarm Service Request Created");
	}

	@Kroll.method
	public void cancelNotification(int requestCode){
		NotificationManager notificationManager = (NotificationManager) TiApplication.getInstance().getSystemService(TiApplication.NOTIFICATION_SERVICE);
		notificationManager.cancel(requestCode);
	}

	@Kroll.method
	public void cancelNotifications(){
		NotificationManager notificationManager = (NotificationManager) TiApplication.getInstance().getSystemService(TiApplication.NOTIFICATION_SERVICE);
		notificationManager.cancelAll();
	}

	@Kroll.method
	public void setRootActivityClassName(@Kroll.argument(optional=true) Object className){
		//
		utils.infoLog("Request to set rootActivityClassName");

		if (className != null) {
			if (className instanceof String) {
				utils.infoLog("Setting rootActivityClassName to: " + className);
				AlarmmanagerModule.rootActivityClassName = (String)className;
			}
		}
	}
}
