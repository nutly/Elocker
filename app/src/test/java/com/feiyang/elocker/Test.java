package com.feiyang.elocker;

import java.io.IOException;
import java.util.HashMap;


public class Test {
    public static void main(String[] args) throws IOException {
        HashMap<String, Integer> map = new HashMap<String, Integer>();
        for (int i = 1; i < 6; i++)
            map.put("test" + i, i);
        for (String key : map.keySet()) {
            System.out.println(key + "##");
        }
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