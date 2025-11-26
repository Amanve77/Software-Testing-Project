# Equipment Rental Manager

## Project Overview

This is a Command-Line Interface (CLI) application for managing an equipment rental business. The system provides separate portals for managers and customers, allowing comprehensive management of equipment inventory, rentals, reservations, and financial tracking. All data is persisted in JSON format, making it a lightweight information system without requiring a database.

**Project Type**: Coursework Project for CS731 - Software Testing  
**Testing Strategy**: Mutation Testing using PIT (Pitest)  
**Total Lines of Code**: ~1378 lines (Only Source Code)  
**Test Cases**: 119 test methods across 17 test files

## Repository Information

**Complete Code Repository**: https://github.com/Amanve77/Software-Testing-Project.git

## Team Members and Contributions

**Team Member 1**: Aman Verma - MT2024020  
**Contributions**: 
- Implemented RentalService and comprehensive RentalServiceTest with boundary value, state-based, and exception testing
- Developed ReservationService and ReservationServiceTest covering reservation lifecycle and state transitions
- Created ReportService and ReportServiceTest for financial reporting and analytics
- Implemented complete data persistence layer (DataStore, JsonStore, AuditLog) with corresponding test cases
- Configured PIT mutation testing tool in pom.xml with optimal mutator selection and parallel execution
- Developed TestDataSupport infrastructure for test isolation and data management
- Implemented ManagerCLI interface and ManagerCLITest for manager portal functionality
- Authored project documentation (README.md and PROJECT_REPORT.md)

**Team Member 2**: Deomani Singh - MT2024040  
**Contributions**: 
- Implemented InventoryService and InventoryServiceTest with boundary value testing for stock management
- Developed CustomerService and CustomerServiceTest with validation and registration logic
- Created AuthService and AuthServiceTest for manager authentication
- Designed and implemented all domain models (Equipment, Customer, Rental, Reservation) with comprehensive domain tests
- Implemented CustomerCLI interface and CustomerCLITest for customer portal interactions
- Developed MainCLI and MainCLITest for application entry point and navigation
- Created App.java main class and AppTest for application initialization testing
- Implemented utility classes (ConsoleColors, ValidationUtils) with corresponding test cases

## Prerequisites

- JDK 11 or newer (bytecode target: Java 1.8 for compatibility)
- Apache Maven 3.8+
- Git (for cloning repository)

## Project Structure

```
EquipmentRentalManager/
├── src/
│   ├── main/java/org/equipment/
│   │   ├── App.java                    # Main entry point
│   │   ├── cli/                        # CLI interfaces
│   │   │   ├── MainCLI.java           # Main menu
│   │   │   ├── ManagerCLI.java        # Manager portal
│   │   │   └── CustomerCLI.java       # Customer portal
│   │   ├── data/                      # Data persistence layer
│   │   │   ├── DataStore.java         # Central data store
│   │   │   ├── JsonStore.java         # JSON I/O operations
│   │   │   └── AuditLog.java          # Audit logging
│   │   ├── domain/                    # Domain models
│   │   │   ├── Customer.java
│   │   │   ├── Equipment.java
│   │   │   ├── Rental.java
│   │   │   └── Reservation.java
│   │   ├── service/                   # Business logic services
│   │   │   ├── AuthService.java       # Authentication
│   │   │   ├── CustomerService.java
│   │   │   ├── InventoryService.java
│   │   │   ├── RentalService.java
│   │   │   ├── ReservationService.java
│   │   │   └── ReportService.java
│   │   └── utils/                     # Utility classes
│   │       ├── ConsoleColors.java
│   │       └── ValidationUtils.java
│   └── test/java/org/equipment/       # Test suite
│       └── [Corresponding test files]
├── data/                              # JSON data files
│   ├── customers.json
│   ├── equipment.json
│   ├── rentals.json
│   ├── reservations.json
│   └── audit.log
├── target/
│   ├── pit-reports/                   # Mutation testing reports
│   └── surefire-reports/             # Unit test reports
├── pom.xml                            # Maven configuration
└── README.md                          # This file
```

## Core Functionality

### Manager Portal Features
1. **Inventory Management**
   - List all equipment
   - Add new equipment
   - Update daily rates
   - Toggle maintenance status
   - Low stock alerts

2. **Rental Management**
   - View all rentals
   - Fulfill reservations
   - Decline reservations
   - Monitor overdue rentals

3. **Financial Reports**
   - Total held deposits
   - Late fees collected
   - Overdue rentals with projected fees
   - Category-wise overdue analysis
   - Top customers by rental count

4. **Audit Logging**
   - View complete audit trail of all system actions

### Customer Portal Features
1. **Equipment Browsing**
   - View available equipment catalog
   - Filter by availability

2. **Rental Operations**
   - Rent equipment
   - View personal rentals
   - Return rentals
   - Extend rental periods

3. **Reservation System**
   - Reserve equipment when out of stock
   - View reservation status
   - Automatic fulfillment when stock becomes available

## Build Instructions

### Compile and Package
```bash
mvn clean package
```

This command:
- Cleans previous builds
- Compiles source code
- Runs all unit tests
- Generates executable JAR: `target/EquipmentRentalManager-1.0-SNAPSHOT-shaded.jar`

### Run the Application
```bash
java -jar target/EquipmentRentalManager-1.0-SNAPSHOT-shaded.jar
```

The application will prompt you to choose between:
- Manager Portal (requires authentication)
- Customer Portal (select from existing customers)
- Exit

Seed data in `data/*.json` provides ready-made accounts and inventory for immediate testing.

## Testing

### Run Unit Tests
```bash
mvn test
```

Tests use isolated data directory (`target/test-data/`) and do not modify production data files.

### Mutation Testing with PIT

**Tool Used**: PIT (Pitest) 1.15.0  
**Command**:
```bash
mvn org.pitest:pitest-maven:mutationCoverage
```

Or using the makefile:
```bash
make pitest
```

**Configuration**:
- **Target Classes**: All classes in `org.equipment.*` packages
- **Target Tests**: All classes matching `*Test` pattern
- **Mutators Used**:
  - INCREMENTS
  - NEGATE_CONDITIONALS
  - MATH
  - CONDITIONALS_BOUNDARY
  - INVERT_NEGS
  - NON_VOID_METHOD_CALLS
  - VOID_METHOD_CALLS
- **Mutation Threshold**: 40% (minimum required)
- **Threads**: 4 (parallel execution)
- **Reports**: HTML format in `target/pit-reports/`

### Mutation Testing Results

**Overall Coverage**:
- **Line Coverage**: 91% (707/778 lines)
- **Mutation Coverage**: 90% (816/908 mutations killed)
- **Test Strength**: 98% (816/831 mutations killed by tests)

**Package-wise Breakdown**:
- `org.equipment`: 83% line, 100% mutation coverage
- `org.equipment.cli`: 85% line, 83% mutation coverage
- `org.equipment.data`: 90% line, 88% mutation coverage
- `org.equipment.domain`: 97% line, 98% mutation coverage
- `org.equipment.service`: 98% line, 96% mutation coverage
- `org.equipment.utils`: 89% line, 93% mutation coverage

**Detailed Reports**: Open `target/pit-reports/index.html` in a web browser for comprehensive mutation analysis.

## Test Case Design Strategy

### Testing Approach
1. **Unit Testing**: Comprehensive unit tests for all service classes, domain models, and utilities
2. **Mutation Testing**: Used to evaluate test quality and identify weak test cases
3. **Boundary Value Testing**: Applied to numeric inputs (rental days, rates, stock levels)
4. **Equivalence Partitioning**: Tested valid/invalid inputs, positive/negative scenarios
5. **State-Based Testing**: Verified state transitions (rental status, reservation status, maintenance flags)
6. **Exception Testing**: Validated error handling for invalid inputs and edge cases

### Test Coverage Highlights
- **119 test methods** covering all major functionality
- **Exception scenarios**: Invalid inputs, missing entities, business rule violations
- **State management**: Rental lifecycle, reservation workflow, equipment availability
- **Business logic**: Late fee calculations, deposit management, category capacity limits
- **Data persistence**: JSON read/write operations, audit logging

## Data Storage

### Default Data Directory
Data files are stored in `data/` directory:
- `customers.json`: Customer records
- `equipment.json`: Equipment catalog
- `rentals.json`: Active and historical rentals
- `reservations.json`: Equipment reservations
- `audit.log`: System audit trail

### Custom Data Directory
Override the data directory using system property:
```bash
java -Dequipment.data.dir=C:\temp\equipment-data -jar target/EquipmentRentalManager-1.0-SNAPSHOT-shaded.jar
```

### Test Data Isolation
Tests use `target/test-data/` to avoid modifying production data. Delete `target/` directory to reset to seed data.

## Dependencies

- **JUnit 4.13.2**: Unit testing framework
- **Jackson 2.17.1**: JSON serialization/deserialization
  - `jackson-databind`: Core JSON processing
  - `jackson-datatype-jsr310`: Java 8 time API support
- **PIT (Pitest) 1.15.0**: Mutation testing tool

## Execution Instructions

1. **Extract the archive**:
   ```bash
   tar -xzf <roll-number-1-2>.tar.gz
   cd EquipmentRentalManager
   ```

2. **Build the project**:
   ```bash
   mvn clean package
   ```

3. **Run unit tests**:
   ```bash
   mvn test
   ```

4. **Run mutation testing**:
   ```bash
   mvn org.pitest:pitest-maven:mutationCoverage
   ```

5. **View mutation reports**:
   Open `target/pit-reports/index.html` in a web browser

6. **Run the application**:
   ```bash
   java -jar target/EquipmentRentalManager-1.0-SNAPSHOT-shaded.jar
   ```

## Key Features Demonstrated

1. **Complete Functionality**: Full equipment rental management system with ~3000 lines of code
2. **Comprehensive Testing**: 119 test methods with 91% line coverage
3. **Mutation Testing**: 90% mutation coverage demonstrating high-quality test suite
4. **CLI Interface**: Interactive command-line interface for both managers and customers
5. **Data Persistence**: JSON-based storage without database dependency
6. **Business Logic**: Complex rules including late fees, capacity limits, maintenance states
7. **Audit Trail**: Complete logging of all system operations

## Notes

- The project uses Java 8 bytecode target for maximum compatibility
- All tests are isolated and do not interfere with production data
- Mutation testing reports are timestamped and can be regenerated
- The application is designed to be self-contained with no external database requirements
