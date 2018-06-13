package ch.rweiss.jmx.client.cli.histo;

public class ClassInfo
{
  private final String name;
  private final long instances;
  private final long bytes;

  public ClassInfo(String name, long instances, long bytes)
  {
    this.name = name;
    this.instances = instances;
    this.bytes = bytes;
  }
  
  public String name()
  {
    return name;
  }
  
  public long instances()
  {
    return instances;
  }
  
  public long bytes()
  {
    return bytes;
  }
}
