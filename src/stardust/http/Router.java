package stardust.http;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author joao
 */
public class Router {
    public static final String DEFAULT_HOSTNAME = "*";    
    private String hostName;

    private Map<String,Class> routes = Collections.synchronizedMap(new LinkedHashMap<String,Class>());
    private Map<String,String> rules = Collections.synchronizedMap(new LinkedHashMap<String,String>());

    public Router(String virtualHostName) {
        this.hostName = virtualHostName;
    }

    public String getHostName() {
        return this.hostName;
    }
    
    public void addRule(String matchRegex,String replaceString){
        rules.put(matchRegex, replaceString);
    }

    public Map<String,Class> getRoutes() {
        return routes;
    }

    public Map<String,String> getRules() {
        return rules;
    }

    public String applyRules(String request) {
        for (String rg : rules.keySet()) {
            if (request.matches(rg)) {
                Matcher m = Pattern.compile(rg).matcher(request);
                String result = m.replaceAll(rules.get(rg));

                return result;
            }
        }
        return request;
    }

    public void addRoute(String pathRegex, Class rh){        
        if (Handler.class.isAssignableFrom(rh)) {
            routes.put(pathRegex, rh);
        }
    }

    public Class getHandler(String request) {
        try {
            String r = applyRules(request);
            for (String rg : routes.keySet()) {
                if (request.matches(rg)) {
                    return routes.get(rg);
                }
            }
        } catch (Exception ex){}
        return null;
    }

}