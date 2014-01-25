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
        //Add default notification flags
        boolean playSound =  bundle.getBoolean("notification_play_sound",false);
        utils.debugLog("On notification play sound? " + new Boolean(playSound).toString());
        boolean doVibrate =  bundle.getBoolean("notification_vibrate",false);
        utils.debugLog("On notification vibrate? " + new Boolean(doVibrate).toString());
        boolean showLights =  bundle.getBoolean("notification_show_lights",false);
        utils.debugLog("On notification show lights? " + new Boolean(showLights).toString());
        
        notificationManager = (NotificationManager) TiApplication.getInstance().getSystemService(TiApplication.NOTIFICATION_SERVICE);
    	utils.debugLog("NotificationManager created");
    	
    	Intent notifyIntent =createIntent(className);
   
    	Notification notification = new Notification(icon, contentTitle, System.currentTimeMillis());		 
    	PendingIntent sender = PendingIntent.getActivity( TiApplication.getInstance().getApplicationContext(), 
    													  requestCode, notifyIntent,  
    													  PendingIntent.FLAG_UPDATE_CURRENT | Notification.FLAG_AUTO_CANCEL);
  
    	
    	utils.debugLog("setting notification flags"); 
    	notification = createNotifyFlags(notification,playSound,soundPath,doVibrate,showLights);
    	utils.debugLog("setLatestEventInfo"); 
    	notification.setLatestEventInfo(TiApplication.getInstance().getApplicationContext(), contentTitle,contentText, sender);
    	utils.debugLog("Notifying using requestCode =" + requestCode);        
    	notificationManager.notify(requestCode, notification);
    	utils.infoLog("You should now see a notification");

    }
    
    private Notification createNotifyFlags(Notification notification, boolean playSound, String soundPath, boolean doVibrate, boolean showLights){
    	//Set the notifications flags
    	if(playSound){
    		if(!utils.isEmptyString(soundPath)){
    			notification.sound = Uri.parse(soundPath);
    		}else{
    			notification.defaults |= Notification.DEFAULT_SOUND;
    		}
    	}
    	if(doVibrate){
    		notification.defaults |= Notification.DEFAULT_VIBRATE;
    	}
    	if(showLights){
    		notification.defaults |= Notification.DEFAULT_LIGHTS;
    		notification.flags |= Notification.FLAG_SHOW_LIGHTS;
    	}
    	if (playSound && doVibrate && showLights){
    		notification.defaults = Notification.DEFAULT_ALL;
    	}
    	
    	//Set alarm flags
    	notification.flags |= Notification.FLAG_ONLY_ALERT_ONCE | Notification.FLAG_AUTO_CANCEL;
    	return notification;
    }
    private Intent createIntent(String className){
    	Intent intent = null;
		try {
			if(utils.isEmptyString(className)){
				utils.debugLog("Using application context");
				intent = new Intent(TiApplication.getInstance().getApplicationContext(),TiApplication.getInstance().getRootOrCurrentActivity().getClass());				
			}else{
				utils.debugLog("Trying to get a class for name '" + className + "'");
				@SuppressWarnings("rawtypes")Class intentClass = Class.forName(className);
				intent = new Intent(TiApplication.getInstance().getApplicationContext(), intentClass);				
			}

		} catch (ClassNotFoundException e) {
			utils.errorLog(e);
		}  
		
		return intent;
    }
}
