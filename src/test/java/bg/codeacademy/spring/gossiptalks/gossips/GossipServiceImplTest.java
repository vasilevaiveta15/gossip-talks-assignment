package bg.codeacademy.spring.gossiptalks.gossips;

import bg.codeacademy.spring.gossiptalks.exceptions.InvalidException;
import bg.codeacademy.spring.gossiptalks.user.User;
import bg.codeacademy.spring.gossiptalks.user.UserService;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.testng.AssertJUnit.assertEquals;

public class GossipServiceImplTest
{

  @Mock
  private GossipRepository gossipRepository;

  private GossipServiceImpl gossipServiceToTest;

  @Mock
  private Authentication authentication;

  @Mock
  private SecurityContext securityContext;

  @Mock
  UserService userService;

  private final Map<Long, Gossip> fakeDb = new HashMap<>();


  @BeforeClass
  public void beforeClass()
  {
    fakeDb.put(1L, new Gossip("Hello1!!!!", "iveta", LocalDateTime.now()));
    fakeDb.put(2L, new Gossip("Hello2!!!!", "tisho", LocalDateTime.now()));
    fakeDb.put(3L, new Gossip("Hello3!!!!", "pesho", LocalDateTime.now()));
  }

  @BeforeMethod
  public void setUp()
  {
    MockitoAnnotations.openMocks(this);
    gossipServiceToTest = new GossipServiceImpl(gossipRepository, userService);
    authentication = Mockito.mock(Authentication.class);
    securityContext = Mockito.mock(SecurityContext.class);

  }

  @Test
  public void testFindAll()
  {
    User user = new User();
    user.setPassword("Pass!123");
    user.setUsername("iveta12345");
    user.setName("pesho");
    user.setEmail("pesho@abv.bg");

    Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

    SecurityContextHolder.setContext(securityContext);

    when(userService.getByUsername(anyString()))
        .thenReturn(user);

    when(authentication.getName())
        .thenReturn(user.getUsername());

    when(userService.findAllFriendsOfUser(anyString()))
        .thenReturn(Collections.singletonList(user));

    gossipServiceToTest.getAll(0, 2);

    verify(userService, times(1)).getByUsername(user.getUsername());
  }


  @Test(expectedExceptions = InvalidException.class)
  public void testGetByUsername_ReturnsGossipFromFakeDb_IfUsernameIsInvalid()
  {
    when(gossipRepository.findById(anyString()))
        .thenReturn(Optional.empty());

    gossipServiceToTest.getByUsername("ivetaa", null);
    verify(gossipRepository, atLeast(1)).findByUsernameOrderByDateTimeDesc("ivetaa", null);
  }

  @Test
  public void testGetByUsername_ReturnGossipFromFakeDb_IfUsernameIsValid()
  {
    User user = new User();
    user.setPassword("Pass!123");
    user.setUsername("iveta12345");
    user.setName("pesho");
    user.setEmail("pesho@abv.bg");

    Gossip gossip = fakeDb.get(1L);

    when(userService.getByUsername(anyString()))
        .thenReturn(user);

    when(gossipRepository.save(any(Gossip.class)))
        .thenReturn(gossip);

    when(gossipRepository.findById(any()))
        .thenReturn(Optional.of(gossip));

    user.getGossips().add(gossip);

    String text = gossip.getText();
    Gossip actualGossip = gossipServiceToTest.insert(text);

    verify(gossipRepository, times(1)).save(any(gossip.getClass()));
    verify(userService, times(1)).getByUsername(user.getUsername());

    Assert.assertEquals(actualGossip, gossip);
    Assert.assertEquals(actualGossip.getId(), gossip.getId());

  }

  @Test
  public void inset_SaveAndReturnGossip()
  {
    User user = new User();
    user.setPassword("Pass!123");
    user.setUsername("iveta12345");
    user.setName("pesho");
    user.setEmail("pesho@abv.bg");

    Gossip gossip = new Gossip("Hello5", user.getUsername(), LocalDateTime.now());

    Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);

    SecurityContextHolder.setContext(securityContext);

    when(gossipRepository.save(any(Gossip.class)))
        .thenReturn(gossip);

    when(authentication.getName())
        .thenReturn(user.getUsername());

    when(gossipRepository.findById(any()))
        .thenReturn(Optional.of(gossip));

    when(userService.getByUsername(anyString()))
        .thenReturn(user);

    user.getGossips().add(gossip);
    String text = gossip.getText();
    Gossip actualGossip = gossipServiceToTest.insert(text);

    verify(gossipRepository, times(1)).save(any(gossip.getClass()));
    verify(userService, times(1)).getByUsername(user.getUsername());

    assertEquals(actualGossip, gossip);
    assertEquals(actualGossip.getUsername(), gossip.getUsername());
    assertEquals(actualGossip.getDateTime(), gossip.getDateTime());
    assertEquals(actualGossip.getText(), gossip.getText());
  }
}