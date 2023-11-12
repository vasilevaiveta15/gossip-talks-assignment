package bg.codeacademy.spring.gossiptalks.user;

import bg.codeacademy.spring.gossiptalks.exceptions.PasswordMismatchException;
import bg.codeacademy.spring.gossiptalks.user.dto.ChangePasswordRequest;
import bg.codeacademy.spring.gossiptalks.user.dto.CreateUserRequest;
import bg.codeacademy.spring.gossiptalks.user.dto.FollowRequest;
import bg.codeacademy.spring.gossiptalks.user.dto.UserResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@Validated
@RestController
@RequestMapping("/api/v1/users")
public class UserController
{
  private final UserService userService;

  public UserController(UserService userService)
  {
    this.userService = userService;
  }

  @GetMapping
  public ResponseEntity<List<UserResponse>> getUsers(@RequestParam(required = false) String name,
                                                     @RequestParam(required = false) boolean f)
  {
    List<User> users = userService.getUsers(name, f);
    List<UserResponse> userResponses = new ArrayList<>();
    for (User user : users) {
      UserResponse userResponse = userService.getModelMapper().map(user, UserResponse.class);
      userResponses.add(userResponse);
    }
    return ResponseEntity.ok(userResponses);
  }

  @ResponseStatus(value = HttpStatus.OK, reason = "Successful operation")
  @RequestMapping(method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public void createUser(@Valid CreateUserRequest createUserRequest, BindingResult bindingResult)
  {
    if (!createUserRequest.getPassword().equals(createUserRequest.getPasswordConfirmation())) {
      throw new PasswordMismatchException("Failed - passwords don't match.");
    }
    User user = userService.getModelMapper().map(createUserRequest, User.class);
    userService.createUser(user);
  }

  @RequestMapping(path = "/me", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<UserResponse> changeCurrentUserPassword(@Valid ChangePasswordRequest dto, BindingResult bindingResult)
  {
    User user = userService.changeCurrentUserPassword
        (dto.getPassword(), dto.getPasswordConfirmation(), dto.getOldPassword());
    UserResponse userResponse = userService.getModelMapper().map(user, UserResponse.class);
    return ResponseEntity.ok(userResponse);
  }

  @RequestMapping(path = "/me", method = RequestMethod.GET)
  public ResponseEntity<UserResponse> getCurrentUser()
  {
    User user = userService.getCurrentUser();
    UserResponse userResponse = userService.getModelMapper().map(user, UserResponse.class);
    return ResponseEntity.ok(userResponse);
  }

  @RequestMapping(path = "/{username}/follow", method = RequestMethod.POST, consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<?> followUser(@PathVariable String username, FollowRequest follow)
  {
    User user = this.userService.followUser(username, follow.getFollow());
    return ResponseEntity.ok(this.userService.getModelMapper().map(user, UserResponse.class));
  }
}
