# 🏆 Quiz Leaderboard System

## 📌 Overview

This project is developed as part of the Bajaj Finserv Health Internship Assignment.

The application interacts with an external Quiz Validator API, processes multiple responses, removes duplicate data, and generates a final leaderboard based on participant scores.

The system handles duplicate API responses to simulate real-world distributed system behavior and ensures accurate score computation.

---

## 🎯 Objective

* Poll API multiple times (0–9)
* Collect all responses
* Remove duplicate entries using `(roundId + participant)`
* Calculate total scores per participant
* Generate a sorted leaderboard
* Compute total score of all participants
* Submit results back to API

---

## ⚙️ Tech Stack

* Java
* Maven
* REST API Integration

---

## 🔄 Workflow

### 1. API Polling

* The system calls the API 10 times using poll values from 0 to 9
* A delay of 5 seconds is maintained between each API call as required

### 2. Data Collection

* Stores all responses received from API

### 3. Deduplication Logic

* Uses a unique key: `(roundId + participant)`
* A Set/Map is used to track unique combinations
* Ensures duplicate entries are ignored

### 4. Score Aggregation

* Calculates total score for each participant

### 5. Leaderboard Generation

* Sorts participants in descending order of totalScore

### 6. Total Score Calculation

* Computes combined score of all participants

### 7. Submission

* Sends final leaderboard to API endpoint (only once)

---

## 🧠 Key Logic

### Deduplication Strategy

```
Unique Key = roundId + participant
```

### Example

* Poll 1 → Alice (R1, 10)
* Poll 3 → Alice (R1, 10) → Duplicate ❌ Ignored

---

## 📊 Sample Output

Leaderboard:

1. Bob - 120
2. Alice - 100

Total Score: 220

---

## 🚀 How to Run

1. Clone the repository:

```
git clone https://github.com/manasa-jonnalagadda/quiz-leaderboard.git
```

2. Navigate to project:

```
cd quiz-leaderboard
```

3. Build project:

```
mvn clean install
```

4. Run application:

```
java -jar target/quiz-leaderboard-1.0-SNAPSHOT.jar
```

---

## 📁 Project Structure

```
quiz-leaderboard/
│── src/
│── pom.xml
│── README.md
│── .gitignore
```

---

## 🧩 Challenges Faced

* Handling duplicate API responses correctly
* Avoiding double counting of scores
* Maintaining accurate aggregation across multiple polls

---

## ✅ Features

✔ Handles duplicate API responses
✔ Accurate score aggregation
✔ Sorted leaderboard generation
✔ Clean and modular code structure

---

## 📌 Submission Details

* Public GitHub Repository included
* Complete working code
* Detailed documentation provided

---

## 🙌 Author

Manasa Jonnalagadda
