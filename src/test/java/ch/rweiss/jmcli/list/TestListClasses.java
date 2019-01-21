package ch.rweiss.jmcli.list;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import ch.rweiss.jmcli.CommandTester;
import ch.rweiss.jmcli.JmCli;
import ch.rweiss.jmx.client.Jvm;

public class TestListClasses
{
  private static final String STANDARD_COMMAND_OUTPUT = "\n"+
      "Classes\n"+
      "\n"+
      "Name                                                                                               Instances Bytes\n";

  @RegisterExtension
  public CommandTester tester = new CommandTester();

  @Test
  public void noOptions()
  {
    JmCli.main(new String[] {"list", "classes"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void intervalOptionShort()
  {
    JmCli.main(new String[] {"list", "classes", "-i", "1"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void intervalOptionLong()
  {
    JmCli.main(new String[] {"list", "classes", "--interval", "1"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void sortOptionShort()
  {
    JmCli.main(new String[] {"list", "classes", "-s", "Name"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void sortOptionLong()
  {
    JmCli.main(new String[] {"list", "classes", "--sort", "Name"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void sortInstancesColumn()
  {
    JmCli.main(new String[] {"list", "classes", "--sort", "Instances"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void sortBytesColumn()
  {
    JmCli.main(new String[] {"list", "classes", "--sort", "Bytes"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }
  
  @Test
  public void jvmOptionShort()
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"list", "classes", "-j", localJvm.id()});
    
    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void jvmOptionLong()
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"list", "classes", "--jvm", localJvm.id()});
    
    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void wrongJvmOption()
  {
    JmCli.main(new String[] {"list", "classes", "-j", "ThisJvmShouldNotBeFound"});

    tester.assertTrimmedStdOut().contains(
        "Java virtual machine 'ThisJvmShouldNotBeFound' not found.\n"+
        "Please specify a correct Java process id or main class name or a host:port.");
  }

  @Test
  public void packageFilter()
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"list", "classes", "-j", localJvm.id(), "java.lang"});

    tester.assertTrimmedStdOut()
        .contains("java.lang")
        .doesNotContain("java.util")
        .doesNotContain("ch.rweiss");
  }

  @Test
  public void multiplePackageFilter()
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"list", "classes", "-j", localJvm.id(), "java.lang", "java.util"});

    tester.assertTrimmedStdOut()
        .contains("java.lang")
        .contains("java.util")
        .doesNotContain("ch.rweiss");
  }

}
