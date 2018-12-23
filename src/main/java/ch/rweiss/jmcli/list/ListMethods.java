package ch.rweiss.jmcli.list;

import java.lang.Thread.State;
import java.lang.management.ThreadInfo;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

import javax.management.openmbean.CompositeData;

import ch.rweiss.jmcli.AbstractCommand;
import ch.rweiss.jmcli.IntervalOption;
import ch.rweiss.jmcli.JvmOption;
import ch.rweiss.jmcli.SortColumnOption;
import ch.rweiss.jmcli.SortColumnOption.Direction;
import ch.rweiss.jmcli.Styles;
import ch.rweiss.jmcli.executor.AbstractJmxDataExecutor;
import ch.rweiss.jmcli.ui.CommandUi;
import ch.rweiss.jmx.client.JmxClient;
import ch.rweiss.jmx.client.MBean;
import ch.rweiss.jmx.client.MBeanName;
import ch.rweiss.terminal.Key;
import ch.rweiss.terminal.StyledText;
import ch.rweiss.terminal.table.AbbreviateStyle;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;

public class ListMethods extends AbstractJmxDataExecutor
{
  private static final Comparator<MethodData> RUNNABLE_DESC = Comparator
      .comparingInt(MethodData::runnableSelfSamples)
      .thenComparingInt(MethodData::waitingSelfSamples)
      .thenComparingInt(MethodData::blockedSelfSamples);
  private static final String SELF_SAMPLES_COLUMN = "Self";

  @Command(name = "methods", description="Lists all executed methods")
  public static class Cmd extends AbstractCommand
  {
    @Mixin
    private IntervalOption intervalOption = new IntervalOption(1);
    
    @Mixin
    private JvmOption jvmOption = new JvmOption();
    
    @Mixin
    private SortColumnOption sortOption = new SortColumnOption(SELF_SAMPLES_COLUMN, Direction.DESCENDING);
    
    @Override
    public void run()
    {
      new ListMethods(this).execute();
    }
  }

  private Map<String, MethodData> methods = new HashMap<>();
  private static Table<MethodData> table = declareTable();
  
  public ListMethods(Cmd command)
  {
    super("Methods", command.intervalOption, command.jvmOption);
    command.sortOption.sort(table);
  }

  @Override
  protected void gatherDataFrom(JmxClient jmxClient)
  {
    MBean threadBean = jmxClient.bean(MBeanName.THREAD);
    long[] threadIds = (long[])threadBean.attribute("AllThreadIds").value();
    CompositeData[] threads = (CompositeData[])threadBean.operation("getThreadInfo", "long[]", "int").invoke(threadIds, 500);
    for (CompositeData thread : threads)
    {      
      analyzeThread(thread);
    }
    table.setRows(methods.values());
    triggerUiUpdate();
  }

  private void analyzeThread(CompositeData thread)
  {
    if (thread == null)
    {
      return;
    }
    ThreadInfo info = ThreadInfo.from(thread);
    StackTraceElement[] stackTrace = info.getStackTrace();
    if (stackTrace.length > 0)
    {
      StackTraceElement topFrame = stackTrace[0];
      for (StackTraceElement element : stackTrace)
      {
        String methodName = element.getClassName()+"."+element.getMethodName();
        MethodData methodInfo = methods.computeIfAbsent(methodName, MethodData::new);
        methodInfo.sample(element==topFrame, info.getThreadState());
      }
    }
  }
  
  @Override
  protected void writeDataToUi(CommandUi ui, boolean isPeriodical)
  {
    if (isPeriodical)
    {
      table.printTop();
    }
    else
    {
      table.print();
    }
  }
  
  @Override
  protected void keyPressed(Key key)
  {
    boolean processed = table.keyPressed(key);
    if (processed)
    {
      triggerUiUpdate();
    }
  }

  private static Table<MethodData> declareTable()
  {
    Table<MethodData> methodTable = new Table<>();
    methodTable.addColumn(
        methodTable.createColumn("Name", 30)
          .withTitleStyle(Styles.NAME_TITLE)
          .withTextProvider(MethodData::name)
          .withAbbreviateStyle(AbbreviateStyle.LEFT_WITH_DOTS)
          .withMinWidth(10)
          .toColumn());
    
    methodTable.addColumn(
        methodTable.createColumn(SELF_SAMPLES_COLUMN, 10)
          .withTitleStyle(Styles.NAME_TITLE)
          .withStyledTextProvider(MethodData::runnableSelfSamplesText)
          .withSorter(RUNNABLE_DESC)
          .toColumn());

    methodTable.addColumn(
        methodTable.createColumn("Waiting", 10)
          .withTitleStyle(Styles.NAME_TITLE)
          .withStyledTextProvider(MethodData::waitingSelfSamplesText)
          .withSorter(Comparator.comparingInt(MethodData::waitingSelfSamples))
          .toColumn());

    methodTable.addColumn(
        methodTable.createColumn("Blocked", 10)
          .withTitleStyle(Styles.NAME_TITLE)
          .withStyledTextProvider(MethodData::blockedSelfSamplesText)
          .withSorter(Comparator.comparingInt(MethodData::blockedSelfSamples))
          .toColumn());

    methodTable.addColumn(
        methodTable.createColumn("Total", 10)
          .withTitleStyle(Styles.NAME_TITLE)
          .withTextProvider(MethodData::totalSamplesStr)
          .withCellStyle(Styles.VALUE)
          .withSorter(Comparator.comparingInt(MethodData::totalSamples))
          .toColumn());
    return methodTable;
  }

  private static final class MethodData
  {
    private final String name;
    private int totalSamples;
    private int runnableSelfSamples;
    private int waitingSelfSamples;
    private int blockedSelfSamples;
    
    private MethodData(String name)
    {
      this.name = name;
    }
    
    private void sample(boolean isTopOfStack, State state)
    {
      totalSamples++;
      if (isTopOfStack)
      {
        switch(state)
        {
          case RUNNABLE:
            runnableSelfSamples++;
            break;
          case WAITING:
          case TIMED_WAITING:
            waitingSelfSamples++;
            break;
          case BLOCKED:
            blockedSelfSamples++;
            break;
          default:
            break;
        }
      }
    }
    
    private String name()
    {
      return name;
    }
    
    private int totalSamples()
    {
      return totalSamples;
    }
    
    private String totalSamplesStr()
    {
      return Integer.toString(totalSamples);
    }
    
    private int runnableSelfSamples()
    {
      return runnableSelfSamples;
    }
    
    private StyledText runnableSelfSamplesText()
    {
      return new StyledText(Integer.toString(runnableSelfSamples), ListThreadsStates.GREEN);
    }

    private int waitingSelfSamples()
    {
      return waitingSelfSamples;
    }
    
    private StyledText waitingSelfSamplesText()
    {
      return new StyledText(Integer.toString(waitingSelfSamples), ListThreadsStates.YELLOW);
    }

    private int blockedSelfSamples()
    {
      return blockedSelfSamples;
    }
    
    private StyledText blockedSelfSamplesText()
    {
      return new StyledText(Integer.toString(blockedSelfSamples), ListThreadsStates.RED);
    }

  }    
}
