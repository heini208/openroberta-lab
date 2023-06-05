var __extends=this&&this.__extends||function(){var e=function(t,s){return e=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(e,t){e.__proto__=t}||function(e,t){for(var s in t)Object.prototype.hasOwnProperty.call(t,s)&&(e[s]=t[s])},e(t,s)};return function(t,s){if("function"!=typeof s&&null!==s)throw new TypeError("Class extends value "+String(s)+" is not a constructor or null");function o(){this.constructor=t}e(t,s),t.prototype=null===s?Object.create(s):(o.prototype=s.prototype,new o)}}();define(["require","exports","robot.base.mobile","robot.sensors","robot.actuators","jquery"],(function(e,t,s,o,n,r){Object.defineProperty(t,"__esModule",{value:!0});var a=function(e){function t(t,s,r,a,i){var c=e.call(this,t,s,r,a,i)||this;return c.volume=.5,c.tts=new n.TTS,c.webAudio=new n.WebAudio,c.timer=new o.Timer(5),c.imgList=["simpleBackground","drawBackground","robertaBackground","rescueBackground","blank","mathBackground"],c.configure(s),c}return __extends(t,e),t.prototype.updateActions=function(t,s,o){e.prototype.updateActions.call(this,t,s,o);var n=this.interpreter.getRobotBehaviour().getActionState("volume",!0);(n||0===n)&&(this.volume=n/100)},t.prototype.reset=function(){e.prototype.reset.call(this),this.volume=.5},t.prototype.configure=function(e){this.chassis=new n.EV3Chassis(this.id,e,2,this.pose),this.led=new n.StatusLed;var t=e.SENSORS,a=function(e){switch(t[e].TYPE){case"TOUCH":i[e]=new o.TouchSensor(e,25,0,i.chassis.geom.color);break;case"GYRO":i[e]=new o.GyroSensorExt(e,0,0,0);break;case"COLOR":var n=[],r=i;Object.keys(i).forEach((function(e){r[e]&&r[e]instanceof o.ColorSensor&&n.push(r[e])}));var a=10*(n.length+1)-5*(Object.keys(t).filter((function(e){return"COLOR"==t[e].TYPE})).length+1);i[e]=new o.ColorSensor(e,15,a,0,5);break;case"INFRARED":case"ULTRASONIC":var c=[],u=i;Object.keys(i).forEach((function(e){u[e]&&u[e]instanceof o.DistanceSensor&&c.push(u[e])}));var h=c.length+1,l=Object.keys(t).filter((function(e){return"ULTRASONIC"==t[e].TYPE||"INFRARED"==t[e].TYPE})).length,f=new s.Pose(i.chassis.geom.x+i.chassis.geom.w,0,0);if(3==l)1==h?f=new s.Pose(i.chassis.geom.h/2,-i.chassis.geom.h/2,-Math.PI/4):2==h&&(f=new s.Pose(i.chassis.geom.h/2,i.chassis.geom.h/2,Math.PI/4));else if(l%2==0)switch(h){case 1:f=new s.Pose(i.chassis.geom.x+i.chassis.geom.w,-i.chassis.geom.h/2,-Math.PI/4);break;case 2:f=new s.Pose(i.chassis.geom.x+i.chassis.geom.w,i.chassis.geom.h/2,Math.PI/4);break;case 3:f=new s.Pose(i.chassis.geom.x,-i.chassis.geom.h/2,-3*Math.PI/4);break;case 4:f=new s.Pose(i.chassis.geom.x,i.chassis.geom.h/2,3*Math.PI/4)}"ULTRASONIC"==t[e].TYPE?i[e]=new o.UltrasonicSensor(e,f.x,f.y,f.theta,255):i[e]=new o.InfraredSensor(e,f.x,f.y,f.theta,70)}},i=this;for(var c in t)a(c);this.buttons=new o.EV3Keys([{name:"escape",value:!1},{name:"up",value:!1},{name:"left",value:!1},{name:"enter",value:!1},{name:"right",value:!1},{name:"down",value:!1}],this.id);var u=this;for(var h in this.buttons.keys){var l=r("#"+this.buttons.keys[h].name+u.id);l.on("mousedown touchstart",(function(){u.buttons.keys[this.id.replace(/\d+$/,"")].value=!0})),l.on("mouseup touchend",(function(){u.buttons.keys[this.id.replace(/\d+$/,"")].value=!1}))}},t}(s.RobotBaseMobile);t.default=a}));
//# sourceMappingURL=robot.ev3.js.map
//# sourceMappingURL=robot.ev3.js.map
