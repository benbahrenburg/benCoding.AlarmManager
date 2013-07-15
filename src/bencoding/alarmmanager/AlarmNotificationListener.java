/**
 * benCoding.AlarmManager Project
 * Copyright (c) 2009-2012 by Ben Bahrenburg. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package bencoding.alarmmanager;

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

public class AlarmNotificationListener extends BroadcastReceiver {
    private static final boolean FORCE_LOG = true;
    NotificationManager mNotificationManager;

    @Override 
    public void onReceive(Context context, Intent intent) {
    	utils.msgLogger("In Alarm Notification Listener",FORCE_LOG);
    	Bundle bundle = intent.getExtras();
    	if(bundle.get("notification_request_code") == null){
    		utils.msgLogger("notification_request_code is null assume cancelled",FORCE_LOG);
    		return;
    	}
    	int requestCode = bundle.getInt("notification_request_code", AlarmmanagerModule.DEFAULT_REQUEST_CODE);	
    	utils.msgLogger("requestCode is " + requestCode);
    	String contentTitle = bundle.getString("notification_title");	
    	utils.msgLogger("contentTitle is " + contentTitle);
    	String contentText = bundle.getString("notification_msg");
    	utils.msgLogger("contentText is " + contentText);
    	String className = bundle.getString("notification_root_classname");
    	utils.msgLogger("className is " + className);
    	boolean hasIcon = bundle.getBoolean("notification_has_icon", FORCE_LOG);
        int icon = R.drawable.stat_notify_more;        
        if(hasIcon){
        	icon = bundle.getInt("notification_icon",R.drawable.stat_notify_more);
        	utils.msgLogger("User provided an icon of " + icon);
        }else{
        	utils.msgLogger("No icon provided, default will be used");
        }
        String sound = bundle.getString("notification_sound");
        //Add default notification flags
        boolean playSound =  bundle.getBoolean("notification_play_sound",false);
        utils.msgLogger("On notification play sound? " + new Boolean(playSound).toString());
        boolean doVibrate =  bundle.getBoolean("notification_vibrate",false);
        utils.msgLogger("On notification vibrate? " + new Boolean(doVibrate).toString());
        boolean showLights =  bundle.getBoolean("notification_show_lights",false);
        utils.msgLogger("On notification show lights? " + new Boolean(showLights).toString());
        
    	mNotificationManager =(NotificationManager) TiApplication.getInstance().getSystemService(TiApplication.NOTIFICATION_SERVICE);
    	utils.msgLogger("NotificationManager created");
    	showNotification(contentTitle,contentText,icon,sound,playSound,doVibrate,showLights,className,requestCode);

    }
    private void showNotification(String contentTitle, String contentText, int contentIcon, String sound, boolean playSound, boolean doVibrate, boolean showLights, String className, int requestCode) {
    	utils.msgLogger("Building Notification");  
    	// MAKE SURE YOU HAVE android:launchMode="singleTask" SET IN YOUR TIAPP.XML FILE
    	// IF YOU DON'T HAVE THIS, IT WILL RESTART YOUR APP
    	// See the sample project for an example
		Intent intent = null;
		try {
			utils.msgLogger("Trying to get a class for name '" + className + "'");
			@SuppressWarnings("rawtypes")Class intentClass = Class.forName(className);
			intent = new Intent(TiApplication.getInstance().getApplicationContext(), intentClass);
		} catch (ClassNotFoundException e) {
			utils.msgLogger("Unable to get Class.forName '" + AlarmManagerProxy.rootActivityClassName + "', using getRootOrCurrentActivity() instead");
			intent = new Intent(TiApplication.getInstance().getApplicationContext(),TiApplication.getInstance().getRootOrCurrentActivity().getClass());
		}
    	Notification notification = new Notification(contentIcon, contentTitle, System.currentTimeMillis());		 
    	PendingIntent sender = PendingIntent.getActivity( TiApplication.getInstance().getApplicationContext(), requestCode, intent,  PendingIntent.FLAG_UPDATE_CURRENT | Notification.FLAG_AUTO_CANCEL);
    	
    	//Set the notifications flags
    	if(!sound.equals("")){
    		notification.sound = Uri.parse(sound);
    	}else if(playSound){
    		notification.defaults |= Notification.DEFAULT_SOUND;
    	}
    	if(doVibrate){
    		notification.defaults |=Notification.DEFAULT_VIBRATE;
    	}
    	if(showLights){
    		notification.defaults |=Notification.DEFAULT_LIGHTS;
    	}
    	//Set alarm flags
    	notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_AUTO_CANCEL;
    	
    	//notification.setLatestEventInfo(TiApplication.getInstance().getRootOrCurrentActivity(), contentTitle,contentText, sender);
    	notification.setLatestEventInfo(TiApplication.getInstance().getApplicationContext(), contentTitle,contentText, sender);
    	utils.msgLogger("Notifying");        
    	mNotificationManager.notify(requestCode, notification);
    	utils.msgLogger("You should now see a notification",FORCE_LOG);    
   }
}
