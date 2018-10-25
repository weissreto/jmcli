package ch.rweiss.jmcli.list;

public class BlockedAndWaitingThreads
{
  public static void main(String[] args)
  {
    new Thread(BlockedAndWaitingThreads::thread, "Blocked and Waiting Thread 1").start();
    new Thread(BlockedAndWaitingThreads::thread, "Blocked and Waiting Thread 2").start();
  }
  
  private static void thread()
  {
    while(true)
    {
      execute();
    }
  }
  
  private static synchronized void execute() 
  {
    sleep(100);
  }

  private static void sleep(long millis)
  {
    try
    {
      Thread.sleep(millis);
    }
    catch (InterruptedException ex)
    {
      throw new RuntimeException(ex);
    }
  }
}
