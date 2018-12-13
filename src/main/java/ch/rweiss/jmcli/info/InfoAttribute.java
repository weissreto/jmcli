package ch.rweiss.jmcli.info;

import java.util.List;

import org.apache.commons.lang3.tuple.Pair;

import ch.rweiss.jmcli.AbstractAttributeCommand;
import ch.rweiss.jmcli.IntervalOption;
import ch.rweiss.jmcli.JvmOption;
import ch.rweiss.jmcli.Styles;
import ch.rweiss.jmcli.WildcardFilters;
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

public class InfoAttribute extends AbstractJmxExecutor 
{
  @Command(name="attribute", description="Prints information about attributes")
  public static final class Cmd extends AbstractAttributeCommand
  {
    @Mixin
    private IntervalOption intervalOption = new IntervalOption();
    
    @Mixin
    private JvmOption jvmOption = new JvmOption();

    @Override
    public void run()
    {
      new InfoAttribute(this).execute();
    }
 
  }
  private Table<MAttribute> description = declareDescriptionTable();
  private Table<Pair<String, StyledText>> properties = declarePropertiesTable();
  private MBeanFilter beanFilter;
  private WildcardFilters attributeFilters;
  private BeanTitle beanTitle = new BeanTitle(ui());
  
  public InfoAttribute(Cmd command)
  {
    super("Attribute Info", command.intervalOption, command.jvmOption);
    beanFilter = command.beanFilter();
    attributeFilters = command.attributeFilters();
  }

  @Override
  protected void execute(CommandUi ui, JmxClient jmxClient)
  {
    beanTitle.reset();
    for (MBean bean : jmxClient.beansThatMatch(beanFilter))
    {
      List<MAttribute> attributes = attributeFilters.filter(bean.attributes(), MAttribute::name);
      if (!attributes.isEmpty())
      {
        beanTitle.printBeanNameTitle(bean);
        for (MAttribute attr : attributes)
        {
          print(attr);
        }
      }
    }
  }

  private void print(MAttribute attr)
  {
    printNameTitle(attr);
    printDescription(attr);
    
    properties.clear();
    properties.addRow(Pair.of("Name", new StyledText(attr.name(), Styles.VALUE)));
    properties.addRow(Pair.of("Type", new StyledText(attr.type(), Styles.VALUE)));
    properties.addRow(Pair.of("Readable", new StyledText(Boolean.toString(attr.isReadable()), Styles.VALUE)));
    properties.addRow(Pair.of("Writable", new StyledText(Boolean.toString(attr.isWritable()), Styles.VALUE)));
    properties.addRow(Pair.of("Value", ListAttributes.getValue(attr)));
    properties.print();
  }

  private void printNameTitle(MAttribute attr)
  {
    ui().printEmptyLine();
    ui().printSubTitle(attr.name());
  }
  
  private void printDescription(MAttribute attr)
  {
    ui().printEmptyLine();
    description.printSingleRow(attr);
    ui().printEmptyLine();
  }
    
  private static Table<MAttribute> declareDescriptionTable()
  {
    Table<MAttribute> table = new Table<>();
    table.addColumn(
        table.createColumn("", 40, attribute -> attribute.description())
          .multiLine()
          .withCellStyle(Styles.DESCRIPTION)
          .withMinWidth(8)
          .toColumn());
    return table;
  }

  private static Table<Pair<String,StyledText>> declarePropertiesTable()
  {
    Table<Pair<String, StyledText>> table = new Table<>();
    table.hideHeader();
    table.addColumn(
        table.createColumn("Name", 20, pair -> pair.getKey())
          .withAbbreviateStyle(AbbreviateStyle.LEFT)
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

