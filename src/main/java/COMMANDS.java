public enum COMMANDS {
    ECHO,
    EXIT,
    TYPE,
    EXTERNAL;

    public static COMMANDS fromString(String text) throws IllegalAccessException {
        for (COMMANDS cmd : COMMANDS.values()) {
            if (cmd.name().equalsIgnoreCase(text)) {
                return cmd;
            }
        }
        return EXTERNAL;
    }

    public static boolean isValid(String text) {
        for (COMMANDS cmd : COMMANDS.values()) {
            if (cmd.name().equalsIgnoreCase(text)) {
                return true;
            }
        }
        return false;
    }
}
