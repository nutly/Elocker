package com.feiyang.elocker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class Test {
    public static void main(String[] args) throws IOException {
        String[] menu = {"string1", "string2"};
        List<String> menuList = new ArrayList<String>();
        menuList.addAll(Arrays.asList(menu));
        menuList.remove("string1");
        for (int i = 0; i < menuList.size(); i++) {
            System.out.println(menuList.get(i));
        }
    }
}