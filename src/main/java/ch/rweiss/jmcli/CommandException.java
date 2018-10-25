package ch.rweiss.jmcli;

import java.text.MessageFormat;

public class CommandException extends RuntimeException
{
  public CommandException(String errorMessage, Object... arguments)
  {
    super(MessageFormat.format(errorMessage, arguments));
  }

  public CommandException(Exception cause, String errorMessage, Object... arguments)
  {
    super(MessageFormat.format(errorMessage, arguments), cause);
  }
}
