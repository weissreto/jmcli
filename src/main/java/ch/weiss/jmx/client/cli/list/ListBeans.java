package ch.weiss.jmx.client.cli.list;

import ch.weiss.jmx.client.MBean;
import ch.weiss.jmx.client.MBeanTreeNode;
import ch.weiss.jmx.client.cli.AbstractJmxClientCommand;
import ch.weiss.jmx.client.cli.Styles;
import picocli.CommandLine.Command;

@Command(name = "beans", description="Lists all available management beans")
public class ListBeans extends AbstractJmxClientCommand
{
  @Override
  protected void printTitle()
  {
    term.write("Beans");
  }

  @Override
  protected void execute()
  {
    MBeanTreeNode beanTree = getJmxClient().beanTree();
    print(beanTree, -2);
    term.reset();
  }

  private void print(MBeanTreeNode node, int indent)
  {
    if (indent >= 0)
    {
      print(indent);
      term.style(Styles.NAME);
      term.write(node.name().simpleName());
      MBean bean = node.bean();
      if (bean != null)
      {
        term.cursor().column(40);
        term.style(Styles.ID);
        term.write(bean.name().fullQualifiedName());
      }
      term.newLine();
    }
    for (MBeanTreeNode child : node.children())
    {
      print(child, indent + 2);
    }
  }

  private void print(int indent)
  {
    for (int pos = 0; pos < indent; pos++)
    {
      term.write(" ");
    }
  }

}
