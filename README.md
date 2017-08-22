# bencoding.AlarmManager Module

The bencoding.AlarmManager module enables you to use the native Android AlarmManager in your Titanium apps to schedule notifications and services.

This project can be found on github at [https://github.com/benbahrenburg/benCoding.AlarmManager](https://github.com/benbahrenburg/benCoding.AlarmManager)

## IMPORTANT
This module, provides access to the Android AlarmManager, which works very differently then iOS Local Notifications.  Please read the Android Docs to understand the limitations and differences between these to very different approaches. Android docs are available [here](http://developer.android.com/reference/android/app/AlarmManager.html).

 ## ABOUT ALARMS
Here you will find two ways to create alarms: *addAlarmNotification* and *addAlarmService*. The first will create a notification to be triggered at certain date/time *but* if the device is restarted those notifications will be lost. If you need those notifications, use the service method (maybe you could need the [bootReceiver](https://github.com/benbahrenburg/benCoding.Android.Tools/blob/master/documentation/bootreceiver.md) tool). 

## SUPPORT
No project, "how do I", or "can it" support is provided for this module. The code is available for your reference and usage. If you find an issue and want to contribute Pull Requests are appreciated.  If you are interested in being a contributor and helping with community support please contact me via Twitter.

## Before you start
* This is an Android module designed to work with Titanium SDK 6.0.0 or greater.
* Before getting start please note you need to compile this module yourself

## Building from source

If you are building from source you will need to do the following:
* Update your .classpath file fit your environment
* Update the build.properties to have the correct paths for your environment

## Setup
* Compile the module
* Install the bencoding.alarmmanager module. If you need help here is a "How To" [guide](http://docs.appcelerator.com/platform/latest/#!/guide/Using_a_Module). 
* You can now use the module via the commonJS require method, example shown below.
* You will also need to add the receivers into your tiapp.xml file.

<pre><code>
var alarmManager = require('bencoding.alarmmanager').createAlarmManager();
</code></pre>

Now we have the module installed and avoid in our project we can start to use the components, see the feature guide below for details.

## Methods

### addAlarmNotification
The addAlarmNotification allows you to schedule an Alarm that will then create an notification.

You can create an AlarmNotification using the below properties:
* <b>requestCode</b> (Required) (int) ID for the specific alarm being created. If the requestCode, it will update the saved alarm
* <b>second</b> - (Required) (int) The second of the start time. 
* <b>minute</b> - (Required) (int) The minute of the start time. 
* <b>hour</b> - (Optional) (int) The hour you want to start the alarm
* <b>day</b> - (Optional) (int) The day you want to start the alarm
* <b>month</b> - (Optional) (int) The month you want to start the alarm
* <b>year</b> - (Optional) (int) The year you want to start the alarm
* <b>contentTitle</b> - (Required) (string) The title of the notification
* <b>contentText</b> - (Required) (string) The text of the notification
* <b>playSound</b> (Optional) (bool) Play a notification sound when alarm triggered. If <i>sound</i> is not set, the default notification sound is played.
* <b>sound</b> (Optional) (string) URL to a custom sound to play.
* <b>vibrate</b> (Optional) (bool) Vibrate the device on notification. Please note this requires the vibrate permission.
* <b>showLights</b> (Optional) (bool) Activate notification lights on device when alarm triggered.
* <b>icon</b> - (Optional)The icon of the notification, this can be a system icon or resource included path, e.g. use `Ti.App.Android.R.drawable.appicon` to use your app icon at `platform/android/res/drawable-*/appicon.png`.
* <b>repeat</b> - (Optional) (int) Used to schedule a repeating alarm. You can provide a millisecond value or use the words hourly, daily, monthly, yearly.

Please note if you omit the day, month, and year parameters the module will assume you mean to make the alarm effective from the current time If second is provided, alarm will be set to now plus the number of seconds provided; if minute is provided, alarm will be set for now plus the number of minutes provided.


The valid repeat options are:
* hourly
* daily
* monthly
* yearly

You can also provide a millisecond value to schedule your own repeat frequency.

### addAlarmService
The addAlarmService allows you to schedule an Alarm that will run a service within your Titanium App.

Before using this method you will need to define a service and re-compile your project. After recompiling your project open your /build/AndroidManifest.xml to file your ifull service name.  This is important as Titanium generates this name. To learn more about Android Services please read the documentation [here](http://docs.appcelerator.com/platform/latest/#!/api/Titanium.Android.Service).

You can create an AlarmService using the below properties:

* <b>service</b> - (Required) The full service name from your AndroidManifest.xml to be run.
* <b>requestCode</b> (Optional) (int) ID for the specific alarm being created. If the requestCode, it will update the saved alarm
* <b>second</b> - (Required) (int) The second of the start time. 
* <b>minute</b> - (Required) (int) The minute of the start time. 
* <b>hour</b> - (Optional) (int) The hour you want to start the alarm
* <b>day</b> - (Optional) (int) The day you want to start the alarm
* <b>month</b> - (Optional) (int) The month you want to start the alarm
* <b>year</b> - (Optional) (int) The year you want to start the alarm
* <b>interval</b> - (Optional) The value used to create an interval service. This value must be in milliseconds.
* <b>forceRestart</b> - (Optional) Force the service to restart if it is already running.
* <b>repeat</b> - (Optional) Used to schedule a repeating alarm. You can provide a millisecond value or use the words hourly, daily, monthly, yearly.
* <b>customData</b> - (Optional) (string) Used to pass custom text data to the service ("[]" if empty)

Please note if you omit the day, month, and year parameters the module will assume you mean to make the alarm effective from today and add the number of minutes provided.

The valid repeat options are:
* hourly
* daily
* monthly
* yearly

You can also provide a millisecond value to schedule your own repeat frequency.

### cancelAlarmNotification
This method cancels the alarm linked to the requestCode provided when calling the cancelAlarmNotification method.

<b>Below parameters:</b>
* (int)(Required) Provide an int with the requestCode used when creating the Alarm

<b>Sample:</b>
<pre><code>
//Sample
alarmManager.cancelAlarmNotification(41);	
</code></pre>

### cancelAlarmService
This method cancels the alarm linked to the requestCode provided when calling the cancelAlarmService method.

<b>Below parameters:</b>
* (int)(Optional) Provide an int with the requestCode used when creating the Alarm

<b>Sample:</b>
<pre><code>
//Sample
alarmManager.cancelAlarmService(41);	
</code></pre>

### Method Usage Example
The below example shows how to use all of the methods. For additional samples please see the example folder contained within the module or [here on Github](https://github.com/benbahrenburg/benCoding.SMS/tree/master/example).

<pre><code>
//Import our module into our Titanium App
var requestCode = 41;
var alarmModule = require('bencoding.alarmmanager');
var alarmManager = alarmModule.createAlarmManager();

//Create a date variable to be used later 
var now = new Date();

//Set an Alarm to publish a notification in about two minutes
alarmManager.addAlarmNotification({
    icon: Ti.App.Android.R.drawable.appicon, //Optional icon must be a resource id or url
	minute:2, //Set the number of minutes until the alarm should go off
	contentTitle:'Alarm #2', //Set the title of the Notification that will appear
	contentText:'Alarm & Notify Basic Repeat' //Set the body of the notification that will apear
});	
var ew1 = Ti.UI.createAlertDialog({
	title:'Info', message:"You should see your alarm notification in about 2 minutes & repeat each minute",
	buttonNames:[Ti.Android.currentActivity.getString(Ti.Android.R.string.ok)]
});
ew1.show();

//Below is an example on how you can provide a full date to schedule your alarm
//Set an Alarm to publish a notification in about two minutes and repeat each minute
alarmManager.addAlarmNotification({	
	requestCode:requestCode,	
	year: now.getFullYear(),
	month: now.getMonth(),
	day: now.getDate(),
	hour: now.getHours(),
	minute: now.getMinutes() + 2, //Set the number of minutes until the alarm should go off
	contentTitle:'Alarm #3', //Set the title of the Notification that will appear
	contentText:'Alarm & Notify Scheduled', //Set the body of the notification that will apear
	sound: Ti.Filesystem.getResRawDirectory() + 'alarm', //Set a custom sound to play, located at: platform/android/res/raw/alarm.mp3
	repeat:60000 //You can use the words hourly,daily,weekly,monthly,yearly or you can provide milliseconds.
	//Or as shown above you can provide the millesecond value 	
});	
var ew2 = Ti.UI.createAlertDialog({
	title:'Info', message:"You should see your alarm notification in about 2 minutes",
	buttonNames:[Ti.Android.currentActivity.getString(Ti.Android.R.string.ok)]
});
 ew2.show();	
    
//Cancel our Notification based Alarms 
alarmManager.cancelAlarmNotification(requestCode);	
var ew9 = Ti.UI.createAlertDialog({
	title:'Info', message:"Your alarm notification has been cancelled",
	buttonNames:[Ti.Android.currentActivity.getString(Ti.Android.R.string.ok)]
});
ew9.show();	

//Schedule a service to be run (once) in about two minutes    
alarmManager.addAlarmService({
	//The full name for the service to be called. Find this in your AndroidManifest.xml Titanium creates
	service:'com.appworkbench.alarmtest.TestserviceService', 		
	minute:2, //Set the number of minutes until the alarm should go off
	customData: JSON.stringify(['item1','item2']) // pass JSON string to service
	// use JSON.parse(Ti.Android.currentService.getIntent().getStringExtra('customData')) to get the JSON inside the service
});	
var ew5 = Ti.UI.createAlertDialog({
	title:'Info', message:"The Service provided will be started in about 2 minutes",
	buttonNames:[Ti.Android.currentActivity.getString(Ti.Android.R.string.ok)]
});
ew5.show();	

//Schedule a service to be run (once) in about two minutes, then to run at the same time each day
alarmManager.addAlarmService({	
	//The full name for the service to be called. Find this in your AndroidManifest.xml Titanium creates
	service:'com.appworkbench.alarmtest.TestserviceService', 			
	year: now.getFullYear(),
	month: now.getMonth(),
	day: now.getDate(),
	hour: now.getHours(),
	minute: now.getMinutes() + 2, //Set the number of minutes until the alarm should go off
	repeat:'daily' //You can use the words hourly,daily,weekly,monthly,yearly or you can provide milliseconds.
});	
var ew8 = Ti.UI.createAlertDialog({
	title:'Info', message:"You should see your alarm notification in about 2 minutes & repeat each day",
	buttonNames:[Ti.Android.currentActivity.getString(Ti.Android.R.string.ok)]
});
ew8.show();	

alarmManager.cancelAlarmService();	
var ew10 = Ti.UI.createAlertDialog({
	title:'Info', message:"Your alarm service has been cancelled",
	buttonNames:[Ti.Android.currentActivity.getString(Ti.Android.R.string.ok)]
});
ew10.show();	
            	
</code></pre>

## FAQ

### Can I schedule application events?
Since your application needs to be active to receive these events it is best to use the Alarm Service to schedule a background service in your application. This will ensure the app is awake and you can generate events from there.

### Can I schedule more then one alarm?
To schedule multiple alarms provide a unique requestCode when adding your alarms. This will allow you to create several different alarms. If you later want to cancel the alarms, you will need to provide the requestCode used when creating the alarm.

### How does cancel work?
To cancel a specific alarm please provide the requestCode used when creating the alarm. If you did not provide a requestCode leave the parameter empty for the cancel method.

### Can I use my own Notification icons?
Yes but the URL must be an image located in Resources/android/images/ or an Android content URI.

### My project isn't receiving alarm
Please make sure you have the correct receiver entries setup in your tiapp.xml file.  See the example tiapp.xml for details. Additionally after adding the receiver entries you will need to clean your project and relaunch the emulator.

### Why does it restart my app when I click on the notification?
You need to make sure you have the singleTop launchMode set in your tiapp.xml file.  See below for a snippet and the ExampleProject for an full example.

    <android xmlns:android="http://schemas.android.com/apk/res/android">
        <manifest>
            <supports-screens android:anyDensity="false"/>
            <application>
                <activity android:alwaysRetainTaskState="true"
                    android:configChanges="keyboardHidden|orientation"
                    android:label="AlarmTest"
                    android:launchMode="singleTop"
                    android:name=".AlarmtestActivity" android:theme="@style/Theme.Titanium">
                    <intent-filter>
                        <action android:name="android.intent.action.MAIN"/>
                        <category android:name="android.intent.category.LAUNCHER"/>
                    </intent-filter>
                </activity>
            </application>
        </manifest>
        <services>
            <service type="interval" url="testservice.js"/>
        </services>
    </android>


### What happens to my alarms when the user uninstalls the app?
Android removes your alarms when the user uninstalls your app.

### Where do I find the testservice.js file
The testservice.js file can be copied from the KitchenSink example provided by Appcelerator. If you need a copy of this file you can find it in their github account here [https://github.com/appcelerator/titanium_mobile/blob/master/demos/KitchenSink/Resources/android/testservice.js](https://github.com/appcelerator/titanium_mobile/blob/master/demos/KitchenSink/Resources/android/testservice.js)

### My Project can't find testservice.js
I would recommend trying different paths in your app, for example the root of your project, or platform/android, or Resoures/Android .  Depending on your version of Titanium the answer could be different.

## Licensing

This project is licensed under the OSI approved Apache Public License (version 2). For details please see the license associated with each project.

Developed by [Ben Bahrenburg](http://bahrenburgs.com) available on twitter [@benCoding](http://twitter.com/benCoding)

## Learn More

### Twitter

Please consider following the [@benCoding Twitter](http://www.twitter.com/benCoding) for updates 
and more about Titanium.

### Blog

For module updates, Titanium tutorials and more please check out my blog at [benCoding.Com](http://benCoding.com). 
