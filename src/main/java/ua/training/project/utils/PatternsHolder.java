package ua.training.project.utils;

public interface PatternsHolder {

    String NAME_PATTERN = "[A-Z][a-z]{1,24}|[А-ЯЁІЇ][а-яёії']{1,19}";

    String SURNAME_PATTERN = "[A-Z][a-z]{1,24}|[А-ЯЁІЇ][а-яёії']{1,24}";

    String USERNAME_PATTERN = "^[A-Za-z][A-Za-z0-9_-]{3,25}$";

    String PASSWORD_PATTERN = "^[A-Z][A-Za-z0-9_-]{5,25}$";

    Integer MINIMAL_AGE = 18;

    Integer MAX_AGE = 100;

}
