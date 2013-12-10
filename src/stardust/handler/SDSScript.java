package stardust.handler;

import java.util.Arrays;

/*
 * A Fast SSI Parser
 */
public class SDSScript
{
static final String _TAG_BEGIN = "<!--#";
static final String _TAG_END   = " -->";
    
private static final short         S = 0;
private static final short      TEXT = 1;
private static final short TAG_BEGIN = 2;
private static final short  TAG_NAME = 3;
private static final short   TAG_END = 4;

private static final String[] stateName = {"S","TEXT","TAG_BEGIN","TAG_NAME","TAG_END"};
        
private static final short[][] states = {
                /* S         => */{TEXT,TAG_BEGIN},
                /* TEXT      => */{TAG_BEGIN},
                /* TAG_BEGIN => */{TAG_NAME},
                /* TAG_NAME  => */{TAG_END},
                /* TAG_END   => */{TEXT,TAG_BEGIN}
                };
                
private static  boolean acceptsNextState(int currentState, int nextState) {
    for (short state : states[currentState]) if (state == nextState) return true;
    return false;
}

private static  boolean conformsSet(short toState,int a,int pos) {
    
    switch(toState) {        
        case TEXT       : return true;
        case TAG_BEGIN  : return pos < _TAG_BEGIN.length() &&  _TAG_BEGIN.charAt(pos) == (char)a;
        case TAG_NAME   : return (a >= 'a' && a <= 'z') || a == '_';
        case TAG_END    : return pos < _TAG_END.length() && _TAG_END.charAt(pos) == (char)a;        
    }
    return false;
}

private static boolean isFinal(short toState,int a, int pos) {
    switch(toState) {
        case TEXT       : return a != '<';
        case TAG_BEGIN  : return pos == _TAG_BEGIN.length();
        case TAG_NAME   : return a != 'a';
        case TAG_END    : return pos == _TAG_END.length();
    }
    return false;
}

public  void stateChanged(int previous, int current, String[] data) {
    if (current == TAG_END) {
//        System.out.println("\n( "+stateName[previous]+"->"+stateName[current]+" )\n\nNAME: "+data[TAG_NAME]);        
    }
}

private String out="";
public void step(short current,char c, short[] conflits,String[] buffer,String scriptname, int pos, int length) {
    if ((current == TEXT && conflits[TAG_BEGIN] == 0)) {
        System.out.print(c);
    }
}

public  void run (String code) {
   short smatch[] = {0,0,0,0,0};
   String hold[] = {"","","","",""};
           
   short cs = TEXT;

    for (int i = 0 ; i < code.length() ; i++ ) {
        char a = code.charAt(i);
//        System.out.print(" "+a+" ("+stateName[cs]+") ");
        for (short c = 0 ; c< states.length ; c++) {            
            if (acceptsNextState(cs,c)) {
                if (conformsSet(c,(int)a,smatch[c])) {
                    ++smatch[c];
                    hold[c]+=a;
                } else {
                    smatch[c] = 0;
                    hold[c]="";
                }
//                System.out.print("  "+stateName[c] + "="+smatch[c]);
                if (isFinal(c,(int)a,smatch[c])) {
//                    System.out.print("[>] "+hold[c]);
                    if (hold[c].length()>0) {
                        hold[cs] = hold[cs].substring(0, hold[cs].length()-hold[c].length()+1);
                    }
                    stateChanged(cs,c,hold);
                    cs=c;
                    hold[cs] += a;                    
                    Arrays.fill(smatch, (short)0);
                    Arrays.fill(hold,"");
                    break;                    
                }
            } else if (c == cs) {
                hold[cs] += a; 
            }
        }
        step(cs,a,smatch,hold,"<native>",i,code.length());        
//        System.out.println();
    }
}

}