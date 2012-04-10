<h1>benCoding.AlarmManager Module</h1>

The benCoding.AlarmManager module enables you to use the native Android AlarmManager in your Titanium apps to schedule notifications and services.

This project can be found on github at [https://github.com/benbahrenburg/benCoding.AlarmManager](https://github.com/benbahrenburg/benCoding.AlarmManager)

<h2>Before you start</h2>
* This is an Android module designed to work with Titanium SDK 1.8.2 or greater.
* Before using this module you first need to install the package. If you need instructions on how to install a 3rd party module please read this installation guide.

<h2>What is in the repo?</h2>
* Module

The Module folder contains the benCoding.AlarmManager project.  To get the compiled module you will want to go [here.](https://github.com/benbahrenburg/benCoding.AlarmManager/tree/master/Module/dist)

* ExampleProject

The ExampleProject folder contains a full example project.  Since this module uses services and activities it is important that you review this example before using in your app.

<h2>Download the release</h2>

To get the module, go to the [Module/dist](https://github.com/benbahrenburg/benCoding.AlarmManager/tree/master/Module/dist) folder. This will have a release compiled for anyone download it from github.

<h2>Building from source?</h2>

If you are building from source you will need to do the following:
* Update your .classpath file fit your environment
* Update the build.properties to have the correct paths for your environment

<h2>Setup</h2>

* Download the latest release from the releases folder ( or you can build it yourself )
* Install the bencoding.sms module. If you need help here is a "How To" [guide](https://wiki.appcelerator.org/display/guides/Configuring+Apps+to+Use+Modules). 
* You can now use the module via the commonJS require method, example shown below.
* You will also need to add the receivers into your tiapp.xml file.

<pre><code>
var alarmManager = require('bencoding.alarmmanager').createAlarmManager();
</code></pre>

Now we have the module installed and avoid in our project we can start to use the components, see the feature guide below for details.

<h2>benCoding.AlarmManager How To Example</h2>

For a detailed example on how to use this module, please download the example project located in the ExampleProject folder on github.

<h2>Receivers</h2>
Android Alarm's work using BroadcastReceivers.  In order to have your Titanium project subscribe to the Alarms it generates you need to add the receivers into your tiapp.xml file.

The benCoding.AlarmManager uses two receivers. One for each kind of Alarm you can schedule.  See below for the example.


	<receiver android:name="bencoding.alarmmanager.AlarmNotificationListener"></receiver>
    <receiver android:name="bencoding.alarmmanager.AlarmServiceListener"></receiver> 

Given all of the different configuration options needed, I highly recommend starting with the ExampleProject before incorporating into another project.

You can reach more about BroadcastReceivers [here.](http://developer.android.com/reference/android/content/BroadcastReceiver.html)

<h2>Methods</h2>

<h3>addAlarmNotification</h3>
The addAlarmNotification allows you to schedule an Alarm that will then create an notification.

You can create an AlarmNotification using the below properties:

* <b>minute</b> - (Required) The minute of the start time. 
* <b>hour</b> - (Optional) The hour you want to start the alarm
* <b>day</b> - (Optional) The day you want to start the alarm
* <b>month</b> - (Optional) The month you want to start the alarm
* <b>year</b> - (Optional) The year you want to start the alarm
* <b>contentTitle</b> - (Required) The title of the notification
* <b>contentText</b> - (Required) The text of the notification
* <b>icon</b> - (Optional)The icon of the notification
* <b>repeat</b> - (Optional) Used to schedule a repeating alarm. You can provide a millisecond value or use the words hourly, daily, monthly, yearly.

Please note if you omit the day, month, and year parameters the module will assume you mean to make the alarm effective from today and add the number of minutes provided.

The valid repeat options are:
* hourly
* daily
* monthly
* yearly

You can also provide a millisecond value to schedule your own repeat frequency.

<h3>addAlarmService</h3>
The addAlarmService allows you to schedule an Alarm that will run a service within your Titanium App.

Before using this method you will need to define a service and re-compile your project. After recompiling your project open your /build/AndroidManifest.xml to file your full service name.  This is important as Titanium generates this name. To learn more about Android Services please read the documentation [here](http://developer.appcelerator.com/apidoc/mobile/latest/Titanium.Android.Service-object).

You can create an AlarmService using the below properties:

* <b>service</b> - (Required) The full service name from your AndroidManifest.xml to be run.
* <b>minute</b> - (Required) The minute of the start time. 
* <b>hour</b> - (Optional) The hour you want to start the alarm
* <b>day</b> - (Optional) The day you want to start the alarm
* <b>month</b> - (Optional) The month you want to start the alarm
* <b>year</b> - (Optional) The year you want to start the alarm
* <b>repeat</b> - (Optional) Used to schedule a repeating alarm. You can provide a millisecond value or use the words hourly, daily, monthly, yearly.

Please note if you omit the day, month, and year parameters the module will assume you mean to make the alarm effective from today and add the number of minutes provided.

The valid repeat options are:
* hourly
* daily
* monthly
* yearly

You can also provide a millisecond value to schedule your own repeat frequency.

<h3>cancelAlarmNotification</h3>
This method cancels all alarms submitted using the addAlarmNotification method.  Unfortunately if you want to cancel only one alarm you need to cancel them all and re-submit.  This is due to how Android handles pendingIntents.

<h3>cancelAlarmService</h3>
This method cancels all alarms submitted using the addAlarmService method.  Unfortunately if you want to cancel only one alarm you need to cancel them all and re-submit.  This is due to how Android handles pendingIntents.

<h3>Method Usage Example</h3>
The below example shows how to use all of the methods. For additional samples please see the example folder contained within the module or [here on Github](https://github.com/benbahrenburg/benCoding.SMS/tree/master/example).

<pre><code>
//Import our module into our Titanium App
var alarmModule = require('bencoding.alarmmanager');
var alarmManager = alarmModule.createAlarmManager();

//Create a date variable to be used later 
var now = new Date();

//Set an Alarm to publish a notification in about two minutes
alarmManager.addAlarmNotification({
    icon: Ti.Android.R.drawable.stat_notify_sync, //Optional icon must be a resource id or url
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
	year: now.getFullYear(),
	month: now.getMonth(),
	day: now.getDate(),
	hour: now.getHours(),
	minute: now.getMinutes() + 2, //Set the number of minutes until the alarm should go off
	contentTitle:'Alarm #3', //Set the title of the Notification that will appear
	contentText:'Alarm & Notify Scheduled', //Set the body of the notification that will apear
	repeat:60000 //You can use the words hourly,daily,weekly,monthly,yearly or you can provide milliseconds.
	//Or as shown above you can provide the millesecond value 	
});	
var ew2 = Ti.UI.createAlertDialog({
	title:'Info', message:"You should see your alarm notification in about 2 minutes",
	buttonNames:[Ti.Android.currentActivity.getString(Ti.Android.R.string.ok)]
});
 ew2.show();	
    
//Cancel our Notification based Alarms 
alarmManager.cancelAlarmNotification();	
var ew9 = Ti.UI.createAlertDialog({
	title:'Info', message:"Your alarm notification has been cancelled",
	buttonNames:[Ti.Android.currentActivity.getString(Ti.Android.R.string.ok)]
});
ew9.show();	

//Schedule a service to be run (once) in about two minutes    
alarmManager.addAlarmService({
	//The full name for the service to be called. Find this in your AndroidManifest.xml Titanium creates
	service:'com.appworkbench.alarmtest.TestserviceService', 		
	minute:2 //Set the number of minutes until the alarm should go off
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

<h2>FAQ</h2>

<h3>Can I schedule application events?</h3>
Since your application needs to be active to receive these events it is best to use the Alarm Service to schedule a background service in your application. This will ensure the app is awake and you can generate events from there.

<h3>How does cancel work?</h3>
Android handles the cancel action at a PendingIntent level.  This means that when you call cancelAlarmNotification it cancels any Alarm you've schedule using addAlarmNotification.  The same is true to cancelAlarmService and addAlarmService.

<h3>Can I use my own Notification icons?</h3>
Yes but the URL must be an image located in Resources/android/images/ or an Android content URI.

<h3>Can I schedule an interval service?</h3>
No, it is not recommended that you mix alarms and interval services.  If this is needed, you can schedule a service to create an interval service. But, I would caution against mixing repeating service types.

<h3>My project isn't receiving alarm</h3>
Please make sure you have the correct receiver entries setup in your tiapp.xml file.  See the ExampleProject for details. Additionally after adding the receiver entries you will need to clean your project and relaunch the emulator.

<h3>Why does it restart my app when I click on the notification?</h3>
You need to make sure you have the singleTask launchMode set in your tiapp.xml file.  See below for a snippet and the ExampleProject for an full example.

    <android xmlns:android="http://schemas.android.com/apk/res/android">
        <manifest>
            <supports-screens android:anyDensity="false"/>
            <application>
                <activity android:alwaysRetainTaskState="true"
                    android:configChanges="keyboardHidden|orientation"
                    android:label="AlarmTest"
                    android:launchMode="singleTask"
                    android:name=".AlarmtestActivity" android:theme="@style/Theme.Titanium">
                    <intent-filter>
                        <action android:name="android.intent.action.MAIN"/>
                        <category android:name="android.intent.category.LAUNCHER"/>
                    </intent-filter>
                </activity>
                 <receiver android:name="bencoding.alarmmanager.AlarmNotificationListener"></receiver>
                 <receiver android:name="bencoding.alarmmanager.AlarmServiceListener"></receiver>  
            </application>
        </manifest>
        <services>
            <service type="interval" url="testservice.js"/>
        </services>
    </android>


<h3>What happens to my alarms when the user uninstalls the app?</h3>
Android removes your alarms when the user uninstalls your app.

<h2>Licensing & Support</h2>

This project is licensed under the OSI approved Apache Public License (version 2). For details please see the license associated with each project.

Developed by [Ben Bahrenburg](http://bahrenburgs.com) available on twitter [@benCoding](http://twitter.com/benCoding)

<h2>Learn More</h2>

<h3>Twitter</h3>

Please consider following the [@benCoding Twitter](http://www.twitter.com/benCoding) for updates 
and more about Titanium.

<h3>Blog</h3>

For module updates, Titanium tutorials and more please check out my blog at [benCoding.Com](http://benCoding.com). 
