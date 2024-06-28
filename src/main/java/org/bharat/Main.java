package org.bharat;

import org.bharat.jsonObjs.JsonArray;
import org.bharat.jsonObjs.JsonStr;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        JsonStr strobj = new JsonStr("bharat", "maheshwari");
        JsonArray arr = new JsonArray(List.of("name", 1, strobj));

        Jason jason = new Jason();

        System.out.println(jason.serialize(arr));
    }
}