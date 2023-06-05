var __extends=this&&this.__extends||function(){var e=function(o,t){return e=Object.setPrototypeOf||{__proto__:[]}instanceof Array&&function(e,o){e.__proto__=o}||function(e,o){for(var t in o)Object.prototype.hasOwnProperty.call(o,t)&&(e[t]=o[t])},e(o,t)};return function(o,t){if("function"!=typeof t&&null!==t)throw new TypeError("Class extends value "+String(t)+" is not a constructor or null");function n(){this.constructor=o}e(o,t),o.prototype=null===t?Object.create(t):(n.prototype=t.prototype,new n)}}();define(["require","exports","robot.actuators","robot.base.mobile","robot.sensors","jquery"],(function(e,o,t,n,s,r){Object.defineProperty(o,"__esModule",{value:!0});var i=function(e){function o(o,n,s,r,i){var u=e.call(this,o,n,s,r,i)||this;return u.volume=.5,u.webAudio=new t.WebAudio,u.imgList=["simpleBackgroundEdison","drawBackground","rescueBackground","mathBackground"],u.pose.x=60,u.pose.y=60,u.mouse={x:7,y:0,rx:0,ry:0,r:13},n.TRACKWIDTH=7,n.WHEELDIAMETER=3.7,u.chassis=new t.EdisonChassis(u.id,n,3,u.pose),u.configure(n),u}return __extends(o,e),o.prototype.configure=function(e){this.infraredSensors=new s.EdisonInfraredSensors,this.lightSensor=new s.LineSensor({x:15,y:0},3),this.soundSensor=new s.SoundSensorBoolean(this),this.leds=new t.EdisonLeds({x:16.5,y:4.5},this.id,"#fa7000");this.buttons=new s.EV3Keys([{name:"play",value:!1},{name:"rec",value:!1}],this.id);var o=this;for(var n in this.buttons.keys){var i=r("#"+this.buttons.keys[n].name+o.id);i.on("mousedown touchstart",(function(){o.buttons.keys[this.id.replace(/\d+$/,"")].value=!0})),i.on("mouseup touchend",(function(){o.buttons.keys[this.id.replace(/\d+$/,"")].value=!1}))}},o}(n.RobotBaseMobile);o.default=i}));
//# sourceMappingURL=robot.edison.js.map
//# sourceMappingURL=robot.edison.js.map
