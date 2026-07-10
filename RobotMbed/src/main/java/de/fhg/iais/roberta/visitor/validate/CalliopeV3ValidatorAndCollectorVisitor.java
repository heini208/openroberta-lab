package de.fhg.iais.roberta.visitor.validate;

import com.google.common.collect.ClassToInstanceMap;

import de.fhg.iais.roberta.bean.IProjectBean;
import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.components.UsedActor;
import de.fhg.iais.roberta.syntax.action.mbed.RadioReceiveAction;
import de.fhg.iais.roberta.syntax.action.mbed.calliopeV3.*;
import de.fhg.iais.roberta.syntax.sensor.generic.GyroSensor;
import de.fhg.iais.roberta.syntax.sensor.mbed.*;
import de.fhg.iais.roberta.syntax.sensor.mbed.GetJobResultSample;

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
        use_qiskit();
        return null;
    }

    @Override
    public Void visitRunCircuitSim(RunCircuitSim runCircuitSim) {
        use_qiskit();
        return null;
    }

    @Override
    public Void visitGetJobResultSample(GetJobResultSample getJobResultSample) {
        use_qiskit();
        return null;
    }

    @Override
    public Void visitGetJobResultStates(GetJobResultStates getJobResultStates) {
        use_qiskit();
        return null;
    }

    @Override
    public Void visitGetJobResultCounts(GetJobResultCounts getJobResultCounts) {
        use_qiskit();
        return null;
    }

    @Override
    public Void visitGetJobResultProbabilities(GetJobResultProbabilities getJobResultProbabilities) {
        use_qiskit();
        return null;
    }

    private void use_qiskit() {
        usedHardwareBuilder.addUsedActor(new UsedActor(SC.QISKIT, SC.QISKIT));
    }

    @Override
    public Void visitCreateCircuit(CreateCircuit createCircuit) {
        use_qiskit();
        return null;
    }

    @Override
    public Void visitCloneCircuit(CloneCircuit cloneCircuit) {
        use_qiskit();
        return null;
    }

    @Override
    public Void visitMeasureQubit(MeasureQubit measure) {
        use_qiskit();
        return null;
    }

    @Override
    public Void visitMeasureAllQubits(MeasureAllQubits measureAll) {
        use_qiskit();
        return null;
    }

    @Override
    public Void visitDeleteCircuit(DeleteCircuit deleteCircuit) {
        use_qiskit();
        return null;
    }

    @Override
    public Void visitResetCircuit(ResetCircuit resetCircuit) {
        use_qiskit();
        return null;
    }

    // Gates
    @Override
    public Void visitSingleQubitGate(SingleQubitGate singleQubitGate) {
        use_qiskit();
        return null;
    }

    @Override
    public Void visitRotationGate(RotationGate rotationGate) {
        use_qiskit();
        return null;
    }

    @Override
    public Void visitTwoQubitGate(TwoQubitGate twoQubitGate) {
        use_qiskit();
        return null;
    }

}
