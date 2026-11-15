import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * A single appointment that links a patient to a health professional at a
 * specific time on the current day.
 *
 * <p>The professional field is declared as the base type
 * {@link HealthProfessional}, so one appointment class works with every
 * professional type created in Part 2 without any casting or special cases.</p>
 *
 * <p>The class implements {@link Comparable} so a whole collection of
 * appointments can be sorted by time. The time is stored as a
 * {@link LocalTime} rather than text, because "08:30" and "10:00" only sort
 * correctly when the time is a real time value.</p>
 *
 * @author Jueding Lin
 * @version 1.0
 */
public class Appointment implements Comparable<Appointment> {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final Patient patient;
    private final HealthProfessional professional;
    private final LocalTime time;

    /**
     * Creates an appointment.
     *
     * @param patient      the patient being seen
     * @param professional the health professional who will see the patient
     * @param time         the time of the appointment
     * @throws IllegalArgumentException if any argument is missing
     */
    public Appointment(Patient patient, HealthProfessional professional, LocalTime time) {
        // All three parts are required, so each is checked before it is stored.
        if (patient == null) {
            throw new IllegalArgumentException("An appointment needs a patient.");
        }
        if (professional == null) {
            throw new IllegalArgumentException("An appointment needs a health professional.");
        }
        if (time == null) {
            throw new IllegalArgumentException("An appointment needs a time.");
        }
        this.patient = patient;
        this.professional = professional;
        this.time = time;
    }

    /**
     * @return the patient being seen
     */
    public Patient getPatient() {
        return patient;
    }

    /**
     * @return the health professional who will see the patient
     */
    public HealthProfessional getProfessional() {
        return professional;
    }

    /**
     * @return the time of the appointment
     */
    public LocalTime getTime() {
        return time;
    }

    /**
     * @return the time in the readable form HH:mm
     */
    public String getFormattedTime() {
        return time.format(TIME_FORMAT);
    }

    /**
     * Orders appointments from earliest to latest. When two appointments share
     * a time, the professional's identifier is used as a tie-breaker, which is
     * allowed because appointments at the same time for different
     * professionals may appear in any order.
     *
     * @param other the appointment to compare with
     * @return a negative number, zero, or a positive number as this appointment
     *         is earlier than, the same time as, or later than the other one
     */
    @Override
    public int compareTo(Appointment other) {
        // Compare real time values, so 08:30 sorts before 10:00.
        int byTime = this.time.compareTo(other.time);
        if (byTime != 0) {
            return byTime;
        }
        // Tie-breaker for the same time; the brief allows any order in that case.
        return this.professional.getId().compareTo(other.professional.getId());
    }

    /**
     * @return the appointment time, the patient details and the professional
     *         the patient is seeing
     */
    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        // Line 1: the appointment time and the patient.
        text.append(getFormattedTime()).append("  ").append(patient).append(System.lineSeparator());
        // Line 2: the professional the patient is seeing.
        text.append("          with ").append(professional.getProfessionalType())
                .append(" ").append(professional.getId()).append(" - ")
                .append(professional.getName()).append(System.lineSeparator());
        // Line 3: the detail supplied by the professional's own type.
        text.append("          ").append(professional.getTypeSpecificDetails());
        return text.toString();
    }
}
