package ch.rweiss.jmcli.set;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import ch.rweiss.jmcli.CommandTester;
import ch.rweiss.jmcli.JmCli;
import ch.rweiss.jmx.client.Jvm;
import ch.rweiss.jmx.client.MBeanName;

public class TestSetAttribute
{
  private static final String STANDARD_COMMAND_OUTPUT = "\n"+
      "Set Attribute\n"+
      "\n"+
      "java.lang:type=Threading\n"+
      "\n"+
      "Setting attribute             ThreadCpuTimeEnabled\n"+
      "Value (Before)                false\n"+
      "Value (Now)                   false";

  private static final String SET_TEST_ATTRIBUTE_OUTPUT = "\n"+
      "Set Attribute\n"+
      "\n"+
      "ch.rweiss.jmcli:name=AttributeTester\n"+
      "\n"+
      "Setting attribute             TestAttr\n"+
      "Value (Before)                Hello\n"+
      "Value (Now)                   World";

  @RegisterExtension
  public CommandTester tester = new CommandTester();

  private static final AttributeTesterImpl ATTRIBUTE_TESTER = new AttributeTesterImpl();
  
  @RegisterExtension
  public static final RegisterMxBeanExtension JMX = new RegisterMxBeanExtension("ch.rweiss.jmcli:name=AttributeTester", ATTRIBUTE_TESTER);
  
  @BeforeAll
  public static void beforeAll()
  {
    JmCli.main(new String[] {"set", "attribute", MBeanName.THREAD.fullQualifiedName(), "ThreadCpuTimeEnabled", "false"});
  }
  
  @Test
  public void noOptions()
  {
    JmCli.main(new String[] {"set", "attribute", MBeanName.THREAD.fullQualifiedName(), "ThreadCpuTimeEnabled", "false"});

    tester.assertStdOut().isEqualTo(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void intervalOptionShort()
  {
    JmCli.main(new String[] {"set", "attribute", MBeanName.THREAD.fullQualifiedName(), "ThreadCpuTimeEnabled", "false", "-i", "1"});

    tester.assertStdOut().isEqualTo(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void intervalOptionLong()
  {
    JmCli.main(new String[] {"set", "attribute", MBeanName.THREAD.fullQualifiedName(), "ThreadCpuTimeEnabled", "false", "--interval", "1"});

    tester.assertStdOut().isEqualTo(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void jvmOptionShort()
  {
    ATTRIBUTE_TESTER.setTestAttr("Hello");
    assertThat(ATTRIBUTE_TESTER.getTestAttr()).isEqualTo("Hello");
    Jvm localJvm = Jvm.localJvm();

    JmCli.main(new String[] {"set", "attribute", JMX.getName(), "TestAttr", "World", "-j", localJvm.id()});
    
    tester.assertStdOut().isEqualTo(SET_TEST_ATTRIBUTE_OUTPUT);
    assertThat(ATTRIBUTE_TESTER.getTestAttr()).isEqualTo("World");
  }

  @Test
  public void jvmOptionLong()
  {
    ATTRIBUTE_TESTER.setTestAttr("Hello");
    assertThat(ATTRIBUTE_TESTER.getTestAttr()).isEqualTo("Hello");
    Jvm localJvm = Jvm.localJvm();
    
    JmCli.main(new String[] {"set", "attribute", JMX.getName(), "TestAttr", "World", "--jvm", localJvm.id()});
    
    tester.assertStdOut().isEqualTo(SET_TEST_ATTRIBUTE_OUTPUT);
    assertThat(ATTRIBUTE_TESTER.getTestAttr()).isEqualTo("World");
  }

  @Test
  public void wrongJvmOption()
  {
    JmCli.main(new String[] {"set", "attribute", "-j", "ThisJvmShouldNotBeFound", JMX.getName(), "TestAttr", "World"});

    tester.assertStdOut().contains(
        "Java virtual machine 'ThisJvmShouldNotBeFound' not found.\n"+
        "Please specify a correct Java process id or main class name or a host:port.");
  }
}
