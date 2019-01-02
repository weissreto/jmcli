package ch.rweiss.jmcli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class TestJmCli
{
  private static final String STANDARD_COMMAND_OUTPUT = "\n"+
  "Java Virtual Maschines\n"+
  "\n"+
  "Id        Display Name\n";

  private static final String VERSION_TEXT = 
      "Java Management Command Line Interface\r\n" + 
      "0.3\r\n" + 
      "(c) 2018 Reto Weiss\r\n";

  private static final String HELP_TEXT = 
      "\033[33m       _   __  __    _____   _   _ \033[39m\033[0m\r\n"+
      "\033[33m      | | |  \\/  |  / ____| | | (_)\033[39m\033[0m\r\n"+
      "\033[33m      | | | \\  / | | |      | |  _ \033[39m\033[0m\r\n"+
      "\033[33m  _   | | | |\\/| | | |      | | | |\033[39m\033[0m\r\n"+
      "\033[33m | |__| | | |  | | | |____  | | | |\033[39m\033[0m\r\n"+
      "\033[33m  \\____/  |_|  |_|  \\_____| |_| |_|\033[39m\033[0m\r\n"+
      "\r\n"+
       "Usage: \033[1mjmcli\033[21m\033[0m [\033[33m-hvV\033[39m\033[0m] [COMMAND]\r\n"+
       "Java Management Command Line Interface\r\n"+
       "  \033[33m-h\033[39m\033[0m, \033[33m--help\033[39m\033[0m      Display this help and exit\r\n"+
       "  \033[33m-v\033[39m\033[0m, \033[33m--verbose\033[39m\033[0m   Display details message\r\n"+
       "  \033[33m-V\033[39m\033[0m, \033[33m--version\033[39m\033[0m   Display version info and exit\r\n"+
       "Commands:\r\n"+
       "  \033[1mlist\033[21m\033[0m       Lists objects\r\n"+
       "  \033[1minfo\033[21m\033[0m       Prints information about an object\r\n"+
       "  \033[1mset\033[21m\033[0m        Sets values\r\n"+
       "  \033[1minvoke\033[21m\033[0m     Invokes operation\r\n"+
       "  \033[1mchart\033[21m\033[0m      Draws a chart\r\n"+
       "  \033[1mdashboard\033[21m\033[0m  Draws a dashboard\r\n";
  
  @RegisterExtension
  public CommandTester tester = new CommandTester();
  
  @Test
  public void defaultCommandListVm()
  {
    JmCli.main(new String[] {});
    
    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }
  
  @Test
  public void helpShort()
  {
    JmCli.main(new String[] {"-h"});
    
    tester.assertStdErr().isEqualTo(HELP_TEXT);
  }
  
  @Test
  public void helpLong()
  {
    JmCli.main(new String[] {"--help"});
    
    tester.assertStdErr().isEqualTo(HELP_TEXT);
  }

  @Test
  public void versionShort()
  {
    JmCli.main(new String[] {"-V"});
    tester.assertStdErr().isEqualTo(VERSION_TEXT);
  }

  @Test
  public void versionLong()
  {
    JmCli.main(new String[] {"--version"});
    tester.assertStdErr().isEqualTo(VERSION_TEXT);
  }
  
  @Test 
  public void unknownOption()
  {
    JmCli.main(new String[] {"--wrongOption"});
    tester.assertStdErr().isEqualTo(
        "Unknown option: --wrongOption\r\n"+
        HELP_TEXT);
  }
  
  @Test 
  public void verboseShort()
  {
    JmCli.main(new String[] {"-v"});
    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

  @Test 
  public void verboseLong()
  {
    JmCli.main(new String[] {"--verbose"});
    tester.assertStdOut().startsWith(STANDARD_COMMAND_OUTPUT);
  }

}
