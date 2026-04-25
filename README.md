# 🏆 Quiz Leaderboard System

## 📌 Overview

This project is developed as part of the **Bajaj Finserv Health Internship Assignment**.

The application interacts with an external Quiz Validator API, processes multiple responses, removes duplicate data, and generates an accurate leaderboard based on participant scores.

It simulates real-world distributed system behavior where duplicate API responses may occur, ensuring correct and reliable data processing.

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

## 🔗 API Details

**Base URL:**
https://devapigw.vidalhealthtpa.com/srm-quiz-task

**Endpoints Used:**

* `GET /quiz/messages?regNo={regNo}&poll={0-9}`
* `POST /quiz/submit`

---

## ⚙️ Tech Stack

* Java
* Maven
* REST API Integration
* Jackson (for JSON processing)

---

## 🔄 Workflow

### 1. API Polling

* The system calls the API **10 times** using poll values from `0 to 9`
* A **5-second delay** is maintained between each API call as required

### 2. Data Collection

* Stores all responses received from the API

### 3. Deduplication Logic

* Uses a unique key: `(roundId + participant)`
* A **HashSet** is used to track processed entries
* Ensures duplicate entries are ignored

### 4. Score Aggregation

* Calculates total score for each participant using a Map

### 5. Leaderboard Generation

* Sorts participants in **descending order of totalScore**

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
* Poll 3 → Alice (R1, 10) → Duplicate → Ignored

---

## 📊 Sample Output

```
=== FINAL LEADERBOARD ===

1. Bob - 120
2. Alice - 100

Total Score Across All Users: 220
```

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

3. Build the project:

```
mvn clean install
```

4. Run the application:

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
* Preventing double counting of scores
* Maintaining accurate aggregation across multiple polls

---

## ✅ Features

✔ Handles duplicate API responses
✔ Accurate score aggregation
✔ Sorted leaderboard generation
✔ Clean and modular code structure
✔ Basic exception handling for API failures

---

## 📌 Submission Details

* Public GitHub repository included
* Complete working implementation
* Follows all assignment constraints strictly

---

## 🙌 Author

**Manasa Jonnalagadda**
