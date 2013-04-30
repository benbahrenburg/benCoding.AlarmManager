/**
 * benCoding.AlarmManager Project
 * Copyright (c) 2013 by Ben Bahrenburg. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package bencoding.alarmmanager;

import android.util.Log;

public class utils {
	public static final long YEARLY_MILLISECONDS =  31536000000L;
	public static final long MONTHLY_MILLISECONDS =  2628000000L;
	public static final long WEEKLY_MILLISECONDS = 604800000L;
	public static final long DAILY_MILLISECONDS = 86400000L;
	public static final long HOURLY_MILLISECONDS = 3600000L;

	private static boolean _writeToLog = false;

    public static void setDebug(boolean value){
    	_writeToLog = value;
    }
    
    public static void msgLogger(String logMessage){
    	msgLogger(logMessage,false);
    }
    public static void msgLogger(String logMessage, boolean requireLog){
    	if(_writeToLog){
    		Log.d(AlarmmanagerModule.MODULE_FULL_NAME, logMessage);
    	} else if (requireLog) {
    		Log.d(AlarmmanagerModule.MODULE_FULL_NAME, logMessage);
    	}
    }
}
