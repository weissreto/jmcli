package ch.rweiss.jmcli.list;

import ch.rweiss.jmcli.AbstractCommand;
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
