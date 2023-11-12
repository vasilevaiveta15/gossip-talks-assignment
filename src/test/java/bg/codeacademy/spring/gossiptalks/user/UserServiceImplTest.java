package bg.codeacademy.spring.gossiptalks.user;

import bg.codeacademy.spring.gossiptalks.exceptions.InvalidException;
import bg.codeacademy.spring.gossiptalks.exceptions.PasswordMismatchException;
import bg.codeacademy.spring.gossiptalks.gossips.Gossip;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.context.web.WebAppConfiguration;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.testng.Assert.*;

@SpringBootTest
@WebAppConfiguration
@ActiveProfiles(value = "test")
@AutoConfigureMockMvc(addFilters = false) //ignore security config
public class UserServiceImplTest extends AbstractTestNGSpringContextTests
{
  @Mock
  private UserRepository        userRepository;
  @Mock
  private BCryptPasswordEncoder passwordEncoder;
  @Mock
  private Authentication        authentication;
  @Mock
  private SecurityContext       securityContext;
  private User                  testUser;
  private UserServiceImpl       userService;
  private User                  friendUser;
  private User                  secondFriendUser;
  private User                  nonFriendUser;
  private List<User>            allUsers;
  private List<User>            allFriends;

  @BeforeClass
  public void beforeClass()
  {
    testUser = new User("Ivan", "ivan@gmail.com", "ivan76", "abc123ABC");
    friendUser = new User("Stefan", "stefan@gmail.com", "stefan76", "abc123ABC");
    secondFriendUser = new User("Gosho", "gosho@gmail.com", "gosho76", "abc123ABC");
    nonFriendUser = new User("Pavel", "pavel@gmail.com", "pavel76", "abc123ABC");
    testUser.getFriends().add(friendUser);
    testUser.getFriends().add(secondFriendUser);
    Gossip gossip1 = new Gossip("test", friendUser.getUsername(), LocalDateTime.now());
    Gossip gossip2 = new Gossip("text", nonFriendUser.getUsername(), LocalDateTime.now());
    friendUser.setGossips(Arrays.asList(gossip1, gossip1));
    nonFriendUser.setGossips(Collections.singletonList(gossip2));
    allUsers = Arrays.asList(friendUser, secondFriendUser, nonFriendUser);
    allFriends = Arrays.asList(friendUser, secondFriendUser);
  }

  @BeforeMethod
  public void setUp()
  {
//    initialize mocks before each method
    MockitoAnnotations.openMocks(this);
    userService = new UserServiceImpl(userRepository, passwordEncoder);
    authentication = Mockito.mock(Authentication.class);
    securityContext = Mockito.mock(SecurityContext.class);
  }

  @Test
  public void createUser_SavesUserInRepository_IfInputDataIsCorrect()
  {
    when(userRepository.save(any(User.class)))
        .thenReturn(testUser);

    User actualUser = userService.createUser(testUser);
    verify(userRepository, times(1)).save(any(testUser.getClass()));
//    assert saved user is the one returned
    assertEquals(actualUser, testUser);
    assertEquals(actualUser.getUsername(), testUser.getUsername());
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
      description = "Failed - the user already exists")
  public void createUser_ThrowsIllegalArgumentException_IfEmailAlreadyExists()
  {
    when(userRepository.save(any(User.class)))
        .thenReturn(null);
    when(userRepository.findByEmail(any(String.class)))
        .thenReturn(testUser);

    userService.createUser(testUser);
    verify(userRepository, times(1)).save(any(User.class));
    verify(userRepository, times(1)).findByEmail(any(String.class));
  }

  @Test(expectedExceptions = IllegalArgumentException.class,
      description = "Failed - the user already exists")
  public void createUser_ThrowsIllegalArgumentException_IfUsernameAlreadyExists()
  {
    when(userRepository.save(any(User.class)))
        .thenReturn(null);
    when(userRepository.findByUsername(any(String.class)))
        .thenReturn(testUser);

    userService.createUser(testUser);
    verify(userRepository, times(1)).save(any(User.class));
    verify(userRepository, times(1)).findByEmail(any(String.class));
  }

  @Test
  public void createUser_EncodesUserPassword()
  {
    when(userRepository.save(any(User.class)))
        .thenReturn(testUser);
    when(passwordEncoder.encode(any(String.class)))
        .thenReturn("encodedPassword");

    User expectedUser = userService.createUser(testUser);
    verify(userRepository, times(1)).save(any(User.class));
    verify(passwordEncoder, times(1)).encode(any(String.class));
    //assert that the stored password is the encrypted one
    assertEquals(expectedUser.getPassword(), passwordEncoder.encode(expectedUser.getPassword()));
  }

  @Test
  public void getCurrentUser_ReturnsUser_IfUserLoggedIn()
  {
    mockUserAuthentication();

    userService.getCurrentUser();
    verify(userRepository, times(1)).findByUsername(testUser.getUsername());
  }

  @Test
  public void changeCurrentUserPassword_ChangesUserPassword_IfOldPasswordIsValid()
  {
    mockUserAuthentication();

    String validOldPassword = testUser.getPassword();
    User actualUser = userService.changeCurrentUserPassword("test", "test", validOldPassword);
    verify(userRepository, times(1)).findByUsername(testUser.getUsername());
    assertEquals(actualUser.getPassword(), testUser.getPassword());
  }

  @Test(expectedExceptions = AssertionError.class)
  public void changeCurrentUserPassword_ThrowsAssertionError_IfOldPasswordIsInvalid()
  {
    mockUserAuthentication();

    String invalidOldPassword = "invalid password";
    User actualUser = userService.changeCurrentUserPassword("test", "test", invalidOldPassword);
    assertNotEquals(actualUser.getPassword(), testUser.getPassword());
  }

  @Test(expectedExceptions = PasswordMismatchException.class)
  public void changeCurrentUserPassword_ThrowsPasswordMismatchException_IfPasswordsDontMatch()
  {
    mockUserAuthentication();

    String newPassword = "123";
    String passwordConfirmation = "456";
    User actualUser = userService.changeCurrentUserPassword(newPassword, passwordConfirmation, testUser.getPassword());
    assertNotEquals(actualUser.getPassword(), testUser.getPassword());
  }

  @Test
  public void getUsers_ReturnsAListOfAllFriends_IfNameBlankAndFollowingTrue()
  {
    mockUserAuthentication();
    when(userRepository.findAllFriendsOfUser(any(String.class)))
        .thenReturn(testUser.getFriends());

    List<User> expectedFriendsList = userService.getUsers("", true);
    verify(userRepository, times(1)).findAllFriendsOfUser(testUser.getUsername());
    assertEquals(expectedFriendsList.get(0), friendUser);
  }

  @Test
  public void getUsers_ReturnsAListOfNameSpecifingFriends_IfNameSetAndFollowingTrue()
  {
    mockUserAuthentication();
    when(userRepository.findAllFriendsOfUser(any(String.class)))
        .thenReturn(testUser.getFriends());

    List<User> expectedFriendsList = userService.getUsers(friendUser.getUsername(), true);
    verify(userRepository, times(1)).findAllFriendsOfUser(testUser.getUsername());
    assertEquals(expectedFriendsList.get(0), friendUser);
  }

  @Test
  public void getUsers_ReturnsAllUsers_IfNameNullAndFollowingFalse()
  {
    mockUserAuthentication();
    when(userRepository.findAll())
        .thenReturn(allUsers);

    List<User> actualList = userService.getUsers(null, false);
    verify(userRepository, times(1)).findAll();
    assertEquals(actualList, allUsers);
  }

  @Test
  public void getUsers_ReturnsAllNameSpecificUsers_IfNameSetAndFollowingFalse()
  {
    mockUserAuthentication();
    when(userRepository.findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(any(String.class), any(String.class)))
        .thenReturn(Arrays.asList(friendUser, nonFriendUser));

    List<User> actualList = userService.getUsers("an", false);
    verify(userRepository, times(1)).findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCase("an", "an");
    assertEquals(actualList, Arrays.asList(friendUser, nonFriendUser));
  }

  @Test
  public void getUsers_SortsResultingListInDescendingOrderByNumberOfGossips()
  {
    mockUserAuthentication();
    when(userRepository.findAll())
        .thenReturn(allUsers);

    List<User> actualList = userService.getUsers(null, false);
    assertEquals(actualList.get(0).getGossips().size(), allUsers.get(0).getGossips().size());
    assertEquals(actualList.get(1).getGossips().size(), allUsers.get(1).getGossips().size());
    assertEquals(actualList.get(2).getGossips().size(), allUsers.get(2).getGossips().size());
  }

  @Test
  public void getByUsername_ReturnsUser_IfUsernameExists()
  {
    when(userRepository.findByUsername(any(String.class)))
        .thenReturn(testUser);

    userService.getByUsername(testUser.getUsername());
    verify(userRepository, times(1)).findByUsername(testUser.getUsername());
  }

  @Test
  public void getByEmail_ReturnsUser_IfEmailExists()
  {
    when(userRepository.findByEmail(any(String.class)))
        .thenReturn(testUser);

    userService.getByEmail(testUser.getEmail());
    verify(userRepository, times(1)).findByEmail(testUser.getEmail());
  }

  @Test
  public void findAllFriendsOfUser_CallsRepositoryFindAllFriendsMethod()
  {
    when(userRepository.findAllFriendsOfUser(any(String.class)))
        .thenReturn(allFriends);

    userService.findAllFriendsOfUser(testUser.getUsername());
    verify(userRepository, times(1)).findAllFriendsOfUser(testUser.getUsername());
  }

  @Test(expectedExceptions = InvalidException.class)
  public void followUser_ThrowsInvalidException_IfUsernameDoesntExist()
  {
    when(userRepository.findByUsername(any(String.class)))
        .thenReturn(null);

    userService.followUser("Invalid username", true);
    verify(userRepository, times(1)).findByUsername("Invalid username");
  }

  @Test(expectedExceptions = InvalidException.class)
  public void followUser_ThrowsInvalidException_IfUserTriesToFollowHimself()
  {
    mockUserAuthentication();
    when(userRepository.findByUsername(any(String.class)))
        .thenReturn(testUser);

    userService.followUser(testUser.getUsername(), true);
    verify(userRepository, atLeast(2)).findByUsername(testUser.getUsername());
  }

  @Test
  public void followUser_AddsUserToFriends_IfFollowIsTrue()
  {
    mockUserAuthentication();
    when(userRepository.findByUsername(nonFriendUser.getUsername()))
        .thenReturn(nonFriendUser);

    User user = new User("Spas", "spas@gmail.com", "spas76", "abc");
    user.getFriends().add(nonFriendUser);

    userService.followUser(nonFriendUser.getUsername(), true);

    verify(userRepository, atLeast(2)).findByUsername(any(String.class));
    assertEquals(testUser.getFriends().get(2), user.getFriends().get(0));
  }

  @Test
  public void followUser_RemovesUserFromFriends_IfFollowIsFalse()
  {
    mockUserAuthentication();
    when(userRepository.findByUsername(friendUser.getUsername()))
        .thenReturn(friendUser);

    User actualUser = userService.followUser(friendUser.getUsername(), false);
    verify(userRepository, atLeast(2)).findByUsername(any(String.class));
    assertNotNull(actualUser);
    testUser.getFriends().add(0, friendUser); //restoring state of testUser friends list
  }

  @Test
  public void followUser_ReturnsTheUserToFollowObject()
  {
    mockUserAuthentication();
    when(userRepository.findByUsername(friendUser.getUsername()))
        .thenReturn(friendUser);

    User returnedUser = userService.followUser(friendUser.getUsername(), false);
    verify(userRepository, atLeast(2)).findByUsername(any(String.class));
    assertEquals(returnedUser.getUsername(), friendUser.getUsername());
    assertEquals(returnedUser.getEmail(), friendUser.getEmail());
    testUser.getFriends().add(0, friendUser); //restoring state of testUser friends list
  }

  private void mockUserAuthentication()
  {
    when(authentication.getPrincipal())
        .thenReturn(testUser.getUsername());
    when(securityContext.getAuthentication())
        .thenReturn(authentication);
    when(userRepository.findByUsername(any(String.class)))
        .thenReturn(testUser);
    SecurityContextHolder.setContext(securityContext);
  }
}