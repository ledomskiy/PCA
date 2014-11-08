package net.java.jinterval.test;

//import com.sun.xml.internal.ws.util.StringUtils;
import java.io.IOException;
//import java.text.NumberFormat;
import java.util.ArrayList;
//import net.java.jinterval.pca.matrixUtils.GetMatrixFromFile;
import net.java.jinterval.matrixutils.ReadMatrixFromCSVFile;
import net.java.jinterval.interval.Interval;
//import net.java.jinterval.interval.SetIntervalContextInfSupBase;
//import net.java.jinterval.interval.SetIntervalContext;
//import net.java.jinterval.interval.Decoration;
//import net.java.jinterval.interval.SetInterval;
//import net.java.jinterval.interval.SetIntervalContextInfSup;
//import net.java.jinterval.interval.SetIntervalOps;
//import net.java.jinterval.interval.set.SetIntervalContextInfSupBase;
//import net.java.jinterval.interval.set.SetIntervalContext;
//import net.java.jinterval.interval.Decoration;
//import net.java.jinterval.interval.set.SetInterval;
//import net.java.jinterval.interval.set.SetIntervalContextInfSup;
//import net.java.jinterval.interval.set.SetIntervalOps;
//import net.java.jinterval.rational.ExtendedRationalContext;
//import net.java.jinterval.rational.Rational;
//import net.java.jinterval.pca.matrixUtils.AbstractGetMatrix;
//import net.java.jinterval.pca.matrixUtils.GetMatrixFromFile;
import net.java.jinterval.matrixutils.WriteMatrixIntoCSVFile;
import net.java.jinterval.pca.AbstractPCA;
import net.java.jinterval.pca.CPCA;
import net.java.jinterval.pca.VPCA;
import net.java.jinterval.pca.CIPCA;


public class PCATest 
{
    public static void main( String[] args ) throws IOException, Exception
    {
        //worked example
       Interval[][] matrix;
        String pathToFile;
        String pathToFolder;
        String fileName;
        ArrayList<String> rowHeadersAL = new ArrayList<String>();
        String[] rowHeaders;
        ArrayList<String> columnHeadersAL = new ArrayList<String>();
        String[] columnHeaders;
        pathToFolder = "//home//lpa//repos//git//PCA_JInterval//DataExamples//";
        fileName = "ELibraryAsChinese.csv";
        pathToFile = pathToFolder + fileName;
//        AbstractGetMatrix source = new GetMatrixFromFile(pathToFile);
        ReadMatrixFromCSVFile source = new ReadMatrixFromCSVFile(pathToFile);
        matrix = source.getMatrix(rowHeadersAL, columnHeadersAL);
        rowHeaders = new String[rowHeadersAL.size()];
        
        columnHeaders = new String[columnHeadersAL.size()];
        rowHeadersAL.toArray(rowHeaders);
        columnHeadersAL.toArray(columnHeaders);
        
        
        
        String usingMethod = "CIPCA";
        String usingWeightedType = "ProportionalVolume";
        AbstractPCA pca = new CIPCA(matrix, 0.0 ,AbstractPCA.WeightingSchemes.ProportionalVolume);
        pca.solve();
        
            
        double[][] lm = pca.getLoadingsMatrix();
        Interval[][] sm = pca.getScoresMatrix();
        double[] contributionRates;
        contributionRates = pca.getContributionRates();
        
        //WriteMatrixIntoCSVFile.writeScoresMatrix(pathToFolder + "elibrary2011SM_"+usingMethod+"_"+usingWeightedType+".csv", sm, rowHeaders, contributionRates, ".");
        //WriteMatrixIntoCSVFile.writeLoadingsMatrix1(pathToFolder + "elibrary2011LM_"+usingMethod+"_"+usingWeightedType+".csv", lm, columnHeaders, contributionRates,".");
        
        //////////////////////    READ FROM CSV FILE      //////////////////////////
//        ReadMatrixFromCSVFile sourceCSV = new ReadMatrixFromCSVFile("D://FROM_NOTEBOOK//asu//book//diplom//DataExamples//elibrary2001группировкаПоГРНТИ.csv");
//        Interval[][] test;
//        test = sourceCSV.getMatrix();
//        for(int i=0; i<test.length; i++){
//            for(int j=0; j<test[i].length; j++){
//                System.out.print("[" + test[i][j].doubleInf() + ";"+test[i][j].doubleSup()+"]");
//            }
//            System.out.println();
//        }
    }
}





















