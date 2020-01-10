package com.example.dash;

import com.twitter.sdk.android.core.internal.persistence.PreferenceStore;

import java.lang.reflect.*;

public class UnitTestExtension {
    static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }

    static Object getField(Object object, String fieldName){
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
