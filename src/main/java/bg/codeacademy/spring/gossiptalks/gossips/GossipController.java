package bg.codeacademy.spring.gossiptalks.gossips;

import bg.codeacademy.spring.gossiptalks.gossips.dto.CreateGossipRequest;
import bg.codeacademy.spring.gossiptalks.gossips.dto.GossipDto;
import bg.codeacademy.spring.gossiptalks.gossips.dto.GossipList;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.lang.reflect.Type;
import java.util.List;

@RestController
@RequestMapping("/api/v1")

public class GossipController
{
  private final GossipService gossipService;

  @Autowired
  public GossipController(GossipService gossipService)
  {
    this.gossipService = gossipService;
  }

  @GetMapping("/gossips")
  public ResponseEntity<?> showAllGossipsByPage(
      @RequestParam(defaultValue = "0") @Min(value = 0) Integer pageNo,
      @Min(value = 1) @Max(value = 100) @RequestParam(defaultValue = "100") Integer pageSize)
  {
    Page<Gossip> pageGossip = gossipService.getAll(pageNo, pageSize);
    return getTypeForTheMapper(pageGossip);
  }

  @GetMapping("/users/{username}/gossips")
  public ResponseEntity<?> showAllGossipsOfUserByPage(@PathVariable("username") String username,
                                                      @RequestParam(defaultValue = "0") @Min(value = 0) Integer pageNo,
                                                      @RequestParam(defaultValue = "20") @Min(value = 1) Integer pageSize)
  {
    Page<Gossip> pageGossip = gossipService.getByUsername(username,
        PageRequest.of(pageNo, pageSize));
    return getTypeForTheMapper(pageGossip);
  }

  @PostMapping(path = "/gossips", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
  @ResponseStatus(value = HttpStatus.OK)
  public GossipDto createGossip(@Valid CreateGossipRequest gossip)
  {
    return this.gossipService.getModelMapper()
        .map(gossipService.insert(gossip.getText()), GossipDto.class);
  }

  private ResponseEntity<?> getTypeForTheMapper(Page<Gossip> pageGossip)
  {
    List<Gossip> gossips = pageGossip.toList();

    /* Take the type of GossipListDto(List<GossipDto>)*/
    Type listType = new TypeToken<List<GossipDto>>()
    {
    }.getType();

    /* Mapped List<Gossips> gossips to Type listType*/
    List<GossipDto> gossipDtos = this.gossipService.getModelMapper().map(gossips, listType);

    GossipList gossipLists = new GossipList();
    /*  Set all values which has GossipList */

    /*  PAGE NUMBER: The page we are looking  */
    gossipLists.setPageNumber(pageGossip.getNumber());

    /*  PAGE SIZE: The maximum element we can save on Page  */
    gossipLists.setPageSize(pageGossip.getSize());

    /*  COUNT: Gossips number(count)  */
    gossipLists.setCount(Math.toIntExact(pageGossip.getTotalElements()));

    /*  TOTAL: The number of Pages that save all elements */
    gossipLists.setTotal(pageGossip.getTotalPages());

    gossipLists.setContent(gossipDtos);

    return ResponseEntity.ok(gossipLists);
  }
}
