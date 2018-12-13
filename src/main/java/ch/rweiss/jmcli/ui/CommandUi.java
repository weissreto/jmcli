package ch.rweiss.jmcli.ui;

import ch.rweiss.jmcli.Styles;
import ch.rweiss.terminal.AnsiTerminal;
import ch.rweiss.terminal.table.AbbreviateStyle;
import ch.rweiss.terminal.table.Table;

public class CommandUi
{
  protected AnsiTerminal terminal = AnsiTerminal.get();
  private static Table<String> header = declareHeaderTable(); 
  private static Table<String> subTitle = declareSubTitleTable();

  public AnsiTerminal terminal()
  {
    return terminal;
  }

  public void printHeader(String name)
  {
    printEmptyLine();
    header.printSingleRow(name);
    printEmptyLine();
  }  
  
  public void printSubTitle(String title)
  {
    subTitle.printSingleRow(title);
  }
  
  public void printEmptyLine()
  {
    terminal.clear().lineToEnd();
    terminal.newLine();
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
