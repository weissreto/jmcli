package ch.rweiss.jmcli;

import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class WildcardFilters
{
  private List<Pattern> filterPatterns;

  private WildcardFilters(List<String> wildcardFilters)
  {
    this.filterPatterns = toPatterns(wildcardFilters);
  }

  public boolean matches(String test)
  {
    if (filterPatterns.isEmpty())
    {
      return true;
    }
    for (Pattern attributePattern : filterPatterns)
    {
      if (attributePattern.matcher(test).matches())
      {
        return true;
      }
    }
    return false;
  }
  
  private static List<Pattern> toPatterns(List<String> wildcardFilters)
  {
    return wildcardFilters
          .stream()
          .map(WildcardFilters::toPattern)
          .collect(Collectors.toList());
  }

  private static Pattern toPattern(String wildcardFilter)
  {
    StringBuilder pattern = new StringBuilder();
    StringTokenizer tokenizer = new StringTokenizer(wildcardFilter, "*?", true);
    while (tokenizer.hasMoreTokens())
    {
      String token = tokenizer.nextToken();
      if (token.equals("*"))
      {
        pattern.append(".*");
      }
      else if (token.equals("."))
      {
        pattern.append("\\w");
      }
      else 
      {
        pattern.append(Pattern.quote(token));
      }      
    }
    return Pattern.compile(pattern.toString());
  }
    
  public static WildcardFilters createForFilters(List<String> filtersWithWildcards)
  {
    return new WildcardFilters(filtersWithWildcards);
  }

  public static WildcardFilters createForPrefixes(List<String> prefixesWithWildcards)
  {
    return new WildcardFilters(ensureStarAtEnd(prefixesWithWildcards));
  }
  
  private static List<String> ensureStarAtEnd(List<String> prefixes)
  {
    return prefixes
        .stream()
        .map(WildcardFilters::ensureStarAtEnd)
        .collect(Collectors.toList());
  }
  
  private static String ensureStarAtEnd(String prefix)
  {
    if (StringUtils.endsWith(prefix, "*"))
    {
      return prefix;
    }
    return prefix+"*"; 
  }
}
