define(["require","exports","comm"],(function(o,r,e){Object.defineProperty(r,"__esModule",{value:!0}),r.init=r.robot=r.toolbox=r.configuration=r.program=r.user=r.gui=r.server=void 0,r.server={},r.gui={},r.user={},r.program={},r.configuration={},r.toolbox="",r.robot={},r.init=function(){var o=new $.Deferred;return r.server.ping=!0,r.server.pingTime=3e3,r.server.robotsByName={},r.gui.view="",r.gui.prevView="",r.gui.language="",r.gui.robot="",r.gui.blocklyWorkspace="",r.gui.bricklyWorkspace="",r.gui.program={},r.gui.program.toolbox={},r.gui.program.toolbox.a="",r.gui.program.prog={},r.gui.program={},r.gui.program.toolbox={},r.gui.program.prog={},r.gui.program.download=!1,r.gui.configuration={},r.gui.configuration.toolbox="",r.gui.configuration.conf="",r.gui.connection="",r.gui.vendor="",r.gui.sim=!1,r.gui.multipleSim=!1,r.gui.markerSim=!1,r.gui.nn=!1,r.gui.nnActivations={},r.gui.webotsSim=!1,r.gui.webotsUrl="",r.gui.fileExtension="",r.gui.connectionType={TOKEN:"token",AUTO:"autoConnection",AGENTORTOKEN:"arduinoAgentOrToken",LOCAL:"local",WEBVIEW:"webview",JSPLAY:"jsPlay",TDM:"tdm"},r.gui.runEnabled=!1,r.user.id=-1,r.user.accountName="",r.user.name="",r.user.isAccountActivated=!1,r.program.name="",r.program.saved=!0,r.program.shared=!0,r.program.timestamp="",r.program.source="",r.program.xml="",r.program.toolbox={},r.program.toolbox.level="",r.program.toolbox.xml="",r.configuration.name="",r.configuration.saved=!0,r.configuration.timestamp="",r.configuration.xml="",r.robot.token="",r.robot.name="",r.robot.state="",r.robot.battery="",r.robot.version="",r.robot.fWName="",r.robot.sensorValues="",r.robot.nepoExitValue=0,r.robot.time=-1,r.robot.robotPort="",r.robot.socket=null,r.robot.hasWlan=!1,e.setInitToken(void 0),e.json("/init",{cmd:"init",screenSize:[window.screen.availWidth,window.screen.availHeight]},(function(i){if("ok"===i.rc){for(var n in e.setInitToken(i.initToken),$.extend(r.server,i.server),r.server.robots)r.server.robotsByName[r.server.robots[n].name]=r.server.robots[n];r.server.version=i["server.version"],r.server.time=i.serverTime,o.resolve()}else console.log("ERROR: "+i.message)}),"init data from server"),o.promise()}}));
//# sourceMappingURL=guiState.model.js.map
//# sourceMappingURL=guiState.model.js.map
