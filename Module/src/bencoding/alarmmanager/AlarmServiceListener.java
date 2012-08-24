/**
 * benCoding.AlarmManager Project
 * Copyright (c) 2009-2012 by Ben Bahrenburg. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package bencoding.alarmmanager;

import org.appcelerator.titanium.TiApplication;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlarmServiceListener  extends BroadcastReceiver {
	private static final String LCAT = "AlarmServiceListener";
	private static final boolean FORCE_LOG = true;
	private boolean isServiceRunning(Context context, String serviceName) {
	 
		ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
		for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) 
			if (serviceName.equals(service.service.getClassName()))
				return true;            
		return false;
	}
	@Override 
    public void onReceive(Context context, Intent intent) {
		utils.msgLogger(LCAT,"In Alarm Service Listener",FORCE_LOG);   	
    	Bundle bundle = intent.getExtras();
        String fullServiceName = bundle.getString("alarm_service_name");
        boolean forceRestart = bundle.getBoolean("alarm_service_force_restart",false);
        boolean hasInterval = bundle.getBoolean("alarm_service_has_interval",false);
        
        utils.msgLogger(LCAT,"Full Service Name: " + fullServiceName);
        if (this.isServiceRunning(context,fullServiceName)) {        	
        	if(forceRestart){
        		utils.msgLogger(LCAT,"Service is already running, we will stop it then restart",FORCE_LOG);
              	Intent tempIntent = new Intent();
              	tempIntent.setClassName(TiApplication.getInstance().getApplicationContext(), fullServiceName);              	
              	context.stopService(tempIntent);
              	utils.msgLogger(LCAT,"Service has been stopped",FORCE_LOG);
        	}else{
        		utils.msgLogger(LCAT,"Service is already running not need for us to start",FORCE_LOG);
        		return;
        	}
        }
 
      	Intent serviceIntent = new Intent();
      	serviceIntent.setClassName(TiApplication.getInstance().getApplicationContext(), fullServiceName);
       	
      	utils.msgLogger(LCAT,"Is this an interval service? " + new Boolean(hasInterval).toString(),FORCE_LOG);      	
      	if(hasInterval){
      		utils.msgLogger(LCAT,"Is this an interval amount " + bundle.getLong("alarm_service_interval", 45*60*1000L),FORCE_LOG);
      		serviceIntent.putExtra("interval", bundle.getLong("alarm_service_interval", 45*60*1000L)); // Default to 45mins
      	}
        context.startService(serviceIntent); 
        utils.msgLogger(LCAT, "Alarm Service Started",FORCE_LOG);	
    }
}
