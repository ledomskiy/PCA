package net.java.jinterval.matrixutils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.NumberFormat;
import net.java.jinterval.interval.Interval;

/**
 *
 * @author lpa
 */
public class WriteMatrixIntoCSVFile {
    
    public static void writeScoresMatrix(String pathToFile, Interval[][] scoresMatrix, String[] rowHeaders, double[] contributionRates, String decimalPartDelimiter) throws IOException{
        //создаем файлики для выхлопа и пишем в них заголовки
        File outputFile = new File(pathToFile);
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        
        FileWriter fileWriter = new FileWriter(outputFile);
        String doubleInf = "";
        String doubleSup = "";
        //пишем заголовки
        if(rowHeaders != null && contributionRates != null){
            fileWriter.append("ExperimentNumber;ExperimentName");
        }
        if(scoresMatrix != null && scoresMatrix.length > 0){
            for(int i=0; i<scoresMatrix[0].length; i++){
                fileWriter.append(";PC"+(i+1) +" ("+nf.format(contributionRates[i])+"%)");
                fileWriter.append(";PC"+(i+1) +" ("+nf.format(contributionRates[i])+"%)");
            }
        }
        fileWriter.append("\n");
        fileWriter.flush();
        
        //записываем сами значения
        for(int i=0; i<scoresMatrix.length; i++){
            if(rowHeaders != null){
                fileWriter.append(""+(i+1));
                fileWriter.append(";");
                fileWriter.append(rowHeaders[i]);
                fileWriter.append(";");
            }
            doubleInf = "" + scoresMatrix[i][0].doubleInf();
            doubleSup = "" + scoresMatrix[i][0].doubleSup();
            doubleInf = doubleInf.replace(".", decimalPartDelimiter);
            doubleSup = doubleSup.replace(".", decimalPartDelimiter);
            fileWriter.append(doubleInf);
            fileWriter.append(";");
            fileWriter.append(doubleSup);
            fileWriter.flush();
//            fileWriter.append(";");
            for(int j=1; j<scoresMatrix[i].length; j++){
                doubleInf = "" + scoresMatrix[i][j].doubleInf();
                doubleSup = "" + scoresMatrix[i][j].doubleSup();
                fileWriter.append(";");
                fileWriter.append(doubleInf);
                fileWriter.append(";");
                fileWriter.append(doubleSup);
                fileWriter.flush();
            }
            fileWriter.append("\n");
            fileWriter.flush();
        }
        fileWriter.flush();
        fileWriter.close();
    }
    public static void writeLoadingsMatrix(String pathToFile, double[][] loadingsMatrix, String[] columnHeaders, String decimalPartDelimiter) throws IOException{
                
        //создаем файлики для выхлопа и пишем в них заголовки
        File outputFile = new File(pathToFile);
        FileWriter fileWriter = new FileWriter(outputFile);
        int shift = 0;
        String value;
        for(int i=0; i < loadingsMatrix.length; i++){
            //пишем заголовки, если они есть
            if(columnHeaders != null){
                shift = columnHeaders.length%2;
                fileWriter.append(columnHeaders[i*2+shift]+";");
            }
            //пишем первый элемент матрицы нагрузок
            value = "" + loadingsMatrix[i][0];
            value = value.replace(".", decimalPartDelimiter);
            fileWriter.append(value);
            for(int j=1; j<loadingsMatrix[i].length; j++){
                value = "" + loadingsMatrix[i][j];
                value = value.replace(".", decimalPartDelimiter);
                fileWriter.append(";");
                fileWriter.append(value);
            }
            fileWriter.append("\n");
            fileWriter.flush();           
        }
        fileWriter.close();
    }
        public static void writeLoadingsMatrix1(String pathToFile, double[][] loadingsMatrix, String[] columnHeaders, double[] contributionRates, String decimalPartDelimiter) throws IOException{
                
        //создаем файлики для выхлопа и пишем в них заголовки
        OutputStream os=new FileOutputStream(pathToFile);
        int shift = 0;
        String value;
        String buffer;
        
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(2);
        //пишем первую строчку с заголовками столбцов
//        buffer = "name;PC1 ("+nf.format(contributionRates[0])+"%);PC2 ("+nf.format(contributionRates[1])+"%)\n";
        buffer = "name";
        for(int i=0; i<loadingsMatrix[0].length; i++){
            buffer = buffer + ";PC"+(i+1)+" ("+nf.format(contributionRates[i])+"%)";
        }
        buffer = buffer + "\n";
        buffer.replace(",", ".");
        os.write(buffer.getBytes("Cp1251"));
        for(int i=0; i < loadingsMatrix.length; i++){
            //пишем заголовки, если они есть
            if(columnHeaders != null){
                //shift = columnHeaders.length%2;
                //buffer = columnHeaders[i*2+shift]+";";
                buffer = "" + (i+1)+";";
                os.write(buffer.getBytes("Cp1251"));
            }
            //пишем первый элемент матрицы нагрузок
            value = "" + loadingsMatrix[i][0];
            value = value.replace(".", decimalPartDelimiter);
            os.write(value.getBytes("Cp1251"));
            for(int j=1; j<loadingsMatrix[i].length; j++){
                value = "" + loadingsMatrix[i][j];
                value = value.replace(".", decimalPartDelimiter);
                value = ";" + value;
                os.write(value.getBytes("Cp1251"));
            }
            os.write("\n".getBytes("Cp1251"));
                       
        }
        os.close();
    }
    
}