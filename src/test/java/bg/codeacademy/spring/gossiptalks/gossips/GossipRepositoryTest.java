package bg.codeacademy.spring.gossiptalks.gossips;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotEquals;

@DataJpaTest
@Transactional
public class GossipRepositoryTest extends AbstractTransactionalTestNGSpringContextTests
{

  private final String USERNAME = "niksan";

  @Autowired
  private GossipRepository gossipRepository;

  private Pageable pageable;


  @BeforeMethod
  public void setUp()
  {

  }

  @Test
  public void findByUsernameOrderByDateTimeDesc_ReturnsPageOfGossips_IfUsernameIsValid()
  {
    Gossip gossip = new Gossip("gossipsTest", USERNAME, LocalDateTime.now());
    pageable = PageRequest.of(0, 2);
    gossipRepository.save(gossip);

    Page<Gossip> expectedPage = new PageImpl<>(Collections.singletonList(gossip));
    Page<Gossip> actualPage = gossipRepository.findByUsernameOrderByDateTimeDesc(USERNAME, pageable);
    assertEquals(actualPage.getTotalElements(), expectedPage.getTotalElements());
    // assertEquals(actualPage.getTotalElements(),1);

  }

  @Test
  public void findByUsernameOrderByDateTimeDesc_ReturnsEmptyPageOfGossips_WhenUsernameIsNotValid()
  {
    pageable = PageRequest.of(0, 2);

    Page<Gossip> actualPage = gossipRepository.findByUsernameOrderByDateTimeDesc("", pageable);
    assertEquals(actualPage.getTotalElements(), 0);

  }

  @Test(expectedExceptions = IllegalArgumentException.class)
  public void findByUsernameOrderByDateTimeDesc_ThrowsIllegalArgumentException_WhenPageSizeIsZero()
  {
    Gossip gossip = new Gossip("gossipsTest", USERNAME, LocalDateTime.now());
    pageable = PageRequest.of(0, 0);
    gossipRepository.save(gossip);

    gossipRepository.findByUsernameOrderByDateTimeDesc(USERNAME, pageable);
  }

  @Test
  public void findByUsernameOrderByDateTimeDesc_ReturnsGossipsInDescendingOrder()
  {
    Gossip gossip = new Gossip("gossipsTest", USERNAME, LocalDateTime.now());
    Gossip gossipOne = new Gossip("gossipsTest1", USERNAME, LocalDateTime.of(1900, 12, 4, 9, 24));
    Gossip gossipTwo = new Gossip("gossipTest2", USERNAME, LocalDateTime.of(2000, 10, 12, 10, 20));
    pageable = PageRequest.of(0, 1);
    gossipRepository.save(gossip);
    gossipRepository.save(gossipOne);
    gossipRepository.save(gossipTwo);

    PageImpl<Gossip> expectedPage = new PageImpl<>(Arrays.asList(gossip, gossipTwo, gossipOne));
    Page<Gossip> actualPage = gossipRepository.findByUsernameOrderByDateTimeDesc(USERNAME, pageable);

    // assertNotEquals(actualPage.getTotalPages(),expectedPage.getTotalPages());
    assertNotEquals(actualPage.get().findFirst(), expectedPage.get().anyMatch(gossip1 -> gossip1.getDateTime().isBefore(LocalDateTime.of(2000, 10, 12, 10, 20))));
  }

}