package ch.rweiss.jmcli.info;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import ch.rweiss.jmcli.AbstractBeanCommand;
import ch.rweiss.jmcli.Styles;
import ch.rweiss.jmcli.WildcardFilters;
import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.MOperation;
import ch.rweiss.jmx.client.MParameter;
import ch.rweiss.terminal.table.AbbreviateStyle;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name="operation", description="Prints information about operations")
public class InfoOperation extends AbstractBeanCommand
{
  @Parameters(index="1..*", paramLabel="OPERATION", description="Operation name or filter with wildcards. E.g gc, getThread*")
  private List<String> operationFilters = new ArrayList<>();
  private WildcardFilters filters;
  
  private Table<MOperation> description = declareDescriptionTable();
  private Table<Pair<String, String>> properties = declarePropertiesTable();
  private Table<MParameter> parameters = declareParameterTable();

  public InfoOperation()
  {
    super("Operation Info"); 
  }
  
  @Override
  public void run()
  {
    filters = WildcardFilters.crateForFilters(operationFilters);
    super.run();
  }
  
  @Override
  protected void execute()
  {
    for (MBean bean : getBeans())
    {
      List<MOperation> operations = getFilteredOperations(bean);
      if (!operations.isEmpty())
      {
        printBeanNameTitle(bean);
        for (MOperation op : operations)
        {
          print(op);
        }
      }
    }
  }

  private List<MOperation> getFilteredOperations(MBean bean)
  {
    return bean
      .operations()
      .stream()
      .filter(operation -> filters.matches(operation.name()))
      .collect(Collectors.toList());
  }
  
  private void print(MOperation op)
  {
    printNameTitle(op);
    printDescription(op);
    
    properties.clear();
    properties.addRow(Pair.of("Name", op.name()));
    properties.addRow(Pair.of("Signature", op.signature()));
    properties.addRow(Pair.of("Impact", op.impact().toString()));
    properties.addRow(Pair.of("Return Type", op.returnType()));
    properties.print();

    printParameters(op);
  }

  private void printNameTitle(MOperation op)
  {
    printEmptyLine();
    printSubTitle(op.name());
  }
  
  private void printDescription(MOperation op)
  {
    printEmptyLine();
    description.printSingleRow(op);
    printEmptyLine();
  }
  
  private void printParameters(MOperation op)
  {
    if (!op.parameters().isEmpty())
    {
      printEmptyLine();
      
      parameters.setRows(op.parameters());
      parameters.print();
    }
  }
  
  private static Table<MOperation> declareDescriptionTable()
  {
    Table<MOperation> table = new Table<>();
    table.addColumn(
        table.createColumn("", 40, operation -> operation.description())
          .multiLine()
          .withCellStyle(Styles.DESCRIPTION)
          .withMinWidth(8)
          .toColumn());
    return table;
  }

  private static Table<Pair<String,String>> declarePropertiesTable()
  {
    Table<Pair<String, String>> table = new Table<>();
    table.hideHeader();
    table.addColumn(
        table.createColumn("Name", 20, pair -> pair.getKey())
          .withAbbreviateStyle(AbbreviateStyle.LEFT)
          .withCellStyle(Styles.NAME)
          .withMinWidth(8)
          .toColumn());
    table.addColumn(
        table.createColumn("Value", 60, pair -> pair.getValue())
          .withCellStyle(Styles.VALUE)
          .multiLine()
          .withMinWidth(8)
          .toColumn());
    return table;
  }
  
  private static Table<MParameter> declareParameterTable()
  {
    Table<MParameter> table = new Table<>();
    table.addColumn(
        table.createColumn("Parameter", 20, parameter -> parameter.name())
          .withAbbreviateStyle(AbbreviateStyle.RIGHT_WITH_DOTS)
          .withTitleStyle(Styles.NAME)
          .withCellStyle(Styles.VALUE)
          .withMinWidth(8)
          .toColumn());
    table.addColumn(
        table.createColumn("Type", 30, parameter -> parameter.type())
          .withAbbreviateStyle(AbbreviateStyle.RIGHT_WITH_DOTS)
          .withTitleStyle(Styles.NAME)
          .withCellStyle(Styles.VALUE)
          .withMinWidth(8)
          .toColumn());
    table.addColumn(
        table.createColumn("Description", 30, parameter -> parameter.description())
          .withTitleStyle(Styles.NAME)
          .withCellStyle(Styles.DESCRIPTION)
          .multiLine()
          .withMinWidth(8)
          .toColumn());
    return table;
  }

}

