package de.fhg.iais.roberta.syntax.action.mbed.calliopeV3;

import de.fhg.iais.roberta.syntax.action.Action;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.transformer.forClass.NepoExpr;
import de.fhg.iais.roberta.transformer.forField.NepoValue;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.ast.BlocklyProperties;

@NepoExpr(
        name = "QISKIT_DELETE_CIRCUIT",
        category = "ACTOR",
        blocklyNames = {"robActions_qiskit_delete_circuit"}
)
public final class DeleteCircuit extends Action {

    @NepoValue(name = "CIRCUIT_ID", type = BlocklyType.STRING)
    public final Expr circuitId;

    public DeleteCircuit(BlocklyProperties properties, Expr circuitId) {
        super(properties);
        this.circuitId = circuitId;
        setReadOnly();
    }
}