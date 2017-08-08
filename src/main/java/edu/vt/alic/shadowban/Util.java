package edu.vt.alic.shadowban;

public class Util {

    private Util() {}

    public static String path(String specifier, String uuid) {
        return "Shadow Banned." + uuid + "." + specifier;
    }
}
