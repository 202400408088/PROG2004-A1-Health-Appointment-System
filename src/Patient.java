/**
 * A patient of the health service.
 *
 * <p>An appointment only holds data about a patient, so the patient is modelled
 * as its own small class rather than as two loose fields inside the
 * appointment. That keeps the patient's own validity rules in one place and
 * means a patient could later be reused across several appointments.</p>
 *
 * @author Jueding Lin
 * @version 1.0
 */
public class Patient {

    private static final int MOBILE_LENGTH = 10;
    private static final String MOBILE_PREFIX = "04";

    private final String name;
    private final String mobileNumber;

    /**
     * Creates a patient.
     *
     * @param name         the patient's name
     * @param mobileNumber the patient's mobile number: ten digits starting with 04
     * @throws IllegalArgumentException if the name is blank or the mobile
     *                                  number is not a valid Australian mobile
     */
    public Patient(String name, String mobileNumber) {
        this.name = validateName(name);
        this.mobileNumber = validateMobile(mobileNumber);
    }

    /**
     * Validates a patient name.
     *
     * @param name the name to check
     * @return the trimmed name
     * @throws IllegalArgumentException if the name is missing or blank
     */
    private static String validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("A patient name is required.");
        }
        return name.trim();
    }

    /**
     * Validates a mobile number. Only digits are kept, then the number must be
     * ten digits long and start with 04, which is the Australian mobile prefix.
     *
     * @param mobileNumber the number supplied by the caller
     * @return the validated number
     * @throws IllegalArgumentException if the number is not a valid mobile
     */
    private static String validateMobile(String mobileNumber) {
        if (mobileNumber == null) {
            throw new IllegalArgumentException("A patient mobile number is required.");
        }
        // Keep the digits only, so "0412 345 678" and "0412345678" mean the same number.
        StringBuilder digits = new StringBuilder();
        for (int index = 0; index < mobileNumber.length(); index++) {
            char character = mobileNumber.charAt(index);
            if (Character.isDigit(character)) {
                digits.append(character);
            // Spaces, hyphens and a leading plus are accepted formatting characters.
            } else if (character != ' ' && character != '-' && character != '+') {
                throw new IllegalArgumentException(
                        "A patient mobile number can only contain digits and spaces: " + mobileNumber);
            }
        }
        String plainNumber = digits.toString();
        // An Australian mobile number has exactly ten digits.
        if (plainNumber.length() != MOBILE_LENGTH) {
            throw new IllegalArgumentException(
                    "A patient mobile number must have " + MOBILE_LENGTH + " digits: " + mobileNumber);
        }
        // Mobiles start with 04; anything else is rejected at this point.
        if (!plainNumber.startsWith(MOBILE_PREFIX)) {
            throw new IllegalArgumentException(
                    "A patient mobile number must start with " + MOBILE_PREFIX + ": " + mobileNumber);
        }
        return plainNumber;
    }

    /**
     * @return the patient's name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the patient's mobile number, digits only
     */
    public String getMobileNumber() {
        return mobileNumber;
    }

    /**
     * @return the mobile number in the readable form 0412 345 678
     */
    public String getFormattedMobile() {
        // 0412345678 is displayed as 0412 345 678 by splitting it into three groups.
        return mobileNumber.substring(0, 4) + " " + mobileNumber.substring(4, 7)
                + " " + mobileNumber.substring(7);
    }

    /**
     * @return the patient's name and contact number
     */
    @Override
    public String toString() {
        return name + " (mobile " + getFormattedMobile() + ")";
    }
}
