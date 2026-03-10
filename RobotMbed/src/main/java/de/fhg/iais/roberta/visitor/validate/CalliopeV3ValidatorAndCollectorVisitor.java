package de.fhg.iais.roberta.visitor.validate;

import com.google.common.collect.ClassToInstanceMap;

import de.fhg.iais.roberta.bean.IProjectBean;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.components.UsedActor;
import de.fhg.iais.roberta.syntax.action.mbed.RadioReceiveAction;
import de.fhg.iais.roberta.syntax.sensor.generic.GyroSensor;
import de.fhg.iais.roberta.syntax.sensor.mbed.*;
import de.fhg.iais.roberta.syntax.sensor.mbed.GetJobResultSample;

import de.fhg.iais.roberta.syntax.action.mbed.calliopeV3.DeleteCircuit;
import de.fhg.iais.roberta.syntax.action.mbed.calliopeV3.ResetCircuit;
import de.fhg.iais.roberta.syntax.action.mbed.calliopeV3.XGate;
import de.fhg.iais.roberta.syntax.action.mbed.calliopeV3.HGate;
import de.fhg.iais.roberta.syntax.action.mbed.calliopeV3.ZGate;
import de.fhg.iais.roberta.syntax.action.mbed.calliopeV3.YGate;
import de.fhg.iais.roberta.syntax.action.mbed.calliopeV3.RXGate;
import de.fhg.iais.roberta.syntax.action.mbed.calliopeV3.RYGate;
import de.fhg.iais.roberta.syntax.action.mbed.calliopeV3.RZGate;
import de.fhg.iais.roberta.syntax.action.mbed.calliopeV3.CXGate;
import de.fhg.iais.roberta.syntax.action.mbed.calliopeV3.CZGate;
import de.fhg.iais.roberta.syntax.action.mbed.calliopeV3.SwapGate;
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
        if (hasBlueTooth) {
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
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_LIST);
        return null;
    }

    @Override
    public Void visitIBMJob(IBMJob ibmJob) {
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_STRING);
        usedMethodBuilder.addUsedMethod(CalliopeMethods.SETUP_IBM);
        usedHardwareBuilder.addUsedActor(new UsedActor(SC.IBM, SC.IBM));
        return null;
    }

    @Override
    public Void visitIBMJobStatus(IBMJobStatus ibmJobStatus) {
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_STRING);
        usedMethodBuilder.addUsedMethod(CalliopeMethods.SETUP_IBM);
        usedHardwareBuilder.addUsedActor(new UsedActor(SC.IBM, SC.IBM));
        usedHardwareBuilder.addUsedActor(new UsedActor(ibmJobStatus.getUserDefinedPort(), SC.QISKIT));
        return null;
    }

    @Override
    public Void visitRunCircuitSim(RunCircuitSim runCircuitSim) {
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_STRING);
        return null;
    }

    @Override
    public Void visitRunCircuitIBM(RunCircuitIBM runCircuitIBM) {
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.SETUP_IBM);
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_STRING);
        usedHardwareBuilder.addUsedActor(new UsedActor(SC.IBM, SC.IBM));
        return null;
    }

    @Override
    public Void visitGetJobResultSample(GetJobResultSample getJobResultSample) {
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.SETUP_IBM);
        usedHardwareBuilder.addUsedActor(new UsedActor(SC.IBM, SC.IBM));
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_LIST);
        return null;
    }

    @Override
    public Void visitGetJobResultStates(GetJobResultStates getJobResultStates) {
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.SETUP_IBM);
        usedHardwareBuilder.addUsedActor(new UsedActor(SC.IBM, SC.IBM));
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_LIST);
        return null;
    }

    @Override
    public Void visitGetJobResultCounts(GetJobResultCounts getJobResultCounts) {
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.SETUP_IBM);
        usedHardwareBuilder.addUsedActor(new UsedActor(SC.IBM, SC.IBM));
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_LIST);
        return null;
    }

    @Override
    public Void visitGetJobResultProbabilities(GetJobResultProbabilities getJobResultProbabilities) {
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.SETUP_IBM);
        usedHardwareBuilder.addUsedActor(new UsedActor(SC.IBM, SC.IBM));
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_LIST);
        return null;
    }

    private void setup_Wifi() {
        usedMethodBuilder.addUsedMethod(CalliopeMethods.SETUP_WIFI);
        usedMethodBuilder.addUsedMethod(CalliopeMethods.SEND_AND_WAIT);
        usedHardwareBuilder.addUsedActor(new UsedActor(SC.WIFI, SC.WIFI));
        usedHardwareBuilder.addUsedActor(new UsedActor(SC.QISKIT, SC.QISKIT));
    }

    @Override
    public Void visitCreateCircuit(CreateCircuit createCircuit) {
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_STRING);
        return null;
    }

    @Override
    public Void visitCloneCircuit(CloneCircuit cloneCircuit) {
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_STRING);
        return null;
    }

    @Override
    public Void visitMeasureQubit(MeasureQubit measure) {
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_OK);
        return null;
    }

    @Override
    public Void visitMeasureAllQubits(MeasureAllQubits measureAll) {
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_OK);
        return null;
    }

    @Override
    public Void visitDeleteCircuit(DeleteCircuit deleteCircuit) {
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_OK);
        return null;
    }

    @Override
    public Void visitResetCircuit(ResetCircuit resetCircuit) {
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_OK);
        return null;
    }

    // Gates
    @Override
    public Void visitXGate(XGate xGate) {
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_OK);
        return null;
    }

    @Override
    public Void visitHGate(HGate hGate) {
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_OK);
        return null;
    }

    @Override
    public Void visitZGate(ZGate zGate) {
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_OK);
        return null;
    }

    @Override
    public Void visitYGate(YGate yGate) {
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_OK);
        return null;
    }

    @Override
    public Void visitRXGate(RXGate rxGate) {
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_OK);
        return null;
    }

    @Override
    public Void visitRYGate(RYGate ryGate) {
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_OK);
        return null;
    }

    @Override
    public Void visitRZGate(RZGate rzGate) {
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_OK);
        return null;
    }

    @Override
    public Void visitCXGate(CXGate cxGate) {
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_OK);
        return null;
    }

    @Override
    public Void visitCZGate(CZGate czGate) {
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_OK);
        return null;
    }

    @Override
    public Void visitSwapGate(SwapGate swapGate) {
        setup_Wifi();
        usedMethodBuilder.addUsedMethod(CalliopeMethods.CMD_OK);
        return null;
    }
}
