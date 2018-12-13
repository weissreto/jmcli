package ch.rweiss.jmcli;

import ch.rweiss.jmx.client.MBeanFilter;
import picocli.CommandLine.Parameters;

public abstract class AbstractBeanCommand extends AbstractCommand
{
  @Parameters(index="0", arity="0..1", paramLabel="BEAN", description="Bean name or filter with wildcards. E.g *:*, java.lang:*, java.lang:type=Memory")
  private String beanNameOrFilter = "*:*";

  public MBeanFilter beanFilter() 
  {
    return MBeanFilter.with(beanNameOrFilter);
  }
}
