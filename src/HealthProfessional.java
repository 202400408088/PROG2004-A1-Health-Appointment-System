import java.time.DayOfWeek;
import java.time.format.TextStyle;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Common base class for every health professional the clinic employs.
 *
 * <p>The class stores only the information that every professional shares, no
 * matter what their specialty is: a numeric identifier, a name, and the fixed
 * set of days they work each week. The specialised types in Part 2 extend this
 * class instead of repeating these fields.</p>
 *
 * <p>The class is abstract because a "plain" health professional with no
 * specialty has no meaning in this system. Declaring it abstract stops
 * {@code new HealthProfessional(...)} from compiling and forces every concrete
 * subclass to say what kind of professional it is and what it actually does.</p>
 *
 * @author Jueding Lin
 * @version 1.0
 */
public abstract class HealthProfessional {

    /** Longest identifier the clinic issues, so the value stays readable on reports. */
    private static final int MAX_ID_LENGTH = 8;

    private final String id;
    private final String name;
    private final List<DayOfWeek> availableDays;

    /**
     * Creates a health professional with an explicitly supplied working pattern.
     *
     * @param id            identifier made of digits only
     * @param name          the professional's name
     * @param availableDays the days of the week the professional works
     * @throws IllegalArgumentException if any argument is missing or invalid
     */
    public HealthProfessional(String id, String name, List<DayOfWeek> availableDays) {
        // Every field is validated before it is stored, so a broken object cannot exist.
        this.id = validateId(id);
        this.name = validateName(name);
        this.availableDays = copyDays(availableDays);
    }

    /**
     * Creates a health professional who works the clinic's default pattern,
     * Monday to Friday. The working pattern is the one attribute that has a
     * sensible default, so a second constructor is worthwhile here.
     *
     * @param id   identifier made of digits only
     * @param name the professional's name
     * @throws IllegalArgumentException if the identifier or name is invalid
     */
    public HealthProfessional(String id, String name) {
        // Delegate to the full constructor and fill in the clinic's default pattern.
        this(id, name, defaultAvailability());
    }

    /**
     * Validates a professional identifier.
     *
     * @param id the identifier to check
     * @return the trimmed identifier
     * @throws IllegalArgumentException if the identifier is missing, too long or
     *                                  contains anything other than digits
     */
    private static String validateId(String id) {
        if (id == null) {
            throw new IllegalArgumentException("A health professional ID is required.");
        }
        // Work on a trimmed copy so surrounding spaces are not counted as characters.
        String trimmedId = id.trim();
        if (trimmedId.isEmpty()) {
            throw new IllegalArgumentException("A health professional ID cannot be blank.");
        }
        if (trimmedId.length() > MAX_ID_LENGTH) {
            throw new IllegalArgumentException(
                    "A health professional ID cannot be longer than " + MAX_ID_LENGTH + " digits.");
        }
        // Digits only: the identifier has to stay safe to print on a report.
        for (int index = 0; index < trimmedId.length(); index++) {
            if (!Character.isDigit(trimmedId.charAt(index))) {
                throw new IllegalArgumentException(
                        "A health professional ID must contain digits only, but was: " + trimmedId);
            }
        }
        return trimmedId;
    }

    /**
     * Validates a professional name.
     *
     * @param name the name to check
     * @return the trimmed name
     * @throws IllegalArgumentException if the name is missing or blank
     */
    private static String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("A health professional name is required.");
        }
        return name.trim();
    }

    /**
     * Copies the working pattern so the object keeps its own private list and
     * no caller can alter it later. Duplicate days are ignored.
     *
     * @param days the days supplied by the caller
     * @return a new list holding the validated days
     * @throws IllegalArgumentException if no day, or a null day, is supplied
     */
    private static List<DayOfWeek> copyDays(List<DayOfWeek> days) {
        if (days == null || days.isEmpty()) {
            throw new IllegalArgumentException("At least one working day is required.");
        }
        // Build a new list instead of keeping (and exposing) the caller's list.
        List<DayOfWeek> copy = new ArrayList<>();
        for (DayOfWeek day : days) {
            if (day == null) {
                throw new IllegalArgumentException("A working day cannot be null.");
            }
            // Duplicate days are ignored so the printed working pattern stays tidy.
            if (!copy.contains(day)) {
                copy.add(day);
            }
        }
        return copy;
    }

    /**
     * @return the clinic's default working pattern, Monday to Friday
     */
    private static List<DayOfWeek> defaultAvailability() {
        List<DayOfWeek> days = new ArrayList<>();
        // Monday to Friday is the clinic's standard full-time pattern.
        days.add(DayOfWeek.MONDAY);
        days.add(DayOfWeek.TUESDAY);
        days.add(DayOfWeek.WEDNESDAY);
        days.add(DayOfWeek.THURSDAY);
        days.add(DayOfWeek.FRIDAY);
        return days;
    }

    /**
     * @return the professional's identifier
     */
    public String getId() {
        return id;
    }

    /**
     * @return the professional's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a copy of the working pattern. Handing back a copy keeps the
     * object's own list safe from outside modification.
     *
     * @return the days the professional works
     */
    public List<DayOfWeek> getAvailableDays() {
        return new ArrayList<>(availableDays);
    }

    /**
     * Reports whether the professional works on a particular day.
     *
     * @param day the day to test
     * @return {@code true} if the professional works that day
     */
    public boolean isAvailableOn(DayOfWeek day) {
        return day != null && availableDays.contains(day);
    }

    /**
     * @return the working days as short labels, for example "Mon, Wed, Fri"
     */
    public String getAvailableDaysText() {
        // Build the label list by hand so no trailing comma is ever printed.
        StringBuilder text = new StringBuilder();
        for (int index = 0; index < availableDays.size(); index++) {
            if (index > 0) {
                text.append(", ");
            }
            text.append(availableDays.get(index).getDisplayName(TextStyle.SHORT, Locale.ENGLISH));
        }
        return text.toString();
    }

    /**
     * Every subclass must state the kind of professional it represents so the
     * type can always be printed clearly.
     *
     * @return a label such as "General Practitioner"
     */
    public abstract String getProfessionalType();

    /**
     * Every subclass must describe the extra information it holds beyond the
     * shared base data.
     *
     * @return one readable line of type-specific details
     */
    public abstract String getTypeSpecificDetails();

    /**
     * Every subclass must describe the duty it performs. Printing a mixed
     * collection of professionals through this method is what makes the
     * different types behave differently through one common type.
     *
     * @return a short description of the professional's main duty
     */
    public abstract String performPrimaryDuty();

    /**
     * Two professionals are the same professional when they carry the same
     * identifier. This is what lets the appointment manager recognise a
     * double booking, even when two different objects describe the same person.
     *
     * @param other the object to compare with
     * @return {@code true} if both objects describe the same identifier
     */
    @Override
    public boolean equals(Object other) {
        // Fast path: the same object is always equal to itself.
        if (this == other) {
            return true;
        }
        if (!(other instanceof HealthProfessional)) {
            return false;
        }
        HealthProfessional otherProfessional = (HealthProfessional) other;
        // Identity is the identifier, not the name and not the object reference.
        return id.equals(otherProfessional.id);
    }

    /**
     * @return a hash value consistent with {@link #equals(Object)}
     */
    @Override
    public int hashCode() {
        return id.hashCode();
    }

    /**
     * Builds a full, readable description of the professional: the type, the
     * shared details, and the details contributed by the subclass.
     *
     * @return the description used whenever the object is printed
     */
    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        // First line: the type, the identifier and the name.
        text.append(getProfessionalType()).append(" ").append(id).append(" - ").append(name)
                .append(System.lineSeparator());
        // Second line: the shared data every professional inherits.
        text.append("       Working days : ").append(getAvailableDaysText()).append(System.lineSeparator());
        // Third line: the detail each subclass contributes for itself.
        text.append("       ").append(getTypeSpecificDetails());
        return text.toString();
    }
}
