package ch.rweiss.jmcli.info;

import ch.rweiss.jmcli.AbstractCommand;
import picocli.CommandLine.Command;

@Command(name="info", description="Prints information about an object", subcommands={
    InfoVirtualMachine.class,
    InfoBean.class,
    InfoAttribute.class,
    InfoOperation.class    
})
public class Info extends AbstractCommand
{

  @Override
  public void run()
  {
    new InfoVirtualMachine().run();
  }

}
