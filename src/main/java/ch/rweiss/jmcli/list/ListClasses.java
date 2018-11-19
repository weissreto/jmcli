package ch.rweiss.jmcli.list;

import java.util.ArrayList;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import ch.rweiss.jmcli.AbstractDataJmxClientCommand;
import ch.rweiss.jmcli.Styles;
import ch.rweiss.jmcli.WildcardFilters;
import ch.rweiss.jmcli.histo.ClassInfo;
import ch.rweiss.jmcli.histo.HistoDumpParser;
import ch.rweiss.jmx.client.JmxClient;
import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.MBeanName;
import ch.rweiss.jmx.client.MOperation;
import ch.rweiss.terminal.AnsiTerminal;
import ch.rweiss.terminal.Key;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "classes", description="Lists all classes and the number of instances")
public class ListClasses extends AbstractDataJmxClientCommand
{
  @Parameters(index = "0..*", paramLabel = "PACKAGE", description = "Package name or filter with wildcards. E.g java.util.*")
  private java.util.List<String> packageFilters = new ArrayList<>();
  private String filter;

  private final Table<ClassInfo> table = declareTable();

  public ListClasses()
  {
    super("Classes");
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
      filters = new ArrayList<>(packageFilters);
      filters.add("*" + filter);
    }
    else
    {
      filters = packageFilters;
    }
    WildcardFilters wildcardFilters = WildcardFilters.createForPrefixes(filters);
    classes = classes.stream().filter(classInfo -> wildcardFilters.matches(classInfo.name()))
        .collect(Collectors.toList());

    table.setRows(classes);
    triggerUiUpdate();
  }

  @Override
  protected void writeDataToUi(AnsiTerminal terminal, boolean isPeriodical)
  {
    if (filter != null)
    {
      terminal.write("Filter: ");
      terminal.write(filter);
      terminal.clear().lineToEnd();
      terminal.newLine();
    }
    if (isPeriodical)
    {
      table.printTop();
    }
    else
    {
      table.print();
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
    boolean processed = table.keyPressed(key);
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