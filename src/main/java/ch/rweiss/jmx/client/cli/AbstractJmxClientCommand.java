package ch.rweiss.jmx.client.cli;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import ch.rweiss.jmx.client.JmxClient;
import ch.rweiss.jmx.client.JmxException;
import ch.rweiss.jmx.client.Jvm;
import picocli.CommandLine.Option;

public abstract class AbstractJmxClientCommand extends AbstractHeaderCommand
{
  @Option(names = {"-j", "--jvm"}, description = "Process id or a part of the main class name or the host:port of the Java virtual machine")
  protected String jvm;
  
  @Option(names = {"-i", "--interval"}, description = "Refresh interval in seconds")
  protected int interval=0;

  protected JmxClient jmxClient;
  
  protected AbstractJmxClientCommand(String name)
  {
    super(name);
  }
  
  @Override
  public void run()
  {
    try(JmxClient client = connect())
    {
      this.jmxClient = client;
      if (interval > 0)
      {
        runPeriodically();
      }
      else
      {
        super.run();
      }
    }
    catch(IOException ex)
    {
      throw new JmxException("Cannot close jmx client", ex);
    }
  }
  
  protected JmxClient getJmxClient()
  {
    return jmxClient;
  }
  
  private JmxClient connect()
  {
    if (isHostAndPort())
    {
      return JmxClient.connectTo(getHost(), getPort());
    }
    Jvm attachableJvm = Jvm.runningJvm(jvm);
    if (attachableJvm == null)
    {
      throw new CommandException("Java virtual machine {0} not found.\nPlease specify a correct Java process id or Java main class name or a host:port.", jvm);
    }
    return attachableJvm.connect();
  }

  private boolean isHostAndPort()
  {
    return StringUtils.contains(jvm, ":");
  }

  private String getHost()
  {
    return StringUtils.substringBefore(jvm, ":");
  }
  
  private int getPort()
  {
    return Integer.parseInt(StringUtils.substringAfter(jvm, ":"));
  }

  private void runPeriodically()
  {
    term.clear().screen();
    term.cursor().hide();
    try
    {
      while(System.in.available()==0)
      {
        term.cursor().position(1, 1);
        beforeRun();
        super.run();
        afterRun();
        sleep();
      }
    }
    catch(IOException ex)
    {
      throw new JmxException("Input stream error", ex);
    }
    finally
    {
      term.cursor().show();
    }    
  }

  protected void afterRun()
  {
  }

  protected void beforeRun()
  {
  }

  private void sleep() throws IOException
  {
    try
    {
      int wait = interval*1000;
      while (wait > 0 && System.in.available() == 0)
      {
        Thread.sleep(100);
        wait = wait - 100;
      }
    }
    catch (InterruptedException ex)
    {
      throw new JmxException("Interrupted", ex);
    }
  }
  
  protected static String toErrorMessage(JmxException error)
  {
    String message;
    message = error.getShortDisplayMessage();
    return "<" + message + ">";
  }
}
