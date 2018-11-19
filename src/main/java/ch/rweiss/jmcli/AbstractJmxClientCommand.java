package ch.rweiss.jmcli;

import java.util.Optional;

import ch.rweiss.jmx.client.JmxClient;
import ch.rweiss.jmx.client.JmxException;
import ch.rweiss.terminal.Key;
import picocli.CommandLine.Mixin;

public abstract class AbstractJmxClientCommand extends AbstractHeaderCommand
{
  @Mixin
  private JvmOption jvmOption = new JvmOption();

  @Mixin
  protected IntervalOption intervalOption = new IntervalOption();
  
  private boolean quit;
  
  protected AbstractJmxClientCommand(String name)
  {
    super(name);
  }
  
  @Override
  public void run()
  {
    jvmOption.connectAndRun(this::runPeriodicalOrOnce);
  }
  
  private void runPeriodicalOrOnce()
  {
    if (isPeriodical())
    {
      runPeriodical();
    }
    else
    {
      super.run();
    }
  }
  
  protected JmxClient jmxClient()
  {
    return jvmOption.jmxClient();
  }

  private void runPeriodical()
  {
    term.clear().screen();
    try
    {
      while(isNotQuit())
      {
        term.cursor().position(1, 1);
        term.cursor().hide();
        beforeRun();
        super.run();
        afterRun();
        sleep();
      }
    }
    finally
    {
      term.cursor().show();
    }    
  }
  

  protected void afterRun()
  {
    // does nothing here. Maybe used in sub classes to cleanup
  }

  protected void beforeRun()
  {
    // Does nothing here. Maybe used in sub classes to initialize
  }

  private void sleep() 
  {
    long wait = intervalOption.waitTime();
    Optional<Key> key = term.input().waitForKey(wait);
    key.ifPresent(k -> quit());      
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
  
  protected static String toErrorMessage(JmxException error)
  {
    String message;
    message = error.getShortDisplayMessage();
    return "<" + message + ">";
  }
}
