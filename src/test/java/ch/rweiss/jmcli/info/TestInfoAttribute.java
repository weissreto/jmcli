package ch.rweiss.jmcli.info;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import ch.rweiss.jmcli.CommandTester;
import ch.rweiss.jmcli.JmCli;
import ch.rweiss.jmx.client.Jvm;

public class TestInfoAttribute
{
  private static final String STANDARD_COMMAND_OUTPUT = "\n"+
                                                        "Attribute Info\n";
  
  @RegisterExtension
  public CommandTester tester = new CommandTester();

  @Test
  public void noOptions()
  {
    JmCli.main(new String[] {"info", "attribute"});

    tester.assertTrimmedStdOut().startsWith(
        STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void intervalOptionShort()
  {
    JmCli.main(new String[] {"info", "attribute", "-i", "1"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void intervalOptionLong()
  {
    JmCli.main(new String[] {"info", "attribute", "--interval", "1"});

    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }
  
  @Test
  public void jvmOptionShort()
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"info", "attribute", "-j", localJvm.id()});
    
    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void jvmOptionLong()
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"info", "attribute", "--jvm", localJvm.id()});
    
    tester.assertTrimmedStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void beanFilter() throws IOException
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"info", "attribute", "--jvm", localJvm.id(), "java.lang:type=Threading"});
    
    assertInfo("attribute.txt");
  }
  
  @Test
  public void beanAndAttributeFilter() throws IOException
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"info", "attribute", "--jvm", localJvm.id(), "java.lang:type=Threading", "ThreadCount"});
    
    assertInfo("attributeThreadCount.txt");
  }

  @Test
  public void beanAndWildcardAttributeFilter() throws IOException
  {
    Jvm localJvm = Jvm.localJvm();
    JmCli.main(new String[] {"info", "attribute", "--jvm", localJvm.id(), "java.lang:type=Threading", "*ThreadCount*"});
    
    assertInfo("attributeWildcardThreadCount.txt");
  }

  private void assertInfo(String referenceFileName) throws IOException
  {
    String actual = tester.trimmedStdOut();
    actual = actual.replaceAll(
        "Value                         [^\n]*", 
        "Value                         ???");
    assertThat(actual).isEqualTo(CommandTester.readReference(TestInfoAttribute.class, referenceFileName));
  }
}
