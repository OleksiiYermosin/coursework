package ua.training.project.utils;

public interface PatternsHolder {

    String NAME_PATTERN = "[A-Z][a-z]{1,24}|[А-ЯЁІЇ][а-яёії']{1,19}";

    String SURNAME_PATTERN = "[A-Z][a-z]{1,24}|[А-ЯЁІЇ][а-яёії']{1,24}";

    String USERNAME_PATTERN = "^[A-Za-z][A-Za-z0-9_-]{3,25}$";

    String MINIMAL_AGE = "18";

}
