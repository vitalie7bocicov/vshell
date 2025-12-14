package util;

import model.Redirect;
import model.RedirectMode;
import model.RedirectType;

import java.nio.file.Path;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.Optional;

public class RedirectParser {

    public static Optional<Redirect> parse(List<String> tokens) {
        for (int i = tokens.size() - 1; i >= 0; i--) {
            String token = tokens.get(i);
            RedirectMode mode = null;
            RedirectType type = null;

            switch (token) {
                case "1>", ">" -> {
                    type = RedirectType.STDOUT;
                    mode = RedirectMode.TRUNCATE;
                }
                case ">>", "1>>" -> {
                    type = RedirectType.STDOUT;
                    mode = RedirectMode.APPEND;
                }
                case "2>" -> {
                    type = RedirectType.STDERR;
                    mode = RedirectMode.TRUNCATE;
                }
                case "2>>" -> {
                    type = RedirectType.STDERR;
                    mode = RedirectMode.APPEND;
                }
            }

            if (type != null) {
                if (i == tokens.size() - 1) {
                    throw new InvalidParameterException("syntax error near unexpected token `newline`");
                }
                return Optional.of(new Redirect(type, mode, i, Path.of(tokens.get(i + 1))));
            }
        }
        return Optional.empty();
    }
}
