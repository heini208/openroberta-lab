package de.fhg.iais.roberta.syntax.sensor.mbed;

import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.syntax.sensor.Sensor;
import de.fhg.iais.roberta.transformer.forClass.NepoExpr;
import de.fhg.iais.roberta.transformer.forField.NepoValue;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.ast.BlocklyProperties;

@NepoExpr(
        name = "QISKIT_CREATE_CIRCUIT",
        category = "SENSOR",
        blocklyNames = {"robSensors_qiskit_create_circuit"}
)
public final class CreateCircuit extends Sensor {

    @NepoValue(name = "NUM_QUBITS", type = BlocklyType.NUMBER)
    public final Expr numQubits;

    @NepoValue(name = "NUM_CLBITS", type = BlocklyType.NUMBER)
    public final Expr numClbits;

    public CreateCircuit(BlocklyProperties properties, Expr numQubits, Expr numClbits) {
        super(properties);
        this.numQubits = numQubits;
        this.numClbits = numClbits;
        setReadOnly();
    }
}