package de.fhg.iais.roberta.syntax.action.mbed.calliopeV3;

import de.fhg.iais.roberta.syntax.action.Action;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.transformer.forClass.NepoExpr;
import de.fhg.iais.roberta.transformer.forField.NepoValue;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.ast.BlocklyProperties;

@NepoExpr(
        name = "QISKIT_SWAP",
        category = "ACTOR",
        blocklyNames = {"robActions_qiskit_swap"}
)
public final class SwapGate extends Action {

    @NepoValue(name = "CIRCUIT_ID", type = BlocklyType.STRING)
    public final Expr circuitId;

    @NepoValue(name = "Q1", type = BlocklyType.NUMBER)
    public final Expr q1;

    @NepoValue(name = "Q2", type = BlocklyType.NUMBER)
    public final Expr q2;

    public SwapGate(BlocklyProperties properties, Expr circuitId, Expr q1, Expr q2) {
        super(properties);
        this.circuitId = circuitId;
        this.q1 = q1;
        this.q2 = q2;
        setReadOnly();
    }
}