package ch.weiss.jmx.client.cli;

import java.text.MessageFormat;

public class CommandException extends RuntimeException
{
  public CommandException(String errorMessage, Object... arguments)
  {
    super(MessageFormat.format(errorMessage, arguments));
  }
}
