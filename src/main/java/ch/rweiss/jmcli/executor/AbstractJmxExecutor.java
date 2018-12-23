package ch.rweiss.jmcli.executor;

import java.util.Optional;

import ch.rweiss.jmcli.IntervalOption;
import ch.rweiss.jmcli.JvmOption;
import ch.rweiss.jmcli.ui.CommandUi;
import ch.rweiss.jmx.client.JmxClient;
import ch.rweiss.terminal.AnsiTerminal;
import ch.rweiss.terminal.Key;

public abstract class AbstractJmxExecutor extends AbstractSimpleExecutor
{
  private final IntervalOption intervalOption;
  private final JvmOption jvmOption;
  
  private boolean quit;
  
  protected AbstractJmxExecutor(String name, IntervalOption intervalOption, JvmOption jvmOption)
  {
    super(name);
    this.intervalOption = intervalOption;
    this.jvmOption = jvmOption;
  }
  
  @Override
  public final void execute()
  {
    jvmOption.connectAndRun(this::executePeriodicalOrOnce);
  }
  
  @Override
  protected final void execute(CommandUi ui)
  {
    execute(ui, jvmOption.jmxClient());
  }
  
  protected abstract void execute(CommandUi ui, JmxClient jmxClient);

  private void executePeriodicalOrOnce()
  {
    if (isPeriodical())
    {
      executePeriodical();
    }
    else
    {
      super.execute();
    }
  }
  
  private void executePeriodical()
  {
    AnsiTerminal terminal = ui().terminal();
    terminal.clear().screen();
    terminal.cursor().hide();
    try
    {
      while(isNotQuit())
      {
        terminal.cursor().position(1, 1);
        super.execute();
        terminal.clear().screenToEnd();
        sleep();
      }
    }
    finally
    {
      terminal.cursor().show();
    }    
  }
  
  private void sleep() 
  {
    if (ui().terminal().isAnsi())
    {
      long wait = intervalOption.waitTime();
      Optional<Key> key = ui().terminal().input().waitForKey(wait);
      key.ifPresent(k -> quit());
    }
    else
    {
      quit();
    }
  }
  
  private boolean isNotQuit()
  {
    return !quit;
  }

  private void quit()
  {
    quit = true;
  }
  
  protected boolean isPeriodical()
  {
    return intervalOption.isPeriodical();
  }
  
  protected JmxClient jmxClient()
  {
    return jvmOption.jmxClient();
  }
}
