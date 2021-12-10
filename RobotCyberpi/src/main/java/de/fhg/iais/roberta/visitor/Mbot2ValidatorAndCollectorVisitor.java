package de.fhg.iais.roberta.visitor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.collect.ClassToInstanceMap;

import de.fhg.iais.roberta.bean.IProjectBean;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.components.ConfigurationComponent;
import de.fhg.iais.roberta.components.UsedActor;
import de.fhg.iais.roberta.components.UsedSensor;
import de.fhg.iais.roberta.constants.CyberpiConstants;
import de.fhg.iais.roberta.syntax.BlocklyConstants;
import de.fhg.iais.roberta.syntax.MotionParam;
import de.fhg.iais.roberta.syntax.MotorDuration;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.SC;
import de.fhg.iais.roberta.syntax.WithUserDefinedPort;
import de.fhg.iais.roberta.syntax.action.Action;
import de.fhg.iais.roberta.syntax.action.MoveAction;
import de.fhg.iais.roberta.syntax.action.display.ClearDisplayAction;
import de.fhg.iais.roberta.syntax.action.mbot2.LedBrightnessAction;
import de.fhg.iais.roberta.syntax.action.mbot2.LedOnActionWithIndex;
import de.fhg.iais.roberta.syntax.action.mbot2.DisplaySetColourAction;
import de.fhg.iais.roberta.syntax.action.mbot2.LedsOffAction;
import de.fhg.iais.roberta.syntax.action.mbot2.PlayRecordingAction;
import de.fhg.iais.roberta.syntax.action.display.ShowTextAction;
import de.fhg.iais.roberta.syntax.action.mbot2.PrintlnAction;
import de.fhg.iais.roberta.syntax.action.mbot2.QuadRGBLightOffAction;
import de.fhg.iais.roberta.syntax.action.mbot2.QuadRGBLightOnAction;
import de.fhg.iais.roberta.syntax.action.mbot2.Ultrasonic2LEDAction;
import de.fhg.iais.roberta.syntax.action.motor.MotorGetPowerAction;
import de.fhg.iais.roberta.syntax.action.motor.MotorOnAction;
import de.fhg.iais.roberta.syntax.action.motor.MotorSetPowerAction;
import de.fhg.iais.roberta.syntax.action.motor.MotorStopAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.CurveAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.MotorDriveStopAction;
import de.fhg.iais.roberta.syntax.action.motor.differential.TurnAction;
import de.fhg.iais.roberta.syntax.action.sound.PlayFileAction;
import de.fhg.iais.roberta.syntax.action.sound.PlayNoteAction;
import de.fhg.iais.roberta.syntax.action.sound.ToneAction;
import de.fhg.iais.roberta.syntax.action.sound.VolumeAction;
import de.fhg.iais.roberta.syntax.lang.blocksequence.MainTask;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.syntax.lang.expr.NumConst;
import de.fhg.iais.roberta.syntax.sensor.Sensor;
import de.fhg.iais.roberta.syntax.sensor.generic.AccelerometerSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.EncoderSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.GyroSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.KeysSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.LightSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.SoundSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.TimerSensor;
import de.fhg.iais.roberta.syntax.sensor.generic.UltrasonicSensor;
import de.fhg.iais.roberta.syntax.sensor.mbot2.Joystick;
import de.fhg.iais.roberta.syntax.sensor.mbot2.QuadRGBSensor;
import de.fhg.iais.roberta.syntax.sensor.mbot2.SoundRecord;
import de.fhg.iais.roberta.util.dbc.Assert;
import de.fhg.iais.roberta.visitor.validate.CommonNepoValidatorAndCollectorVisitor;
import de.fhg.iais.roberta.syntax.action.motor.differential.DriveAction;


public class Mbot2ValidatorAndCollectorVisitor extends CommonNepoValidatorAndCollectorVisitor implements IMbot2Visitor<Void> {

    private static final List<String> NON_BLOCKING_PROPERTIES = Collections.unmodifiableList(Arrays.asList("MOTOR_L", "MOTOR_R", "BRICK_WHEEL_DIAMETER", "BRICK_TRACK_WIDTH"));

    private static final Map<String, String> SENSOR_COMPONENT_TYPE_MAP = new HashMap<String, String>() {{
        put("SOUND_RECORD", SC.SOUND);
        put("QUAD_COLOR_SENSING", CyberpiConstants.QUADRGB);
    }};

    public Mbot2ValidatorAndCollectorVisitor(ConfigurationAst robotConfiguration, ClassToInstanceMap<IProjectBean.IBuilder<?>> beanBuilders) {
        super(robotConfiguration, beanBuilders);
    }

    @Override
    public Void visitMainTask(MainTask<Void> mainTask) {
        validateConfig(mainTask);
        requiredComponentVisited(mainTask, mainTask.getVariables());
        return null;
    }

    private void validateConfig(MainTask<Void> mainTask) {
        List<String> takenPins = new ArrayList<>();
        robotConfiguration.getConfigurationComponents().forEach((k, v) -> checkIfPinTaken(v, mainTask, takenPins));
    }

    private void checkIfPinTaken(ConfigurationComponent configurationComponent, MainTask<Void> mainTask, List<String> takenPins) {
        Map<String, String> componentProperties = configurationComponent.getComponentProperties();
        for ( Map.Entry<String, String> property : componentProperties.entrySet() ) {
            if ( NON_BLOCKING_PROPERTIES.contains(property.getKey()) ) {
                continue;
            }
            if ( takenPins.contains(property.getValue()) ) {
                addErrorToPhrase(mainTask, "CONFIGURATION_ERROR_OVERLAPPING_PORTS");
                break;
            }
            takenPins.add(property.getValue());
        }
    }

    @Override
    public Void visitClearDisplayAction(ClearDisplayAction<Void> clearDisplayAction) {
        usedHardwareBuilder.addUsedActor(new UsedActor("", SC.DISPLAY));
        return null;
    }

    @Override
    public Void visitJoystick(Joystick<Void> joystick) {
        checkSensorPort(joystick);
        usedHardwareBuilder.addUsedSensor(new UsedSensor(joystick.getUserDefinedPort(), SC.JOYSTICK, joystick.slot));
        return null;
    }

    @Override
    public Void visitKeysSensor(KeysSensor<Void> keysSensor) {
        checkSensorPort(keysSensor);
        usedHardwareBuilder.addUsedActor(new UsedActor("", SC.KEY));
        return null;
    }

    @Override
    public Void visitEncoderSensor(EncoderSensor<Void> encoderSensor) {
        ConfigurationComponent configurationComponent = this.robotConfiguration.optConfigurationComponent(encoderSensor.getUserDefinedPort());
        if ( configurationComponent == null ) {
            addErrorToPhrase(encoderSensor, "CONFIGURATION_ERROR_MOTOR_MISSING");
        } else {
            usedHardwareBuilder.addUsedActor(new UsedActor(encoderSensor.getUserDefinedPort(), configurationComponent.getComponentType()));
        }
        return null;
    }

    @Override
    public Void visitSoundSensor(SoundSensor<Void> soundSensor) {
        checkSensorPort(soundSensor);
        usedHardwareBuilder.addUsedSensor(new UsedSensor(soundSensor.getUserDefinedPort(), SC.SOUND, soundSensor.getMode()));
        return null;
    }

    @Override
    public Void visitSoundRecord(SoundRecord<Void> soundRecord) {
        checkSensorPort(soundRecord);
        usedHardwareBuilder.addUsedSensor(new UsedSensor(soundRecord.getUserDefinedPort(), CyberpiConstants.RECORD, soundRecord.mode));
        return null;
    }

    @Override
    public Void visitPlayRecordingAction(PlayRecordingAction<Void> playRecordingAction) {
        usedHardwareBuilder.addUsedActor(new UsedActor(playRecordingAction.getUserDefinedPort(), CyberpiConstants.RECORD));
        return null;
    }

    @Override
    public Void visitLightSensor(LightSensor<Void> lightSensor) {
        checkSensorPort(lightSensor);
        usedHardwareBuilder.addUsedSensor(new UsedSensor(lightSensor.getUserDefinedPort(), SC.LIGHT, lightSensor.getMode()));
        return null;
    }

    @Override
    public Void visitGyroSensor(GyroSensor<Void> gyroSensor) {
        checkSensorPort(gyroSensor);
        usedHardwareBuilder.addUsedSensor(new UsedSensor(gyroSensor.getUserDefinedPort(), SC.GYRO, gyroSensor.getMode()));
        return null;
    }

    @Override
    public Void visitAccelerometerSensor(AccelerometerSensor<Void> accelerometerSensor) {
        checkSensorPort(accelerometerSensor);
        usedHardwareBuilder.addUsedSensor(new UsedSensor(accelerometerSensor.getUserDefinedPort(), SC.ACCELEROMETER, accelerometerSensor.getMode()));
        return null;
    }

    @Override
    public Void visitDisplaySetColourAction(DisplaySetColourAction<Void> displaySetColourAction) {
        usedHardwareBuilder.addUsedActor(new UsedActor(displaySetColourAction.getUserDefinedPort(), SC.DISPLAY));
        requiredComponentVisited(displaySetColourAction, displaySetColourAction.getColor());
        return null;
    }

    @Override
    public Void visitUltrasonicSensor(UltrasonicSensor<Void> ultrasonicSensor) {
        checkSensorPort(ultrasonicSensor);
        usedHardwareBuilder.addUsedActor(new UsedActor(ultrasonicSensor.getUserDefinedPort(), SC.ULTRASONIC));
        return null;
    }

    @Override
    public Void visitUltrasonic2LEDAction(Ultrasonic2LEDAction<Void> ultrasonic2LEDAction) {
        checkSensorPort(ultrasonic2LEDAction);
        requiredComponentVisited(ultrasonic2LEDAction, ultrasonic2LEDAction.brightness);
        usedHardwareBuilder.addUsedActor(new UsedActor(ultrasonic2LEDAction.getUserDefinedPort(), SC.ULTRASONIC));
        return null;
    }

    @Override
    public Void visitQuadRGBSensor(QuadRGBSensor<Void> quadRGBSensor) {
        checkSensorPort(quadRGBSensor);
        usedHardwareBuilder.addUsedSensor(new UsedSensor(quadRGBSensor.getUserDefinedPort(), CyberpiConstants.QUADRGBSENSOR, quadRGBSensor.mode));
        return null;
    }

    @Override
    public Void visitQuadRGBLightOnAction(QuadRGBLightOnAction<Void> quadRGBLightOnAction) {
        checkSensorPort(quadRGBLightOnAction);
        usedHardwareBuilder.addUsedActor(new UsedActor(quadRGBLightOnAction.getUserDefinedPort(), CyberpiConstants.QUADRGBSENSOR));
        usedMethodBuilder.addUsedMethod(Mbot2Methods.RGBASSTRING);
        requiredComponentVisited(quadRGBLightOnAction, quadRGBLightOnAction.getColor());
        return null;
    }

    @Override
    public Void visitQuadRGBLightOffAction(QuadRGBLightOffAction<Void> quadRGBLightOffAction) {
        checkSensorPort(quadRGBLightOffAction);
        usedHardwareBuilder.addUsedActor(new UsedActor(quadRGBLightOffAction.getUserDefinedPort(), CyberpiConstants.QUADRGBSENSOR));
        return null;
    }

    @Override
    public Void visitLedOnActionWithIndex(LedOnActionWithIndex<Void> ledOnActionWithIndex) {
        requiredComponentVisited(ledOnActionWithIndex, ledOnActionWithIndex.getColor());
        usedHardwareBuilder.addUsedActor(new UsedActor(ledOnActionWithIndex.getUserDefinedPort(), SC.RGBLED));
        return null;
    }

    @Override
    public Void visitLedsOffAction(LedsOffAction<Void> ledsOffAction) {
        usedHardwareBuilder.addUsedActor(new UsedActor(ledsOffAction.getUserDefinedPort(), SC.RGBLED));
        return null;
    }

    @Override
    public Void visitLedBrightnessAction(LedBrightnessAction<Void> ledBrightnessAction) {
        requiredComponentVisited(ledBrightnessAction, ledBrightnessAction.getBrightness());
        usedHardwareBuilder.addUsedActor(new UsedActor(ledBrightnessAction.getUserDefinedPort(), SC.RGBLED));
        return null;
    }

    @Override
    public Void visitPrintlnAction(PrintlnAction<Void> printlnAction) {
        requiredComponentVisited(printlnAction, printlnAction.msg);
        usedHardwareBuilder.addUsedActor(new UsedActor(printlnAction.getUserDefinedPort(), SC.DISPLAY));
        return null;
    }

    @Override
    public Void visitShowTextAction(ShowTextAction<Void> showTextAction) {
        requiredComponentVisited(showTextAction, showTextAction.msg);
        requiredComponentVisited(showTextAction, showTextAction.x);
        requiredComponentVisited(showTextAction, showTextAction.y);
        usedHardwareBuilder.addUsedActor(new UsedActor(showTextAction.port, SC.DISPLAY));
        return null;
    }

    @Override
    public Void visitToneAction(ToneAction<Void> toneAction) {
        requiredComponentVisited(toneAction, toneAction.getDuration(), toneAction.getFrequency());
        usedHardwareBuilder.addUsedActor(new UsedActor("", SC.MUSIC));
        if ( toneAction.getDuration().getKind().hasName("NUM_CONST") ) {
            double toneActionConst = Double.parseDouble(((NumConst<Void>) toneAction.getDuration()).getValue());
            if ( toneActionConst <= 0 ) {
                addWarningToPhrase(toneAction, "BLOCK_NOT_EXECUTED");
            }
        }
        return null;
    }

    @Override
    public Void visitPlayNoteAction(PlayNoteAction<Void> playNoteAction) {
        usedHardwareBuilder.addUsedActor(new UsedActor("", SC.MUSIC));
        return null;
    }

    @Override
    public Void visitVolumeAction(VolumeAction<Void> volumeAction) {
        if ( volumeAction.getMode() == VolumeAction.Mode.SET ) {
            requiredComponentVisited(volumeAction, volumeAction.getVolume());
        }
        usedHardwareBuilder.addUsedActor(new UsedActor(BlocklyConstants.EMPTY_PORT, SC.SOUND));
        return null;
    }

    @Override
    public Void visitTimerSensor(TimerSensor<Void> timerSensor) {
        usedHardwareBuilder.addUsedSensor(new UsedSensor(timerSensor.getUserDefinedPort(), SC.TIMER, timerSensor.getMode()));
        return null;
    }

    @Override
    public Void visitPlayFileAction(PlayFileAction<Void> playFileAction) {
        addWarningToPhrase(playFileAction, "BLOCK_NOT_SUPPORTED");
        return null;
    }

    @Override
    public Void visitMotorGetPowerAction(MotorGetPowerAction<Void> motorGetPowerAction) {
        addWarningToPhrase(motorGetPowerAction, "BLOCK_NOT_SUPPORTED");
        return null;
    }

    @Override
    public Void visitMotorSetPowerAction(MotorSetPowerAction<Void> motorSetPowerAction) {
        addWarningToPhrase(motorSetPowerAction, "BLOCK_NOT_SUPPORTED");
        return null;
    }


    @Override
    public Void visitTurnAction(TurnAction<Void> turnAction) {
        return null;
    }

    @Override
    public Void visitMotorDriveStopAction(MotorDriveStopAction<Void> stopAction) {
        return null;
    }

    @Override
    public Void visitMotorOnAction(MotorOnAction<Void> motorOnAction) {
        requiredComponentVisited(motorOnAction, motorOnAction.getParam().getSpeed());
        MotorDuration<Void> duration = motorOnAction.getParam().getDuration();
        if ( duration != null ) {
            checkForZeroSpeed(motorOnAction, motorOnAction.getParam().getSpeed());
            requiredComponentVisited(motorOnAction, duration.getValue());
        }
        checkMotorPortAndAddUsedActor(motorOnAction);
        return null;
    }

    @Override
    public Void visitMotorStopAction(MotorStopAction<Void> motorStopAction) {
        checkMotorPortAndAddUsedActor(motorStopAction);
        return null;
    }

    @Override
    public Void visitDriveAction(DriveAction<Void> driveAction) {
        if ( driveAction.getParam().getDuration() != null ) {
            usedMethodBuilder.addUsedMethod(Mbot2Methods.DIFFDRIVEFOR);
        }
        checkDifferentialDriveBlock(driveAction);
        checkAndVisitMotionParam(driveAction, driveAction.getParam());
        return null;
    }

    @Override
    public Void visitCurveAction(CurveAction<Void> curveAction) {
        if ( curveAction.getParamLeft().getDuration() != null ) {
            usedMethodBuilder.addUsedMethod(Mbot2Methods.DIFFDRIVEFOR);
        }
        checkDifferentialDriveBlock(curveAction);
        checkAndVisitMotionParam(curveAction, curveAction.getParamLeft());
        checkAndVisitMotionParam(curveAction, curveAction.getParamRight());
        return null;
    }

    private void checkDifferentialDriveBlock(Action<Void> motionAction) {
        if ( hasDifferentialDriveCheck(motionAction) ) {
            hasEncodersOnDifferentialDriveCheck(motionAction);
            addDifferentialDriveToUsedHardware();
        }
    }

    private boolean hasDifferentialDriveCheck(Action<Void> motionAction) {
        ConfigurationComponent differentialDrive = getDifferentialDrive();
        if ( differentialDrive == null ) {
            addErrorToPhrase(motionAction, "CONFIGURATION_ERROR_ACTOR_MISSING");
            return false;
        } else if ( differentialDrive.getOptProperty("MOTOR_L").equals(differentialDrive.getOptProperty("MOTOR_R")) ) {
            addErrorToPhrase(motionAction, "CONFIGURATION_ERROR_OVERLAPPING_PORTS");
            return false;
        }
        return true;
    }

    private ConfigurationComponent getDifferentialDrive() {
        Map<String, ConfigurationComponent> configComponents = this.robotConfiguration.getConfigurationComponents();
        for ( ConfigurationComponent component : configComponents.values() ) {
            if ( component.getComponentType().equals(CyberpiConstants.DIFFERENTIALDRIVE) ) {
                return component;
            }
        }
        return null;
    }

    private void hasEncodersOnDifferentialDriveCheck(Action<Void> motionParam) {
        ConfigurationComponent differentialDrive = getDifferentialDrive();
        Assert.notNull(differentialDrive, "differentialDrive block is missing in the configuration");
        List<ConfigurationComponent> rightMotors = getEncodersOnPort(differentialDrive.getOptProperty("MOTOR_R"));
        List<ConfigurationComponent> leftMotors = getEncodersOnPort(differentialDrive.getOptProperty("MOTOR_L"));

        if ( rightMotors.size() > 1 ) {
            addErrorToPhrase(motionParam, "CONFIGURATION_ERROR_MULTIPLE_RIGHT_MOTORS");
        } else if ( leftMotors.size() > 1 ) {
            addErrorToPhrase(motionParam, "CONFIGURATION_ERROR_MULTIPLE_LEFT_MOTORS");
        } else if ( rightMotors.isEmpty() ) {
            addErrorToPhrase(motionParam, "CONFIGURATION_ERROR_MOTOR_RIGHT_MISSING");
        } else if ( leftMotors.isEmpty() ) {
            addErrorToPhrase(motionParam, "CONFIGURATION_ERROR_MOTOR_LEFT_MISSING");
        }
    }

    private void addDifferentialDriveToUsedHardware() {
        ConfigurationComponent diffDrive = getDifferentialDrive();
        Assert.notNull(diffDrive, "differential missing in Configuration");

        usedHardwareBuilder.addUsedActor(new UsedActor(diffDrive.getUserDefinedPortName(), CyberpiConstants.DIFFERENTIALDRIVE));
        List<ConfigurationComponent> motorsR = getEncodersOnPort(diffDrive.getOptProperty("MOTOR_R"));
        List<ConfigurationComponent> motorsL = getEncodersOnPort(diffDrive.getOptProperty("MOTOR_L"));
        if ( !motorsL.isEmpty() ) {
            usedHardwareBuilder.addUsedActor(new UsedActor(motorsL.get(0).getUserDefinedPortName(), motorsL.get(0).getComponentType()));
        }
        if ( !motorsR.isEmpty() ) {
            usedHardwareBuilder.addUsedActor(new UsedActor(motorsR.get(0).getUserDefinedPortName(), motorsR.get(0).getComponentType()));
        }
    }

    private List<ConfigurationComponent> getEncodersOnPort(String port) {
        Map<String, ConfigurationComponent> configComponents = this.robotConfiguration.getConfigurationComponents();
        List<ConfigurationComponent> encoders = new ArrayList<>();
        for ( ConfigurationComponent component : configComponents.values() ) {
            if ( component.getComponentType().equals(SC.ENCODER) || component.getComponentType().equals("MOTOR") || component.getComponentType().equals(SC.STEPMOTOR) ) {
                if ( component.getComponentProperties().containsValue(port) ) {
                    encoders.add(component);
                }
            }
        }
        return encoders;
    }

    private void checkAndVisitMotionParam(Action<Void> action, MotionParam<Void> param) {
        MotorDuration<Void> duration = param.getDuration();
        Expr<Void> speed = param.getSpeed();

        requiredComponentVisited(action, speed);

        if ( duration != null ) {
            requiredComponentVisited(action, duration.getValue());
            checkForZeroSpeed(action, speed);
        }
    }


    private void checkForZeroSpeed(Action<Void> action, Expr<Void> speed) {
        if ( speed.getKind().hasName("NUM_CONST") ) {
            if ( Math.abs(Double.parseDouble(((NumConst<Void>) speed).getValue())) < 1E-7 ) {
                addWarningToPhrase(action, "MOTOR_SPEED_0");
            }
        }
    }

    private void checkMotorPortAndAddUsedActor(MoveAction<Void> moveAction) {
        ConfigurationComponent actor = this.robotConfiguration.optConfigurationComponent(moveAction.getUserDefinedPort());
        if ( actor == null ) {
            addErrorToPhrase(moveAction, "CONFIGURATION_ERROR_MOTOR_MISSING");
            return;
        }

        usedHardwareBuilder.addUsedActor(new UsedActor(moveAction.getUserDefinedPort(), actor.getComponentType()));
    }

    private void checkSensorPort(WithUserDefinedPort<Void> sensor) {
        Assert.isTrue(sensor instanceof Phrase, "checking Port of a non Phrase");
        Phrase<Void> sensorAsSensor = (Phrase<Void>) sensor;

        String userDefinedPort = sensor.getUserDefinedPort();
        ConfigurationComponent configurationComponent = this.robotConfiguration.optConfigurationComponent(userDefinedPort);
        if ( configurationComponent == null ) {
            configurationComponent = getSubComponent(userDefinedPort);
            if ( configurationComponent == null ) {
                addErrorToPhrase(sensorAsSensor, "CONFIGURATION_ERROR_SENSOR_MISSING");
                return;
            }
        }
        checkSensorType(sensorAsSensor, configurationComponent);
    }

    private void checkSensorType(Phrase<Void> sensor, ConfigurationComponent configurationComponent) {
        String expectedComponentType = SENSOR_COMPONENT_TYPE_MAP.get(sensor.getKind().getName());
        String typeWithoutSensing = sensor.getKind().getName().replace("_SENSING", "");
        if ( !(typeWithoutSensing.equalsIgnoreCase(configurationComponent.getComponentType())) ) {
            if ( expectedComponentType != null && !expectedComponentType.equalsIgnoreCase(configurationComponent.getComponentType()) ) {
                addErrorToPhrase(sensor, "CONFIGURATION_ERROR_SENSOR_WRONG");
            }
        }
    }

    private ConfigurationComponent getSubComponent(String userDefinedPort) {
        for ( ConfigurationComponent component : this.robotConfiguration.getConfigurationComponentsValues() ) {
            try {
                for ( List<ConfigurationComponent> subComponents : component.getSubComponents().values() ) {
                    for ( ConfigurationComponent subComponent : subComponents ) {
                        if ( subComponent.getUserDefinedPortName().equals(userDefinedPort) ) {
                            return subComponent;
                        }
                    }
                }
            } catch ( UnsupportedOperationException e ) {
                continue;
            }
        }
        return null;
    }

}
