package ch.weiss.jmx.client.cli.list;

import javax.management.openmbean.CompositeData;

import ch.weiss.jmx.client.MBean;
import ch.weiss.jmx.client.MBeanName;
import ch.weiss.jmx.client.cli.AbstractJmxClientCommand;
import ch.weiss.jmx.client.cli.Styles;
import ch.weiss.terminal.Color;
import ch.weiss.terminal.Style;
import ch.weiss.terminal.chart.unit.Unit;
import picocli.CommandLine.Command;

@Command(name = "threads", description="Lists all treads")
public class ListThreads extends AbstractJmxClientCommand
{
  private static final Style GREEN = Style.create().withColor(Color.GREEN).toStyle();
  private static final Style BRIGHT_GREEN = Style.create().withColor(Color.BRIGHT_GREEN).toStyle();
  private static final Style YELLOW = Style.create().withColor(Color.BRIGHT_YELLOW).toStyle();

  @Override
  protected void printTitle()
  {
    term.write("Threads");
  }

  @Override
  protected void execute()
  {
    term.newLine();
    
    Table table = new Table();
    table.addColumn("Name", 40, Styles.NAME_TITLE, Styles.NAME);
    table.addColumn("State", 15, Styles.NAME_TITLE, Styles.VALUE);
    table.addColumn("Cpu", 9, Styles.NAME_TITLE, Styles.VALUE);
    table.addColumn("User", 9, Styles.NAME_TITLE, Styles.VALUE);
    table.addColumn("Waited", 9, Styles.NAME_TITLE, Styles.VALUE);
    table.addColumn("Waited", 9, Styles.NAME_TITLE, Styles.VALUE);
    table.addColumn("Blocked", 9, Styles.NAME_TITLE, Styles.VALUE);
    table.addColumn("Blocked", 9, Styles.NAME_TITLE, Styles.VALUE);
        
    MBean threadBean = jmxClient.bean(MBeanName.THREAD);
    threadBean.attribute("ThreadCpuTimeEnabled").value(true);
    threadBean.attribute("ThreadContentionMonitoringEnabled").value(true);
    CompositeData[] threads = (CompositeData[])threadBean.operation("dumpAllThreads", "boolean", "boolean").invoke(false, false);
    for (CompositeData thread : threads)
    {
      table.addRow();
      table.addValue((String)thread.get("threadName"));
      String state = (String)thread.get("threadState");
      boolean suspended = (boolean)thread.get("suspended");
      table.addValue(state, getStyle(state, suspended));
      long threadId = (long) thread.get("threadId");
      long cpuTime = (long)threadBean.operation("getThreadCpuTime", "long").invoke(threadId);
      table.addValue(format(cpuTime, Unit.NANO_SECONDS));
      long usrTime = (long) threadBean.operation("getThreadUserTime", "long").invoke(threadId);
      table.addValue(format(usrTime, Unit.NANO_SECONDS));
      long waitedTime = (long)thread.get("waitedTime");
      table.addValue(format(waitedTime, Unit.MILLI_SECONDS));
      long waited = (long) thread.get("waitedCount");
      table.addValue(format(waited, Unit.NONE));
      long blockedTime = (long) thread.get("blockedTime");
      table.addValue(format(blockedTime, Unit.MILLI_SECONDS));
      long blocked = (long) thread.get("blockedCount");
      table.addValue(format(blocked, Unit.NONE));
    }
    table.print();
  }
  
  private static Style getStyle(String stateName, boolean suspended)
  {
    Thread.State state = Thread.State.valueOf(stateName);
    switch(state)
    {
      case NEW:
        return Styles.VALUE;
      case BLOCKED:
        return Styles.ERROR;
      case TERMINATED:
        return Styles.VALUE;
      case WAITING:
        return YELLOW;
      case TIMED_WAITING:
        return YELLOW;
      case RUNNABLE:
        return suspended ? GREEN : BRIGHT_GREEN; 
      default:
        return Styles.VALUE;
    }
  }

  public String format(long value, Unit unit)
  {
    if (value == 0)
    {
      return "0 "+unit.symbol();
    }
    Unit scaledUnit = unit;
    Unit newScaledUnit = unit;
    String valueStr;
    do
    {
      scaledUnit = newScaledUnit;
      valueStr = Long.toString(unit.convertTo(value, scaledUnit));
      int length = valueStr.length();
      if (length > 4)
      {
        newScaledUnit = scaledUnit.scaleUp();
      }
      if (length < 2)
      {
        newScaledUnit = scaledUnit.scaleDown();
      }
    } while (newScaledUnit != scaledUnit);
    return valueStr + " "+scaledUnit.symbol();
  }
}
