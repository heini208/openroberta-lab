package de.fhg.iais.roberta.syntax.action.mbed.calliopeV3;

import de.fhg.iais.roberta.syntax.action.Action;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.transformer.forClass.NepoExpr;
import de.fhg.iais.roberta.transformer.forField.NepoValue;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.ast.BlocklyProperties;

@NepoExpr(
        name = "QISKIT_CX",
        category = "ACTOR",
        blocklyNames = {"robActions_qiskit_cx"}
)
public final class CXGate extends Action {

    @NepoValue(name = "CIRCUIT_ID", type = BlocklyType.STRING)
    public final Expr circuitId;

    @NepoValue(name = "CONTROL", type = BlocklyType.NUMBER)
    public final Expr control;

    @NepoValue(name = "TARGET", type = BlocklyType.NUMBER)
    public final Expr target;

    public CXGate(BlocklyProperties properties, Expr circuitId, Expr control, Expr target) {
        super(properties);
        this.circuitId = circuitId;
        this.control = control;
        this.target = target;
        setReadOnly();
    }
}