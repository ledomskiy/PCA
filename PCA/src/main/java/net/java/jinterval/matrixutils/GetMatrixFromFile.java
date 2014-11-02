package net.java.jinterval.matrixutils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

/**
 *
 * @author lpa
 */
public class GetMatrixFromFile extends AbstractGetMatrix {
    protected File file;
    //конструктрор на основе уже существующего файла
    public GetMatrixFromFile(File source) throws FileNotFoundException {
        this.file = source;
        this.source = new FileInputStream(source);
    }
    //конструктор на основе пути к файлу

    public GetMatrixFromFile(String pathToFile) throws FileNotFoundException {
        file = new File(pathToFile);
        this.source = new FileInputStream(file);
    }   
    
}
