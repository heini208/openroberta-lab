package de.fhg.iais.roberta.syntax.sensors.arduino.arduino;

import de.fhg.iais.roberta.syntax.sensor.ExternalSensor;
import de.fhg.iais.roberta.transformer.forClass.NepoExpr;
import de.fhg.iais.roberta.util.ast.BlocklyProperties;
import de.fhg.iais.roberta.util.ast.ExternalSensorBean;

@NepoExpr(name = "QISKIT_IBM_JOB_RESULT", category = "SENSOR", blocklyNames = {"robSensors_qiskit_job_result"})
public final class IBMJobResult extends ExternalSensor {

    public IBMJobResult(BlocklyProperties properties, ExternalSensorBean externalSensorBean) {
        super(properties, externalSensorBean);
        setReadOnly();
    }

}
