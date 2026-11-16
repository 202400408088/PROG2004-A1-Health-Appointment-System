import java.time.DayOfWeek;
import java.util.List;

/**
 * A cardiologist: a specialist who investigates and treats heart conditions.
 *
 * <p>Extends {@link HealthProfessional}. The class adds the sub-specialty and
 * the laboratory the specialist reports from, plus the ability to order an
 * electrocardiogram (ECG).</p>
 *
 * @author Jueding Lin
 * @version 1.0
 */
public class Cardiologist extends HealthProfessional {

    private final String subSpecialty;
    private final String reportingLab;

    /**
     * Creates a cardiologist in the clinic's cardiology department.
     *
     * @param id            identifier made of digits only
     * @param name          the specialist's name
     * @param availableDays the days the specialist works
     * @param subSpecialty  the area of cardiology the specialist focuses on
     * @param reportingLab  the laboratory the specialist reports from
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Cardiologist(String id, String name, List<DayOfWeek> availableDays,
                        String subSpecialty, String reportingLab) {
        // Delegate with the department that suits this type of professional.
        this(id, name, availableDays, subSpecialty, reportingLab, Department.cardiology());
    }

    /**
     * Creates a cardiologist who works in a supplied department.
     *
     * @param id            identifier made of digits only
     * @param name          the specialist's name
     * @param availableDays the days the specialist works
     * @param subSpecialty  the area of cardiology the specialist focuses on
     * @param reportingLab  the laboratory the specialist reports from
     * @param department    the department the specialist belongs to
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Cardiologist(String id, String name, List<DayOfWeek> availableDays,
                        String subSpecialty, String reportingLab, Department department) {
        // The base class stores and validates the shared fields.
        super(id, name, availableDays, department);
        this.subSpecialty = requireText(subSpecialty, "A cardiology sub-specialty is required.");
        this.reportingLab = requireText(reportingLab, "A reporting laboratory is required.");
    }

    /**
     * Creates a cardiologist who works Monday and Wednesday, the clinic's
     * standard specialist pattern.
     *
     * @param id           identifier made of digits only
     * @param name         the specialist's name
     * @param subSpecialty the area of cardiology the specialist focuses on
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Cardiologist(String id, String name, String subSpecialty) {
        // Default working pattern and laboratory for a part-time specialist.
        this(id, name, standardSpecialistDays(), subSpecialty, "Cardiac Laboratory");
    }

    /**
     * @return the default specialist working pattern, Monday and Wednesday
     */
    private static List<DayOfWeek> standardSpecialistDays() {
        List<DayOfWeek> days = new java.util.ArrayList<>();
        days.add(DayOfWeek.MONDAY);
        days.add(DayOfWeek.WEDNESDAY);
        return days;
    }

    /**
     * @param value   the text to check
     * @param message the message to use if the text is unusable
     * @return the trimmed text
     * @throws IllegalArgumentException if the text is missing or blank
     */
    private static String requireText(String value, String message) {
        // One helper rejects blank text for every text field in this class.
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalArgumentException(message);
        }
        return value.trim();
    }

    /**
     * @return the sub-specialty the specialist focuses on
     */
    public String getSubSpecialty() {
        return subSpecialty;
    }

    /**
     * @return the laboratory the specialist reports from
     */
    public String getReportingLab() {
        return reportingLab;
    }

    /**
     * Orders an ECG for a named patient.
     *
     * @param patientName the patient to test
     * @return a confirmation message, or a warning if no patient name was supplied
     */
    public String requestElectrocardiogram(String patientName) {
        if (patientName == null || patientName.trim().isEmpty()) {
            return "WARNING: a patient name is required before an ECG can be ordered.";
        }
        // The confirmation names the patient and the laboratory doing the test.
        return getName() + " ordered an ECG for " + patientName.trim()
                + " in " + reportingLab + ".";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProfessionalType() {
        return "Cardiologist (Specialist)";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeSpecificDetails() {
        return "Sub-specialty: " + subSpecialty + " | Reports from: " + reportingLab;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String performPrimaryDuty() {
        return "Assesses heart conditions, orders cardiac tests and reviews specialist referrals.";
    }
}
