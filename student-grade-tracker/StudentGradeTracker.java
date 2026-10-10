import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** Console application for recording students' grades and viewing class statistics. */
public class StudentGradeTracker {
    private static final double MIN_GRADE = 0.0;
    private static final double MAX_GRADE = 100.0;

    private static final class Student {
        private final String name;
        private final List<Double> grades = new ArrayList<>();

        private Student(String name) { this.name = name; }
        private double average() { return grades.stream().mapToDouble(Double::doubleValue).average().orElse(0.0); }
        private double highest() { return grades.stream().mapToDouble(Double::doubleValue).max().orElse(0.0); }
        private double lowest() { return grades.stream().mapToDouble(Double::doubleValue).min().orElse(0.0); }
    }

    public static void main(String[] args) {
        List<Student> students = new ArrayList<>();
        try (Scanner input = new Scanner(System.in)) {
            System.out.println("=== Student Grade Tracker ===");
            while (true) {
                System.out.println("\n1. Add student and grades  2. Add grade to student  3. Summary report  4. Exit");
                int choice = readInt(input, "Choose an option: ", 1, 4);
                switch (choice) {
                    case 1 -> addStudent(input, students);
                    case 2 -> addGrade(input, students);
                    case 3 -> printReport(students);
                    case 4 -> { System.out.println("Goodbye."); return; }
                    default -> throw new IllegalStateException("Unexpected menu choice");
                }
            }
        }
    }

    private static void addStudent(Scanner input, List<Student> students) {
        String name = readNonBlank(input, "Student name: ");
        Student student = new Student(name);
        int count = readInt(input, "Number of grades to enter: ", 1, 100);
        for (int i = 1; i <= count; i++) student.grades.add(readGrade(input, "Grade " + i + " (0-100): "));
        students.add(student);
        System.out.println("Added " + name + " with " + count + " grade(s).");
    }

    private static void addGrade(Scanner input, List<Student> students) {
        if (students.isEmpty()) { System.out.println("Add a student first."); return; }
        listStudents(students);
        int index = readInt(input, "Student number: ", 1, students.size()) - 1;
        students.get(index).grades.add(readGrade(input, "Grade (0-100): "));
        System.out.println("Grade added.");
    }

    private static void printReport(List<Student> students) {
        System.out.println("\n=== Student Summary ===");
        if (students.isEmpty()) { System.out.println("No students recorded yet."); return; }
        System.out.printf("%-5s %-24s %8s %8s %8s %8s%n", "No.", "Student", "Count", "Average", "Highest", "Lowest");
        double classTotal = 0;
        int gradeCount = 0;
        double classHighest = Double.NEGATIVE_INFINITY, classLowest = Double.POSITIVE_INFINITY;
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            System.out.printf("%-5d %-24s %8d %8.2f %8.2f %8.2f%n", i + 1, s.name, s.grades.size(), s.average(), s.highest(), s.lowest());
            for (double grade : s.grades) { classTotal += grade; gradeCount++; }
            classHighest = Math.max(classHighest, s.highest());
            classLowest = Math.min(classLowest, s.lowest());
        }
        System.out.printf("Class average: %.2f | Highest grade: %.2f | Lowest grade: %.2f%n", classTotal / gradeCount, classHighest, classLowest);
    }

    private static void listStudents(List<Student> students) {
        for (int i = 0; i < students.size(); i++) System.out.printf("%d. %s%n", i + 1, students.get(i).name);
    }

    private static double readGrade(Scanner input, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(input.nextLine().trim());
                if (Double.isFinite(value) && value >= MIN_GRADE && value <= MAX_GRADE) return value;
            } catch (NumberFormatException ignored) { }
            System.out.println("Enter a number from 0 to 100.");
        }
    }

    private static int readInt(Scanner input, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(input.nextLine().trim());
                if (value >= min && value <= max) return value;
            } catch (NumberFormatException ignored) { }
            System.out.printf("Enter a whole number from %d to %d.%n", min, max);
        }
    }

    private static String readNonBlank(Scanner input, String prompt) {
        while (true) { System.out.print(prompt); String value = input.nextLine().trim(); if (!value.isEmpty()) return value; System.out.println("Name cannot be blank."); }
    }
}
