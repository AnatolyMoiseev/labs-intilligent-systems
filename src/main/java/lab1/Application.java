package lab1;

import java.io.IOException;
import java.util.Scanner;

public class Application {

    private static String inputTime() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите дату и время: ");

        return scanner.nextLine();
    }

    public static void main(String[] args) {
        try {
            CoordinatesCalculator.calculate(inputTime());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
