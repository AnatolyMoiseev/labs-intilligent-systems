import java.io.*;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GPSC
{
    private int[] inputTime;
    private Sputnik[] coordinates = new Sputnik[210];
    private int counter = 0;

    public GPSC(int[] time)
    {
        inputTime = time;
    }

    public void readFile() throws IOException {
        int j = -1;
        BufferedReader reader = new BufferedReader(new FileReader("zina2790.17n"));
        String line;
        while ((line = reader.readLine()) != null)
        {
            if (counter >= 7)
            {
                //string[] strings = line.Split(' ');
                if ((counter + 1) % 8 == 0) //1 row
                {
                    j++;
                    coordinates[j].setNumber(Integer.parseInt(line.substring(0, 2)));
                    coordinates[j].setYear(Integer.parseInt(line.substring(3, 2)));
                    coordinates[j].setMonth(Integer.parseInt(line.substring(6, 2)));
                    coordinates[j].setDay(Integer.parseInt(line.substring(9, 2)));
                    coordinates[j].setHours(Integer.parseInt(line.substring(12, 2)));
                    coordinates[j].setHours(Integer.parseInt(line.substring(15, 2)));
                    coordinates[j].setSeconds(Double.parseDouble(line.substring(18, 3).replace('.', ',')));
                }
                else if ((counter) % 8 == 0) //2 row
                {
                    coordinates[j].setCRS(Double.parseDouble(line.substring(22, 19).replace('.', ',').replace('D','E')));
                    coordinates[j].setDeltn(Double.parseDouble(line.substring(41, 19).replace('.', ',').replace('D','E')));
                    coordinates[j].setM0(Double.parseDouble(line.substring(60, 19).replace('.', ',').replace('D','E')));
                }
                else if ((counter - 1) % 8 == 0) //3 row
                {
                    coordinates[j].setCUC(Double.parseDouble(line.substring(3, 19).replace('.', ',').replace('D','E')));
                    coordinates[j].setE0(Double.parseDouble(line.substring(22, 19).replace('.', ',').replace('D','E')));
                    coordinates[j].setCUS(Double.parseDouble(line.substring(41, 19).replace('.', ',').replace('D','E')));
                    coordinates[j].setSqrtA(Math.pow(Double.parseDouble(line.substring(60, 19).replace('.', ',').replace('D','E')), 2));
                }
                else if ((counter - 2) % 8 == 0) //4 row
                {
                    coordinates[j].setTOE(Double.parseDouble(line.substring(3, 19).replace('.', ',').replace('D','E')));
                    coordinates[j].setCIC(Double.parseDouble(line.substring(22, 19).replace('.', ',').replace('D','E')));
                    coordinates[j].setOmega0(Double.parseDouble(line.substring(41, 19).replace('.', ',').replace('D','E')));
                    coordinates[j].setCIS(Double.parseDouble(line.substring(60, 19).replace('.', ',').replace('D','E')));
                }
                else if ((counter - 3) % 8 == 0) //5 row
                {
                    coordinates[j].setI0(Double.parseDouble(line.substring(3, 19).replace('.', ',').replace('D','E')));
                    coordinates[j].setCRC(Double.parseDouble(line.substring(22, 19).replace('.', ',').replace('D','E')));
                    coordinates[j].setOmega(Double.parseDouble(line.substring(41, 19).replace('.', ',').replace('D','E')));
                    coordinates[j].setOMEGADOT(Double.parseDouble(line.substring(60, 19).replace('.', ',').replace('D','E')));
                }
                else if ((counter - 3) % 8 == 0) //6 row
                {
                    coordinates[j].setIDOT(Double.parseDouble(line.substring(3, 19).replace('.', ',').replace('D','E')));
                }
            }
            counter++;
        }
        counter = j;
        reader.close();

    }

    public void clearCoordinates()
    {
        for (int i = 0; i < 100; i++)
            coordinates[i].setNumber(0);
    }
}
