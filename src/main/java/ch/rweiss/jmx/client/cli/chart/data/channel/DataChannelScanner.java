package ch.rweiss.jmx.client.cli.chart.data.channel;

import java.util.HashSet;
import java.util.Set;

import ch.rweiss.check.Check;

public class DataChannelScanner
{
  private Set<ScannableDataChannel> dataChannels = new HashSet<>();
  
  public void add(DataChannel dataChannel)
  {
    Check.parameter("dataChannel").withValue(dataChannel).isNotNull();
    if (dataChannel instanceof ScannableDataChannel)
    {
      dataChannels.add((ScannableDataChannel) dataChannel);
    }
    else 
    {
      FunctionDataChannel functionDataChannel = (FunctionDataChannel)dataChannel;
      dataChannels.add(functionDataChannel.scannableDataChannel());
    }
  }
    
  public void scanNow()
  {
    dataChannels.forEach(dataChannel -> dataChannel.scan());
  }
}
  
