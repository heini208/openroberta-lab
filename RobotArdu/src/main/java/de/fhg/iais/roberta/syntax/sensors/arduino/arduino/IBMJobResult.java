package de.fhg.iais.roberta.syntax.sensors.arduino.arduino;

import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.syntax.sensor.ExternalSensor;
import de.fhg.iais.roberta.syntax.sensor.Sensor;
import de.fhg.iais.roberta.transformer.forClass.NepoExpr;
import de.fhg.iais.roberta.transformer.forField.NepoField;
import de.fhg.iais.roberta.transformer.forField.NepoValue;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.ast.BlocklyProperties;
import de.fhg.iais.roberta.util.ast.ExternalSensorBean;
import de.fhg.iais.roberta.util.syntax.BlocklyConstants;
import de.fhg.iais.roberta.util.syntax.WithUserDefinedPort;

@NepoExpr(name = "QISKIT_IBM_JOB_RESULT", category = "SENSOR", blocklyNames = {"robSensors_qiskit_job_result"})
public final class IBMJobResult extends Sensor implements WithUserDefinedPort {

    @NepoField(name = BlocklyConstants.SENSORPORT, value = BlocklyConstants.EMPTY_PORT)
    public final String port;

    @NepoValue(name = "ID", type = BlocklyType.STRING)
    public final Expr id;

    public IBMJobResult(BlocklyProperties properties, String port, Expr id) {
        super(properties);
        this.port = port;
        this.id = id;
        setReadOnly();
    }

    @Override
    public String getUserDefinedPort() {
        return this.port;
    }
}
