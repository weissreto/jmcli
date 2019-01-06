package ch.rweiss.jmcli.list;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import ch.rweiss.jmcli.CommandTester;
import ch.rweiss.jmcli.JmCli;
import ch.rweiss.jmx.client.Jvm;

public class TestListThreads
{
  private static final String STANDARD_COMMAND_OUTPUT = "\n"+
      "Threads\n"+
      "\n"+
      "Name                                               State          Cpu      User     Waited   Waited   Blocked  Blocked\n";

  @RegisterExtension
  public CommandTester tester = new CommandTester();

  @Test
  public void noOptions()
  {
    JmCli.main(new String[] {"list", "threads"});

    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void intervalOptionShort()
  {
    JmCli.main(new String[] {"list", "threads", "-i", "1"});

    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void intervalOptionLong()
  {
    JmCli.main(new String[] {"list", "threads", "--interval", "1"});

    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void jvmOptionShort()
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"list", "threads", "-j", localJvm.id()});
    
    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void jvmOptionLong()
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"list", "threads", "--jvm", localJvm.id()});
    
    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void wrongJvmOption()
  {
    JmCli.main(new String[] {"list", "threads", "-j", "ThisJvmShouldNotBeFound"});

    tester.assertStdOut().contains(
        "Java virtual machine 'ThisJvmShouldNotBeFound' not found.\n"+
        "Please specify a correct Java process id or main class name or a host:port.");
  }
  
  @Test
  public void sortOptionShort()
  {
    JmCli.main(new String[] {"list", "threads", "-s", "Name"});

    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void sortOptionLong()
  {
    JmCli.main(new String[] {"list", "threads", "--sort", "Name"});

    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void sortStateColumn()
  {
    JmCli.main(new String[] {"list", "threads", "--sort", "State"});

    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void sortCpuColumn()
  {
    JmCli.main(new String[] {"list", "threads", "--sort", "Cpu"});

    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void sortUserColumn()
  {
    JmCli.main(new String[] {"list", "threads", "--sort", "User"});

    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void sortWaitingColumn()
  {
    JmCli.main(new String[] {"list", "threads", "--sort", "Waited"});

    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void sortBlockedColumn()
  {
    JmCli.main(new String[] {"list", "threads", "--sort", "Blocked"});

    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

}
