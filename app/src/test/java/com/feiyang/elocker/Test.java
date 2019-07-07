package com.feiyang.elocker;

import java.io.IOException;
import java.util.TreeSet;


public class Test {
    public static void main(String[] args) throws IOException {
        TreeSet<Integer> test = new TreeSet<Integer>();
        test.add(3);
        test.add(1);
        test.add(3);
        test.add(3);
        test.remove(3);
        System.out.println(test.toString().replace("[", ""));
    }

    public enum Operation {
        Open("Open1"), Close("Close1");
        private String description;

        Operation(String description) {

            this.description = description;

        }

        public static Operation from(String value) {
            Operation operation;
            try {
                operation = Operation.valueOf(value);
            } catch (Exception e) {
                operation = Operation.Close;
            }
            return operation;
        }

        @Override
        public String toString() {
            return this.description;
        }
    }

}