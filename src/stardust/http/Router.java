package stardust.http;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Router
{
  private Map<String, Class> routes = Collections.synchronizedMap(new LinkedHashMap());
  private Map<String, String> rules = Collections.synchronizedMap(new LinkedHashMap());

  public void addRule(String matchRegex, String replaceString) {
    this.rules.put(matchRegex, replaceString);
  }

  public Map<String, Class> getRoutes() {
    return this.routes;
  }

  public Map<String, String> getRules() {
    return this.rules;
  }

  public String applyRules(String request) {
    for (String rg : this.rules.keySet()) {
      if (request.matches(rg)) {
        Matcher m = Pattern.compile(rg).matcher(request);
        String result = m.replaceAll((String)this.rules.get(rg));

        return result;
      }
    }
    return request;
  }

  public void addRoute(String pathRegex, Class rh) {
    if (Handler.class.isAssignableFrom(rh))
      this.routes.put(pathRegex, rh);
  }

  public Class getHandler(String request)
  {
    try {
      String r = applyRules(request);
      for (String rg : this.routes.keySet())
        if (request.matches(rg))
          return (Class)this.routes.get(rg);
    }
    catch (Exception ex) {
    }
    return null;
  }
}