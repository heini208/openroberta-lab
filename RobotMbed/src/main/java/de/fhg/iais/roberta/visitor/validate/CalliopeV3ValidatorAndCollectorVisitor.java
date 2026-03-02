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
import de.fhg.iais.roberta.syntax.sensor.mbed.RunCircuitSim;
import de.fhg.iais.roberta.syntax.sensor.mbed.RunCircuitIBM;
import de.fhg.iais.roberta.syntax.sensor.mbed.GetJobResultSim;
import de.fhg.iais.roberta.syntax.sensor.mbed.CreateCircuit;
import de.fhg.iais.roberta.syntax.sensor.mbed.CloneCircuit;
import de.fhg.iais.roberta.syntax.sensor.mbed.MeasureQubit;
import de.fhg.iais.roberta.syntax.sensor.mbed.MeasureAllQubits;

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
        usedMethodBuilder.addUsedMethod(CalliopeMethods.GET_CLEAN_RESPONSE);
        usedMethodBuilder.addUsedMethod(CalliopeMethods.START_REAL_QBIT_JOB);

        usedHardwareBuilder.addUsedActor(new UsedActor(SC.WIFI, SC.WIFI));
        usedMethodBuilder.addUsedMethod(CalliopeMethods.SETUP_IBM);
        usedHardwareBuilder.addUsedActor(new UsedActor(SC.IBM, SC.IBM));

        usedHardwareBuilder.addUsedActor(new UsedActor(ibmjob.getUserDefinedPort(), SC.QISKIT));
        return null;
    }

    @Override
    public Void visitIBMJobResult(IBMJobResult ibmJobResult) {
        usedMethodBuilder.addUsedMethod(CalliopeMethods.SETUP_WIFI);
        usedMethodBuilder.addUsedMethod(CalliopeMethods.PARSE_LIST_RESPONSE);
        usedMethodBuilder.addUsedMethod(CalliopeMethods.SETUP_IBM);
        usedMethodBuilder.addUsedMethod(CalliopeMethods.IBM_GET_JOB_RESULT);

        usedHardwareBuilder.addUsedActor(new UsedActor(SC.IBM, SC.IBM));

        usedMethodBuilder.addUsedMethod(CalliopeMethods.SEND_AND_WAIT);
        usedHardwareBuilder.addUsedActor(new UsedActor(SC.WIFI, SC.WIFI));
        usedHardwareBuilder.addUsedActor(new UsedActor(ibmJobResult.getUserDefinedPort(), SC.QISKIT));
        return null;
    }

    @Override
    public Void visitIBMJobStatus(IBMJobStatus ibmJobStatus) {
        usedMethodBuilder.addUsedMethod(CalliopeMethods.SETUP_WIFI);
        usedMethodBuilder.addUsedMethod(CalliopeMethods.SEND_AND_WAIT);
        usedMethodBuilder.addUsedMethod(CalliopeMethods.SETUP_IBM);
        usedMethodBuilder.addUsedMethod(CalliopeMethods.GET_CLEAN_RESPONSE);
        usedMethodBuilder.addUsedMethod(CalliopeMethods.IBM_GET_JOB_STATUS);
        usedHardwareBuilder.addUsedActor(new UsedActor(SC.IBM, SC.IBM));

        usedHardwareBuilder.addUsedActor(new UsedActor(SC.WIFI, SC.WIFI));
        usedHardwareBuilder.addUsedActor(new UsedActor(ibmJobStatus.getUserDefinedPort(), SC.QISKIT));
        return null;
    }

    @Override
    public Void visitRunCircuitSim(RunCircuitSim runCircuitSim) {
        return null;
    }

    @Override
    public Void visitRunCircuitIBM(RunCircuitIBM runCircuitIBM) {
        return null;
    }

    @Override
    public Void visitGetJobResultSim(GetJobResultSim getJobResultSim) {
        return null;
    }

    @Override
    public Void visitCreateCircuit(CreateCircuit createCircuit) {
        return null;
    }

    @Override
    public Void visitCloneCircuit(CloneCircuit cloneCircuit) {
        return null;
    }

    @Override
    public Void visitMeasureQubit(MeasureQubit measure) {
        return null;
    }

    @Override
    public Void visitMeasureAllQubits(MeasureAllQubits measureAll) {
        return null;
    }

    @Override
    public Void visitDeleteCircuit(DeleteCircuit deleteCircuit) {
        return null;
    }

    @Override
    public Void visitResetCircuit(ResetCircuit resetCircuit) {
        return null;
    }

    @Override
    public Void visitXGate(XGate xGate) {
        return null;
    }

    @Override
    public Void visitHGate(HGate hGate) {
        return null;
    }

    @Override
    public Void visitZGate(ZGate zGate) {
        return null;
    }

    @Override
    public Void visitYGate(YGate yGate) {
        return null;
    }

    @Override
    public Void visitRXGate(RXGate rxGate) {
        return null;
    }

    @Override
    public Void visitRYGate(RYGate ryGate) {
        return null;
    }

    @Override
    public Void visitRZGate(RZGate rzGate) {
        return null;
    }

    @Override
    public Void visitCXGate(CXGate cxGate) {
        return null;
    }

    @Override
    public Void visitCZGate(CZGate czGate) {
        return null;
    }

    @Override
    public Void visitSwapGate(SwapGate swapGate) {
        return null;
    }
}
