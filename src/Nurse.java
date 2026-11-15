import java.time.DayOfWeek;
import java.util.List;

/**
 * A nurse: a health professional who carries out hands-on care on a ward.
 *
 * <p>Extends {@link HealthProfessional}. The class adds the ward the nurse is
 * based on and keeps a count of the blood samples collected during the day,
 * which is an example of state that changes while the program runs.</p>
 *
 * @author Jueding Lin
 * @version 1.0
 */
public class Nurse extends HealthProfessional {

    private final String ward;
    private int bloodSamplesCollected;

    /**
     * Creates a nurse.
     *
     * @param id            identifier made of digits only
     * @param name          the nurse's name
     * @param availableDays the days the nurse works
     * @param ward          the ward the nurse is based on
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Nurse(String id, String name, List<DayOfWeek> availableDays, String ward) {
        // The base class stores and validates the shared fields.
        super(id, name, availableDays);
        if (ward == null || ward.trim().isEmpty()) {
            throw new IllegalArgumentException("A ward is required for a nurse.");
        }
        this.ward = ward.trim();
        // The counter starts at zero for every new nurse object.
        this.bloodSamplesCollected = 0;
    }

    /**
     * Creates a nurse who works Monday to Friday on the supplied ward.
     *
     * @param id   identifier made of digits only
     * @param name the nurse's name
     * @param ward the ward the nurse is based on
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Nurse(String id, String name, String ward) {
        this(id, name, standardNurseDays(), ward);
    }

    /**
     * @return the default nursing pattern, Monday to Friday
     */
    private static List<DayOfWeek> standardNurseDays() {
        List<DayOfWeek> days = new java.util.ArrayList<>();
        days.add(DayOfWeek.MONDAY);
        days.add(DayOfWeek.TUESDAY);
        days.add(DayOfWeek.WEDNESDAY);
        days.add(DayOfWeek.THURSDAY);
        days.add(DayOfWeek.FRIDAY);
        return days;
    }

    /**
     * @return the ward the nurse is based on
     */
    public String getWard() {
        return ward;
    }

    /**
     * @return how many blood samples the nurse has collected today
     */
    public int getBloodSamplesCollected() {
        return bloodSamplesCollected;
    }

    /**
     * Takes a blood sample from a named patient and records it in the count.
     *
     * @param patientName the patient to take the sample from
     * @return a confirmation message, or a warning if no patient name was supplied
     */
    public String takeBloodSample(String patientName) {
        if (patientName == null || patientName.trim().isEmpty()) {
            return "WARNING: a patient name is required before a blood sample can be taken.";
        }
        // The nurse's own state changes each time a sample is taken.
        bloodSamplesCollected++;
        return getName() + " took a blood sample from " + patientName.trim()
                + " (samples collected today: " + bloodSamplesCollected + ").";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProfessionalType() {
        return "Nurse";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeSpecificDetails() {
        return "Ward: " + ward + " | Blood samples collected today: " + bloodSamplesCollected;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String performPrimaryDuty() {
        return "Provides hands-on patient care, takes observations and collects specimens.";
    }
}
