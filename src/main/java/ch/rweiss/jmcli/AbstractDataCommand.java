package ch.rweiss.jmcli;

import java.util.Optional;

import ch.rweiss.terminal.AnsiTerminal;
import ch.rweiss.terminal.Key;
import ch.rweiss.terminal.table.AbbreviateStyle;
import ch.rweiss.terminal.table.Column;
import ch.rweiss.terminal.table.Table;
import picocli.CommandLine.Mixin;

public abstract class AbstractDataCommand extends AbstractCommand
{
  @Mixin
  private IntervalOption intervalOption = new IntervalOption();

  private String name;

  private boolean quit;
  private boolean mustUpdateUi;

  private AnsiTerminal term = AnsiTerminal.get();
  private static Table<String> header = declareHeaderTable();
  private static Table<String> subTitle = declareSubTitleTable();
  
  public AbstractDataCommand(String name)
  {
    this.name = name;
  }

  @Override
  public void run()
  {
    try
    {
      init();
      while (notQuit())
      {
        pumpEvent();
      }
    }
    finally
    {
      restoreTerminal();
    }
  }

  private void init()
  {
    initTerminal();
    init(term);
  }

  /**
   * Init 
   * @param terminal
   */
  protected void init(AnsiTerminal terminal)
  {
    // Does nothing by default
  }

  private void initTerminal()
  {
    term.cursor().hide();
    if (intervalOption.isPeriodical())
    {
      term.clear().screen();
    }
  }

  private void restoreTerminal()
  {
    term.cursor().show();
  }

  private boolean notQuit()
  {
    return !quit;
  }

  protected void quit()
  {
    quit = true;
  }

  private void pumpEvent()
  {
    long timeUntilNextDataGathering = intervalOption.computeTimeUntilNextDataGathering();
    if (timeUntilNextDataGathering > 0)
    {
      processKeyUntil(timeUntilNextDataGathering);
    }
    else
    {
      doGatherData();
    }
    if (mustUpdateUi)
    {
      updateUi();
    }
    if (intervalOption.isNotPeriodical())
    {
      quit();
    }
  }

  void processKeyUntil(long maximumTimeToProcessKey)
  {
    Optional<Key> key = term.input().waitForKey(maximumTimeToProcessKey);
    key.ifPresent(this::keyPressed);
    key.ifPresent(this::isQuitKey);
  }

  private void doGatherData()
  {
    gatherData();
    intervalOption.dataWasGathered();
  }

  private void updateUi()
  {
    mustUpdateUi = false;
    boolean isPeriodical = intervalOption.isPeriodical();
    if (isPeriodical)
    {
      term.cursor().position(1, 1);
    }
    writeHeader();
    writeDataToUi(term, isPeriodical);
    if (isPeriodical)
    {
      term.clear().screenToEnd();
    }
  }

  protected void writeHeader()
  {
    printEmptyLine();
    header.printSingleRow(name);
    printEmptyLine();
  }

  /**
   * Override this method to gather data once or periodically depending on option -i --interval
   * Call {@link #triggerUiUpdate()} if ui should be updated  
   */
  protected void gatherData()
  {
    // does nothing by default
  }  

  /**
   * Write the data gathered by method {@link #gatherData()} to the terminal
   */
  protected abstract void writeDataToUi(AnsiTerminal terminal, boolean isPeriodical);

  /**
   * Override this method if you are interested in key strokes.
   * Call {@link #triggerUiUpdate()} if ui should be updated 
   * @param key the key that was stroked
   */
  protected void keyPressed(Key key)
  {
    // does nothing by default
  }

  private void isQuitKey(Key key)
  {
    if (key.toString().equals("q"))
    {
      quit = true;
    }
  }

  protected void triggerUiUpdate()
  {
    mustUpdateUi = true;
  }
  
  protected void printEmptyLine()
  {
    term.clear().lineToEnd();
    term.newLine();
  }
  
  protected void printSubTitle(String title)
  {
    subTitle.printSingleRow(title);
  }


  private static Table<String> declareHeaderTable()
  {
    Table<String> table = new Table<>();
    Column<String, String> column = table
        .createColumn("", 40, title -> title)
        .withAbbreviateStyle(AbbreviateStyle.RIGHT)
        .withCellStyle(Styles.TITLE)
        .withMinWidth(8)
        .toColumn();
    table.addColumn(column);
    return table;
  }
  
  private static Table<String> declareSubTitleTable()
  {
    Table<String> table = new Table<>();
    Column<String, String> column = table
        .createColumn("Title", 20, title -> title)
        .withAbbreviateStyle(AbbreviateStyle.RIGHT)
        .withCellStyle(Styles.SUB_TITLE)
        .withMinWidth(8)
        .toColumn();
    table.addColumn(column);
    return table;
  }
}
