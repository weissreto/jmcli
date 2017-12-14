package ch.weiss.jmx.client.cli.list;

import java.lang.management.ThreadInfo;
import java.util.HashMap;
import java.util.Map;

import javax.management.openmbean.CompositeData;

import org.apache.commons.lang3.StringUtils;

import ch.weiss.jmx.client.MBean;
import ch.weiss.jmx.client.MBeanName;
import ch.weiss.jmx.client.cli.AbstractJmxClientCommand;
import ch.weiss.jmx.client.cli.Styles;
import ch.weiss.terminal.Color;
import ch.weiss.terminal.Style;
import ch.weiss.terminal.chart.unit.Unit;
import ch.weiss.terminal.table.Column;
import ch.weiss.terminal.table.RowSorter;
import ch.weiss.terminal.table.Table;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "threads", description="Lists all treads")
public class ListThreads extends AbstractJmxClientCommand
{
  @Parameters(index="0", arity="0..1", paramLabel="COLUMN", description="Column name to sort")
  private String columnName;

  @Parameters(index="1", arity="0..1", paramLabel="DIRECTION", description="ASC or DESC")
  private String direction;
  
  @Option(names = {"-d", "--delta"}, description = "Displays delta instead of absolute values")
  protected boolean delta;

  private Map<Long, ThreadData> lastValues = new HashMap<>();
  
  private static final Style GREEN = Style.create().withColor(Color.BRIGHT_GREEN).toStyle();
  private static final Style YELLOW = Style.create().withColor(Color.BRIGHT_YELLOW).toStyle();

  @Override
  protected void printTitle()
  {
    term.write("Threads");
  }

  @Override
  protected void execute()
  {
    term.clear().lineToEnd();
    term.newLine();
    
    Table table = declareTable();
    
    if (StringUtils.isNotBlank(columnName))
    {
      RowSorter sorter = table.sortColumn(columnName);
      if ("DESC".equalsIgnoreCase(direction))
      {
        sorter.descending();
      }
    }
        
    MBean threadBean = jmxClient.bean(MBeanName.THREAD);
    threadBean.attribute("ThreadCpuTimeEnabled").value(true);
    threadBean.attribute("ThreadContentionMonitoringEnabled").value(true);
    CompositeData[] threads = (CompositeData[])threadBean.operation("dumpAllThreads", "boolean", "boolean").invoke(false, false);
    for (CompositeData thread : threads)
    {
      ThreadInfo info = ThreadInfo.from(thread);
      ThreadData data = new ThreadData();
      data.info = info;
      data.cpuTime = (long)threadBean.operation("getThreadCpuTime", "long").invoke(info.getThreadId());
      data.userTime = (long) threadBean.operation("getThreadUserTime", "long").invoke(info.getThreadId()); 
      ThreadData lastData = lastValues.get(info.getThreadId());
      if (lastData == null)
      {
        lastData = data;
      }
      table.addRow();
      table.addValue(info.getThreadName());
      table.addValue(info.getThreadState());
      long cpuTime = deltaOrAbsolute(data.cpuTime, lastData.cpuTime); 
      table.addValue(cpuTime);
      long usrTime = deltaOrAbsolute(data.userTime, lastData.userTime);
      table.addValue(usrTime);
      long waitedTime = deltaOrAbsolute(data.info.getWaitedTime(), lastData.info.getWaitedTime());
      table.addValue(waitedTime);
      long waited = deltaOrAbsolute(data.info.getWaitedCount(), lastData.info.getWaitedCount());
      table.addValue(waited);
      long blockedTime = deltaOrAbsolute(data.info.getBlockedTime(), lastData.info.getBlockedTime());
      table.addValue(blockedTime);
      long blocked = deltaOrAbsolute(data.info.getBlockedCount(), lastData.info.getBlockedCount());
      table.addValue(blocked);
      lastValues.put(info.getThreadId(), data);
    }
    table.print();
  }

  private long deltaOrAbsolute(long currentValue, long lastValue)
  {
    if (delta)
    {
      return currentValue-lastValue;
    }
    return currentValue;
  }

  private static Table declareTable()
  {
    Table table = new Table();
    table.addColumn(
        Column.create("Name", 40)
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.NAME)
          .toColumn());
    
    table.addColumn(
        Column.create("State", 15)
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyler(ListThreads::getThreadStateStyle)
          .toColumn());
    
    table.addColumn(
        Column.create("Cpu", 9)
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.VALUE)
          .withTextProvider(ListThreads::formatNanoSeconds)
          .withSorter(ListThreads::compareLongs)
          .toColumn());
        
    table.addColumn(
        Column.create("User", 9)
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.VALUE)
          .withTextProvider(ListThreads::formatNanoSeconds)
          .withSorter(ListThreads::compareLongs)
          .toColumn());
    
    table.addColumn(
        Column.create("Waited", 9)
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.VALUE)
          .withTextProvider(ListThreads::formatMilliSeconds)
          .withSorter(ListThreads::compareLongs)
          .toColumn());
    
    table.addColumn(
        Column.create("Waited", 9)
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.VALUE)
          .withTextProvider(ListThreads::formatCount)
          .withSorter(ListThreads::compareLongs)
          .toColumn());
    
    table.addColumn(
        Column.create("Blocked", 9)
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.VALUE)
          .withTextProvider(ListThreads::formatMilliSeconds)
          .withSorter(ListThreads::compareLongs)
          .toColumn());
    
    table.addColumn(
        Column.create("Blocked", 9)
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.VALUE)
          .withTextProvider(ListThreads::formatCount)
          .withSorter(ListThreads::compareLongs)
          .toColumn());
    return table;
  }
  
  private static Style getThreadStateStyle(Object state)
  {
    switch((Thread.State)state)
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
        return GREEN; 
      default:
        return Styles.VALUE;
    }
  }
  
  private static int compareLongs(Object o1, Object o2)
  {
    return Long.compare((long)o1, (long)o2);
  }
  
  private static String formatNanoSeconds(Object value)
  {
    return format((long)value, Unit.NANO_SECONDS, true);
  }

  private static String formatMilliSeconds(Object value)
  {
    return format((long)value, Unit.MILLI_SECONDS, true);
  }
  
  private static String formatCount(Object value)
  {
    return format((long)value, Unit.NONE, false);
  }

  private static  String format(long value, Unit unit, boolean doDownScale)
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
      if (length < 2 && doDownScale)
      {
        newScaledUnit = scaledUnit.scaleDown();
      }
    } while (newScaledUnit != scaledUnit);
    return valueStr + " "+scaledUnit.symbol();
  }
  
  private static class ThreadData
  {
    private ThreadInfo info;
    private long cpuTime;
    private long userTime;    
  }
  
}
