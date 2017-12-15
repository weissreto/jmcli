package ch.weiss.jmx.client.cli.list;

import java.lang.management.ThreadInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.LongStream;

import javax.management.openmbean.CompositeData;

import org.apache.commons.lang3.StringUtils;

import ch.weiss.jmx.client.MBean;
import ch.weiss.jmx.client.MBeanName;
import ch.weiss.jmx.client.cli.AbstractJmxClientCommand;
import ch.weiss.jmx.client.cli.Styles;
import ch.weiss.terminal.Color;
import ch.weiss.terminal.Style;
import ch.weiss.terminal.chart.unit.Unit;
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

  private java.util.List<String> deadlockedThreadNames = new ArrayList<>();
  
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
    printEmptyLine();
    
    Table<ThreadData> table = declareTable();
    
    if (StringUtils.isNotBlank(columnName))
    {
      RowSorter<ThreadData> sorter = table.sortColumn(columnName);
      if ("DESC".equalsIgnoreCase(direction))
      {
        sorter.descending();
      }
    }
        
    MBean threadBean = jmxClient.bean(MBeanName.THREAD);
    threadBean.attribute("ThreadCpuTimeEnabled").value(true);
    threadBean.attribute("ThreadContentionMonitoringEnabled").value(true);
    long[] deadlockedThreads = (long[])threadBean.operation("findDeadlockedThreads").invoke(new Object[0]);
    deadlockedThreadNames.clear();
    CompositeData[] threads = (CompositeData[])threadBean.operation("dumpAllThreads", "boolean", "boolean").invoke(false, false);
    for (CompositeData thread : threads)
    {
      ThreadInfo info = ThreadInfo.from(thread);
      ThreadData data = new ThreadData(info);
      data.cpuTime = (long)threadBean.operation("getThreadCpuTime", "long").invoke(info.getThreadId());
      data.userTime = (long) threadBean.operation("getThreadUserTime", "long").invoke(info.getThreadId()); 
      ThreadData lastData = lastValues.get(info.getThreadId());
      if (lastData == null)
      {
        lastData = data;
      }
      if (isDeadlocked(deadlockedThreads, info))
      {
        data.isDeadLocked = true;
      }
      table.addRow(deltaOrAbsolute(data, lastData));
      lastValues.put(info.getThreadId(), data);
    }
    table.print();
  }

  private ThreadData deltaOrAbsolute(ThreadData currentValue, ThreadData lastValue)
  {
    if (delta)
    {
      return new ThreadData(currentValue, lastValue);
    }
    return currentValue;
  }

  private Table<ThreadData> declareTable()
  {
    Table<ThreadData> table = new Table<>();
    table.addColumn(
        table.createColumn("Name", 40)
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyler(this::getDeadlockStyle)
          .withTextProvider(data -> data.name)
          .toColumn());
    
    table.addColumn(
        table.createColumn("State", 15, data -> data.state)
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyler(ListThreads::getThreadStateStyle)
          .toColumn());
    
    table.addColumn(
        table.createColumn("Cpu", 9, data -> data.cpuTime)
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.VALUE)
          .withTextProvider(ListThreads::formatNanoSeconds)
          .toColumn());
        
    table.addColumn(
        table.createColumn("User", 9, data -> data.userTime)
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.VALUE)
          .withTextProvider(ListThreads::formatNanoSeconds)
          .toColumn());
    
    table.addColumn(
        table.createColumn("Waited", 9, data-> data.waitedTime)
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.VALUE)
          .withTextProvider(ListThreads::formatMilliSeconds)
          .toColumn());
    
    table.addColumn(
        table.createColumn("Waited", 9, data -> data.waitedCount)
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.VALUE)
          .withTextProvider(ListThreads::formatCount)
          .toColumn());
    
    table.addColumn(
        table.createColumn("Blocked", 9, data -> data.blockedTime)
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.VALUE)
          .withTextProvider(ListThreads::formatMilliSeconds)
          .toColumn());
    
    table.addColumn(
        table.createColumn("Blocked", 9, data -> data.blockedCount)
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.VALUE)
          .withTextProvider(ListThreads::formatCount)
          .toColumn());
    return table;
  }
  
  private Style getDeadlockStyle(ThreadData data)
  {
    if (data.isDeadLocked)
    {
      return Styles.ERROR;
    }
    return Styles.NAME;
  }
  
  private static Style getThreadStateStyle(Thread.State state)
  {
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
        return GREEN; 
      default:
        return Styles.VALUE;
    }
  }
    
  private static String formatNanoSeconds(Long value)
  {
    return format(value, Unit.NANO_SECONDS, true);
  }

  private static String formatMilliSeconds(Long value)
  {
    return format(value, Unit.MILLI_SECONDS, true);
  }
  
  private static String formatCount(Long value)
  {
    return format(value, Unit.NONE, false);
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

  private static boolean isDeadlocked(long[] deadlockedThreads, ThreadInfo info)
  {
    return deadlockedThreads != null && LongStream.of(deadlockedThreads).filter(x->x==info.getThreadId()).findAny().isPresent();
  }

  private static class ThreadData
  {
    private ThreadData(ThreadData currentValue, ThreadData lastValue)
    {
      isDeadLocked = currentValue.isDeadLocked;
      name = currentValue.name;
      state = currentValue.state;
      cpuTime = currentValue.cpuTime - lastValue.cpuTime;
      userTime = currentValue.userTime - lastValue.userTime;
      waitedTime = currentValue.waitedTime - lastValue.waitedTime;
      waitedCount = currentValue.waitedCount - lastValue.waitedCount;
      blockedTime = currentValue.blockedTime - lastValue.blockedTime;
      blockedCount = currentValue.blockedCount - lastValue.blockedCount;
    }
    
    private ThreadData(ThreadInfo info)
    {
      isDeadLocked = false;
      name = info.getThreadName();
      state = info.getThreadState();
      waitedTime = info.getWaitedTime();
      waitedCount = info.getWaitedCount();
      blockedTime = info.getBlockedTime();
      blockedCount = info.getBlockedCount();
    }
    
    private boolean isDeadLocked;
    private String name;
    private Thread.State state;
    private long cpuTime;
    private long userTime;
    private long waitedTime;
    private long waitedCount;
    private long blockedTime;
    private long blockedCount;
  }
}
