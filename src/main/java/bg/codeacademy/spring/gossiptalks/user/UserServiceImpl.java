package bg.codeacademy.spring.gossiptalks.user;

import bg.codeacademy.spring.gossiptalks.exceptions.InvalidException;
import bg.codeacademy.spring.gossiptalks.exceptions.PasswordMismatchException;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class UserServiceImpl implements UserService
{
  private final UserRepository        userRepository;
  private final ModelMapper           modelMapper;
  private final BCryptPasswordEncoder passwordEncoder;

  public UserServiceImpl(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder)
  {
    this.userRepository = userRepository;
    this.modelMapper = new ModelMapper();
    this.passwordEncoder = passwordEncoder;
  }

  @Override
  public List<User> getUsers(String name, boolean f)
  {
    User user = getCurrentLoggedUser();
    List<User> filteredList = getFilteredList(name, f, user);
    filteredList.sort(Comparator.naturalOrder());
    return filteredList;
  }

  @Override
  public User createUser(User user)
  {
    if (userRepository.findByEmail(user.getEmail()) != null
        || userRepository.findByUsername(user.getUsername()) != null) {
      throw new IllegalArgumentException("Failed - the user already exists.");
    }
    user.setPassword(passwordEncoder.encode(user.getPassword()));
    return userRepository.save(user);
  }

  @Override
  public User getCurrentUser()
  {
    return getCurrentLoggedUser();
  }

  @Override
  public User changeCurrentUserPassword(String password, String passwordConfirmation,
                                        String oldPassword)
  {
    User user = getCurrentLoggedUser();
    if (!password.equals(passwordConfirmation)) {
      throw new PasswordMismatchException("Failed - passwords don't match.");
    }
    user.setPassword(password);
    return user;
  }

  @Override
  public User followUser(String username, boolean follow)
  {
    /*  Check for valid username  */
    if (this.userRepository.findByUsername(username) == null) {
      throw new InvalidException("There is no user with that username.");
    }
    /*  Check for self-following   */

    User userToFollow = this.userRepository.findByUsername(username);
    User loggedUser = getCurrentUser();

    if (loggedUser.getUsername().equals(username)) {
      throw new InvalidException("You cannot follow yourself");
    }
    boolean isFriend = false;
    for (int i = 0; i < loggedUser.getFriends().size(); i++) {
      if (loggedUser.getFriends().get(i).getUsername().equals(userToFollow.getUsername())) {
        isFriend = true;
        break;
      }
    }
    if (follow) {
      if (!isFriend) {
        loggedUser.getFriends().add(userToFollow);
        userToFollow.setFollowing(follow);
      }
    }
    else {
      loggedUser.getFriends().remove(userToFollow);
      userToFollow.setFollowing(follow);
    }
    this.userRepository.save(loggedUser);
    return userToFollow;
  }


  @Override
  public User getByUsername(String username)
  {
    return userRepository.findByUsername(username);
  }

  @Override
  public User getByEmail(String email)
  {
    return userRepository.findByEmail(email);
  }

  public ModelMapper getModelMapper()
  {
    return modelMapper;
  }

  @Override
  public List<User> findAllFriendsOfUser(String username)
  {
    return this.userRepository.findAllFriendsOfUser(username);
  }

  private User getCurrentLoggedUser()
  {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    String principalUsername;
    if (principal instanceof UserDetails) {
      principalUsername = ((UserDetails) principal).getUsername();
    }
    else {
      principalUsername = principal.toString();
    }
    return userRepository.findByUsername(principalUsername);
  }

  private List<User> getFilteredList(String name, boolean f, User user)
  {
    List<User> users;
    List<User> filteredList = new ArrayList<>();
    if (name == null) {
      if (f) {
        filteredList = userRepository.findAllFriendsOfUser(user.getUsername());
      }
      else {
        filteredList = userRepository.findAll();
      }
    }
    else {
      if (f) {
        users = userRepository.findAllFriendsOfUser(user.getUsername());
        for (User u : users) {
          if (u.getUsername().contains(name) || u.getName().contains(name)) {
            filteredList.add(u);
          }
        }
      }
      else {
        filteredList = userRepository.findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(name, name);
      }
    }
    return filteredList;
  }
}
