import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * Keeps every appointment the clinic has booked and offers the operations the
 * clinic needs: add, view, filter, sort and cancel.
 *
 * <p>Appointments are held in an {@link ArrayList} because the collection grows
 * as bookings arrive, is read as a whole when staff view or sort the day, and
 * is small enough that scanning it is cheap. Every method returns a readable
 * message instead of failing silently, so a caller can print the outcome of
 * every operation.</p>
 *
 * @author Jueding Lin
 * @version 1.0
 */
public class AppointmentManager {

    /** Prefix used on a message when an operation succeeded. */
    public static final String OK = "OK      : ";

    /** Prefix used on a message when an operation could not be performed. */
    public static final String WARNING = "WARNING : ";

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final List<Appointment> appointments;
    private final ClinicSchedule schedule;

    /**
     * Creates a manager that books appointments against the supplied timetable.
     *
     * @param schedule the timetable the clinic runs to
     * @throws IllegalArgumentException if no timetable is supplied
     */
    public AppointmentManager(ClinicSchedule schedule) {
        if (schedule == null) {
            throw new IllegalArgumentException("An appointment manager needs a clinic schedule.");
        }
        this.schedule = schedule;
        // Start empty; every booking arrives through addAppointment.
        this.appointments = new ArrayList<>();
    }

    /**
     * Adds an appointment after checking that the booking is possible.
     *
     * @param appointment the appointment to add
     * @return a message describing the outcome
     */
    public String addAppointment(Appointment appointment) {
        // Guard 1: there has to be something to book.
        if (appointment == null) {
            return WARNING + "no appointment was supplied.";
        }
        // Guard 2: the time must be one of the clinic's own slots.
        if (!schedule.isValidSlot(appointment.getTime())) {
            return WARNING + format(appointment.getTime()) + " is not one of the clinic's slots. "
                    + "The clinic runs " + schedule.describe() + ".";
        }
        // Guard 3: one professional cannot be in two places at the same time.
        if (isProfessionalBooked(appointment.getProfessional(), appointment.getTime())) {
            return WARNING + appointment.getProfessional().getName() + " already has an appointment at "
                    + format(appointment.getTime()) + ".";
        }
        // All guards passed, so the booking is stored.
        appointments.add(appointment);
        return OK + "booked " + format(appointment.getTime()) + " for " + appointment.getPatient().getName()
                + " with " + appointment.getProfessional().getProfessionalType() + " "
                + appointment.getProfessional().getId() + " - "
                + appointment.getProfessional().getName() + ".";
    }

    /**
     * Reports whether a professional is already booked at a time. The
     * comparison uses the professional's identifier, so two objects describing
     * the same person are still recognised as the same professional.
     *
     * @param professional the professional to check
     * @param time         the time to check
     * @return {@code true} if the professional already has that slot
     */
    private boolean isProfessionalBooked(HealthProfessional professional, LocalTime time) {
        // A linear scan is enough: the clinic books a few dozen slots a day.
        for (Appointment booked : appointments) {
            if (booked.getProfessional().equals(professional) && booked.getTime().equals(time)) {
                return true;
            }
        }
        return false;
    }

    /**
     * @return every appointment in time order, earliest first
     */
    public List<Appointment> getAllAppointments() {
        // Hand back a sorted copy so callers cannot reorder the manager's own list.
        return sortedCopy(appointments);
    }

    /**
     * Finds the appointments booked with one professional.
     *
     * @param professionalId the professional's identifier
     * @return the matching appointments in time order
     */
    public List<Appointment> findByProfessional(String professionalId) {
        List<Appointment> matches = new ArrayList<>();
        if (professionalId == null || professionalId.trim().isEmpty()) {
            return matches;
        }
        String wantedId = professionalId.trim();
        for (Appointment appointment : appointments) {
            // Compare identifiers, so two objects describing one person still match.
            if (appointment.getProfessional().getId().equals(wantedId)) {
                matches.add(appointment);
            }
        }
        return sortedCopy(matches);
    }

    /**
     * Finds the appointments booked under one patient mobile number.
     *
     * @param mobileNumber the patient's mobile number, in any accepted format
     * @return the matching appointments in time order
     */
    public List<Appointment> findByPatientPhone(String mobileNumber) {
        List<Appointment> matches = new ArrayList<>();
        if (mobileNumber == null || mobileNumber.trim().isEmpty()) {
            return matches;
        }
        // "0423 456 789" and "0423456789" reduce to the same digits.
        String wantedDigits = digitsOnly(mobileNumber);
        for (Appointment appointment : appointments) {
            if (appointment.getPatient().getMobileNumber().equals(wantedDigits)) {
                matches.add(appointment);
            }
        }
        return sortedCopy(matches);
    }

    /**
     * Cancels the appointment a professional holds at a given time.
     *
     * @param professionalId the professional's identifier
     * @param time           the time of the appointment
     * @return a message describing the outcome
     */
    public String cancelByProfessionalAndTime(String professionalId, LocalTime time) {
        if (professionalId == null || professionalId.trim().isEmpty() || time == null) {
            return WARNING + "a professional ID and an appointment time are required to cancel a booking.";
        }
        String wantedId = professionalId.trim();
        // An iterator is used because the list is modified inside the loop.
        for (Iterator<Appointment> iterator = appointments.iterator(); iterator.hasNext(); ) {
            Appointment appointment = iterator.next();
            if (appointment.getProfessional().getId().equals(wantedId)
                    && appointment.getTime().equals(time)) {
                // Remove through the iterator, not the list, so the loop stays valid.
                iterator.remove();
                return OK + "cancelled the " + format(time) + " appointment for "
                        + appointment.getPatient().getName() + " with "
                        + appointment.getProfessional().getName() + ".";
            }
        }
        return WARNING + "no appointment found for professional " + wantedId
                + " at " + format(time) + ".";
    }

    /**
     * Cancels every appointment booked under one patient mobile number.
     *
     * @param mobileNumber the patient's mobile number, in any accepted format
     * @return a message describing the outcome
     */
    public String cancelByPatientPhone(String mobileNumber) {
        if (mobileNumber == null || mobileNumber.trim().isEmpty()) {
            return WARNING + "a patient mobile number is required to cancel a booking.";
        }
        String wantedDigits = digitsOnly(mobileNumber);
        int cancelled = 0;
        // The same pattern removes every booking held under one mobile number.
        for (Iterator<Appointment> iterator = appointments.iterator(); iterator.hasNext(); ) {
            Appointment appointment = iterator.next();
            if (appointment.getPatient().getMobileNumber().equals(wantedDigits)) {
                // Each removed booking is counted, so the message can report the total.
                iterator.remove();
                cancelled++;
            }
        }
        // Nothing matched, so report it instead of claiming a successful cancel.
        if (cancelled == 0) {
            return WARNING + "no appointment found for patient mobile " + mobileNumber.trim() + ".";
        }
        return OK + "cancelled " + cancelled + " appointment(s) for patient mobile "
                + mobileNumber.trim() + ".";
    }

    /**
     * @return how many appointments are currently booked
     */
    public int getAppointmentCount() {
        return appointments.size();
    }

    /**
     * @return the timetable this manager books against
     */
    public ClinicSchedule getSchedule() {
        return schedule;
    }

    /**
     * Sorts a list of appointments without touching the manager's own list.
     *
     * @param list the appointments to sort
     * @return a sorted copy of the list
     */
    private List<Appointment> sortedCopy(List<Appointment> list) {
        // Sort the copy, so the source list keeps its original order.
        List<Appointment> copy = new ArrayList<>(list);
        Collections.sort(copy);
        return copy;
    }

    /**
     * Keeps only the digits of a phone number so the filter accepts numbers
     * typed with spaces.
     *
     * @param text the text to clean
     * @return the digits in the text
     */
    private String digitsOnly(String text) {
        StringBuilder digits = new StringBuilder();
        for (int index = 0; index < text.length(); index++) {
            // Ignore spaces, brackets and dashes the patient may have typed.
            if (Character.isDigit(text.charAt(index))) {
                digits.append(text.charAt(index));
            }
        }
        return digits.toString();
    }

    /**
     * @param time the time to format
     * @return the time in the readable form HH:mm
     */
    private String format(LocalTime time) {
        return time.format(TIME_FORMAT);
    }
}
