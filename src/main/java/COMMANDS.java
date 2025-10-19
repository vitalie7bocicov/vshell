public enum COMMANDS {
    ECHO,
    EXIT,
    TYPE;

    public static COMMANDS fromString(String text) throws IllegalAccessException {
        for (COMMANDS cmd : COMMANDS.values()) {
            if (cmd.name().equalsIgnoreCase(text)) {
                return cmd;
            }
        }
        throw new IllegalAccessException("No command with name " + text + " found");
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
