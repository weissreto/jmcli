package ch.rweiss.jmcli.info;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import ch.rweiss.jmcli.AbstractJmxClientCommand;
import ch.rweiss.jmcli.Styles;
import ch.rweiss.jmx.client.MAttribute;
import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.MBeanName;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;

@Command(name="vm", description="Prints information about a virtual machine")
public class InfoVirtualMachine extends AbstractJmxClientCommand
{
  private final Table<Pair<String, String>> table = declareTable();

  public InfoVirtualMachine()
  {
    super("Java Virtual Machine Info");
  }

  private static Table<Pair<String, String>> declareTable()
  {
    Table<Pair<String, String>> table = new Table<>();
    table.addColumn(
        table.createColumn("Name", 16, pair -> pair.getKey())
          .withCellStyle(Styles.NAME)
          .toColumn());

    table.addColumn(
        table.createColumn("Value", 40, pair -> pair.getValue())
          .withCellStyle(Styles.VALUE)
          .multiLine()
          .withMinWidth(10)
          .toColumn());
    return table;
  }

  @Override
  protected void execute()
  {
    printEmptyLine();
    
    table.clear();

    MBean runtime = getJmxClient().bean(MBeanName.RUNTIME);    
    MBean jmImpl = getJmxClient().bean(MBeanName.createFor("JMImplementation:type=MBeanServerDelegate"));
    
    table.addRow(Pair.of("Name", runtime.attribute("VmName").valueAsString()));
    
    table.addRow(Pair.of("Version", 
        runtime.attribute("SpecVersion").valueAsString()+
        " ("+jmImpl.attribute("ImplementationVersion").valueAsString()+")"));
    
    table.addRow(Pair.of("Up since", toDisplayString(runtime.attribute("Uptime").value())));
    
    MAttribute attribute = runtime.attribute("InputArguments");
    String[] arguments = (String[])attribute.value();
    table.addRow(Pair.of("Input Arguments", StringUtils.join(arguments, "\n")));
    
    attribute = runtime.attribute("ClassPath");
    String classPath = attribute.valueAsString();
    classPath = StringUtils.replace(classPath, File.pathSeparator, "\n");
    table.addRow(Pair.of("Classpath", classPath));
    
    table.printWithoutHeader();   
  }
  
  @Override
  protected void afterRun()
  {
    super.afterRun();
    term.clear().screenToEnd();
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
