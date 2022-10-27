package com.client.controller;

import java.util.LinkedList;

public class AAATest {
    public static void main(String[] args) {
        LinkedList linkedList = new LinkedList();
        linkedList.add(1);
        linkedList.add(5);
        linkedList.add(2);
        int i = linkedList.indexOf(5);
        System.out.println(i);
    }
}
