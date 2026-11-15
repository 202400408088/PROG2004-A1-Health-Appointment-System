# PROG2004 Assessment 1 — Health Service Appointment System

An object-oriented backbone for a small health-service appointment system, written in Java.
There is no user interface: `AssignmentOne` runs a scripted demonstration that creates
professionals, books appointments, filters, sorts, cancels and reports the error cases.

**Author:** Jueding Lin

> Rename the project folder and the zip to `A1-<your MySCU username>` before submitting.

---

## 1. How to run

### From a terminal

```bash
cd A1-username
javac -d out src/*.java
java -cp out AssignmentOne
```

### From IntelliJ IDEA / Eclipse

1. Open the `A1-username` folder as a project (it is a plain Java project; `src` is the source root).
2. Set the project SDK to Java 11 or newer.
3. Run the `AssignmentOne` class.

---

## 2. Class map

| Class | Responsibility | Part |
|---|---|---|
| `HealthProfessional` (abstract) | Shared state and behaviour for every professional: numeric ID, name, fixed weekly working days. Declares the abstract operations every subtype must supply. | 1 |
| `GeneralPractitioner` | Consulting room, new-patient status, `prescribeMedication(...)`. | 2 |
| `Cardiologist` | Sub-specialty, reporting lab, `requestElectrocardiogram(...)`. | 2 |
| `Surgeon` | Surgical field, theatre, `scheduleProcedure(...)`. | 2 |
| `Dietitian` | Diet focus, years practising, `createMealPlan(...)`. | 2 |
| `Nurse` | Ward, mutable count of blood samples, `takeBloodSample(...)`. | 2 |
| `Patient` | Patient name and validated mobile number. | 4 |
| `Appointment` | Links one `Patient`, one `HealthProfessional` and one `LocalTime`; implements `Comparable<Appointment>`. | 4 |
| `ClinicSchedule` | The fixed daily timetable (08:00–16:00, 30-minute slots), generated from its own configuration. | 5 |
| `AppointmentManager` | Owns the `ArrayList` of appointments and offers add / view / filter / sort / cancel, each returning a readable message. | 5 |
| `AssignmentOne` | `main` method: the scripted end-to-end demonstration. | 3, 4, 5, 6 |

Relationship summary:

```
                 HealthProfessional (abstract)
                 id, name, availableDays
                 + getProfessionalType()
                 + getTypeSpecificDetails()
                 + performPrimaryDuty()
                        ^
        ┌───────────┼────────────┬─────────────┬───────────┐
 GeneralPractitioner  Cardiologist  Surgeon   Dietitian   Nurse
        ▲
   (5 concrete types, all usable through the base type)

  Patient ──── Appointment ──── HealthProfessional
                   │
                   └── Comparable<Appointment> (orders by time)

  AppointmentManager ── has-a ──> List<Appointment>
                     ── has-a ──> ClinicSchedule
```

---

## 3. Design decisions and justification

### Part 1 — Modelling a health professional

* **Only genuinely shared data lives in the base class.** Every professional has an ID, a
  name and a weekly pattern, and nothing else is common across a GP, a surgeon and a nurse,
  so `HealthProfessional` holds exactly those three fields.
* **The ID is validated as digits only.** The brief requires a numeric identifier, so the
  constructor rejects letters, blanks and over-long values before an object can exist.
* **No setters; defensive copies instead.** `id`, `name` and `availableDays` are `final`. The
  getter for the working pattern returns a copy, so no caller can mutate the internal list
  through the reference it is handed. This is the encapsulation rule “readable from outside,
  never invalid from outside”.
* **`List<DayOfWeek>` for the working days.** The days are a fixed collection of values but
  they are displayed in order and must not repeat, so an ordered `List` with duplicate
  rejection in the constructor is the natural fit. `java.time.DayOfWeek` gives real day
  values instead of loose strings.
* **Two constructors.** The second takes only an ID and a name and applies the clinic’s
  default Monday–Friday pattern, because most professionals work a standard week. That
  removes a repetitive argument from the common case without losing the explicit form.
* **`toString()` is overridden** so one line of code — `System.out.println(doctor)` — prints
  a readable block instead of a hash code.
* **The class is `abstract`.** A professional with no specialty should not be creatable, so
  the compiler itself blocks `new HealthProfessional(...)` and every subtype must answer
  `getProfessionalType()`, `getTypeSpecificDetails()` and `performPrimaryDuty()`.
* **`equals`/`hashCode` on the ID.** Two objects that describe the same person are the same
  professional; Part 5 depends on this to detect a double booking.

### Part 2 — Specialising health professionals

* **Inheritance, not copy-and-paste.** Each subtype calls `super(id, name, days)` and adds
  only its own fields, so the shared data and its validation exist once.
* **Four required types plus one more:** `GeneralPractitioner`, `Cardiologist`, `Surgeon`,
  `Dietitian` and `Nurse` — five concrete types, all distinct in both data and behaviour.
* **The base constructor does the shared validation; the subclass constructor validates only
  its own extra fields** (room, theatre, ward, non-negative years). This keeps each rule next
  to the data it protects and guarantees both layers are set up on every construction path.
* **Each type keeps its own second constructor** with sensible defaults (a new GP accepts new
  patients, a new dietitian has zero years practising, a specialist works Mon/Wed), which
  shows the overloading idea being useful beyond the base class.
* **`toString()` is built in the base and completed by the subtype.** The base prints type,
  ID, name and working days, then appends `getTypeSpecificDetails()`; the subclass supplies
  only the part that is new, so the printing logic is written once.
* **`performPrimaryDuty()` is abstract**, so a mixed collection can ask every object the same
  question and get five different answers — polymorphism rather than `if`/`else` on type.
* **Why an abstract class rather than an interface:** the subtypes share *state* as well as
  behaviour, and the shared validation has to run for every subtype. An abstract class gives
  both a single inherited implementation; an interface could not hold the fields or the
  protected helpers.

### Part 3 — Working with health professional objects

* **One `List<HealthProfessional>` holds all five types**, so the printing loop and the duty
  loop are each written once, and adding a sixth type changes no loop at all.
* **A new type costs one class plus one `add` line.** Nothing in the printing code names a
  concrete type.
* **Type-specific actions are reached by an explicit cast** only where the action genuinely
  belongs to one type (`prescribeMedication`, `requestElectrocardiogram`, …). This is
  deliberate: the collection stays polymorphic, and the demo shows the narrow place where a
  cast is legitimate.

### Part 4 — Modelling an appointment

* **`Patient` is its own class.** The patient has its own validity rules (name, mobile
  number), and the same patient may hold several appointments, so the data is modelled once
  and reused instead of being copied into every appointment.
* **`Appointment` holds a `Patient`, a `HealthProfessional` and a `LocalTime`.** Because the
  field type is the base class, one appointment class serves every professional type with no
  casting and no subclassing of appointments.
* **The constructor refuses incomplete appointments** — null patient, null professional or
  null time all throw with a message, so an appointment can never exist half-built.
* **Time is a `LocalTime`, not a `String`.** `"08:30"` and `"10:00"` sort wrongly as text;
  a real time value compares correctly and also makes slot validation trivial.
* **`Comparable<Appointment>` compares by time**, so `Collections.sort(...)` orders a whole
  day’s list. Appointments at the same time are tie-broken by professional ID, which is
  permitted (“may appear in any order”) and keeps the demo output stable.

### Part 5 — Managing the collection of appointments

* **`ArrayList<Appointment>`** is the right structure here: the day’s list grows as bookings
  arrive, the whole list is read when staff view or sort it, entries are found by scanning a
  small collection, and cancellations remove by position through an `Iterator`. A `HashMap`
  keyed by time would fit exactly one professional and would need extra structures for the
  phone-number filter; a `TreeSet` would silently reject legitimate same-time bookings for
  different professionals.
* **The manager owns the list.** Callers receive sorted copies, never the internal list, so
  the collection cannot be corrupted from outside.
* **`addAppointment` validates in a fixed order:** missing appointment → time outside the
  timetable → professional already booked at that time → accept. Double booking is detected
  with `HealthProfessional.equals`, which compares IDs.
* **Every operation returns a `String` message**, prefixed `OK` or `WARNING`, instead of
  throwing or printing internally. The manager therefore stays reusable and the demonstration
  can show success and failure side by side.
* **The timetable is an object, not constants.** `ClinicSchedule` generates its slots from an
  opening time, a closing time and a slot length, and the manager asks it
  `isValidSlot(time)`. Changing the clinic to 09:00–17:00 in 20-minute slots is one constructor
  call; no other class changes.
* **Filters and sorting share one helper.** `findByProfessional` and `findByPatientPhone`
  build a subset and pass it through the same `sortedCopy` method, so ordering is guaranteed
  everywhere and implemented once.
* **Cancelling by phone removes all of that patient’s bookings** and reports how many were
  removed; cancelling by professional and time removes exactly one. A missing match returns a
  warning instead of failing silently.

### Part 6 — Working with appointments

The demonstration exercises normal and error paths for every operation: seven successful
bookings across all five professional types, a rejected double booking, a rejected time
outside the timetable, a rejected null appointment, two different filter arguments, a
cancellation by professional and time, a cancellation by phone, and three cancellations that
must fail. The final listing proves the collection changed.

---

## 4. Assumptions

1. Bookings are for the current day only, so no date is stored — `LocalDate.now()` is printed
   in the banner for context.
2. “08:00–16:00 in 30-minute intervals” means the last bookable slot starts at 15:30, giving
   16 slots per day.
3. A patient mobile number is an Australian mobile: ten digits starting with `04`. Spaces,
   hyphens and a leading `+` are tolerated on input, and the number is stored as digits.
4. Professional IDs are digits only and at most eight digits long.
5. Cancelling by patient mobile number cancels every appointment held under that number.
6. Professional availability days are recorded and printed but are not enforced when booking,
   because the brief fixes bookings to “the current day” and does not ask for a day check.

---

## 5. The four pillars in this code

| Pillar | Where it lives | What to say |
|---|---|---|
| **Encapsulation** | `HealthProfessional` fields are `private final`, validated in the constructor, exposed only through getters that copy the list; `Nurse.bloodSamplesCollected` can only change through `takeBloodSample`; `AppointmentManager` keeps its `ArrayList` private and hands back sorted copies. | The object’s state can only be changed through methods that enforce its rules, so no caller can put an object into an invalid state. |
| **Inheritance** | `GeneralPractitioner`, `Cardiologist`, `Surgeon`, `Dietitian`, `Nurse` all `extends HealthProfessional` and call `super(...)`. | The shared ID, name and working days, and their validation, are written once; each subtype adds only what is new. |
| **Polymorphism** | `List<HealthProfessional>` printed in one loop; `performPrimaryDuty()` and `toString()` overridden in every subtype; `Appointment` holds a `HealthProfessional` reference. | The same call produces five different results depending on the real object, and new types need no change in the calling code. |
| **Abstraction** | `HealthProfessional` is `abstract` with three abstract methods; `ClinicSchedule` hides slot generation behind `isValidSlot` / `getSlots`; `AppointmentManager` hides storage and searching behind add / find / cancel. | Callers deal with “what a professional does”, not “which class it is”; the compiler forbids a professional with no specialty. |

---

## 6. Code quality

* Javadoc on every class and public method, plus inline comments where a decision needs a
  reason rather than a description. Inline `//` comments also mark each validation guard,
  each step of the search and cancel loops, and each line a `toString` method builds.
* Consistent naming, small single-purpose methods, no duplicated validation.
* No libraries or language features beyond the unit material: `ArrayList`, `Collections`,
  `Iterator`, `List`, `Comparable`, `equals`/`hashCode`, inheritance, polymorphism,
  abstraction, `java.time` values, `StringBuilder`, `String.format`-style formatting.
