package ch.rweiss.jmcli.executor;

import java.util.Optional;

import ch.rweiss.jmcli.IntervalOption;
import ch.rweiss.jmcli.ui.CommandUi;
import ch.rweiss.terminal.Key;

public abstract class AbstractDataExecutor extends AbstractExecutor
{
  private IntervalOption intervalOption; 

  private boolean quit;
  private boolean mustUpdateUi;
  
  public AbstractDataExecutor(String name, IntervalOption intervalOption)
  {
    super(name);
    this.intervalOption = intervalOption;
  }

  public void execute()
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
      restore();
    }
  }

  private void init()
  {
    ui().terminal().cursor().hide();
    if (intervalOption.isPeriodical())
    {
      ui().terminal().clear().screen();
    }
  }

  private void restore()
  {
    ui().terminal().cursor().show();
    ui().terminal().reset();
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
    Optional<Key> key = ui().terminal().input().waitForKey(maximumTimeToProcessKey);
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
      ui().terminal().cursor().position(1, 1);
    }
    printHeader();
    writeDataToUi(ui(), isPeriodical);
    if (isPeriodical)
    {
      ui().terminal().clear().screenToEnd();
    }
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
  protected abstract void writeDataToUi(CommandUi ui, boolean isPeriodical);

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
}
