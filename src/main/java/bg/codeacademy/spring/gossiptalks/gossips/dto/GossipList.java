package bg.codeacademy.spring.gossiptalks.gossips.dto;

import javax.validation.constraints.Min;
import java.util.List;

public class GossipList
{
  @Min(value = 0)
  private Integer pageNumber;

  private Integer pageSize;
  private Integer count;
  private Integer total;
  private List<GossipDto> content;

  public Integer getPageNumber()
  {
    return pageNumber;
  }

  public void setPageNumber(Integer pageNumber)
  {
    this.pageNumber = pageNumber;
  }

  public Integer getPageSize()
  {
    return pageSize;
  }

  public void setPageSize(Integer pageSize)
  {
    this.pageSize = pageSize;
  }

  public Integer getCount()
  {
    return count;
  }

  public void setCount(Integer count)
  {
    this.count = count;
  }

  public Integer getTotal()
  {
    return total;
  }

  public void setTotal(Integer total)
  {
    this.total = total;
  }

  public List<GossipDto> getContent() {
    return content;
  }

  public void setContent(List<GossipDto> content) {
    this.content = content;
  }
}
