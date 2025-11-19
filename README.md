# ♠️ Blackjack Card Game (Java Swing)

## 📋 Project Overview

This project is an implementation of a multiplayer Blackjack (21) card game in Java with a graphical user interface (GUI) developed for the "Programming Fundamentals 3" course. Players play against a computer-controlled "Dealer" according to classic blackjack rules.

The goal of the project is not only to implement the game, but also to demonstrate modern software development principles (OOP, MVC, SOLID) and tools (Gradle, JUnit).

---

## 🚀 Features

### Gameplay:
1. Start a new game with any name.
2. Hit: Request a new card from the deck.
3. Stand: End the round with the current score.
4. Dealer AI: Automatic play (the Dealer must draw below 17 points).
Instant result announcement (Win/Lose/Draw/Bust).

### Interface:
1. Swing-based graphical interface.
2. Menu system for navigation.
3. Visual display of cards and scores.

### Persistence:
1. Saving the game state (.dat file).
2. Loading and continuing a previous game state.

---

## 🛠️ Technology Stack
* **Language: Java (JDK 21 recommended, min. JDK 17)**
* **Build System: Gradle**
* **GUI Framework: Java Swing**
* **Testing: JUnit 5**
* **Data Storage: Java Serialization API**

---

## 📂 Project Structure

The project follows the standard Gradle directory structure, with the source code divided into logical packages:
```
BlackjackProject/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── blackjack/
│   │   │       ├── Main.java
│   │   │       ├── gui/
│   │   │       │   └── GameFrame.java
│   │   │       ├── io/
│   │   │       │   └── SaveManager.java
│   │   │       ├── logic/
│   │   │       │   └── BlackjackGame.java
│   │   │       └── model/
│   │   │           ├── Card.java
│   │   │           ├── Dealer.java
│   │   │           ├── Deck.java
│   │   │           ├── Player.java
│   │   │           ├── Rank.java
│   │   │           └── Suit.java
│   │   └── resources/
│   │       └── card_images/
│   └── test/
│       └── java/
│           └── blackjack/
│               ├── io/
│               │   └── SaveManagerTest.java
│               ├── logic/
│               │   └── BlackjackGameTest.java
│               └── model/
│                   ├── Dealer.java
│                   ├── CardTest.java
│                   ├── DeckTest.java
│                   └── PlayerTest.java
└── saves/
    └── gamestate.dat

```

---

## 💻 Installation and Running

The project includes Gradle Wrapper, so there is no need to install Gradle in advance. Java Development Kit (JDK) 17 or later must be installed.

### Steps

#### Cloning/Downloading:
Download the source code to your computer.

#### Compiling and Testing:
Open a terminal in the project root and run:
**Windows:**
```
gradlew build
```

**Linux/Mac:**
```
./gradlew build
```

#### Running:
**Windows:**
```
gradlew run
```

**Linux/Mac:**
```
./gradlew run
```

---

## 🧪 Test Plan

The project contains unit tests for critical business logic using JUnit 5.

#### Running the tests:
**Windows:**
```
gradlew test
```

**Linux/Mac:**
```
./gradlew test
```
