package ch.rweiss.jmx.client.cli.list;

import java.lang.Thread.State;
import java.lang.management.ThreadInfo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.stream.LongStream;

import javax.management.openmbean.CompositeData;

import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.MBeanName;
import ch.rweiss.jmx.client.cli.AbstractJmxClientCommand;
import ch.rweiss.jmx.client.cli.Styles;
import ch.rweiss.terminal.Color;
import ch.rweiss.terminal.Style;
import ch.rweiss.terminal.StyledText;
import ch.rweiss.terminal.table.AbbreviateStyle;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;

@Command(name = "threads-states", description="Lists all treads stated")
public class ListThreadsStates extends AbstractJmxClientCommand
{
  private Map<Long, ThreadData> threads = new HashMap<>();
  private int valueCount = 0;

  private java.util.List<String> deadlockedThreadNames = new ArrayList<>();
  private static Table<ThreadData> threadStates = declareTable();

  private static final Style GREEN = Style.create().withColor(Color.BRIGHT_GREEN).toStyle();
  private static final Style YELLOW = Style.create().withColor(Color.BRIGHT_YELLOW).toStyle();
  private static final Style RED = Style.create().withColor(Color.BRIGHT_RED).toStyle();
  private static final int MAX_STATES = 80;
  
  ListThreadsStates()
  {
    super("Threads States");
  }

  @Override
  protected void execute()
  {
    MBean threadBean = jmxClient.bean(MBeanName.THREAD);
    threadBean.attribute("ThreadCpuTimeEnabled").value(true);
    threadBean.attribute("ThreadContentionMonitoringEnabled").value(true);
    long[] deadlockedThreads = (long[])threadBean.operation("findDeadlockedThreads").invoke(new Object[0]);
    deadlockedThreadNames.clear();
    CompositeData[] threadDump = (CompositeData[])threadBean.operation("dumpAllThreads", "boolean", "boolean").invoke(false, false);
    threadStates.clear();
    for (CompositeData thread : threadDump)
    {        
      ThreadInfo info = ThreadInfo.from(thread);
      ThreadData data = threads.computeIfAbsent(info.getThreadId(), id->new ThreadData(info));
      data.add(valueCount, info);
      if (isDeadlocked(deadlockedThreads, info))
      {
        data.isDeadLocked = true;
      }
      threadStates.addRow(data);
    }
    valueCount++;
    threadStates.printWithoutHeader();
  }
  
  @Override
  protected void afterRun()
  {
    super.afterRun();
    term.clear().screenToEnd();
  }

  private static Table<ThreadData> declareTable()
  {
    Table<ThreadData> table = new Table<>();
    table.addColumn(
        table.createColumn("Name", 40)
          .withTitleStyle(Styles.NAME_TITLE)
          .withStyledTextProvider(ListThreadsStates::threadName)
          .withAbbreviateStyle(AbbreviateStyle.LEFT_WITH_DOTS)
          .withMinWidth(10)
          .toColumn());
    
    table.addColumn(
        table.createColumn("State", 60, data -> data.states)
          .withTitleStyle(Styles.NAME_TITLE)
          .withStyledTextProvider(ListThreadsStates::threadStates)
          .withAbbreviateStyle(AbbreviateStyle.LEFT)
          .withMinWidth(10)
          .toColumn());
      return table;
  }
  
  private static StyledText threadName(ThreadData data)
  {
    if (data.isDeadLocked)
    {
      return new StyledText(data.name, Styles.ERROR);
    }
    return new StyledText(data.name, Styles.NAME);
  }
  
  private static StyledText threadStates(java.util.List<ThreadState> states)
  {
    StyledText text = new StyledText("");
    for (ThreadState state : states)
    {
      switch(state.state)
      {
        case RUNNABLE:
          text = text.append("\u25A0", GREEN);
          break;
        case WAITING:
        case TIMED_WAITING:
          text = text.append("\u25A0", YELLOW);
          break;
        case BLOCKED:
          text = text.append("\u25A0", RED);
          break;
        case NEW:
        case TERMINATED:
        default:
          text = text.append(" ", null);
          break;
      }
    }
    return text;
  }
  
  private static boolean isDeadlocked(long[] deadlockedThreads, ThreadInfo info)
  {
    return deadlockedThreads != null && LongStream.of(deadlockedThreads).filter(x->x==info.getThreadId()).findAny().isPresent();
  }
  

  private static class ThreadState
  {
    private Thread.State state;

    public ThreadState(State threadState)
    {
      this.state = threadState;
    }
  }
  
  private static class ThreadData
  {
    private final LinkedList<ThreadState> states = new LinkedList<>();
    private boolean isDeadLocked;
    private String name;
    
    private ThreadData(ThreadInfo info)
    {
      isDeadLocked = false;
      name = info.getThreadName();
    }    
    
    private void add(int valueCount, ThreadInfo info)
    {
      if (states.isEmpty() && valueCount > 0)
      {
        int count = Math.min(valueCount, MAX_STATES-1);
        while (states.size() < count)
        {
          states.addLast(new ThreadState(Thread.State.NEW));
        }
      }
      states.addLast(new ThreadState(info.getThreadState()));
      if (states.size() >= MAX_STATES)
      {
        states.removeFirst();
      }
    }    
  }
}
