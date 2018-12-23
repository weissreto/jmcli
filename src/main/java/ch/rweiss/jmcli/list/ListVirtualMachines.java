package ch.rweiss.jmcli.list;

import ch.rweiss.jmcli.AbstractCommand;
import ch.rweiss.jmcli.IntervalOption;
import ch.rweiss.jmcli.SortColumnOption;
import ch.rweiss.jmcli.SortColumnOption.Direction;
import ch.rweiss.jmcli.Styles;
import ch.rweiss.jmcli.executor.AbstractDataExecutor;
import ch.rweiss.jmcli.ui.CommandUi;
import ch.rweiss.jmx.client.Jvm;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

public class ListVirtualMachines extends AbstractDataExecutor
{
  
  private static final String ID_COLUMN_NAME = "Id";

  @Command(name="vm", description="Lists all available java virtual maschines")
  public static final class Cmd extends AbstractCommand
  {
    @Mixin 
    private IntervalOption intervalOption = new IntervalOption();
    
    @Mixin
    private SortColumnOption sortOption = new SortColumnOption(ID_COLUMN_NAME, Direction.ASCENDING);

    @Override
    public void run()
    {
      new ListVirtualMachines(this).execute(); 
    }
  }
  
  private static Table<Jvm> jvms = declareJvmTable();

  public ListVirtualMachines(Cmd command)
  {
    super("Java Virtual Maschines", command.intervalOption);
    command.sortOption.sort(jvms);
    
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
        table.createColumn(ID_COLUMN_NAME, 10, jvm -> jvm.id())
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
