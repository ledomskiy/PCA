package net.java.jinterval.matrixutils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Scanner;
import net.java.jinterval.interval.Interval;
import net.java.jinterval.interval.set.SetIntervalOps;

/**
 *
 * @author lpa
 */
public class ReadMatrixFromCSVFile {
    //файл источник
    protected File sourceFile;
    //читатель
    protected FileReader fileReader;
    protected char symbolValueSeparator;
    protected char symbolLineSeparator;
    protected boolean containHeaders;
    protected boolean containRowName;
    protected String decimalPartSeparator;
    //Конструктор
    public ReadMatrixFromCSVFile(String pathToCSVFile) throws FileNotFoundException{
        this.symbolLineSeparator = '\n';
        this.symbolValueSeparator = ';';
        this.decimalPartSeparator = ",";
        this.containHeaders = true;
        this.containRowName = true;
        this.sourceFile = new File(pathToCSVFile);
        this.fileReader = new FileReader(sourceFile);
//        this.inputStreamReader = new InputStreamReader(sourceFile);
    }
    public ReadMatrixFromCSVFile(File sourceFile) throws FileNotFoundException{
        this.symbolLineSeparator = '\n';
        this.symbolValueSeparator = ';';
        this.containHeaders = true;
        this.sourceFile = sourceFile;
        this.fileReader = new FileReader(sourceFile);
    }

    public void setContainHeaders(boolean containHeaders) {
        this.containHeaders = containHeaders;
    }

    public void setDecimalPartSeparator(String decimalPartSeparator) {
        this.decimalPartSeparator = decimalPartSeparator;
    }

    public void setSymbolLineSeparator(char symbolLineSeparator) {
        this.symbolLineSeparator = symbolLineSeparator;
    }

    public void setSymbolValueSeparator(char symbolValueSeparator) {
        this.symbolValueSeparator = symbolValueSeparator;
    }
    
    public Interval[][] getMatrix(ArrayList<String> rowHeaders, ArrayList<String> columnHeaders) throws IOException, Exception{
        ArrayList<ArrayList<Interval>> result = new ArrayList<ArrayList<Interval>>();
        ArrayList<Interval> row = new ArrayList<Interval>();
        //количество столбцов в datatable
        Integer countColumnsDatatable = null;
        //текущий прочитанный символ
        int ch;
        //буфер для хранения текущего значения
        String buffer = "";
        //если файл содержит заголовки, пропускам первую строку
        
        Scanner scanner = new Scanner(sourceFile,"UTF8");
        String lineDelimiters = "";
        lineDelimiters = lineDelimiters + symbolLineSeparator;
        scanner.useDelimiter(lineDelimiters);
        if(containHeaders){
            buffer = scanner.nextLine();
            String[] columnHeadersArray;
            columnHeadersArray = buffer.split(";");
            for(int i=0; i<columnHeadersArray.length; i++){
                columnHeaders.add(columnHeadersArray[i]);
            }
        }
        
        
        ///// НЕКОРРЕКТНО ЧИТАЮТСЯ РУССКИЕ СИМВОЛЫ, НУЖНО РАЗОБРАТЬСЯ С КОДИРОВКОЙ!!!
        if(containHeaders){
            do{
                ch = fileReader.read();                
                if((char)ch == symbolValueSeparator || (char)ch == symbolLineSeparator){
//                    System.out.println((char)buffer.getBytes("Cp1251")[0]);
                    buffer = "";
                }else{
                    buffer = buffer + (char)ch;
                }
                
//                System.out.print((char)ch);
            }while(ch > 0 && (char)ch != symbolLineSeparator);
            
        }
        
        
        //
        int countReadedValuesInLine = 0;
        boolean rowNameReaded = false;
        //для хранения начала и конца интервала
        double infValue = 0.0;
        double supValue = 0.0;
        buffer = "";
        //читаем до конца файла
        do{
            ch = fileReader.read();
            //если для каждой строки есть название и оно еще не прочитано
            if(containRowName == true && rowNameReaded == false){
                buffer = "";
                //читаем до тех пор, пока не считаем символ разделитель
                while((char)ch != symbolValueSeparator && ch > 0){
                    buffer = buffer + (char)ch;
                    ch = fileReader.read();
                }
                //гавнокод - подумал что сработает, непонятно зачем и почему... переписать на досуге!!!
                if(ch > 0){
//                    System.out.println("row name is " + buffer);
                    rowHeaders.add(buffer);
                }
                //говорим что название строки считано
                buffer = "";
                rowNameReaded = true;
                continue;                
            }
            //если считанный символ = символу разделителю значений
            if((char)ch == symbolValueSeparator){
                countReadedValuesInLine++;
                //добавляем значение
                if(countReadedValuesInLine%2==1){
                    buffer = buffer.replace(decimalPartSeparator, ".");                
                    infValue = Double.parseDouble(buffer);
                }else{
                    buffer = buffer.replace(",", ".");                
                    supValue = Double.parseDouble(buffer);
                    row.add(SetIntervalOps.nums2(infValue, supValue));                    
                }
                buffer = "";
                continue;
            }
            //если началась новая строка данных
            if((char)ch == symbolLineSeparator || ch <= 0){
                countReadedValuesInLine++;
                //известно ли количество значений в строке
                if(countColumnsDatatable == null){
                    //проверяем, что считано четное количество значений (интервал задается 2-мя значениями)
                    if(countReadedValuesInLine%2 == 1){
                        throw new Exception("An odd number of values!");
                    }else{
                        countColumnsDatatable = countReadedValuesInLine/2;
                    }                    
                }else{
                //проверяем что считано необходимое количество значений
                    if(countReadedValuesInLine%2 == 1 || countReadedValuesInLine/2 != countColumnsDatatable){
                        throw new Exception("The number of values ​​does not match the number of columns!");
                    }
                }
                //если все проверки прошли
                buffer = buffer.replace(decimalPartSeparator, ".");                
                supValue = Double.parseDouble(buffer);
                row.add(SetIntervalOps.nums2(infValue, supValue));
                //добавляем строку данных в DataTable
                result.add(row);
                
                buffer = "";
                countReadedValuesInLine = 0;
                rowNameReaded = false;
                row = new ArrayList<Interval>();
            }
            buffer = buffer + (char)ch;
        }while(ch > 0);
        
        //теперь читаем строки 
        
        //размеры матрицы
        int countColumns;
        int countRows;
        countColumns = result.get(0).size();
        countRows = result.size();
        Interval[][] resultMatrix = new Interval[countRows][countColumns];
        //забиваем данные в результирующий массив
        for(int i=0; i< countRows; i++){
            for(int j=0; j < countColumns; j++){
                resultMatrix[i][j] = result.get(i).get(j);
            }
        }       
        return resultMatrix;
    }
    
    
}