package ch.rweiss.jmcli.executor;

import ch.rweiss.jmcli.IntervalOption;
import ch.rweiss.jmcli.JvmOption;
import ch.rweiss.jmx.client.JmxClient;
import picocli.CommandLine.Mixin;

public abstract class AbstractJmxDataExecutor extends AbstractDataExecutor
{
  @Mixin 
  private JvmOption jvmOption = new JvmOption(); 
  
  protected AbstractJmxDataExecutor(String name, IntervalOption intervalOption, JvmOption jvmOption)
  {
    super(name, intervalOption);
    this.jvmOption = jvmOption;
  }
  
  @Override
  public void execute()
  {
    jvmOption.connectAndRun(super::execute);
  }
  
  @Override
  protected final void gatherData()
  {
    gatherDataFrom(jvmOption.jmxClient());
  }

  /**
   * Gather data from provided jmx client
   * @param jmxClient
   */
  protected abstract void gatherDataFrom(JmxClient jmxClient);
}
