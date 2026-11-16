import java.time.DayOfWeek;
import java.util.List;

/**
 * A dietitian: an allied health professional who plans what a patient eats.
 *
 * <p>Extends {@link HealthProfessional}. The class adds the diet focus, the
 * number of years the dietitian has practised, and the ability to produce a
 * meal plan.</p>
 *
 * @author Jueding Lin
 * @version 1.0
 */
public class Dietitian extends HealthProfessional {

    private final String dietFocus;
    private final int yearsPractising;

    /**
     * Creates a dietitian in the clinic's nutrition and dietetics department.
     *
     * @param id              identifier made of digits only
     * @param name            the dietitian's name
     * @param availableDays   the days the dietitian works
     * @param dietFocus       the area of dietetics the dietitian focuses on
     * @param yearsPractising the number of years the dietitian has practised
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Dietitian(String id, String name, List<DayOfWeek> availableDays,
                     String dietFocus, int yearsPractising) {
        // Delegate with the department that suits this type of professional.
        this(id, name, availableDays, dietFocus, yearsPractising, Department.dietetics());
    }

    /**
     * Creates a dietitian who works in a supplied department.
     *
     * @param id              identifier made of digits only
     * @param name            the dietitian's name
     * @param availableDays   the days the dietitian works
     * @param dietFocus       the area of dietetics the dietitian focuses on
     * @param yearsPractising the number of years the dietitian has practised
     * @param department      the department the dietitian belongs to
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Dietitian(String id, String name, List<DayOfWeek> availableDays,
                     String dietFocus, int yearsPractising, Department department) {
        // The base class stores and validates the shared fields.
        super(id, name, availableDays, department);
        // Both extra fields are checked before anything is assigned.
        if (dietFocus == null || dietFocus.trim().isEmpty()) {
            throw new IllegalArgumentException("A diet focus is required for a dietitian.");
        }
        // Experience cannot be negative, so that value is rejected here.
        if (yearsPractising < 0) {
            throw new IllegalArgumentException("Years practising cannot be negative.");
        }
        this.dietFocus = dietFocus.trim();
        this.yearsPractising = yearsPractising;
    }

    /**
     * Creates a newly qualified dietitian who works Monday, Tuesday and Thursday.
     *
     * @param id        identifier made of digits only
     * @param name      the dietitian's name
     * @param dietFocus the area of dietetics the dietitian focuses on
     * @throws IllegalArgumentException if any argument is invalid
     */
    public Dietitian(String id, String name, String dietFocus) {
        this(id, name, standardDietitianDays(), dietFocus, 0);
    }

    /**
     * @return the default dietitian working pattern, Monday, Tuesday and Thursday
     */
    private static List<DayOfWeek> standardDietitianDays() {
        List<DayOfWeek> days = new java.util.ArrayList<>();
        days.add(DayOfWeek.MONDAY);
        days.add(DayOfWeek.TUESDAY);
        days.add(DayOfWeek.THURSDAY);
        return days;
    }

    /**
     * @return the area of dietetics the dietitian focuses on
     */
    public String getDietFocus() {
        return dietFocus;
    }

    /**
     * @return the number of years the dietitian has practised
     */
    public int getYearsPractising() {
        return yearsPractising;
    }

    /**
     * Builds a simple daily meal plan for a target energy intake.
     *
     * @param dailyKilojoules the target energy intake for the day
     * @return a one-line meal plan, or a warning if the target is unrealistic
     */
    public String createMealPlan(int dailyKilojoules) {
        // Outside this range the request is unrealistic, so a warning is returned.
        if (dailyKilojoules < 4000 || dailyKilojoules > 16000) {
            return "WARNING: " + dailyKilojoules + " kJ is outside the range a dietitian can plan for.";
        }
        return getName() + " planned a daily intake of " + dailyKilojoules
                + " kJ: breakfast, a high-fibre lunch and a light dinner.";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProfessionalType() {
        return "Dietitian";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTypeSpecificDetails() {
        return "Diet focus: " + dietFocus + " | Years practising: " + yearsPractising;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String performPrimaryDuty() {
        return "Assesses nutritional needs and prepares meal plans for patients.";
    }
}
