package ch.rweiss.jmcli.set;

import org.apache.commons.lang3.tuple.Pair;

import ch.rweiss.jmcli.AbstractBeanCommand;
import ch.rweiss.jmcli.IntervalOption;
import ch.rweiss.jmcli.JvmOption;
import ch.rweiss.jmcli.Styles;
import ch.rweiss.jmcli.executor.AbstractJmxExecutor;
import ch.rweiss.jmcli.list.ListAttributes;
import ch.rweiss.jmcli.ui.BeanTitle;
import ch.rweiss.jmcli.ui.CommandUi;
import ch.rweiss.jmx.client.JmxClient;
import ch.rweiss.jmx.client.MAttribute;
import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.MBeanFilter;
import ch.rweiss.terminal.StyledText;
import ch.rweiss.terminal.table.AbbreviateStyle;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;

public final class SetAttribute extends AbstractJmxExecutor
{
  @Command(name="attribute", description="Sets the value of an attribute")
  public static final class Cmd extends AbstractBeanCommand
  {
    @Parameters(index="1", paramLabel="ATTRIBUTE", description="The name of the attribute to set")
    private String attributeName;
  
    @Parameters(index="2", paramLabel="VALUE", description="The value to set")
    private String value;
    
    @Mixin
    private IntervalOption intervalOption = new IntervalOption();
    
    @Mixin
    private JvmOption jvmOption = new JvmOption();

    @Override
    public void run()
    {
      new SetAttribute(this).execute();
    }
  }
  
  private Table<Pair<String, StyledText>> attributeValues = declareValueTable();
  private MBeanFilter beanFilter;
  private Cmd command;
  private BeanTitle beanTitle = new BeanTitle(ui());

  public SetAttribute(Cmd command)
  {
    super("Set Attribute", command.intervalOption, command.jvmOption);
    beanFilter = command.beanFilter();
    this.command = command;
  }
  
  @Override
  protected void execute(CommandUi ui, JmxClient jmxClient)
  {
    beanTitle.reset();
    for (MBean bean : jmxClient.beansThatMatch(beanFilter))
    {
      MAttribute attribute = bean.attribute(command.attributeName);
      if (attribute != null)
      {
        beanTitle.printBeanNameTitle(bean);
        setValue(attribute);
      }
    }
  }
  
  private void setValue(MAttribute attribute)
  {
    ui().printEmptyLine();
    attributeValues.addRow(Pair.of("Setting attribute", new StyledText(attribute.name(), Styles.VALUE)));
    attributeValues.addRow(Pair.of("Value (Before)", ListAttributes.getValue(attribute)));
    attribute.value(command.value);
    attributeValues.addRow(Pair.of("Value (Now)", ListAttributes.getValue(attribute)));
    attributeValues.print();
  }
    
  private static Table<Pair<String,StyledText>> declareValueTable()
  {
    Table<Pair<String, StyledText>> table = new Table<>();
    table.hideHeader();
    table.addColumn(
        table.createColumn("Name", 20, pair -> pair.getKey())
          .withAbbreviateStyle(AbbreviateStyle.LEFT_WITH_DOTS)
          .withCellStyle(Styles.NAME)
          .withMinWidth(8)
          .toColumn());
    table.addColumn(
        table.createColumn("Value", 60)
          .withStyledTextProvider(pair -> pair.getValue())
          .multiLine()
          .withMinWidth(8)
          .toColumn());
    return table;
  }
}