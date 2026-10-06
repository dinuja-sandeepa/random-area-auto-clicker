# 🖱️ Random Area Auto Clicker

![Java](https://img.shields.io/badge/Java-8%2B-orange?logo=java)
![Platform](https://img.shields.io/badge/Platform-Windows-blue?logo=windows)
![License](https://img.shields.io/badge/License-MIT-green)
![Release](https://img.shields.io/github/v/release/dinuja-sandeepa/random-area-auto-clicker?color=brightgreen)

A modern, lightweight automated mouse clicking utility developed in Java. Unlike standard fixed-point auto clickers, this tool allows users to visually define an interactive screen bounding box and perform randomized clicks within that region with variable intervals, effectively simulating human-like input behavior.

---

## ✨ Features

- 🎯 **Visual Area Selection:** Click and drag directly on screen to set custom click boundaries.
- ⏱️ **Randomized Delays:** Set minimum and maximum delay thresholds (in seconds) to randomize time between clicks.
- ⌨️ **Global Hotkeys (via JNativeHook):**
  - **`F6`** — Start clicking
  - **`F7`** — Stop clicking
  - *Functions globally across your entire OS, even when minimized or out of focus.*
- 📊 **Real-Time Dashboard:** Live tracking of click counts, coordinates, last click location, and delay countdowns.
- 💾 **Automatic Persistence:** Automatically saves and restores your delay thresholds and boundary coordinates between sessions.
- 🎨 **Modern Swing UI:** Clean card-based user interface with status indicators.

---

## 🚀 Quick Download & Run (Windows)

No build tools or compiler required:

1. Head to the **[Latest Releases](https://github.com/dinuja-sandeepa/random-area-auto-clicker/releases)** page.
2. Download **`Random Auto Clicker.exe`**.
3. Double-click the file to launch.

> **Requirement:** Requires **Java Runtime Environment (JRE 8 or newer)** installed on your machine.

---

## 📖 How to Use

1. **Select Click Area:**
   - Click the **Select Area** button.
   - Click and drag across the screen to define the clickable region.
2. **Configure Intervals:**
   - Enter your **Min Delay (sec)** (e.g., `1.0`).
   - Enter your **Max Delay (sec)** (e.g., `5.0`).
3. **Execute:**
   - Press **`F6`** (or click **▶ Start**) to begin randomized clicking.
   - Press **`F7`** (or click **■ Stop**) to halt the process at any moment.

---

## 🛠️ Building from Source

### Prerequisites
- **JDK 8** or higher
- **Apache Maven**
- Git

### Build Instructions

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/dinuja-sandeepa/random-area-auto-clicker.git](https://github.com/dinuja-sandeepa/random-area-auto-clicker.git)
   cd random-area-auto-clicker

2. **Compile and package with Maven:**
   ```bash
   mvn clean package

3. **Run the generated JAR:**
   ```bash
   java -jar target/RandomAreaAutoClicker-1.0-SNAPSHOT.jar

## 🧩 Dependencies & Architecture

- **GUI Framework:** Java Swing
- **Native Keyboard Hook:** [JNativeHook](https://github.com/kwhat/jnativehook) — captures global OS keystrokes
- **Input Automation:** `java.awt.Robot`
- **Packaging:** Launch4j — for Windows executable wrapper generation

---

## 📄 License

This project is licensed under the **MIT License** — see the [LICENSE](LICENSE) file for details.
