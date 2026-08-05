package jjs.djed.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class Patterns {
    private Patterns() {}

    public final static Pattern SKILL_NAME_PATTERN = Pattern.compile(
              "^(?!\\s)(?!.*\\s{2,})" +
                    "[\\p{L}\\p{N} .,'’\\-+&()/#!?:" +
                    "\\x{1F300}-\\x{1FAFF}\\x{2600}-\\x{27BF}\\x{2190}-\\x{21FF}\\x{FE0F}\\x{200D}]{1,60}" +
                    "(?<!\\s)$"
    );

    public static final Pattern SKILL_DESCRIPTION_PATTERN = Pattern.compile(
            "^(?!\\s)(?!.*\\n{3,})(?!.*[ \\t]{3,})" +
                    "[\\p{L}\\p{N}\\s.,'’\"“”\\-+&()/#!?:;%@*=" +
                    "\\x{1F300}-\\x{1FAFF}\\x{2600}-\\x{27BF}\\x{2190}-\\x{21FF}\\x{FE0F}\\x{200D}]{1,1000}" +
                    "(?<!\\s)$",
            Pattern.DOTALL
    );

    public static boolean isValid(Pattern p, String s) {
        Matcher matcher = p.matcher(s);
        return matcher.find();
    }
}
