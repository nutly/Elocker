package com.feiyang.elocker;

import com.google.gson.JsonObject;

import java.io.IOException;


public class Test {
    public static void main(String[] args) throws IOException {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("test1", "test1");

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