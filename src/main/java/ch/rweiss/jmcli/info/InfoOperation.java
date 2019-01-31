package ch.rweiss.jmcli.info;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import ch.rweiss.jmcli.AbstractOperationCommand;
import ch.rweiss.jmcli.IntervalOption;
import ch.rweiss.jmcli.JvmOption;
import ch.rweiss.jmcli.Styles;
import ch.rweiss.jmcli.WildcardFilters;
import ch.rweiss.jmcli.executor.AbstractJmxExecutor;
import ch.rweiss.jmcli.ui.BeanTitle;
import ch.rweiss.jmcli.ui.CommandUi;
import ch.rweiss.jmx.client.JmxClient;
import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.MBeanFilter;
import ch.rweiss.jmx.client.MOperation;
import ch.rweiss.jmx.client.MParameter;
import ch.rweiss.terminal.table.AbbreviateStyle;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

public final class InfoOperation extends AbstractJmxExecutor
{
  @Command(name="operation", description="Prints information about operations")
  public static final class Cmd extends AbstractOperationCommand
  {
    @Mixin
    private IntervalOption intervalOption = new IntervalOption();
    
    @Mixin
    private JvmOption jvmOption = new JvmOption();
    
    @Override
    public void run()
    {
      new InfoOperation(this).execute();
    }
  }

  private static final Comparator<MOperation> COMPERATOR = Comparator
      .comparing(MOperation::name)
      .thenComparing(operation->operation.parameters().size())
      .thenComparing(MOperation::signature);
  
  private WildcardFilters operationFilters;
  private MBeanFilter beanFilters;
  private BeanTitle beanTitle = new BeanTitle(ui());
  
  private Table<MOperation> description = declareDescriptionTable();
  private Table<Pair<String, String>> properties = declarePropertiesTable();
  private Table<MParameter> parameters = declareParameterTable();

  public InfoOperation(Cmd command)
  {
    super("Operation Info", command.intervalOption, command.jvmOption); 
    operationFilters = command.operationFilters();
    beanFilters = command.beanFilter();
  }

  @Override
  protected void execute(CommandUi ui, JmxClient jmxClient)
  {
    beanTitle.reset();
    for (MBean bean : jmxClient.beansThatMatch(beanFilters))
    {
      List<MOperation> operations = operationFilters.filter(bean.operations(), MOperation::name);
      if (!operations.isEmpty())
      {
        beanTitle.printBeanNameTitle(bean);
        Collections.sort(operations, COMPERATOR);
        for (MOperation op : operations)
        {
          print(op);
        }
      }
    }
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
    ui().printEmptyLine();
    ui().printSubTitle(op.name());
  }
  
  private void printDescription(MOperation op)
  {
    ui().printEmptyLine();
    description.printSingleRow(op);
    ui().printEmptyLine();
  }
  
  private void printParameters(MOperation op)
  {
    if (!op.parameters().isEmpty())
    {
      ui().printEmptyLine();
      
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

