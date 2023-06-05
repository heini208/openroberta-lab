define(["require","exports","log","jquery","robot.controller","guiState.controller","socket.io","comm"],(function(t,o,e,n,r,s,c,a){Object.defineProperty(o,"__esModule",{value:!0}),o.uploadProgram=o.getRobotList=o.getPortList=o.closeConnection=o.init=o.listRobotStop=o.listRobotStart=void 0;var i,u=[],d=[],l=[],b=[],g='[{"Name":"none","IdVendor":"none","IdProduct":"none"}]';function S(){u=[],d=[],l=[],b=[],a.listRobotsFromAgent((function(t){}),(function(t){g=t.responseText}),(function(){}));try{jsonObject=JSON.parse(g),jsonObject.forEach((function(t){s.getVendor()===t.IdVendor.toLowerCase()&&(u.push(t.Name),d.push(t.IdVendor),l.push(t.IdProduct),b.push(s.getRobotRealName()))}))}catch(t){s.setRobotPort("")}u.indexOf(s.getRobotPort())<0&&s.setRobotPort(""),1==u.length&&r.setPort(u[0]),s.updateMenuStatus(m().length)}function m(){return u}o.listRobotStart=function(){n("#menuConnect").parent().addClass("disabled"),S(),i=window.setInterval(S,3e3)},o.listRobotStop=function(){n("#menuConnect").parent().addClass("disabled"),window.clearInterval(i)},o.init=function(){robotSocket=s.getSocket(),null!=robotSocket&&0!=s.getIsAgent()||(robotSocket=c("ws://127.0.0.1:8991/"),s.setSocket(robotSocket),s.setIsAgent(!0),n("#menuConnect").parent().addClass("disabled"),robotSocket.on("connect_error",(function(t){s.setIsAgent(!1)})),robotSocket.on("connect",(function(){robotSocket.emit("command","log on"),s.setIsAgent(!0),window.setInterval((function(){u=[],d=[],l=[],b=[],robotSocket.emit("command","list")}),3e3)})),robotSocket.on("message",(function(t){t.includes('"Network": false')?(jsonObject=JSON.parse(t),jsonObject.Ports.forEach((function(t){s.getVendor()===t.VendorID.toLowerCase()&&(u.push(t.Name),d.push(t.VendorID),l.push(t.ProductID),b.push(s.getRobotRealName()))})),s.setIsAgent(!0),robotSocket.on("connect_error",(function(t){s.setIsAgent(!1),n("#menuConnect").parent().removeClass("disabled")})),u.indexOf(s.getRobotPort())<0&&(s.getRobotPort(),s.setRobotPort("")),1==u.length&&r.setPort(u[0]),s.updateMenuStatus(m().length)):t.includes("OS")&&(jsonObject=JSON.parse(t),jsonObject.OS)})),robotSocket.on("disconnect",(function(){})),robotSocket.on("error",(function(t){})))},o.closeConnection=function(){robotSocket=s.getSocket(),null!=robotSocket&&(robotSocket.disconnect(),s.setSocket(null))},o.getPortList=m,o.getRobotList=function(){return b},o.uploadProgram=function(t,o){a.sendProgramHexToAgent(t,o,s.getProgramName(),s.getSignature(),s.getCommandLine(),(function(){e.text("Create agent upload success"),n("#menuRunProg").parent().removeClass("disabled"),n("#runOnBrick").parent().removeClass("disabled")}))}}));
//# sourceMappingURL=socket.controller.js.map
//# sourceMappingURL=socket.controller.js.map
