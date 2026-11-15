import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * The fixed daily timetable the clinic runs to.
 *
 * <p>The clinic sees patients from 08:00 to 16:00 in 30 minute slots. Instead of
 * hard-coding those values inside the appointment manager, the schedule lives
 * here as an object. Every slot is generated from the opening time, the closing
 * time and the slot length, so changing the timetable is a one-line change and
 * the rest of the system keeps working.</p>
 *
 * @author Jueding Lin
 * @version 1.0
 */
public class ClinicSchedule {

    private static final DateTimeFormatter TIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    private final LocalTime openingTime;
    private final LocalTime closingTime;
    private final int slotLengthMinutes;
    private final List<LocalTime> slots;

    /**
     * Creates the clinic's standard timetable: 08:00 to 16:00 in 30 minute slots.
     */
    public ClinicSchedule() {
        // The clinic's standard day: 08:00 to 16:00 in half hour slots.
        this(LocalTime.of(8, 0), LocalTime.of(16, 0), 30);
    }

    /**
     * Creates a timetable from an opening time, a closing time and a slot length.
     * The closing time is the time the clinic stops, so the last appointment
     * starts one slot earlier.
     *
     * @param openingTime       the first slot of the day
     * @param closingTime       the time the clinic closes
     * @param slotLengthMinutes the length of each appointment slot in minutes
     * @throws IllegalArgumentException if the times or the slot length are unusable
     */
    public ClinicSchedule(LocalTime openingTime, LocalTime closingTime, int slotLengthMinutes) {
        if (openingTime == null || closingTime == null) {
            throw new IllegalArgumentException("An opening time and a closing time are required.");
        }
        // An opening time later than the closing time would generate no slots.
        if (openingTime.isAfter(closingTime)) {
            throw new IllegalArgumentException("The clinic cannot open after it closes.");
        }
        if (slotLengthMinutes < 5) {
            throw new IllegalArgumentException("A slot must be at least 5 minutes long.");
        }
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.slotLengthMinutes = slotLengthMinutes;
        // The slot list is generated once, from the three values above.
        this.slots = buildSlots();
    }

    /**
     * Builds every slot between the opening and closing times.
     *
     * @return the list of slot start times
     */
    private List<LocalTime> buildSlots() {
        List<LocalTime> builtSlots = new ArrayList<>();
        // Start at the opening time and step forward one slot at a time.
        LocalTime slot = openingTime;
        // The closing time is a boundary, so the loop stops before it.
        while (slot.isBefore(closingTime)) {
            builtSlots.add(slot);
            slot = slot.plusMinutes(slotLengthMinutes);
        }
        return builtSlots;
    }

    /**
     * @return a copy of every slot start time in the day
     */
    public List<LocalTime> getSlots() {
        return new ArrayList<>(slots);
    }

    /**
     * @return the first slot of the day
     */
    public LocalTime getOpeningTime() {
        return openingTime;
    }

    /**
     * @return the time the clinic closes
     */
    public LocalTime getClosingTime() {
        return closingTime;
    }

    /**
     * @return the length of each appointment slot in minutes
     */
    public int getSlotLengthMinutes() {
        return slotLengthMinutes;
    }

    /**
     * Reports whether a requested time is one of the clinic's slots.
     *
     * @param time the time to test
     * @return {@code true} if the time is a slot start time
     */
    public boolean isValidSlot(LocalTime time) {
        // A time is bookable only when it is exactly one of the slot start times.
        return time != null && slots.contains(time);
    }

    /**
     * @return a short description of the timetable, for example
     *         "08:00 to 15:30 in 30 minute slots (16 slots per day)"
     */
    public String describe() {
        String firstSlot = slots.get(0).format(TIME_FORMAT);
        String lastSlot = slots.get(slots.size() - 1).format(TIME_FORMAT);
        return firstSlot + " to " + lastSlot + " in " + slotLengthMinutes
                + " minute slots (" + slots.size() + " slots per day)";
    }
}
