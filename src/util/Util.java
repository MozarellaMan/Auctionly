package util;

public class Util {
    public static final String RED = "\033[0;31m";
    public static final String RESET = "\033[0m";

    public static void warning(String input) {
        System.out.println(RED + input + RESET);
    }
}
