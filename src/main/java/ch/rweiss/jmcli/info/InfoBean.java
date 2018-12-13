package ch.rweiss.jmcli.info;

import org.apache.commons.lang3.tuple.Pair;

import ch.rweiss.jmcli.AbstractBeanCommand;
import ch.rweiss.jmcli.IntervalOption;
import ch.rweiss.jmcli.JvmOption;
import ch.rweiss.jmcli.Styles;
import ch.rweiss.jmcli.executor.AbstractJmxExecutor;
import ch.rweiss.jmcli.ui.BeanTitle;
import ch.rweiss.jmcli.ui.CommandUi;
import ch.rweiss.jmx.client.JmxClient;
import ch.rweiss.jmx.client.MAttribute;
import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.MBeanFilter;
import ch.rweiss.jmx.client.MOperation;
import ch.rweiss.terminal.table.AbbreviateStyle;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

public class InfoBean extends AbstractJmxExecutor
{
  @Command(name="bean", description="Prints information about managment beans")
  public static final class Cmd extends AbstractBeanCommand
  {
    @Mixin
    private IntervalOption intervalOption = new IntervalOption();
    
    @Mixin
    private JvmOption jvmOption = new JvmOption();

    @Override
    public void run()
    {
      new InfoBean(this).execute();
    }
  }
  
  private Table<MBean> description = declareDescriptionTable();
  private Table<Pair<String, String>> properties = declarePropertiesTable();
  private Table<MAttribute> attributes = declareAttributesTable();
  private Table<MOperation> operations = declareOperationsTable();
  private MBeanFilter beanFilter;
  private BeanTitle beanTitle = new BeanTitle(ui());

  public InfoBean(Cmd command)
  {
    super("Bean Info", command.intervalOption, command.jvmOption);
    beanFilter = command.beanFilter();
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

  private void print(MBean bean)
  {
    beanTitle.printBeanNameTitle(bean);
    printDescription(bean);
    printNameAndType(bean);
    printAttributes(bean);
    printOperations(bean);
  }

  private void printDescription(MBean bean)
  {
    ui().printEmptyLine();
    description.printSingleRow(bean);
    ui().printEmptyLine();
  }

  private void printNameAndType(MBean bean)
  {
    properties.clear();
    properties.addRow(Pair.of("Name", bean.name().fullQualifiedName()));
    properties.addRow(Pair.of("Type", bean.type()));
    properties.print();
    ui().printEmptyLine();
  }

  private void printAttributes(MBean bean)
  {
    if (!bean.attributes().isEmpty())
    {
      ui().printSubTitle("Attributes:");

      attributes.setRows(bean.attributes());
      attributes.print();
      ui().printEmptyLine();
    }
  }

  private void printOperations(MBean bean)
  {
    if (!bean.operations().isEmpty())
    {
      ui().printSubTitle("Operations:");
      operations.setRows(bean.operations());
      operations.print();
    }
  }
    
  private static Table<MBean> declareDescriptionTable()
  {
    Table<MBean> table = new Table<>();
    table.addColumn(
        table.createColumn("", 40, bean -> bean.description())
          .multiLine()
          .withCellStyle(Styles.DESCRIPTION)
          .withMinWidth(8)
          .toColumn());
    return table;
  }

  private static Table<Pair<String,String>> declarePropertiesTable()
  {
    Table<Pair<String, String>> table = new Table<>();
    table.hideHeader();
    table.addColumn(
        table.createColumn("Name", 20, pair -> pair.getKey())
          .withAbbreviateStyle(AbbreviateStyle.LEFT)
          .withCellStyle(Styles.NAME)
          .withMinWidth(8)
          .toColumn());
    table.addColumn(
        table.createColumn("Value", 60, pair -> pair.getValue())
          .multiLine()
          .withCellStyle(Styles.VALUE)
          .withMinWidth(8)
          .toColumn());
    return table;
  }
  
  private static Table<MAttribute> declareAttributesTable()
  {
    Table<MAttribute> table = new Table<>();
    table.hideHeader();
    table.addColumn(
        table.createColumn("Name", 20, attribute -> attribute.name())
          .withAbbreviateStyle(AbbreviateStyle.RIGHT_WITH_DOTS)
          .withMinWidth(8)
          .withCellStyle(Styles.NAME)
          .toColumn());
    table.addColumn(
        table.createColumn("Type", 60, attribute -> attribute.type())
          .withAbbreviateStyle(AbbreviateStyle.RIGHT_WITH_DOTS)
          .withMinWidth(8)
          .withCellStyle(Styles.VALUE)
          .toColumn());
    return table;
  }

  private static Table<MOperation> declareOperationsTable()
  {
    Table<MOperation> table = new Table<>();
    table.hideHeader();
    table.addColumn(
        table.createColumn("Name", 20, operation -> operation.name())
          .withAbbreviateStyle(AbbreviateStyle.RIGHT_WITH_DOTS)
          .withMinWidth(8)
          .withCellStyle(Styles.NAME)
          .toColumn());
    table.addColumn(
        table.createColumn("Signature", 60, operation -> operation.signature())
          .withAbbreviateStyle(AbbreviateStyle.RIGHT_WITH_DOTS)
          .withMinWidth(8)
          .withCellStyle(Styles.VALUE)
          .toColumn());
    return table;
  }

  
}
