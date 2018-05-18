package ch.rweiss.jmx.client.cli.list;

import ch.rweiss.jmx.client.Jvm;
import ch.rweiss.jmx.client.cli.AbstractHeaderCommand;
import ch.rweiss.jmx.client.cli.Styles;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;

@Command(name="vm", description="Lists all available java virtual maschines")
public class ListVirtualMachines extends AbstractHeaderCommand
{
  private Table<Jvm> table = declareTable();

  @Override
  protected void printTitle()
  {
    term.write("Available Java Virtual Machines");    
  }
  
  @Override
  public void execute()
  {
    printEmptyLine();
    table.setRows(Jvm.getAvailableRunningJvms());
    table.print();
  }

  private static Table<Jvm> declareTable()
  {
    Table<Jvm> table = new Table<>();
    table.addColumn(
        table.createColumn("Id", 10, jvm -> jvm.id())
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.ID)
          .toColumn());

    table.addColumn(
        table.createColumn("Display Name", 20, jvm -> jvm.displayName())
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.NAME)
          .multiLine()
          .withMinWidth(10)
          .toColumn());
    return table;
  }

}
