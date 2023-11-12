package bg.codeacademy.spring.gossiptalks.validators.passwordvalidator;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PasswordValidator implements ConstraintValidator<Password, String>
{
  @Override
  public void initialize(Password constraintAnnotation)
  {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(String password, ConstraintValidatorContext constraintValidatorContext)
  {
    if (password == null) {
      getViolation(constraintValidatorContext, "Password can't be null");
      return false;
    }

    boolean containsUpperCaseLetter = false;
    boolean containsLowerCaseLetter = false;
    boolean containsDigit = false;
    boolean containsWhiteSpace = false;
    boolean containsSpecialSymbol = password.matches(".*[^A-Za-z0-9 ].*");
    boolean isWithinSizeBounds = password.length() >= 6 && password.length() <= 64;

    if (!isWithinSizeBounds) {
      getViolation(constraintValidatorContext, "Password must be from 6 to 64 characters long");
    }

    for (char c : password.toCharArray()) {
      if (Character.isUpperCase(c)) {
        containsUpperCaseLetter = true;
      }
      if (Character.isLowerCase(c)) {
        containsLowerCaseLetter = true;
      }
      if (Character.isDigit(c)) {
        containsDigit = true;
      }
      if (Character.isWhitespace(c)) {
        containsWhiteSpace = true;
      }
    }

    if (!containsUpperCaseLetter) {
      getViolation(constraintValidatorContext, "Password must contain upper case letter");
    }
    if (!containsLowerCaseLetter) {
      getViolation(constraintValidatorContext, "Password must contain lower case letter");
    }
    if (!containsDigit) {
      getViolation(constraintValidatorContext, "Password must contain a digit");
    }
    if (!containsSpecialSymbol) {
      getViolation(constraintValidatorContext, "Password must contain a special symbol");
    }
    if (containsWhiteSpace) {
      getViolation(constraintValidatorContext, "Password must NOT contain whitespaces");
    }

    return containsDigit
        && containsLowerCaseLetter
        && containsUpperCaseLetter
        && containsSpecialSymbol
        && !containsWhiteSpace
        && isWithinSizeBounds;
  }

  private void getViolation(ConstraintValidatorContext constraintValidatorContext, String message)
  {
    constraintValidatorContext
        .buildConstraintViolationWithTemplate(message)
        .addConstraintViolation()
        .disableDefaultConstraintViolation();
  }
}
