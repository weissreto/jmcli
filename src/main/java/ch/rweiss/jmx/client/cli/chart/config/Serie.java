package ch.rweiss.jmx.client.cli.chart.config;

import java.util.ArrayList;
import java.util.List;

public class Serie
{
  private String name;
  private String description;
  private String unit;
  private String color;
  private String dataChannel;
  private List<String> functions = new ArrayList<>();
  
  public Serie()
  {
  }

  public String getName()
  {
    return name;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public String getDescription()
  {
    return description;
  }

  public void setDescription(String description)
  {
    this.description = description;
  }

  public String getUnit()
  {
    return unit;
  }

  public void setUnit(String unit)
  {
    this.unit = unit;
  }

  public String getColor()
  {
    return color;
  }

  public void setColor(String color)
  {
    this.color = color;
  }

  public String getDataChannel()
  {
    return dataChannel;
  }

  public void setDataChannel(String dataChannel)
  {
    this.dataChannel = dataChannel;
  }
  
  public List<String> getFunctions()
  {
    return functions;
  }
}
