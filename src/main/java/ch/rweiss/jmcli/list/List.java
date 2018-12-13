package ch.rweiss.jmcli.list;

import ch.rweiss.jmcli.AbstractCommand;
import picocli.CommandLine.Command;

@Command(name = "list", description="Lists objects", subcommands = {
    ListVirtualMachines.Cmd.class,
    ListBeans.Cmd.class,
    ListAttributes.Cmd.class,
    ListThreads.Cmd.class,
    ListThreadsStates.Cmd.class,
    ListClasses.Cmd.class,
    ListMethods.Cmd.class
    /*,
    ListOperations.class*/})
public class List extends AbstractCommand
{

  @Override
  public void run()
  {    
    new ListVirtualMachines.Cmd().run();
  }

}
