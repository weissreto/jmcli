package ch.rweiss.jmcli.invoke;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

import ch.rweiss.jmcli.AbstractBeanCommand;
import ch.rweiss.jmcli.CommandException;
import ch.rweiss.jmcli.IntervalOption;
import ch.rweiss.jmcli.JvmOption;
import ch.rweiss.jmcli.Styles;
import ch.rweiss.jmcli.executor.AbstractJmxExecutor;
import ch.rweiss.jmcli.ui.BeanTitle;
import ch.rweiss.jmcli.ui.CommandUi;
import ch.rweiss.jmx.client.JmxClient;
import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.MOperation;
import ch.rweiss.terminal.table.AbbreviateStyle;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;

public final class InvokeOperation extends AbstractJmxExecutor
{
  @Command(name="operation", description="Invokes an operation")
  public static final class Cmd extends AbstractBeanCommand
  {
    @Parameters(index="1", paramLabel="OPERATION", description="The name or signature of the operation to call")
    private String operationName;
  
    @Parameters(index="2..*", paramLabel="PARAMETER", description="Operation parameters")
    private List<String> parameters = new ArrayList<>();
    
    @Mixin
    private IntervalOption intervalOption = new IntervalOption();
    
    @Mixin
    private JvmOption jvmOption = new JvmOption();
    
    @Override
    public void run()
    {
      new InvokeOperation(this).execute();
    }
  }
  
  private Table<Pair<String, String>> operationTable = declareOperationTable();
  private Cmd command;
  private BeanTitle beanTitle = new BeanTitle(ui());

  public InvokeOperation(Cmd command)
  {
    super("Invoke Operation", command.intervalOption, command.jvmOption);
    this.command = command;
  }

  @Override
  protected void execute(CommandUi ui, JmxClient jmxClient)
  {
    beanTitle.reset();
    for (MBean bean : jmxClient.beansThatMatch(command.beanFilter()))
    {
      MOperation operation = findOperation(bean);
      if (operation != null)
      {
        beanTitle.printBeanNameTitle(bean);
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
    String operation = StringUtils.substringBefore(command.operationName, "(");
    String typesStr = StringUtils.substringBetween(command.operationName, "(", ")");
    String[] types = typesStr.split(",");
    return bean.operation(operation, types);
  }

  private boolean isSignatureSpecified()
  {
    return StringUtils.contains(command.operationName, "(") && StringUtils.contains(command.operationName, ")");
  }

  private MOperation findOperationWithoutSignature(MBean bean)
  {
    List<MOperation> operations = bean.operations(command.operationName);
    if (operations.isEmpty())
    {
      throw new CommandException("Operation {0} not found for bean {1}", command.operationName, bean.name().fullQualifiedName());
    }
    if (operations.size() == 1)
    {
      return operations.get(0);
    }
    operations = operations
        .stream()
        .filter(operation -> operation.parameters().size() == command.parameters.size())
        .collect(Collectors.toList());
    if (operations.isEmpty())
    {
      throw new CommandException("Operation {0} with {1} parameters not found for bean {2}", command.operationName, command.parameters.size(), bean.name().fullQualifiedName());
    }
    if (operations.size() == 1)
    {
      return operations.get(0);
    }
    throw new CommandException("More than one operation {0} with {1} parameters found for bean {2}. Please specify operation signature.", command.operationName, command.parameters.size(), bean.name().fullQualifiedName());
  }
  
  private void invoke(MOperation operation)
  {
    ui().printEmptyLine();
    operationTable.addRow(Pair.of("Invokeing operation", operation.signature()));
    operationTable.addRow(Pair.of("with parameters", command.parameters.toString()));
    
    String result = operation.invoke(command.parameters);
    
    operationTable.addRow(Pair.of("", ""));
    operationTable.addRow(Pair.of("Result", result));
    operationTable.print();
  }
  
  private static Table<Pair<String,String>> declareOperationTable()
  {
    Table<Pair<String, String>> table = new Table<>();
    table.hideHeader();
    table.addColumn(
        table.createColumn("Name", 20, pair -> pair.getKey())
          .withAbbreviateStyle(AbbreviateStyle.LEFT_WITH_DOTS)
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
}
