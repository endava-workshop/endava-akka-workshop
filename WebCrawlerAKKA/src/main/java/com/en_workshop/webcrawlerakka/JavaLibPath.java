package com.en_workshop.webcrawlerakka;

/**
 * Created by rpaduraru on 4/15/14.
 */
public class JavaLibPath {

    public static void main(String[] args) {
        String libPathProperty = System.getProperty("java.library.path");
        System.out.println(libPathProperty);
    }
}
