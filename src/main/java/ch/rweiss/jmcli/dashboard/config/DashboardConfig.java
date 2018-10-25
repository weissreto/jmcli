package ch.rweiss.jmcli.dashboard.config;

import java.util.ArrayList;
import java.util.List;

public class DashboardConfig
{
  private final List<Section> sections = new ArrayList<>();
  private String title;
  
  public List<Section> getSections()
  {
    return sections;
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }
}
