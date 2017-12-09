package ch.weiss.jmx.client.cli;

import ch.weiss.jmx.client.cli.info.Info;
import ch.weiss.jmx.client.cli.list.List;
import ch.weiss.jmx.client.cli.list.ListVirtualMachines;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Help;

@Command(
  name="jmcli", 
  description="Java Management Command Line Interface",
  version= {"Java Management Command Line Interface", "0.1", "(c) 2017"},
  header="@|fg(yellow)"+
         "        _   __  __    _____   _   _ \r\n" + 
         "      | | |  \\/  |  / ____| | | (_)\r\n" + 
         "      | | | \\  / | | |      | |  _ \r\n" + 
         "  _   | | | |\\/| | | |      | | | |\r\n" + 
         " | |__| | | |  | | | |____  | | | |\r\n" + 
         "  \\____/  |_|  |_|  \\_____| |_| |_|\r\n" +
         "|@",
  subcommands = {
    List.class,
    Info.class
})
public class JmxClientCli extends AbstractCommand
{
  public static void main(String[] args)
  {
    CommandLine.run(new JmxClientCli(), System.err, Help.Ansi.ON, args);    
  }

  @Override
  public void run()
  {
    new ListVirtualMachines().run();
  }
}
