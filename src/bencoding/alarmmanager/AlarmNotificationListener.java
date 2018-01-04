/**
 * benCoding.AlarmManager Project
 * Copyright (c) 2009-2012 by Ben Bahrenburg. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package bencoding.alarmmanager;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.text.SimpleDateFormat;
import android.app.AlarmManager;

import org.appcelerator.titanium.TiApplication;

import android.R;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.net.Uri;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.support.v4.app.NotificationCompat.BigTextStyle;

public class AlarmNotificationListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
    	NotificationManager notificationManager = null;

    	utils.debugLog("In Alarm Notification Listener");
    	Bundle bundle = intent.getExtras();
    	if(bundle.get("notification_request_code") == null){
    		utils.infoLog("notification_request_code is null assume cancelled");
    		return;
    	}
    	int requestCode = bundle.getInt("notification_request_code", AlarmmanagerModule.DEFAULT_REQUEST_CODE);
    	utils.debugLog("requestCode is " + requestCode);
    	String contentTitle = bundle.getString("notification_title");
    	utils.debugLog("contentTitle is " + contentTitle);
    	String contentText = bundle.getString("notification_msg");
    	utils.debugLog("contentText is " + contentText);
    	String className = bundle.getString("notification_root_classname");
    	utils.debugLog("className is " + className);
    	boolean hasIcon = bundle.getBoolean("notification_has_icon", true);
        int icon = R.drawable.stat_notify_more;
        if(hasIcon){
        	icon = bundle.getInt("notification_icon",R.drawable.stat_notify_more);
        	utils.debugLog("User provided an icon of " + icon);
        }else{
        	utils.debugLog("No icon provided, default will be used");
        }
        String soundPath = bundle.getString("notification_sound");
        boolean hasCustomSound = !utils.isEmptyString(soundPath);
        //Add default notification flags
        boolean playSound =  bundle.getBoolean("notification_play_sound",false);
        utils.debugLog("On notification play sound? " + new Boolean(playSound).toString());
        boolean doVibrate =  bundle.getBoolean("notification_vibrate",false);
        utils.debugLog("On notification vibrate? " + new Boolean(doVibrate).toString());
        boolean showLights =  bundle.getBoolean("notification_show_lights",false);
        utils.debugLog("On notification show lights? " + new Boolean(showLights).toString());

        notificationManager = (NotificationManager) TiApplication.getInstance().getSystemService(TiApplication.NOTIFICATION_SERVICE);
    	utils.debugLog("NotificationManager created");

    	Intent notifyIntent = createIntent(className);
        notifyIntent.putExtra("requestCode", requestCode);

        String customData = bundle.getString("customData");

        if (!utils.isEmptyString(customData)) {
            notifyIntent.putExtra("customData", customData);
        }

    	//Notification notification = new Notification(icon, contentTitle, System.currentTimeMillis());
    	PendingIntent sender = PendingIntent.getActivity( TiApplication.getInstance().getApplicationContext(),
    													  requestCode, notifyIntent,
    													  PendingIntent.FLAG_UPDATE_CURRENT | Notification.FLAG_AUTO_CANCEL);

    	NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(
  		TiApplication.getInstance().getApplicationContext())
  				.setWhen(System.currentTimeMillis())
  				.setContentText(contentText)
  				.setContentTitle(contentTitle)
  				.setSmallIcon(icon)
  				.setAutoCancel(true)
  				.setTicker(contentTitle)
  				.setContentIntent(sender)
  				.setStyle(new NotificationCompat.BigTextStyle().bigText(contentText)).setOnlyAlertOnce(true).setAutoCancel(true);
    	utils.debugLog("setting notification flags");
    	notificationBuilder = createNotifyFlags(notificationBuilder,playSound,hasCustomSound,soundPath,doVibrate,showLights);
    	utils.debugLog("Notifying using requestCode =" + requestCode);
    	notificationManager.notify(requestCode, notificationBuilder.build());
    	utils.infoLog("You should now see a notification");

    	if (bundle.getLong("notification_repeat_ms", 0) > 0) {
    		createRepeatNotification(bundle);
    	}

    }
    private void createRepeatNotification(Bundle bundle) {

    	Intent intent = new Intent(TiApplication.getInstance().getApplicationContext(), AlarmNotificationListener.class);
    	// Use the same extras as the original notification
    	intent.putExtras(bundle);

    	// Update date and time by repeat interval (in milliseconds)
		int day = bundle.getInt("notification_day");
		int month = bundle.getInt("notification_month");
		int year = bundle.getInt("notification_year");
		int hour = bundle.getInt("notification_hour");
		int minute = bundle.getInt("notification_minute");
		int second = bundle.getInt("notification_second");
		Calendar cal = new GregorianCalendar(year, month, day);
		cal.add(Calendar.HOUR_OF_DAY, hour);
		cal.add(Calendar.MINUTE, minute);
		cal.add(Calendar.SECOND, second);

    	Calendar now = Calendar.getInstance();
    	long repeat_ms = bundle.getLong("notification_repeat_ms", 0);
    	int repeat_s = (int)repeat_ms / 1000;

		// Add frequence until cal > now
		while (now.getTimeInMillis() > cal.getTimeInMillis()) {
			cal.add(Calendar.SECOND, repeat_s);
		}

    	int requestCode = bundle.getInt("notification_request_code", AlarmmanagerModule.DEFAULT_REQUEST_CODE);
		intent.setData(Uri.parse("alarmId://" + requestCode));
		long ms = cal.getTimeInMillis();

		Date date = new Date(ms);
		String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
		utils.infoLog("Creating Alarm Notification repeat for: "  + sdf.format(date));

		//Create the Alarm Manager
		AlarmManager am = (AlarmManager) TiApplication.getInstance().getApplicationContext()
			.getSystemService(TiApplication.ALARM_SERVICE);
		PendingIntent sender = PendingIntent.getBroadcast( TiApplication.getInstance().getApplicationContext(),
			requestCode, intent,  PendingIntent.FLAG_UPDATE_CURRENT );
		am.set(AlarmManager.RTC_WAKEUP, ms, sender);

    }
    private NotificationCompat.Builder createNotifyFlags(NotificationCompat.Builder notification, boolean playSound, boolean hasCustomSound, String soundPath, boolean doVibrate, boolean showLights){
    	//Set the notifications flags
    	if (playSound && !hasCustomSound && doVibrate && showLights){
            notification.setDefaults(Notification.DEFAULT_ALL);
    	} else {
    		int defaults = 0;
    		if (showLights) {
    		    defaults = defaults | Notification.DEFAULT_LIGHTS;
    		    notification.setLights(0xFF0000FF,200,3000);
    		}
        // DEFAULT_SOUND overrides custom sound, so never set both
        if (playSound) {
            if(hasCustomSound){
              notification.setSound(Uri.parse(soundPath));
            } else {
              defaults = defaults | Notification.DEFAULT_SOUND;
            }
        }
    		if (doVibrate) {
    		    defaults = defaults | Notification.DEFAULT_VIBRATE;
    		}
    		notification.setDefaults(defaults);
    	}

    	//Set alarm flags
    	return notification;
    }
    private Intent createIntent(String className){
		try {

			if(utils.isEmptyString(className)){
				//If no activity is provided, use the App Start Activity
				utils.infoLog("[AlarmManager] Using application Start Activity");

				Intent iStartActivity = TiApplication.getInstance().getApplicationContext().getPackageManager()
		             	.getLaunchIntentForPackage( TiApplication.getInstance().getApplicationContext().getPackageName() );

				//Add the flags needed to restart
				iStartActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				iStartActivity.addCategory(Intent.CATEGORY_LAUNCHER);
				iStartActivity.setAction(Intent.ACTION_MAIN);

				return iStartActivity;

			}else{

				utils.infoLog("[AlarmManager] Trying to get a class for name '" + className + "'");
				@SuppressWarnings("rawtypes")Class intentClass = Class.forName(className);
				Intent intentFromClass = new Intent(TiApplication.getInstance().getApplicationContext(), intentClass);
				//Add the flags needed to restart
				intentFromClass.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
				intentFromClass.addCategory(Intent.CATEGORY_LAUNCHER);
				intentFromClass.setAction(Intent.ACTION_MAIN);

				return intentFromClass;
			}

		} catch (ClassNotFoundException e) {
			utils.errorLog(e);
			return null;
		}
    }
}
