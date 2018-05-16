package ch.rweiss.jmx.client.cli;

import org.apache.commons.lang3.StringUtils;

import ch.rweiss.jmx.client.JmxException;
import ch.rweiss.terminal.AnsiTerminal;
import ch.rweiss.terminal.Style;

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

  protected void printNameValue(String name, String value)
  {
    printNameValue(0, name, value);
  }

  protected void printNameValue(int intend, String name, String value)
  {
    printName(intend, name);
    printSecondColumn(value);
    term.clear().lineToEnd();
    term.newLine();
  }
  
  protected static String toErrorMessage(JmxException error)
  {
    String message;
    message = error.getShortDisplayMessage();
    return "<" + message + ">";
  }

  protected void printNameTitle(String text)
  {
    printNameTitle(0, text);
  }

  protected void printNameTitle(int intend, String text)
  {
    printFirstColumn(intend, Styles.NAME_TITLE, text);
    term.clear().lineToEnd();
    term.newLine();
  }

  protected void printName(int intend, String text)
  {
    printFirstColumn(intend, Styles.NAME, text);
  }

  protected void printFirstColumn(Style style, String text)
  {
    printFirstColumn(0, style, text);
  }

  protected void printFirstColumn(int intend, Style style, String text)
  {
    term.cursor().column(1+intend);
    term.style(style);
    term.write(StringUtils.defaultString(text));
    term.reset();
  }

  protected void printSecondColumn(String text)
  {
    printSecondColumn(Styles.VALUE, text);
  }

  protected void printSecondColumn(Style style, String text)
  {
    term.cursor().column(40);
    term.style(style);
    term.write(StringUtils.defaultString(text));
    term.reset();
  }
}
