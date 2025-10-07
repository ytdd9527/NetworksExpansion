package com.balugaq.netex.api.algorithm;

import org.jspecify.annotations.NullMarked;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * @author balugaq
 */
@NullMarked
public class Calculator {
    private static final Map<String, Integer> PRIORITY = new HashMap<>();

    static {
        PRIORITY.put("~", 4);
        PRIORITY.put("!", 4);

        PRIORITY.put("&", 3);
        PRIORITY.put("^", 3);
        PRIORITY.put("|", 3);
        PRIORITY.put("<<", 3);
        PRIORITY.put(">>", 3);

        PRIORITY.put("*", 2);
        PRIORITY.put("/", 2);
        PRIORITY.put("+", 1);
        PRIORITY.put("-", 1);
        PRIORITY.put("(", 0);
    }

    @SuppressWarnings("DuplicatedCode")
    public static BigDecimal calculate(String expression) throws NumberFormatException {
        if (expression == null || expression.trim().isEmpty()) {
            throw new NumberFormatException("表达式为空");
        }

        String expr = replaceBrackets(expression);

        expr = expr.replaceAll("\\s+", "");

        expr = completeParentheses(expr);

        if (!isValidParentheses(expr)) {
            throw new NumberFormatException("括号无法自动补全为有效表达式");
        }

        Deque<BigDecimal> numStack = new ArrayDeque<>();
        Deque<String> opStack = new ArrayDeque<>();

        int i = 0;
        int n = expr.length();

        while (i < n) {
            char c = expr.charAt(i);

            if (Character.isDigit(c) || c == '.') {
                int j = i;
                while (j < n && (Character.isDigit(expr.charAt(j)) || expr.charAt(j) == '.')) {
                    j++;
                }

                boolean isPercentage = false;
                if (j < n && expr.charAt(j) == '%') {
                    isPercentage = true;
                    j++;
                }

                String numStr = expr.substring(i, j - (isPercentage ? 1 : 0));
                BigDecimal num = parseNumber(numStr);

                if (isPercentage) {
                    num = num.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
                }

                numStack.push(num);
                i = j;
            }
            else if (c == '(') {
                opStack.push(String.valueOf(c));
                i++;
            }
            else if (c == ')') {
                String pk = opStack.peek();
                if (pk == null) {
                    throw new NumberFormatException("括号不匹配: " + expression);
                }
                
                while (!pk.equals("(")) {
                    calculateTop(numStack, opStack);
                }
                opStack.pop();                i++;
            }
            else if ((c == '<' || c == '>') && i + 1 < n && expr.charAt(i + 1) == c) {
                String op = expr.substring(i, i + 2);
                while (!opStack.isEmpty() && getPriority(opStack.peek()) >= getPriority(op)) {
                    calculateTop(numStack, opStack);
                }
                opStack.push(op);
                i += 2;
            }
            else if (c == '~' || c == '!') {
                String op = String.valueOf(c);
                opStack.push(op);
                i++;
            }
            else if (isOperator(String.valueOf(c))) {
                if (c == '+' && (i == 0 || expr.charAt(i - 1) == '(' || isOperator(String.valueOf(expr.charAt(i - 1))))) {
                    if (i + 1 >= n || (!Character.isDigit(expr.charAt(i + 1)) && expr.charAt(i + 1) != '.')) {
                        throw new NumberFormatException("无效的正号位置: " + expression);
                    }

                    int j = i + 1;
                    while (j < n && (Character.isDigit(expr.charAt(j)) || expr.charAt(j) == '.')) {
                        j++;
                    }

                    boolean isPercentage = false;
                    if (j < n && expr.charAt(j) == '%') {
                        isPercentage = true;
                        j++;
                    }

                    String numStr = expr.substring(i + 1, j - (isPercentage ? 1 : 0));
                    BigDecimal num = parseNumber(numStr);

                    if (isPercentage) {
                        num = num.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
                    }

                    numStack.push(num);
                    i = j;
                }
                else if (c == '-' && (i == 0 || expr.charAt(i - 1) == '(' || isOperator(String.valueOf(expr.charAt(i - 1))))) {
                    if (i + 1 >= n || (!Character.isDigit(expr.charAt(i + 1)) && expr.charAt(i + 1) != '.')) {
                        throw new NumberFormatException("无效的负号位置: " + expression);
                    }

                    int j = i + 1;
                    while (j < n && (Character.isDigit(expr.charAt(j)) || expr.charAt(j) == '.')) {
                        j++;
                    }

                    boolean isPercentage = false;
                    if (j < n && expr.charAt(j) == '%') {
                        isPercentage = true;
                        j++;
                    }

                    String numStr = "-" + expr.substring(i + 1, j - (isPercentage ? 1 : 0));
                    BigDecimal num = parseNumber(numStr);

                    if (isPercentage) {
                        num = num.divide(new BigDecimal("100"), 10, RoundingMode.HALF_UP);
                    }

                    numStack.push(num);
                    i = j;
                } else {
                    String op = String.valueOf(c);
                    while (!opStack.isEmpty() && getPriority(opStack.peek()) >= getPriority(op)) {
                        calculateTop(numStack, opStack);
                    }
                    opStack.push(op);
                    i++;
                }
            }
            else {
                throw new NumberFormatException("无效字符: " + c);
            }
        }

        while (!opStack.isEmpty()) {
            calculateTop(numStack, opStack);
        }

        if (numStack.size() != 1) {
            throw new NumberFormatException("无效的表达式: " + expression);
        }

        return numStack.pop();
    }

    @SuppressWarnings("RegExpRedundantEscape")
    private static String replaceBrackets(String expr) {
        return expr
            .replaceAll("[\\[\\{（【《]", "(")
            .replaceAll("[\\]\\}）】》]", ")");
    }

    private static String completeParentheses(String expr) {
        int openCount = 0;
        int closeCount = 0;

        for (char c : expr.toCharArray()) {
            if (c == '(') {
                openCount++;
            } else if (c == ')') {
                closeCount++;
            }
        }

        int diff = openCount - closeCount;

        if (diff > 0) {
            expr = expr + ")".repeat(diff);
        }
        else if (diff < 0) {
            expr = "(".repeat(Math.max(0, -diff)) + expr;
        }

        return expr;
    }

    private static BigDecimal parseNumber(String numStr) throws NumberFormatException {
        try {
            if (numStr.endsWith(".")) {
                numStr += "0";
            }
            if (numStr.startsWith(".") && numStr.length() > 1) {
                numStr = "0" + numStr;
            }
            return new BigDecimal(numStr);
        } catch (NumberFormatException e) {
            throw new NumberFormatException("无效的数字格式: " + numStr);
        }
    }

    private static void calculateTop(Deque<BigDecimal> numStack, Deque<String> opStack) throws NumberFormatException {
        String op = opStack.pop();

        if (op.equals("~") || op.equals("!")) {
            if (numStack.isEmpty()) {
                throw new NumberFormatException("无效的表达式，单目运算符缺少操作数");
            }

            BigDecimal a = numStack.pop();
            long aLong = a.longValue();
            long resultLong = switch (op) {
                case "~" ->                    ~aLong;
                case "!" ->                    (aLong == 0) ? 1 : 0;
                default -> throw new NumberFormatException("无效的单目运算符: " + op);
            };

            numStack.push(new BigDecimal(resultLong));
            return;
        }

        if (numStack.size() < 2) {
            throw new NumberFormatException("无效的表达式，双目运算符缺少操作数");
        }

        BigDecimal b = numStack.pop();
        BigDecimal a = numStack.pop();
        BigDecimal result;

        switch (op) {
            case "+":
                result = a.add(b);
                break;
            case "-":
                result = a.subtract(b);
                break;
            case "*":
                result = a.multiply(b);
                break;
            case "/":
                if (b.compareTo(BigDecimal.ZERO) == 0) {
                    throw new ArithmeticException("除数不能为0");
                }
                result = a.divide(b, 10, RoundingMode.HALF_UP);
                break;
            case "&":
                long aLongAnd = a.longValue();
                long bLongAnd = b.longValue();
                result = new BigDecimal(aLongAnd & bLongAnd);
                break;
            case "|":
                long aLongOr = a.longValue();
                long bLongOr = b.longValue();
                result = new BigDecimal(aLongOr | bLongOr);
                break;
            case "^":
                long aLongXor = a.longValue();
                long bLongXor = b.longValue();
                result = new BigDecimal(aLongXor ^ bLongXor);
                break;
            case "<<":
                long aLongLsh = a.longValue();
                long bLongLsh = b.longValue();
                result = new BigDecimal(aLongLsh << bLongLsh);
                break;
            case ">>":
                long aLongRsh = a.longValue();
                long bLongRsh = b.longValue();
                result = new BigDecimal(aLongRsh >> bLongRsh);
                break;
            default:
                throw new NumberFormatException("无效的运算符: " + op);
        }

        numStack.push(result);
    }

    private static int getPriority(String op) {
        return PRIORITY.getOrDefault(op, 0);
    }

    private static boolean isValidParentheses(String expr) {
        Deque<Character> stack = new ArrayDeque<>();
        for (char c : expr.toCharArray()) {
            if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                if (stack.isEmpty() || stack.pop() != '(') {
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }

    private static boolean isOperator(String op) {
        return PRIORITY.containsKey(op);
    }
}
