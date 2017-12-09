package ch.weiss.jmx.client.cli.list;

import ch.weiss.jmx.client.Jvm;
import ch.weiss.jmx.client.cli.AbstractHeaderCommand;
import ch.weiss.jmx.client.cli.Styles;
import picocli.CommandLine.Command;

@Command(name="vm", description="Lists all available java virtual maschines")
public class ListVirtualMachines extends AbstractHeaderCommand
{

  @Override
  protected void printTitle()
  {
    term.write("Available Java Virtual Machines");    
  }
  
  @Override
  public void execute()
  {
    for (Jvm jvm : Jvm.getAvailableRunningJvms())
    {
      term.style(Styles.ID);
      term.write(jvm.id());
      term.cursor().column(10);
      term.style(Styles.NAME);
      term.write(jvm.displayName());
      term.newLine();
    }
  }

}
