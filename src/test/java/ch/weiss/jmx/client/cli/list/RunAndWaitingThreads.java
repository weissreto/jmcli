package ch.weiss.jmx.client.cli.list;

public class RunAndWaitingThreads
{
  public static void main(String[] args)
  {
    new Thread(RunAndWaitingThreads::thread, "Run and Waiting Thread").start();
  }
  
  private static void thread()
  {
    while(true)
    {
      execute(100);
      sleep(100);
    }
  }
  
  private static synchronized void execute(long millis) 
  {
    long start = System.currentTimeMillis();
    while (System.currentTimeMillis()-start < millis)
    {
      //
    }
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
