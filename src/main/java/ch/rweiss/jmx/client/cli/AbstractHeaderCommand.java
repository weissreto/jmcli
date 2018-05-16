package ch.rweiss.jmx.client.cli;

import ch.rweiss.jmx.client.JmxException;
import ch.rweiss.terminal.AnsiTerminal;

public abstract class AbstractHeaderCommand extends AbstractCommand
{
  protected AnsiTerminal term = AnsiTerminal.get();
  
  @Override
  public void run()
  {
    try
    {
      printHeader();
      execute();
    }
    finally
    {
      term.reset();
    }
  }
  
  protected void printHeader()
  {
    printEmptyLine();
    term.style(Styles.TITLE);
    printTitle();
    term.clear().lineToEnd();
    term.reset();
    printEmptyLine();
  }  
  
  abstract protected void printTitle();
  abstract protected void execute();

  protected void printEmptyLine()
  {
    term.clear().lineToEnd();
    term.newLine();
  }
  
  protected static String toErrorMessage(JmxException error)
  {
    String message;
    message = error.getShortDisplayMessage();
    return "<" + message + ">";
  }
}
