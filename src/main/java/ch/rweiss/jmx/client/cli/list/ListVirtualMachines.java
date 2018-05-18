package ch.rweiss.jmx.client.cli.list;

import ch.rweiss.jmx.client.Jvm;
import ch.rweiss.jmx.client.cli.AbstractHeaderCommand;
import ch.rweiss.jmx.client.cli.Styles;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;

@Command(name="vm", description="Lists all available java virtual maschines")
public class ListVirtualMachines extends AbstractHeaderCommand
{
  private static Table<Jvm> jvms = declareJvmTable();

  public ListVirtualMachines()
  {
    super("Java Virtual Maschines");
  }
  
  @Override
  public void execute()
  {
    jvms.setRows(Jvm.getAvailableRunningJvms());
    jvms.print();
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
