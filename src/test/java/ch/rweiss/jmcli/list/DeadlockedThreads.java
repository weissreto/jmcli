package ch.rweiss.jmcli.list;

public class DeadlockedThreads
{
  private static Object lock1 = new Object();
  private static Object lock2 = new Object();

  public static void main(String[] args)
  {
    new Thread(DeadlockedThreads::thread1, "Deadlock Thread 1").start();
    new Thread(DeadlockedThreads::thread2, "Deadlock Thread 2").start();
  }
  
  private static void thread1()
  {
    synchronized(lock1)
    {
      sleep(500);
      synchronized(lock2)
      {
        // 
      }
    }
  }

  private static void thread2() 
  {
    synchronized (lock2)
    {
      sleep(500);
      synchronized(lock1)
      {
        //
      }
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
