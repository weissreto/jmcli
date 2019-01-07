package ch.rweiss.jmcli.set;

public final class AttributeTesterImpl implements AttributeTesterMXBean
{
  private String testAttr;
    
  @Override
  public String getTestAttr()
  {
    return testAttr;
  }
  
  @Override
  public void setTestAttr(String testAttr)
  {
    this.testAttr = testAttr;
  }

}