package ch.rweiss.jmcli.list;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import ch.rweiss.jmcli.CommandTester;
import ch.rweiss.jmcli.JmCli;
import ch.rweiss.jmx.client.Jvm;

public class TestListMethods
{
  private static final String STANDARD_COMMAND_OUTPUT = "\n"+
      "Methods\n"+
      "\n"+
      "Name                                                                            Self      Waiting   Blocked   Total\n";

  @RegisterExtension
  public CommandTester tester = new CommandTester();

  @Test
  public void noOptions()
  {
    JmCli.main(new String[] {"list", "methods"});

    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void intervalOptionShort()
  {
    JmCli.main(new String[] {"list", "methods", "-i", "1"});

    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void intervalOptionLong()
  {
    JmCli.main(new String[] {"list", "methods", "--interval", "1"});

    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void sortOptionShort()
  {
    JmCli.main(new String[] {"list", "methods", "-s", "Name"});

    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void sortOptionLong()
  {
    JmCli.main(new String[] {"list", "methods", "--sort", "Name"});

    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void sortSelfColumn()
  {
    JmCli.main(new String[] {"list", "methods", "--sort", "Self"});

    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void sortWaitingColumn()
  {
    JmCli.main(new String[] {"list", "methods", "--sort", "Waiting"});

    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }
  
  @Test
  public void sortBlockedColumn()
  {
    JmCli.main(new String[] {"list", "methods", "--sort", "Blocked"});

    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void sortTotalColumn()
  {
    JmCli.main(new String[] {"list", "methods", "--sort", "Total"});

    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void jvmOptionShort()
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"list", "methods", "-j", localJvm.id()});
    
    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void jvmOptionLong()
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"list", "methods", "--jvm", localJvm.id()});
    
    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void wrongJvmOption()
  {
    JmCli.main(new String[] {"list", "methods", "-j", "ThisJvmShouldNotBeFound"});

    tester.assertStdOut().contains(
        "Java virtual machine 'ThisJvmShouldNotBeFound' not found.\n"+
        "Please specify a correct Java process id or main class name or a host:port.");
  }
}
