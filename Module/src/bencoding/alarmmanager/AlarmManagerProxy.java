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
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.TiC;
import org.appcelerator.titanium.util.TiConvert;
import org.appcelerator.titanium.util.TiUIHelper;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;

import android.content.Intent;

@Kroll.proxy(creatableInModule=AlarmmanagerModule.class)
public class AlarmManagerProxy extends KrollProxy {
	NotificationManager mNotificationManager;
	private static final String LCAT = "AlarmManager";
	private static final boolean FORCE_LOG = true;
	public AlarmManagerProxy() {
		super();
	}

	private Calendar getMinutedBasedCalendar(KrollDict args){
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
		Calendar cal =  new GregorianCalendar(year, month, day);
		cal.add(Calendar.HOUR_OF_DAY, hour);
		cal.add(Calendar.MINUTE, minute);
		return cal;
	}

	private Intent createAlarmNotifyIntent(KrollDict args){
		int notificationIcon = 0;
		String contentTitle = "";
		String contentText = "";
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
					utils.msgLogger(LCAT, "No image found for " + iconUrl);
					utils.msgLogger(LCAT, "Default icon will be used");
				}
			}
		}

		Intent intent = new Intent(TiApplication.getInstance().getApplicationContext(), AlarmNotificationListener.class);
		//Add some extra information so when the alarm goes off we have enough to create the notification
		intent.putExtra("notification_title", contentTitle);
		intent.putExtra("notification_msg", contentText);
		intent.putExtra("notification_has_icon", (notificationIcon!=0));
		intent.putExtra("notification_icon", notificationIcon);
		intent.putExtra("notification_play_sound", playSound);		
		intent.putExtra("notification_vibrate", doVibrate);
		intent.putExtra("notification_show_lights", showLights);
		return intent;
	}
	
	@Kroll.method
	public void cancelAlarmNotification(){
		// To cancel an alarm the signature needs to be the same as the submitting one.
		utils.msgLogger(LCAT, "Cancelling Alarm Notification",FORCE_LOG);		
		HashMap<String, Object> placeholder = new HashMap<String, Object>(0);
		KrollDict args = new KrollDict(placeholder);
		//Create the Alarm Manager
		AlarmManager am = (AlarmManager) TiApplication.getInstance().getApplicationContext().getSystemService(TiApplication.ALARM_SERVICE);
		Intent intent = createAlarmNotifyIntent(args);
		PendingIntent sender = PendingIntent.getBroadcast( TiApplication.getInstance().getApplicationContext(), 192837, intent,  PendingIntent.FLAG_UPDATE_CURRENT );
		am.cancel(sender);	
		utils.msgLogger(LCAT, "Alarm Notification Canceled",FORCE_LOG);
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
		utils.msgLogger(LCAT, "Repeat Frequency enabled: " + results);
		return results;
	}
	private long repeatingFrequency(KrollDict args){
		long freqResults = utils.DAILY_MILLISECONDS;
		Object repeat = args.get("repeat");
		if (repeat instanceof Number) {
			utils.msgLogger(LCAT, "Repeat value provided in milliseconds found");
			freqResults = ((Number)repeat).longValue();
		} else {
			String repeatValue = TiConvert.toString(repeat);
			utils.msgLogger(LCAT, "Repeat value of " + repeatValue + " found");
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
		utils.msgLogger(LCAT, "Repeat Frequency in milliseconds is " + freqResults);
		return freqResults;
	}
	@Kroll.method
	public void addAlarmNotification(@SuppressWarnings("rawtypes") HashMap hm){		
		utils.msgLogger(LCAT, "Creating Alarm Notification",FORCE_LOG);		
		@SuppressWarnings("unchecked")
		KrollDict args = new KrollDict(hm);
		if(!args.containsKeyAndNotNull("minute")){
			throw new IllegalArgumentException("The minute field is required");
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
		
		//If minutes are provided by not years, we just take the minutes to mean to add minutes until fire
		boolean minuteBased = (args.containsKeyAndNotNull("minute") && !args.containsKeyAndNotNull("year"));
		//Based on what kind of duration we build our calendar
		if(minuteBased){
			calendar=getMinutedBasedCalendar(args);
		}else{
			calendar=getFullCalendar(args);
		}
		//Create the Alarm Manager
		AlarmManager am = (AlarmManager) TiApplication.getInstance().getApplicationContext().getSystemService(TiApplication.ALARM_SERVICE);
		Intent intent = createAlarmNotifyIntent(args);
		PendingIntent sender = PendingIntent.getBroadcast( TiApplication.getInstance().getApplicationContext(), 192837, intent,  PendingIntent.FLAG_UPDATE_CURRENT );
		
		if(isRepeating){
			utils.msgLogger(LCAT, "Setting Alarm to repeat");
			am.setRepeating(AlarmManager.RTC_WAKEUP,calendar.getTimeInMillis(), repeatingFrequency, sender);
		}else{
			utils.msgLogger(LCAT, "Setting Alarm for a single run");
			am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
		}
				
		utils.msgLogger(LCAT, "Alarm Notification Created",FORCE_LOG);
	}

	private Intent createAlarmServiceIntent(KrollDict args){
		String serviceName = args.getString("service");
		Intent intent = new Intent(TiApplication.getInstance().getApplicationContext(), AlarmServiceListener.class);
		intent.putExtra("alarm_service_name", serviceName);
		//Pass in flag if we need to restart the servie on each call
		intent.putExtra("alarm_service_force_restart", (optionIsEnabled(args,"forceRestart")));
		//Check if the user has seleced to use intervals
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
		return intent;
	}
	@Kroll.method
	public void cancelAlarmService(){	
		// To cancel an alarm the signature needs to be the same as the submitting one.
		utils.msgLogger(LCAT, "Cancelling Alarm Service",FORCE_LOG);		
		HashMap<String, Object> placeholder = new HashMap<String, Object>(0);
		KrollDict args = new KrollDict(placeholder);
		//Create the Alarm Manager
		AlarmManager am = (AlarmManager) TiApplication.getInstance().getApplicationContext().getSystemService(TiApplication.ALARM_SERVICE);
		Intent intent = createAlarmServiceIntent(args);
		PendingIntent sender = PendingIntent.getBroadcast( TiApplication.getInstance().getApplicationContext(), 192837, intent,  PendingIntent.FLAG_UPDATE_CURRENT );
		am.cancel(sender);	
		utils.msgLogger(LCAT, "Alarm Service Canceled",FORCE_LOG);		
	}
	@Kroll.method
	public void addAlarmService(@SuppressWarnings("rawtypes") HashMap hm){		
		utils.msgLogger(LCAT,"Creating Alarm Service Request",FORCE_LOG);		
		@SuppressWarnings("unchecked")
		KrollDict args = new KrollDict(hm);
		if(!args.containsKeyAndNotNull("service")){
			throw new IllegalArgumentException("Service name (service) is required");
		}
		if(!args.containsKeyAndNotNull("minute")){
			throw new IllegalArgumentException("The minute field is required");
		}		
		Calendar calendar = null;
		boolean isRepeating = hasRepeating(args);
		long repeatingFrequency = 0;
		if(isRepeating){
			repeatingFrequency=repeatingFrequency(args);
		}		
		
		//If minutes are provided by not years, we just take the minutes to mean to add minutes until fire
		boolean minuteBased = (args.containsKeyAndNotNull("minute") && !args.containsKeyAndNotNull("year"));
		//Based on what kind of duration we build our calendar
		if(minuteBased){
			calendar=getMinutedBasedCalendar(args);
		}else{
			calendar=getFullCalendar(args);
		}
		AlarmManager am = (AlarmManager) TiApplication.getInstance().getApplicationContext().getSystemService(TiApplication.ALARM_SERVICE);
		Intent intent = createAlarmServiceIntent(args);
		PendingIntent sender = PendingIntent.getBroadcast( TiApplication.getInstance().getApplicationContext(), 192837, intent,  PendingIntent.FLAG_UPDATE_CURRENT );
		if(isRepeating){
			utils.msgLogger(LCAT, "Setting Alarm to repeat at frequency " + repeatingFrequency);
		    PendingIntent pendingIntent = PendingIntent.getBroadcast( TiApplication.getInstance().getApplicationContext(), 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT );
		    am.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), repeatingFrequency, pendingIntent);
		}else{
			utils.msgLogger(LCAT, "Setting Alarm for a single run");
			am.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), sender);
		}
			
		utils.msgLogger(LCAT, "Alarm Service Request Created",FORCE_LOG);	
	}
}
