package ch.rweiss.jmcli;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.RegisterExtension;

public class TestJmCli
{
  private static final String NEW_LINE = System.lineSeparator();
  
  private static final String STANDARD_COMMAND_OUTPUT = "\n"+
      "Java Virtual Maschines\n"+
      "\n"+
      "Id        Display Name\n";

  private static final String VERSION_TEXT = 
      "Java Management Command Line Interface" + NEW_LINE + 
      "0.3"  + NEW_LINE + 
      "(c) 2018 Reto Weiss" + NEW_LINE;

  private static final String HELP_TEXT = 
      "\033[33m       _   __  __    _____   _   _ \033[39m\033[0m" + NEW_LINE +
      "\033[33m      | | |  \\/  |  / ____| | | (_)\033[39m\033[0m" + NEW_LINE +
      "\033[33m      | | | \\  / | | |      | |  _ \033[39m\033[0m" + NEW_LINE +
      "\033[33m  _   | | | |\\/| | | |      | | | |\033[39m\033[0m" + NEW_LINE +
      "\033[33m | |__| | | |  | | | |____  | | | |\033[39m\033[0m" + NEW_LINE +
      "\033[33m  \\____/  |_|  |_|  \\_____| |_| |_|\033[39m\033[0m" + NEW_LINE +
      "" + NEW_LINE +
       "Usage: \033[1mjmcli\033[21m\033[0m [\033[33m-hvV\033[39m\033[0m] [COMMAND]" + NEW_LINE +
       "Java Management Command Line Interface" + NEW_LINE +
       "  \033[33m-h\033[39m\033[0m, \033[33m--help\033[39m\033[0m      Display this help and exit" + NEW_LINE +
       "  \033[33m-v\033[39m\033[0m, \033[33m--verbose\033[39m\033[0m   Display details message" + NEW_LINE +
       "  \033[33m-V\033[39m\033[0m, \033[33m--version\033[39m\033[0m   Display version info and exit" + NEW_LINE + 
       "Commands:" + NEW_LINE +
       "  \033[1mlist\033[21m\033[0m       Lists objects" + NEW_LINE +
       "  \033[1minfo\033[21m\033[0m       Prints information about an object" + NEW_LINE +
       "  \033[1mset\033[21m\033[0m        Sets values" + NEW_LINE +
       "  \033[1minvoke\033[21m\033[0m     Invokes operation" + NEW_LINE +
       "  \033[1mchart\033[21m\033[0m      Draws a chart" + NEW_LINE +
       "  \033[1mdashboard\033[21m\033[0m  Draws a dashboard" + NEW_LINE;
  
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
        "Unknown option: --wrongOption" + NEW_LINE+
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
