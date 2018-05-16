package ch.rweiss.jmx.client.cli.invoke;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.MOperation;
import ch.rweiss.jmx.client.cli.AbstractBeanCommand;
import ch.rweiss.jmx.client.cli.CommandException;
import ch.rweiss.jmx.client.cli.Styles;
import ch.rweiss.terminal.table.AbbreviateStyle;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;
import picocli.CommandLine.Parameters;

@Command(name="operation", description="Invokes an operation")
public class InvokeOperation extends AbstractBeanCommand
{
  @Parameters(index="1", paramLabel="OPERATION", description="The name or signature of the operation to call")
  private String operationName;

  @Parameters(index="2..*", paramLabel="PARAMETER", description="Operation parameters")
  private List<String> parameters = new ArrayList<>();
  
  private Table<MBean> beanTitle = declareBeanTitleTable();
  private Table<Pair<String, String>> operationTable = declareOperationTable();


  @Override
  protected void printTitle()
  {
    term.write("Invoke Operation");
  }

  @Override
  protected void execute()
  {
    for (MBean bean : getBeans())
    {
      MOperation operation = findOperation(bean);
      if (operation != null)
      {
        printBeanName(bean);
        invoke(operation);
      }
    }
  }

  private MOperation findOperation(MBean bean)
  {
    if (isSignatureSpecified())
    {
      return findOperationWithSignature(bean);
    }
    return findOperationWithoutSignature(bean);
  }

  private MOperation findOperationWithSignature(MBean bean)
  {
    String operation = StringUtils.substringBefore(operationName, "(");
    String typesStr = StringUtils.substringBetween(operationName, "(", ")");
    String[] types = typesStr.split(",");
    return bean.operation(operation, types);
  }

  private boolean isSignatureSpecified()
  {
    return StringUtils.contains(operationName, "(") && StringUtils.contains(operationName, ")");
  }

  private MOperation findOperationWithoutSignature(MBean bean)
  {
    List<MOperation> operations = bean.operations(operationName);
    if (operations.isEmpty())
    {
      throw new CommandException("Operation {0} not found for bean {1}", operationName, bean.name().fullQualifiedName());
    }
    if (operations.size() == 1)
    {
      return operations.get(0);
    }
    operations = operations
        .stream()
        .filter(operation -> operation.parameters().size() == parameters.size())
        .collect(Collectors.toList());
    if (operations.isEmpty())
    {
      throw new CommandException("Operation {0} with {1} parameters not found for bean {2}", operationName, parameters.size(), bean.name().fullQualifiedName());
    }
    if (operations.size() == 1)
    {
      return operations.get(0);
    }
    throw new CommandException("More than one operation {0} with {1} parameters found for bean {2}. Please specify operation signature.", operationName, parameters.size(), bean.name().fullQualifiedName());
  }
  
  private void printBeanName(MBean bean)
  {
    printEmptyLine();
    beanTitle.addRow(bean);
    beanTitle.printWithoutHeader();
  }

  private void invoke(MOperation operation)
  {
    printEmptyLine();
    operationTable.addRow(Pair.of("Invokeing operation", operation.signature()));
    operationTable.addRow(Pair.of("with parameters", parameters.toString()));
    
    String result = operation.invoke(parameters);
    
    operationTable.addRow(Pair.of("", ""));
    operationTable.addRow(Pair.of("Result", result));
    operationTable.printWithoutHeader();
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
  
  private static Table<Pair<String,String>> declareOperationTable()
  {
    Table<Pair<String, String>> table = new Table<>();
    table.addColumn(
        table.createColumn("Name", 20, pair -> pair.getKey())
          .withAbbreviateStyle(AbbreviateStyle.LEFT_WITH_DOTS)
          .withCellStyle(Styles.NAME)
          .withMinWidth(8)
          .toColumn());
    table.addColumn(
        table.createColumn("Value", 60, pair -> pair.getValue())
          .multiLine()
          .withMinWidth(8)
          .toColumn());
    return table;
  }
}
