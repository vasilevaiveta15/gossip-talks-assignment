package bg.codeacademy.spring.gossiptalks.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.transaction.Transactional;
import java.util.List;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

@DataJpaTest
@Transactional
public class UserRepositoryTest extends AbstractTestNGSpringContextTests
{
  @Autowired
  private UserRepository userRepository;

  private User user = new User();

  @BeforeClass
  public void beforeClass()
  {
    user.setUsername("niksan4o");
    user.setName("nIkOLay");
    user.setEmail("nikolayB@abv.bg");
    user.setPassword("Niko123$");
    user = this.userRepository.save(user);
  }

  @BeforeMethod
  public void setUp()
  {
  }

  @Test
  public void findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCase_ReturnsListOfUsers_WithCaseInsensitive()
  {

    List<User> userActual = userRepository.
        findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCase("NiK",
            "niKS");

    assertEquals(userActual.get(0).getName(), user.getName());
    assertEquals(userActual.get(0).getUsername(), user.getUsername());

  }

  @Test
  public void findByEmail_ReturnsUser_WhenEmailExists()
  {


    User userActual = userRepository.findByEmail(user.getEmail());

    assertEquals(userActual.getEmail(), user.getEmail());

  }

  @Test
  public void findByEmail_ReturnsNull_WhenEmailNotFound()
  {


    User userActual = userRepository.findByEmail("niskanB@abv.bg");

    assertNull(userActual);
    // assertNotEquals(userActual,"niksanB@abv.bg");
  }

  @Test
  public void findByUsername_ReturnsUser_WhenUsernameExists()
  {


    User userActual = userRepository.findByUsername(user.getUsername());

    assertEquals(userActual.getUsername(), user.getUsername());

  }

  @Test
  public void findByUsername_ReturnsNull_WhenUsernameNotFound()
  {


    User userActual = userRepository.findByUsername("obichamSusam");

    assertNull(userActual);
  }

}