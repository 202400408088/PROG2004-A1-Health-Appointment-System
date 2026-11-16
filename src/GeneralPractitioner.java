import java.time.DayOfWeek;
import java.util.List;

/**
 * A general practitioner: the first point of contact for most patients.
 *
 * <p>Extends {@link HealthProfessional}, so the identifier, name and working
 * pattern are inherited rather than repeated. The class adds the consulting
 * room the doctor works from, whether the doctor is taking new patients, and
 * the ability to write a prescription.</p>
 *
 * @author Jueding Lin
 * @version 1.0
 */
public class GeneralPractitioner extends HealthProfessional {

    private final String consultingRoom;
    private final boolean acceptingNewPatients;

    /**
     * Creates a general practitioner with an explicitly supplied working pattern,
     * placed in the clinic's general practice department.
     *
     * @param id                  identifier made of digits only
     * @param name                the doctor's name
     * @param availableDays       the days the doctor works
     * @param consultingRoom      the room the doctor consults from
     * @param acceptingNewPatients whether the doctor is taking new patients
     * @throws IllegalArgumentException if any argument is invalid
     */
    public GeneralPractitioner(String id, String name, List<DayOfWeek> availableDays,
                               String consultingRoom, boolean acceptingNewPatients) {
        // Delegate with the department that suits this type of professional.
        this(id, name, availableDays, consultingRoom, acceptingNewPatients,
                Department.generalPractice());
    }

    /**
     * Creates a general practitioner who works in a supplied department.
     *
     * @param id                  identifier made of digits only
     * @param name                the doctor's name
     * @param availableDays       the days the doctor works
     * @param consultingRoom      the room the doctor consults from
     * @param acceptingNewPatients whether the doctor is taking new patients
     * @param department          the department the doctor belongs to
     * @throws IllegalArgumentException if any argument is invalid
     */
    public GeneralPractitioner(String id, String name, List<DayOfWeek> availableDays,
                               String consultingRoom, boolean acceptingNewPatients,
                               Department department) {
        // Hand the shared data to the base class, which owns the shared validation.
        super(id, name, availableDays, department);
        this.consultingRoom = validateConsultingRoom(consultingRoom);
        this.acceptingNewPatients = acceptingNewPatients;
    }

    /**
     * Creates a general practitioner who works the default Monday to Friday
     * pattern and is currently accepting new patients. Both defaults are the
     * most common case for a general practice clinic.
     *
     * @param id             identifier made of digits only
     * @param name           the doctor's name
     * @param consultingRoom the room the doctor consults from
     * @throws IllegalArgumentException if any argument is invalid
     */
    public GeneralPractitioner(String id, String name, String consultingRoom) {
        // Delegate to the full constructor, filling in the two sensible defaults.
        this(id, name, defaultDays(), consultingRoom, true);
    }

    /**
     * @return the default Monday to Friday working pattern
     */
    private static List<DayOfWeek> defaultDays() {
        List<DayOfWeek> days = new java.util.ArrayList<>();
        // Monday to Friday is the usual full-time general practice week.
        days.add(DayOfWeek.MONDAY);
        days.add(DayOfWeek.TUESDAY);
        days.add(DayOfWeek.WEDNESDAY);
        days.add(DayOfWeek.THURSDAY);
        days.add(DayOfWeek.FRIDAY);
        return days;
    }

    /**
     * @param consultingRoom the room to check
     * @return the trimmed room label
     * @throws IllegalArgumentException if the room is missing or blank
     */
    private static String validateConsultingRoom(String consultingRoom) {
        if (consultingRoom == null || consultingRoom.trim().isEmpty()) {
            throw new IllegalArgumentException("A consulting room is required for a general practitioner.");
        }
        return consultingRoom.trim();
    }

    /**
     * @return the room the doctor consults from
     */
    public String getConsultingRoom() {
        return consultingRoom;
    }

    /**
     * @return {@code true} if the doctor is taking new patients
     */
    public boolean isAcceptingNewPatients() {
        return acceptingNewPatients;
    }

    /**
     * Writes a prescription for a patient. The prescription is returned as a
     * readable confirmation so the caller can print it.
     *
     * @param medication the medicine being prescribed
     * @param dosage     the dosage, for example "500 mg twice daily"
     * @return a confirmation message, or a warning if the request is incomplete
     */
    public String prescribeMedication(String medication, String dosage) {
        // Both parts are required: a prescription with no dosage is unsafe.
        if (medication == null || medication.trim().isEmpty()
                || dosage == null || dosage.trim().isEmpty()) {
            return "WARNING: a medication and a dosage are both required before a prescription is written.";
        }
        return getName() + " prescribed " + medication.trim() + " (" + dosage.trim() + ").";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProfessionalType() {
        return "General Practitioner";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeSpecificDetails() {
        // Turn the boolean into readable text for the details line.
        String newPatients = acceptingNewPatients ? "yes" : "no";
        return "Consulting room: " + consultingRoom + " | Accepting new patients: " + newPatients;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String performPrimaryDuty() {
        return "Consults with patients, diagnoses common conditions and prescribes medication.";
    }
}
