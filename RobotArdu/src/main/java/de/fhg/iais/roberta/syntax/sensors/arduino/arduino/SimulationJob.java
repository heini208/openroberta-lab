package de.fhg.iais.roberta.syntax.sensors.arduino.arduino;

import de.fhg.iais.roberta.syntax.sensor.ExternalSensor;
import de.fhg.iais.roberta.transformer.forClass.NepoExpr;
import de.fhg.iais.roberta.util.ast.BlocklyProperties;
import de.fhg.iais.roberta.util.ast.ExternalSensorBean;

@NepoExpr(name = "QISKIT_SIMULATION_JOB", category = "SENSOR", blocklyNames = {"robSensors_qiskit_start_job_simulation"})
public final class SimulationJob extends ExternalSensor {

    public SimulationJob(BlocklyProperties properties, ExternalSensorBean externalSensorBean) {
        super(properties, externalSensorBean);
        setReadOnly();
    }

}
