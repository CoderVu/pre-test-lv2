# Selenium Level 2 Pre-Test Assignment

## Project Overview
This project contains automated tests for the "Sortable Data Tables" functionality on the-internet.herokuapp.com website. The tests verify first row data validation and column sorting functionality.

## Prerequisites
- Java JDK 11 or higher
- Maven 3.6 or higher
- Chrome browser (latest version)
- Git

## Project Structure
```
pre-test-lv2/
├── src/
│   ├── main/java/org/example/
│   │   ├── config/          # WebDriver configuration
│   │   ├── constants/       # Test constants
│   │   ├── model/          # Data models
│   │   └── pages/          # Page Object Model classes
│   └── test/java/org/example/
│       ├── listeners/       # Test listeners
│       ├── report/         # Extent reports
│       ├── testdata/       # Test data
│       └── tests/          # Test classes
├── pom.xml                 # Maven dependencies
├── testng.xml             # TestNG configuration
└── README.md              # This file
```

## Setup Instructions

### 1. Clone the Repository
```bash
git clone https://github.com/CoderVu/pre-test-lv2.git
cd pre-test-lv2
```

### 2. Verify Prerequisites
```bash
# Check Java version
java -version

# Check Maven version
mvn -version
```

### 3. Install Dependencies
```bash
mvn clean install
```

## Running Tests

### Run All Tests
```bash
mvn clean test
```

### Run Specific Test Class
```bash
mvn test -Dtest=TestCase
```

### Run Specific Test Method
```bash
mvn test -Dtest=TestCase#verifyDataTableFirstRowAndColumnSorting
```

### Run Tests with Different Browser
```bash
# Chrome (default)
mvn test -Dbrowser=chrome

# Firefox
mvn test -Dbrowser=firefox

# Edge
mvn test -Dbrowser=edge
```

## Test Description

### Test Case: TC01 - Verify Data Table Functionality
**Description:** Verify first row data validation and column sorting

**Steps:**
1. Navigate to main page (https://the-internet.herokuapp.com)
2. Click on "Sortable Data Tables" link
3. Verify first row data matches expected values:
   - Last Name: Smith
   - First Name: John
   - Email: jsmith@gmail.com
   - Due: $50.00
   - Website: http://www.jsmith.com
4. Verify all columns can be sorted in ascending order
5. Verify all columns can be sorted in descending order

## Test Results

### Reports Location
- **Extent Reports:** `src/test/resources/extent-reports/`
- **TestNG Reports:** `target/surefire-reports/`
- **Screenshots:** `src/test/resources/` (on test failure)

### View Reports
1. Open the HTML report file in your browser
2. Navigate to the test results directory
3. Open `extent-report_YYYYMMDD_HHMMSS.html`

## Project Features

### Page Object Model (POM)
- **BasePage:** Common methods and utilities
- **DataTablesPage:** Page-specific methods and locators

### Test Framework
- **TestNG:** Test execution framework
- **Selenium WebDriver:** Browser automation
- **Extent Reports:** Detailed test reporting
- **Maven:** Build and dependency management

### Key Features
- ✅ Cross-browser support (Chrome, Firefox, Edge)
- ✅ Automatic screenshot capture on failure
- ✅ Detailed test reporting
- ✅ Page Object Model implementation
- ✅ Explicit waits and error handling
- ✅ Stale element handling with retry logic

## Troubleshooting

### Common Issues

#### 1. WebDriver Issues
```bash
# Update ChromeDriver
# Download latest ChromeDriver from: https://chromedriver.chromium.org/
# Place in system PATH or project directory
```

#### 2. Maven Dependencies
```bash
# Clean and reinstall dependencies
mvn clean install -U
```

#### 3. Browser Compatibility
- Ensure browser version matches WebDriver version
- Use latest browser versions for best compatibility

#### 4. Test Failures
- Check internet connection
- Verify website accessibility
- Review test logs in reports

## Configuration

### Browser Configuration
Edit `src/main/java/org/example/config/WebDriverConfig.java` to modify browser settings.

### Test Constants
Edit `src/main/java/org/example/constants/TestConstants.java` to modify:
- Base URL
- Timeouts
- Column names
- Screenshot paths

### Test Data
Edit `src/test/java/org/example/testdata/TestData.java` to modify expected test data.

## Contributing
1. Follow Page Object Model pattern
2. Add proper error handling
3. Include meaningful test descriptions
4. Update documentation for new features

## Contact
For questions or issues, please contact the development team.

---
**Note:** This project is designed for Selenium Level 2 pre-test assignment evaluation. 