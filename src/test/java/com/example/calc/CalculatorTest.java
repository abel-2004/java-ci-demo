package com.example.calc;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class CalculatorTest {

    private final Calculator calc = new Calculator();

    @Test
    void add() {
        assertEquals(5, calc.add(2, 3));
    }

    @Test
    void divide() {
        assertEquals(2.5, calc.divide(5, 2));
    }

    @Test
    void divideByZeroThrows() {
        assertThrows(IllegalArgumentException.class, () -> calc.divide(1, 0));
    }
}
