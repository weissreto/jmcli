package ch.rweiss.jmx.client.cli;

import java.util.List;

import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.MBeanFilter;
import ch.rweiss.terminal.table.AbbreviateStyle;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Parameters;

public abstract class AbstractBeanCommand extends AbstractJmxClientCommand
{
  private static Table<MBean> beanTitle = declareBeanTitleTable();

  @Parameters(index="0", arity="0..1", paramLabel="BEAN", description="Bean name or filter with wildcards. E.g *:*, java.lang:*, java.lang:type=Memory")
  private String beanNameOrFilter = "*:*";

  private boolean firstBean = true;
  
  protected AbstractBeanCommand(String name)
  {
    super(name);
  }
  
  protected List<MBean> getBeans()
  {
    return getJmxClient().beansThatMatch(MBeanFilter.with(beanNameOrFilter));
  }
  
  protected void printBeanNameTitle(MBean bean)
  {
    if (!firstBean)
    {
      printEmptyLine();
    }
    firstBean = false;
    beanTitle.setSingleRow(bean);
    beanTitle.printWithoutHeader();    
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
