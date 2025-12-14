package model;

import java.nio.file.Path;

public record Redirect(
    RedirectType type,
    RedirectMode mode,
    int operatorIndex,
    Path path
) {}
