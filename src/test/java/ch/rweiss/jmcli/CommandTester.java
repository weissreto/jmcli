package ch.rweiss.jmcli;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.assertj.core.api.AbstractStringAssert;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

import ch.rweiss.terminal.AnsiTerminal;
import ch.rweiss.terminal.AnsiTerminal.OffScreen;
import ch.rweiss.terminal.AnsiTerminalTester;
import ch.rweiss.terminal.Position;

public class CommandTester implements BeforeEachCallback, AfterEachCallback
{
  private PrintStream originalStdErr;
  private ByteArrayOutputStream stdErr;
  private Position maxTerminalPosition;

  public CommandTester()
  {
    this(null);
  }

  public CommandTester(Position maxTerminalPosition)
  {
    this.maxTerminalPosition = maxTerminalPosition;
  }
  
  @Override
  public void afterEach(ExtensionContext context) throws Exception
  {
    AnsiTerminal.get().offScreen().off();
    System.setErr(originalStdErr);
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception
  {
    OffScreen offScreen = AnsiTerminal.get().offScreen();
    if (maxTerminalPosition != null)
    {
      offScreen.on(maxTerminalPosition);
    }
    else
    {
      offScreen.on();
    }
    originalStdErr = System.err;
    stdErr = new ByteArrayOutputStream();
    System.setErr(new PrintStream(stdErr, true, StandardCharsets.UTF_16.name()));
  }

  public String stdOut()
  {
    return AnsiTerminalTester.dumpOffScreenBuffer();
  }
  
  public AbstractStringAssert<?> assertStdOut()
  {
    return assertThat(stdOut());
  }

  public AbstractStringAssert<?> assertTrimmedStdOut()
  {
    return assertThat(trimmedStdOut());
  }

  public AbstractStringAssert<?> assertStdErr()
  {
    return assertThat(new String(stdErr.toByteArray(), StandardCharsets.UTF_16));
  }

  public String trimmedStdOut()
  {
    String dump = stdOut();
    String trimmedDump = Arrays
        .stream(StringUtils.split(dump, '\n'))
        .map(String::trim)
        .collect(Collectors.joining("\n"));
    return StringUtils.stripEnd(trimmedDump, null);
  }
}
