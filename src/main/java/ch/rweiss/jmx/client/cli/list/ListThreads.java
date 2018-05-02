package ch.rweiss.jmx.client.cli.list;

import java.lang.management.ThreadInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.LongStream;

import javax.management.openmbean.CompositeData;

import org.apache.commons.lang3.StringUtils;

import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.MBeanName;
import ch.rweiss.jmx.client.cli.AbstractJmxClientCommand;
import ch.rweiss.jmx.client.cli.Styles;
import ch.rweiss.terminal.Color;
import ch.rweiss.terminal.Style;
import ch.rweiss.terminal.StyledText;
import ch.rweiss.terminal.chart.unit.Unit;
import ch.rweiss.terminal.table.AbbreviateStyle;
import ch.rweiss.terminal.table.RowSorter;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

@Command(name = "threads", description="Lists all treads")
public class ListThreads extends AbstractJmxClientCommand
{
  private static final int MILLIS_TO_NANOS = 1000*1000;

  @Parameters(index="0", arity="0..1", paramLabel="COLUMN", description="Column name to sort")
  private String sortColumnName;

  @Parameters(index="1", arity="0..1", paramLabel="DIRECTION", description="ASC or DESC")
  private String direction;
  
  @Option(names = {"-d", "--delta"}, description = "Displays delta instead of absolute values")
  protected boolean delta;

  @Option(names = {"-f", "--format"}, description = "Format of the values (s)econds, (p)ercentage, (g)raphics)")
  protected String format;

  private Map<Long, ThreadData> lastValues = new HashMap<>();
  private long lastExecutionTimeInNanoSeconds = -1;
  private long deltaTimeInNanoSeconds;

  private java.util.List<String> deadlockedThreadNames = new ArrayList<>();

  private Table<ThreadData> table = declareTable();
  
  private static final Style GREEN = Style.create().withColor(Color.BRIGHT_GREEN).toStyle();
  private static final Style[] GREENS_GRAPHICS = buildGradient(0, 63, 0, 0, 24, 0, 8);
  private static final Style[] GREENS_PERCENTAGE = buildGradient(0, 155, 0, 0, 1, 0, 101);
  private static final Style YELLOW = Style.create().withColor(Color.BRIGHT_YELLOW).toStyle();
  private static final Style[] YELLOWS_GRAPHICS = buildGradient(63, 63, 0, 24, 24, 0, 8);
  private static final Style[] YELLOWS_PERCENTAGE = buildGradient(155, 155, 0, 1, 1, 0, 101);
  private static final Style RED = Styles.ERROR;
  private static final Style[] REDS_GRAPHICS = buildGradient(63, 0, 0, 24, 0, 0, 8);
  private static final Style[] REDS_PERCENTAGE = buildGradient(155, 0, 0, 1, 0, 0, 101);


  
  @Override
  protected void printTitle()
  {
    term.write("Threads");
  }

  private static Style[] buildGradient(int startRed, int startGreen, int startBlue, int deltaRed, int deltaGreen, int deltaBlue, int count)
  {
    Style[] gradient = new Style[count];
    for (int pos = 0; pos < count; pos++)
    {
      gradient[pos] = Style.create()
          .withColor(
              new Color(
                  startRed+pos*deltaRed, 
                  startGreen+pos*deltaGreen,
                  startBlue+pos*deltaBlue)
              )
          .toStyle();
    }
    return gradient;
  }

  @Override
  protected void execute()
  {
    printEmptyLine();
    
    if (StringUtils.isNotBlank(sortColumnName))
    {
      RowSorter<ThreadData> sorter = table.sortColumn(sortColumnName);
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
    computeDeltaTime();
    table.clear();
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
      table .addRow(deltaOrAbsolute(data, lastData));
      lastValues.put(info.getThreadId(), data);
    }
    table .print();
  }

  private void computeDeltaTime()
  {
    long currentExecutionTime = System.nanoTime();
    deltaTimeInNanoSeconds = currentExecutionTime - lastExecutionTimeInNanoSeconds;
    if (deltaTimeInNanoSeconds <= 0)
    {
      deltaTimeInNanoSeconds = 1;
    }
    lastExecutionTimeInNanoSeconds = currentExecutionTime;
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
    Table<ThreadData> threadsTable = new Table<>();
    threadsTable.addColumn(
        threadsTable.createColumn("Name", 30)
          .withTitleStyle(Styles.NAME_TITLE)
          .withStyledTextProvider(ListThreads::threadName)
          .withAbbreviateStyle(AbbreviateStyle.LEFT_WITH_DOTS)
          .withMinWidth(10)
          .toColumn());
    
    threadsTable.addColumn(
        threadsTable.createColumn("State", 15, data -> data.state)
          .withTitleStyle(Styles.NAME_TITLE)
          .withStyledTextProvider(ListThreads::threadState)
          .toColumn());
    
    threadsTable.addColumn(
        threadsTable.createColumn("Cpu", 9, data -> data.cpuTime)
          .withTitleStyle(Styles.NAME_TITLE)
          .withStyledTextProvider(this::cpuUsage)
          .toColumn());
        
    threadsTable.addColumn(
        threadsTable.createColumn("User", 9, data -> data.userTime)
          .withTitleStyle(Styles.NAME_TITLE)
          .withStyledTextProvider(this::cpuUsage)
          .toColumn());
    
    threadsTable.addColumn(
        threadsTable.createColumn("Waited", 9, data-> data.waitedTime)
          .withTitleStyle(Styles.NAME_TITLE)
          .withStyledTextProvider(this::waitedTime)
          .toColumn());
    
    threadsTable.addColumn(
        threadsTable.createColumn("Waited", 9, data -> data.waitedCount)
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(YELLOW)
          .withTextProvider(ListThreads::formatCount)
          .toColumn());
    
    threadsTable.addColumn(
        threadsTable.createColumn("Blocked", 9, data -> data.blockedTime)
          .withTitleStyle(Styles.NAME_TITLE)
          .withStyledTextProvider(this::blockedTime)
          .toColumn());
    
    threadsTable.addColumn(
        threadsTable.createColumn("Blocked", 9, data -> data.blockedCount)
          .withTitleStyle(Styles.NAME_TITLE)
          .withCellStyle(Styles.ERROR)
          .withTextProvider(ListThreads::formatCount)
          .toColumn());
    return threadsTable;
  }
  
  private static StyledText threadName(ThreadData data)
  {
    if (data.isDeadLocked)
    {
      return new StyledText(data.name, Styles.ERROR);
    }
    return new StyledText(data.name, Styles.NAME);
  }
  
  private static StyledText threadState(Thread.State state)
  {
    return new StyledText(state.toString(), getThreadStateStyle(state));
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
  
  private StyledText cpuUsage(Long value)
  {
    if (isFormatGraphics())
    {
      long count = scaledDeltaValue(value, 8, 1);
      return buildGraphics(count, ">", GREENS_GRAPHICS);
    }
    if (isFormatPercentage())
    {
      int percentage = (int)scaledDeltaValue(value, 100, 1);
      return new StyledText(Long.toString(percentage)+" %", GREENS_PERCENTAGE[percentage]);
    }
    return new StyledText(formatNanoSeconds(value), GREEN);
  }

  private static String formatNanoSeconds(Long value)
  {
    return format(value, Unit.NANO_SECONDS, true);
  }

  private StyledText waitedTime(Long value)
  {
    if (isFormatGraphics())
    {
      long count = scaledDeltaValue(value, 8, MILLIS_TO_NANOS);
      return buildGraphics(count, "o", YELLOWS_GRAPHICS);
    }
    if (isFormatPercentage())
    {
      int percentage = (int)scaledDeltaValue(value, 100, MILLIS_TO_NANOS);
      return new StyledText(Long.toString(percentage)+" %", YELLOWS_PERCENTAGE[percentage]);
    }
    return new StyledText(formatMilliSeconds(value), YELLOW);
  }

  private StyledText blockedTime(Long value)
  {
    if (isFormatGraphics())
    {
      long count = scaledDeltaValue(value, 8, MILLIS_TO_NANOS);
      return buildGraphics(count, "x", REDS_GRAPHICS);
    }
    if (isFormatPercentage())
    {
      int percentage = (int)scaledDeltaValue(value, 100, MILLIS_TO_NANOS);
      return new StyledText(Long.toString(percentage)+" %", REDS_PERCENTAGE[percentage]);
    }
    return new StyledText(formatMilliSeconds(value), RED);
  }

  private static StyledText buildGraphics(long count, String symbol, Style[] gradient)
  {
    StyledText graphicText = new StyledText("");
    for (int pos = 0; pos < count; pos++)
    {
      graphicText = graphicText.append(symbol, gradient[pos]); 
    }
    return graphicText;
  }

  private long scaledDeltaValue(long value, long maxValue, long unitScale)
  {
    return Math.min(value*maxValue*unitScale/deltaTimeInNanoSeconds, maxValue);
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
  
  private boolean isFormatGraphics()
  {
    return isDelta() && 
           format != null &&
           format.startsWith("g");
  }
  
  private boolean isFormatPercentage()
  {
    return isDelta() &&
           format != null &&
           format.startsWith("p");
  }

  private boolean isDelta()
  {
    return interval > 0 && 
           delta;
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
