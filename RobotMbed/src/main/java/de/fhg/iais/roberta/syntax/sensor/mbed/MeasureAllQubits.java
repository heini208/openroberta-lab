package de.fhg.iais.roberta.syntax.sensor.mbed;

import de.fhg.iais.roberta.syntax.sensor.Sensor;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.transformer.forClass.NepoExpr;
import de.fhg.iais.roberta.transformer.forField.NepoValue;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.ast.BlocklyProperties;

@NepoExpr(
        name = "QISKIT_MEASURE_ALL",
        category = "SENSOR",
        blocklyNames = {"robSensors_qiskit_measure_all"}
)
public final class MeasureAllQubits extends Sensor {

    @NepoValue(name = "CIRCUIT_ID", type = BlocklyType.STRING)
    public final Expr circuitId;

    public MeasureAllQubits(BlocklyProperties properties, Expr circuitId) {
        super(properties);
        this.circuitId = circuitId;
        setReadOnly();
    }
}