# Spotify API Authentication Testing Framework

This framework is designed to test multiple authentication flows and functionalities of the Spotify API. It covers a variety of use cases such as reading playlists, retrieving artist and song data, uploading files, creating playlists, adding songs, and more. The framework utilizes several tools and libraries to automate, validate, and report the test cases effectively.

## Tools and Technologies Used
- **Rest Assured**: For testing REST APIs and interacting with the Spotify API.
- **Playwright (Java)**: For automating the process of logging in and obtaining authorization codes through headless browser automation.
- **TestNG**: For test execution and organizing tests into test suites.
- **Jackson**: For working with JSON data (serialization and deserialization).
- **ExtentSparkReporter**: For generating detailed HTML reports after each test run.
- **Java Utilities**: For handling common tasks such as data manipulation, dynamic payload creation, and file management.

## Framework Flow Overview

### 1. **Obtain Authorization Code**
   - Use **Playwright (Java)** in headless mode to interact with the Spotify login page.
   - The system is directed to a specific URL containing user credentials and required scopes.
   - After successful redirection, the code is parsed from the URL.

### 2. **Obtain Access Token**
   - Send the parsed authorization code, along with your API key and secret, to obtain the access token for authenticating requests to the Spotify API.

### 3. **Test Flow Execution**
   - The framework executes positive test scenarios, with some dependencies between methods to simulate real-world usage.
   - Hard assertions are used to validate expected results throughout the tests.
   - Utility actions such as dynamic payload creation and data transfer between tests are handled automatically.

### 4. **Reporting**
   - **Extent Reports** are generated after each test run. The report is saved as an HTML file, containing details about the test execution, success/failure results, and logs for debugging.

## Setup Instructions

### Prerequisites
- **Java 11+**: Ensure that you have Java 11 or later installed.
- **Maven**: The project uses Maven for dependency management.
- **Spotify Developer Account**: You'll need a Spotify developer account and an API key/secret to interact with the Spotify API.
- **IDE**: Use an IDE like IntelliJ IDEA or Eclipse to open and run the project.



