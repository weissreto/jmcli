package ch.rweiss.jmcli;

import org.apache.commons.lang3.StringUtils;

import ch.rweiss.jmx.client.JmxClient;
import ch.rweiss.jmx.client.Jvm;
import picocli.CommandLine.Option;

public class JvmOption
{
  @Option(names = {"-j", "--jvm"}, description = "Process id or a part of the main class name or the host:port of the Java virtual machine")
  protected String jvm;
  private JmxClient client;

  public void connectAndRun(Runnable runnable)
  {
    try(JmxClient jmxClient = connect())
    {
      this.client = jmxClient;
      runnable.run();
    }
  }
  
  public JmxClient jmxClient()
  {
    return client;
  }

  private JmxClient connect()
  {
    if (isHostAndPort())
    {
      return JmxClient.connectTo(host(), port());
    }
    Jvm attachableJvm = Jvm.runningJvm(jvm);
    if (attachableJvm == null)
    {
      if (StringUtils.isBlank(jvm))
      {
        throw new CommandException("No java virtual machine found");
      }
      throw new CommandException("Java virtual machine ''{0}'' not found.\nPlease specify a correct Java process id or main class name or a host:port.", jvm);
    }
    return attachableJvm.connect();
  }

  private boolean isHostAndPort()
  {
    return StringUtils.contains(jvm, ":");
  }

  private String host()
  {
    return StringUtils.substringBefore(jvm, ":");
  }
  
  private int port()
  {
    return Integer.parseInt(StringUtils.substringAfter(jvm, ":"));
  }
}
