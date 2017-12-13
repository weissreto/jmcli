package ch.weiss.jmx.client.cli;

import java.io.IOException;

import org.apache.commons.lang3.StringUtils;

import ch.weiss.jmx.client.JmxClient;
import ch.weiss.jmx.client.JmxException;
import ch.weiss.jmx.client.Jvm;
import picocli.CommandLine.Option;

public abstract class AbstractJmxClientCommand extends AbstractHeaderCommand
{
  @Option(names = {"-j", "--jvm"}, description = "Process id or a part of the main class name or the host:port of the Java virtual machine", required = true)
  protected String jvm;
  
  @Option(names = {"-i", "--interval"}, description = "Refresh interval in seconds")
  protected int interval=0;

  protected JmxClient jmxClient;
  
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
    term.cursor().hide();
    term.clear().screen();
    try
    {
      while(System.in.available()==0)
      {
        term.cursor().position(1, 1);
        super.run();
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
}
