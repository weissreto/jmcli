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
import ch.rweiss.terminal.AnsiTerminalTester;

public class CommandTester implements BeforeEachCallback, AfterEachCallback
{
  private PrintStream originalStdErr;
  private ByteArrayOutputStream stdErr;

  @Override
  public void afterEach(ExtensionContext context) throws Exception
  {
    AnsiTerminal.get().offScreen().off();
    System.setErr(originalStdErr);
  }

  @Override
  public void beforeEach(ExtensionContext context) throws Exception
  {
    AnsiTerminal.get().offScreen().on();
    originalStdErr = System.err;
    stdErr = new ByteArrayOutputStream();
    System.setErr(new PrintStream(stdErr, true, StandardCharsets.UTF_16.name()));
  }

  public AbstractStringAssert<?> assertStdOut()
  {
    String dump = AnsiTerminalTester.dumpOffScreenBuffer();
    String trimmedDump = Arrays
        .stream(StringUtils.split(dump, '\n'))
        .map(String::trim)
        .collect(Collectors.joining("\n"));
    return assertThat(trimmedDump);
  }

  public AbstractStringAssert<?> assertStdErr()
  {
    return assertThat(new String(stdErr.toByteArray(), StandardCharsets.UTF_16));
  }
}
