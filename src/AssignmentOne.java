import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Demonstration driver for the health service appointment system.
 *
 * <p>There is no user interface in this assessment, so the main method stands in
 * for the people who would use the system: it creates health professionals,
 * prints them, books appointments, filters and sorts the bookings, cancels some
 * of them, and shows what happens when an operation cannot be carried out.</p>
 *
 * <p>Parts 1 and 2 of the brief live in the class design
 * ({@link HealthProfessional} and its five subclasses); this class demonstrates
 * Parts 3, 4, 5 and 6.</p>
 *
 * @author Jueding Lin
 * @version 1.0
 */
public class AssignmentOne {

    private static final String TITLE_LINE =
            "========================================================================";
    private static final String SECTION_LINE =
            "------------------------------------------------------------------------";

    /**
     * Runs the scripted demonstration.
     *
     * @param args command line arguments, not used
     */
    public static void main(String[] args) {
        printBanner();

        // ---------------------------------------------------------------- Part 3
        printSection("PART 3 - WORKING WITH HEALTH PROFESSIONAL OBJECTS");
        // One list holds every type, because all of them share the base class.
        List<HealthProfessional> professionals = createProfessionals();
        System.out.println(" " + professionals.size()
                + " health professionals were created from five different classes.");
        System.out.println(" They are stored in one List<HealthProfessional>, so every type is");
        System.out.println(" handled together through the same base type.");
        System.out.println();
        printProfessionalList(professionals);

        System.out.println(" One professional object is printed on its own, then the shared");
        System.out.println(" behaviour inherited from the base class is exercised:");
        // A base type variable can refer to any subclass object.
        HealthProfessional example = professionals.get(0);
        System.out.println("   Object        : " + example);
        System.out.println("   ID            : " + example.getId());
        System.out.println("   Works Tuesday : " + example.isAvailableOn(DayOfWeek.TUESDAY));
        System.out.println("   Works Friday  : " + example.isAvailableOn(DayOfWeek.FRIDAY));
        System.out.println();

        System.out.println(" Each type also carries behaviour only it can perform:");
        // A cast is needed only to call behaviour that this type alone has.
        GeneralPractitioner generalPractitioner = (GeneralPractitioner) professionals.get(0);
        Cardiologist cardiologist = (Cardiologist) professionals.get(3);
        Surgeon surgeon = (Surgeon) professionals.get(5);
        Dietitian dietitian = (Dietitian) professionals.get(6);
        Nurse nurse = (Nurse) professionals.get(8);
        System.out.println("   " + generalPractitioner.prescribeMedication("Amoxicillin", "500 mg three times daily"));
        System.out.println("   " + cardiologist.requestElectrocardiogram("Ava Thompson"));
        System.out.println("   " + surgeon.scheduleProcedure("Knee arthroscopy"));
        System.out.println("   " + dietitian.createMealPlan(8700));
        System.out.println("   " + nurse.takeBloodSample("Liam Patel"));
        System.out.println("   " + nurse.takeBloodSample("Ava Thompson"));
        System.out.println("   The nurse's own record now reads: " + nurse.getTypeSpecificDetails());
        System.out.println();

        // Show that the constructors reject invalid data before objects exist.
        demonstrateGuards();

        // ---------------------------------------------------------------- Part 4
        printSection("PART 4 - MODELLING AN APPOINTMENT");
        Patient ava = new Patient("Ava Thompson", "0412345678");
        Patient noah = new Patient("Noah Wilson", "0423 456 789");
        Patient mia = new Patient("Mia Chen", "0434567890");
        Patient liam = new Patient("Liam Patel", "0445678901");

        Appointment firstAppointment =
                new Appointment(ava, generalPractitioner, LocalTime.of(8, 30));
        System.out.println(" A newly created appointment prints as:");
        System.out.println("   " + firstAppointment);
        System.out.println();

        System.out.println(" Appointments order themselves by time, so a mixed collection can be");
        System.out.println(" sorted before the times have even been put in order:");
        List<Appointment> unordered = new ArrayList<>();
        unordered.add(new Appointment(mia, generalPractitioner, LocalTime.of(11, 0)));
        unordered.add(new Appointment(liam, dietitian, LocalTime.of(8, 30)));
        unordered.add(new Appointment(noah, cardiologist, LocalTime.of(9, 30)));
        System.out.println("   Before sorting: 11:00, 08:30, 09:30");
        // compareTo is what Collections.sort uses to order the appointments.
        Collections.sort(unordered);
        System.out.print("   After sorting :");
        for (Appointment appointment : unordered) {
            System.out.print(" " + appointment.getFormattedTime());
        }
        System.out.println();
        System.out.println();

        // ---------------------------------------------------------------- Part 5
        printSection("PART 5 - MANAGING THE COLLECTION OF APPOINTMENTS");
        // The timetable object decides which times the manager will accept.
        ClinicSchedule schedule = new ClinicSchedule();
        AppointmentManager manager = new AppointmentManager(schedule);
        System.out.println(" Clinic timetable : " + schedule.describe());
        System.out.println(" Bookings are only made for the current day: " + LocalDate.now());
        System.out.println();

        System.out.println(" Adding appointments (normal cases):");
        printMessage(manager.addAppointment(firstAppointment));

        Appointment cardiologyBooking =
                new Appointment(noah, cardiologist, LocalTime.of(9, 0));
        printMessage(manager.addAppointment(cardiologyBooking));

        Appointment secondGeneralPracticeBooking =
                new Appointment(mia, professionals.get(1), LocalTime.of(9, 30));
        printMessage(manager.addAppointment(secondGeneralPracticeBooking));

        Appointment nursingBooking =
                new Appointment(liam, nurse, LocalTime.of(10, 0));
        printMessage(manager.addAppointment(nursingBooking));

        Appointment dietitianBooking =
                new Appointment(ava, dietitian, LocalTime.of(11, 0));
        printMessage(manager.addAppointment(dietitianBooking));

        Appointment surgicalBooking =
                new Appointment(noah, surgeon, LocalTime.of(13, 30));
        printMessage(manager.addAppointment(surgicalBooking));

        Appointment afternoonBooking =
                new Appointment(liam, generalPractitioner, LocalTime.of(14, 0));
        printMessage(manager.addAppointment(afternoonBooking));
        System.out.println();

        System.out.println(" Adding appointments (error cases):");
        Appointment doubleBooking =
                new Appointment(mia, cardiologist, LocalTime.of(9, 0));
        printMessage(manager.addAppointment(doubleBooking));

        Appointment outsideSchedule =
                new Appointment(mia, professionals.get(2), LocalTime.of(10, 15));
        printMessage(manager.addAppointment(outsideSchedule));

        // A null booking has to be reported, not thrown.
        printMessage(manager.addAppointment(null));
        System.out.println();

        printAppointments("All appointments, sorted from earliest to latest:",
                manager.getAllAppointments());
        System.out.println();

        // ---------------------------------------------------------------- Part 6
        printSection("PART 6 - WORKING WITH APPOINTMENTS");
        printAppointments("Filtered by health professional 1001:",
                manager.findByProfessional("1001"));
        System.out.println();
        printAppointments("Filtered by health professional 2001:",
                manager.findByProfessional("2001"));
        System.out.println();
        printAppointments("Filtered by patient mobile 0412 345 678:",
                manager.findByPatientPhone("0412 345 678"));
        System.out.println();
        printAppointments("Filtered by patient mobile 0445678901:",
                manager.findByPatientPhone("0445678901"));
        System.out.println();

        System.out.println(" Cancelling appointments:");
        // Cancel one booking by naming the professional and the time.
        printMessage(manager.cancelByProfessionalAndTime("1001", LocalTime.of(8, 30)));
        // Cancel every booking held under one mobile number.
        printMessage(manager.cancelByPatientPhone("0423 456 789"));
        System.out.println();

        System.out.println(" Cancelling appointments (error cases):");
        printMessage(manager.cancelByProfessionalAndTime("3001", LocalTime.of(13, 30)));
        printMessage(manager.cancelByProfessionalAndTime("9999", LocalTime.of(14, 0)));
        printMessage(manager.cancelByPatientPhone("0499999999"));
        System.out.println();

        printAppointments("The collection after the cancellations:", manager.getAllAppointments());
        System.out.println();
        System.out.println(" Appointments still booked: " + manager.getAppointmentCount()
                + " (7 were booked successfully, 1 was cancelled individually and");
        System.out.println(" 2 were cancelled by patient mobile number).");

        System.out.println();
        System.out.println(TITLE_LINE);
        System.out.println(" End of demonstration.");
        System.out.println(TITLE_LINE);
    }

    /**
     * Prints the banner that introduces the demonstration.
     */
    private static void printBanner() {
        System.out.println(TITLE_LINE);
        System.out.println(" PROG2004 Object Oriented Programming - Assessment 1");
        System.out.println(" Health Service Appointment System");
        System.out.println(" Demonstration run for " + LocalDate.now());
        System.out.println(TITLE_LINE);
    }

    /**
     * Prints a section heading.
     *
     * @param title the heading to print
     */
    private static void printSection(String title) {
        System.out.println();
        System.out.println(SECTION_LINE);
        System.out.println(" " + title);
        System.out.println(SECTION_LINE);
        System.out.println();
    }

    /**
     * Prints one message returned by the appointment manager.
     *
     * @param message the message to print
     */
    private static void printMessage(String message) {
        System.out.println("   " + message);
    }

    /**
     * Prints every professional in the collection, followed by the duty that
     * each object performs for itself.
     *
     * @param professionals the professionals to print
     */
    private static void printProfessionalList(List<HealthProfessional> professionals) {
        System.out.println(" Full details of every professional:");
        for (int index = 0; index < professionals.size(); index++) {
            HealthProfessional professional = professionals.get(index);
            System.out.println("   (" + (index + 1) + ") " + professional);
            // The same call dispatches to each subclass's own implementation.
            System.out.println("       Primary duty : " + professional.performPrimaryDuty());
            System.out.println();
        }
    }

    /**
     * Prints a labelled list of appointments.
     *
     * @param label        the description of what the list contains
     * @param appointments the appointments to print
     */
    private static void printAppointments(String label, List<Appointment> appointments) {
        System.out.println(" " + label);
        if (appointments.isEmpty()) {
            System.out.println("     (no appointments to show)");
            return;
        }
        for (Appointment appointment : appointments) {
            System.out.println("     - " + appointment);
        }
        System.out.println("     Total in this list: " + appointments.size());
    }

    /**
     * Creates the demonstration professionals: three general practitioners, two
     * cardiologists, one surgeon, two dietitians and two nurses.
     *
     * @return the professionals, ready to be processed as one collection
     */
    private static List<HealthProfessional> createProfessionals() {
        List<HealthProfessional> professionals = new ArrayList<>();

        professionals.add(new GeneralPractitioner("1001", "Dr Anna Nguyen",
                days(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
                "Room 3", true));
        professionals.add(new GeneralPractitioner("1002", "Dr Ben Carter",
                days(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY),
                "Room 5", false));
        // This one uses the shorter constructor, so it takes the default days.
        professionals.add(new GeneralPractitioner("1003", "Dr Priya Sharma", "Room 8"));

        professionals.add(new Cardiologist("2001", "Dr Helen Marsh",
                days(DayOfWeek.MONDAY, DayOfWeek.WEDNESDAY),
                "Interventional cardiology", "Cardiac Laboratory 1"));
        professionals.add(new Cardiologist("2002", "Dr Omar Haddad",
                days(DayOfWeek.TUESDAY, DayOfWeek.FRIDAY),
                "Electrophysiology", "Cardiac Laboratory 2"));

        professionals.add(new Surgeon("3001", "Dr Lucas Reid",
                days(DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
                "Orthopaedic surgery", "Theatre 2"));

        professionals.add(new Dietitian("4001", "Ms Mei Tan",
                days(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.THURSDAY),
                "Type 2 diabetes and weight management", 9));
        professionals.add(new Dietitian("4002", "Mr Daniel Brooks",
                days(DayOfWeek.WEDNESDAY, DayOfWeek.FRIDAY),
                "Sports nutrition", 4));

        professionals.add(new Nurse("5001", "Nurse Grace Okafor",
                days(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY,
                        DayOfWeek.THURSDAY, DayOfWeek.FRIDAY), "Ward B"));
        professionals.add(new Nurse("5002", "Nurse Tom Baker",
                days(DayOfWeek.WEDNESDAY, DayOfWeek.THURSDAY, DayOfWeek.FRIDAY), "Ward A"));

        return professionals;
    }

    /**
     * Shows that invalid objects cannot be created: each attempt is rejected by
     * the constructor with a clear message.
     */
    private static void demonstrateGuards() {
        System.out.println(" Invalid data is rejected before an object can exist:");
        try {
            // 1. A letter in the identifier is rejected.
            new GeneralPractitioner("10A1", "Dr Test", "Room 1");
        } catch (IllegalArgumentException exception) {
            System.out.println("   Guarded : " + exception.getMessage());
        }
        try {
            // 2. A blank name is rejected.
            new Nurse("6001", "   ", "Ward C");
        } catch (IllegalArgumentException exception) {
            System.out.println("   Guarded : " + exception.getMessage());
        }
        try {
            // 3. A mobile number with only seven digits is rejected.
            new Patient("Chris Bell", "0412 345");
        } catch (IllegalArgumentException exception) {
            System.out.println("   Guarded : " + exception.getMessage());
        }
        try {
            // 4. A booking with no patient, professional or time is rejected.
            new Appointment(null, null, null);
        } catch (IllegalArgumentException exception) {
            System.out.println("   Guarded : " + exception.getMessage());
        }
        try {
            // 5. A timetable that opens after it closes is rejected.
            new ClinicSchedule(LocalTime.of(17, 0), LocalTime.of(9, 0), 30);
        } catch (IllegalArgumentException exception) {
            System.out.println("   Guarded : " + exception.getMessage());
        }
    }

    /**
     * Builds a list of working days from a set of days.
     *
     * @param dayList the days the professional works
     * @return a list holding those days
     */
    private static List<DayOfWeek> days(DayOfWeek... dayList) {
        List<DayOfWeek> list = new ArrayList<>();
        for (DayOfWeek day : dayList) {
            list.add(day);
        }
        return list;
    }
}
