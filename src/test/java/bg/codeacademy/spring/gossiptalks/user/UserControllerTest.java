package bg.codeacademy.spring.gossiptalks.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootContextLoader;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.testng.Assert.assertNotNull;

@SpringBootTest
@AutoConfigureMockMvc
@TestExecutionListeners({WithSecurityContextTestExecutionListener.class})
@ActiveProfiles(value = "dev")
@ContextConfiguration(loader = SpringBootContextLoader.class)
public class UserControllerTest extends AbstractTestNGSpringContextTests
{
  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserRepository userRepository;

  private User testUser;

  @BeforeClass
  public void beforeClass()
  {
    testUser = new User("Stefan", "stefan@gmail.com", "stefan", "Abc123!@#");
    this.userRepository.save(testUser);

    User testUser2 = new User("Ivan", "ivan@gmail.com", "ivan", "Abc123!@#");
    this.userRepository.save(testUser2);
  }

  @Test
  public void userRepository_InitializedCorrectly()
  {
    assertNotNull(userRepository);
  }

  @Test
  public void getCurrentUser_ReturnsUnauthorized_IfUserNotLoggedIn() throws Exception
  {
    mockMvc.perform(get("/api/v1/users/me"))
        .andDo(print())
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(username = "stefan")
  public void getCurrentUser_ReturnsCurrentlyLoggedUser() throws Exception
  {
    mockMvc.perform(get("/api/v1/users/me"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.username").value(testUser.getUsername()))
        .andExpect(jsonPath("$.email").value(testUser.getEmail()));
  }

  @Test
  public void getUsers_ReturnsUnauthorizedError_IfUsersNotLoggedIn() throws Exception
  {
    mockMvc.perform(get("/api/v1/users?name=stefan&f=true"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  @WithMockUser(username = "stefan")
  public void getUsers_ReturnsUserResponse_IfUserFound() throws Exception
  {
    mockMvc.perform(get("/api/v1/users?name=stefan&f=false"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username").value(testUser.getUsername()))
        .andExpect(jsonPath("$[0].email").value(testUser.getEmail()));
  }

  @Test
  @WithMockUser(username = "stefan")
  public void getUsers_ReturnsEmptyUserResponse_IfNoUsersFound() throws Exception
  {
    mockMvc.perform(get("/api/v1/users?name=stefan&f=true"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].username").doesNotExist())
        .andExpect(jsonPath("$[0].email").doesNotExist());
  }

  @Test
  public void createUser_ReturnsStatusOk_IfSuccessfulOperation() throws Exception
  {
    mockMvc.perform(multipart("/api/v1/users")
            .param("email", "newUser@abv.bg")
            .param("name", "New User")
            .param("username", "newuser")
            .param("password", "Abc123!@#")
            .param("passwordConfirmation", "Abc123!@#"))
        .andExpect(status().isOk());
  }

  @Test
  @WithMockUser(username = "stefan")
  public void createUser_ReturnsBadRequest_IfPasswordsDontMatch() throws Exception
  {
    mockMvc.perform(multipart("/api/v1/users")
            .param("email", "newUser@abv.bg")
            .param("name", "New User")
            .param("username", "newuser")
            .param("password", "Abc123!@#asdfasdf")
            .param("passwordConfirmation", "Abc123!@#"))
        .andExpect(status().isBadRequest());
  }
}