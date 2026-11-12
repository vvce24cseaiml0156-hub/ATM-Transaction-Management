# Java-Based ATM Transaction Management Application

A complete, production-grade Java-based ATM Transaction Management system featuring comprehensive **JUnit 5** automated testing, optimized multi-stage **Docker** containerization, and a robust **GitHub Actions CI/CD** pipeline deploying directly to **Docker Hub**.

---

## 🚀 Key Features

- **Balance Enquiry**: Real-time account balance retrieval with audit trail recording.
- **Cash Deposit**: Validated credit transactions with instantaneous balance updates.
- **Cash Withdrawal**: Secure cash disbursement with strict fund verification.
- **Transaction Validation**: Strict guard against invalid transaction amounts (zero, negative, non-numeric).
- **Insufficient Balance Handling**: Immediate rejection of withdrawals exceeding balance, preserving account integrity.
- **Audit Logging**: Comprehensive transaction history tracking every attempt, successful or failed.
- **Modular Object-Oriented Architecture**: Clean separation of concerns across models, service interfaces, exceptions, and ATM terminal controllers.

---

## 🏛️ Architecture & Project Structure

```
ATM-Transaction-Management/
├── .github/
│   └── workflows/
│       └── ci-cd.yml                # Automated CI/CD Pipeline (Build, Test, Docker Build & Push)
├── src/
│   ├── main/
│   │   └── java/
│   │       └── com/
│   │           └── atm/
│   │               ├── ATM.java                        # ATM Terminal Session Controller
│   │               ├── Main.java                       # Application Entry Point & Demo Runner
│   │               ├── exception/
│   │               │   ├── AccountNotFoundException.java
│   │               │   ├── ATMException.java
│   │               │   ├── InsufficientBalanceException.java
│   │               │   ├── InvalidAmountException.java
│   │               │   └── InvalidPinException.java
│   │               ├── model/
│   │               │   ├── Account.java                # Bank Account Domain Model
│   │               │   ├── Transaction.java            # Immutable Transaction Record
│   │               │   ├── TransactionStatus.java      # SUCCESS, FAILED, REJECTED
│   │               │   └── TransactionType.java        # DEPOSIT, WITHDRAWAL, BALANCE_ENQUIRY
│   │               └── service/
│   │                   ├── ATMService.java             # Core Banking Service Contract
│   │                   └── ATMServiceImpl.java         # Thread-safe Service Implementation
│   └── test/
│       └── java/
│           └── com/
│               └── atm/
│                   ├── ATMTest.java                    # Terminal Lifecycle & Session Tests
│                   ├── model/
│                   │   └── AccountTest.java            # Account & Balance Domain Tests
│                   └── service/
│                       └── ATMServiceTest.java         # Core Banking Service Layer Tests
├── Dockerfile                       # Multi-stage build & lightweight alpine runtime
├── pom.xml                          # Maven build configuration & JUnit 5 dependencies
├── .gitignore                       # Git exclusions
└── README.md                        # Documentation
```

---

## 🧪 Automated Testing (JUnit 5)

The test suite covers all functional and edge-case requirements across unit and integration levels:

| Test Class | Test Target | Key Scenarios Verified |
| :--- | :--- | :--- |
| `AccountTest` | Domain Logic & Balances | Initial balance check, deposit update, withdrawal deduction, insufficient balance rejection, negative amount rejection, zero amount rejection, immutability of balance on failure. |
| `ATMServiceTest` | Service Layer & Security | PIN authentication, invalid credentials, account lookup, transaction execution, transaction history recording. |
| `ATMTest` | ATM Terminal Controller | End-to-end user workflow: unauthenticated access rejection, card insertion, balance enquiry, deposit, withdrawal, statement printing, and card ejection. |

### Running Tests Locally
```bash
mvn clean test
```

Surefire XML test reports are automatically generated in `target/surefire-reports/`.

---

## 🛠️ Building the Application

### Prerequisites
- Java JDK 17 or higher
- Apache Maven 3.8+

### Compile & Package
```bash
mvn clean package
```
Generates the executable JAR: `target/atm-transaction-management.jar`.

### Run the Application
```bash
java -jar target/atm-transaction-management.jar
```

---

## 🐳 Docker Deployment

The application utilizes a multi-stage Docker build:
1. **Build Stage (`maven:3.9.6-eclipse-temurin-17`)**: Compiles code, executes all JUnit 5 tests, and packages the JAR.
2. **Runtime Stage (`eclipse-temurin:17-jre-alpine`)**: Minimal JRE image running under a non-root unprivileged security profile (`atmuser`).

### Build Local Image
```bash
docker build -t atm-transaction-management:latest .
```

### Run Docker Container
```bash
docker run --rm atm-transaction-management:latest
```

---

## 🔄 GitHub Actions CI/CD Pipeline

The `.github/workflows/ci-cd.yml` pipeline triggers on pushes to the `main` branch.

### Pipeline Execution Flow:
1. **Checkout Code**: Fetches source code from repository.
2. **Java JDK 17 Setup**: Configures Eclipse Temurin OpenJDK 17 with Maven caching.
3. **Compile**: Executes `mvn clean compile`.
4. **Run JUnit 5 Tests**: Executes `mvn test`.
5. **Publish Test Reports**: Archives test execution reports as artifacts.
6. **Package JAR**: Builds standalone executable artifact.
7. **Docker Buildx Setup**: Configures builder for container optimization.
8. **Docker Hub Login**: Authenticates securely using GitHub Secrets.
9. **Build & Tag Docker Image**: Tags with `latest` and git commit SHA.
10. **Push to Docker Hub**: Publishes image to Docker Hub repository.

### Required GitHub Secrets
To enable Docker Hub deployment in GitHub Actions, configure repository secrets under **Settings > Secrets and variables > Actions**:
- `DOCKERHUB_USERNAME`: Your Docker Hub username.
- `DOCKERHUB_TOKEN`: Your Docker Hub personal access token (PAT).

---

## 📄 License
This project is licensed under the MIT License.
