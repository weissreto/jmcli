package ch.rweiss.jmcli.info;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import ch.rweiss.jmcli.CommandTester;
import ch.rweiss.jmcli.JmCli;
import ch.rweiss.jmx.client.Jvm;

public class TestInfoOperation
{
  private static final String STANDARD_COMMAND_OUTPUT = "\n"+
                                                        "Operation Info\n";
  
  @RegisterExtension
  public CommandTester tester = new CommandTester();

  @Test
  public void noOptions()
  {
    JmCli.main(new String[] {"info", "operation"});

    tester.assertTrimmedStdOut().startsWith(
        STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void intervalOptionShort()
  {
    JmCli.main(new String[] {"info", "operation", "-i", "1"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void intervalOptionLong()
  {
    JmCli.main(new String[] {"info", "operation", "--interval", "1"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }
  
  @Test
  public void jvmOptionShort()
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"info", "operation", "-j", localJvm.id()});
    
    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void jvmOptionLong()
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"info", "operation", "--jvm", localJvm.id()});
    
    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void beanFilter() throws IOException
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"info", "operation", "--jvm", localJvm.id(), "java.lang:type=Threading"});
    
    assertInfo("operation.txt");
  }
  
  @Test
  public void beanAndOperationFilter() throws IOException
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"info", "operation", "--jvm", localJvm.id(), "java.lang:type=Threading", "findDeadlockedThreads"});
    
    assertInfo("operationDeadlockedThreads.txt");
  }

  @Test
  public void beanAndWildcardOperationFilter() throws IOException
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"info", "operation", "--jvm", localJvm.id(), "java.lang:type=Threading", "*find*"});
    
    assertInfo("operationWildcardFind.txt");
  }

  private void assertInfo(String referenceFileName) throws IOException
  {
    String actual = tester.trimmedStdOut();
    actual = actual.replaceAll(
        "Value                         [^\n]*", 
        "Value                         ???");
    assertThat(actual).isEqualTo(CommandTester.readReference(TestInfoOperation.class, referenceFileName));
  }
}
