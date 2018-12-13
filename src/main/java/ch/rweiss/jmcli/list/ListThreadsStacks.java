package ch.rweiss.jmcli.list;

import java.lang.Thread.State;
import java.lang.management.ThreadInfo;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import javax.management.openmbean.CompositeData;

import ch.rweiss.jmcli.AbstractCommand;
import ch.rweiss.jmcli.IntervalOption;
import ch.rweiss.jmcli.JvmOption;
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

public class ListThreadsStacks extends AbstractJmxDataExecutor
{
  private static final String SELF_SAMPLES_COLUMN = "Runnable";

  @Command(name = "threads-stacks", description="Lists all thread-stackes")
  public static class Cmd extends AbstractCommand
  {
    @Mixin
    private IntervalOption intervalOption = new IntervalOption(1);
    
    @Mixin
    private JvmOption jvmOption = new JvmOption();
    
    @Override
    public void run()
    {
      new ListThreadsStacks(this).execute();
    }
  }

  private StackElement topStackElement = new StackElement("", -1);
  private static Table<StackElement> table = declareTable();
  
  public ListThreadsStacks(Cmd command)
  {
    super("Threads-Stacks", command.intervalOption, command.jvmOption);
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
    java.util.List<StackElement> elements = new ArrayList<>();
    topStackElement.addChildrenRecursive(elements);
    table.setRows(elements);
    triggerUiUpdate();
  }

  private void analyzeThread(CompositeData thread)
  {
    if (thread == null)
    {
      return;
    }
    ThreadInfo info = ThreadInfo.from(thread);
    StackElement parentElement = topStackElement.sampleChild(info.getThreadName(), info.getThreadState());
    StackTraceElement[] stackTrace = info.getStackTrace();
    if (stackTrace.length > 0)
    {
      for (int pos = stackTrace.length-1; pos >= 0; pos--)
      {
        StackTraceElement element = stackTrace[pos];
        String methodName = element.getClassName()+"."+element.getMethodName();
        parentElement = parentElement.sampleChild(methodName, info.getThreadState());
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

  private static Table<StackElement> declareTable()
  {
    Table<StackElement> methodTable = new Table<>();
    methodTable.addColumn(
        methodTable.createColumn("Name", 30)
          .withTitleStyle(Styles.NAME_TITLE)
          .withTextProvider(StackElement::displayName)
          .withAbbreviateStyle(AbbreviateStyle.RIGHT_WITH_DOTS)
          .withMinWidth(10)
          .toColumn());
        
    methodTable.addColumn(
        methodTable.createColumn(SELF_SAMPLES_COLUMN, 10)
          .withTitleStyle(Styles.NAME_TITLE)
          .withStyledTextProvider(StackElement::runnableSamplesText)
          .toColumn());

    methodTable.addColumn(
        methodTable.createColumn("Waiting", 10)
          .withTitleStyle(Styles.NAME_TITLE)
          .withStyledTextProvider(StackElement::waitingSamplesText)
          .toColumn());

    methodTable.addColumn(
        methodTable.createColumn("Blocked", 10)
          .withTitleStyle(Styles.NAME_TITLE)
          .withStyledTextProvider(StackElement::blockedSamplesText)
          .toColumn());

    return methodTable;
  }

  private static final class StackElement
  {
    private static final Comparator<StackElement> SAMPLES_DESCENDING = Comparator
        .comparingInt(StackElement::runnableSamples)
        .thenComparingInt(StackElement::waitingSamples)
        .thenComparingInt(StackElement::blockedSamples)
        .reversed();
    private final String name;
    private int runnableSamples;
    private int waitingSamples;
    private int blockedSamples;
    private final int depth;
    private final Map<String, StackElement> children = new HashMap<>();
    
    private StackElement(String name, int depth)
    {
      this.name = name;
      this.depth = depth;
    }
    
    public StackElement sampleChild(String childName, State state)
    {
      StackElement child = children.computeIfAbsent(childName, cName -> new StackElement(cName, depth+1));
      child.sample(state);
      return child;
    }

    private void sample(State state)
    {
      switch(state)
      {
        case RUNNABLE:
          runnableSamples++;
          break;
        case WAITING:
        case TIMED_WAITING:
          waitingSamples++;
          break;
        case BLOCKED:
          blockedSamples++;
          break;
        default:
          break;
      }
    }

    public void addChildrenRecursive(List<StackElement> elements)
    {
      List<StackElement> sortedChildren = new ArrayList<>(children.values());
      Collections.sort(sortedChildren, SAMPLES_DESCENDING);
      for (StackElement element : sortedChildren)
      {
        elements.add(element);
        element.addChildrenRecursive(elements);
      }
    }
        
    private String displayName()
    {
      StringBuilder builder = new StringBuilder();
      IntStream.range(0, depth).forEach(pos -> builder.append(' '));
      builder.append(name);
      return builder.toString();
    }
    
    private int runnableSamples()
    {
      return runnableSamples;
    }
    
    private StyledText runnableSamplesText()
    {
      return new StyledText(Integer.toString(runnableSamples), ListThreadsStates.GREEN);
    }

    private int waitingSamples()
    {
      return waitingSamples;
    }

    private StyledText waitingSamplesText()
    {
      return new StyledText(Integer.toString(waitingSamples), ListThreadsStates.YELLOW);
    }

    private int blockedSamples()
    {
      return blockedSamples;
    }

    private StyledText blockedSamplesText()
    {
      return new StyledText(Integer.toString(blockedSamples), ListThreadsStates.RED);
    }
  }    
}
