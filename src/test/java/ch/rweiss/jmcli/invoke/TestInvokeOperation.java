package ch.rweiss.jmcli.invoke;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

import ch.rweiss.jmcli.CommandTester;
import ch.rweiss.jmcli.JmCli;
import ch.rweiss.jmcli.set.RegisterMxBeanExtension;
import ch.rweiss.jmx.client.Jvm;
import ch.rweiss.jmx.client.MBeanName;

public class TestInvokeOperation
{
  private static final String STANDARD_COMMAND_OUTPUT = "\n"+
      "Invoke Operation\n"+
      "\n"+
      "java.lang:type=Memory\n"+
      "\n"+
      "Invoking operation            void gc()\n"+
      "with parameters               []\n"+
      "\n"+
      "Result                        null";

  @RegisterExtension
  public CommandTester tester = new CommandTester();
  
  private static final OperationTesterImpl OPERATION_TESTER = new OperationTesterImpl();
  
  @RegisterExtension
  public static final RegisterMxBeanExtension JMX = new RegisterMxBeanExtension("ch.rweiss.jmcli:name=OperationTester", OPERATION_TESTER);
    
  @Test
  public void noOptions()
  {
    JmCli.main(new String[] {"invoke", "operation", MBeanName.MEMORY.fullQualifiedName(), "gc"});

    tester.assertTrimmedStdOut().isEqualTo(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void intervalOptionShort()
  {
    JmCli.main(new String[] {"invoke", "operation", MBeanName.MEMORY.fullQualifiedName(), "gc", "-i", "1"});

    tester.assertTrimmedStdOut().isEqualTo(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void intervalOptionLong()
  {
    JmCli.main(new String[] {"invoke", "operation", MBeanName.MEMORY.fullQualifiedName(), "gc", "--interval", "1"});

    tester.assertTrimmedStdOut().isEqualTo(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void jvmOptionShort()
  {
    Jvm localJvm = Jvm.localJvm();

    JmCli.main(new String[] {"invoke", "operation", MBeanName.MEMORY.fullQualifiedName(), "gc", "-j", localJvm.id()});
    
    tester.assertTrimmedStdOut().isEqualTo(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void jvmOptionLong()
  {
    Jvm localJvm = Jvm.localJvm();
    
    JmCli.main(new String[] {"invoke", "operation", MBeanName.MEMORY.fullQualifiedName(), "gc", "--jvm", localJvm.id()});
    
    tester.assertTrimmedStdOut().isEqualTo(STANDARD_COMMAND_OUTPUT);
  }

  @Test
  public void wrongJvmOption()
  {
    JmCli.main(new String[] {"invoke", "operation", "-j", "ThisJvmShouldNotBeFound", MBeanName.MEMORY.fullQualifiedName(), "gc"});

    tester.assertTrimmedStdOut().contains(
        "Java virtual machine 'ThisJvmShouldNotBeFound' not found.\n"+
        "Please specify a correct Java process id or main class name or a host:port.");
  }
  
  @Test
  public void stringArg()
  {
    invokeOperation("stringArg", "Hello");
    assertResult("java.lang.String stringArg(java.lang.String p0)", "Hello1", "Hello");
  }

  @Test
  public void byteArg()
  {
    invokeOperation("byteArg", "15");
    assertResult("byte byteArg(byte p0)", "16", "15");
  }

  @Test
  public void shortArg()
  {
    invokeOperation("shortArg", "15");
    assertResult("short shortArg(short p0)", "16", "15");
  }

  @Test
  public void intArg()
  {
    invokeOperation("intArg", "15");
    assertResult("int intArg(int p0)", "16", "15");
  }

  @Test
  public void longArg()
  {
    invokeOperation("longArg", "15");
    assertResult("long longArg(long p0)", "16", "15");
  }

  @Test
  public void floatArg()
  {
    invokeOperation("floatArg", "15.56");
    assertResult("float floatArg(float p0)", "16.56", "15.56");
  }

  @Test
  public void doubleArg()
  {
    invokeOperation("doubleArg", "15.56");
    assertResult("double doubleArg(double p0)", "16.56", "15.56");
  }

  @Test
  public void charArg()
  {
    invokeOperation("charArg", "A");
    assertResult("char charArg(char p0)", "B", "A");
  }

  @Test
  public void booleanArg()
  {
    invokeOperation("booleanArg", "true");
    assertResult("boolean booleanArg(boolean p0)", "false", "true");
  }
  
  @Test
  public void multipleArgs()
  {
    invokeOperation("multipleArgs", "15", "31", "102");
    assertResult("int multipleArgs(int p0, int p1, int p2)", "148", "15", "31", "102");
  }
  
  @Test
  public void noOperationFound()
  {
    invokeOperation("missing");

    tester.assertTrimmedStdOut().contains(
        "Invoke Operation\n"+
        "\n"+
        "\n"+
        "\n"+
        "Operation missing not found for bean "+JMX.getName());
  }

  @Test
  public void wrongParameterCount()
  {
    invokeOperation("stringArg");

    tester.assertTrimmedStdOut().contains(
        "Invoke Operation\n"+
        "\n"+
        JMX.getName()+"\n"+    
        "\n"+
        "\n"+
        "\n"+
        "Wrong number of arguments given for operation stringArg. Expected 1 but was 0");
  }
  
  @Test
  public void overloadedMethodWrongParameterCount()
  {
    invokeOperation("oneArg");

    tester.assertTrimmedStdOut().contains(
        "Invoke Operation\n"+
        "\n"+
        "\n"+
        "\n"+
        "Operation oneArg with 0 parameters not found for bean "+JMX.getName());
  }
  
  @Test
  public void overloadedMethodSameParameterCount()
  {
    invokeOperation("oneArg", "Hi");

    tester.assertTrimmedStdOut().contains(
        "Invoke Operation\n"+
        "\n"+
        "\n"+
        "\n"+
        "More than one operation oneArg with 1 parameters found for bean "+JMX.getName()+".\n"+
        "Please specify operation signature.");
  }
  
  @Test
  public void overloadedMethod()
  {
    invokeOperation("oneArg", "1", "2");
    assertResult("int oneArg(int p0, int p1)", "3", "1", "2");
  }

  @Test
  public void signature()
  {
    invokeOperation("oneArg(int)", "1");
    assertResult("int oneArg(int p0)", "1", "1");
    
    invokeOperation("oneArg(java.lang.String)", "Hello");
    assertResult("java.lang.String oneArg(java.lang.String p0)", "Hello", "Hello");
  }

  private static void invokeOperation(String operation, String... args)
  {
    Jvm localJvm = Jvm.localJvm();
    List<String> cmdArgs = new ArrayList<>();
    cmdArgs.add("invoke");
    cmdArgs.add("operation");
    cmdArgs.add("-j");
    cmdArgs.add(localJvm.id());
    cmdArgs.add(JMX.getName());
    cmdArgs.add(operation);
    cmdArgs.addAll(Arrays.asList(args));
    
    JmCli.main(cmdArgs.toArray(new String[cmdArgs.size()]));
  }

  private void assertResult(String operationSignature, String result, String... parameters)
  {
    tester.assertTrimmedStdOut().contains(
        "Invoke Operation\n"+
        "\n"+
        JMX.getName()+"\n"+
        "\n"+
        "Invoking operation            "+operationSignature+"\n"+
        "with parameters               ["+Arrays.stream(parameters).collect(Collectors.joining(", "))+"]\n"+
        "\n"+
        "Result                        "+result);
  }
}
