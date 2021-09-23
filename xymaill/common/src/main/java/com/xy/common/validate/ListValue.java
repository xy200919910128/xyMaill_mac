package com.xy.common.validate;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 自定义校验 需要三部
 * 1，自定义校验注解如下所示：
 * 2，自定义校验解析器 详情参考代码 com.xy.maill.common.validate.ListValueConstraintValidator
 * 3@Constraint(
 *         validatedBy = {ListValueConstraintValidator.class}  在本注解中这个要加上自定义的校验解析器 可以指定多个 通过第二个泛型参数区分即可
 * )
 */



@Documented
@Constraint(
        validatedBy = {ListValueConstraintValidator.class}
)
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.CONSTRUCTOR, ElementType.PARAMETER, ElementType.TYPE_USE})
@Retention(RetentionPolicy.RUNTIME)
public @interface ListValue {

    String message() default "必须提交指定的值";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    int[] vals() default {0,1};
}
