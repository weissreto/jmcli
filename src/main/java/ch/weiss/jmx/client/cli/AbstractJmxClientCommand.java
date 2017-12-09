package ch.weiss.jmx.client.cli;

import java.io.IOException;

import ch.weiss.jmx.client.JmxClient;
import ch.weiss.jmx.client.JmxException;
import ch.weiss.jmx.client.Jvm;
import picocli.CommandLine.Option;

public abstract class AbstractJmxClientCommand extends AbstractHeaderCommand
{
  @Option(names = {"-v", "--vm"}, description = "Id or part of the name of the virtual machine", required = true)
  protected String vmIdOrName;
  
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
    Jvm jvm = Jvm.runningJvm(vmIdOrName);
    if (jvm == null)
    {
      throw new IllegalArgumentException("Virtual machine with id " + vmIdOrName + " not found");
    }
    return jvm.connect();
  }

  private void runPeriodically()
  {
    term.clear();
    term.cursor().hide();
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
