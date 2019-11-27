import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.util.Scanner;

public class Application {

    private static String inputTime() {
        Scanner scanner = new Scanner(System.in);
        System.out.print("Введите дату: ");

        String suka = scanner.nextLine();

        return suka;
    }

    public static void main(String[] args) {
        try {
            CoordinatesCalculator.calculate(inputTime());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
