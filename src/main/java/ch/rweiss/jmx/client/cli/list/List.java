package ch.rweiss.jmx.client.cli.list;

import ch.rweiss.jmx.client.cli.AbstractCommand;
import picocli.CommandLine.Command;

@Command(name = "list", description="Lists objects", subcommands = {
    ListVirtualMachines.class,
    ListBeans.class,
    ListAttributes.class,
    ListThreads.class,
    ListThreadsStates.class,
    ListClasses.class
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
