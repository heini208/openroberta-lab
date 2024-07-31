import * as UTIL from 'util.roberta';
import * as MSG from 'message';
import * as GUISTATE from 'guiState.model';
import * as HELP_C from 'progHelp.controller';
import * as LEGAL_C from 'legal.controller';
import * as WEBVIEW_C from 'webview.controller';
import * as CV from 'confVisualization';
import * as $ from 'jquery';
import * as Blockly from 'blockly';
import * as NOTIFICATION_C from 'notification.controller';
import * as CONNECTION_C from 'connection.controller';
import * as PROGRAM_C from 'program.controller';
import * as CONFIGURATION_C from 'configuration.controller';
import * as USER_C from 'user.controller';
import * as NN_C from 'nn.controller';
import { switchLanguage as PROGLIST_C_switchLanguage } from 'progList.controller';
import { switchLanguage as CONFLIST_C_switchLanguage } from 'confList.controller';
import { switchLanguage as GALLERYLIST_C_switchLanguage } from 'galleryList.controller';
import { switchLanguage as TUTORIALLIST_C_switchLanguage } from 'tutorialList.controller';
import { switchLanguage as LOGLIST_C_switchLanguage } from 'logList.controller';
import { switchLanguage as PROGINFO_C_switchLanguage } from 'progInfo.controller';

export var LONG = 300000; // Ping time 5min
export var SHORT = 3000; // Ping time 3sec

/**
 * Init robot
 */
function init(language, opt_data) {
    var ready = $.Deferred();
    $.when(GUISTATE.init()).then(function () {
        GUISTATE.gui.webview = opt_data || false;
        if (GUISTATE.gui.webview) {
            $('.logo').css({
                right: '32px',
            });
        }

        GUISTATE.gui.view = 'start';
        GUISTATE.gui.prevView = 'start';
        GUISTATE.gui.language = language;
        GUISTATE.gui.startWithoutPopup = false;

        GUISTATE.user.id = -1;
        GUISTATE.user.accountName = '';
        GUISTATE.user.name = '';

        GUISTATE.robot.name = '';
        GUISTATE.robot.robotPort = '';

        GUISTATE.program.toolbox.level = 'beginner';
        setProgramOwnerName(null);
        setProgramAuthorName(null);
        setProgramShareRelation(null);
        setProgramName('NEPOprog');

        if (GUISTATE.server.theme !== 'default') {
            var themePath = '../theme/' + GUISTATE.server.theme + '.json';
            $.getJSON(themePath)
                .done(function (data) {
                    // store new theme properties (only colors so far)
                    GUISTATE.server.theme = data;
                })
                .fail(function (e, r) {
                    // this should not happen
                    console.error('"' + themePath + '" is not a valid json file! The reason is probably a', r);
                    GUISTATE.server.theme = 'default';
                });
        }
        ready.resolve();
    });
    return ready.promise();
}

function setInitialState() {
    // Toolbox?
    $('.level').removeClass('disabled');
    $('.level.' + GUISTATE.program.toolbox.level).addClass('disabled');
    // View?
    if (GUISTATE.gui.view === 'tabProgram' || GUISTATE.gui.view === 'start') {
        $('#head-navigation-configuration-edit').css('display', 'none');
        GUISTATE.gui.blocklyWorkspace.markFocused();
    } else if (GUISTATE.gui.view === 'tabConfiguration') {
        $('#head-navigation-program-edit').css('display', 'none');
        GUISTATE.gui.bricklyWorkspace.markFocused();
    }

    // Robot?
    $('#menu-' + GUISTATE.gui.robot)
        .parent()
        .addClass('disabled');
    // Tutorials?
    updateTutorialMenu();
}

function setExtensions(extensions) {
    GUISTATE.robot.extentions = extensions;
}

export function getExtensions() {
    return GUISTATE.robot.extentions;
}

function hasExtension(value) {
    var hasExtension = false;
    for (const extension in GUISTATE.robot.extentions) {
        if (extension === value) {
            hasExtension = true;
        }
    }
    return hasExtension;
}

/**
 * Check if a program is a standard program or not
 *
 */
function isProgramStandard() {
    return GUISTATE.program.name == 'NEPOprog';
}

function isProgramWritable() {
    if (GUISTATE.program.shared == 'WRITE') {
        return true;
    } else if (GUISTATE.program.shared == 'READ') {
        return false;
    }
    return true;
}

function isConfigurationStandard() {
    return GUISTATE.configuration.name == getRobotGroup().toUpperCase() + 'basis';
}

function getConfigurationStandardName() {
    return getRobotGroup().toUpperCase() + 'basis';
}

function isConfigurationAnonymous() {
    return GUISTATE.configuration.name == '';
}

function isKioskMode() {
    return GUISTATE.kiosk && GUISTATE.kiosk === true;
}

function setState(result) {
    if (result['serverVersion']) {
        GUISTATE.server.version = result['serverVersion'];
        $('.labReleaseVersion').text(GUISTATE.server.version);
    }
    if (result['robotVersion']) {
        GUISTATE.robot.version = result['robotVersion'];
    }
    //if (getConnection() !== getConnectionTypeEnum().TDM) {
    if (result['robotFirmwareName'] != undefined) {
        GUISTATE.robot.fWName = result['robotFirmwareName'];
    } else {
        GUISTATE.robot.fWName = '';
    }
    if (result['robotWait'] != undefined) {
        GUISTATE.robot.time = result['robotWait'];
    } else {
        GUISTATE.robot.time = -1;
    }
    if (result['robotBattery'] != undefined) {
        GUISTATE.robot.battery = result['robotBattery'];
    } else {
        GUISTATE.robot.battery = '';
    }
    if (result['robotName'] != undefined) {
        GUISTATE.robot.name = result['robotName'];
    } else {
        GUISTATE.robot.name = '';
    }
    if (result['robotState'] != undefined) {
        GUISTATE.robot.state = result['robotState'];
    } else {
        GUISTATE.robot.state = '';
    }
    //}
    if (result['robotSensorvalues'] != undefined) {
        GUISTATE.robot.sensorValues = result['robotSensorvalues'];
    } else {
        GUISTATE.robot.sensorValues = '';
    }
    if (result['robotNepoexitvalue'] != undefined) {
        //TODO: For different robots we have different error messages
        if (result['robotNepoexitvalue'] !== GUISTATE.robot.nepoExitValue) {
            GUISTATE.nepoExitValue = result['robotNepoexitvalue'];
            if (GUISTATE.nepoExitValue !== 143 && GUISTATE.robot.nepoExitValue !== 0) {
                MSG.displayMessage('POPUP_PROGRAM_TERMINATED_UNEXPECTED', 'POPUP', '');
            }
        }
    }
    if (GUISTATE.user.accountName) {
        $('#iconDisplayLogin').removeClass('error');
        $('#iconDisplayLogin').addClass('ok');
    } else {
        $('#iconDisplayLogin').removeClass('ok');
        $('#iconDisplayLogin').addClass('error');
    }
    if (CONNECTION_C.getConnectionInstance() != null && CONNECTION_C.getConnectionInstance() !== undefined) {
        CONNECTION_C.getConnectionInstance().setState();
    }
}

function getBlocklyWorkspace() {
    return GUISTATE.gui.blocklyWorkspace;
}

function setBlocklyWorkspace(workspace) {
    GUISTATE.gui.blocklyWorkspace = workspace;
}

function getBricklyWorkspace() {
    return GUISTATE.gui.bricklyWorkspace;
}

function setBricklyWorkspace(workspace) {
    GUISTATE.gui.bricklyWorkspace = workspace;
}

function setRobot(robot, result, opt_init) {
    // make sure we use the group instead of the specific robottype if the robot belongs to a group
    var robotGroup = findGroup(robot);
    GUISTATE.gui.program = result.program;
    GUISTATE.gui.configuration = result.configuration;
    GUISTATE.gui.sim = result.sim;
    GUISTATE.gui.multipleSim = result.multipleSim;
    GUISTATE.gui.markerSim = result.markerSim;
    GUISTATE.gui.pluginSim = result.pluginSim;
    GUISTATE.gui.nnActivations = result.nnActivations;
    GUISTATE.gui.webotsSim = result.webotsSim;
    GUISTATE.gui.webotsUrl = result.webotsUrl;
    GUISTATE.gui.vendor = result.vendor;
    GUISTATE.gui.signature = result.signature;
    GUISTATE.gui.commandLine = result.commandLine;
    GUISTATE.gui.configurationUsed = result.configurationUsed;
    GUISTATE.gui.sourceCodeFileExtension = result.sourceCodeFileExtension;
    GUISTATE.gui.binaryFileExtension = result.binaryFileExtension;
    GUISTATE.gui.firmwareDefault = result.firmwareDefault;

    $('#blocklyDiv, #bricklyDiv').css('background', 'url(../../../../css/img/' + robotGroup + 'Background.jpg) repeat');
    $('#blocklyDiv, #bricklyDiv').css('background-size', '100%');
    $('#blocklyDiv, #bricklyDiv').css('background-position', 'initial');

    if (!isConfigurationUsed()) {
        $('#bricklyDiv').css('background', 'url(../../../../css/img/' + robotGroup + 'BackgroundConf.svg) no-repeat');
        $('#bricklyDiv').css('background-position', 'center');
        $('#bricklyDiv').css('background-size', '75% auto');
    } else if (CV.CircuitVisualization.isRobotVisualized(robotGroup, robot)) {
        $('#bricklyDiv').css('background', '');
        $('#bricklyDiv').css('background-position', '');
        $('#bricklyDiv').css('background-size', '');
    }

    $('.robotType').removeClass('disabled');
    $('.robotType.' + robot).addClass('disabled');
    $('#head-navi-icon-robot').removeClass('typcn-open');
    $('#head-navi-icon-robot').removeClass('typcn-' + GUISTATE.gui.robotGroup);
    $('#head-navi-icon-robot').addClass('typcn-' + robotGroup);
    $('.simWindow').removeClass('simWindow-openedButHidden');

    checkSim();
    setProgramOwnerName(null);
    setProgramAuthorName(null);
    setProgramShareRelation(null);
    if (!opt_init) {
        setProgramSaved(true);
        setConfigurationSaved(true);
        if (findGroup(robot) != getRobotGroup()) {
            setConfigurationName(robotGroup.toUpperCase() + 'basis');
            setProgramName('NEPOprog');
        }
    } else {
        setConfigurationName(robotGroup.toUpperCase() + 'basis');
        setProgramName('NEPOprog');
    }

    $('#simRobot').removeClass('typcn-' + GUISTATE.gui.robotGroup);
    $('#simRobot').addClass('typcn-' + robotGroup);

    var groupSwitched = false;
    if (findGroup(robot) != getRobotGroup()) {
        groupSwitched = true;
    }

    if (GUISTATE.gui.firmwareDefault === undefined) {
        $('#robotDefaultFirmware').addClass('hidden');
    } else {
        $('#robotDefaultFirmware').removeClass('hidden');
    }

    GUISTATE.gui.robot = robot;
    GUISTATE.gui.robotGroup = robotGroup;

    var value = Blockly.Msg.MENU_START_BRICK;
    if (value.indexOf('$') >= 0) {
        value = value.replace('$', getRobotRealName());
    }
    $('#menuRunProg').html(value);
    if (GUISTATE.gui.blocklyWorkspace) {
        GUISTATE.gui.blocklyWorkspace.robControls.refreshTooltips(getRobotRealName());
    }
    if (groupSwitched) {
        HELP_C.initView();
        if (inWebview()) {
            WEBVIEW_C.setRobotBehaviour();
            WEBVIEW_C.jsToAppInterface({
                target: 'internal',
                type: 'setRobot',
                robot: robotGroup,
            });
        }
    }

    if (hasExtension('nn')) {
        $('#nn-activations').empty();
        $('.tabLinkNN').show();
        $.each(GUISTATE.gui.nnActivations, function (_, item) {
            $('#nn-activations').append(
                $('<option>', {
                    value: item,
                    text: UTIL.activationDisplayName[item],
                })
            );
        });
    } else {
        $('.tabLinkNN').hide();
    }

    UTIL.clearTabAlert('tabConfiguration'); // also clear tab alert when switching robots
}

export function resetRobot() {
    GUISTATE.gui.robot = undefined;
}

function setKioskMode(kiosk) {
    GUISTATE.kiosk = kiosk;
}

function findGroup(robot) {
    var robots = getRobots();
    for (var propt in robots) {
        if (robots[propt].name == robot && robots[propt].group !== '') {
            robot = robots[propt].group;
            return robot;
        }
    }
    return robot;
}

function findRobot(group) {
    var robots = getRobots();
    let robot;
    for (robot in robots) {
        if (robots[robot].group === group) {
            return robots[robot].name;
            break;
        }
    }
    return null;
}

function setConnectionState(state) {
    switch (state) {
        case 'busy':
            $('#head-navi-icon-robot').removeClass('error');
            $('#head-navi-icon-robot').removeClass('wait');
            $('#head-navi-icon-robot').addClass('busy');
            setRunEnabled(false);
            break;
        case 'error':
            $('#head-navi-icon-robot').removeClass('busy');
            $('#head-navi-icon-robot').removeClass('wait');
            $('#head-navi-icon-robot').addClass('error');
            setRunEnabled(false);
            break;
        case 'wait':
            if (isRobotConnected()) {
                $('#head-navi-icon-robot').removeClass('busy');
                $('#head-navi-icon-robot').removeClass('error');
                $('#head-navi-icon-robot').addClass('wait');
                setRunEnabled(true);
            } else {
                setConnectionState('error');
            }
            break;
        default:
            break;
    }
}

function isRunEnabled() {
    return GUISTATE.gui.runEnabled;
}

function setRunEnabled(running) {
    running ? true : false;
    GUISTATE.gui.runEnabled = running;
    if (running) {
        GUISTATE.gui.blocklyWorkspace && GUISTATE.gui.blocklyWorkspace.robControls.enable('runOnBrick');
        GUISTATE.gui.blocklyWorkspace && GUISTATE.gui.blocklyWorkspace.robControls.enable('stopProgram');
        $('.menuRunProg, #runSourceCodeEditor').removeClass('disabled');
    } else {
        GUISTATE.gui.blocklyWorkspace && GUISTATE.gui.blocklyWorkspace.robControls.disable('runOnBrick');
        GUISTATE.gui.blocklyWorkspace && GUISTATE.gui.blocklyWorkspace.robControls.disable('stopProgram');
        $('.menuRunProg, #runSourceCodeEditor').addClass('disabled');
    }
}

function getRobot() {
    return GUISTATE.gui.robot;
}

function getRobotGroup() {
    return GUISTATE.gui.robotGroup;
}

function setRobotPort(port) {
    GUISTATE.robot.robotPort = port;
}

function getRobotPort() {
    return GUISTATE.robot.robotPort;
}

function getRobotRealName() {
    for (var robot in getRobots()) {
        if (!getRobots().hasOwnProperty(robot)) {
            continue;
        }
        if (getRobots()[robot].name == getRobot()) {
            return getRobots()[robot].realName;
        }
    }
    return getRobot();
}

function getMenuRobotRealName(robotName) {
    for (var robot in getRobots()) {
        if (!getRobots().hasOwnProperty(robot)) {
            continue;
        }
        if (getRobots()[robot].name == robotName) {
            return getRobots()[robot].realName;
        }
    }
    return 'Robot not found';
}

function isRobotBeta(robotName) {
    return getRobotsByName()[robotName].announcement == 'beta';
}

export function isRobotDeprecated(robot) {
    return getRobotsByName()[robot].announcement == 'deprecated';
}

export function getRobotDeprecatedData(robot) {
    if (isRobotDeprecated(robot)) {
        return NOTIFICATION_C.getDeprecatedNotifications(robot, getLanguage());
    }
}

function getRobotInfoDE(robotName) {
    for (var robot in getRobots()) {
        if (!getRobots().hasOwnProperty(robot)) {
            continue;
        }
        if (getRobots()[robot].name == robotName) {
            return getRobots()[robot].infoDE;
        }
    }
    return '#';
}

function getRobotInfoEN(robotName) {
    for (var robot in getRobots()) {
        if (!getRobots().hasOwnProperty(robot)) {
            continue;
        }
        if (getRobots()[robot].name == robotName) {
            return getRobots()[robot].infoEN;
        }
    }
    return '#';
}

function isRobotConnected() {
    if (!CONNECTION_C.getConnectionInstance()) return false;
    return CONNECTION_C.getConnectionInstance().isRobotConnected();
}

function isConfigurationUsed() {
    return GUISTATE.gui.configurationUsed;
}

function isRobotDisconnected() {
    return (GUISTATE.robot.time = -1);
}

function getRobotTime() {
    return GUISTATE.robot.time;
}

function getRobotName() {
    return GUISTATE.robot.name;
}

function getRobotBattery() {
    return GUISTATE.robot.battery;
}

function getRobotState() {
    return GUISTATE.robot.state;
}

function getRobotVersion() {
    return GUISTATE.robot.version;
}

function hasRobotDefaultFirmware() {
    return GUISTATE.gui.firmwareDefault;
}

function setView(view) {
    if (GUISTATE.gui.view === view) {
        return;
    }
    $('#head-navi-tooltip-program').attr('data-bs-toggle', 'dropdown');
    $('#head-navi-tooltip-configuration').attr('data-bs-toggle', 'dropdown');
    $('#head-navi-tooltip-robot').attr('data-bs-toggle', 'dropdown');
    $('#head-navigation-program-edit').removeClass('disabled');
    $('.robotType').removeClass('disabled');
    $('#head-navigation-configuration-edit').removeClass('disabled');
    GUISTATE.gui.prevView = GUISTATE.gui.view;
    GUISTATE.gui.view = view;
    if (!isRobotConnected()) {
        setRunEnabled(false);
        $('#runSourceCodeEditor').addClass('disabled');
    }
    if ($('.rightMenuButton.rightActive').length > 0) {
        $('.rightMenuButton.rightActive').clickWrap();
    }
    if (view === 'tabConfiguration') {
        $('#head-navigation-program-edit').css('display', 'none');
        $('#head-navigation-configuration-edit').css('display', 'inline');
        $('#menuTabProgram').parent().removeClass('disabled');
        $('#menuTabNN').parent().removeClass('disabled');
        $('#menuTabNNLearn').parent().removeClass('disabled');
        $('#menuTabConfiguration').parent().addClass('disabled');
        UTIL.clearTabAlert(view);
    } else if (view === 'tabProgram') {
        $('#head-navigation-configuration-edit').css('display', 'none');
        $('#head-navigation-program-edit').css('display', 'inline');
        $('#menuTabConfiguration').parent().removeClass('disabled');
        $('#menuTabNN').parent().removeClass('disabled');
        $('#menuTabNNLearn').parent().removeClass('disabled');
        $('#menuTabProgram').parent().addClass('disabled');
    } else if (view === 'tabNN') {
        $('#head-navigation-configuration-edit').css('display', 'none');
        $('#head-navigation-program-edit').css('display', 'inline');
        $('#menuTabConfiguration').parent().removeClass('disabled');
        $('#menuTabProgram').parent().removeClass('disabled');
        $('#menuTabNN').parent().addClass('disabled');
        $('#menuTabNNLearn').parent().removeClass('disabled');
    } else if (view === 'tabNNlearn') {
        $('#head-navigation-configuration-edit').css('display', 'none');
        $('#head-navigation-program-edit').css('display', 'inline');
        $('#menuTabConfiguration').parent().removeClass('disabled');
        $('#menuTabProgram').parent().removeClass('disabled');
        $('#menuTabNN').parent().removeClass('disabled');
        $('#menuTabNNLearn').parent().addClass('disabled');
    } else if (view === 'tabSourceCodeEditor') {
        $('#head-navigation-configuration-edit').css('display', 'none');
        $('#head-navigation-program-edit').css('display', 'inline');
        $('#menuTabProgram').parent().removeClass('disabled');
        $('#menuTabConfiguration').parent().removeClass('disabled');
        $('#menuTabNN').parent().removeClass('disabled');
        $('#menuTabNNLearn').parent().removeClass('disabled');
        $('#head-navigation-program-edit').addClass('disabled');
        $('.robotType').addClass('disabled');
        $('#head-navi-tooltip-program').attr('data-bs-toggle', '');
        $('#head-navi-tooltip-configuration').attr('data-bs-toggle', '');
    } else {
        $('#head-navi-tooltip-program').attr('data-bs-toggle', '');
        $('#head-navi-tooltip-configuration').attr('data-bs-toggle', '');
        $('#head-navigation-program-edit').addClass('disabled');
        $('#head-navigation-configuration-edit').addClass('disabled');
    }
}

function getView() {
    return GUISTATE.gui.view;
}

function getPrevView() {
    return GUISTATE.gui.prevView;
}

function setLanguage(language) {
    $('#language li a[lang=' + language + ']')
        .parent()
        .addClass('disabled');
    $('#language li a[lang=' + GUISTATE.gui.language + ']')
        .parent()
        .removeClass('disabled');
    if (language === 'de') {
        $('.EN').css('display', 'none');
        $('.DE').css('display', 'inline');
        $('li>a.DE').css('display', 'block');
    } else {
        $('.DE').css('display', 'none');
        $('.EN').css('display', 'inline');
        $('li>a.EN').css('display', 'block');
    }
    GUISTATE.gui.language = language;
    HELP_C.initView();
    LEGAL_C.loadLegalTexts();
    USER_C.initValidationMessages();
    NOTIFICATION_C.reloadNotifications();
    if (getView() !== 'tabStart') {
        PROGRAM_C.reloadView();
        CONFIGURATION_C.reloadView();
        PROGLIST_C_switchLanguage();
        CONFLIST_C_switchLanguage();
        GALLERYLIST_C_switchLanguage();
        TUTORIALLIST_C_switchLanguage();
        LOGLIST_C_switchLanguage();
        PROGINFO_C_switchLanguage();
        NN_C.reloadViews();
        var value = Blockly.Msg.MENU_START_BRICK;
        if (value.indexOf('$') >= 0) {
            value = value.replace('$', getRobotRealName());
        }
        $('#menuRunProg').text(value);
        if (getBlocklyWorkspace()) {
            getBlocklyWorkspace().robControls.refreshTooltips(getRobotRealName());
        }
        $('#infoContent').attr('data-placeholder', Blockly.Msg.INFO_DOCUMENTATION_HINT || 'Document your program here ...');
        $('.bootstrap-tagsinput input').attr('placeholder', Blockly.Msg.INFO_TAGS || 'Tags');
        updateTutorialMenu();
    }
}

function getLanguage() {
    return GUISTATE.gui.language;
}

function isProgramSaved() {
    return GUISTATE.program.saved;
}

function setProgramSaved(save) {
    if (save) {
        $('#menuSaveProg').parent().removeClass('disabled');
        $('#menuSaveProg').parent().addClass('disabled');
        getBlocklyWorkspace().robControls.disable('saveProgram');
    } else {
        if (isUserLoggedIn() && !isProgramStandard() && isProgramWritable()) {
            $('#menuSaveProg').parent().removeClass('disabled');
            getBlocklyWorkspace().robControls.enable('saveProgram');
        } else {
            $('#menuSaveProg').parent().removeClass('disabled');
            $('#menuSaveProg').parent().addClass('disabled');
            getBlocklyWorkspace().robControls.disable('saveProgram');
        }
    }
    GUISTATE.program.saved = save;
}

function isConfigurationSaved() {
    return GUISTATE.configuration.saved;
}

function setConfigurationSaved(save) {
    if (save) {
        $('#menuSaveConfig').parent().removeClass('disabled');
        $('#menuSaveConfig').parent().addClass('disabled');
        getBricklyWorkspace().robControls.disable('saveProgram');
    } else {
        if (isUserLoggedIn() && !isConfigurationStandard() && !isConfigurationAnonymous()) {
            $('#menuSaveConfig').parent().removeClass('disabled');
            getBricklyWorkspace().robControls.enable('saveProgram');
        } else {
            $('#menuSaveConfig').parent().removeClass('disabled');
            $('#menuSaveConfig').parent().addClass('disabled');
            getBricklyWorkspace().robControls.disable('saveProgram');
        }
    }
    GUISTATE.configuration.saved = save;
}

function getProgramShared() {
    return GUISTATE.program.shared;
}

function setProgramSource(source) {
    GUISTATE.program.source = source;
}

function getProgramSource() {
    return GUISTATE.program.source;
}

function getSourceCodeFileExtension() {
    return GUISTATE.gui.sourceCodeFileExtension;
}

function getBinaryFileExtension() {
    return GUISTATE.gui.binaryFileExtension;
}

function isUserLoggedIn() {
    return GUISTATE.user.id >= 0;
}

function getProgramTimestamp() {
    return GUISTATE.program.timestamp;
}

function setProgramTimestamp(timestamp) {
    GUISTATE.program.timestamp = timestamp;
}

function getProgramName() {
    return GUISTATE.program.name;
}

function setProgramName(name) {
    var displayName = name;
    if (getProgramShareRelation() && getProgramShareRelation() !== 'NONE' && getProgramOwnerName() !== getUserAccountName()) {
        var owner = getProgramOwnerName(),
            author = getProgramAuthorName(),
            relation = getProgramShareRelation(),
            icon = '',
            content = '',
            suffix = '';

        if (owner === 'Gallery') {
            // user has uploaded this program to the gallery
            icon = 'th-large-outline';
            if (relation === 'READ') {
                content = author;
            }
        } else if (owner === 'Roberta') {
            // user loads a program from the example program list
            icon = 'roberta';
        } else if (relation == 'WRITE') {
            // user loads a program, owned by another user, but with WRITE rights
            icon = 'pencil';
            suffix = '<span style="color:#33B8CA;">' + owner + '</span>';
        } else if (relation == 'READ') {
            // user loads a program, owned by another user, but with READ rights
            icon = 'eye';
            suffix = '<span style="color:#33B8CA;">' + owner + '</span>';
        }

        displayName += ' <b><span style="color:#33B8CA;" class="typcn typcn-' + icon + ' progName">' + content + '</span></b>' + suffix;
    }
    $('#tabProgramName').html(displayName);
    GUISTATE.program.name = name;
}

function getProgramOwnerName() {
    return GUISTATE.program.owner || getUserAccountName();
}

function setProgramOwnerName(name) {
    GUISTATE.program.owner = name;
}

function getProgramAuthorName() {
    return GUISTATE.program.author || getUserAccountName();
}

function setProgramAuthorName(name) {
    GUISTATE.program.author = name;
}

function getProgramShareRelation() {
    return GUISTATE.program.shared;
}

function setProgramShareRelation(relation) {
    GUISTATE.program.shared = relation;
}

function getConfigurationName() {
    return GUISTATE.configuration.name;
}

function setConfigurationName(name) {
    $('#tabConfigurationName').html(name);
    GUISTATE.configuration.name = name;
}

function setConfigurationNameDefault() {
    setConfigurationName(getConfigurationStandardName());
}

function setProgramToolboxLevel(level) {
    $('.level').removeClass('disabled');
    $('.level.' + level).addClass('disabled');
    GUISTATE.program.toolbox.level = level;
}

function getProgramToolboxLevel() {
    return GUISTATE.program.toolbox.level;
}

function getToolbox(level) {
    return GUISTATE.gui.program.toolbox[level];
}

function getConfToolbox() {
    return GUISTATE.conf.toolbox;
}

function getRobotFWName() {
    return GUISTATE.robot.fWName;
}

function setRobotToken(token) {
    GUISTATE.robot.token = token;
}

function setRobotUrl(url) {
    GUISTATE.robot.url = url;
}

function setConfigurationXML(xml) {
    GUISTATE.configuration.xml = xml;
}

function getConfigurationXML() {
    return GUISTATE.configuration.xml;
}

function setProgramXML(xml) {
    GUISTATE.program.xml = xml;
}

function getProgramXML() {
    return GUISTATE.program.xml;
}

function getRobots() {
    return GUISTATE.server.robots;
}

function getRobotsByName() {
    return GUISTATE.server.robotsByName;
}

function getProgramToolbox() {
    if (GUISTATE.gui.program.dynamicToolbox) {
        return GUISTATE.gui.program.dynamicToolbox;
    } else {
        return GUISTATE.gui.program.toolbox[GUISTATE.program.toolbox.level];
    }
}

export function setDynamicProgramToolbox(toolbox) {
    GUISTATE.gui.program['dynamicToolbox'] = toolbox;
}

export function resetDynamicProgramToolbox() {
    delete GUISTATE.gui.program['dynamicToolbox'];
}

function getConfigurationToolbox() {
    return GUISTATE.gui.configuration.toolbox;
}

function getProgramProg() {
    return GUISTATE.gui.program.prog;
}

function getConfigurationConf() {
    return GUISTATE.gui.configuration.conf;
}

function getStartWithoutPopup() {
    return GUISTATE.gui.startWithoutPopup;
}

function setStartWithoutPopup() {
    return (GUISTATE.gui.startWithoutPopup = true);
}

function getServerVersion() {
    return GUISTATE.server.version;
}

function isPublicServerVersion() {
    return GUISTATE.server.isPublic;
}

function getUserName() {
    return GUISTATE.user.name;
}

function getUserAccountName() {
    return GUISTATE.user.accountName;
}

function isUserAccountActivated() {
    return GUISTATE.user.isAccountActivated;
}

function isUserMemberOfUserGroup() {
    return GUISTATE.user.userGroup != '';
}

function getUserUserGroup() {
    return GUISTATE.user.userGroup;
}

function getUserUserGroupOwner() {
    return GUISTATE.user.userGroupOwner;
}

function setLogin(result) {
    setState(result);
    GUISTATE.user.accountName = result.userAccountName;
    if (result.userName === undefined || result.userName === '') {
        GUISTATE.user.name = result.userAccountName;
    } else {
        GUISTATE.user.name = result.userName;
    }
    GUISTATE.user.id = result.userId;
    GUISTATE.user.isAccountActivated = result.isAccountActivated;
    GUISTATE.user.userGroup = result.userGroupName;
    GUISTATE.user.userGroupOwner = result.userGroupOwner;

    $('.navbar-nav > li > ul > .login, .logout').removeClass('disabled');
    $('.navbar-nav > li > ul > .login.unavailable').addClass('disabled');
    $('.navbar-nav > li > ul > .logout').addClass('disabled');
    $('#head-navi-icon-user').removeClass('error');
    $('#head-navi-icon-user').addClass('ok');
    $('.menuLogin').addClass('pe-none');
    $('#menuSaveProg').parent().addClass('disabled');
    $('#menuSaveConfig').parent().addClass('disabled');

    if (getRobot()) {
        setProgramSaved(true);
        setConfigurationSaved(true);
        if (GUISTATE.gui.view == 'tabGalleryList') {
            $('#galleryList').find('button[name="refresh"]').clickWrap();
        }
    }
    if (isUserMemberOfUserGroup()) {
        $('#registerUserName, #registerUserEmail').prop('disabled', true);
        $('#userGroupMemberDefaultPasswordHint').removeClass('hidden');
    }
}

function setLogout() {
    if (isUserMemberOfUserGroup()) {
        $('#registerUserName, #registerUserEmail').prop('disabled', false);
        $('#userGroupMemberDefaultPasswordHint').addClass('hidden');
    }

    GUISTATE.user.id = -1;
    GUISTATE.user.accountName = '';
    GUISTATE.user.name = '';
    GUISTATE.user.userGroup = '';
    GUISTATE.user.userGroupOwner = '';
    if (getView() === 'tabUserGroupList') {
        $('#' + getPrevView()).tabWrapShow();
    }
    setProgramName('NEPOprog');
    setProgramOwnerName(null);
    setProgramAuthorName(null);
    setProgramShareRelation(null);
    GUISTATE.program.shared = false;
    $('.navbar-nav > li > ul > .logout, .login').removeClass('disabled');
    $('.navbar-nav > li > ul > .login').addClass('disabled');
    $('#head-navi-icon-user').removeClass('ok');
    $('#head-navi-icon-user').addClass('error');
    if (GUISTATE.gui.view == 'tabProgList') {
        $('#tabProgram').tabWrapShow();
    } else if (GUISTATE.gui.view == 'tabConfList') {
        $('#tabConfiguration').clickWrap();
    } else if (GUISTATE.gui.view == 'tabGalleryList') {
        $('#galleryList').find('button[name="refresh"]').clickWrap();
    }
    $('.menuLogin').removeClass('pe-none');
}

function setProgram(result, opt_owner, opt_author) {
    if (result) {
        GUISTATE.program.shared = result.programShared;
        GUISTATE.program.timestamp = result.lastChanged;
        setProgramSaved(true);
        setConfigurationSaved(true);
        var name = result.name;

        setProgramShareRelation(result.programShared);
        if (opt_owner) {
            setProgramOwnerName(opt_owner);
        } else if (result.parameters && result.parameters.OWNER_NAME) {
            setProgramOwnerName(result.parameters.OWNER_NAME);
        } else {
            setProgramOwnerName(null);
        }

        if (opt_author) {
            setProgramAuthorName(opt_author);
        } else if (result.parameters && result.parameters.AUTHOR_NAME) {
            setProgramAuthorName(result.parameters.AUTHOR_NAME);
        } else {
            setProgramOwnerName(null);
        }
        setProgramName(result.name);
    }
}

/**
 * Set configuration
 * @param {*} result
 */
function setConfiguration(result) {
    if (result) {
        setConfigurationName(result.name);
        GUISTATE.configuration.timestamp = result.lastChanged;
        setConfigurationSaved(true);
        setProgramSaved(false);
        $('#tabConfigurationName').html(result.name);
    }
}

function checkSim() {
    if (hasSim()) {
        if (hasMarkerSim()) {
            $('#simMarkerObject').parent().css('display', 'inline-block');
        } else {
            $('#simMarkerObject').parent().css('display', 'none');
        }
        $('#menuRunSim').parent().removeClass('disabled');
        $('#simButton, #simDebugButton').show();
    } else {
        $('#menuRunSim').parent().addClass('disabled');
        $('#simButton, #simDebugButton').hide();
    }
    if (hasWebotsSim()) {
        $('#simDebugButton').hide();
    }
    if (hasMultiSim()) {
        $('#menuRunMulipleSim').parent().removeClass('unavailable');
        $('#menuRunMulipleSim').parent().addClass('available');
        $('#menuRunMulipleSim').parent().removeClass('disabled');
    } else {
        $('#menuRunMulipleSim').parent().addClass('unavailable');
        $('#menuRunMulipleSim').parent().removeClass('available');
        $('#menuRunMulipleSim').parent().addClass('disabled');
    }
}

function hasSim() {
    return GUISTATE.gui.sim == true;
}

function hasMultiSim() {
    return GUISTATE.gui.multipleSim == true;
}

function hasMarkerSim() {
    return GUISTATE.gui.markerSim == true;
}

export function getPluginSim() {
    return GUISTATE.gui.pluginSim || null;
}

function hasWebotsSim() {
    return GUISTATE.gui.webotsSim == true;
}

function getWebotsUrl() {
    return GUISTATE.gui.webotsUrl;
}

function getListOfTutorials() {
    return GUISTATE.server.tutorial;
}

function getVendor() {
    return GUISTATE.gui.vendor;
}

function getSignature() {
    return GUISTATE.gui.signature;
}

function getCommandLine() {
    return GUISTATE.gui.commandLine;
}

function setPing(ping) {
    GUISTATE.server.ping = ping;
}

function doPing() {
    return GUISTATE.server.ping;
}

function setPingTime(time) {
    GUISTATE.server.pingTime = time;
}

function getPingTime() {
    return GUISTATE.server.pingTime;
}

function getAvailableHelp() {
    return GUISTATE.server.help;
}

function getTheme() {
    return GUISTATE.server.theme;
}

function inWebview() {
    return GUISTATE.gui.webview || false;
}

function setWebview(webview) {
    GUISTATE.gui.webview = webview;
}

function updateMenuStatus(numOfConnections) {
    CONNECTION_C.getConnectionInstance().updateMenuStatus(numOfConnections);
}

function updateTutorialMenu() {
    $('#head-navigation-tutorial').hide();
    var tutorialList = getListOfTutorials();
    for (var tutorial in tutorialList) {
        if (tutorialList.hasOwnProperty(tutorial) && tutorialList[tutorial].language === getLanguage().toUpperCase()) {
            $('#head-navigation-tutorial').show();
            break;
        }
    }
}

function getLegalTextsMap() {
    return GUISTATE.server.legalTexts;
}
export {
    init,
    setInitialState,
    isProgramStandard,
    isProgramWritable,
    isConfigurationStandard,
    getConfigurationStandardName,
    isConfigurationAnonymous,
    isKioskMode,
    setState,
    getBlocklyWorkspace,
    setBlocklyWorkspace,
    getBricklyWorkspace,
    setBricklyWorkspace,
    setRobot,
    setKioskMode,
    findGroup,
    findRobot,
    setConnectionState,
    isRunEnabled,
    setRunEnabled,
    getRobot,
    getRobotGroup,
    setRobotPort,
    getRobotPort,
    getRobotRealName,
    getMenuRobotRealName,
    isRobotBeta,
    getRobotInfoDE,
    getRobotInfoEN,
    isRobotConnected,
    isConfigurationUsed,
    isRobotDisconnected,
    getRobotTime,
    getRobotName,
    getRobotBattery,
    getRobotState,
    getRobotVersion,
    hasRobotDefaultFirmware,
    setView,
    getView,
    getPrevView,
    setLanguage,
    getLanguage,
    isProgramSaved,
    setProgramSaved,
    isConfigurationSaved,
    setConfigurationSaved,
    getProgramShared,
    setProgramSource,
    getProgramSource,
    getSourceCodeFileExtension,
    getBinaryFileExtension,
    isUserLoggedIn,
    getProgramTimestamp,
    setProgramTimestamp,
    getProgramName,
    setProgramName,
    getProgramOwnerName,
    setProgramOwnerName,
    getProgramAuthorName,
    setProgramAuthorName,
    getProgramShareRelation,
    setProgramShareRelation,
    getConfigurationName,
    setConfigurationName,
    setConfigurationNameDefault,
    setProgramToolboxLevel,
    getProgramToolboxLevel,
    getToolbox,
    getConfToolbox,
    getRobotFWName,
    setRobotToken,
    setRobotUrl,
    setConfigurationXML,
    getConfigurationXML,
    setProgramXML,
    getProgramXML,
    getRobots,
    getProgramToolbox,
    getConfigurationToolbox,
    getProgramProg,
    getConfigurationConf,
    getStartWithoutPopup,
    setStartWithoutPopup,
    getServerVersion,
    isPublicServerVersion,
    getUserName,
    getUserAccountName,
    isUserAccountActivated,
    isUserMemberOfUserGroup,
    getUserUserGroup,
    getUserUserGroupOwner,
    setLogin,
    setLogout,
    setProgram,
    setConfiguration,
    checkSim,
    hasSim,
    hasMultiSim,
    hasMarkerSim,
    hasWebotsSim,
    getWebotsUrl,
    getListOfTutorials,
    getVendor,
    getSignature,
    getCommandLine,
    setPing,
    doPing,
    setPingTime,
    getPingTime,
    getAvailableHelp,
    getTheme,
    inWebview,
    setWebview,
    updateMenuStatus,
    updateTutorialMenu,
    getLegalTextsMap,
    setExtensions,
    hasExtension,
};
