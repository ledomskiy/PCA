package net.java.jinterval.matrixutils;

import net.java.jinterval.interval.Interval;
import net.java.jinterval.interval.set.SetIntervalOps;
/**
 *
 * @author lpa
 */
public class ConverterMatrix {
    public static Interval[][] convertDoubleToInterval (Double [][] matrixDouble){
        int countRows = matrixDouble.length;
        int countColumns;
        
        if(countRows > 0){
            countColumns = matrixDouble[0].length;
        }else{
            countColumns = 0;
        }
        
        double infValue;
        double supValue;
        
        Interval [][] matrixInterval = new Interval[countRows][countColumns/2];
        
        for(int numRow=0; numRow<countRows; numRow++){
            for(int numColumn=0; numColumn<countColumns/2; numColumn++){
                infValue = matrixDouble [numRow][numColumn*2];
                supValue = matrixDouble [numRow][numColumn*2+1];
                
                matrixInterval [numRow][numColumn] = 
                        SetIntervalOps.nums2 (infValue, supValue);
            }
        }
        
        return matrixInterval;
    }
    
    public static Double[][] convertIntervalToDouble (Interval [][] matrixInterval){
        int countRows = matrixInterval.length;
        int countColumns;
        
        if(countRows > 0){
            countColumns = matrixInterval[0].length;
        }else{
            countColumns = 0;
        }
        
        double infValue;
        double supValue;
        
        Double [][] matrixDouble = new Double[countRows][countColumns*2];
        
        for(int numRow=0; numRow<countRows; numRow++){
            for(int numColumn=0; numColumn<countColumns; numColumn++){
                matrixDouble [numRow][numColumn*2] = matrixInterval [numRow][numColumn].doubleInf();
                matrixDouble [numRow][numColumn*2+1] = matrixInterval [numRow][numColumn].doubleSup();
            }
        }
        
        return matrixDouble;
    }
}
