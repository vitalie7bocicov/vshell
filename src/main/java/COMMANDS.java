public enum COMMANDS {
    ECHO,
    EXIT,
    TYPE,
    PWD,
    CD,
    EXTERNAL;

    public static COMMANDS fromString(String text) {
        for (COMMANDS cmd : COMMANDS.values()) {
            if (cmd.name().equalsIgnoreCase(text)) {
                return cmd;
            }
        }
        return EXTERNAL;
    }
}
