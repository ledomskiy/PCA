package net.java.jinterval.pca;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import net.java.jinterval.interval.Interval;
import net.java.jinterval.interval.set.SetInterval;
import net.java.jinterval.interval.set.SetIntervalOps;
import net.java.jinterval.matrixutils.MatrixViewer;
import org.apache.commons.math3.exception.OutOfRangeException;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.EigenDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

/**
 *
 * @author lpa
 */
abstract public class AbstractPCA {
    //количество наблюдений
    protected int countRows;
    //размерность вектора признаков
    protected int countColumns;
    //таблица с исходными данными 
    protected Interval[][] datatable;
    //вектор дисперсий по столбцам
    protected RealVector dispersions;  
    //матрица счетов
    protected ArrayList<ArrayList<Interval>> scoresMatrix;
    //матрица нагрузок
    protected ArrayList<ArrayList<Double>> loadingsMatrix;
    
    //типы взвешивания
    public enum WeightingSchemes{
        EqualWeight,
        ProportionalVolume,
        InverselyProportionalVolume                        
    }
    //используемый тип взвешивания
    protected WeightingSchemes weightingScheme;
    //массив весов
    protected double[] weights;
    protected double[] expectedValuesColumns;
    //необходимый процент объяснения общей дисперсии главными компонентами
    protected Double comulativeContributionRate;
    protected double[] contributionRates;
    
    //Формат вывода чисел
    NumberFormat numberFormat;
    //конструктор, принимающий на вход datatable
    AbstractPCA(Interval[][] datatable){
        this.datatable = datatable;
        //количество наблюдений
        countRows = datatable.length;
        //если есть хоть одно наблюдение
        if(countRows > 0){
            countColumns = datatable[0].length;
            weights = new double[countRows];
        }else{
            countColumns = 0;
        }
        //устанавливаем схему взвешивания
        weightingScheme = WeightingSchemes.EqualWeight;
        //устанавливаем формат вывода чисел
        numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(3);
        numberFormat.setMinimumIntegerDigits(1);
        numberFormat.setMinimumFractionDigits(3);
        
    }
    AbstractPCA(Interval[][] datatable, WeightingSchemes weightingScheme){
        this.datatable = datatable;
        //количество наблюдений
        countRows = datatable.length;
        //если есть хоть одно наблюдение
        if(countRows > 0){
            countColumns = datatable[0].length;
            weights = new double[countRows];
        }else{
            countColumns = 0;
        }
        //устанавливаем схему взвешивания
        this.weightingScheme = weightingScheme;
        //weightingScheme = WeightingSchemes.ProportionalVolume;
//        weightingScheme = WeightingSchemes.InverselyProportionalVolume;
        //устанавливаем формат вывода чисел
        numberFormat = NumberFormat.getInstance();
        numberFormat.setMaximumFractionDigits(3);
        numberFormat.setMinimumIntegerDigits(1);
        numberFormat.setMinimumFractionDigits(3);
        
    }
    protected void calculateLoadingsMatrix(EigenDecomposition eigenDecomposition) throws OutOfRangeException {
        //заполняем матрицу нагрузок
        for(int i=0; i<countColumns; i++){
            //вставляем новую строчку в матрицу нагрузок
            loadingsMatrix.add(new ArrayList<Double>());
            for(int j=0; j<countColumns; j++){
                loadingsMatrix.get(i).add(eigenDecomposition.getEigenvector(j).getEntry(i));
            }
        }
    }
    protected void calculateScoresMatrix(EigenDecomposition eigenDecomposition) {
        //считаем проекции на новые координаты
        for(int i=0; i<countRows; i++){
            //добавляем новую строку в матрицу счетов
            scoresMatrix.add(new ArrayList<Interval>());
            for(int j=0; j<countColumns; j++){
                scoresMatrix.get(i).add(getProjectionOnPC(i, j, eigenDecomposition));
            }
        }
        
    }

    public double[] getContributionRates() {
        return contributionRates;
    }
    
    //централизация данных
    protected void centralization() {
        //мат.ожидания столбцов
        expectedValuesColumns = new double[datatable[0].length];
        
        //для каждого столбца считаем его математическое ожидание
        for(int j=0; j<countColumns; j++){
            //считаем мат ожидание j-го столбца
            expectedValuesColumns[j] = ExpectedValue(j);
        }       
        //вычитаем из каждого интервала стеднее значение его столбца
        for(int i=0; i<countRows; i++){
            for(int j=0; j<countColumns; j++){
                //плёха плёха... может не кастануться
                datatable[i][j] = SetIntervalOps.add((SetInterval)datatable[i][j], 
                        SetIntervalOps.nums2(-expectedValuesColumns[j], -expectedValuesColumns[j]));
            }
        }
    }
    //возвращает матрицу счетов
    public Interval[][] getScoresMatrix(){
        Interval[][] resultScoresMatrix = new Interval[countRows][countColumns];
        for(int i=0; i<countRows; i++){
            for(int j=0; j<countColumns; j++){
                resultScoresMatrix[i][j] = scoresMatrix.get(i).get(j);
            }
        }
        return resultScoresMatrix;
    }
    //возвращает матрицу нагрузок
    public double[][] getLoadingsMatrix(){
        double[][] resultLoadingsMatrix = new double[countColumns][countColumns];
        for(int i=0; i< countColumns; i++){
            for(int j=0; j<countColumns; j++){
                resultLoadingsMatrix[i][j] = loadingsMatrix.get(i).get(j);
            }
        }
        return resultLoadingsMatrix;
    }
    
    //вычичляет объем numberRow-го гиперкуба
    protected double calculateVolumeHypercube(int numberRow){
        double result = 0.0;
        double Eps = 1.0e-10;
        double intervalSize;
        boolean isNotPoint = false;
        //для каждого интервала в строке
        for(int i=0; i<countColumns; i++){
            intervalSize = datatable[numberRow][i].doubleSup() - datatable[numberRow][i].doubleInf(); 
            //проверяем начало и конец интервала не совпадали
            if(Math.abs(intervalSize) > Eps){
                //если уже был найден нетривиальный интервал
                if(isNotPoint){
                    result *= intervalSize;
                }else{//если найден первый нетривиальный интервал
                    isNotPoint = true;
                    result = intervalSize;
                }                
            }
        }
        return result;
    }
    
    //заполняет массив weights используя datatable и схему взвешивания
    protected void calculateWeights(){
        double totalVolume = 0.0;
        switch(weightingScheme){
            case EqualWeight:
                System.out.println("\nWeighting scheme is EqualWeights");
                //устанавливаем равные веса для всех
                for(int i=0; i<weights.length; i++){
                    weights[i] = 1.0/countRows;
                }
                break;
            case ProportionalVolume:
                System.out.println("\nWeighting scheme is ProportionalVolume");
                //вычисляем объем каждого гиперкуба
                for(int i=0; i<weights.length; i++){
                    weights[i] = calculateVolumeHypercube(i);
                }
                //находим суммарный объем
                for(int i=0; i<weights.length; i++){
                    totalVolume += weights[i];
                }
                //нормируем веса
                for(int i=0; i<weights.length; i++){
                    weights[i] /= totalVolume;
                }
                break;
            case InverselyProportionalVolume:
                System.out.println("\nWeighting scheme is InverselyProrotrionalVolume");
                //вычисляем объем каждого гиперкуба
                for(int i=0; i<weights.length; i++){
                    weights[i] = calculateVolumeHypercube(i);
                }
                //находим суммарный объем
                totalVolume = 0.0;
                for(int i=0; i<weights.length; i++){
                    totalVolume += weights[i];
                }
                for(int i=0; i<weights.length; i++){
                    weights[i] = 1 - weights[i]/totalVolume;
                }
                //перевычисляем суммарный объем
                totalVolume = 0.0;
                for(int i=0; i<weights.length; i++){
                    totalVolume += weights[i];
                }
                //нормируем веса
                for(int i=0; i<weights.length; i++){
                    weights[i] /= totalVolume;
                }
                break;
        }
    }
    
    //дополняет строку s пробелами до длины length
    protected String leftPad(String s, int length) {
        return String.format("%1$" + length + "s", s);  
    }
    //печать таблицы
    public void printDatatable(Interval[][] datatable){
        for(int i=0; i<datatable.length; i++){
            for(int j=0; j<datatable[i].length; j++){
                System.out.print("[");
                System.out.print(leftPad(numberFormat.format(datatable[i][j].doubleInf()),8));
                System.out.print("; ");
                System.out.print(leftPad(numberFormat.format(datatable[i][j].doubleSup()),8));
                System.out.print("] ");
            }
            System.out.println();
        }
    }
    
    //вывод матрицы счетов
    public void printScoresMatrix(){        
        for(int i=0; i<scoresMatrix.size(); i++){
            for(int j=0; j<scoresMatrix.get(i).size(); j++){
                System.out.print("[");
                System.out.print(leftPad(numberFormat.format(scoresMatrix.get(i).get(j).doubleInf()),8));
                System.out.print("; ");
                System.out.print(leftPad(numberFormat.format(scoresMatrix.get(i).get(j).doubleSup()),8));
                System.out.print("] ");
            }
            System.out.println();
        }
    }
    //вывод матрицы нагрузок
    public void printLoadingsMatrix(){
        for(int i=0; i<loadingsMatrix.size(); i++){
            for(int j=0; j<loadingsMatrix.get(i).size(); j++){
                System.out.print(leftPad(numberFormat.format(loadingsMatrix.get(i).get(j)),8) + " ");
            }
            System.out.println();
        }
    }
    //вывод весов
    protected void printWeights(){
        for(int i=0; i<weights.length; i++){
            System.out.println("weights["+i+"] = " + weights[i]);
        }
    }
    //математическое ожидание столбца
    protected double ExpectedValue(int numberColumn){
        //где будем хранить результат
        double result = 0.0;
        for(int i=0; i<datatable.length; i++){
            result += datatable[i][numberColumn].doubleMid();
        }
        //возвращаем результат
        return result/datatable.length;
    }
    //возвращает ковариационную матрицу для datatable (строки - переменные)
    protected double[][] calculateCovarianceMatrix(){
        //количество столбцов в datatable
        int countColumnsDatatable;
        countColumnsDatatable = datatable[0].length;
        //выделяем память
        double[][] covarianceMatrix = new double[countColumnsDatatable][countColumnsDatatable];
        //заполняем ковариационную матрицу
        //она симметрична! поэтому все считать не надо
        for(int i=0; i < countColumnsDatatable; i++){
            for(int j=i+1; j < countColumnsDatatable; j++){
                //берем скалярное произведение соответствующих столбцов из datatable
                covarianceMatrix[i][j] = covarianceMatrix[j][i] = innerProductDiffVectors(i, j);
            }            
        }
        //считаем значения на диагонали
        for(int i=0; i < countColumnsDatatable; i++ ){
            covarianceMatrix[i][i] = squareNormVector(i);
        }        
        return covarianceMatrix;
    }
    //возвращает результат скалярного произведения вектора на вертор 
//    (индексы должны быть) различными
    protected double innerProductDiffVectors(int indexColumn1, int indexColumn2){
        //здесь будем хранить результат
        double result = 0.0;
        //количество строк в datatable
        int countRows = datatable.length;
        //считаем
        for(int i = 0 ; i < countRows; i++){
            result += weights[i]*datatable[i][indexColumn1].doubleMid()*datatable[i][indexColumn2].doubleMid();
        }
        //возвращаем результат
        return result;
    }
    
    //квадрат нормы - в этом методе это не есть скалярное произведение вектора на себя
    abstract protected double squareNormVector(int indexColumn);    
    //стандартизация
    protected void standartization(){
        dispersions = new ArrayRealVector(datatable[0].length);
        //вычисляем корень из квадрата нормы столбца
        for(int i=0; i<datatable[0].length; i++){
            dispersions.setEntry(i, Math.sqrt(squareNormVector(i)));
        }
        //масштабируем наблюдения
        for(int i=0; i<datatable.length; i++){
            for(int j=0; j<datatable[0].length; j++){
                datatable[i][j] = SetIntervalOps.nums2(datatable[i][j].doubleInf()/dispersions.getEntry(j), 
                                                        datatable[i][j].doubleSup()/dispersions.getEntry(j));
                        
            }
        }
        
        
    }
    //сортирует вектор собственных значений и возвращает HashMap индексов
    protected HashMap getIndexesEValues(double[] realEigenvalues){
        HashMap<Integer, Integer> indexesEValues = new HashMap<Integer, Integer>();
        //инициализируем начальными значениями
        for(int i=0; i<realEigenvalues.length; i++){
            indexesEValues.put(i, i);
        }
        double temp;
        int tmp;
        //выполняем пузырьковую сортировку
        for(int i=0; i<realEigenvalues.length; i++){
            for(int j=realEigenvalues.length-1; j > i; j--){
                if(realEigenvalues[indexesEValues.get(j)] > realEigenvalues[indexesEValues.get(j-1)] ){
                    tmp = indexesEValues.get(j);
                    indexesEValues.put(j, indexesEValues.get(j-1));
                    indexesEValues.put(j-1, tmp);
                }
            }
        }
        return indexesEValues;
    }
    //возвращает проекцию интервала на главную компоненту
    protected SetInterval getProjectionOnPC(int numRow, int numPC, EigenDecomposition eigenDecomposition){
        //нижнее и верхнее значение результирующего интервала
        double infValue, supValue;
        ArrayRealVector infValues = new ArrayRealVector(countColumns);
        ArrayRealVector supValues = new ArrayRealVector(countColumns);
        //собственный вектор, на который будем искать проекцию
        RealVector eigenVector = eigenDecomposition.getEigenvector(numPC);
       
        //знак координаты собственного вектора
        double factor;
        //заполняем массив 
        for(int i=0; i<countColumns; i++){
            //See Wang, Gung, Wu - CIPCA page 4
            if(eigenVector.getEntry(i) > 0.0){
                //=>нижнее значение может дать только inf, а верхнее sup
                infValues.setEntry(i, datatable[numRow][i].doubleInf());
                supValues.setEntry(i, datatable[numRow][i].doubleSup());
            }else{
                //=>нижнее значение может дать только sup, а верхнее inf
                infValues.setEntry(i, datatable[numRow][i].doubleSup());
                supValues.setEntry(i, datatable[numRow][i].doubleInf());
            }
        }
        //проекция длины вектора на косинус угла между векторами
        infValue = infValues.getNorm()*infValues.cosine(eigenVector);
        supValue = supValues.getNorm()*supValues.cosine(eigenVector);             
        return SetIntervalOps.nums2(infValue, supValue);        
    }
    
    
    
    
    //возвращает центроид всех данных
    protected ArrayRealVector getCentroidAllData(){
        ArrayRealVector result = new ArrayRealVector(countColumns);
        double columnResult;
        for(int i=0; i<countColumns; i++){
            columnResult =0.0;
            for(int j=0; j<countRows; j++){
                columnResult += datatable[j][i].doubleInf();
                columnResult += datatable[j][i].doubleSup();
            }
            result.setEntry(i, columnResult/(2*countRows));
        }
        return result;
    }
    
    
    public void solve(){
        //создаем матрицу счетов
        scoresMatrix = new ArrayList<ArrayList<Interval>>();
        //создаем матрицу нагрузок
        loadingsMatrix = new ArrayList<ArrayList<Double>>();
        
        //выводим исходную datatable
        System.out.println("The original datatable:");
        printDatatable(datatable);
        centralization();
        //Выводим матожидания для столбцов
        System.out.println("\nExpected values for columns:");
        for(int i=0; i< expectedValuesColumns.length; i++){
            System.out.print(expectedValuesColumns[i] + "\t");
        }
        System.out.println();
        
        //выводим смещенную datatable
        System.out.println("\nCentralized datatable:");
        printDatatable(datatable); 
        //взвешивание
        calculateWeights();
        System.out.println("\nWeights:");
        printWeights();
        //стандартизация
        standartization();
        System.out.println("\nStandardized datatable:");
        printDatatable(datatable);
        
        
        //считаем ковариационную матрицу
        double[][] covarianceMatrix = new double[countColumns][countColumns];
        covarianceMatrix = calculateCovarianceMatrix();
        System.out.println("\nCovariance Matrix:");
        MatrixViewer.printMatrix(covarianceMatrix);
        System.out.println();
        //создаем RealMatrix
        RealMatrix realMatrix = new Array2DRowRealMatrix(covarianceMatrix);
        //Добываем собственные вектора и собственные значения
        EigenDecomposition eigenDecomposition = new EigenDecomposition(realMatrix);
        double[] realEigenvalues = eigenDecomposition.getRealEigenvalues();
        System.out.println("\nReal Eigenvalues:");
        for(int i=0; i<realEigenvalues.length; i++){
            System.out.print(realEigenvalues[i] + " ");
        }
        System.out.println();
        
//        //сортируем по убыванию собственные значения
//        HashMap<Integer, Integer> indexesEValues = getIndexesEValues(realEigenvalues);
//        //отсортированные собственные значения
//        System.out.println("\nOrdered eigen values:");
//        for(int i=0; i<realEigenvalues.length; i++){
//            System.out.println(i +  " -> " + indexesEValues.get(i));
//        }
        //выводим собственные вектора для каждого собственного значения
        System.out.println("\nEigen vectors:");
        for(int i=0; i<realEigenvalues.length; i++){
            System.out.println("Eigenvalue = " + numberFormat.format(realEigenvalues[i]));            
            System.out.println("\t" + eigenDecomposition.getEigenvector(i));
        }
        
        //количество информации, даваемое каждым собственным вектором
        double sumAllEigenValues = 0.0;
        for(int i=0; i<realEigenvalues.length; i++){
            sumAllEigenValues += realEigenvalues[i];
        }
        System.out.println();
        contributionRates = new double[realEigenvalues.length];
        double currenComulativeContributionRate = 0.0;
        for(int i=0; i<realEigenvalues.length; i++){
            contributionRates[i] = realEigenvalues[i]*100/sumAllEigenValues;
            currenComulativeContributionRate += realEigenvalues[i]*100/sumAllEigenValues;
//            System.out.println("Eigen value " + numberFormat.format(realEigenvalues[i]) + " given " + 
//                    numberFormat.format(realEigenvalues[i]*100/sumAllEigenValues) + "% information" +
//                    " CCR = " + currenComulativeContributionRate);
            System.out.println(currenComulativeContributionRate);
        }
//        
//        System.out.println("ComulativeContributionRate");
//        currenComulativeContributionRate = 0.0;
//        for(int i=0; i<contributionRates.length; i++){
//            comulativeContributionRate += contributionRates[i];
//            System.out.println(comulativeContributionRate);
//        }
        //вычисление матрицы счетов
        calculateScoresMatrix(eigenDecomposition);  
        
        
        //выводим матрицу счетов
        System.out.println("\nScores matrix:");
        printScoresMatrix();
        calculateLoadingsMatrix(eigenDecomposition);
        //выводим матрицу нагрузок
        System.out.println("\nLoadings matrix:");
        printLoadingsMatrix();
        
    }
    
}