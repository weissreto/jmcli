package ch.rweiss.jmcli.invoke;

public final class OperationTesterImpl implements OperationTesterMXBean
{
  @Override
  public String stringArg(String value)
  {
    return value+"1";
  }
  
  @Override
  public byte byteArg(byte value)
  {
    return (byte)(value+1);
  }
  
  @Override
  public short shortArg(short value)
  {
    return (short)(value+1);
  }

  @Override
  public int intArg(int value)
  {
    return value+1;
  }

  @Override
  public long longArg(long value)
  {
    return value+1;
  }

  @Override
  public float floatArg(float value)
  {
    return value+1.0f;
  }

  @Override
  public double doubleArg(double value)
  {
    return value+1.0d;
  }

  @Override
  public boolean booleanArg(boolean value)
  {
    return !value;
  }

  @Override
  public char charArg(char value)
  {
    return (char)(value+1);
  }
  
  @Override
  public int multipleArgs(int arg1, int arg2, int arg3)
  {
    return arg1 + arg2 + arg3;
  }
  
  @Override
  public int oneArg(int value)
  {
    return value;
  }
  
  @Override
  public String oneArg(String value)
  {
    return value;
  }
  
  @Override
  public int oneArg(int arg1, int arg2)
  {
    return arg1 + arg2;
  }
}