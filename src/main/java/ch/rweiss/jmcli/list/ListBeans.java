package ch.rweiss.jmcli.list;

import ch.rweiss.jmcli.AbstractJmxClientCommand;
import ch.rweiss.jmcli.Styles;
import ch.rweiss.jmx.client.MBeanTreeNode;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;

@Command(name = "beans", description="Lists all available management beans")
public class ListBeans extends AbstractJmxClientCommand
{
  private Table<MBeanTreeNode> table = declareTable();

  ListBeans()
  {
    super("Beans");
  }

  @Override
  protected void execute()
  {
    table.clear();

    MBeanTreeNode beanTree = getJmxClient().beanTree();    
    addBeans(beanTree);

    table.printWithoutHeader();   
  }
  
  @Override
  protected void afterRun()
  {
    super.afterRun();
    term.clear().screenToEnd();
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
          .withCellStyle(Styles.VALUE)
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
