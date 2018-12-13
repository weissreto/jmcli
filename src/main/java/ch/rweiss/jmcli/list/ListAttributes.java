package ch.rweiss.jmcli.list;

import java.util.List;

import ch.rweiss.jmcli.AbstractAttributeCommand;
import ch.rweiss.jmcli.IntervalOption;
import ch.rweiss.jmcli.JvmOption;
import ch.rweiss.jmcli.Styles;
import ch.rweiss.jmcli.WildcardFilters;
import ch.rweiss.jmcli.executor.AbstractJmxExecutor;
import ch.rweiss.jmcli.ui.BeanTitle;
import ch.rweiss.jmcli.ui.CommandUi;
import ch.rweiss.jmx.client.JmxClient;
import ch.rweiss.jmx.client.JmxException;
import ch.rweiss.jmx.client.MAttribute;
import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.MBeanFilter;
import ch.rweiss.terminal.StyledText;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

public final class ListAttributes extends AbstractJmxExecutor
{  
  @Command(name="attributes", description="Lists all threads and their states")
  public static final class Cmd extends AbstractAttributeCommand
  {
    @Mixin
    private IntervalOption intervalOption = new IntervalOption();
    
    @Mixin
    private JvmOption jvmOption = new JvmOption();


    @Override
    public void run()
    {
      new ListAttributes(this).execute();
    }
  }
  
  private final Table<MAttribute> table = declareTable();
  private MBeanFilter beanFilter;
  private WildcardFilters attributeFilter;
  private BeanTitle beanTitle = new BeanTitle(ui());

  public ListAttributes(Cmd command)
  {
    super("Attributes", command.intervalOption, command.jvmOption);  
    this.beanFilter = command.beanFilter();
    this.attributeFilter = command.attributeFilters();
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

  private static Table<MAttribute> declareTable()
  {
    Table<MAttribute> table = new Table<>();
    table.hideHeader();
    table.addColumn(
        table.createColumn("Name", 20, attribute -> attribute.name())
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.NAME)
          .withMinWidth(10)
          .toColumn());
    
    table.addColumn(
        table.createColumn("Value", 40)        
          .withTitleStyle(Styles.NAME_TITLE)
          .withStyledTextProvider(attribute -> getValue(attribute))
          .multiLine()
          .withMinWidth(10)
          .toColumn());
    return table;
  }
    
  public static StyledText getValue(MAttribute attribute)
  {
    try
    {
      return new StyledText(attribute.valueAsString(), Styles.VALUE);
    }
    catch(JmxException ex)
    {
      return new StyledText(toErrorMessage(ex), Styles.ERROR);
    }
  }
  
  private static String toErrorMessage(JmxException error)
  {
    String message;
    message = error.getShortDisplayMessage();
    return "<" + message + ">";
  }


  private void print(MBean bean)
  {
    List<MAttribute> attributes = attributeFilter.filter(bean.attributes(), MAttribute::name);
    if (!attributes.isEmpty())
    {
      beanTitle.printBeanNameTitle(bean);
      table.setRows(attributes);
      table.print();
    }
  }
}
