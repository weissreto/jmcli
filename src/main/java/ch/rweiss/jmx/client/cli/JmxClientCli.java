package ch.rweiss.jmx.client.cli;

import java.util.Arrays;

import org.apache.commons.lang3.exception.ExceptionUtils;

import ch.rweiss.jmx.client.JmxException;
import ch.rweiss.jmx.client.cli.chart.Chart;
import ch.rweiss.jmx.client.cli.dashboard.Dashboard;
import ch.rweiss.jmx.client.cli.info.Info;
import ch.rweiss.jmx.client.cli.invoke.Invoke;
import ch.rweiss.jmx.client.cli.list.List;
import ch.rweiss.jmx.client.cli.list.ListVirtualMachines;
import ch.rweiss.jmx.client.cli.set.Set;
import ch.rweiss.terminal.AnsiTerminal;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.ExecutionException;
import picocli.CommandLine.Help;

@Command(
  name="jmcli", 
  description="Java Management Command Line Interface",
  version= {"Java Management Command Line Interface", "0.1", "(c) 2018"},
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
    Info.class,
    Set.class,
    Invoke.class,
    Chart.class,
    Dashboard.class
})
public class JmxClientCli extends AbstractCommand
{
  public static void main(String[] args)
  {
    try
    {
      CommandLine.run(new JmxClientCli(), System.err, Help.Ansi.ON, args);
    }
    catch(ExecutionException ex)
    {
      printError(ex, args);
    }
  }

  private static void printError(ExecutionException ex, String[] args)
  {
    AnsiTerminal term = AnsiTerminal.get();
    term.style(Styles.ERROR);
    term.newLine();
    term.newLine();
    String message = getRootMessage(ex);
    term.write(message);
    term.newLine();
    term.newLine();
    if (Arrays.asList(args).stream().filter(arg -> arg.equals("-v")).findAny().isPresent())
    {
      term.write(ExceptionUtils.getStackTrace(ex));
    }
    term.reset();
  }

  private static String getRootMessage(ExecutionException ex)
  {
    if (ex.getCause() instanceof CommandException)
    {
      return ex.getCause().getMessage();
    }
    if (ex.getCause() instanceof JmxException)
    {
      JmxException jmxEx = (JmxException) ex.getCause();
      if (jmxEx.getCause() != null)
      {
        return ex.getCause().getMessage()+" because of "+ExceptionUtils.getRootCauseMessage(jmxEx);
      }
      return ex.getCause().getMessage();
    }
    return ex.getMessage();
  }

  @Override
  public void run()
  {
    new ListVirtualMachines().run();
  }
}
