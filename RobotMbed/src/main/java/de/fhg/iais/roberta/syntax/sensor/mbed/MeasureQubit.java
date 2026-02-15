package de.fhg.iais.roberta.syntax.sensor.mbed;

import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.syntax.sensor.Sensor;
import de.fhg.iais.roberta.transformer.forClass.NepoExpr;
import de.fhg.iais.roberta.transformer.forField.NepoValue;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.ast.BlocklyProperties;

@NepoExpr(
        name = "QISKIT_MEASURE",
        category = "SENSOR",
        blocklyNames = {"robSensors_qiskit_measure"}
)
public final class MeasureQubit extends Sensor {

    @NepoValue(name = "CIRCUIT_ID", type = BlocklyType.STRING)
    public final Expr circuitId;

    @NepoValue(name = "QUBIT", type = BlocklyType.NUMBER)
    public final Expr qubit;

    @NepoValue(name = "CBIT", type = BlocklyType.NUMBER)
    public final Expr classicalBit;

    public MeasureQubit(BlocklyProperties properties, Expr circuitId, Expr qubit, Expr classicalBit) {
        super(properties);
        this.circuitId = circuitId;
        this.qubit = qubit;
        this.classicalBit = classicalBit;
        setReadOnly();
    }
}