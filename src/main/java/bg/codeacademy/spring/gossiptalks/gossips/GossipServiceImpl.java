package bg.codeacademy.spring.gossiptalks.gossips;

import bg.codeacademy.spring.gossiptalks.exceptions.InvalidException;
import bg.codeacademy.spring.gossiptalks.user.User;
import bg.codeacademy.spring.gossiptalks.user.UserService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
@Transactional
public class GossipServiceImpl implements GossipService
{

  private final GossipRepository gossipRepository;
  private final ModelMapper modelMapper;
  private final UserService userService;


  @Autowired
  public GossipServiceImpl(GossipRepository gossipRepository,
                           UserService userService)
  {
    this.gossipRepository = gossipRepository;
    this.userService = userService;
    this.modelMapper = new ModelMapper();
  }

  @Override
  public Page<Gossip> getAll(Integer pageNo, Integer pageSize)
  {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = this.userService.getByUsername(authentication.getName());

    List<User> allFriendsOfUser = this.userService.findAllFriendsOfUser(user.getUsername());
    List<Gossip> allGossipOfUser = new ArrayList<>();
    for (User user1 : allFriendsOfUser) {
      allGossipOfUser.addAll(user1.getGossips());
    }
    allGossipOfUser.sort(Comparator.comparing(Gossip::getDateTime).reversed());
    Pageable paging = PageRequest.of(pageNo, pageSize);
    int start = Math.min((int) paging.getOffset(), allGossipOfUser.size());
    int end = Math.min((start + paging.getPageSize()), allGossipOfUser.size());

    Page<Gossip> allGossipOfUserPage = new PageImpl<>(allGossipOfUser.subList(start, end), paging, allGossipOfUser.size());

    return allGossipOfUserPage;
  }

  @Override
  public Page<Gossip> getByUsername(String username, Pageable pageable)
  {
    /*  Check for valid username  */
    if (this.userService.getByUsername(username) == null) {
      throw new InvalidException("There is no user with that username.");
    }
    return this.gossipRepository.findByUsernameOrderByDateTimeDesc(username, pageable);
  }

  @Override
  public Gossip insert(String text)
  {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    User user = this.userService.getByUsername(authentication.getName());
    if (text.length() < 1) {
      throw new IllegalArgumentException("You must enter a message!");
    }
    if (text.length() >= 100) {
      throw new IllegalArgumentException("Your text is too long!");
    }
    Gossip gossip = new Gossip();
    gossip.setDateTime(LocalDateTime.now());
    gossip.setUsername(user.getUsername());
    gossip.setText(text);
    user.getGossips().add(gossip);
    return this.gossipRepository.save(gossip);
  }

  public ModelMapper getModelMapper()
  {
    return modelMapper;
  }
}
