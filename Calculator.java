package dsh;

/**
 * A calculator class for parsing and evaluating expressions including those of env variables.
 * Now supports PEMDAS order of operations.
 * 
 * @author - Ryan Pointer
 * @version - 6/27/25
 */
public class Calculator {
    private final SymbolTable env;

    public Calculator(SymbolTable env) {
        this.env = env;
    }

    public Data evaluate(String expression) {
        try {
            // Remove whitespace and check for empty input
            expression = expression.trim();
            if (expression.isEmpty()) {
                return Data.of("Error: Empty expression");
            }

            // First check for balanced parentheses
            if (!hasBalancedParentheses(expression)) {
                return Data.of("Error: Unbalanced parentheses in expression");
            }

            // Remove outer parentheses if they wrap the entire expression
            while (expression.startsWith("(") && expression.endsWith(")") && 
                   hasBalancedParentheses(expression.substring(1, expression.length() - 1))) {
                expression = expression.substring(1, expression.length() - 1);
            }

            // Check if it's a number
            try {
                if (expression.contains(".")) {
                    return Data.of(Double.parseDouble(expression));
                } else {
                    return Data.of(Integer.parseInt(expression));
                }
            } catch (NumberFormatException e) {
                // Not a number, continue
            }

            // Check if it's a variable reference
            if (isValidVariableName(expression)) {
                Data varValue = env.get(expression);
                if (varValue != null) {
                    return varValue;
                }
                return Data.of("Error: Undefined variable '" + expression + "'");
            }

            // Handle arithmetic operations with PEMDAS
            String[] parts = splitExpressionByPrecedence(expression);
            if (parts != null) {
                Data left = evaluate(parts[0]);
                Data right = evaluate(parts[2]);
                String operator = parts[1];

                if (isError(left)) return left;
                if (isError(right)) return right;

                switch (operator) {
                    case "+": return safeAdd(left, right);
                    case "-": return safeSubtract(left, right);
                    case "*": return safeMultiply(left, right);
                    case "/": return safeDivide(left, right);
                    default: return Data.of("Error: Unknown operator '" + operator + "'");
                }
            }

            return Data.of("Error: Invalid expression '" + expression + "'");
        } catch (Exception e) {
            return Data.of("Error: " + e.getMessage());
        }
    }

    private boolean hasBalancedParentheses(String expr) {
        int balance = 0;
        for (char c : expr.toCharArray()) {
            if (c == '(') balance++;
            if (c == ')') balance--;
            if (balance < 0) return false;
        }
        return balance == 0;
    }

    private boolean isError(Data data) {
        return data != null && data.isString() && data.asString().startsWith("Error:");
    }

    private boolean isValidVariableName(String name) {
        return name.matches("[a-zA-Z][a-zA-Z0-9_]*");
    }

    private Data safeAdd(Data left, Data right) {
        if (!left.isNumeric() || !right.isNumeric()) {
            return Data.of("Error: Cannot add " + typeName(left) + " and " + typeName(right));
        }
        return left.add(right);
    }

    private Data safeSubtract(Data left, Data right) {
        if (!left.isNumeric() || !right.isNumeric()) {
            return Data.of("Error: Cannot subtract " + typeName(left) + " and " + typeName(right));
        }
        return left.subtract(right);
    }

    private Data safeMultiply(Data left, Data right) {
        if (!left.isNumeric() || !right.isNumeric()) {
            return Data.of("Error: Cannot multiply " + typeName(left) + " and " + typeName(right));
        }
        return left.multiply(right);
    }

    private Data safeDivide(Data left, Data right) {
        if (!left.isNumeric() || !right.isNumeric()) {
            return Data.of("Error: Cannot divide " + typeName(left) + " and " + typeName(right));
        }
        if (right.asDouble() == 0) {
            return Data.of("Error: Division by zero");
        }
        return left.divide(right);
    }

    private String typeName(Data data) {
        if (data == null) return "null";
        return data.getType().toString().toLowerCase();
    }

    /**
     * Split expression by operator precedence (PEMDAS).
     * Returns the lowest precedence operator found outside parentheses.
     */
    private String[] splitExpressionByPrecedence(String expression) {
        // Find operators by precedence (lowest to highest)
        // Addition and subtraction have lowest precedence
        String[] lowPrecedenceOps = {"+", "-"};
        String[] highPrecedenceOps = {"*", "/"};
        
        // First look for + and - (lowest precedence)
        for (String op : lowPrecedenceOps) {
            int index = findOperatorIndex(expression, op);
            if (index > 0) {
                return new String[]{
                    expression.substring(0, index),
                    op,
                    expression.substring(index + 1)
                };
            }
        }
        
        // Then look for * and / (higher precedence)
        for (String op : highPrecedenceOps) {
            int index = findOperatorIndex(expression, op);
            if (index > 0) {
                return new String[]{
                    expression.substring(0, index),
                    op,
                    expression.substring(index + 1)
                };
            }
        }
        
        return null;
    }

    /**
     * Find the rightmost occurrence of an operator at parentheses level 0.
     * This ensures left-to-right evaluation for operators of the same precedence.
     */
    private int findOperatorIndex(String expression, String operator) {
        int parenLevel = 0;
        int lastIndex = -1;
        
        for (int i = 0; i < expression.length(); i++) {
            char c = expression.charAt(i);
            if (c == '(') {
                parenLevel++;
            } else if (c == ')') {
                parenLevel--;
            } else if (parenLevel == 0 && operator.equals(String.valueOf(c))) {
                // Make sure it's not at the beginning or end of the expression
                if (i > 0 && i < expression.length() - 1) {
                    lastIndex = i;
                }
            }
        }
        
        return lastIndex;
    }
}