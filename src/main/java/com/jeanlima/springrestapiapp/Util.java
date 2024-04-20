package com.jeanlima.springrestapiapp;

import com.jeanlima.springrestapiapp.model.Produto;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.math.BigDecimal;

public class Util {

    static public void updateField(Produto produto, String fieldName, Object value) {
        Field field = ReflectionUtils.findField(Produto.class, fieldName);
        if (field == null) {
            throw new RuntimeException("Field '" + fieldName + "' not found on Produto");
        }
        ReflectionUtils.makeAccessible(field);

        try {
            if (BigDecimal.class.equals(field.getType()) && value instanceof Number) {
                field.set(produto, new BigDecimal(value.toString()));
            } else if (field.getType().isAssignableFrom(value.getClass())) {
                field.set(produto, value);
            } else {
                throw new IllegalArgumentException("Cannot set field '" + fieldName + "' to type " + value.getClass().getSimpleName());
            }
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Unable to access field '" + fieldName + "'");
        }
    }
}
