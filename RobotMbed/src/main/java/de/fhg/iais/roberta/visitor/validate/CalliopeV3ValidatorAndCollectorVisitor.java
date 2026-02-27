package de.fhg.iais.roberta.visitor.validate;

import com.google.common.collect.ClassToInstanceMap;

import de.fhg.iais.roberta.bean.IProjectBean;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.components.UsedActor;
import de.fhg.iais.roberta.syntax.action.mbed.RadioReceiveAction;
import de.fhg.iais.roberta.syntax.sensor.generic.GyroSensor;
import de.fhg.iais.roberta.syntax.sensor.mbed.SimulationJob;
import de.fhg.iais.roberta.syntax.sensor.mbed.IBMJob;
import de.fhg.iais.roberta.syntax.sensor.mbed.IBMJobResult;
import de.fhg.iais.roberta.syntax.sensor.mbed.IBMJobStatus;
import de.fhg.iais.roberta.util.syntax.SC;
import de.fhg.iais.roberta.visitor.CalliopeMethods;
import de.fhg.iais.roberta.visitor.ICalliopeVisitor;

public class CalliopeV3ValidatorAndCollectorVisitor extends CalliopeCommonValidatorAndCollectorVisitor implements ICalliopeVisitor<Void> {
    private final boolean hasBlueTooth;
    protected final boolean isSim;

    public CalliopeV3ValidatorAndCollectorVisitor(
        ConfigurationAst brickConfiguration,
        ClassToInstanceMap<IProjectBean.IBuilder> beanBuilders,
        boolean isSim,
        boolean displaySwitchUsed,
        boolean hasBlueTooth) //
    {
        super(brickConfiguration, beanBuilders, isSim, displaySwitchUsed, hasBlueTooth);
        this.isSim = isSim;
        this.hasBlueTooth = hasBlueTooth;
    }

    @Override
    public Void visitGyroSensor(GyroSensor gyroSensor) {
        usedMethodBuilder.addUsedMethod(CalliopeMethods.GET_ROTATION);
        return null;
    }

    @Override
    public Void visitRadioReceiveAction(RadioReceiveAction radioReceiveAction) {
        if ( hasBlueTooth ) {
            addErrorToPhrase(radioReceiveAction, "BLOCK_NOT_SUPPORTED");
        } else {
            addToPhraseIfUnsupportedInSim(radioReceiveAction, true, isSim);
            usedHardwareBuilder.addUsedActor(new UsedActor("", SC.RADIO));
            usedMethodBuilder.addUsedMethod(CalliopeMethods.RECEIVE_MESSAGE);
        }
        return super.visitRadioReceiveAction(radioReceiveAction);
    }

    @Override
    public Void visitSimulationJob(SimulationJob simulationJob) {
        usedMethodBuilder.addUsedMethod(CalliopeMethods.SETUP_WIFI);
        usedMethodBuilder.addUsedMethod(CalliopeMethods.PARSE_LIST_RESPONSE);
        usedMethodBuilder.addUsedMethod(CalliopeMethods.SEND_AND_WAIT);
        usedMethodBuilder.addUsedMethod(CalliopeMethods.SIMULATE_QBIT_MEASURE);
        usedHardwareBuilder.addUsedActor(new UsedActor(SC.WIFI, SC.WIFI));
        usedHardwareBuilder.addUsedActor(new UsedActor(SC.QISKIT, SC.QISKIT));
        return null;
    }

    @Override
    public Void visitIBMJob(IBMJob ibmjob) {
        usedMethodBuilder.addUsedMethod(CalliopeMethods.SETUP_WIFI);
        usedMethodBuilder.addUsedMethod(CalliopeMethods.SEND_AND_WAIT);
        usedHardwareBuilder.addUsedActor(new UsedActor(SC.WIFI, SC.WIFI));
        usedHardwareBuilder.addUsedActor(new UsedActor(ibmjob.getUserDefinedPort(), SC.QISKIT));
        return null;
    }

    @Override
    public Void visitIBMJobResult(IBMJobResult ibmJobResult) {
        usedMethodBuilder.addUsedMethod(CalliopeMethods.SETUP_WIFI);
        usedMethodBuilder.addUsedMethod(CalliopeMethods.PARSE_LIST_RESPONSE);

        usedMethodBuilder.addUsedMethod(CalliopeMethods.SEND_AND_WAIT);
        usedHardwareBuilder.addUsedActor(new UsedActor(SC.WIFI, SC.WIFI));
        usedHardwareBuilder.addUsedActor(new UsedActor(ibmJobResult.getUserDefinedPort(), SC.QISKIT));
        return null;
    }

    @Override
    public Void visitIBMJobStatus(IBMJobStatus ibmJobStatus) {
        usedMethodBuilder.addUsedMethod(CalliopeMethods.SETUP_WIFI);
        usedMethodBuilder.addUsedMethod(CalliopeMethods.SEND_AND_WAIT);
        usedHardwareBuilder.addUsedActor(new UsedActor(SC.WIFI, SC.WIFI));
        usedHardwareBuilder.addUsedActor(new UsedActor(ibmJobStatus.getUserDefinedPort(), SC.QISKIT));
        return null;
    }
}
