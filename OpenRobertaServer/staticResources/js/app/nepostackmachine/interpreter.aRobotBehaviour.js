define(["require","exports"],(function(t,e){Object.defineProperty(e,"__esModule",{value:!0}),e.ARobotBehaviour=void 0;var o=function(){function t(){this.hardwareState={},this.hardwareState.actions={},this.hardwareState.sensors={},this.blocking=!1}return t.prototype.getActionState=function(t,e){void 0===e&&(e=!1);var o=this.hardwareState.actions[t];return e&&delete this.hardwareState.actions[t],o},t.prototype.setBlocking=function(t){this.blocking=t},t.prototype.getBlocking=function(){return this.blocking},t}();e.ARobotBehaviour=o}));
//# sourceMappingURL=interpreter.aRobotBehaviour.js.map
//# sourceMappingURL=interpreter.aRobotBehaviour.js.map
