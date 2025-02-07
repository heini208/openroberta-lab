package de.fhg.iais.roberta.visitor.lang.codegen;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.common.collect.Lists;

import de.fhg.iais.roberta.components.ConfigurationAst;
import de.fhg.iais.roberta.mode.action.DriveDirection;
import de.fhg.iais.roberta.mode.action.TurnDirection;
import de.fhg.iais.roberta.mode.general.IndexLocation;
import de.fhg.iais.roberta.mode.general.ListElementOperations;
import de.fhg.iais.roberta.syntax.Phrase;
import de.fhg.iais.roberta.syntax.action.serial.SerialWriteAction;
import de.fhg.iais.roberta.syntax.configuration.ConfigurationComponent;
import de.fhg.iais.roberta.syntax.lang.blocksequence.ActivityTask;
import de.fhg.iais.roberta.syntax.lang.blocksequence.Location;
import de.fhg.iais.roberta.syntax.lang.blocksequence.MainTask;
import de.fhg.iais.roberta.syntax.lang.blocksequence.StartActivityTask;
import de.fhg.iais.roberta.syntax.lang.expr.ActionExpr;
import de.fhg.iais.roberta.syntax.lang.expr.Binary;
import de.fhg.iais.roberta.syntax.lang.expr.Binary.Op;
import de.fhg.iais.roberta.syntax.lang.expr.BoolConst;
import de.fhg.iais.roberta.syntax.lang.expr.ColorConst;
import de.fhg.iais.roberta.syntax.lang.expr.ConnectConst;
import de.fhg.iais.roberta.syntax.lang.expr.EmptyExpr;
import de.fhg.iais.roberta.syntax.lang.expr.EmptyList;
import de.fhg.iais.roberta.syntax.lang.expr.Expr;
import de.fhg.iais.roberta.syntax.lang.expr.ExprList;
import de.fhg.iais.roberta.syntax.lang.expr.FunctionExpr;
import de.fhg.iais.roberta.syntax.lang.expr.ListCreate;
import de.fhg.iais.roberta.syntax.lang.expr.MathConst;
import de.fhg.iais.roberta.syntax.lang.expr.MethodExpr;
import de.fhg.iais.roberta.syntax.lang.expr.NNGetBias;
import de.fhg.iais.roberta.syntax.lang.expr.NNGetOutputNeuronVal;
import de.fhg.iais.roberta.syntax.lang.expr.NNGetWeight;
import de.fhg.iais.roberta.syntax.lang.expr.NullConst;
import de.fhg.iais.roberta.syntax.lang.expr.NumConst;
import de.fhg.iais.roberta.syntax.lang.expr.RgbColor;
import de.fhg.iais.roberta.syntax.lang.expr.SensorExpr;
import de.fhg.iais.roberta.syntax.lang.expr.ShadowExpr;
import de.fhg.iais.roberta.syntax.lang.expr.StmtExpr;
import de.fhg.iais.roberta.syntax.lang.expr.StringConst;
import de.fhg.iais.roberta.syntax.lang.expr.Unary;
import de.fhg.iais.roberta.syntax.lang.expr.Var;
import de.fhg.iais.roberta.syntax.lang.expr.VarDeclaration;
import de.fhg.iais.roberta.syntax.lang.functions.GetSubFunct;
import de.fhg.iais.roberta.syntax.lang.functions.IndexOfFunct;
import de.fhg.iais.roberta.syntax.lang.functions.LengthOfIsEmptyFunct;
import de.fhg.iais.roberta.syntax.lang.functions.ListGetIndex;
import de.fhg.iais.roberta.syntax.lang.functions.ListRepeat;
import de.fhg.iais.roberta.syntax.lang.functions.ListSetIndex;
import de.fhg.iais.roberta.syntax.lang.functions.MathCastCharFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathCastStringFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathConstrainFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathNumPropFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathOnListFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathPowerFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathRandomFloatFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathRandomIntFunct;
import de.fhg.iais.roberta.syntax.lang.functions.MathSingleFunct;
import de.fhg.iais.roberta.syntax.lang.functions.TextCharCastNumberFunct;
import de.fhg.iais.roberta.syntax.lang.functions.TextJoinFunct;
import de.fhg.iais.roberta.syntax.lang.functions.TextPrintFunct;
import de.fhg.iais.roberta.syntax.lang.functions.TextStringCastNumberFunct;
import de.fhg.iais.roberta.syntax.lang.methods.MethodCall;
import de.fhg.iais.roberta.syntax.lang.methods.MethodIfReturn;
import de.fhg.iais.roberta.syntax.lang.methods.MethodReturn;
import de.fhg.iais.roberta.syntax.lang.methods.MethodVoid;
import de.fhg.iais.roberta.syntax.lang.stmt.ActionStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.AssertStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.AssignStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.DebugAction;
import de.fhg.iais.roberta.syntax.lang.stmt.ExprStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.FunctionStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.IfStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.MethodStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.NNSetBiasStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.NNSetInputNeuronVal;
import de.fhg.iais.roberta.syntax.lang.stmt.NNSetWeightStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.NNStepStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.RepeatStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.RepeatStmt.Mode;
import de.fhg.iais.roberta.syntax.lang.stmt.SensorStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.Stmt;
import de.fhg.iais.roberta.syntax.lang.stmt.StmtFlowCon;
import de.fhg.iais.roberta.syntax.lang.stmt.StmtFlowCon.Flow;
import de.fhg.iais.roberta.syntax.lang.stmt.StmtList;
import de.fhg.iais.roberta.syntax.lang.stmt.StmtTextComment;
import de.fhg.iais.roberta.syntax.lang.stmt.TernaryExpr;
import de.fhg.iais.roberta.syntax.lang.stmt.WaitStmt;
import de.fhg.iais.roberta.syntax.lang.stmt.WaitTimeStmt;
import de.fhg.iais.roberta.syntax.sensor.Sensor;
import de.fhg.iais.roberta.typecheck.BlocklyType;
import de.fhg.iais.roberta.typecheck.NepoInfo;
import de.fhg.iais.roberta.util.basic.C;
import de.fhg.iais.roberta.util.dbc.DbcException;
import de.fhg.iais.roberta.util.syntax.FunctionNames;
import de.fhg.iais.roberta.util.syntax.MotorDuration;
import de.fhg.iais.roberta.util.visitor.JumpLinker;
import static de.fhg.iais.roberta.util.visitor.JumpLinker.JumpTarget.BREAK;
import static de.fhg.iais.roberta.util.visitor.JumpLinker.JumpTarget.CONTINUE;
import static de.fhg.iais.roberta.util.visitor.JumpLinker.JumpTarget.INTERNAL_BREAK;
import static de.fhg.iais.roberta.util.visitor.JumpLinker.JumpTarget.METHOD_END;
import static de.fhg.iais.roberta.util.visitor.JumpLinker.JumpTarget.STATEMENT_END;
import de.fhg.iais.roberta.visitor.BaseVisitor;
import de.fhg.iais.roberta.visitor.IVisitor;
import de.fhg.iais.roberta.visitor.lang.ILanguageVisitor;

public abstract class AbstractStackMachineVisitor extends BaseVisitor<Void> implements ILanguageVisitor<Void> {
    private static final Predicate<String> IS_INVALID_BLOCK_ID = s -> s.equals("1");
    private static final List<Class<? extends Phrase>> DONT_ADD_DEBUG_STOP = Arrays.asList(Expr.class, VarDeclaration.class, Sensor.class, MainTask.class, ExprStmt.class, StmtList.class, RepeatStmt.class, WaitStmt.class);

    private List<JSONObject> opArray = new ArrayList<>();
    protected final ConfigurationAst configuration;

    private final JumpLinker jumpLinker = new JumpLinker();

    private final Map<String, List<JSONObject>> methodCalls = new HashMap<>();
    private final Map<String, Integer> methodDeclarations = new HashMap<>();

    /**
     * blocklyIds which will be added to the next block marking a possible step into for the debugger
     */
    private final Set<String> possibleDebugStops = new HashSet<>();
    /**
     * blocklyIds which will be initiated with next block
     */
    private final Set<String> toInitiateBlocks = new HashSet<>();

    /**
     * blocklyIds which are initiated but not yet terminated
     */
    private final Set<String> openBlocks = new HashSet<>();

    protected boolean debugger = true;

    protected AbstractStackMachineVisitor(ConfigurationAst configuration) {
        this.configuration = configuration;
    }

    @Override
    public Void visit(Phrase visitable) {
        boolean shouldHighlight = shouldBeHighlighted(visitable);
        if ( shouldHighlight ) beginPhrase(visitable);
        Void result = super.visit(visitable);
        if ( shouldHighlight ) endPhrase(visitable);
        return result;
    }

    private boolean shouldBeHighlighted(Phrase visitable) {
        boolean isNotMainTask = !(visitable instanceof MainTask);
        return isNotMainTask && !openBlocks.contains(visitable.getProperty().getBlocklyId());
    }

    protected void endPhrase(Phrase phrase) {
        String blocklyId = phrase.getProperty().getBlocklyId();
        if ( debugger && isValidBlocklyId(blocklyId) ) {
            if ( !opArray.isEmpty() ) {
                JSONObject lastElement = opArray.get(opArray.size() - 1);
                if ( !lastElement.has(C.HIGHTLIGHT_MINUS) ) {
                    lastElement.put(C.HIGHTLIGHT_MINUS, Collections.singletonList(blocklyId));
                } else {
                    JSONArray array = lastElement.getJSONArray(C.HIGHTLIGHT_MINUS);
                    if ( !array.toList().contains(blocklyId) ) {
                        array.put(blocklyId);
                    }
                }
            }
            toInitiateBlocks.remove(blocklyId);
            openBlocks.remove(blocklyId);
        }
    }

    protected void beginPhrase(Phrase phrase) {
        String blocklyId = phrase.getProperty().getBlocklyId();
        if ( debugger && isValidBlocklyId(blocklyId) ) {
            toInitiateBlocks.add(blocklyId);
            openBlocks.add(blocklyId);

            if ( DONT_ADD_DEBUG_STOP.stream().noneMatch(cls -> cls.isInstance(phrase)) ) {
                possibleDebugStops.add(blocklyId);
            }
        }
    }

    @Override
    public final Void visitNumConst(NumConst numConst) {
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, numConst.getKind().getName()).put(C.VALUE, numConst.value);
        return app(o);
    }

    @Override
    public final Void visitMathConst(MathConst mathConst) {
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, C.MATH_CONST).put(C.VALUE, mathConst.mathConst);
        return app(o);
    }

    @Override
    public final Void visitBoolConst(BoolConst boolConst) {
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, boolConst.getKind().getName()).put(C.VALUE, boolConst.value);
        return app(o);
    }

    @Override
    public final Void visitStringConst(StringConst stringConst) {
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, stringConst.getKind().getName());
        o.put(C.VALUE, stringConst.value.replaceAll("[<>\\$]", ""));
        return app(o);
    }

    @Override
    public final Void visitNullConst(NullConst nullConst) {
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, "C." + nullConst.getKind().getName());
        return app(o);
    }

    @Override
    public Void visitColorConst(ColorConst colorConst) {
        int colorId = 0;
        switch ( colorConst.getHexValueAsString().toUpperCase() ) {
            case "#FF1493":
                colorId = 1;
                break;
            case "#800080":
                colorId = 2;
                break;
            case "#4876FF":
                colorId = 3;
                break;
            case "#00FFFF":
                colorId = 4;
                break;
            case "#90EE90":
                colorId = 5;
                break;
            case "#008000":
                colorId = 6;
                break;
            case "#FFFF00":
                colorId = 7;
                break;
            case "#FFA500":
                colorId = 8;
                break;
            case "#FF0000":
                colorId = 9;
                break;
            case "#FFFFFE":
            case "#FFFFFF":
                colorId = 10;
                break;
            default:
                colorConst.addInfo(NepoInfo.error("SIM_BLOCK_NOT_SUPPORTED"));
                throw new DbcException("Invalid color constant: " + colorConst.getHexIntAsString());
        }
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, C.COLOR_CONST).put(C.VALUE, colorId);
        return app(o);
    }

    @Override
    public final Void visitRgbColor(RgbColor rgbColor) {
        rgbColor.R.accept(this);
        rgbColor.G.accept(this);
        rgbColor.B.accept(this);
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, C.RGB_COLOR_CONST);
        return app(o);
    }

    @Override
    public final Void visitShadowExpr(ShadowExpr shadowExpr) {
        if ( shadowExpr.block != null ) {
            shadowExpr.block.accept(this);
        } else {
            shadowExpr.shadow.accept(this);
        }
        return null;
    }

    @Override
    public final Void visitVar(Var var) {
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, C.VAR).put(C.NAME, var.name);
        return app(o);
    }

    @Override
    public final Void visitVarDeclaration(VarDeclaration var) {
        if ( var.value.getKind().hasName("EXPR_LIST") ) {
            ExprList list = (ExprList) var.value;
            if ( list.get().size() == 2 ) {
                list.get().get(1).accept(this);
            } else {
                list.get().get(0).accept(this);
            }
        } else {
            var.value.accept(this);
        }
        JSONObject o = makeNode(C.VAR_DECLARATION).put(C.TYPE, var.typeVar).put(C.NAME, var.name);
        return app(o);
    }

    @Override
    public final Void visitUnary(Unary unary) {
        unary.expr.accept(this);
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, C.UNARY).put(C.OP, unary.op);
        return app(o);
    }

    @Override
    public final Void visitBinary(Binary binary) {
        switch ( binary.op ) {
            case AND:
            case OR:
                /*
                Jumps are needed because of lazy evaluation

                AND                       OR
                left                      left
                 ▼                         ▼
                JUMP false──┐             JUMP true ──┐
                 ▼          │              ▼          │
                right       │             right       │
                 ▼          │              ▼          │
                JUMP Always─┼─┐           JUMP Always─┼─┐
                 ▼          │ │            ▼          │ │
                false◄──────┘ │           true◄───────┘ │
                 ▼            │            ▼            │
                  ◄───────────┘             ◄───────────┘
                 */

                appComment(C.BINARY, true);

                boolean isOr = binary.op == Op.OR;
                binary.left.accept(this);
                JSONObject skipNextCondition = makeNode(C.JUMP).put(C.CONDITIONAL, isOr);
                app(skipNextCondition);

                binary.getRight().accept(this);
                JSONObject jumpToEnd = makeNode(C.JUMP).put(C.CONDITIONAL, C.ALWAYS);
                app(jumpToEnd);

                skipNextCondition.put(C.TARGET, opArray.size());
                app(makeNode(C.EXPR).put(C.EXPR, C.BOOL_CONST).put(C.VALUE, isOr));
                jumpToEnd.put(C.TARGET, opArray.size());

                appComment(C.BINARY, false);
                return null;
            default:
                binary.left.accept(this);
                binary.getRight().accept(this);
                JSONObject o;
                // FIXME: The math change should be removed from the binary expression since it is a statement
                switch ( binary.op ) {
                    case MATH_CHANGE:
                        o = makeNode(C.MATH_CHANGE).put(C.NAME, ((Var) binary.left).name);
                        break;
                    case TEXT_APPEND:
                        o = makeNode(C.TEXT_APPEND).put(C.NAME, ((Var) binary.left).name);
                        break;

                    default:
                        o = makeNode(C.EXPR).put(C.EXPR, C.BINARY).put(C.OP, binary.op);
                        break;
                }
                return app(o);

        }

    }

    @Override
    public final Void visitMathPowerFunct(MathPowerFunct mathPowerFunct) {
        mathPowerFunct.param.get(0).accept(this);
        mathPowerFunct.param.get(1).accept(this);
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, C.BINARY).put(C.OP, mathPowerFunct.functName);
        return app(o);
    }

    @Override
    public final Void visitActionExpr(ActionExpr actionExpr) {
        actionExpr.action.accept(this);
        return null;
    }

    @Override
    public final Void visitSensorExpr(SensorExpr sensorExpr) {
        sensorExpr.sensor.accept(this);
        return null;
    }

    @Override
    public final Void visitMethodExpr(MethodExpr methodExpr) {
        methodExpr.getMethod().accept(this);
        return null;
    }

    @Override
    public final Void visitEmptyList(EmptyList emptyList) {
        throw new DbcException("Operation not supported");
    }

    @Override
    public final Void visitEmptyExpr(EmptyExpr emptyExpr) {
        JSONObject o;
        switch ( emptyExpr.getDefVal() ) {
            case STRING:
                o = makeNode(C.EXPR).put(C.EXPR, C.STRING_CONST).put(C.VALUE, "");
                break;
            case BOOLEAN:
                o = makeNode(C.EXPR).put(C.EXPR, C.BOOL_CONST).put(C.VALUE, "true");
                break;
            case NUMBER_INT:
            case NUMBER:
                o = makeNode(C.EXPR).put(C.EXPR, C.NUM_CONST).put(C.VALUE, 0);
                break;
            case COLOR:
                o = makeNode(C.EXPR).put(C.EXPR, C.COLOR_CONST).put(C.VALUE, 3);
                break;
            case NULL:
            case CONNECTION:
            case ARRAY_BOOLEAN:
            case ARRAY_COLOUR:
            case ARRAY_CONNECTION:
            case ARRAY_IMAGE:
            case ARRAY_NUMBER:
            case ARRAY_STRING:
                o = makeNode(C.EXPR).put(C.EXPR, C.NULL_CONST);
                break;
            case IMAGE:
                JSONArray jsonImage = new JSONArray();
                for ( int i = 0; i < 5; i++ ) {
                    ArrayList<Integer> a = new ArrayList<>();
                    for ( int j = 0; j < 5; j++ ) {
                        a.add(0);
                    }
                    jsonImage.put(new JSONArray(a));
                }
                o = makeNode(C.EXPR).put(C.EXPR, C.IMAGE_CONST).put(C.VALUE, jsonImage);
                break;
            case CAPTURED_TYPE: // TODO: get the captured type
                o = makeNode(C.EXPR).put(C.EXPR, C.NUM_CONST).put(C.VALUE, 0);
                break;
            default:
                throw new DbcException("Operation not supported");
        }
        return app(o);
    }

    @Override
    public final Void visitExprList(ExprList exprList) {
        for ( Expr expr : exprList.get() ) {
            if ( !expr.getKind().hasName("EMPTY_EXPR") ) {
                expr.accept(this);
            }
        }
        return null;
    }

    @Override
    public final Void visitStmtExpr(StmtExpr stmtExpr) {
        stmtExpr.stmt.accept(this);
        return null;
    }

    @Override
    public final Void visitActionStmt(ActionStmt actionStmt) {
        actionStmt.action.accept(this);
        return null;
    }

    @Override
    public final Void visitAssignStmt(AssignStmt assignStmt) {
        assignStmt.expr.accept(this);
        JSONObject o = makeNode(C.ASSIGN_STMT).put(C.NAME, assignStmt.name.name);
        return app(o);
    }

    @Override
    public final Void visitExprStmt(ExprStmt exprStmt) {
        exprStmt.expr.accept(this);
        return null;
    }

    @Override
    public final Void visitTernaryExpr(TernaryExpr ternaryExpr) {
        appComment(C.IF_STMT, true);
        ternaryExpr.condition.accept(this);
        // JUMP when condition is not fullfilled
        JSONObject jumpOverThen = makeNode(C.JUMP).put(C.CONDITIONAL, false);
        app(jumpOverThen);
        ternaryExpr.thenPart.accept(this);
        // JUMP because if is finished
        JSONObject jumpToEnd = makeNode(C.JUMP).put(C.CONDITIONAL, C.ALWAYS);
        app(jumpToEnd);
        jumpLinker.register(jumpToEnd, STATEMENT_END);
        jumpOverThen.put(C.TARGET, opArray.size());
        ternaryExpr.elsePart.accept(this);
        jumpLinker.handle(STATEMENT_END).forEach(jump -> jump.put(C.TARGET, opArray.size()));
        appComment(C.IF_STMT, false);
        return null;
    }

    @Override
    public final Void visitIfStmt(IfStmt ifStmt) {
        appComment(C.IF_STMT, true);

        int numberOfThens = ifStmt.expr.size();
        // TODO: better a list of pairs. pair of lists needs this kind of for
        for ( int i = 0; i < numberOfThens; i++ ) {
            ifStmt.expr.get(i).accept(this);
            // JUMP when condition not fullfilled
            JSONObject jumpOverThen = makeNode(C.JUMP).put(C.CONDITIONAL, false);
            app(jumpOverThen);
            ifStmt.thenList.get(i).accept(this);

            // JUMP when if was fullfilled
            JSONObject jumpToEnd = makeNode(C.JUMP).put(C.CONDITIONAL, C.ALWAYS);
            app(jumpToEnd);
            jumpLinker.register(jumpToEnd, STATEMENT_END);

            jumpOverThen.put(C.TARGET, opArray.size());
        }
        if ( !ifStmt.elseList.get().isEmpty() ) {
            ifStmt.elseList.accept(this);
        }

        jumpLinker.handle(STATEMENT_END)
            .forEach(jump -> jump.put(C.TARGET, opArray.size()));

        appComment(C.IF_STMT, false);
        return null;
    }

    @Override
    public final Void visitNNStepStmt(NNStepStmt nnStepStmt) {
        JSONObject o = makeNode(C.NN_STEP_STMT);
        app(o);
        return null;
    }

    @Override
    public final Void visitNNSetInputNeuronVal(NNSetInputNeuronVal setStmt) {
        setStmt.value.accept(this);
        JSONObject o = makeNode(C.NN_SETINPUTNEURON_STMT).put(C.NAME, setStmt.name);
        app(o);
        return null;
    }

    @Override
    public final Void visitNNGetOutputNeuronVal(NNGetOutputNeuronVal getVal) {
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, C.NN_GETOUTPUTNEURON_VAL).put(C.NAME, getVal.name);
        app(o);
        return null;
    }

    @Override
    public final Void visitNNSetWeightStmt(NNSetWeightStmt chgStmt) {
        chgStmt.value.accept(this);
        JSONObject o = makeNode(C.NN_SETWEIGHT_STMT).put(C.FROM, chgStmt.from).put(C.TO, chgStmt.to);
        app(o);
        return null;
    }

    @Override
    public final Void visitNNSetBiasStmt(NNSetBiasStmt chgStmt) {
        chgStmt.value.accept(this);
        JSONObject o = makeNode(C.NN_SETBIAS_STMT).put(C.NAME, chgStmt.name);
        app(o);
        return null;
    }

    @Override
    public final Void visitNNGetWeight(NNGetWeight getVal) {
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, C.NN_GETWEIGHT).put(C.FROM, getVal.from).put(C.TO, getVal.to);
        app(o);
        return null;
    }

    @Override
    public final Void visitNNGetBias(NNGetBias getVal) {
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, C.NN_GETBIAS).put(C.NAME, getVal.name);
        app(o);
        return null;
    }

    @Override
    public final Void visitRepeatStmt(RepeatStmt repeatStmt) {
        Mode mode = repeatStmt.mode;
        String blocklyId = repeatStmt.getProperty().getBlocklyId();

        switch ( mode ) {
            case WAIT:
                // the very special case of a wait stmt. The AST is not perfectly designed for this case
                repeatStmt.expr.accept(this);
                JSONObject skipThenPart = makeNode(C.JUMP).put(C.CONDITIONAL, false);
                app(skipThenPart);
                repeatStmt.list.accept(this);
                JSONObject breakStatement = makeNode(C.JUMP).put(C.CONDITIONAL, C.ALWAYS);
                addHighlightingToJump(breakStatement);
                app(breakStatement);
                jumpLinker.register(breakStatement, INTERNAL_BREAK);
                skipThenPart.put(C.TARGET, opArray.size());
                return null;
            case FOR:
            case TIMES:
                jumpLinker.isolate(() -> {
                    appComment(C.REPEAT_STMT, true);

                    if ( !(repeatStmt.expr instanceof ExprList) ) {
                        throw new DbcException(String.format("Expected %s to be an ExprList", repeatStmt.expr));
                    }

                    List<Expr> exprList = ((ExprList) repeatStmt.expr).get();
                    if ( !(exprList.get(0) instanceof Var) ) {
                        throw new DbcException(String.format("Expected %s to be an variable", exprList.get(0)));
                    }
                    Var variable = (Var) exprList.get(0);
                    String variableName = variable.name;
                    Expr initialValue = exprList.get(1);
                    Expr terminationValue = exprList.get(2);
                    Expr incrementValue = exprList.get(3);

                    // Initialize Var
                    initialValue.accept(this);
                    app(makeNode(C.VAR_DECLARATION).put(C.TYPE, initialValue.getVarType()).put(C.NAME, variableName));

                    int programCounterAfterInitialization = opArray.size();
                    // Termination Expr
                    variable.accept(this);
                    terminationValue.accept(this);
                    app(makeNode(C.EXPR).put(C.EXPR, C.BINARY).put(C.OP, Op.LT));
                    JSONObject jump = makeNode(C.JUMP).put(C.CONDITIONAL, false);
                    app(jump);

                    addDebugStatement(repeatStmt);

                    repeatStmt.list.accept(this);

                    int programCounterAfterStatementList = opArray.size();

                    // Increment
                    incrementValue.accept(this);
                    variable.accept(this);
                    app(makeNode(C.EXPR).put(C.EXPR, C.BINARY).put(C.OP, Op.ADD));
                    app(makeNode(C.ASSIGN_STMT).put(C.NAME, variableName));

                    app(makeNode(C.JUMP).put(C.CONDITIONAL, C.ALWAYS).put(C.TARGET, programCounterAfterInitialization));
                    int programCounterAfterForLoop = opArray.size();
                    jump.put(C.TARGET, programCounterAfterForLoop);

                    app(makeNode(C.UNBIND_VAR).put(C.NAME, variableName));

                    jumpLinker.handle(CONTINUE).forEach(statement -> {
                        statement.put(C.TARGET, programCounterAfterStatementList);
                        removeOpenBlocksFromUnhighlight(statement);
                    });

                    jumpLinker.handle(BREAK).forEach(statement -> {
                        statement.put(C.TARGET, programCounterAfterForLoop);
                        removeOpenBlocksFromUnhighlight(statement, blocklyId);
                    });

                    if ( !jumpLinker.isEmpty() ) {
                        throw new DbcException("Invalid flow control expression");
                    }

                    appComment(C.REPEAT_STMT, false);
                });
                return null;
            case FOR_EACH:
                jumpLinker.isolate(() -> {
                    appComment(C.REPEAT_STMT, true);
                    if ( !(repeatStmt.expr instanceof Binary) ) {
                        throw new DbcException(String.format("Expected %s to be an Binary", repeatStmt.expr));
                    }

                    Binary binary = (Binary) repeatStmt.expr;
                    if ( !(binary.left instanceof VarDeclaration) ) {
                        throw new DbcException(String.format("Expected %s to be a VarDeclaration", repeatStmt.expr));
                    }
                    if ( !(binary.getRight() instanceof Var) ) {
                        throw new DbcException(String.format("Expected %s to be a VarDeclaration", repeatStmt.expr));
                    }

                    VarDeclaration varDeclaration = (VarDeclaration) binary.left;
                    String variableName = varDeclaration.name;
                    String runVariableName = variableName + "_runningVariable";
                    Var listVariable = (Var) binary.getRight();

                    // Init run variable (int i = 0)
                    app(makeNode(C.EXPR).put(C.EXPR, C.NUM_CONST).put(C.VALUE, 0));
                    app(makeNode(C.VAR_DECLARATION).put(C.TYPE, BlocklyType.NUMBER).put(C.NAME, runVariableName));

                    // Init variable (Element element)
                    varDeclaration.accept(this);
                    int programCounterAfterInitialization = opArray.size();

                    // Termination expr ( i < list.length )
                    app(makeNode(C.EXPR).put(C.EXPR, C.VAR).put(C.NAME, runVariableName));
                    listVariable.accept(this);
                    app(makeNode(C.EXPR).put(C.EXPR, C.LIST_OPERATION).put(C.OP, FunctionNames.LIST_LENGTH.toString().toLowerCase()));
                    app(makeNode(C.EXPR).put(C.EXPR, C.BINARY).put(C.OP, Op.LT));

                    JSONObject jump = makeNode(C.JUMP).put(C.CONDITIONAL, false);
                    app(jump);

                    // Assign variable ( element = list.get(i) )
                    listVariable.accept(this);
                    app(makeNode(C.EXPR).put(C.EXPR, C.VAR).put(C.NAME, runVariableName));
                    app(makeNode(C.EXPR)
                        .put(C.EXPR, C.LIST_OPERATION)
                        .put(C.OP, ListElementOperations.GET.toString().toLowerCase())
                        .put(C.POSITION, IndexLocation.FROM_START.toString().toLowerCase()));
                    app(makeNode(C.ASSIGN_STMT).put(C.NAME, variableName));

                    addDebugStatement(repeatStmt);
                    repeatStmt.list.accept(this);

                    int programCounterAfterStatementList = opArray.size();

                    // Increment (i = i + 1)
                    app(makeNode(C.EXPR).put(C.EXPR, C.NUM_CONST).put(C.VALUE, 1));
                    app(makeNode(C.EXPR).put(C.EXPR, C.VAR).put(C.NAME, runVariableName));
                    app(makeNode(C.EXPR).put(C.EXPR, C.BINARY).put(C.OP, Op.ADD));
                    app(makeNode(C.ASSIGN_STMT).put(C.NAME, runVariableName));

                    app(makeNode(C.JUMP).put(C.CONDITIONAL, C.ALWAYS).put(C.TARGET, programCounterAfterInitialization));
                    int programCounterAfterForLoop = opArray.size();
                    jump.put(C.TARGET, programCounterAfterForLoop);

                    app(makeNode(C.UNBIND_VAR).put(C.NAME, variableName));
                    app(makeNode(C.UNBIND_VAR).put(C.NAME, runVariableName));

                    jumpLinker.handle(CONTINUE).forEach(statement -> {
                        statement.put(C.TARGET, programCounterAfterStatementList);
                        removeOpenBlocksFromUnhighlight(statement);
                    });

                    jumpLinker.handle(BREAK).forEach(statement -> {
                        statement.put(C.TARGET, programCounterAfterForLoop);
                        removeOpenBlocksFromUnhighlight(statement, blocklyId);
                    });

                    if ( !jumpLinker.isEmpty() ) {
                        throw new DbcException("Invalid flow control expression");
                    }

                    appComment(C.REPEAT_STMT, false);
                });
                return null;
            case FOREVER:
            case FOREVER_ARDU:
                jumpLinker.isolate(() -> {
                    appComment(C.REPEAT_STMT, true);

                    int beforeExprTarget = opArray.size();
                    addDebugStatement(repeatStmt);

                    repeatStmt.list.accept(this);

                    app(makeNode(C.JUMP).put(C.CONDITIONAL, C.ALWAYS).put(C.TARGET, beforeExprTarget));

                    jumpLinker.handle(BREAK).forEach(statement -> {
                        statement.put(C.TARGET, opArray.size());
                        removeOpenBlocksFromUnhighlight(statement, blocklyId);
                    });

                    jumpLinker.handle(CONTINUE).forEach(statement -> {
                        statement.put(C.TARGET, beforeExprTarget);
                        removeOpenBlocksFromUnhighlight(statement);
                    });

                    if ( !jumpLinker.isEmpty() ) {
                        throw new DbcException("Invalid flow control expression");
                    }

                    appComment(C.REPEAT_STMT, false);
                });
                return null;
            case WHILE:
            case UNTIL:
                jumpLinker.isolate(() -> {
                    appComment(C.REPEAT_STMT, true);
                    int beforeExprTarget = opArray.size();
                    addDebugStatement(repeatStmt);

                    repeatStmt.expr.accept(this);
                    // no difference between WHILE and UNTIL because a NOT gets injected into UNTIL by jaxbToAST
                    JSONObject jumpOverWhile = makeNode(C.JUMP).put(C.CONDITIONAL, false);
                    addHighlightingToJump(jumpOverWhile);
                    jumpLinker.register(jumpOverWhile, BREAK);

                    app(jumpOverWhile);
                    repeatStmt.list.accept(this);
                    app(makeNode(C.JUMP).put(C.CONDITIONAL, C.ALWAYS).put(C.TARGET, beforeExprTarget));

                    jumpLinker.handle(BREAK).forEach(statement -> {
                        statement.put(C.TARGET, opArray.size());
                        removeOpenBlocksFromUnhighlight(statement);
                    });

                    jumpLinker.handle(CONTINUE).forEach(statement -> {
                        statement.put(C.TARGET, beforeExprTarget);
                        removeOpenBlocksFromUnhighlight(statement, blocklyId);
                    });

                    if ( !jumpLinker.isEmpty() ) {
                        throw new DbcException("Invalid flow control expression");
                    }

                    appComment(C.REPEAT_STMT, false);
                });
                return null;
            default:
                throw new DbcException("Invalid repeat mode: " + mode);
        }

    }

    @Override
    public final Void visitSensorStmt(SensorStmt sensorStmt) {
        sensorStmt.sensor.accept(this);
        return null;
    }

    @Override
    public final Void visitStmtFlowCon(StmtFlowCon stmtFlowCon) {
        JSONObject jump = makeNode(C.JUMP)
            .put(C.CONDITIONAL, C.ALWAYS);

        addHighlightingToJump(jump);
        jumpLinker.register(jump, stmtFlowCon.flow == Flow.BREAK ? BREAK : CONTINUE);
        return app(jump);
    }

    @Override
    public final Void visitStmtList(StmtList stmtList) {
        for ( Stmt stmt : stmtList.get() ) {
            stmt.accept(this);
        }
        return null;
    }

    @Override
    public final Void visitMainTask(MainTask mainTask) {
        mainTask.variables.accept(this);
        if ( mainTask.debug.equals("TRUE") ) {
            JSONObject o = makeNode(C.CREATE_DEBUG_ACTION);
            return app(o);
        }
        return null;
    }

    @Override
    public final Void visitActivityTask(ActivityTask activityTask) {
        throw new DbcException("Operation not supported");
    }

    @Override
    public final Void visitStartActivityTask(StartActivityTask startActivityTask) {
        throw new DbcException("Operation not supported");
    }

    @Override
    public final Void visitWaitStmt(WaitStmt waitStmt) {
        jumpLinker.isolate(() -> {
            appComment(C.WAIT_STMT, true);
            int programCounterStart = opArray.size();
            addDebugStatement(waitStmt);

            waitStmt.statements.get()
                .forEach(statement -> statement.accept(this));
            this.getOpArray().add(makeNode(C.EXPR).put(C.EXPR, C.NUM_CONST).put(C.VALUE, 1));
            this.getOpArray().add(makeNode(C.WAIT_TIME_STMT));
            app(makeNode(C.JUMP).put(C.CONDITIONAL, C.ALWAYS).put(C.TARGET, programCounterStart));

            jumpLinker.handle(INTERNAL_BREAK).forEach((statement) -> statement.put(C.TARGET, opArray.size()));

            appComment(C.WAIT_STMT, false);
        });
        return null;
    }

    @Override
    public final Void visitWaitTimeStmt(WaitTimeStmt waitTimeStmt) {
        waitTimeStmt.time.accept(this);
        JSONObject o = makeNode(C.WAIT_TIME_STMT);
        return app(o);
    }

    @Override
    public final Void visitLocation(Location location) {
        throw new DbcException("Operation not supported");
    }

    @Override
    public final Void visitTextPrintFunct(TextPrintFunct textPrintFunct) {
        return null;
    }

    @Override
    public final Void visitStmtTextComment(StmtTextComment textComment) {
        JSONObject o;
        o = makeNode(C.COMMENT).put(C.VALUE, textComment.textComment);
        return app(o);
    }

    @Override
    public final Void visitFunctionStmt(FunctionStmt functionStmt) {
        functionStmt.function.accept(this);
        return null;
    }

    @Override
    public final Void visitFunctionExpr(FunctionExpr functionExpr) {
        functionExpr.getFunction().accept(this);
        return null;
    }

    @Override
    public final Void visitGetSubFunct(GetSubFunct getSubFunct) {
        getSubFunct.param.forEach(x -> x.accept(this));

        JSONObject o =
            makeNode(C.EXPR)
                .put(C.EXPR, C.LIST_OPERATION)
                .put(C.OP, C.LIST_GET_SUBLIST)
                .put(C.POSITION, getSubFunct.strParam.stream().map(x -> x.toString().toLowerCase()).toArray());

        return app(o);
    }

    @Override
    public final Void visitIndexOfFunct(IndexOfFunct indexOfFunct) {
        indexOfFunct.param.forEach(x -> x.accept(this));
        JSONObject o =
            makeNode(C.EXPR)
                .put(C.EXPR, C.LIST_OPERATION)
                .put(C.OP, C.LIST_FIND_ITEM)
                .put(C.POSITION, indexOfFunct.location.toString().toLowerCase());
        return app(o);
    }

    @Override
    public final Void visitLengthOfIsEmptyFunct(LengthOfIsEmptyFunct lengthOfIsEmptyFunct) {
        lengthOfIsEmptyFunct.param.get(0).accept(this);
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, C.LIST_OPERATION).put(C.OP, lengthOfIsEmptyFunct.functName.toString().toLowerCase());
        return app(o);
    }

    @Override
    public final Void visitListCreate(ListCreate listCreate) {
        listCreate.exprList.accept(this);
        int n = listCreate.exprList.get().size();

        JSONObject o = makeNode(C.EXPR).put(C.EXPR, C.CREATE_LIST).put(C.NUMBER, n);
        return app(o);
    }

    @Override
    public final Void visitListSetIndex(ListSetIndex listSetIndex) {
        listSetIndex.param.forEach(x -> x.accept(this));
        JSONObject o =
            makeNode(C.LIST_OPERATION)
                .put(C.OP, listSetIndex.mode.toString().toLowerCase())
                .put(C.POSITION, listSetIndex.location.toString().toLowerCase());
        return app(o);
    }

    @Override
    public final Void visitListGetIndex(ListGetIndex listGetIndex) {
        listGetIndex.param.forEach(x -> x.accept(this));
        JSONObject o =
            makeNode(C.EXPR)
                .put(C.EXPR, C.LIST_OPERATION)
                .put(C.OP, listGetIndex.getElementOperation().toString().toLowerCase())
                .put(C.POSITION, listGetIndex.location.toString().toLowerCase());
        return app(o);
    }

    @Override
    public final Void visitListRepeat(ListRepeat listRepeat) {
        listRepeat.param.forEach(x -> x.accept(this));
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, C.CREATE_LIST_REPEAT);
        return app(o);
    }

    @Override
    public final Void visitMathConstrainFunct(MathConstrainFunct mathConstrainFunct) {
        mathConstrainFunct.param.get(0).accept(this);
        mathConstrainFunct.param.get(1).accept(this);
        mathConstrainFunct.param.get(2).accept(this);
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, C.MATH_CONSTRAIN_FUNCTION);
        return app(o);
    }

    @Override
    public final Void visitMathNumPropFunct(MathNumPropFunct mathNumPropFunct) {
        mathNumPropFunct.param.get(0).accept(this);
        if ( mathNumPropFunct.functName == FunctionNames.DIVISIBLE_BY ) {
            mathNumPropFunct.param.get(1).accept(this);
        }
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, C.MATH_PROP_FUNCT).put(C.OP, mathNumPropFunct.functName);
        return app(o);
    }

    @Override
    public final Void visitMathOnListFunct(MathOnListFunct mathOnListFunct) {
        mathOnListFunct.param.forEach(x -> x.accept(this));
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, C.MATH_ON_LIST).put(C.OP, mathOnListFunct.functName.toString().toLowerCase());
        return app(o);
    }

    @Override
    public final Void visitMathRandomFloatFunct(MathRandomFloatFunct mathRandomFloatFunct) {
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, C.RANDOM_DOUBLE);
        return app(o);
    }

    @Override
    public final Void visitMathRandomIntFunct(MathRandomIntFunct mathRandomIntFunct) {
        mathRandomIntFunct.param.get(0).accept(this);
        mathRandomIntFunct.param.get(1).accept(this);
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, C.RANDOM_INT);
        return app(o);
    }

    @Override
    public final Void visitMathSingleFunct(MathSingleFunct mathSingleFunct) {
        mathSingleFunct.param.get(0).accept(this);
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, C.SINGLE_FUNCTION).put(C.OP, mathSingleFunct.functName);
        return app(o);
    }

    @Override
    public Void visitMathCastStringFunct(MathCastStringFunct mathCastStringFunct) {
        mathCastStringFunct.param.get(0).accept(this);
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, C.CAST_STRING);
        return app(o);
    }

    @Override
    public Void visitMathCastCharFunct(MathCastCharFunct mathCastCharFunct) {
        mathCastCharFunct.param.get(0).accept(this);
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, C.CAST_CHAR);
        return app(o);
    }

    @Override
    public Void visitTextStringCastNumberFunct(TextStringCastNumberFunct textStringCastNumberFunct) {
        textStringCastNumberFunct.param.get(0).accept(this);
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, C.CAST_STRING_NUMBER);
        return app(o);
    }

    @Override
    public Void visitTextCharCastNumberFunct(TextCharCastNumberFunct textCharCastNumberFunct) {
        textCharCastNumberFunct.param.get(0).accept(this);
        textCharCastNumberFunct.param.get(1).accept(this);
        JSONObject o = makeNode(C.EXPR).put(C.EXPR, C.CAST_CHAR_NUMBER);
        return app(o);
    }

    @Override
    public final Void visitTextJoinFunct(TextJoinFunct textJoinFunct) {
        textJoinFunct.param.accept(this);
        int n = textJoinFunct.param.get().size();
        JSONObject o = makeNode(C.TEXT_JOIN).put(C.NUMBER, n);
        return app(o);
    }

    @Override
    public final Void visitMethodVoid(MethodVoid methodVoid) {
        jumpLinker.isolate(() -> {
            registerMethodDeclaration(methodVoid.getMethodName());
            appComment(C.METHOD_VOID, true);

            // Start of method
            ExprList parameters = methodVoid.getParameters();
            if ( !parameters.get().stream().allMatch(parameter -> parameter instanceof VarDeclaration) ) {
                throw new DbcException(String.format("Expected %s to be a list of VarDeclarations", parameters.get()));
            }

            Lists.reverse(parameters.get())
                .stream()
                .map(parameter -> (VarDeclaration) parameter)
                .forEach(parameter -> {
                    JSONObject o = makeNode(C.VAR_DECLARATION).put(C.TYPE, parameter.typeVar).put(C.NAME, parameter.name);
                    app(o);
                });

            methodVoid.body.accept(this);


            jumpLinker.handle(METHOD_END).forEach(statement -> {
                statement.put(C.TARGET, opArray.size());
                removeOpenBlocksFromUnhighlight(statement, methodVoid.getProperty().getBlocklyId());
            });

            parameters.get().stream()
                .map(parameter -> (VarDeclaration) parameter)
                .forEach(parameter -> app(makeNode(C.UNBIND_VAR).put(C.NAME, parameter.name)));

            app(makeNode(C.RETURN).put(C.VALUES, false));
            appComment(C.METHOD_VOID, false);
        });
        return null;
    }


    @Override
    public final Void visitMethodReturn(MethodReturn methodReturn) {
        jumpLinker.isolate(() -> {
            registerMethodDeclaration(methodReturn.getMethodName());
            appComment(C.METHOD_RETURN, true);

            // Start of method
            ExprList parameters = methodReturn.getParameters();
            if ( !parameters.get().stream().allMatch(parameter -> parameter instanceof VarDeclaration) ) {
                throw new DbcException(String.format("Expected %s to be a list of VarDeclarations", parameters.get()));
            }

            Lists.reverse(parameters.get())
                .stream()
                .map(parameter -> (VarDeclaration) parameter)
                .forEach(parameter -> {
                    JSONObject o = makeNode(C.VAR_DECLARATION).put(C.TYPE, parameter.typeVar).put(C.NAME, parameter.name);
                    app(o);
                });

            methodReturn.body.accept(this);

            methodReturn.returnValue.accept(this);

            jumpLinker.handle(METHOD_END).forEach(statement -> {
                statement.put(C.TARGET, opArray.size());
                removeOpenBlocksFromUnhighlight(statement, methodReturn.getProperty().getBlocklyId());
            });

            parameters.get().stream()
                .map(parameter -> (VarDeclaration) parameter)
                .forEach(parameter -> app(makeNode(C.UNBIND_VAR).put(C.NAME, parameter.name)));

            app(makeNode(C.RETURN).put(C.VALUES, true));
            appComment(C.METHOD_RETURN, false);
        });
        return null;
    }

    @Override
    public final Void visitMethodIfReturn(MethodIfReturn methodIfReturn) {
        appComment(C.IF_RETURN, true);

        methodIfReturn.oraCondition.accept(this);
        JSONObject jumpOverReturn = makeNode(C.JUMP).put(C.CONDITIONAL, false);
        app(jumpOverReturn);

        methodIfReturn.oraReturnValue.accept(this);
        JSONObject jumpToMethodEnd = makeNode(C.JUMP).put(C.CONDITIONAL, C.ALWAYS);
        addHighlightingToJump(jumpToMethodEnd);
        jumpLinker.register(jumpToMethodEnd, METHOD_END);

        app(jumpToMethodEnd);
        jumpOverReturn.put(C.TARGET, opArray.size());

        appComment(C.IF_RETURN, false);
        return null;
    }

    @Override
    public final Void visitMethodStmt(MethodStmt methodStmt) {
        methodStmt.method.accept(this);
        return null;
    }

    @Override
    public final Void visitMethodCall(MethodCall methodCall) {
        appComment(C.METHOD_CALL, true);

        JSONObject returnAddress = makeNode(C.EXPR).put(C.EXPR, C.NUM_CONST);
        app(returnAddress);
        methodCall.getParametersValues().get()
            .forEach(v -> v.accept(this));
        app(createJumpToMethod(methodCall));
        returnAddress.put(C.VALUE, opArray.size());
        appComment(C.METHOD_CALL, false);
        return null;
    }

    @Override
    public Void visitConnectConst(ConnectConst connectConst) {
        throw new DbcException("Operation not supported");
    }

    @SuppressWarnings("unchecked")
    @Override
    public Void visitAssertStmt(AssertStmt assertStmt) {
        assertStmt.asserts.accept(this);
        ((Binary) assertStmt.asserts).left.accept((IVisitor) this);
        ((Binary) assertStmt.asserts).getRight().accept((IVisitor) this);
        String op = ((Binary) assertStmt.asserts).op.toString();
        JSONObject o = makeNode(C.ASSERT_ACTION).put(C.MSG, assertStmt.msg).put(C.OP, op);
        return app(o);
    }

    @Override
    public Void visitDebugAction(DebugAction debugAction) {
        debugAction.value.accept(this);
        JSONObject o = makeNode(C.DEBUG_ACTION);
        return app(o);
    }

    @Override
    public Void visitSerialWriteAction(SerialWriteAction serialWriteAction) {
        serialWriteAction.value.accept(this);
        JSONObject o = makeNode(C.SERIAL_WRITE_ACTION);
        return app(o);
    }

    private void addHighlightingToJump(JSONObject o) {
        if ( !debugger ) {
            return;
        }
        o.put(C.HIGHTLIGHT_MINUS, new ArrayList<>(openBlocks));
    }

    private void addDebugStatement(Phrase phrase) {
        if ( debugger ) {
            possibleDebugStops.add(phrase.getProperty().getBlocklyId());
            app(makeNode(C.COMMENT));
        }
    }

    private Void appComment(Object commentType, boolean isStart) {
        return app(makeNode(C.COMMENT).put(C.TARGET, commentType).put(C.TYPE, isStart ? C.START : C.END));
    }

    private void removeOpenBlocksFromUnhighlight(JSONObject statement) {
        removeOpenBlocksFromUnhighlight(statement, null);
    }

    private void removeOpenBlocksFromUnhighlight(JSONObject statement, String repeatId) {
        if ( !debugger ) {
            return;
        }

        if ( !statement.has(C.HIGHTLIGHT_MINUS) ) {
            throw new DbcException("Jump is missing a Highlight Minus");
        }

        List<Object> newUnhighlight = statement.getJSONArray(C.HIGHTLIGHT_MINUS).toList().stream()
            .filter(id -> !openBlocks.contains(id) || id.equals(repeatId))
            .collect(Collectors.toList());
        statement.put(C.HIGHTLIGHT_MINUS, newUnhighlight);
    }

    private JSONObject createJumpToMethod(MethodCall methodCall) {
        JSONObject jump = makeNode(C.JUMP).put(C.CONDITIONAL, C.ALWAYS);
        methodCalls.putIfAbsent(methodCall.getMethodName(), new ArrayList<>());
        methodCalls.get(methodCall.getMethodName()).add(jump);
        return jump;
    }

    private void registerMethodDeclaration(String methodName) {
        methodDeclarations.put(methodName, opArray.size());
    }

    private List<JSONObject> getRegisteredMethodCalls(String methodName) {
        return methodCalls.getOrDefault(methodName, new ArrayList<>());
    }

    protected final ConfigurationComponent getConfigurationComponent(String userDefinedName) {
        ConfigurationComponent configurationComponent = this.configuration.getConfigurationComponent(userDefinedName);
        return configurationComponent;
    }

    /**
     * Processes the optional duration and adds it to the stack if it is available.
     *
     * @param duration the duration, may be null, represents distance/duration/degrees depending on the method
     * @return whether the duration was pushed to the the stack
     */
    protected final boolean processOptionalDuration(MotorDuration duration) {
        if ( duration != null ) {
            duration.getValue().accept(this);
            return true;
        } else {
            return false;
        }
    }

    private void bindMethods() {
        methodDeclarations
            .forEach((methodName, address) -> getRegisteredMethodCalls(methodName)
                .forEach(jsonObject -> jsonObject.put(C.TARGET, address)));
    }

    protected final DriveDirection getDriveDirection(boolean isReverse) {
        return isReverse ? DriveDirection.BACKWARD : DriveDirection.FOREWARD;
    }

    protected final TurnDirection getTurnDirection(boolean isReverse) {
        return isReverse ? TurnDirection.RIGHT : TurnDirection.LEFT;
    }

    public final void generateCodeFromPhrases(List<List<Phrase>> phrasesSet) {
        List<Phrase> methods = new ArrayList<>();
        for ( List<Phrase> phrases : phrasesSet ) {
            for ( Phrase phrase : phrases ) {
                boolean isMethod = phrase instanceof MethodVoid || phrase instanceof MethodReturn;
                if ( !isMethod ) {
                    phrase.accept(this);
                    continue;
                }
                methods.add(phrase);
            }
        }
        app(makeNode(C.STOP));
        for ( Phrase method : methods ) {
            method.accept(this);
        }
        bindMethods();
    }

    protected final JSONObject makeNode(String opCode) {
        JSONObject operation = new JSONObject().put(C.OPCODE, opCode);
        if ( !toInitiateBlocks.isEmpty() ) {
            toInitiateBlocks.removeIf(IS_INVALID_BLOCK_ID);
            operation.put(C.HIGHTLIGHT_PLUS, new ArrayList<>(toInitiateBlocks));
            toInitiateBlocks.clear();
        }
        if ( !possibleDebugStops.isEmpty() ) {
            possibleDebugStops.removeIf(IS_INVALID_BLOCK_ID);
            operation.put(C.POSSIBLE_DEBUG_STOP, new ArrayList<>(possibleDebugStops));
            possibleDebugStops.clear();
        }
        return operation;
    }

    protected Void app(JSONObject o) {
        this.getOpArray().add(o);
        return null;
    }

    protected final void generateProgramPrefix(boolean withWrapping) {
        // nothing to do
    }

    public List<JSONObject> getOpArray() {
        return this.opArray;
    }

    private boolean isValidBlocklyId(String blocklyId) {
        return !blocklyId.trim().equals("1");
    }
}
