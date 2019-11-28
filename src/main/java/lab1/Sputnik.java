package lab1;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class Sputnik
{
    private int number; //1row
    private int year;
    private int month;
    private int day;
    private int hours;
    private int minuts;
    private double seconds;
    private double CRS; //2row2
    private double deltn; //2row3
    private double M0; //2row4
    private double CUC; //3row1
    private double e0; //3row2
    private double CUS; //3row3
    private double SqrtA; //3row4
    private double TOE; //4row1
    private double CIC; //4row2
    private double Omega0; //4row3
    private double CIS; //4row4
    private double I0; //5row1
    private double CRC; //5row2
    private double omega; //5row3
    private double OMEGADOT; //5row4
    private double IDOT; //6row1

}