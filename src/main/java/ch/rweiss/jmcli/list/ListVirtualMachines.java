package ch.rweiss.jmcli.list;

import ch.rweiss.jmcli.AbstractDataCommand;
import ch.rweiss.jmcli.Styles;
import ch.rweiss.jmx.client.Jvm;
import ch.rweiss.terminal.AnsiTerminal;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;

@Command(name="vm", description="Lists all available java virtual maschines")
public class ListVirtualMachines extends AbstractDataCommand
{
  private static Table<Jvm> jvms = declareJvmTable();

  public ListVirtualMachines()
  {
    super("Java Virtual Maschines");
  }
  
  @Override
  protected void gatherData()
  {
    jvms.setRows(Jvm.getAvailableRunningJvms());
    triggerUiUpdate();
  }
  
  @Override
  protected void writeDataToUi(AnsiTerminal terminal, boolean isPeriodical)
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
