package net.java.jinterval.pca;

import net.java.jinterval.interval.Interval;
/**
 *
 * @author lpa
 */
public class CPCA extends AbstractPCA {
    public CPCA(Interval[][] datatable){
        super(datatable);
    }
    public CPCA(Interval[][] datatable, int countPC, WeightingSchemes weightingScheme){
        super(datatable, countPC, weightingScheme);
    }
    public CPCA(Interval[][] datatable, double cumulativeContributionRate, 
                 WeightingSchemes weightingScheme){
        super(datatable, cumulativeContributionRate, weightingScheme);
    }
    
    //квадрат нормы столбца
    protected double squareNormVector(int indexColumn){
        //здесь будем хранить результат
        double result = 0.0;
        //количество строк в datatable
        int countRows = datatable.length;
        //считаем
        for(int i = 0 ; i < countRows; i++){
            result += weights[i]*(datatable[i][indexColumn].doubleInf()*datatable[i][indexColumn].doubleInf()+
                      datatable[i][indexColumn].doubleSup()*datatable[i][indexColumn].doubleSup());
        }
        //возвращаем результат
        return result/4;
    }
    
 
    
}