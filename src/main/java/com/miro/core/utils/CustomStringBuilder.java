package com.miro.core.utils;

public class CustomStringBuilder {
    private StringBuilder sb;

    public CustomStringBuilder(){
        sb = new StringBuilder();
    }

    public int length(){
        return sb.length();
    }

    public void append(String str)
    {
        sb.append(str != null ? str : "");
    }

    public void appendLine(String str)
    {
        sb.append(str != null ? str : "").append(System.getProperty("line.separator"));
    }

    public String toString()
    {
        return sb.toString();
    }
}
