package org.bharat;

public class Main {
    public static void main(String[] args) {
        Jason jason = new Jason();

        jason.addString("first_name", "bharat").addString("last_name", "maheshwari");

        System.out.println(jason.serialize());
    }
}