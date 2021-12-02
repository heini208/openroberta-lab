package de.fhg.iais.roberta.visitor;

import java.util.Map;

import com.google.common.collect.ClassToInstanceMap;

import de.fhg.iais.roberta.bean.IProjectBean;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.components.ConfigurationComponent;
import de.fhg.iais.roberta.components.UsedActor;
import de.fhg.iais.roberta.components.UsedSensor;
import de.fhg.iais.roberta.constants.CyberpiConstants;
import de.fhg.iais.roberta.syntax.MotionParam;
import de.fhg.iais.roberta.syntax.MotorDuration;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.SC;
import de.fhg.iais.roberta.syntax.action.Action;
import de.fhg.iais.roberta.syntax.action.display.ClearDisplayAction;
import de.fhg.iais.roberta.syntax.action.mbot2.LedBrightnessAction;
import de.fhg.iais.roberta.syntax.action.mbot2.LedOnActionWithIndex;
import de.fhg.iais.roberta.syntax.action.mbot2.DisplaySetColourAction;
import de.fhg.iais.roberta.syntax.action.mbot2.LedsOffAction;
import de.fhg.iais.roberta.syntax.action.mbot2.PlayRecordingAction;
import de.fhg.iais.roberta.syntax.action.display.ShowTextAction;
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
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.syntax.lang.expr.NumConst;
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
import de.fhg.iais.roberta.visitor.validate.CommonNepoValidatorAndCollectorVisitor;
import de.fhg.iais.roberta.syntax.action.motor.differential.DriveAction;


public class Mbot2ValidatorAndCollectorVisitor extends CommonNepoValidatorAndCollectorVisitor implements IMbot2Visitor<Void> {

    public Mbot2ValidatorAndCollectorVisitor(ConfigurationAst robotConfiguration, ClassToInstanceMap<IProjectBean.IBuilder<?>> beanBuilders) {
        super(robotConfiguration, beanBuilders);
    }

    @Override
    public Void visitClearDisplayAction(ClearDisplayAction<Void> clearDisplayAction) {
        return null;
    }

    @Override
    public Void visitJoystick(Joystick<Void> joystick) {
        return null;
    }

    @Override
    public Void visitKeysSensor(KeysSensor<Void> keysSensor) {
        return null;
    }

    @Override
    public Void visitEncoderSensor(EncoderSensor<Void> encoderSensor) {
        return null;
    }

    @Override
    public Void visitSoundSensor(SoundSensor<Void> soundSensor) {
        return null;
    }

    @Override
    public Void visitSoundRecord(SoundRecord<Void> soundRecord) {
        return null;
    }

    @Override
    public Void visitLightSensor(LightSensor<Void> lightSensor) {
        return null;
    }

    @Override
    public Void visitGyroSensor(GyroSensor<Void> gyroSensor) {
        return null;
    }

    @Override
    public Void visitAccelerometerSensor(AccelerometerSensor<Void> accelerometerSensor) {
        return null;
    }

    @Override
    public Void visitPlayRecordingAction(PlayRecordingAction<Void> playRecordingAction) {
        return null;
    }

    @Override
    public Void visitDisplaySetColourAction(DisplaySetColourAction<Void> displaySetColourAction) {
        requiredComponentVisited(displaySetColourAction, displaySetColourAction.getColor());
        return null;
    }

    @Override
    public Void visitUltrasonicSensor(UltrasonicSensor<Void> ultrasonicSensor) {
        return null;
    }

    @Override
    public Void visitQuadRGBSensor(QuadRGBSensor<Void> quadRGBSensor) {
        usedHardwareBuilder.addUsedSensor(new UsedSensor(quadRGBSensor.getUserDefinedPort(), CyberpiConstants.QUADRGBSENSOR, quadRGBSensor.mode));
        return null;
    }

    @Override
    public Void visitQuadRGBLightOnAction(QuadRGBLightOnAction<Void> quadRGBLightOnAction) {
        usedHardwareBuilder.addUsedActor(new UsedActor(quadRGBLightOnAction.getUserDefinedPort(), CyberpiConstants.QUADRGBSENSOR));
        usedMethodBuilder.addUsedMethod(Mbot2Methods.RGBASSTRING);
        requiredComponentVisited(quadRGBLightOnAction, quadRGBLightOnAction.getColor());
        return null;
    }

    @Override
    public Void visitQuadRGBLightOffAction(QuadRGBLightOffAction<Void> quadRGBLightOffAction) {
        return null;
    }

    @Override
    public Void visitUltrasonic2LEDAction(Ultrasonic2LEDAction<Void> ultrasonic2LEDAction) {
        return null;
    }

    @Override
    public Void visitLedOnActionWithIndex(LedOnActionWithIndex<Void> ledOnActionWithIndex) {
        return null;
    }

    @Override
    public Void visitLedsOffAction(LedsOffAction<Void> ledsOffAction) {
        return null;
    }

    @Override
    public Void visitLedBrightnessAction(LedBrightnessAction<Void> ledBrightnessAction) {
        return null;
    }

    @Override
    public Void visitMotorGetPowerAction(MotorGetPowerAction<Void> motorGetPowerAction) {
        return null;
    }

    @Override
    public Void visitMotorOnAction(MotorOnAction<Void> motorOnAction) {
        return null;
    }

    @Override
    public Void visitMotorSetPowerAction(MotorSetPowerAction<Void> motorSetPowerAction) {
        return null;
    }

    @Override
    public Void visitMotorStopAction(MotorStopAction<Void> motorStopAction) {
        return null;
    }

    @Override
    public Void visitShowTextAction(ShowTextAction<Void> showTextAction) {
        requiredComponentVisited(showTextAction, showTextAction.msg);
        optionalComponentVisited(showTextAction.x);
        optionalComponentVisited(showTextAction.y);

        return null;
    }

    @Override
    public Void visitDriveAction(DriveAction<Void> driveAction) {
        if ( driveAction.getParam().getDuration() != null ) {
            usedMethodBuilder.addUsedMethod(Mbot2Methods.DIFFDRIVEFOR);
        }
        hasDifferentialDriveCheck(driveAction);
        hasEncodersOnDifferentialDriveCheck(driveAction);
        checkAndVisitMotionParam(driveAction, driveAction.getParam());
        return null;
    }

    private void hasDifferentialDriveCheck(Phrase<Void> driveAction) {
        ConfigurationComponent differentialDrive = getDifferentialDrive();
        if ( differentialDrive == null || differentialDrive.getOptProperty("MOTOR_L").equals(differentialDrive.getOptProperty("MOTOR_R")) ) {
            //error has no differentialdrive
            addErrorToPhrase(driveAction, "");
        }
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

    private void hasEncodersOnDifferentialDriveCheck(Phrase<Void> driveAction) {
        Map<String, ConfigurationComponent> configComponents = this.robotConfiguration.getConfigurationComponents();
        ConfigurationComponent differentialDrive = getDifferentialDrive();
        if ( differentialDrive == null ) {
            //error has no differentialdrive
            addErrorToPhrase(driveAction, "");
            return;
        }
        int numLeftMotors = 0;
        int numRightMotors = 0;
        for ( ConfigurationComponent component : configComponents.values() ) {
            if ( component.getComponentType().equals("ENCODER") ) {
                if ( (component.getOptProperty("PORT1").equals(differentialDrive.getOptProperty("MOTOR_R"))) ) {
                    numRightMotors++;
                } else if ( component.getOptProperty("PORT1").equals(differentialDrive.getOptProperty("MOTOR_L")) ) {
                    numLeftMotors++;
                }
            }
            if ( numRightMotors > 1 || numLeftMotors > 1 ) {
                addErrorToPhrase(driveAction, "");
                return;
            }
        }
        if ( numRightMotors != 1 || numLeftMotors != 1 ) {
            addErrorToPhrase(driveAction, "CONFIGURATION_ERROR_MOTOR_MISSING");
        }
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

    @Override
    public Void visitCurveAction(CurveAction<Void> curveAction) {
        if ( curveAction.getParamLeft().getDuration() != null ) {
            usedMethodBuilder.addUsedMethod(Mbot2Methods.DIFFDRIVEFOR);
        }
        return null;
    }

    @Override
    public Void visitToneAction(ToneAction<Void> toneAction) {
        return null;
    }

    @Override
    public Void visitPlayNoteAction(PlayNoteAction<Void> playNoteAction) {
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
    public Void visitTimerSensor(TimerSensor<Void> timerSensor) {
        usedHardwareBuilder.addUsedSensor(new UsedSensor(timerSensor.getUserDefinedPort(), SC.TIMER, timerSensor.getMode()));
        return null;
    }

    @Override
    public Void visitVolumeAction(VolumeAction<Void> volumeAction) {
        return null;
    }

    @Override
    public Void visitPlayFileAction(PlayFileAction<Void> playFileAction) {
        return null;
    }
}
