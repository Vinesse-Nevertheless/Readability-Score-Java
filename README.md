# Readability Score Analyzer

A robust Java-based command-line tool designed to analyze the complexity of text documents. This application calculates multiple standardized readability indices to determine the approximate grade level and age appropriateness of a given text.

## 🚀 Features

- **Multi-Metric Analysis**: Supports four major readability algorithms:
  - **Automated Readability Index (ARI)**
  - **Flesch–Kincaid Grade Level (FK)**
  - **Simple Measure of Gobbledygook Index (SMOG)**
  - **Coleman–Liau Index (CL)**
- **Custom Syllabic Engine**: Implements a sophisticated syllable counter that handles English linguistic nuances such as silent 'e', diphthongs, and contextual 'y' logic.
- **Efficient Text Processing**: Utilizes advanced Regex (lookaheads/lookbehinds) to accurately split sentences and tokenize words while handling edge cases in punctuation.
- **Extensible Architecture**: Built using a **Dispatcher Pattern** to decouple formula logic from data collection and console presentation.

## 🛠️ Technical Highlights

### Design Patterns
To transition from a basic script to a professional application, this project utilizes:
*   **Single Responsibility Principle (SRP)**: Each readability index is isolated in its own class, ensuring that mathematical logic is never mixed with input parsing or output formatting.
*   **Dispatcher Architecture**: A central controller delegates calculation tasks based on user input, making the system easy to extend with new metrics in the future.
*   **Data Separation**: Result-to-age mapping is handled via optimized lookups, keeping the presentation layer (Printer class) clean and maintainable.

### Problem-Solving & Technical Rigor
A key challenge in this project was navigating contradictory documentation. The development process involved:
*   **Independent Verification**: Prioritizing formal algorithmic logic over flawed example outputs provided in project specifications.
*   **State Management**: Implementing a state-based syllabic evaluator to handle complex word structures without relying on external libraries.

## 📊 How It Works

The application follows a 3-step pipeline:
1.  **Ingestion**: Reads text from a local file and calculates core statistics (Characters, Words, Sentences, Syllables, and Polysyllables).
2.  **Calculation**: Prompts the user to select a specific index or calculate "all" metrics simultaneously.
3.  **Mapping**: Translates the resulting score into a recommended age range and grade level based on the standardized mapping table.

## 💻 Usage

1. Compile the Java files:
   ```bash
   javac *.java
   ```

2. Run the application with a text file as an argument:
   ```bash
   java Main input.txt
   ```

### Example Output
This output is from the example text file included in this project named exampleInput.txt.

Words: 137  
Sentences: 14  
Characters: 687  
Syllables: 210  
Polysyllables: 17

Enter the score you want to calculate (ARI, FK, SMOG, CL, all): all

Automated Readability Index: 7.08 (about 13-year-olds).  
Flesch–Kincaid readability tests: 6.31 (about 12-year-olds).  
Simple Measure of Gobbledygook: 9.42 (about 15-year-olds).  
Coleman–Liau index: 10.66 (about 16-year-olds).

This text should be understood in average by 14-year-olds.   

## License
This project is licensed under the [CC BY-NC 4.0](https://creativecommons.org/licenses/by-nc/4.0/) License - see the LICENSE file for details.
![Java](https://img.shields.io/badge/language-Java-orange)
![License](https://img.shields.io/badge/license-CC%20BY--NC%204.0-blue)
![AI-No-Training](https://img.shields.io/badge/AI-No--Training-red)
