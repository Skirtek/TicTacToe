package sample.utilities;

import com.sun.deploy.util.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {

    public static boolean isNickNameValid(String nickname){
        if(isNullOrWhitespace(nickname)){
            return false;
        }
        Pattern compiledPattern = Pattern.compile("^[A-Za-z]\\S*");
        Matcher matcher = compiledPattern.matcher(nickname);
        return matcher.matches() && nickname.length() < 36;
    }

    public static boolean isNullOrWhitespace(String value) {
        return value == null || StringUtils.trimWhitespace(value).length() == 0;
    }
}
