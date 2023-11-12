package bg.codeacademy.spring.gossiptalks.validators.oldpasswordvalidator;

import bg.codeacademy.spring.gossiptalks.validators.passwordvalidator.PasswordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = OldPasswordValidator.class)
public @interface OldPassword
{
  String message() default "Old password doesn't match your current password!";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
