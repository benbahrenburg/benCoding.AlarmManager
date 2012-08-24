/**
 * benCoding.AlarmManager Project
 * Copyright (c) 2009-2012 by Ben Bahrenburg. All Rights Reserved.
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

	public static final boolean IS_DEBUG = true;
	
    public static void msgLogger(String LCAT, String logMessage){
    	msgLogger(LCAT,logMessage,false);
    }
    public static void msgLogger(String LCAT, String logMessage, boolean requireLog){
    	if(IS_DEBUG){
    		Log.d(LCAT, logMessage);
    	} else if (requireLog) {
    		Log.d(LCAT, logMessage);
    	}
    }
}
