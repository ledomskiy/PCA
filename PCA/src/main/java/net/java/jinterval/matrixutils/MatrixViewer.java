package net.java.jinterval.matrixutils;

import java.text.NumberFormat;

/**
 *
 * @author lpa
 */
public class MatrixViewer {
    public static void printMatrix(double[][] matrix){
        NumberFormat nf = NumberFormat.getInstance();
        nf.setMaximumFractionDigits(3);
        for(int i=0; i<matrix.length; i++){
            for(int j=0; j<matrix[i].length; j++){
                System.out.print(nf.format(matrix[i][j]) + "\t");
            }
            System.out.println();
        }
    }
    
}