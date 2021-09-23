package com.xy.common.validate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.HashSet;
import java.util.Set;


//ConstraintValidator<ListValue,Integer> Integer可以是不同类型  这就是需要传进来的类型参数
public class ListValueConstraintValidator implements ConstraintValidator<ListValue,Integer> {

    Set defaultVal = new HashSet();

    @Override
    public void initialize(ListValue constraintAnnotation) {
        int[] d = constraintAnnotation.vals();
        for (Integer  v:d ) {
            defaultVal.add(v);
        }
    }

    @Override
    public boolean isValid(Integer integer, ConstraintValidatorContext constraintValidatorContext) {
        if(!defaultVal.contains(integer)){
            return false;
        }
        return true;
    }
}
