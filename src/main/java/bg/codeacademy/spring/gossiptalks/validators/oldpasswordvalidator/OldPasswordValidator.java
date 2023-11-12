package bg.codeacademy.spring.gossiptalks.validators.oldpasswordvalidator;

import bg.codeacademy.spring.gossiptalks.user.User;
import bg.codeacademy.spring.gossiptalks.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class OldPasswordValidator implements ConstraintValidator<OldPassword, String>
{
  @Autowired
  private UserRepository userRepository;
  @Autowired
  private BCryptPasswordEncoder passwordEncoder;

  @Override
  public void initialize(OldPassword constraintAnnotation)
  {
    ConstraintValidator.super.initialize(constraintAnnotation);
  }

  @Override
  public boolean isValid(String oldPassword, ConstraintValidatorContext constraintValidatorContext)
  {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String principalUsername;
    if (principal instanceof UserDetails) {
      principalUsername = ((UserDetails) principal).getUsername();
    }
    else {
      principalUsername = principal.toString();
    }
    User user = userRepository.findByUsername(principalUsername);
    return passwordEncoder.matches(oldPassword, user.getPassword());
  }
}
