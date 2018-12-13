package ch.rweiss.jmcli.list;

import java.util.List;

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
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

public final class ListOperations extends AbstractJmxExecutor
{  
  @Command(name="operations", description="Lists all operations")
  public static final class Cmd extends AbstractOperationCommand
  {
    @Mixin
    private IntervalOption intervalOption = new IntervalOption();
    
    @Mixin
    private JvmOption jvmOption = new JvmOption();


    @Override
    public void run()
    {
      new ListOperations(this).execute();
    }
  }
  
  private final Table<MOperation> table = declareTable();
  private MBeanFilter beanFilter;
  private WildcardFilters operationFilters;
  private BeanTitle beanTitle = new BeanTitle(ui());

  public ListOperations(Cmd command)
  {
    super("Attributes", command.intervalOption, command.jvmOption);  
    this.beanFilter = command.beanFilter();
    this.operationFilters = command.operationFilters();
  }
  
  @Override
  protected void execute(CommandUi ui, JmxClient jmxClient)
  {
    beanTitle.reset();
    for (MBean bean : jmxClient.beansThatMatch(beanFilter))
    {
      print(bean);
    }
  }

  private void print(MBean bean)
  {
    List<MOperation> operations = operationFilters.filter(bean.operations(), MOperation::name);
    if (!operations.isEmpty())
    {
      beanTitle.printBeanNameTitle(bean);
      table.setRows(operations);
      table.print();
    }
  }
  
  private static Table<MOperation> declareTable()
  {
    Table<MOperation> table = new Table<>();
    table.hideHeader();
    table.addColumn(
        table.createColumn("Name", 20, MOperation::name)
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.NAME)
          .withMinWidth(10)
          .toColumn());
    
    table.addColumn(
        table.createColumn("Signature", 40, MOperation::signature)        
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.VALUE)
          .withMinWidth(10)
          .toColumn());
    return table;
  }

}
