import java.time.DayOfWeek;
import java.util.List;

/**
 * A surgeon: a specialist who performs operations in a hospital theatre.
 *
 * <p>Extends {@link HealthProfessional}. The class adds the surgical field, the
 * theatre the surgeon works in, and the ability to add a procedure to a
 * theatre list.</p>
 *
 * @author Jueding Lin
 * @version 1.0
 */
public class Surgeon extends HealthProfessional {

    private final String surgicalField;
    private final String theatre;

    /**
     * Creates a surgeon in the clinic's surgical department.
     *
     * @param id            identifier made of digits only
     * @param name          the surgeon's name
     * @param availableDays the days the surgeon operates
     * @param surgicalField the field of surgery the surgeon specialises in
     * @param theatre       the operating theatre the surgeon works in
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Surgeon(String id, String name, List<DayOfWeek> availableDays,
                   String surgicalField, String theatre) {
        // Delegate with the department that suits this type of professional.
        this(id, name, availableDays, surgicalField, theatre, Department.surgery());
    }

    /**
     * Creates a surgeon who works in a supplied department.
     *
     * @param id            identifier made of digits only
     * @param name          the surgeon's name
     * @param availableDays the days the surgeon operates
     * @param surgicalField the field of surgery the surgeon specialises in
     * @param theatre       the operating theatre the surgeon works in
     * @param department    the department the surgeon belongs to
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Surgeon(String id, String name, List<DayOfWeek> availableDays,
                   String surgicalField, String theatre, Department department) {
        // The base class stores and validates the shared fields.
        super(id, name, availableDays, department);
        this.surgicalField = requireText(surgicalField, "A surgical field is required.");
        this.theatre = requireText(theatre, "An operating theatre is required.");
    }

    /**
     * Creates a surgeon who operates on Wednesday and Friday, and works from
     * Theatre 2 unless another theatre is supplied.
     *
     * @param id            identifier made of digits only
     * @param name          the surgeon's name
     * @param surgicalField the field of surgery the surgeon specialises in
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Surgeon(String id, String name, String surgicalField) {
        // Default theatre: the surgeon usually works from Theatre 2.
        this(id, name, standardTheatreDays(), surgicalField, "Theatre 2");
    }

    /**
     * @return the default operating pattern, Wednesday and Friday
     */
    private static List<DayOfWeek> standardTheatreDays() {
        List<DayOfWeek> days = new java.util.ArrayList<>();
        days.add(DayOfWeek.WEDNESDAY);
        days.add(DayOfWeek.FRIDAY);
        return days;
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
     * @return the field of surgery the surgeon specialises in
     */
    public String getSurgicalField() {
        return surgicalField;
    }

    /**
     * @return the operating theatre the surgeon works in
     */
    public String getTheatre() {
        return theatre;
    }

    /**
     * Adds a procedure to the surgeon's theatre list.
     *
     * @param procedureName the procedure to schedule
     * @return a confirmation message, or a warning if no procedure was supplied
     */
    public String scheduleProcedure(String procedureName) {
        // An unnamed procedure cannot be added to a theatre list.
        if (procedureName == null || procedureName.trim().isEmpty()) {
            return "WARNING: a procedure name is required before it can be scheduled.";
        }
        return getName() + " added \"" + procedureName.trim() + "\" to the " + theatre + " list.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProfessionalType() {
        return "Surgeon (Specialist)";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeSpecificDetails() {
        return "Surgical field: " + surgicalField + " | Operating theatre: " + theatre;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String performPrimaryDuty() {
        return "Performs surgical procedures and manages patients before and after an operation.";
    }
}
