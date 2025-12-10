
package com.github.tasnim2001.tasnimintelliji.keywords;

public class KeywordMatch {

    public final String main;
    public final String sub;
    public final String pattern;
    public final String hit;
    public final int start;
    public final int end;
    public final int line ;

    public KeywordMatch(String main, String sub, String pattern, String hit, int start, int end, int line ) {
        this.main = main;
        this.sub = sub;
        this.pattern = pattern;
        this.hit = hit;
        this.start = start;
        this.end = end;
        this.line = line;
    }
}
