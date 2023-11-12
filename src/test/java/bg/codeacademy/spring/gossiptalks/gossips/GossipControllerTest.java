package bg.codeacademy.spring.gossiptalks.gossips;

import bg.codeacademy.spring.gossiptalks.user.User;
import bg.codeacademy.spring.gossiptalks.user.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.context.support.WithSecurityContextTestExecutionListener;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.MockMvc;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@TestExecutionListeners({WithSecurityContextTestExecutionListener.class})
@ActiveProfiles(profiles = "dev")
public class GossipControllerTest extends AbstractTestNGSpringContextTests
{

  @Autowired
  public MockMvc mockMvc;

  @Autowired
  private GossipRepository gossipRepository;

  @Autowired
  private UserRepository userRepository;

  private       Long   TEST_GOSSIP1_ID;
  private final String TEST_GOSSIP1_TEXT     = "Hello world!";
  private final String TEST_GOSSIP2_TEXT     = "Hello friends!";
  private final String TEST_GOSSIP1_USERNAME = "iveta.vasileva";

  @BeforeClass
  public void setUp()
  {
    User user = new User();
    user.setUsername(TEST_GOSSIP1_USERNAME);
    user.setEmail("iveta_vasileva@abv.bg");
    user.setName("iveta");
    user.setPassword("Pass!123");
    user = this.userRepository.save(user);

    User user1 = new User();
    user1.setUsername("current");
    user1.setEmail("current@abv.bg");
    user1.setName("iveta");
    user1.setPassword("Pass!123");
    user1 = this.userRepository.save(user1);
    user1.getFriends().add(user);
    this.userRepository.save(user1);

    Gossip gossip1 = new Gossip();
    gossip1.setText(TEST_GOSSIP1_TEXT);
    gossip1.setUsername(TEST_GOSSIP1_USERNAME);
    gossip1.setDateTime(LocalDateTime.now());
    gossip1 = this.gossipRepository.save(gossip1);
    TEST_GOSSIP1_ID = gossip1.getId();
    user.getGossips().add(gossip1);
    userRepository.save(user);

    Gossip gossip2 = new Gossip();
    gossip2.setText(TEST_GOSSIP2_TEXT);
    String TEST_GOSSIP2_USERNAME = "pesho.31";
    gossip2.setUsername(TEST_GOSSIP2_USERNAME);
    gossip2.setDateTime(LocalDateTime.now());
    this.gossipRepository.save(gossip2);
  }

  @AfterClass
  public void tearDown()
  {
    this.userRepository.deleteAll();
  }

  @WithMockUser(username = "current")
  @Test
  public void getAllGossips_ReturnsGossipList() throws Exception
  {
    mockMvc.
        perform(get("/api/v1/gossips?pageNo=0&pageSize=2"))
        .andDo(print())
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content.[0].id", is(Math.toIntExact(TEST_GOSSIP1_ID))))
        .andExpect(jsonPath("$.content.[0].text", is(TEST_GOSSIP1_TEXT)))
        .andExpect(jsonPath("$.content.[0].username", is(TEST_GOSSIP1_USERNAME)));

  }

  @WithMockUser(username = "current")
  @Test
  public void getAllGossipsOfUserByPage_ReturnsGossipList() throws Exception
  {
    mockMvc.
        perform(get("/api/v1/users/iveta.vasileva/gossips?pageNo=0&pageSize=20"))
        .andExpect(status().isOk())
        .andDo(print())
        /*  User Iveta has only one gossip. */
        .andExpect(jsonPath("$.content", hasSize(1)))
        .andExpect(jsonPath("$.content.[0].id", is(Math.toIntExact(TEST_GOSSIP1_ID))))
        .andExpect(jsonPath("$.content.[0].text", is(TEST_GOSSIP1_TEXT)))
        .andExpect(jsonPath("$.content.[0].username", is(TEST_GOSSIP1_USERNAME)));

  }


  @WithMockUser(username = "current")
  @Test
  public void createGossip_ReturnGossipDto() throws Exception
  {
    mockMvc.perform(multipart("/api/v1/gossips").param("text", TEST_GOSSIP2_TEXT))
        .andExpect(status().isOk());
  }

}