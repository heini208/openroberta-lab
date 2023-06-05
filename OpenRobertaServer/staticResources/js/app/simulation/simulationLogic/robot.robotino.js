var __extends=this&&this.__extends||function(){var t=function(e,o){return t=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(t,e){t.__proto__=e}||function(t,e){for(var o in e)Object.prototype.hasOwnProperty.call(e,o)&&(t[o]=e[o])},t(e,o)};return function(e,o){if("function"!=typeof o&&null!==o)throw new TypeError("Class extends value "+String(o)+" is not a constructor or null");function r(){this.constructor=e}t(e,o),e.prototype=null===o?Object.create(o):(r.prototype=o.prototype,new r)}}();define(["require","exports","robot.base.mobile","robot.sensors","robot.actuators"],(function(t,e,o,r,n){Object.defineProperty(e,"__esModule",{value:!0});var i=function(t){function e(e,o,i,s,u){var c=t.call(this,e,o,i,s,u)||this;return c.volume=.5,c.tts=new n.TTS,c.webAudio=new n.WebAudio,c.timer=new r.Timer(5),c.imgList=["square","roboLab.jpg","maze"],c.mouse={x:0,y:0,rx:0,ry:0,r:70},c.configure(o),c}return __extends(e,t),e.prototype.updateActions=function(e,o,r){t.prototype.updateActions.call(this,e,o,r);var n=this.interpreter.getRobotBehaviour().getActionState("volume",!0);(n||0===n)&&(this.volume=n/100)},e.prototype.reset=function(){t.prototype.reset.call(this),this.volume=.5},e.prototype.configure=function(t){this.chassis=new n.RobotinoChassis(this.id,this.pose),this.robotinoTouchSensor=new r.RobotinoTouchSensor,this.infraredSensors=new r.RobotinoInfraredSensors,this.odometrySensor=new r.OdometrySensor,this.cameraSensor=new r.CameraSensor(new o.Pose(25,0,0),2*Math.PI/5);Object.keys(t.ACTUATORS).filter((function(e){return"OPTICAL"==t.ACTUATORS[e].TYPE})).length;var e=this;Object.keys(t.SENSORS).filter((function(e){return"OPTICAL"==t.SENSORS[e].TYPE})).forEach((function(o,n){var i=t.SENSORS[o];e[i.BK]=new r.OpticalSensor(o,i.BK,50,n%2==0?-6:6,0,5)}))},e}(o.RobotBaseMobile);e.default=i}));
//# sourceMappingURL=robot.robotino.js.map
//# sourceMappingURL=robot.robotino.js.map
