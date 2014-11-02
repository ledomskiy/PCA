package net.java.jinterval.matrixutils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import net.java.jinterval.interval.Interval;
import net.java.jinterval.interval.set.SetIntervalOps;

/**
 *
 * @author lpa
 */
public abstract class AbstractGetMatrix {
    //источник
    protected InputStream source;
    //всякие разделители и т.д.
    //символ открытия интервала
    protected char symbolOpenInterval = '[';
    //символ закрытия интервала
    protected char symbolCloseInterval = ']';
    //символ-разделитель значений начала и конца интервала
    protected char symbolValueSeparate = ',';
    //символ перехода на новую строку
    protected char symbolEndOfLine = '\n';
    
    
    public Interval[][] getMatrix() throws IOException{
        ArrayList<ArrayList<Interval>> result = new ArrayList<ArrayList<Interval>>();
        //посимвольно парсим текст
        //текущий прочитанный символ из файла
        int ch;
        //какое значение считывается
        boolean infFlag = false;
        boolean supFlag = false;
        //буфер для чтения
        String buffer = "";
        //для хранения начала и конца интервала
        double infValue = 0.0;
        double supValue = 0.0;
        //количество столбцов в datatable
        Integer countColumnsDatatable = null;
        //для хранения строки интервалов
        ArrayList<Interval> row = new ArrayList<Interval>();
        do{
            //считываем очередной символ
            ch=source.read();
        
            //если началось занание интервала
            if((char)ch == symbolOpenInterval){
                //гопорим что считываем теперь начало интервала
                infFlag = true;
                continue;
            }
            //если закончили считывать начало интервала
            if(infFlag && (char)ch == symbolValueSeparate){
                infFlag = false;
                supFlag = true;
                //получаем из буфера double value
                infValue = Double.parseDouble(buffer);
                //опустошаем буфер
                buffer = "";
                continue;
            }
            //если закончилось задание интервала
            if(supFlag && (char)ch == symbolCloseInterval){
                supFlag = false;
                supValue = Double.parseDouble(buffer);
                row.add(SetIntervalOps.nums2(infValue, supValue));
                //опустошаем буфер
                buffer = "";
                continue;
            }            
            //если закончилась строка данных
            if((char)ch == symbolEndOfLine || ch <=0){
                //проверяем установлено ли количество столбцов в datatable
                if(countColumnsDatatable == null){
                    //смотрим считан ли хоть один интервал
                    if(row.size() != 0){
                        //устанавливаем количество столбцов в datatable
                        countColumnsDatatable = row.size();
                    }
                }
                //если количество столбцов в datatable установлено 
                //и количество столбцов в текущей строке совпадает с количеством 
                //столбцов в datatable
                if(countColumnsDatatable != null && countColumnsDatatable == row.size()){
                    //добавляем в таблицу
                    result.add(row);
                }
                row = new ArrayList<Interval>();
                continue;
            }
            //добавляем символ к буферу
            buffer = buffer + (char)ch;
        }while(ch > 0);
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

    public char getSymbolCloseInterval() {
        return symbolCloseInterval;
    }

    public char getSymbolOpenInterval() {
        return symbolOpenInterval;
    }

    public char getSymbolValueSeparate() {
        return symbolValueSeparate;
    }

    public char getSymbolEndOfLine() {
        return symbolEndOfLine;
    }

    public void setSymbolOpenInterval(char symbolOpenInterval) {
        this.symbolOpenInterval = symbolOpenInterval;
    }

    public void setSymbolCloseInterval(char symbolCloseInterval) {
        this.symbolCloseInterval = symbolCloseInterval;
    }

    public void setSymbolValueSeparate(char symbolValueSeparate) {
        this.symbolValueSeparate = symbolValueSeparate;
    }

    public void setSymbolEndOfLine(char symbolEndOfLine) {
        this.symbolEndOfLine = symbolEndOfLine;
    }

    public void setSource(InputStream source) {
        this.source = source;
    }
    
    
    
}