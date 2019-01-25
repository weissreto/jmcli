package ch.rweiss.jmcli.chart;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
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
  private static final Pattern TIME = Pattern.compile("\\d?\\d:\\d\\d:\\d\\d( (PM|AM))? ");
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
  
  @Test
  public void time()
  {
    assertThat(normalizeTime(" 11:45:65 PM  ")).isEqualTo(" ??:??:??     ");
    assertThat(normalizeTime(" 2:45:65 PM  ")) .isEqualTo(" ??:??:??    ");
    assertThat(normalizeTime(" 2:45:65 AM  ")) .isEqualTo(" ??:??:??    ");
    assertThat(normalizeTime(" 11:45:65 AM  ")).isEqualTo(" ??:??:??     ");
    assertThat(normalizeTime(" 2:45:65  "))    .isEqualTo(" ??:??:?? ");
    assertThat(normalizeTime(" 02:45:65  "))   .isEqualTo(" ??:??:??  ");
    assertThat(normalizeTime(" 23:45:65  "))   .isEqualTo(" ??:??:??  ");
  }
  
  private void assertChart(String referenceFile) throws IOException
  {
    String reference = readReference(referenceFile);
    String testee = tester.stdOut();
    testee = normalizeTime(testee);
    testee = normalizeValues(testee);
    testee = adjustLineLengthOnChangedLines(testee);
    assertThat(testee).isEqualTo(reference);
  }

  public static String normalizeTime(String testee)
  {
    Matcher matcher = TIME.matcher(testee);
    StringBuffer sb = new StringBuffer(testee.length());
    while (matcher.find())
    {
      String replacement = replaceTime(matcher.toMatchResult());
      matcher.appendReplacement(sb, replacement);
    }
    matcher.appendTail(sb);
    return sb.toString();
  }
  
  private static String replaceTime(MatchResult result)
  {
    return "??:??:??" + StringUtils.repeat(' ', result.end()-result.start()-8);
  }
  
  public static String normalizeValues(String testee)
  {
    return testee.replaceAll("=\\d*", "=???");
  }

  public static String adjustLineLengthOnChangedLines(String testee)
  {
    return Arrays.stream(testee.split("\n"))
      .map(TestChart::adjustLine)
      .collect(Collectors.joining("\n"));
  }
  
  private static String adjustLine(String line)
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
    try (InputStream is = TestChart.class.getResourceAsStream(referenceFile))
    {
      return readReference(is);
    }
  }

  public static String readReference(InputStream inputStream) throws IOException
  {
    StringBuilder builder = new StringBuilder();
    try (Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8))
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
