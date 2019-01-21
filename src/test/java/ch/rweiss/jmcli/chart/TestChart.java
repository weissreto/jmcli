package ch.rweiss.jmcli.chart;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import ch.rweiss.jmcli.CommandTester;
import ch.rweiss.jmcli.JmCli;
import ch.rweiss.jmx.client.Jvm;
import ch.rweiss.terminal.Position;

public class TestChart
{
  private static final int OUT_WIDTH = 120;
  private static final char[] BUFFER = new char[1024];
  @RegisterExtension
  public CommandTester tester = new CommandTester(new Position(20, OUT_WIDTH));

  @Test
  public void cpu() throws IOException
  {
    JmCli.main(new String[] {"chart", "cpu"});
    assertChart("cpu.txt");
  }  

  @Test
  public void classes() throws IOException
  {
    JmCli.main(new String[] {"chart", "classes"});
    assertChart("classes.txt");
  }  

  @Test
  public void gc() throws IOException
  {
    JmCli.main(new String[] {"chart", "gc"});
    assertChart("gc.txt");
  }  

  @Test
  public void heapMemory() throws IOException
  {
    JmCli.main(new String[] {"chart", "heapMemory"});
    assertChart("heapMemory.txt");
  }  

  @Test
  public void nonHeapMemory() throws IOException
  {
    JmCli.main(new String[] {"chart", "nonHeapMemory"});
    assertChart("nonHeapMemory.txt");
  }  

  @Test
  public void threads() throws IOException
  {
    JmCli.main(new String[] {"chart", "threads"});
    assertChart("threads.txt");
  }  
  
  @Test
  public void user() throws IOException
  {
    JmCli.main(new String[] {"chart", "user", "java.lang:type=OperatingSystem.SystemCpuLoad+PERCENTAGE", "java.lang:type=OperatingSystem.ProcessCpuLoad+PERCENTAGE"});
    assertChart("user.txt");
  }
  
  @Test
  public void unitOptionShort() throws IOException
  {
    JmCli.main(new String[] {"chart", "user", "java.lang:type=OperatingSystem.SystemCpuLoad+PERCENTAGE", "java.lang:type=OperatingSystem.ProcessCpuLoad+PERCENTAGE", "-u", "%"});
    assertChart("userWithUnit.txt");
  }

  @Test
  public void unitOptionLong() throws IOException
  {
    JmCli.main(new String[] {"chart", "user", "java.lang:type=OperatingSystem.SystemCpuLoad+PERCENTAGE", "java.lang:type=OperatingSystem.ProcessCpuLoad+PERCENTAGE", "--unit", "%"});
    assertChart("userWithUnit.txt");
  }

  @Test
  public void titleOptionShort() throws IOException
  {
    JmCli.main(new String[] {"chart", "user", "java.lang:type=OperatingSystem.SystemCpuLoad+PERCENTAGE", "java.lang:type=OperatingSystem.ProcessCpuLoad+PERCENTAGE", "-t", "User"});
    assertChart("userWithTitle.txt");
  }

  @Test
  public void titleOptionLong() throws IOException
  {
    JmCli.main(new String[] {"chart", "user", "java.lang:type=OperatingSystem.SystemCpuLoad+PERCENTAGE", "java.lang:type=OperatingSystem.ProcessCpuLoad+PERCENTAGE", "--title", "User"});
    assertChart("userWithTitle.txt");
  }
  
  @Test
  public void intervalOptionShort() throws IOException
  {
    JmCli.main(new String[] {"chart", "cpu", "-i", "1"});
    assertChart("cpu.txt");
  }

  @Test
  public void intervalOptionLong() throws IOException
  {
    JmCli.main(new String[] {"chart", "cpu", "--interval", "1"});
    assertChart("cpu.txt");
  }
  
  @Test
  public void jvmOptionShort() throws IOException
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"chart", "cpu", "-j", localJvm.id()});
    assertChart("cpu.txt");
  }

  @Test
  public void jvmOptionLong() throws IOException
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"chart", "cpu", "--jvm", localJvm.id()});
    assertChart("cpu.txt");
  }

  @Test
  public void wrongJvmOption()
  {
    JmCli.main(new String[] {"chart", "cpu", "-j", "ThisJvmShouldNotBeFound"});

    tester.assertTrimmedStdOut().contains(
        "Java virtual machine 'ThisJvmShouldNotBeFound' not found.\n"+
        "Please specify a correct Java process id or main class name or a host:port.");
  }
  
  private void assertChart(String referenceFile) throws IOException
  {
    String reference = readReference(referenceFile);
    String testee = tester.stdOut();
    testee = replaceTime(testee);
    testee = replaceValues(testee);
    testee = adjustLineLengthOnChangedLines(testee);
    assertThat(testee).isEqualTo(reference);
  }

  private static String replaceTime(String testee)
  {
    return testee.replaceAll("\\d\\d:\\d\\d:\\d\\d", "??:??:??");
  }

  private static String replaceValues(String testee)
  {
    return testee.replaceAll("=\\d*", "=???");
  }

  private String adjustLineLengthOnChangedLines(String testee)
  {
    return Arrays.stream(testee.split("\n"))
      .map(this::adjustLine)
      .collect(Collectors.joining("\n"));
  }
  
  private String adjustLine(String line)
  {
    if (!line.contains("?"))
    {
      return line;
    }
    if (line.length() == OUT_WIDTH)
    {
      return line;
    }
    if (line.length() > OUT_WIDTH)
    {
      return line.substring(0, OUT_WIDTH);
    }
    return line + StringUtils.repeat(' ', OUT_WIDTH-line.length());
  }

  private static String readReference(String referenceFile) throws IOException
  {
    StringBuilder builder = new StringBuilder();
    try (Reader reader = new InputStreamReader(TestChart.class.getResourceAsStream(referenceFile), StandardCharsets.UTF_8))
    {
      int characters = 0;
      while ((characters = reader.read(BUFFER, 0, BUFFER.length)) > 0) 
      {
        builder.append(BUFFER, 0, characters);
      }
    }
    String reference = builder.toString();
    return reference.replace("\r\n", "\n");
  }
}
