package ch.weiss.jmx.client.cli;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

public class WildcardFilters
{
  private List<Pattern> filterPatterns;

  public WildcardFilters(List<String> wildcardFilters)
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
    if (hasWildcard(wildcardFilter))
    {
      String regex = StringUtils.replace(wildcardFilter, "*", "\\w*");
      regex = StringUtils.replace(regex, "?", "\\w");
      return Pattern.compile(regex);
    }
    return Pattern.compile(Pattern.quote(wildcardFilter));
  }
    
  private static boolean hasWildcard(String attributeFilter)
  {
    return StringUtils.contains(attributeFilter, "*") || 
           StringUtils.contains(attributeFilter, "?");
  }
}
