package ch.rweiss.jmcli.list;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collections;
import java.util.Comparator;

import org.apache.commons.lang3.StringUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import ch.rweiss.jmcli.CommandTester;
import ch.rweiss.jmcli.JmCli;
import ch.rweiss.jmx.client.Jvm;

public class TestListVirtualMaschine
{
  private static final Comparator<Jvm> JVM_ID_ASC = Comparator.comparing(Jvm::id);
  private static final Comparator<Jvm> JVM_ID_DESC = JVM_ID_ASC.reversed();

  public static final String STANDARD_COMMAND_OUTPUT = "\n"+
      "Java Virtual Maschines\n"+
      "\n"+
      "Id        Display Name\n";

  @RegisterExtension
  public CommandTester tester = new CommandTester();

  @Test
  public void noOptions()
  {
    java.util.List<Jvm> jvms = Jvm.getAvailableRunningJvms();

    JmCli.main(new String[] {"list", "vm"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
    assertThat(jvms.size()).isGreaterThanOrEqualTo(2);
    for (Jvm jvm : jvms)
    {
      tester.assertTrimmedStdOut()
          .contains(jvm.id())
          .contains(StringUtils.substring(jvm.displayName(),0, 80));
    }
  }

  @Test
  public void intervalOptionShort()
  {
    JmCli.main(new String[] {"list", "vm", "-i", "1"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void intervalOptionLong()
  {
    JmCli.main(new String[] {"list", "vm", "--interval", "1"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void sortOptionShort()
  {
    JmCli.main(new String[] {"list", "vm", "-s", "Id"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
    assertSortedBy(JVM_ID_DESC);
  }

  @Test
  public void sortOptionShortAscending()
  {
    JmCli.main(new String[] {"list", "vm", "-s", "Id:a"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
    assertSortedBy(JVM_ID_ASC);
  }

  @Test
  public void sortOptionShortAscending2()
  {
    JmCli.main(new String[] {"list", "vm", "-s", "Id:asc"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
    assertSortedBy(JVM_ID_ASC);
  }

  @Test
  public void sortOptionShortAscending3()
  {
    JmCli.main(new String[] {"list", "vm", "-s", "Id:ascending"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
    assertSortedBy(JVM_ID_ASC);
  }
  
  @Test
  public void sortOptionShortAscending4()
  {
    JmCli.main(new String[] {"list", "vm", "-s", "Id:ASC"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
    assertSortedBy(JVM_ID_ASC);
  }

  @Test
  public void sortOptionShortDescending()
  {
    JmCli.main(new String[] {"list", "vm", "-s", "Id:d"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
    assertSortedBy(JVM_ID_DESC);
  }

  @Test
  public void sortOptionShortDescending2()
  {
    JmCli.main(new String[] {"list", "vm", "-s", "Id:desc"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
    assertSortedBy(JVM_ID_DESC);
  }

  @Test
  public void sortOptionShortDescending3()
  {
    JmCli.main(new String[] {"list", "vm", "-s", "Id:descending"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
    assertSortedBy(JVM_ID_DESC);
  }
  
  @Test
  public void sortOptionShortDescending4()
  {
    JmCli.main(new String[] {"list", "vm", "-s", "Id:DESC"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
    assertSortedBy(JVM_ID_DESC);
  }

  @Test
  public void sortOptionLong()
  {
    JmCli.main(new String[] {"list", "vm", "--sort", "Id"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
    assertSortedBy(JVM_ID_DESC);
  }

  private void assertSortedBy(Comparator<Jvm> sortBy)
  {
    java.util.List<Jvm> jvms = Jvm.getAvailableRunningJvms();
    Collections.sort(jvms, sortBy);
    assertThat(jvms.size()).isGreaterThanOrEqualTo(2);
    String stdOut = tester.trimmedStdOut();
    int pos = 0;
    for (Jvm jvm : jvms)
    {
      int nextPos = stdOut.indexOf(jvm.id());
      assertThat(nextPos).isGreaterThan(pos);
      pos = nextPos;
    }
  }

}
