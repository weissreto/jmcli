package ch.rweiss.jmcli.dashboard;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import ch.rweiss.jmcli.CommandTester;
import ch.rweiss.jmcli.JmCli;
import ch.rweiss.jmcli.chart.TestChart;
import ch.rweiss.jmx.client.Jvm;
import ch.rweiss.terminal.Position;

public class TestDashboard
{
  private static final int OUT_WIDTH = 120;
  @RegisterExtension
  public CommandTester tester = new CommandTester(new Position(20, OUT_WIDTH));

  @Test
  public void vm() throws IOException
  {
    JmCli.main(new String[] {"dashboard", "vm"});
    assertDashboard("vm.txt");
  }  
  
  @Test
  public void intervalOptionShort() throws IOException
  {
    JmCli.main(new String[] {"dashboard", "vm", "-i", "1"});
    assertDashboard("vm.txt");
  }

  @Test
  public void intervalOptionLong() throws IOException
  {
    JmCli.main(new String[] {"dashboard", "vm", "--interval", "1"});
    assertDashboard("vm.txt");
  }
  
  @Test
  public void jvmOptionShort() throws IOException
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"dashboard", "vm", "-j", localJvm.id()});
    assertDashboard("vm.txt");
  }

  @Test
  public void jvmOptionLong() throws IOException
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"dashboard", "vm", "--jvm", localJvm.id()});
    assertDashboard("vm.txt");
  }

  @Test
  public void wrongJvmOption()
  {
    JmCli.main(new String[] {"dashboard", "vm", "-j", "ThisJvmShouldNotBeFound"});

    tester.assertTrimmedStdOut().contains(
        "Java virtual machine 'ThisJvmShouldNotBeFound' not found.\n"+
        "Please specify a correct Java process id or main class name or a host:port.");
  }
    
  private void assertDashboard(String referenceFile) throws IOException
  {
    String reference = readReference(referenceFile);
    String testee = tester.stdOut();
    testee = TestChart.normalizeTime(testee);
    testee = normalizeValues(testee);
    testee = adjustValuePosition(testee, 2, 45);
    testee = adjustValuePosition(testee, 3, 85);
    testee = TestChart.adjustLineLengthOnChangedLines(testee);
    assertThat(testee).isEqualTo(reference);
  }
  
  public static String normalizeValues(String testee)
  {
    Matcher matcher = Pattern.compile("([0-9a-zA-Z][0-9a-zA-Z ]*)=(\\d*)").matcher(testee);
    StringBuffer stringBuilder = new StringBuffer();
    while(matcher.find())
    {
      String valueName = StringUtils.substring(matcher.group(1), 0, 3);
      matcher.appendReplacement(stringBuilder, valueName+"=???");
    }
    matcher.appendTail(stringBuilder);
    return stringBuilder.toString();
  }


  private static String adjustValuePosition(String testee, int valueGroup, int position)
  {
    return Arrays.stream(testee.split("\n"))
        .map(line -> adjustValuePositionInLine(line, valueGroup, position))
        .collect(Collectors.joining("\n"));
  }
  
  private static String adjustValuePositionInLine(String line, int valueGroup, int position)
  {
    int positionOfValueGroup = findPositionOfValueGroup(line, valueGroup);
    if (positionOfValueGroup < 0)
    {
      return line;
    }
    if (positionOfValueGroup > position)
    { 
      StringBuilder builder = new StringBuilder();
      builder.append(line.substring(0, position));
      builder.append(line.substring(positionOfValueGroup, line.length()));
      return builder.toString();
    }
    if (positionOfValueGroup < position)
    {
      StringBuilder builder = new StringBuilder();
      builder.append(line.substring(0, positionOfValueGroup));
      builder.append(StringUtils.repeat(' ', position-positionOfValueGroup));
      builder.append(line.substring(positionOfValueGroup, line.length()));
      return builder.toString();
    }
    return line;
  }
  
  private static int findPositionOfValueGroup(String line, int valueGroup)
  {
    Matcher matcher = Pattern.compile("  [0-9a-zA-Z][0-9a-zA-Z ]*=\\?\\?\\?").matcher(line);
    while(matcher.find())
    {
      valueGroup--;
      if (valueGroup == 0)
      {
        return matcher.start()+2;
      }
    }
    return -1;
  }

  private static String readReference(String referenceFile) throws IOException
  {
    try (InputStream is = TestDashboard.class.getResourceAsStream(referenceFile))
    {
      return TestChart.readReference(is);
    }
  }
}
