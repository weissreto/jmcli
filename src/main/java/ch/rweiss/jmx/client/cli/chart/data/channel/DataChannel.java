package ch.rweiss.jmx.client.cli.chart.data.channel;

import ch.rweiss.check.Check;

public abstract class DataChannel
{
  private final String name;
  
  public DataChannel(String name)
  {
    Check.parameter("name").withValue(name).isNotNull();
    this.name = name;
  }
  
  public String name()
  {
    return name;
  }
  
  public abstract Object value();
}
