package lab1;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class CoordinatesCalculator {

    public static void calculate(String time) throws IOException {
        int[] inputTime = new int[6];
        String[] substrings = time.split(".");
        int i = 0;

        for (String substring : substrings) {
            inputTime[i] = Integer.parseInt(substring);
            i++;
        }

        System.out.println("Текущая дата: " + inputTime[0] + "." + inputTime[1] + "." + inputTime[2]);
        System.out.println("Текущее время: " + inputTime[3] + ":" + inputTime[4] + ":" + inputTime[5]);

        GPSC gps = new GPSC(inputTime);
        gps.readFile();
        int numberSp = 0;
        int minday = 100;
        int minhour = 100;
        int minminut = 100;
        double minsec = 100;
        for (i = 0; i < 210; i++) {
            if ((gps.getCoordinates()[i].getNumber() != 0) && (Math.abs(inputTime[0] - gps.getCoordinates()[i].getDay()) <= minday) && (Math.abs(inputTime[3] - gps.getCoordinates()[i].getHours()) <= minhour) && (Math.abs(inputTime[4] - gps.getCoordinates()[i].getMinuts()) <= minminut) && (Math.abs(inputTime[5] - gps.getCoordinates()[i].getSeconds()) < minsec)) {
                numberSp = gps.getCoordinates()[i].getNumber();
                minday = Math.abs(inputTime[0] - gps.getCoordinates()[i].getDay());
                minhour = Math.abs(inputTime[3] - gps.getCoordinates()[i].getHours());
                minminut = Math.abs(inputTime[4] - gps.getCoordinates()[i].getMinuts());
                minsec = Math.abs(inputTime[5] - gps.getCoordinates()[i].getSeconds());
            }
        }

        i = numberSp;
        System.out.println("Номер спутника: " + gps.getCoordinates()[i].getNumber());
        System.out.println("Дата: " + gps.getCoordinates()[i].getDay() + "." + gps.getCoordinates()[i].getMonth() + "." + gps.getCoordinates()[i].getYear());
        System.out.println("Время: " + gps.getCoordinates()[i].getHours() + ":" + gps.getCoordinates()[i].getMinuts() + ":" + gps.getCoordinates()[i].getSeconds());

        //Преобразовать время TPC во время t от начала GPS-недели
        int nday = (int) (gps.getCoordinates()[i].getTOE() / 86400);
        double t = nday * 86400 + inputTime[3] * 3600 + inputTime[4] * 60 + inputTime[5];
        //Вычислить момент tk от эпохи времени GPS, соответствующий расчетному времени t
        double tk = t - gps.getCoordinates()[i].getTOE();
        if (tk > 302400)
            tk -= 604800;
        else if (tk < -302400)
            tk += 604800;
        //Вычислить скорректированное среднее движение
        double m = 3.986005 * Math.pow(10, 14);
        double n = Math.sqrt(m / Math.pow(gps.getCoordinates()[i].getSqrtA(), 3)) + gps.getCoordinates()[i].getDeltn();
        //Определить текущее значение средней аномалии Mk в момент времени tk
        double Mk = gps.getCoordinates()[i].getM0() + n * tk;
        //Решить итеративным методом Ньютона уравнение Кеплера для эксцентричной аномалии Ek
        double eps = 0.00001;
        double EkOld = Mk;
        double EkNew = Mk;
        do {
            EkOld = EkNew;
            EkNew = EkOld + (Mk - EkOld + gps.getCoordinates()[i].getE0() * Math.sin(EkOld)) / (1 - gps.getCoordinates()[i].getE0() * Math.cos(EkOld));
        } while (Math.abs(EkNew - EkOld) > eps);
        //Вычислить производную Ek
        double Ek1 = n / (1 - gps.getCoordinates()[i].getE0() * Math.cos(EkNew));
        //Вычислить истинную аномалию
        double Tetta = Math.atan2((Math.sqrt(1 - Math.pow(gps.getCoordinates()[i].getE0(), 2)) * Math.sin(EkOld)), (Math.cos(EkOld) - gps.getCoordinates()[i].getE0()));
        //Вычислить аргумент широты
        double Fk = Tetta + gps.getCoordinates()[i].getOmega();
        //Вычислить производную аргумента широты
        double Fk1 = (Math.sqrt(1 - Math.pow(gps.getCoordinates()[i].getE0(), 2)) * Ek1) / (1 - gps.getCoordinates()[i].getE0() * Math.cos(EkOld));
        //Вычислить исправленный аргумент широты
        double DUk = gps.getCoordinates()[i].getCUC() * Math.cos(2 * Fk) + gps.getCoordinates()[i].getCUS() * Math.sin(2 * Fk);
        double Uk = Fk + DUk;
        //Вычислить производную исправленного аргумента широты
        double Uk1 = Fk1 * (1 + 2 * (gps.getCoordinates()[i].getCUC() * Math.cos(2 * Fk) + gps.getCoordinates()[i].getCUS() * Math.sin(2 * Fk)));
        //Определить текущее значение исправленного радиус-вектора
        double Drk = gps.getCoordinates()[i].getCRC() * Math.cos(2 * Fk) + gps.getCoordinates()[i].getCRS() * Math.sin(2 * Fk);
        double rk = gps.getCoordinates()[i].getSqrtA() * (1 - gps.getCoordinates()[i].getE0() * Math.cos(EkOld)) + Drk;
        //Вычислить производную радиус-вектора
        double rk1 = gps.getCoordinates()[i].getSqrtA() * gps.getCoordinates()[i].getE0() * Ek1 * Math.sin(EkOld) + 2 * Fk1 * (gps.getCoordinates()[i].getCRC() * Math.cos(2 * Fk) + gps.getCoordinates()[i].getCRS() * Math.sin(2 * Fk));
        //Определить исправленный угол наклона орбиты
        double DIk = gps.getCoordinates()[i].getCIC() * Math.cos(2 * Fk) + gps.getCoordinates()[i].getCIS() * Math.sin(2 * Fk);
        double Ik = gps.getCoordinates()[i].getI0() + DIk + gps.getCoordinates()[i].getIDOT() * tk;
        //Вычислить производную исправленного угла наклона орбиты
        double Ik1 = gps.getCoordinates()[i].getIDOT() + 2 * Fk1 * (gps.getCoordinates()[i].getCIS() * Math.cos(2 * Fk) + gps.getCoordinates()[i].getCIC() * Math.sin(2 * Fk));
        //Вычислить вектор расположения спутника
        double Xfk = rk * Math.cos(Uk);
        double Yfk = rk * Math.sin(Uk);
        double Xfk1 = rk1 * Math.cos(Uk) - Yfk * Uk1;
        double Yfk1 = rk1 * Math.sin(Uk) - Xfk * Uk1;
        //Вычислить исправленную долготу восходящего узла орбиты
        double wz = 7.2921151467 * Math.pow(10, -5);
        double OmegaK = gps.getCoordinates()[i].getOmega0() + (gps.getCoordinates()[i].getOMEGADOT() - wz) * tk - wz * gps.getCoordinates()[i].getTOE();
        //Вычислить производную долготы восходящего узла
        double OmegaK1 = gps.getCoordinates()[i].getOMEGADOT() - wz;
        //Вычислить координаты КА на момент времени tk
        double Xsvk = Xfk * Math.cos(OmegaK) - Yfk * Math.cos(Ik) * Math.sin(OmegaK);

        System.out.println("Координаты:");
        System.out.println("X: " + Xsvk);

        double Ysvk = Xfk * Math.sin(OmegaK) + Yfk * Math.cos(Ik) * Math.cos(OmegaK);

        System.out.println("Y: " + Ysvk);

        double Zsvk = Yfk * Math.sin(Ik);

        System.out.println("Z: " + Zsvk);

        //Вычислить скорости
        double Xsvk1 = -OmegaK1 * Ysvk + Xfk1 * Math.cos(OmegaK) - (Yfk1 * Math.cos(Ik) - Yfk * Ik1 * Math.sin(Ik)) * Math.sin(OmegaK);
        double Ysvk1 = -OmegaK1 * Xsvk + Xfk1 * Math.sin(OmegaK) + (Yfk1 * Math.cos(Ik) - Yfk * Ik1 * Math.sin(Ik)) * Math.cos(OmegaK);
        double Zsvk1 = Yfk * Ik1 * Math.sin(Ik) + Yfk1 * Math.sin(Ik);

        System.out.println("Скорости:");
        System.out.println("X: " + Xsvk1);
        System.out.println("Y: " + Ysvk1);
        System.out.println("Z: " + Zsvk1);

        BufferedReader readerOut = new BufferedReader(new FileReader("igr19695.sp3"));
        String line1;

        int k = -1;
        double Xcheck;
        double Ycheck;
        double Zcheck;
        while ((line1 = readerOut.readLine()) != null) {
            k++;
            if (k >= 23) {
                if (!line1.substring(0, 3).equals("EOF")) {
                    if (Integer.parseInt(line1.substring(2, 2)) == gps.getCoordinates()[i].getNumber()) {
                        Xcheck = Double.parseDouble(line1.substring(5, 13).replace('.', ',')) * 1000;
                        Ycheck = Double.parseDouble(line1.substring(19, 13).replace('.', ',')) * 1000;
                        Zcheck = Double.parseDouble(line1.substring(33, 13).replace('.', ',')) * 1000;
                        System.out.println("Проверка:");
                        System.out.println("X: " + Xcheck);
                        System.out.println("Y: " + Ycheck);
                        System.out.println("Z: " + Zcheck);
                        System.out.println("Модуль разности:");
                        System.out.println("X: " + Math.abs(Xsvk - Xcheck));
                        System.out.println("Y: " + Math.abs(Ysvk - Ycheck));
                        System.out.println("Z: " + Math.abs(Zsvk - Zcheck));
                        break;
                    }
                }
            }
        }
    }

}
