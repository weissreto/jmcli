package ch.rweiss.jmx.client.cli.info;

import ch.rweiss.jmx.client.cli.AbstractJmxClientCommand;
import ch.rweiss.jmx.client.MAttribute;
import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.MBeanName;
import picocli.CommandLine.Command;

@Command(name="vm", description="Prints information about a virtual machine")
public class InfoVirtualMachine extends AbstractJmxClientCommand
{

  @Override
  protected void printTitle()
  {
    term.write("Virtual Machine Info");
  }

  @Override
  protected void execute()
  {
    MBean runtime = getJmxClient().bean(MBeanName.RUNTIME);
    MBean jmImpl = getJmxClient().bean(MBeanName.createFor("JMImplementation:type=MBeanServerDelegate"));
    term.newLine();
    printName(runtime.attribute("VmName").valueAsString());
    term.newLine();
    printName(runtime.attribute("SpecVersion").valueAsString()+" ("+jmImpl.attribute("ImplementationVersion").valueAsString()+")");
    term.newLine();
    term.newLine();

    printNameValue("Up since", toDisplayString(runtime.attribute("Uptime").value()));
    printName("Input Arguments");
    MAttribute attribute = runtime.attribute("InputArguments");
    String[] arguments = (String[])attribute.value();
    for (String argument : arguments)
    {
      printSecondColumn(argument);
      term.newLine();
    }
        
    term.newLine();
    printName("Classpath");
    attribute = runtime.attribute("ClassPath");
    String classPath = attribute.valueAsString();
    for (String classPathEntry : classPath.split(";"))
    {
      printSecondColumn(classPathEntry);
      term.newLine();
    }

  }

  private static String toDisplayString(Object value)
  {
    long upTime = (long)value;
    long s = upTime / 1000;
    long m = s / 60;
    long h = m / 60;
    long d = h / 24;
    h = h % 24;
    m = m % 60;
    s = s % 60;
    StringBuilder builder = new StringBuilder();
    append(builder, d, "day");
    append(builder, h, "hour");
    append(builder, m, "minute");
    append(builder, s, "second");
    return builder.toString();
  }

  private static void append(StringBuilder builder, long value, String unit)
  {
    if (value > 0)
    {
      builder.append(value);
      builder.append(" ");
      builder.append(unit);
      if (value > 1)
      {
        builder.append("s");
      }
      builder.append(" ");
    }
  }
}
