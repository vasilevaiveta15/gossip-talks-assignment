package bg.codeacademy.spring.gossiptalks.validators.passwordvalidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = PasswordValidator.class)
public @interface Password
{
  String message() default "Password must contain upper case, lower case letters, " +
      "a special symbol, a digit, no whitespaces and be from 8 to 64 characters long";

  Class<?>[] groups() default {};

  Class<? extends Payload>[] payload() default {};
}
