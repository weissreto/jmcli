package ch.rweiss.jmcli.info;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import ch.rweiss.jmcli.CommandTester;
import ch.rweiss.jmcli.JmCli;
import ch.rweiss.jmx.client.Jvm;

public class TestInfoVirtualMaschine
{
  private static final String STANDARD_COMMAND_OUTPUT = "\n"+
                                                        "Java Virtual Machine Info\n";
  
  @RegisterExtension
  public CommandTester tester = new CommandTester();

  @Test
  public void noOptions()
  {
    JmCli.main(new String[] {"info", "vm"});

    tester.assertTrimmedStdOut().startsWith(
        STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void intervalOptionShort()
  {
    JmCli.main(new String[] {"info", "vm", "-i", "1"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void intervalOptionLong()
  {
    JmCli.main(new String[] {"info", "vm", "--interval", "1"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }
  
  @Test
  public void jvmOptionShort()
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"info", "vm", "-j", localJvm.id()});
    
    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void jvmOptionLong()
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"info", "vm", "--jvm", localJvm.id()});
    
    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void information()
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"info", "vm", "--jvm", localJvm.id()});
    
    tester.assertTrimmedStdOut()
        .startsWith(STANDARD_COMMAND_OUTPUT)
        .contains("Name")
        .contains("Version")
        .contains("Up since")
        .contains("Input Arguments")
        .contains("Classpath");
  }
}
