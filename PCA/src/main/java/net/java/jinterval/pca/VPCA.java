package net.java.jinterval.pca;

import net.java.jinterval.interval.Interval;
/**
 *
 * @author lpa
 */
public class VPCA extends AbstractPCA {
    public VPCA(Interval[][] datatable){
        super(datatable);
    }
    public VPCA(Interval[][] datatable, WeightingSchemes weightingScheme){
        super(datatable, weightingScheme);
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
                      datatable[i][indexColumn].doubleSup()*datatable[i][indexColumn].doubleSup());
        }
        //возвращаем результат
        return result/2;
    }   
}