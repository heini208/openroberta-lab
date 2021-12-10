package de.fhg.iais.roberta.syntax.action.mbot2;

import de.fhg.iais.roberta.syntax.BlockType;
import de.fhg.iais.roberta.syntax.BlockTypeContainer;
import de.fhg.iais.roberta.syntax.BlocklyBlockProperties;
import de.fhg.iais.roberta.syntax.BlocklyComment;
import de.fhg.iais.roberta.syntax.BlocklyConstants;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.WithUserDefinedPort;
import de.fhg.iais.roberta.syntax.action.Action;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.transformer.NepoField;
import de.fhg.iais.roberta.transformer.NepoPhrase;
import de.fhg.iais.roberta.transformer.NepoValue;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.util.dbc.Assert;

/**
 * This class represents the <b>robActions_println</b> block
 */
@NepoPhrase(containerType = "PRINTLN_ACTION")
public class PrintlnAction<V> extends Action<V> implements WithUserDefinedPort<V> {
    @NepoValue(name = BlocklyConstants.OUT, type = BlocklyType.STRING)
    public final Expr<V> msg;
    @NepoField(name = BlocklyConstants.ACTORPORT, value = BlocklyConstants.EMPTY_PORT)
    public final String port;

    public PrintlnAction(BlockType kind, BlocklyBlockProperties properties, BlocklyComment comment, Expr<V> msg, String port) {
        super(kind, properties, comment);
        Assert.notNull(msg);
        this.msg = msg;
        this.port = port;
        setReadOnly();
    }

    public static <V> Phrase<V> make(Expr<V> msg, String port, BlocklyBlockProperties properties, BlocklyComment comment) {
        return new PrintlnAction<>(BlockTypeContainer.getByName("SHOW_TEXT_ACTION"),properties,comment,msg ,port);
    }

    @Override
    public String getUserDefinedPort() {
        return port;
    }
}