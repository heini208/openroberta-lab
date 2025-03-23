package de.fhg.iais.roberta.syntax.sensors.arduino.arduino;

import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.syntax.sensor.ExternalSensor;
import de.fhg.iais.roberta.syntax.sensor.Sensor;
import de.fhg.iais.roberta.transformer.forClass.NepoExpr;
import de.fhg.iais.roberta.transformer.forField.NepoValue;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.ast.BlocklyProperties;
import de.fhg.iais.roberta.util.ast.ExternalSensorBean;

@NepoExpr(name = "QISKIT_SIMULATION_JOB", category = "SENSOR", blocklyNames = {"robSensors_qiskit_start_job_simulation"})
public final class SimulationJob extends Sensor {
    @NepoValue(name = "QBITS", type = BlocklyType.NUMBER)
    public final Expr qbits;

    public SimulationJob(BlocklyProperties properties, Expr qbits) {
        super(properties);
        this.qbits = qbits;
        setReadOnly();
    }

}
