package ru.itmo.qa.lab3;

public final class Main {
    private Main() {
    }

    public static void main(String[] args) {
        System.out.println("Lab 3 is configured for UI testing of Subscribe.ru.");
        System.out.println("Main documentation files:");
        System.out.println("- lab3/docs/use-case-diagram.puml");
        System.out.println("- lab3/docs/checklist.md");
        System.out.println("- lab3/docs/test-scenarios.md");
        System.out.println("- lab3/docs/test-results.md");
        System.out.println("Run './gradlew :lab3:uiTest' to execute Selenium tests.");
    }
}
