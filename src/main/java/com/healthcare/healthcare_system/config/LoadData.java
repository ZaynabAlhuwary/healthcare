package com.healthcare.healthcare_system.config;

import com.healthcare.healthcare_system.enums.FacilityType;
import com.healthcare.healthcare_system.model.Facility;
import com.healthcare.healthcare_system.model.Patient;
import com.healthcare.healthcare_system.repository.FacilityRepository;
import com.healthcare.healthcare_system.repository.PatientRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!test") // Exclude from tests
public class LoadData implements CommandLineRunner {

    private final FacilityRepository facilityRepository;
    private final PatientRepository patientRepository;
    private final Random random = new Random();

    @Override
    public void run(String... args) throws Exception {
        // Wait for database to be ready
        boolean connected = false;
        int attempts = 0;
        final int maxAttempts = 10;
        final long delayMs = 5000;

        while (!connected && attempts < maxAttempts) {
            try {
                if (facilityRepository.count() == 0 && patientRepository.count() == 0) {
                    loadInitialData();
                }
                connected = true;
                log.info("Database initialization completed successfully");
            } catch (Exception e) {
                attempts++;
                log.warn("Database connection attempt {} failed. Retrying in {} seconds...",
                        attempts, TimeUnit.MILLISECONDS.toSeconds(delayMs));
                log.warn("Error: {}", e.getMessage());
                Thread.sleep(delayMs);
            }
        }

        if (!connected) {
            log.error("Failed to connect to database after {} attempts", maxAttempts);
        }
    }

    private void loadInitialData() {
        // Create facilities
        List<Facility> facilities = createFacilities();
        facilityRepository.saveAll(facilities);

        // Create patients for each facility
        facilities.forEach(facility -> {
            List<Patient> patients = createPatientsForFacility(facility);
            patientRepository.saveAll(patients);
        });
    }

    private List<Facility> createFacilities() {
        return Arrays.asList(
                Facility.builder()
                        .name("City General Hospital")
                        .type(FacilityType.HOSPITAL)
                        .address("123 Main St, Metropolis")
                        .build(),
                Facility.builder()
                        .name("Downtown Clinic")
                        .type(FacilityType.CLINIC)
                        .address("456 Oak Ave, Downtown")
                        .build(),
                Facility.builder()
                        .name("Sunrise Rehabilitation Center")
                        .type(FacilityType.REHABILITATION_CENTER)
                        .address("789 Pine Rd, Sunrise")
                        .build(),
                Facility.builder()
                        .name("Metro Diagnostic Center")
                        .type(FacilityType.DIAGNOSTIC_CENTER)
                        .address("321 Elm St, Metro")
                        .build(),
                Facility.builder()
                        .name("Greenfield Nursing Home")
                        .type(FacilityType.NURSING_HOME)
                        .address("654 Maple Dr, Greenfield")
                        .build(),
                Facility.builder()
                        .name("Central Urgent Care")
                        .type(FacilityType.URGENT_CARE)
                        .address("987 Cedar Ln, Central")
                        .build(),
                Facility.builder()
                        .name("Hillside Mental Health Facility")
                        .type(FacilityType.MENTAL_HEALTH_FACILITY)
                        .address("135 Hilltop Blvd, Hillside")
                        .build(),
                Facility.builder()
                        .name("Valley Specialty Center")
                        .type(FacilityType.SPECIALTY_CENTER)
                        .address("246 Valley View, Riverside")
                        .build(),
                Facility.builder()
                        .name("Coastal Dental Clinic")
                        .type(FacilityType.DENTAL_CLINIC)
                        .address("357 Ocean Dr, Coastal")
                        .build(),
                Facility.builder()
                        .name("Community Primary Care")
                        .type(FacilityType.PRIMARY_CARE)
                        .address("468 Community Way, Midtown")
                        .build()
        );
    }

    private List<Patient> createPatientsForFacility(Facility facility) {
        int patientCount = 10 + random.nextInt(40); // Each facility gets 10-50 patients

        List<String> firstNames = Arrays.asList(
                "John", "Mary", "Robert", "Jennifer", "Michael",
                "Linda", "William", "Elizabeth", "David", "Susan",
                "Richard", "Jessica", "Joseph", "Sarah", "Thomas",
                "Karen", "Charles", "Nancy", "Christopher", "Lisa"
        );

        List<String> lastNames = Arrays.asList(
                "Smith", "Johnson", "Williams", "Brown", "Jones",
                "Miller", "Davis", "Garcia", "Rodriguez", "Wilson",
                "Martinez", "Anderson", "Taylor", "Thomas", "Hernandez",
                "Moore", "Martin", "Jackson", "Thompson", "White"
        );

        List<String> streets = Arrays.asList(
                "Main St", "Oak Ave", "Pine Rd", "Elm St", "Maple Dr",
                "Cedar Ln", "Hilltop Blvd", "Valley View", "Ocean Dr", "Community Way"
        );

        List<String> cities = Arrays.asList(
                "Metropolis", "Downtown", "Sunrise", "Metro", "Greenfield",
                "Central", "Hillside", "Riverside", "Coastal", "Midtown"
        );

        List<String> genders = Arrays.asList("Male", "Female", "Other");

        List<Patient> patients = new ArrayList<>();

        for (int i = 0; i < patientCount; i++) {
            String firstName = firstNames.get(random.nextInt(firstNames.size()));
            String lastName = lastNames.get(random.nextInt(lastNames.size()));
            LocalDate dob = LocalDate.now()
                    .minusYears(20 + random.nextInt(60))
                    .minusMonths(random.nextInt(12))
                    .minusDays(random.nextInt(28));
            String gender = genders.get(random.nextInt(genders.size()));

            String address = (random.nextInt(1000) + 100) + " " +
                    streets.get(random.nextInt(streets.size())) + ", " +
                    cities.get(random.nextInt(cities.size()));

            String phoneNumber = String.format("(%03d) %03d-%04d",
                    random.nextInt(1000), random.nextInt(1000), random.nextInt(10000));

            String email = firstName.toLowerCase() + "." + lastName.toLowerCase() +
                    random.nextInt(100) + "@example.com";

            String insuranceNumber = "INS" + (1000000 + random.nextInt(9000000));

            patients.add(Patient.builder()
                    .facility(facility)
                    .firstName(firstName)
                    .lastName(lastName)
                    .dateOfBirth(dob)
                    .gender(gender)
                    .address(address)
                    .phoneNumber(phoneNumber)
                    .email(email)
                    .insuranceNumber(insuranceNumber)
                    .build());
        }

        return patients;
    }
}