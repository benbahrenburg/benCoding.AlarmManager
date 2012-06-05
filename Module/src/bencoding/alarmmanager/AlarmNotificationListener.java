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
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlarmNotificationListener extends BroadcastReceiver {
    private static final String LCAT = "AlarmNotificationListener";
    private static final boolean FORCE_LOG = true;
    int YOURAPP_NOTIFICATION_ID = 100;
    NotificationManager mNotificationManager;

    @Override 
    public void onReceive(Context context, Intent intent) {
    	utils.msgLogger(LCAT,"In Alarm Notification Listener",FORCE_LOG);
    	Bundle bundle = intent.getExtras();
    	String contentTitle = bundle.getString("notification_title");
    	utils.msgLogger(LCAT,"contentTitle is " + contentTitle);
    	String contentText = bundle.getString("notification_msg");
    	utils.msgLogger(LCAT,"contentText is " + contentText);
    	boolean hasIcon = bundle.getBoolean("notification_has_icon", FORCE_LOG);
        int icon = R.drawable.stat_notify_more;        
        if(hasIcon){
        	icon = bundle.getInt("notification_icon",R.drawable.stat_notify_more);
        	utils.msgLogger(LCAT,"User provided an icon of " + icon);
        }else{
        	utils.msgLogger(LCAT,"No icon provided, default will be used");
        }
        //Add default notification flags
        boolean playSound =  bundle.getBoolean("notification_play_sound",false);
        utils.msgLogger(LCAT,"On notification play sound? " + new Boolean(playSound).toString());
        boolean doVibrate =  bundle.getBoolean("notification_vibrate",false);
        utils.msgLogger(LCAT,"On notification vibrate? " + new Boolean(doVibrate).toString());
        boolean showLights =  bundle.getBoolean("notification_show_lights",false);
        utils.msgLogger(LCAT,"On notification show lights? " + new Boolean(showLights).toString());
        
    	mNotificationManager =(NotificationManager) TiApplication.getInstance().getSystemService(TiApplication.NOTIFICATION_SERVICE);
    	utils.msgLogger(LCAT,"NotificationManager created");
    	showNotification(TiApplication.getInstance().getApplicationContext(),contentTitle,contentText,icon,playSound,doVibrate,showLights); 	
    }
    private void showNotification(Context context, String contentTitle, String contentText, int contentIcon, boolean playSound, boolean doVibrate, boolean showLights) {
    	utils.msgLogger(LCAT,"Building Notification");  
    	// MAKE SURE YOU HAVE android:launchMode="singleTask" SET IN YOUR TIAPP.XML FILE
    	// IF YOU DON'T HAVE THIS, IT WILL RESTART YOUR APP
    	// See the sample project for an example
    	Intent intent = new Intent(TiApplication.getInstance().getApplicationContext(),TiApplication.getInstance().getRootOrCurrentActivity().getClass());    	
    	Notification notification = new Notification(contentIcon, contentTitle, System.currentTimeMillis());		 
    	PendingIntent sender = PendingIntent.getActivity( TiApplication.getInstance().getApplicationContext(), 0, intent,  PendingIntent.FLAG_UPDATE_CURRENT | Notification.FLAG_AUTO_CANCEL);
    	
    	//Set the notifications flags
    	if(playSound){
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
    	utils.msgLogger(LCAT,"Notifying");        
    	mNotificationManager.notify(YOURAPP_NOTIFICATION_ID, notification);
    	utils.msgLogger(LCAT,"You should now see a notification",FORCE_LOG);    
   } 
}
