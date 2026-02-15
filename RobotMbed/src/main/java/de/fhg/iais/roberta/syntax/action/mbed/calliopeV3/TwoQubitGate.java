package de.fhg.iais.roberta.syntax.action.mbed.calliopeV3;

import de.fhg.iais.roberta.syntax.action.Action;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.transformer.forClass.NepoExpr;
import de.fhg.iais.roberta.transformer.forField.NepoField;
import de.fhg.iais.roberta.transformer.forField.NepoValue;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.ast.BlocklyProperties;

@NepoExpr(
        name = "QISKIT_TWO_QUBIT_GATE",
        category = "ACTOR",
        blocklyNames = {"robActions_qiskit_two_qubit_gate"}
)
public final class TwoQubitGate extends Action {

    @NepoField(name = "GATE")
    public final String gate;

    @NepoValue(name = "CIRCUIT_ID", type = BlocklyType.STRING)
    public final Expr circuitId;

    @NepoValue(name = "CONTROL", type = BlocklyType.NUMBER)
    public final Expr control;

    @NepoValue(name = "TARGET", type = BlocklyType.NUMBER)
    public final Expr target;

    public TwoQubitGate(BlocklyProperties properties, String gate, Expr circuitId, Expr control, Expr target) {
        super(properties);
        this.gate = gate;
        this.circuitId = circuitId;
        this.control = control;
        this.target = target;
        setReadOnly();
    }
}