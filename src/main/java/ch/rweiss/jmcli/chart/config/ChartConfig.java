package ch.rweiss.jmcli.chart.config;

import java.util.ArrayList;
import java.util.List;

public class ChartConfig
{
  private String title;
  private List<Serie> series = new ArrayList<>();
  
  public ChartConfig()
  {
  }

  public String getTitle()
  {
    return title;
  }

  public void setTitle(String title)
  {
    this.title = title;
  }

  public List<Serie> getSeries()
  {
    return series;
  }
}

