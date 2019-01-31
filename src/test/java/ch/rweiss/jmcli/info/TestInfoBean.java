package ch.rweiss.jmcli.info;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import ch.rweiss.jmcli.CommandTester;
import ch.rweiss.jmcli.JmCli;
import ch.rweiss.jmx.client.Jvm;

public class TestInfoBean
{
  private static final String STANDARD_COMMAND_OUTPUT = "\n"+
                                                        "Bean Info\n";
  
  @RegisterExtension
  public CommandTester tester = new CommandTester();

  @Test
  public void noOptions()
  {
    JmCli.main(new String[] {"info", "bean"});

    tester.assertTrimmedStdOut().startsWith(
        STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void intervalOptionShort()
  {
    JmCli.main(new String[] {"info", "bean", "-i", "1"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void intervalOptionLong()
  {
    JmCli.main(new String[] {"info", "bean", "--interval", "1"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }
  
  @Test
  public void jvmOptionShort()
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"info", "bean", "-j", localJvm.id()});
    
    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void jvmOptionLong()
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"info", "bean", "--jvm", localJvm.id()});
    
    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void beanFilter() throws IOException
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"info", "bean", "--jvm", localJvm.id(), "java.lang:type=Threading"});
    
    tester.assertTrimmedStdOut().isEqualTo(CommandTester.readReference(TestInfoBean.class, "bean.txt"));
  }
}
