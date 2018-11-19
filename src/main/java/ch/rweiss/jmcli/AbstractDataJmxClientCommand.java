package ch.rweiss.jmcli;

import ch.rweiss.jmx.client.JmxClient;
import ch.rweiss.terminal.AnsiTerminal;
import picocli.CommandLine.Mixin;

public abstract class AbstractDataJmxClientCommand extends AbstractDataCommand
{
  @Mixin 
  private JvmOption jvmOption = new JvmOption(); 
  
  protected AbstractDataJmxClientCommand(String name)
  {
    super(name);
  }
  
  @Override
  public void run()
  {
    jvmOption.connectAndRun(super::run);
  }
  
  @Override
  protected final void init(AnsiTerminal terminal)
  {
    init(terminal, jvmOption.jmxClient());
  }
  
  /**
   * Init
   * @param terminal
   * @param jmxClient
   */
  protected void init(AnsiTerminal terminal, JmxClient jmxClient)
  {    
    // does nothing by default
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
