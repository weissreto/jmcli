package ch.rweiss.jmx.client.cli.list;

import ch.rweiss.jmx.client.MBeanTreeNode;
import ch.rweiss.jmx.client.cli.AbstractJmxClientCommand;
import ch.rweiss.jmx.client.cli.Styles;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;

@Command(name = "beans", description="Lists all available management beans")
public class ListBeans extends AbstractJmxClientCommand
{
  private Table<MBeanTreeNode> table = declareTable();

  @Override
  protected void printTitle()
  {
    term.write("Beans");
  }

  @Override
  protected void execute()
  {
    printEmptyLine();
    
    table.clear();

    MBeanTreeNode beanTree = getJmxClient().beanTree();    
    addBeans(beanTree);

    table.printWithoutHeader();
  }

  private static Table<MBeanTreeNode> declareTable()
  {
    Table<MBeanTreeNode> table = new Table<>();
    table.addColumn(
        table.createColumn("Name", 30, node -> simpleNameWithIndent(node))
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.NAME)
          .withMinWidth(20)
          .toColumn());
    
    table.addColumn(
        table.createColumn("Full Qualified Name", 60, node -> node.name().fullQualifiedName())
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.ID)
          .withMinWidth(10)
          .toColumn());
    return table;
  }

  private static String simpleNameWithIndent(MBeanTreeNode node)
  {
    StringBuilder builder = new StringBuilder();
    for (int indent = 0; indent < node.name().countParts()-1; indent++)
    {
      builder.append("  ");
    }
    builder.append(node.name().simpleName());
    return builder.toString();
  }

  private void addBeans(MBeanTreeNode node)
  {
    if (node.name().countParts() > 0)
    {
      table.addRow(node);
    }
    for (MBeanTreeNode child : node.children())
    {
      addBeans(child);
    }
  }
}
