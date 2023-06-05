var __extends=this&&this.__extends||function(){var t=function(e,a){return t=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(t,e){t.__proto__=e}||function(t,e){for(var a in e)Object.prototype.hasOwnProperty.call(e,a)&&(t[a]=e[a])},t(e,a)};return function(e,a){if("function"!=typeof a&&null!==a)throw new TypeError("Class extends value "+String(a)+" is not a constructor or null");function o(){this.constructor=e}t(e,a),e.prototype=null===a?Object.create(a):(o.prototype=a.prototype,new o)}}();define(["require","exports","./interpreter.aRobotBehaviour","./interpreter.constants","./interpreter.util","util.roberta"],(function(t,e,a,o,r,i){Object.defineProperty(e,"__esModule",{value:!0}),e.RobotSimBehaviour=void 0;var s=function(t){function e(){var e=t.call(this)||this;return e.hardwareState.motors={},r.loggingEnabled(!1,!1),e}return __extends(e,t),e.prototype.getSample=function(t,e,a,o,i,s){var n="robot: "+e+", port: "+o+", mode: "+i;r.debug(n+" getsample from "+a);var h=a;"mbot"==e&&(o="ORT_"+o),t.push(this.getSensorValue(h,o,i,s))},e.prototype.getSensorValue=function(t,e,a,o){var r=this.hardwareState.sensors[t];if(void 0===r)return"undefined";var i=r;return void 0!==e&&(i=i[e]),void 0!==a&&(i=i[a]),void 0!==o&&(i=i[o]),void 0!==i&&i},e.prototype.encoderReset=function(t){r.debug("encoderReset for "+t),this.hardwareState.actions.encoder={},this.hardwareState.actions.encoder[t]="reset"},e.prototype.timerReset=function(t){null==this.hardwareState.actions.timer&&(this.hardwareState.actions.timer=[]),this.hardwareState.actions.timer={},this.hardwareState.actions.timer[t]="reset",r.debug("timerReset for "+t)},e.prototype.ledOnAction=function(t,e,a){var i="robot: "+t+", port: "+e;r.debug(i+" led on color "+a),null==this.hardwareState.actions.led&&(this.hardwareState.actions.led={}),e?(this.hardwareState.actions.led[e]={},this.hardwareState.actions.led[e][o.COLOR]=a):(this.hardwareState.actions.led={},this.hardwareState.actions.led[o.COLOR]=a)},e.prototype.ledOffAction=function(t,e){var a="robot: "+t+", port: "+e;r.debug(a+" led off"),"mbot"===t?(this.hardwareState.actions.leds||(this.hardwareState.actions.leds={}),this.hardwareState.actions.leds[e]={},this.hardwareState.actions.leds[e][o.MODE]=o.OFF):e?(null==this.hardwareState.actions.led&&(this.hardwareState.actions.led={}),this.hardwareState.actions.led[e]={},this.hardwareState.actions.led[e][o.MODE]=o.OFF):(this.hardwareState.actions.led={},this.hardwareState.actions.led.mode=o.OFF)},e.prototype.toneAction=function(t,e,a){return r.debug(t+" piezo: , frequency: "+e+", duration: "+a),this.hardwareState.actions.tone={},this.hardwareState.actions.tone.frequency=e,this.hardwareState.actions.tone.duration=a,this.setBlocking(a>0),0},e.prototype.playFileAction=function(t){return r.debug("play file: "+t),this.hardwareState.actions.tone={},this.hardwareState.actions.tone.file=t,this.setBlocking(!0),0},e.prototype.setVolumeAction=function(t){r.debug("set volume: "+t),this.hardwareState.actions.volume=Math.max(Math.min(100,t),0),this.hardwareState.volume=Math.max(Math.min(100,t),0)},e.prototype.getVolumeAction=function(t){r.debug("get volume"),t.push(this.hardwareState.volume)},e.prototype.setLanguage=function(t){r.debug("set language "+t),this.hardwareState.actions.language=t},e.prototype.sayTextAction=function(t,e,a){return null==this.hardwareState.actions.sayText&&(this.hardwareState.actions.sayText={}),this.hardwareState.actions.sayText.text=t,this.hardwareState.actions.sayText.speed=e,this.hardwareState.actions.sayText.pitch=a,this.setBlocking(!0),0},e.prototype.motorOnAction=function(t,e,a,i,s,n){var h="robot: "+t+", port: "+e,d=void 0===i?" w.o. duration":" for "+i+" msec";return r.debug(h+" motor speed "+s+d),null==this.hardwareState.actions.motors&&(this.hardwareState.actions.motors={}),this.hardwareState.actions.motors[e]=s,this.hardwareState.motors[e]=s,void 0!==n?n:(a!==o.DEGREE&&a!==o.DISTANCE&&a!==o.ROTATIONS||(this.hardwareState.actions.motors[a]=i,this.setBlocking(!0)),0)},e.prototype.motorStopAction=function(t,e){var a="robot: "+t+", port: "+e;return r.debug(a+" motor stop"),null==this.hardwareState.actions.motors&&(this.hardwareState.actions.motors={}),this.hardwareState.actions.motors[e]=0,this.hardwareState.motors[e]=0,0},e.prototype.driveAction=function(t,e,a,i,s){var n="robot: "+t+", direction: "+e,h=void 0===i?" w.o. duration":" for "+i+" msec";return r.debug(n+" motor speed "+a+h),null==this.hardwareState.actions.motors&&(this.hardwareState.actions.motors={}),(e!=o.FOREWARD&&i>0||e==o.FOREWARD&&i<0||e!=o.FOREWARD&&!i)&&(a*=-1),0===i&&(a=0),this.hardwareState.actions.motors[o.MOTOR_LEFT]=a,this.hardwareState.actions.motors[o.MOTOR_RIGHT]=a,this.hardwareState.motors[o.MOTOR_LEFT]=a,this.hardwareState.motors[o.MOTOR_RIGHT]=a,void 0!==s?s:(0!=a&&void 0!==i&&(this.hardwareState.actions.motors[o.DISTANCE]=i,this.setBlocking(!0)),0)},e.prototype.curveAction=function(t,e,a,i,s,n){var h="robot: "+t+", direction: "+e,d=void 0===s?" w.o. duration":" for "+s+" msec";r.debug(h+" left motor speed "+a+" right motor speed "+i+d),null==this.hardwareState.actions.motors&&(this.hardwareState.actions.motors={}),(e!=o.FOREWARD&&s>0||e==o.FOREWARD&&s<0||e!=o.FOREWARD&&!s)&&(a*=-1,i*=-1),0===s&&(i=0,a=0),this.hardwareState.actions.motors[o.MOTOR_LEFT]=a,this.hardwareState.actions.motors[o.MOTOR_RIGHT]=i,this.hardwareState.motors[o.MOTOR_LEFT]=a,this.hardwareState.motors[o.MOTOR_RIGHT]=i;var c=.5*(Math.abs(a)+Math.abs(i));return void 0!==n?n:(0!=c&&void 0!==s&&(this.hardwareState.actions.motors[o.DISTANCE]=s,this.setBlocking(!0)),0)},e.prototype.turnAction=function(t,e,a,i,s){var n="robot: "+t+", direction: "+e,h=void 0===i?" w.o. duration":" for "+i+" msec";return r.debug(n+" motor speed "+a+h),null==this.hardwareState.actions.motors&&(this.hardwareState.actions.motors={}),(e==o.LEFT&&i<0||e==o.RIGHT&&i<0)&&(a*=-1),0===i&&(a=0),this.setTurnSpeed(a,e),void 0!==s?s:(0!==Math.abs(a)&&void 0!==i&&(this.hardwareState.actions.motors[o.ANGLE]=i*(Math.PI/180),this.setBlocking(!0)),0)},e.prototype.setTurnSpeed=function(t,e){e==o.LEFT?(this.hardwareState.actions.motors[o.MOTOR_LEFT]=-t,this.hardwareState.actions.motors[o.MOTOR_RIGHT]=t):(this.hardwareState.actions.motors[o.MOTOR_LEFT]=t,this.hardwareState.actions.motors[o.MOTOR_RIGHT]=-t)},e.prototype.driveStop=function(t){r.debug("robot: "+t+" stop motors"),null==this.hardwareState.actions.motors&&(this.hardwareState.actions.motors={}),this.hardwareState.actions.motors[o.MOTOR_LEFT]=0,this.hardwareState.actions.motors[o.MOTOR_RIGHT]=0},e.prototype.getMotorSpeed=function(t,e,a){var o="robot: "+e+", port: "+a;r.debug(o+" motor get speed");var i=this.hardwareState.motors[a];t.push(i)},e.prototype.setMotorSpeed=function(t,e,a){var o="robot: "+t+", port: "+e;r.debug(o+" motor speed "+a),null==this.hardwareState.actions.motors&&(this.hardwareState.actions.motors={}),this.hardwareState.actions.motors[e]=a,this.hardwareState.motors[e]=a},e.prototype.omniDriveAction=function(t,e,a){return null==this.hardwareState.actions.omniDrive&&(this.hardwareState.actions.omniDrive={}),this.hardwareState.actions.omniDrive[o.X+o.SPEED]=t,this.hardwareState.actions.omniDrive[o.Y+o.SPEED]=e,this.hardwareState.actions.omniDrive[o.ANGLE+o.SPEED]=-a,0},e.prototype.omniDriveDistAction=function(t,e,a){return null==this.hardwareState.actions.omniDrive&&(this.hardwareState.actions.omniDrive={}),0===a||0===t&&0===e?(this.hardwareState.actions.omniDrive[o.X+o.SPEED]=0,this.hardwareState.actions.omniDrive[o.Y+o.SPEED]=0,this.hardwareState.actions.omniDrive[o.DISTANCE]=0):(this.hardwareState.actions.omniDrive[o.X+o.SPEED]=t,this.hardwareState.actions.omniDrive[o.Y+o.SPEED]=e,this.hardwareState.actions.omniDrive[o.DISTANCE]=a,this.setBlocking(!0)),0},e.prototype.omniStopDriveAction=function(){null==this.hardwareState.actions.omniDrive&&(this.hardwareState.actions.omniDrive={}),this.hardwareState.actions.omniDrive[o.X+o.SPEED]=0,this.hardwareState.actions.omniDrive[o.Y+o.SPEED]=0,this.hardwareState.actions.omniDrive[o.ANGLE+o.SPEED]=0},e.prototype.omniDriveTurnAction=function(t,e,a){return null==this.hardwareState.actions.omniDrive&&(this.hardwareState.actions.omniDrive={}),t==o.LEFT&&(e*=-1),(t==o.LEFT&&a<0||t==o.RIGHT&&a<0)&&(e*=-1),0===a&&(e=0),this.hardwareState.actions.omniDrive[o.ANGLE+o.SPEED]=e,this.hardwareState.actions.omniDrive[o.ANGLE]=a,this.setBlocking(!0),0},e.prototype.omniDrivePositionAction=function(t,e,a){return null==this.hardwareState.actions.omniDrive&&(this.hardwareState.actions.omniDrive={}),this.hardwareState.actions.omniDrive[o.POWER]=t,this.hardwareState.actions.omniDrive[o.X]=e,this.hardwareState.actions.omniDrive[o.Y]=a,this.setBlocking(!0),0},e.prototype.showTextAction=function(t,e){var a=""+t;return r.debug('***** show "'+a+'" *****'),this.hardwareState.actions.display={},this.hardwareState.actions.display[e.toLowerCase()]=a,this.setBlocking(!0),0},e.prototype.showTextActionPosition=function(t,e,a){var o=""+t;r.debug('***** show "'+o+'" *****'),this.hardwareState.actions.display={},this.hardwareState.actions.display.text=o,this.hardwareState.actions.display.x=e,this.hardwareState.actions.display.y=a},e.prototype.showImageAction=function(t,e){var a=""+t;r.debug('***** show "'+a+'" *****');var s=t.length,n=0;return e==o.ANIMATION&&(n=200*s),this.hardwareState.actions.display={},this.hardwareState.actions.display.picture=i.clone(t),e&&(this.hardwareState.actions.display.mode=e.toLowerCase()),n},e.prototype.displaySetBrightnessAction=function(t){return r.debug('***** set brightness "'+t+'" *****'),this.hardwareState.actions.display={},this.hardwareState.actions.display[o.BRIGHTNESS]=t,0},e.prototype.lightAction=function(t,e,a){r.debug('***** light action mode= "'+t+" color="+e+'" *****'),void 0!==a?(this.hardwareState.actions.leds||(this.hardwareState.actions.leds={}),this.hardwareState.actions.leds[a]={},this.hardwareState.actions.leds[a][o.MODE]=t,this.hardwareState.actions.leds[a][o.COLOR]=e):(this.hardwareState.actions.led={},this.hardwareState.actions.led[o.MODE]=t,this.hardwareState.actions.led[o.COLOR]=e)},e.prototype.displaySetPixelBrightnessAction=function(t,e,a){return r.debug('***** set pixel x="'+t+", y="+e+", brightness="+a+'" *****'),this.hardwareState.actions.display={},this.hardwareState.actions.display[o.PIXEL]={},this.hardwareState.actions.display[o.PIXEL][o.X]=t,this.hardwareState.actions.display[o.PIXEL][o.Y]=e,this.hardwareState.actions.display[o.PIXEL][o.BRIGHTNESS]=a,0},e.prototype.displayGetPixelBrightnessAction=function(t,e,a){r.debug('***** get pixel x="'+e+", y="+a+'" *****');var i=this.hardwareState.sensors[o.DISPLAY][o.PIXEL];t.push(i[a][e])},e.prototype.clearDisplay=function(){r.debug("clear display"),this.hardwareState.actions.display={},this.hardwareState.actions.display.clear=!0},e.prototype.writePinAction=function(t,e,a){this.hardwareState.actions["pin"+t]={},this.hardwareState.actions["pin"+t][e]={},this.hardwareState.actions["pin"+t][e]=a},e.prototype.gyroReset=function(t){r.debug("***** reset gyro *****"),this.hardwareState.actions.gyroReset={},void 0!==t?(this.hardwareState.actions.gyroReset[t]={},this.hardwareState.actions.gyroReset[t]=!0):this.hardwareState.actions.gyroReset=!0},e.prototype.odometryReset=function(t){this.hardwareState.actions.odometry={},this.hardwareState.actions.odometry.reset=t,null==this.hardwareState.actions.omniDrive&&(this.hardwareState.actions.omniDrive={}),this.hardwareState.actions.omniDrive.reset=t},e.prototype.debugAction=function(t){r.debug('***** debug action "'+t+'" *****'),console.log(t)},e.prototype.assertAction=function(t,e,a,o,i){r.debug('***** assert action "'+i+" "+t+" "+e+" "+a+" "+o+'" *****'),console.assert(i,t+" "+e+" "+a+" "+o)},e.prototype.close=function(){},e.prototype.timerGet=function(t){return 0},e.prototype.recall=function(t){t.push(globalThis.rob3rtaNumber)},e.prototype.remember=function(t){globalThis.rob3rtaNumber=t},e.prototype.circleLedAction=function(t){this.hardwareState.actions.cirleLeds=t},e.prototype.buttonLedAction=function(t){this.hardwareState.actions.buttonLeds=t},e.prototype.proxHLedAction=function(t){this.hardwareState.actions.proxHLeds=t},e.prototype.soundLedAction=function(t){this.hardwareState.actions.soundLed=t},e.prototype.temperatureLedAction=function(t,e){this.hardwareState.actions.temperatureLeds=[e,t]},e}(a.ARobotBehaviour);e.RobotSimBehaviour=s}));
//# sourceMappingURL=interpreter.robotSimBehaviour.js.map
//# sourceMappingURL=interpreter.robotSimBehaviour.js.map
