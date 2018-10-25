package ch.rweiss.jmcli.list;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import ch.rweiss.jmcli.AbstractJmxClientCommand;
import ch.rweiss.jmcli.Styles;
import ch.rweiss.jmcli.WildcardFilters;
import ch.rweiss.jmcli.histo.ClassInfo;
import ch.rweiss.jmcli.histo.HistoDumpParser;
import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.MBeanName;
import ch.rweiss.jmx.client.MOperation;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name = "classes", description="Lists all classes and the number of instances")
public class ListClasses extends AbstractJmxClientCommand
{
  @Parameters(index="0..*", paramLabel="PACKAGE", description="Package name or filter with wildcards. E.g java.util.*")
  private List<String> packageFilters = new ArrayList<>();

  private final Table<ClassInfo> table = declareTable();

  public ListClasses()
  {
    super("Classes");
  }
  
  @Override
  protected void execute()
  {
    MBean bean = getJmxClient().bean(MBeanName.createFor("com.sun.management:type=DiagnosticCommand"));
    MOperation operation = bean.operation("gcClassHistogram", "java.lang.String[]");
    Object result = operation.invoke((Object)null);
    List<ClassInfo> classes = new HistoDumpParser(result.toString()).parse();
    
    WildcardFilters filters = WildcardFilters.createForPrefixes(packageFilters); 
    classes = classes
        .stream()
        .filter(classInfo -> filters.matches(classInfo.name()))
        .collect(Collectors.toList());
     
    table.setRows(classes);
    if (isPeriodically())
    {
      table.printTop();
    }
    else
    {
      table.print();
    }
  }
    
  private static Table<ClassInfo> declareTable()
  {
    Table<ClassInfo> table = new Table<>();
    table.addColumn(
        table.createColumn("Name", 20, info -> info.name())
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.NAME)
          .withMinWidth(10)
          .toColumn());

    table.addColumn(
        table.createColumn("Instances", 10, info -> info.instances())
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.VALUE)
          .toColumn());

    table.addColumn(
        table.createColumn("Bytes", 10, info -> info.bytes())
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.VALUE)
          .toColumn());

    return table;
  }

}
