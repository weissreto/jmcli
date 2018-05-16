package ch.rweiss.jmx.client.cli.info;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;

import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.MOperation;
import ch.rweiss.jmx.client.MParameter;
import ch.rweiss.jmx.client.cli.AbstractBeanCommand;
import ch.rweiss.jmx.client.cli.Styles;
import ch.rweiss.jmx.client.cli.WildcardFilters;
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
  
  private Table<MBean> beanTitle = declareBeanTitleTable();
  private Table<MOperation> operationTitle = declareOperationTitleTable();
  private Table<MOperation> description = declareDescriptionTable();
  private Table<Pair<String, String>> properties = declarePropertiesTable();
  private Table<MParameter> parameters = declareParameterTable();

  @Override
  public void run()
  {
    filters = new WildcardFilters(operationFilters);
    super.run();
  }
  
  @Override
  protected void printTitle()
  {
    term.write("Operation Info");
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
  
  private void printBeanNameTitle(MBean bean)
  {
    printEmptyLine();
    beanTitle.clear();
    beanTitle.addRow(bean);
    beanTitle.printWithoutHeader();    
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
    properties.printWithoutHeader();

    printParameters(op);
  }

  private void printNameTitle(MOperation op)
  {
    printEmptyLine();
    operationTitle.clear();
    operationTitle.addRow(op);
    operationTitle.printWithoutHeader();
  }
  
  private void printDescription(MOperation op)
  {
    printEmptyLine();
    description.clear();
    description.addRow(op);
    description.printWithoutHeader();
    printEmptyLine();
  }
  
  private void printParameters(MOperation op)
  {
    if (!op.parameters().isEmpty())
    {
      printEmptyLine();
      
      parameters.clear();
      for (MParameter param : op.parameters())
      {
        parameters.addRow(param);
      }
      parameters.print();
    }
  }
  
  private static Table<MBean> declareBeanTitleTable()
  {
    Table<MBean> table = new Table<>();
    table.addColumn(
        table.createColumn("", 40, b -> b.name())
          .withAbbreviateStyle(AbbreviateStyle.LEFT_WITH_DOTS)
          .withCellStyle(Styles.NAME_TITLE)
          .withMinWidth(8)
          .toColumn());
    return table;
  }
  
  private static Table<MOperation> declareOperationTitleTable()
  {
    Table<MOperation> table = new Table<>();
    table.addColumn(
        table.createColumn("", 40, operation -> operation.name())
          .withAbbreviateStyle(AbbreviateStyle.LEFT_WITH_DOTS)
          .withCellStyle(Styles.SUB_TITLE)
          .withMinWidth(8)
          .toColumn());
    return table;
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

