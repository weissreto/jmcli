package ch.rweiss.jmcli;

import ch.rweiss.check.Check;
import ch.rweiss.terminal.AnsiTerminal;
import ch.rweiss.terminal.table.AbbreviateStyle;
import ch.rweiss.terminal.table.Table;

public abstract class AbstractHeaderCommand extends AbstractCommand
{
  protected AnsiTerminal term = AnsiTerminal.get();
  
  private String name;
  private static Table<String> header = declareHeaderTable(); 
  private static Table<String> subTitle = declareSubTitleTable();

  
  protected AbstractHeaderCommand(String name)
  {
    setName(name);
  }
  
  abstract protected void execute();

  protected void setName(String name)
  {
    Check.parameter("name").withValue(name).isNotBlank();
    this.name = name;
  }
  
  @Override
  public void run()
  {
    try
    {
      printHeader();
      execute();
    }
    finally
    {
      term.reset();
    }
  }
  
  protected void printHeader()
  {
    printEmptyLine();
    header.printSingleRow(name);
    printEmptyLine();
  }  
  
  protected void printSubTitle(String title)
  {
    subTitle.printSingleRow(title);
  }
  
  protected void printEmptyLine()
  {
    term.clear().lineToEnd();
    term.newLine();
  }
  
  private static Table<String> declareHeaderTable()
  {
    Table<String> table = new Table<>();
    table.addColumn(
        table.createColumn("", 40, title -> title)
          .withAbbreviateStyle(AbbreviateStyle.RIGHT)
          .withCellStyle(Styles.TITLE)
          .withMinWidth(8)
          .toColumn());
    return table;
  }
  
  private static Table<String> declareSubTitleTable()
  {
    Table<String> table = new Table<>();
    table.addColumn(
        table.createColumn("Title", 20, title -> title)
          .withAbbreviateStyle(AbbreviateStyle.RIGHT)
          .withCellStyle(Styles.SUB_TITLE)
          .withMinWidth(8)
          .toColumn());
    return table;
  }


}
