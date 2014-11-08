package net.java.jinterval.pca;

import net.java.jinterval.interval.Interval;
/**
 *
 * @author lpa
 */
public class CIPCA extends AbstractPCA {
    public CIPCA(Interval[][] datatable){
        super(datatable);
    }
    public CIPCA(Interval[][] datatable, int countPC, WeightingSchemes weightingScheme){
        super(datatable, countPC, weightingScheme);
    }
    public CIPCA(Interval[][] datatable, double cumulativeContributionRate, 
                 WeightingSchemes weightingScheme){
        super(datatable, cumulativeContributionRate, weightingScheme);
    }
    //квадрат нормы - в этом методе это не есть скалярное произведение вектора на себя
    protected double squareNormVector(int indexColumn){
        //здесь будем хранить результат
        double result = 0.0;
        //количество строк в datatable
        int countRows = datatable.length;
        //считаем
        for(int i = 0 ; i < countRows; i++){
            result += weights[i]*(datatable[i][indexColumn].doubleInf()*datatable[i][indexColumn].doubleInf()+
                      datatable[i][indexColumn].doubleInf()*datatable[i][indexColumn].doubleSup()+
                      datatable[i][indexColumn].doubleSup()*datatable[i][indexColumn].doubleSup());
        }
        //возвращаем результат
        return result/3;
    }    
 
    
}