package mchorse.mclib.math;

import mchorse.mclib.math.functions.Function;
import mchorse.mclib.math.functions.classic.*;
import mchorse.mclib.math.functions.limit.Clamp;
import mchorse.mclib.math.functions.limit.Max;
import mchorse.mclib.math.functions.limit.Min;
import mchorse.mclib.math.functions.rounding.Ceil;
import mchorse.mclib.math.functions.rounding.Floor;
import mchorse.mclib.math.functions.rounding.Round;
import mchorse.mclib.math.functions.rounding.Trunc;
import mchorse.mclib.math.functions.utility.Lerp;
import mchorse.mclib.math.functions.utility.LerpRotate;
import mchorse.mclib.math.functions.utility.Random;
import mchorse.mclib.utils.MathUtils;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MathBuilder {
    public Map<String, Variable> variables = new HashMap<>();

    public Map<String, Class<? extends Function>> functions = new HashMap<>();

    public MathBuilder() {
        register(new Variable("PI", Math.PI));
        register(new Variable("E", Math.E));
        this.functions.put("floor", Floor.class);
        this.functions.put("round", Round.class);
        this.functions.put("ceil", Ceil.class);
        this.functions.put("trunc", Trunc.class);
        this.functions.put("clamp", Clamp.class);
        this.functions.put("max", Max.class);
        this.functions.put("min", Min.class);
        this.functions.put("abs", Abs.class);
        this.functions.put("cos", Cos.class);
        this.functions.put("sin", Sin.class);
        this.functions.put("exp", Exp.class);
        this.functions.put("ln", Ln.class);
        this.functions.put("sqrt", Sqrt.class);
        this.functions.put("mod", Mod.class);
        this.functions.put("pow", Pow.class);
        this.functions.put("lerp", Lerp.class);
        this.functions.put("lerprotate", LerpRotate.class);
        this.functions.put("random", Random.class);
    }

    public void register(Variable variable) {
        this.variables.put(variable.getName(), variable);
    }

    public IValue parse(String expression) throws Exception {
        return parseSymbols(breakdownChars(breakdown(expression)));
    }

    public String[] breakdown(String expression) throws Exception {
        if (!expression.matches("^[\\w\\d\\s_+-/*%^&|<>=!?:.,()]+$"))
            throw new Exception("Given expression '" + expression + "' contains illegal characters!");
        expression = expression.replaceAll("\\s+", "");
        String[] chars = expression.split("(?!^)");
        int left = 0;
        int right = 0;
        for (String s : chars) {
            if (s.equals("(")) {
                left++;
            } else if (s.equals(")")) {
                right++;
            }
        }
        if (left != right)
            throw new Exception("Given expression '" + expression + "' has more uneven amount of parenthesis, there are " + left + " open and " + right + " closed!");
        return chars;
    }

    public List<Object> breakdownChars(String[] chars) {
        List<Object> symbols = new ArrayList();
        String buffer = "";
        int len = chars.length;
        for (int i = 0; i < len; i++) {
            String s = chars[i];
            boolean longOperator = (i > 0 && isOperator(chars[i - 1] + s));
            if (isOperator(s) || longOperator || s.equals(",")) {
                if (s.equals("-")) {
                    int size = symbols.size();
                    boolean isFirst = (size == 0 && buffer.isEmpty());
                    boolean isOperatorBehind = (size > 0 && (isOperator(symbols.get(size - 1)) || symbols.get(size - 1).equals(",")) && buffer.isEmpty());
                    if (isFirst || isOperatorBehind) {
                        buffer = buffer + s;
                        continue;
                    }
                }
                if (longOperator) {
                    s = chars[i - 1] + s;
                    buffer = buffer.substring(0, buffer.length() - 1);
                }
                if (!buffer.isEmpty()) {
                    symbols.add(buffer);
                    buffer = "";
                }
                symbols.add(s);
                continue;
            }
            if (s.equals("(")) {
                if (!buffer.isEmpty()) {
                    symbols.add(buffer);
                    buffer = "";
                }
                int counter = 1;
                for (int j = i + 1; j < len; j++) {
                    String c = chars[j];
                    if (c.equals("(")) {
                        counter++;
                    } else if (c.equals(")")) {
                        counter--;
                    }
                    if (counter == 0) {
                        symbols.add(breakdownChars(buffer.split("(?!^)")));
                        i = j;
                        buffer = "";
                        break;
                    }
                    buffer = buffer + c;
                }
            } else {
                buffer = buffer + s;
            }
            continue;
        }
        if (!buffer.isEmpty())
            symbols.add(buffer);
        return symbols;
    }

    public IValue parseSymbols(List<Object> symbols) throws Exception {
        IValue ternary = tryTernary(symbols);
        if (ternary != null)
            return ternary;
        int size = symbols.size();
        if (size == 1)
            return valueFromObject(symbols.get(0));
        if (size == 2) {
            Object first = symbols.get(0);
            Object second = symbols.get(1);
            if ((isVariable(first) || first.equals("-")) && second instanceof List)
                return createFunction((String) first, (List<Object>) second);
        }
        int firstOp = -1;
        int secondOp = -1;
        for (int i = 0; i < size; i++) {
            Object o = symbols.get(i);
            if (isOperator(o))
                if (firstOp == -1) {
                    firstOp = i;
                } else {
                    secondOp = i;
                    break;
                }
        }
        Operation op = operationForOperator((String) symbols.get(firstOp));
        if (secondOp == -1) {
            IValue left = parseSymbols(symbols.subList(0, firstOp));
            IValue right = parseSymbols(symbols.subList(firstOp + 1, MathUtils.clamp(firstOp + 3, 0, size)));
            return new Operator(op, left, right);
        }
        if (secondOp > firstOp) {
            Operation compareTo = operationForOperator((String) symbols.get(secondOp));
            IValue left = parseSymbols(symbols.subList(0, firstOp));
            if (compareTo.value > op.value)
                return new Operator(op, left, parseSymbols(symbols.subList(firstOp + 1, size)));
            IValue right = parseSymbols(symbols.subList(firstOp + 1, secondOp));
            return new Operator(compareTo, new Operator(op, left, right), parseSymbols(symbols.subList(secondOp + 1, size)));
        }
        throw new Exception("Given symbols couldn't be parsed! " + symbols);
    }

    protected IValue tryTernary(List<Object> symbols) throws Exception {
        int question = -1;
        int questions = 0;
        int colon = -1;
        int colons = 0;
        int size = symbols.size();
        for (int i = 0; i < size; i++) {
            Object object = symbols.get(i);
            if (object instanceof String)
                if (object.equals("?")) {
                    if (question == -1)
                        question = i;
                    questions++;
                } else if (object.equals(":")) {
                    if (colons + 1 == questions && colon == -1)
                        colon = i;
                    colons++;
                }
        }
        if (questions == colons && question > 0 && question + 1 < colon && colon < size - 1)
            return new Ternary(
                    parseSymbols(symbols.subList(0, question)),
                    parseSymbols(symbols.subList(question + 1, colon)),
                    parseSymbols(symbols.subList(colon + 1, size)));
        return null;
    }

    protected IValue createFunction(String first, List<Object> args) throws Exception {
        if (first.equals("!"))
            return new Negate(parseSymbols(args));
        if (first.startsWith("!") && first.length() > 1)
            return new Negate(createFunction(first.substring(1), args));
        if (first.equals("-"))
            return new Negative(parseSymbols(args));
        if (first.startsWith("-") && first.length() > 1)
            return new Negative(createFunction(first.substring(1), args));
        if (!this.functions.containsKey(first))
            throw new Exception("Function '" + first + "' couldn't be found!");
        List<IValue> values = new ArrayList<>();
        List<Object> buffer = new ArrayList();
        for (Object o : args) {
            if (o.equals(",")) {
                values.add(parseSymbols(buffer));
                buffer.clear();
                continue;
            }
            buffer.add(o);
        }
        if (!buffer.isEmpty())
            values.add(parseSymbols(buffer));
        Class<? extends Function> function = this.functions.get(first);
        Constructor<? extends Function> ctor = function.getConstructor(new Class[]{IValue[].class, String.class});
        Function func = ctor.newInstance(new Object[]{values.toArray(new IValue[values.size()]), first});
        return (IValue) func;
    }

    public IValue valueFromObject(Object object) throws Exception {
        if (object instanceof String) {
            String symbol = (String) object;
            if (symbol.startsWith("!"))
                return new Negate(valueFromObject(symbol.substring(1)));
            if (isDecimal(symbol))
                return new Constant(Double.parseDouble(symbol));
            if (isVariable(symbol))
                if (symbol.startsWith("-")) {
                    symbol = symbol.substring(1);
                    Variable value = getVariable(symbol);
                    if (value != null)
                        return new Negative(value);
                } else {
                    IValue value = getVariable(symbol);
                    if (value != null)
                        return value;
                }
        } else if (object instanceof List) {
            return new Group(parseSymbols((List<Object>) object));
        }
        throw new Exception("Given object couldn't be converted to value! " + object);
    }

    protected Variable getVariable(String name) {
        return this.variables.get(name);
    }

    protected Operation operationForOperator(String op) throws Exception {
        for (Operation operation : Operation.values()) {
            if (operation.sign.equals(op))
                return operation;
        }
        throw new Exception("There is no such operator '" + op + "'!");
    }

    protected boolean isVariable(Object o) {
        return (o instanceof String && !isDecimal((String) o) && !isOperator((String) o));
    }

    protected boolean isOperator(Object o) {
        return (o instanceof String && isOperator((String) o));
    }

    protected boolean isOperator(String s) {
        return (Operation.OPERATORS.contains(s) || s.equals("?") || s.equals(":"));
    }

    protected boolean isDecimal(String s) {
        return s.matches("^-?\\d+(\\.\\d+)?$");
    }
}


/* Location:              C:\Users\Tim\AppData\Roaming\.minecraft\mods\temp\Crystal_Client-1.1.16-projectassfucker_1.jar!\mchorse\mclib\math\MathBuilder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */