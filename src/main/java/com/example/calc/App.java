package com.example.calc;

public final class App {

    public static void main(String[] args) {
        Calculator calc = new Calculator();
        System.out.println("2 + 3 = " + calc.add(2, 3));
        System.out.println("10 / 4 = " + calc.divide(10, 4));
    }
}
