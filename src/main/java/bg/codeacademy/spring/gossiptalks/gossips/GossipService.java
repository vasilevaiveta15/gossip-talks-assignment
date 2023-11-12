package bg.codeacademy.spring.gossiptalks.gossips;

import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface GossipService {

  /*  Takes all gossips of given page.  */
  Page<Gossip> getAll(Integer pageNo, Integer pageSize);

  /*  Takes all gossips to the username we submitted. */
  Page<Gossip> getByUsername(String username, Pageable pageable);

  /*  Creates gossip. */
  Gossip insert(String text);

  /*  So that it can be accessed in the controller. */
  ModelMapper getModelMapper();

}
