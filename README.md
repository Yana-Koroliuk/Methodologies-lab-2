# Methodologies-lab-2

[![CI](https://github.com/Yana-Koroliuk/Methodologies-lab-2/actions/workflows/maven.yml/badge.svg)](https://github.com/Yana-Koroliuk/Methodologies-lab-2/actions/workflows/maven.yml)

This console application converts Markdown files into HTML or ANSI fragments. The generated markup can be output to standard output (stdout) or written to an output file if provided with the `--out` argument. With the proposed modification, the application supports the selection of output format through a command-line flag `--format=value`. Additionally, the application performs checks for common Markdown errors, such as nested or unbalanced markup tags. If it encounters incorrect Markdown due to nesting issues or unbalanced tags, it will output an error to the standard error (stderr) and terminate with a non-zero exit code.

## Prerequisites
> **NOTE:** You need to have Java installed on your system to run this application. Visit [Java's official website](https://www.java.com/download/) to download and install Java.

## Installation
1. Clone the repository from GitHub:
```bash
git clone https://github.com/Yana-Koroliuk/Methodologies-lab-2.git
cd Methodologies-lab-2
```
## Usage
1. Compile the Java files:
```bash
javac src/main/java/com/koroliuk/app/*.java
```
2. Run the compiled Main class:
> **NOTE:**
If the --out argument is omitted, output defaults to stdout. Without the --format flag, console output uses ANSI Escape Codes for formatting, while file output defaults to HTML.
```bash
java -cp src/main/java com.koroliuk.app.Main /path/to/markdownfile.txt --out /path/to/output.html --format=[html|ansi]
```

## Run tests
To execute the tests when you are in the root directory of the repository, enter the following command:
```bash
mvn test
```

## Failed CI commit
### [Failed CI commit](https://github.com/Yana-Koroliuk/Methodologies-lab-2/commit/056e76e630419fcb399697137a867a721feac639)

## Revert commit
### [Revert commit](https://github.com/Yana-Koroliuk/Methodologies-lab-2/commit/cca56bb516ee0cd1b8e8cc16e08a8cc9fd3e8ac0)

## Conclusion
In my opinion, writing unit tests significantly aided in thoroughly examining all cases of input data, eliminating the need to remember each scenario when improving the program's logic implementations. After writing the tests, I almost didn't perform manual testing for various input scenarios, which greatly facilitated the development process. Furthermore, as I aimed to achieve 100% code coverage with tests(what I did), these efforts helped me identify unreachable code. This occurred because the logic of that part had already been checked earlier. Additionally, seeing the percentage of code coverage by tests was extremely beneficial, as it helped assess the quality of the test cases. This approach not only streamlined the development process but also ensured a higher quality and reliability of the final product.
