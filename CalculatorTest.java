package dsh;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import static org.junit.jupiter.api.Assertions.*;

/**
 * JUnit5 tests for Calculator class with PEMDAS support
 */
class CalculatorTest {
    
    private Calculator calculator;
    private SymbolTable symbolTable;
    
    @BeforeEach
    void setUp() {
        symbolTable = new SymbolTable();
        calculator = new Calculator(symbolTable);
    }
    
    @Nested
    @DisplayName("Basic Number Tests")
    class BasicNumberTests {
        
        @Test
        @DisplayName("Should parse integers correctly")
        void testParseIntegers() {
            Data result = calculator.evaluate("42");
            assertTrue(result.isNumeric());
            assertEquals(42, result.asInt());
        }
        
        @Test
        @DisplayName("Should parse doubles correctly")
        void testParseDoubles() {
            Data result = calculator.evaluate("3.14");
            assertTrue(result.isNumeric());
            assertEquals(3.14, result.asDouble(), 0.001);
        }
        
        @Test
        @DisplayName("Should parse negative numbers correctly")
        void testParseNegativeNumbers() {
            Data result = calculator.evaluate("-5");
            assertTrue(result.isNumeric());
            assertEquals(-5, result.asInt());
        }
    }
    
    @Nested
    @DisplayName("Basic Arithmetic Tests")
    class BasicArithmeticTests {
        
        @Test
        @DisplayName("Should add two numbers")
        void testAddition() {
            Data result = calculator.evaluate("5 + 3");
            assertEquals(8, result.asInt());
        }
        
        @Test
        @DisplayName("Should subtract two numbers")
        void testSubtraction() {
            Data result = calculator.evaluate("10 - 4");
            assertEquals(6, result.asInt());
        }
        
        @Test
        @DisplayName("Should multiply two numbers")
        void testMultiplication() {
            Data result = calculator.evaluate("7 * 6");
            assertEquals(42, result.asInt());
        }
        
        @Test
        @DisplayName("Should divide two numbers")
        void testDivision() {
            Data result = calculator.evaluate("15 / 3");
            assertEquals(5, result.asInt());
        }
        
        @Test
        @DisplayName("Should handle division with decimals")
        void testDecimalDivision() {
            Data result = calculator.evaluate("10 / 4");
            assertEquals(2.5, result.asDouble(), 0.001);
        }
    }
    
    @Nested
    @DisplayName("PEMDAS Order of Operations Tests")
    class PEMDASTests {
        
        @Test
        @DisplayName("Should follow multiplication before addition (2 + 3 * 4 = 14)")
        void testMultiplicationBeforeAddition() {
            Data result = calculator.evaluate("2 + 3 * 4");
            assertEquals(14, result.asInt());
        }
        
        @Test
        @DisplayName("Should follow division before subtraction (10 - 6 / 2 = 7)")
        void testDivisionBeforeSubtraction() {
            Data result = calculator.evaluate("10 - 6 / 2");
            assertEquals(7, result.asInt());
        }
        
        @Test
        @DisplayName("Should follow left-to-right for same precedence (8 - 3 + 2 = 7)")
        void testLeftToRightSamePrecedence() {
            Data result = calculator.evaluate("8 - 3 + 2");
            assertEquals(7, result.asInt());
        }
        
        @Test
        @DisplayName("Should follow left-to-right for multiplication/division (12 / 3 * 2 = 8)")
        void testLeftToRightMultDiv() {
            Data result = calculator.evaluate("12 / 3 * 2");
            assertEquals(8, result.asInt());
        }
        
        @Test
        @DisplayName("Should handle complex expression (2 + 3 * 4 - 8 / 2 = 10)")
        void testComplexExpression() {
            Data result = calculator.evaluate("2 + 3 * 4 - 8 / 2");
            assertEquals(10, result.asInt());
        }
        
        @Test
        @DisplayName("Should handle another complex expression (15 - 3 * 2 + 8 / 4 = 11)")
        void testAnotherComplexExpression() {
            Data result = calculator.evaluate("15 - 3 * 2 + 8 / 4");
            assertEquals(11, result.asInt());
        }
    }
    
    @Nested
    @DisplayName("Parentheses Tests")
    class ParenthesesTests {
        
        @Test
        @DisplayName("Should handle simple parentheses (2 + 3) * 4 = 20")
        void testSimpleParentheses() {
            Data result = calculator.evaluate("(2 + 3) * 4");
            assertEquals(20, result.asInt());
        }
        
        @Test
        @DisplayName("Should handle nested parentheses ((2 + 3) * 4) - 5 = 15")
        void testNestedParentheses() {
            Data result = calculator.evaluate("((2 + 3) * 4) - 5");
            assertEquals(15, result.asInt());
        }
        
        @Test
        @DisplayName("Should handle multiple parentheses groups (2 + 3) * (4 - 1) = 15")
        void testMultipleParenthesesGroups() {
            Data result = calculator.evaluate("(2 + 3) * (4 - 1)");
            assertEquals(15, result.asInt());
        }
        
        @Test
        @DisplayName("Should remove unnecessary outer parentheses")
        void testRemoveOuterParentheses() {
            Data result = calculator.evaluate("(5 + 3)");
            assertEquals(8, result.asInt());
        }
    }
    
    @Nested
    @DisplayName("Variable Tests")
    class VariableTests {
        
        @Test
        @DisplayName("Should evaluate variables")
        void testVariableEvaluation() {
            symbolTable.set("x", Data.of(10));
            Data result = calculator.evaluate("x");
            assertEquals(10, result.asInt());
        }
        
        @Test
        @DisplayName("Should use variables in expressions")
        void testVariableInExpression() {
            symbolTable.set("x", Data.of(5));
            symbolTable.set("y", Data.of(3));
            Data result = calculator.evaluate("x + y * 2");
            assertEquals(11, result.asInt());
        }
        
        @Test
        @DisplayName("Should handle undefined variables")
        void testUndefinedVariable() {
            Data result = calculator.evaluate("undefinedVar");
            assertTrue(result.isString());
            assertTrue(result.asString().contains("Error: Undefined variable"));
        }
    }
    
    @Nested
    @DisplayName("Error Handling Tests")
    class ErrorHandlingTests {
        
        @Test
        @DisplayName("Should handle empty expressions")
        void testEmptyExpression() {
            Data result = calculator.evaluate("");
            assertTrue(result.asString().contains("Error: Empty expression"));
        }
        
        @Test
        @DisplayName("Should handle whitespace-only expressions")
        void testWhitespaceExpression() {
            Data result = calculator.evaluate("   ");
            assertTrue(result.asString().contains("Error: Empty expression"));
        }
        
        @Test
        @DisplayName("Should handle unbalanced parentheses")
        void testUnbalancedParentheses() {
            Data result = calculator.evaluate("(2 + 3");
            assertTrue(result.asString().contains("Error: Unbalanced parentheses"));
        }
        
        @Test
        @DisplayName("Should handle division by zero")
        void testDivisionByZero() {
            Data result = calculator.evaluate("5 / 0");
            assertTrue(result.asString().contains("Error: Division by zero"));
        }
        
        @Test
        @DisplayName("Should handle invalid expressions")
        void testInvalidExpression() {
            Data result = calculator.evaluate("2 + + 3");
            assertTrue(result.asString().contains("Error:"));
        }
        
        @Test
        @DisplayName("Should handle unknown operators")
        void testUnknownOperator() {
            Data result = calculator.evaluate("2 % 3");
            assertTrue(result.asString().contains("Error:"));
        }
        
        @Test
        @DisplayName("Should handle non-numeric operations")
        void testNonNumericOperations() {
            symbolTable.set("str", Data.of("hello"));
            Data result = calculator.evaluate("str + 5");
            assertTrue(result.asString().contains("Error: Cannot add"));
        }
    }
    
    @Nested
    @DisplayName("Edge Cases")
    class EdgeCaseTests {
        
        @Test
        @DisplayName("Should handle very large numbers")
        void testLargeNumbers() {
            Data result = calculator.evaluate("1000000 + 2000000");
            assertEquals(3000000, result.asInt());
        }
        
        @Test
        @DisplayName("Should handle decimal arithmetic")
        void testDecimalArithmetic() {
            Data result = calculator.evaluate("1.5 + 2.7");
            assertEquals(4.2, result.asDouble(), 0.001);
        }
        
        @Test
        @DisplayName("Should handle mixed integer and decimal arithmetic")
        void testMixedArithmetic() {
            Data result = calculator.evaluate("5 + 2.5");
            assertEquals(7.5, result.asDouble(), 0.001);
        }
        
        @Test
        @DisplayName("Should handle deeply nested parentheses")
        void testDeeplyNestedParentheses() {
            Data result = calculator.evaluate("((((2 + 3) * 2) - 4) / 2)");
            assertEquals(3, result.asInt());
        }
        
        @Test
        @DisplayName("Should handle expression with many operations")
        void testManyOperations() {
            Data result = calculator.evaluate("1 + 2 * 3 + 4 * 5 - 6 / 2");
            // 1 + 6 + 20 - 3 = 24
            assertEquals(24, result.asInt());
        }
    }
}