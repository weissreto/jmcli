package ch.rweiss.jmcli.info;

import java.io.File;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import ch.rweiss.jmcli.AbstractCommand;
import ch.rweiss.jmcli.IntervalOption;
import ch.rweiss.jmcli.JvmOption;
import ch.rweiss.jmcli.Styles;
import ch.rweiss.jmcli.executor.AbstractJmxExecutor;
import ch.rweiss.jmcli.ui.CommandUi;
import ch.rweiss.jmx.client.JmxClient;
import ch.rweiss.jmx.client.MAttribute;
import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.MBeanName;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

public class InfoVirtualMachine extends AbstractJmxExecutor
{
  @Command(name="vm", description="Prints information about a virtual machine")
  public static final class Cmd extends AbstractCommand
  {
    @Mixin
    private IntervalOption intervalOption = new IntervalOption();
    
    @Mixin
    private JvmOption jvmOption = new JvmOption();
    
    @Override
    public void run()
    {
      new InfoVirtualMachine(this).execute();
    }
  }
  
  private final Table<Pair<String, String>> table = declareTable();
  
  public InfoVirtualMachine(Cmd command)
  {
    super("Java Virtual Machine Info", command.intervalOption, command.jvmOption);
  }

  private static Table<Pair<String, String>> declareTable()
  {
    Table<Pair<String, String>> table = new Table<>();
    table.hideHeader();
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
  protected void execute(CommandUi ui, JmxClient jmxClient)
  {
    ui.printEmptyLine();
    
    table.clear();

    MBean runtime = jmxClient.bean(MBeanName.RUNTIME);    
    MBean jmImpl = jmxClient.bean(MBeanName.createFor("JMImplementation:type=MBeanServerDelegate"));
    
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
    
    table.print();   
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
