package com.miro.core.exceptions;

public class WidgetNotFoundException extends  Exception {

    public WidgetNotFoundException(){

    }

    public WidgetNotFoundException(String message)
    {
        super(message);
    }
}
