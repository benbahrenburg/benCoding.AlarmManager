// this sets the background color of the master UIView (when there are no windows/tab groups on it)
Ti.UI.setBackgroundColor('#000');
//Add this in so Titanium will add the permission for us.
Titanium.Media.vibrate();
//Add this so Titanium will add the permissions and links needed to play sounds
var sound = Titanium.Media.createSound();

//Bring in the module
var alarmModule = require('bencoding.alarmmanager');
//Create a new instance of the Alarm Manager
var alarmManager = alarmModule.createAlarmManager();
//Create our basic window to put our example buttons on
var win = Titanium.UI.createWindow({  
    title:'Tab 1', backgroundColor:'#fff', title:'Alarm Manager Tests'
});

var btn1 = Ti.UI.createButton({
	title:"Alarm & Notify Basic", top:10, left:5, width: 150, height:75
});
win.add(btn1);
btn1.addEventListener('click',function(e){
	alarmManager.addAlarmNotification({
		requestCode: 41, //Request ID used to identify a specific alarm. Provide the same requestCode twice to update 
		icon: Ti.App.Android.R.drawable.appicon, //Optional icon must be a resource id or url
		minute:2, //Set the number of minutes until the alarm should go off
		contentTitle:'Alarm #1', //Set the title of the Notification that will appear
		contentText:'Alarm & Notify Basic', //Set the body of the notification that will apear
		playSound:true, //On notification play the default sound ( by default off )
		vibrate:true, //On notification vibrate device ( by default off )
		showLights: true, //On notification show the device lights ( by default off )
	});	
	var ew = Ti.UI.createAlertDialog({
		title:'Info', message:"You should see your alarm notification in about 2 minutes",
		buttonNames:[Ti.Android.currentActivity.getString(Ti.Android.R.string.ok)]
	});
    ew.show();		
});

var btn2 = Ti.UI.createButton({
	title:"Alarm & Notify Basic Repeat", top:10, right:5, width: 150, height:75
});
win.add(btn2);
btn2.addEventListener('click',function(e){
	alarmManager.addAlarmNotification({
		requestCode: 42, //Request ID used to identify a specific alarm. Provide the same requestCode twice to update 
		minute:2, //Set the number of minutes until the alarm should go off
		contentTitle:'Alarm #2', //Set the title of the Notification that will appear
		contentText:'Alarm & Notify Basic Repeat', //Set the body of the notification that will apear
		sound: Ti.Filesystem.getResRawDirectory() + 'alarm', //Set a custom sound to play, located at: platform/android/res/raw/alarm.mp3
		repeat:60000 //You can use the words hourly,daily,weekly,monthly,yearly or you can provide milliseconds.
		//Or as shown above you can provide the millesecond value 
	});	
	var ew = Ti.UI.createAlertDialog({
		title:'Info', message:"You should see your alarm notification in about 2 minutes & repeat each minute",
		buttonNames:[Ti.Android.currentActivity.getString(Ti.Android.R.string.ok)]
	});
    ew.show();		
});

var btn3 = Ti.UI.createButton({
	title:"Alarm & Notify Scheduled", top:90, left:5, width: 150, height:75
});
win.add(btn3);
btn3.addEventListener('click',function(e){
	var now = new Date();
	alarmManager.addAlarmNotification({	
		requestCode: 43, //Request ID used to identify a specific alarm. Provide the same requestCode twice to update 	
		year: now.getFullYear(),
		month: now.getMonth(),
		day: now.getDate(),
		hour: now.getHours(),
		minute: now.getMinutes() + 2, //Set the number of minutes until the alarm should go off
		contentTitle:'Alarm #3', //Set the title of the Notification that will appear
		contentText:'Alarm & Notify Scheduled' //Set the body of the notification that will apear
	});	
	var ew = Ti.UI.createAlertDialog({
		title:'Info', message:"You should see your alarm notification in about 2 minutes",
		buttonNames:[Ti.Android.currentActivity.getString(Ti.Android.R.string.ok)]
	});
    ew.show();		
});

var btn4 = Ti.UI.createButton({
	title:"Alarm & Notify Scheduled Repeat", top:90, right:5, width: 150, height:75
});
win.add(btn4);
btn4.addEventListener('click',function(e){
	var now = new Date();
	alarmManager.addAlarmNotification({		
		requestCode: 44, //Request ID used to identify a specific alarm. Provide the same requestCode twice to update 
		year: now.getFullYear(),
		month: now.getMonth(),
		day: now.getDate(),
		hour: now.getHours(),
		minute: now.getMinutes() + 2, //Set the number of minutes until the alarm should go off
		contentTitle:'Alarm #4', //Set the title of the Notification that will appear
		contentText:'Alarm & Notify Scheduled Repeat', //Set the body of the notification that will apear
		repeat:'daily' //You can use the words hourly,daily,weekly,monthly,yearly or you can provide milliseconds.
	});	
	var ew = Ti.UI.createAlertDialog({
		title:'Info', message:"You should see your alarm notification in about 2 minutes & repeat each day",
		buttonNames:[Ti.Android.currentActivity.getString(Ti.Android.R.string.ok)]
	});
    ew.show();		
});

var btn5 = Ti.UI.createButton({
	title:"Alarm & Service Basic", top:180, left:5, width: 150, height:75
});
win.add(btn5);
btn5.addEventListener('click',function(e){
	alarmManager.addAlarmService({
		//The full name for the service to be called. Find this in your AndroidManifest.xml Titanium creates
		service:'com.appworkbench.alarmtest.TestserviceService', 		
		minute:2, //Set the number of minutes until the alarm should go off
		interval:60000 // Create an interval service that runs each minute
	});	
	var ew = Ti.UI.createAlertDialog({
		title:'Info', message:"The Service provided will be started in about 2 minutes",
		buttonNames:[Ti.Android.currentActivity.getString(Ti.Android.R.string.ok)]
	});
    ew.show();		
});

var btn6 = Ti.UI.createButton({
	title:"Alarm & Service Basic Repeat", top:180, right:5, width: 150, height:75
});
win.add(btn6);
btn6.addEventListener('click',function(e){
	alarmManager.addAlarmService({
		//The full name for the service to be called. Find this in your AndroidManifest.xml Titanium creates
		service:'com.appworkbench.alarmtest.TestserviceService', 
		minute:2, //Set the number of minutes until the alarm should go off
		repeat:60000, //You can use the words hourly,daily,weekly,monthly,yearly or you can provide milliseconds.
					 //Or as shown above you can provide the millesecond value 
		forceRestart:true //Force the service to restart on each call.
	});	
	var ew = Ti.UI.createAlertDialog({
		title:'Info', message:"The Service provided will be started in about 2 minutes & repeat each minute",
		buttonNames:[Ti.Android.currentActivity.getString(Ti.Android.R.string.ok)]
	});
    ew.show();		
});

var btn7 = Ti.UI.createButton({
	title:"Alarm & Service Scheduled", top:260, left:5, width: 150, height:75
});
win.add(btn7);
btn7.addEventListener('click',function(e){
	var now = new Date();
	alarmManager.addAlarmService({		
		//The full name for the service to be called. Find this in your AndroidManifest.xml Titanium creates
		service:'com.appworkbench.alarmtest.TestserviceService', 		
		year: now.getFullYear(),
		month: now.getMonth(),
		day: now.getDate(),
		hour: now.getHours(),
		minute: now.getMinutes() + 2, //Set the number of minutes until the alarm should go off
	});	
	var ew = Ti.UI.createAlertDialog({
		title:'Info', message:"The Service provided will be started in about 2 minutes",
		buttonNames:[Ti.Android.currentActivity.getString(Ti.Android.R.string.ok)]
	});
    ew.show();		
});

var btn8 = Ti.UI.createButton({
	title:"Alarm & Service Scheduled Repeat", top:260, right:5, width: 150, height:75
});
win.add(btn8);
btn8.addEventListener('click',function(e){
	var now = new Date();
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
	var ew = Ti.UI.createAlertDialog({
		title:'Info', message:"You should see your alarm notification in about 2 minutes & repeat each day",
		buttonNames:[Ti.Android.currentActivity.getString(Ti.Android.R.string.ok)]
	});
    ew.show();		
});


var btn9 = Ti.UI.createButton({
	title:"Alarm & Notify Cancel", top:340, left:5, width: 150, height:75
});
win.add(btn9);
btn9.addEventListener('click',function(e){
	var requestCode = 41; //RequestCOde to be canceled
	alarmManager.cancelAlarmNotification(requestCode);	
	var ew = Ti.UI.createAlertDialog({
		title:'Info', message:"Your alarm notification has been cancelled",
		buttonNames:[Ti.Android.currentActivity.getString(Ti.Android.R.string.ok)]
	});
    ew.show();		
});

var btn10 = Ti.UI.createButton({
	title:"Alarm & Service Cancel", top:340, right:5, width: 150, height:75
});
win.add(btn10);
btn10.addEventListener('click',function(e){
	alarmManager.cancelAlarmService();	
	var ew = Ti.UI.createAlertDialog({
		title:'Info', message:"Your alarm service has been cancelled",
		buttonNames:[Ti.Android.currentActivity.getString(Ti.Android.R.string.ok)]
	});
    ew.show();		
});
win.open();
