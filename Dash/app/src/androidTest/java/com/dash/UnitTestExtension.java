package com.dash;

import java.lang.reflect.Field;

//Helpfull extensions for unit tests
public class UnitTestExtension {
    //Functions to get private access to an object.
    static Object getField(Object object, String fieldName) {
        Object fieldToGet = null;
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            try {
                fieldToGet = field.get(object);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return fieldToGet;
    }
}