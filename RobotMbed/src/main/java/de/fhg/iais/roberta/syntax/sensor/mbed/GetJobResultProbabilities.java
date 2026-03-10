package de.fhg.iais.roberta.syntax.sensor.mbed;

import de.fhg.iais.roberta.syntax.sensor.Sensor;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.transformer.forClass.NepoExpr;
import de.fhg.iais.roberta.transformer.forField.NepoValue;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.ast.BlocklyProperties;

@NepoExpr(
        name = "QISKIT_JOB_RESULT_PROBABILITIES",
        category = "SENSOR",
        blocklyNames = {"robSensors_qiskit_job_result_probabilities"}
)
public final class GetJobResultProbabilities extends Sensor {

    @NepoValue(name = "JOB_ID", type = BlocklyType.STRING)
    public final Expr jobId;

    public GetJobResultProbabilities(BlocklyProperties properties, Expr jobId) {
        super(properties);
        this.jobId = jobId;
        setReadOnly();
    }
}