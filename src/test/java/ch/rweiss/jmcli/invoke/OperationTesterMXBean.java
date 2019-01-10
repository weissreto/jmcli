package ch.rweiss.jmcli.invoke;

public interface OperationTesterMXBean
{
  public String stringArg(String value);
  public byte byteArg(byte value);
  public short shortArg(short value);
  public int intArg(int value);
  public long longArg(long value);
  public float floatArg(float value);
  public double doubleArg(double value);
  public boolean booleanArg(boolean value);
  public char charArg(char value);
  public int multipleArgs(int arg1, int arg2, int arg3);
  
  public int oneArg(int value);
  public String oneArg(String value);
  public int oneArg(int arg1, int arg2);
}