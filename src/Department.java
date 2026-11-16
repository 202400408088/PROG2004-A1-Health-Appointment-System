import java.util.Locale;

/**
 * A department of the clinic, for example General Practice or Cardiology.
 *
 * <p>Every health professional belongs to exactly one department, so the clinic
 * can group its staff and report the appointments of a single department. The
 * department is modelled as its own class rather than a {@code String} on the
 * professional, because a department has its own identity (a code that must be
 * unique), its own readable name, and a physical place in the building. Keeping
 * it in one object means the label and the location are written once and are
 * validated once.</p>
 *
 * <p>Two departments are the same department when they share a code, which is
 * what lets a directory reject a duplicate registration.</p>
 *
 * @author Jueding Lin
 * @version 1.0
 */
public class Department {

    /** Longest code the clinic uses, so the code stays readable on a report. */
    private static final int MAX_CODE_LENGTH = 6;

    private final String code;
    private final String name;
    private final String location;

    /**
     * Creates a department whose building location is not recorded yet.
     *
     * @param code short unique code, for example "CARD"
     * @param name the readable department name
     * @throws IllegalArgumentException if the code or name is unusable
     */
    public Department(String code, String name) {
        // Delegate to the full constructor so the location has one default.
        this(code, name, "Not recorded");
    }

    /**
     * Creates a department with a known location.
     *
     * @param code     short unique code, for example "CARD"
     * @param name     the readable department name
     * @param location where the department sits in the building
     * @throws IllegalArgumentException if the code or name is unusable
     */
    public Department(String code, String name, String location) {
        // Both required values are validated before anything is stored.
        this.code = validateCode(code);
        this.name = requireText(name, "A department name is required.");
        // The location is optional, so a blank value becomes a readable default.
        this.location = location == null || location.trim().isEmpty()
                ? "Not recorded"
                : location.trim();
    }

    /**
     * Validates the department code.
     *
     * @param code the code to check
     * @return the trimmed code in upper case
     * @throws IllegalArgumentException if the code is missing, too long or
     *                                  contains anything other than letters and digits
     */
    private static String validateCode(String code) {
        if (code == null) {
            throw new IllegalArgumentException("A department code is required.");
        }
        // Upper case is stored so "card" and "CARD" are the same department.
        String trimmedCode = code.trim().toUpperCase(Locale.ENGLISH);
        if (trimmedCode.isEmpty()) {
            throw new IllegalArgumentException("A department code cannot be blank.");
        }
        if (trimmedCode.length() > MAX_CODE_LENGTH) {
            throw new IllegalArgumentException(
                    "A department code cannot be longer than " + MAX_CODE_LENGTH + " characters.");
        }
        for (int index = 0; index < trimmedCode.length(); index++) {
            if (!Character.isLetterOrDigit(trimmedCode.charAt(index))) {
                throw new IllegalArgumentException(
                        "A department code must contain letters or digits only, but was: " + trimmedCode);
            }
        }
        return trimmedCode;
    }

    /**
     * @param value   the text to check
     * @param message the message to use if the text is unusable
     * @return the trimmed text
     * @throws IllegalArgumentException if the text is missing or blank
     */
    private static String requireText(String value, String message) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    /**
     * @return the clinic's general practice department
     */
    public static Department generalPractice() {
        return new Department("GENP", "General Practice", "Level 1, Clinic A");
    }

    /**
     * @return the clinic's cardiology department
     */
    public static Department cardiology() {
        return new Department("CARD", "Cardiology", "Level 3, Cardiac Suite");
    }

    /**
     * @return the clinic's surgical department
     */
    public static Department surgery() {
        return new Department("SURG", "Surgery", "Level 4, Theatres");
    }

    /**
     * @return the clinic's nutrition and dietetics department
     */
    public static Department dietetics() {
        return new Department("DIET", "Nutrition and Dietetics", "Level 2, Clinic C");
    }

    /**
     * @return the clinic's nursing department
     */
    public static Department nursing() {
        return new Department("NURS", "Nursing", "Ward block, Level 1");
    }

    /**
     * @return the short department code
     */
    public String getCode() {
        return code;
    }

    /**
     * @return the readable department name
     */
    public String getName() {
        return name;
    }

    /**
     * @return where the department sits in the building
     */
    public String getLocation() {
        return location;
    }

    /**
     * Two departments are equal when they carry the same code.
     *
     * @param other the object to compare with
     * @return {@code true} if both objects describe the same department code
     */
    @Override
    public boolean equals(Object other) {
        // Fast path: the same object is always equal to itself.
        if (this == other) {
            return true;
        }
        if (!(other instanceof Department)) {
            return false;
        }
        Department otherDepartment = (Department) other;
        // The code is the department's identity, not the name or the location.
        return code.equals(otherDepartment.code);
    }

    /**
     * @return a hash value consistent with {@link #equals(Object)}
     */
    @Override
    public int hashCode() {
        return code.hashCode();
    }

    /**
     * @return the code, the name and the location in one readable line
     */
    @Override
    public String toString() {
        return code + " - " + name + " (" + location + ")";
    }
}
