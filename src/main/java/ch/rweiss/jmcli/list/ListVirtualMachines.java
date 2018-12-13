package ch.rweiss.jmcli.list;

import ch.rweiss.jmcli.AbstractCommand;
import ch.rweiss.jmcli.IntervalOption;
import ch.rweiss.jmcli.Styles;
import ch.rweiss.jmcli.executor.AbstractDataExecutor;
import ch.rweiss.jmcli.ui.CommandUi;
import ch.rweiss.jmx.client.Jvm;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

public class ListVirtualMachines extends AbstractDataExecutor
{
  
  @Command(name="vm", description="Lists all available java virtual maschines")
  public static final class Cmd extends AbstractCommand
  {
    @Mixin 
    private IntervalOption intervalOption = new IntervalOption();

    @Override
    public void run()
    {
      new ListVirtualMachines(intervalOption).execute(); 
    }
  }
  
  private static Table<Jvm> jvms = declareJvmTable();

  public ListVirtualMachines(IntervalOption intervalOption)
  {
    super("Java Virtual Maschines", intervalOption);
  }
  
  @Override
  protected void gatherData()
  {
    jvms.setRows(Jvm.getAvailableRunningJvms());
    triggerUiUpdate();
  }
  
  @Override
  protected void writeDataToUi(CommandUi ui, boolean isPeriodical)
  {
    if (isPeriodical)
    {
      jvms.printTop();
    }
    else
    {
      jvms.print();
    }
  }

  private static Table<Jvm> declareJvmTable()
  {
    Table<Jvm> table = new Table<>();
    table.addColumn(
        table.createColumn("Id", 10, jvm -> jvm.id())
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.NAME)
          .toColumn());

    table.addColumn(
        table.createColumn("Display Name", 20, jvm -> jvm.displayName())
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.VALUE)
          .multiLine()
          .withMinWidth(10)
          .toColumn());
    return table;
  }
}
