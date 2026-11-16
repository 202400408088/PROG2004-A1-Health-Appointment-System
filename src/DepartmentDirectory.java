import java.util.ArrayList;
import java.util.List;

/**
 * Keeps the register of departments the clinic runs.
 *
 * <p>The directory owns the list of departments and answers questions about
 * them: which departments exist, what a given code refers to, and which
 * professionals belong to one department. It exists so that the rest of the
 * program never keeps department names in a local variable, and so that a
 * duplicate department can be rejected in one place.</p>
 *
 * <p>Like {@link AppointmentManager}, every operation that can fail returns a
 * readable message with the {@link #WARNING} prefix rather than throwing.</p>
 *
 * @author Jueding Lin
 * @version 1.0
 */
public class DepartmentDirectory {

    /** Prefix used on a message when an operation succeeded. */
    public static final String OK = "OK      : ";

    /** Prefix used on a message when an operation could not be performed. */
    public static final String WARNING = "WARNING : ";

    private final List<Department> departments;

    /**
     * Creates an empty directory. Departments are added through
     * {@link #addDepartment(Department)}.
     */
    public DepartmentDirectory() {
        // Start empty so the same rules apply to every department, including
        // the clinic's standard five.
        this.departments = new ArrayList<>();
    }

    /**
     * Creates a directory holding the five departments the clinic runs.
     *
     * @return a directory with general practice, cardiology, surgery,
     *         nutrition and dietetics, and nursing
     */
    public static DepartmentDirectory standardClinic() {
        DepartmentDirectory directory = new DepartmentDirectory();
        directory.addDepartment(Department.generalPractice());
        directory.addDepartment(Department.cardiology());
        directory.addDepartment(Department.surgery());
        directory.addDepartment(Department.dietetics());
        directory.addDepartment(Department.nursing());
        return directory;
    }

    /**
     * Registers a department, rejecting a code that is already in use.
     *
     * @param department the department to register
     * @return a message describing the outcome
     */
    public String addDepartment(Department department) {
        // Guard 1: there has to be a department to register.
        if (department == null) {
            return WARNING + "no department was supplied.";
        }
        // Guard 2: two departments cannot share one code.
        if (findByCode(department.getCode()) != null) {
            return WARNING + "department " + department.getCode() + " is already registered.";
        }
        departments.add(department);
        return OK + "registered " + department.getCode() + " - " + department.getName() + ".";
    }

    /**
     * Finds a department by its code.
     *
     * @param code the code to look for
     * @return the matching department, or {@code null} when no department uses that code
     */
    public Department findByCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            return null;
        }
        // The stored codes are already upper case, so the lookup is too.
        String wantedCode = code.trim().toUpperCase(java.util.Locale.ENGLISH);
        for (Department department : departments) {
            // Codes are unique, so the first match is the only match.
            if (department.getCode().equals(wantedCode)) {
                return department;
            }
        }
        return null;
    }

    /**
     * @param code the code to test
     * @return {@code true} when a department with that code is registered
     */
    public boolean hasDepartment(String code) {
        return findByCode(code) != null;
    }

    /**
     * Returns a copy of the register, so callers cannot add or remove
     * departments behind the directory's back.
     *
     * @return every registered department, in the order they were added
     */
    public List<Department> getAllDepartments() {
        return new ArrayList<>(departments);
    }

    /**
     * @return how many departments are registered
     */
    public int size() {
        return departments.size();
    }

    /**
     * Lists the professionals who belong to one department.
     *
     * @param code          the department code to search for
     * @param professionals the professionals to search
     * @return the matching professionals, in the order they were supplied
     */
    public List<HealthProfessional> findProfessionalsByDepartment(String code,
                                                                  List<HealthProfessional> professionals) {
        List<HealthProfessional> matches = new ArrayList<>();
        if (code == null || code.trim().isEmpty() || professionals == null) {
            return matches;
        }
        String wantedCode = code.trim().toUpperCase(java.util.Locale.ENGLISH);
        for (HealthProfessional professional : professionals) {
            // A professional with no department recorded never matches a code.
            Department department = professional.getDepartment();
            if (department != null && department.getCode().equals(wantedCode)) {
                matches.add(professional);
            }
        }
        return matches;
    }

    /**
     * Counts the professionals who have no department recorded.
     *
     * @param professionals the professionals to check
     * @return how many of them have no department
     */
    public int countUnassigned(List<HealthProfessional> professionals) {
        int unassigned = 0;
        if (professionals == null) {
            return unassigned;
        }
        for (HealthProfessional professional : professionals) {
            if (professional.getDepartment() == null) {
                unassigned++;
            }
        }
        return unassigned;
    }
}
