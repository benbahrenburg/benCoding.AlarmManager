/**
 * benCoding.AlarmManager Project
 * Copyright (c) 2013 by Ben Bahrenburg. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 */
package bencoding.alarmmanager;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;


@Kroll.module(name="Alarmmanager", id="bencoding.alarmmanager")
public class AlarmmanagerModule extends KrollModule
{
	public static final int DEFAULT_REQUEST_CODE = 192837;
	public static final String MODULE_FULL_NAME = "bencoding.AlarmManage";
	public AlarmmanagerModule()
	{
		super();
	}
	@Kroll.method
	public void disableLogging()
	{
		utils.setDebug(false);
	}
	@Kroll.method
	public void enableLogging()
	{
		utils.setDebug(true);
	}	
}

