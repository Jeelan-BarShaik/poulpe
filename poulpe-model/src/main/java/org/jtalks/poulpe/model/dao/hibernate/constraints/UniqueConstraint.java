package org.jtalks.poulpe.model.dao.hibernate.constraints;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.validation.Constraint;
import javax.validation.Payload;

/**
 * Annotation for declaring that in this class there is a constraint on field
 * uniqueness. For marking which fields should be used with {@link UniqueField}.<br>
 * <br>
 * 
 * Example of usage:
 * 
 * <pre>
 * &#064;UniqueConstraint
 * class Component extends Entity {
 *     &#064;UniqueField
 *     private String name;
 *     &#064;UniqueField
 *     private ComponentType componentType;
 *     // getters, setters
 * }
 * </pre>
 * 
 * 
 * @author Tatiana Birina
 * @author Alexey Grigorev
 * 
 * @see UniqueField
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = UniqueConstraintValidator.class)
@Documented
public @interface UniqueConstraint {
    /**
     * attribute message
     * 
     * @return error message
     */
    String message() default "field must be unique";

    /**
     * attribute groups specifies validation groups, to which this constraint
     * belongs
     */
    Class<?>[] groups() default {};

    /**
     * attribute payload assigns custom payload objects to a constraint
     */
    Class<? extends Payload>[] payload() default {};

}