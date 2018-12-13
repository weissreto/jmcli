package ch.rweiss.jmcli.ui;

import ch.rweiss.jmcli.Styles;
import ch.rweiss.jmx.client.MBean;
import ch.rweiss.terminal.table.AbbreviateStyle;
import ch.rweiss.terminal.table.Table;

public class BeanTitle
{
  private static Table<MBean> beanTitle = declareBeanTitleTable();

  private boolean firstBean = true;

  private CommandUi ui;
  
  public BeanTitle(CommandUi ui)
  {
    this.ui = ui;    
  }

  public void reset()
  {
    firstBean = true;
  }
  
  public void printBeanNameTitle(MBean bean)
  {
    if (!firstBean)
    {
      ui.printEmptyLine();
    }
    firstBean = false;
    beanTitle.printSingleRow(bean);
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
}
