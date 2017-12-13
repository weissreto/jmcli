package ch.weiss.jmx.client.cli.list;

import ch.weiss.jmx.client.cli.AbstractCommand;
import picocli.CommandLine.Command;

@Command(name = "list", description="Lists objects", subcommands = {
    ListVirtualMachines.class,
    ListBeans.class,
    ListAttributes.class,
    ListThreads.class
    /*,
    ListOperations.class*/})
public class List extends AbstractCommand
{

  @Override
  public void run()
  {    
    new ListVirtualMachines().run();
  }

}
