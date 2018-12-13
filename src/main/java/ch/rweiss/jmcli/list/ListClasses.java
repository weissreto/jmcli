package ch.rweiss.jmcli.list;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.rweiss.jmcli.AbstractCommand;
import ch.rweiss.jmcli.IntervalOption;
import ch.rweiss.jmcli.JvmOption;
import ch.rweiss.jmcli.Styles;
import ch.rweiss.jmcli.WildcardFilters;
import ch.rweiss.jmcli.executor.AbstractJmxDataExecutor;
import ch.rweiss.jmcli.histo.ClassInfo;
import ch.rweiss.jmcli.histo.HistoDumpParser;
import ch.rweiss.jmcli.ui.CommandUi;
import ch.rweiss.jmx.client.JmxClient;
import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.MBeanName;
import ch.rweiss.jmx.client.MOperation;
import ch.rweiss.terminal.AnsiTerminal;
import ch.rweiss.terminal.Key;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;

public class ListClasses extends AbstractJmxDataExecutor
{
  @Command(name = "classes", description="Lists all classes and the number of instances")
  public static final class Cmd extends AbstractCommand
  {
    @Parameters(index = "0..*", paramLabel = "PACKAGE", description = "Package name or filter with wildcards. E.g java.util.*")
    private java.util.List<String> packageFilters = new ArrayList<>();
    
    @Mixin
    private IntervalOption intervalOption = new IntervalOption();
    
    @Mixin
    private JvmOption jvmOption = new JvmOption();
    
    @Override
    public void run()
    {
      new ListClasses(this).execute();
    }
  }
  
  private static final Table<ClassInfo> classesTable = declareTable();

  private String filter;
  private Cmd command;

  public ListClasses(Cmd command)
  {
    super("Classes", command.intervalOption, command.jvmOption);
    this.command = command;
  }

  @Override
  protected void gatherDataFrom(JmxClient jmxClient)
  {
    MBean bean =jmxClient.bean(MBeanName.createFor("com.sun.management:type=DiagnosticCommand"));
    MOperation operation = bean.operation("gcClassHistogram", "java.lang.String[]");
    Object result = operation.invoke((Object) null);
    java.util.List<ClassInfo> classes = new HistoDumpParser(result.toString()).parse();

    java.util.List<String> filters;
    if (filter != null)
    {
      filters = new ArrayList<>(command.packageFilters);
      filters.add("*" + filter);
    }
    else
    {
      filters = command.packageFilters;
    }
    WildcardFilters wildcardFilters = WildcardFilters.createForPrefixes(filters);
    classes = classes.stream().filter(classInfo -> wildcardFilters.matches(classInfo.name()))
        .collect(Collectors.toList());

    classesTable.setRows(classes);
    triggerUiUpdate();
  }

  @Override
  protected void writeDataToUi(CommandUi ui, boolean isPeriodical)
  {
    if (filter != null)
    {
      AnsiTerminal terminal = ui.terminal();
      terminal.write("Filter: ");
      terminal.write(filter);
      terminal.clear().lineToEnd();
      terminal.newLine();
    }
    if (isPeriodical)
    {
      classesTable.printTop();
    }
    else
    {
      classesTable.print();
    }
  }

  @Override
  protected void keyPressed(Key key)
  {
    if (!key.isControl())
    {
      filterKey(key);
      return;
    }
    navigateKey(key);
  }

  private void filterKey(Key key)
  {
    if (filter == null)
    {
      filter = new String();
    }
    if (key.toString().equals("\u007F"))
    {
      if (filter.length() > 1)
      {
        filter = StringUtils.substring(filter, 0, filter.length() - 1);
      }
      else
      {
        filter = null;
      }
    }
    else if (!Character.isISOControl(key.toChar()))
    {
      filter += key.toString();
    }
    triggerUiUpdate();
  }

  private void navigateKey(Key key)
  {
    boolean processed = classesTable.keyPressed(key);
    if (processed)
    {
      triggerUiUpdate();
    }    
  }

  private static Table<ClassInfo> declareTable()
  {
    Table<ClassInfo> table = new Table<>();
    table.addColumn(table.createColumn("Name", 20, info -> info.name()).withTitleStyle(Styles.NAME_TITLE)
        .withCellStyle(Styles.NAME).withMinWidth(10).toColumn());

    table.addColumn(table.createColumn("Instances", 10, info -> info.instances()).withTitleStyle(Styles.NAME_TITLE)
        .withCellStyle(Styles.VALUE).toColumn());

    table.addColumn(table.createColumn("Bytes", 10, info -> info.bytes()).withTitleStyle(Styles.NAME_TITLE)
        .withCellStyle(Styles.VALUE).toColumn());

    return table;
  }
}