package edu.spbu.matrix;

import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.HashMap;

/**
 * Плотная матрица
 */
public class DenseMatrix implements Matrix
{
  public static final int rounding = 1000;
  public static final double Epsilon = 0.0001;
  public double[][] matrixList = null;
  public int hashcode = 0;
  public int column = 0;
  public int row = 0;

  /**
   * загружает матрицу из файла
   * @param fileName - name of file
   */
  public DenseMatrix(String fileName) {
    try {
      FileReader fr = new FileReader(fileName);
      BufferedReader reader = new BufferedReader(fr);
      while (reader.readLine() != null) { row++; }

      if (row != 0){
        fr = new FileReader(fileName);
        reader = new BufferedReader(fr);
        String line = reader.readLine();
        column = line.split(" ").length;
        matrixList = new double[row][column];


        for (int i = 0; i < row; i++){
          String[] lineString = line.split(" ");
          if (lineString.length != column){
            throw new IOException("Not rectangular matrix");
          }

          for (int j = 0; j < column; j++){
            matrixList[i][j] = Double.parseDouble(lineString[j]);
          }

          line = reader.readLine();
        }
        hashcode = matrixHashCode(this.matrixList);
      }

      reader.close();
      fr.close();

    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
  public DenseMatrix(double[][] matrix){
    this.matrixList = matrix;
    if (matrix != null){
      row = matrix.length;
      column = matrix[0].length;
      hashcode = matrixHashCode(this.matrixList);
    }
  }
  public DenseMatrix(){}

  public String toString(){
    if (this.matrixList == null) {
      return "";
    }

    StringBuilder matrixString = new StringBuilder();
    for (int i = 0; i < row; i++){
      for (int j = 0; j < column; j++){
        matrixString.append((double)Math.round(matrixList[i][j] * rounding) / rounding).append(" ");
      }
      matrixString.append("\n");
    }
    return matrixString.toString();
  }

  public int matrixHashCode(double[][] matrix){
    int newHashcode = 0;
    for (int i = 0; i < this.row; i++){
      for (int j = 0; j < this.column; j++){
        newHashcode += (int)matrix[i][j] % 100;
      }
      newHashcode %= 1000000;
    }
    return newHashcode;
  }

  public Matrix matrixTransposition(){
    double[][] ret = new double[this.column][this.row];
    for (int i = 0; i < this.row; i++) {
      for (int j = 0; j < this.column; j++){
        ret[j][i] = this.matrixList[i][j];
      }
    }
    return new DenseMatrix(ret);
  }

  /**
   * однопоточное умнджение матриц
   * должно поддерживаться для всех 4-х вариантов
   *
   * @param o - DenseMatrix
   * @return - DenseMatrix
   */
  @Override public Matrix mul(Matrix o)
  {
    double[][] matrix1 = this.matrixList;

    if (o instanceof DenseMatrix){
      double[][] matrixMul = new double[this.row][((DenseMatrix)o).column];

      if (this.row == 0 || this.column == 0 || ((DenseMatrix) o).row == 0 || ((DenseMatrix) o).column == 0) {
        return new DenseMatrix();
      }

      try {
        if (this.column != ((DenseMatrix) o).row) {
          throw new IOException("The number of columns of matrix 1 is not equal to the number of rows of matrix 2");
        }
      }
      catch (IOException e) {
        throw new IllegalArgumentException(e);
      }

      Matrix matrix2 = ((DenseMatrix)o).matrixTransposition();

      for (int i = 0; i < this.row; i++){
        for (int j = 0; j < ((DenseMatrix)matrix2).row; j++){
          for (int k = 0; k < this.column; k++) {
            matrixMul[i][j] += matrix1[i][k] * ((DenseMatrix) matrix2).matrixList[j][k];
          }
        }
      }

      return new DenseMatrix(matrixMul);
    }

    if (o instanceof SparseMatrix){
      return o.mul(this);
    }

    return null;
  }

  public static class matrixPartMulDense extends Thread{
    public double[][] matrix1;
    public double[][] matrix2;
    public double[][] matrixMul;
    public int threadNumber;
    public int numberOfElements;
    public int numberOfThreads;
    matrixPartMulDense(double[][] matrix1, double[][] matrix2,
                        double[][] matrixMul, int threadNumber,
                        int numberOfElements, int numberOfThreads){
      this.matrix1 = matrix1;
      this.matrix2 = matrix2;
      this.matrixMul = matrixMul;
      this.threadNumber = threadNumber;
      this.numberOfElements = numberOfElements;
      this.numberOfThreads = numberOfThreads;
    }

    @Override public void run(){
      int i_begin = (threadNumber * numberOfElements) / matrix2.length;
      int j_begin = (threadNumber * numberOfElements) % matrix2.length;
      int i_end, j_end;
      if (threadNumber != numberOfThreads - 1)
      {
        i_end = (((threadNumber+1) * numberOfElements)-1) / matrix2.length;
        j_end = (((threadNumber+1) * numberOfElements)-1) % matrix2.length;
      }
      else {
        i_end = matrix1.length - 1;
        j_end = matrix2.length - 1;
      }

      int i = i_begin;
      int j = j_begin;
      while(i < i_end){
        if (j >= matrix2.length){
          j = 0;
          i++;
        }
        for (int k = 0; k < matrix1[0].length; k++){
          matrixMul[i][j] += matrix1[i][k] * matrix2[j][k];
        }
        j++;
      }
      for (; j <= j_end; j++){
        for (int k = 0; k < matrix1[0].length; k++){
          matrixMul[i][j] += matrix1[i][k] * matrix2[j][k];
        }
      }
    }
  }

  /**
   * многопоточное умножение матриц
   *
   * @param o - DenseMatrix
   * @return -  DenseMatrix
   */
  @Override public Matrix dmul(Matrix o)
  {
    int numberOfThreads = Runtime.getRuntime().availableProcessors();

    if(o instanceof DenseMatrix) {
      double[][] matrix1 = this.matrixList;
      double[][] matrix2 = ((DenseMatrix)((DenseMatrix)o).matrixTransposition()).matrixList;
      double[][] matrixMul = new double[this.row][((DenseMatrix)o).column];

      int numberOfElements = (this.row * ((DenseMatrix)o).column) / numberOfThreads;
      numberOfThreads++;
      matrixPartMulDense[] allThreads = new matrixPartMulDense[numberOfThreads];

      for (int i = 0; i < numberOfThreads; i++){
        allThreads[i] = new matrixPartMulDense(matrix1, matrix2, matrixMul, i, numberOfElements, numberOfThreads);
        allThreads[i].start();
      }

      try {
        for (int i = 0; i < numberOfThreads; i++){
          allThreads[i].join();
        }
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

      return new DenseMatrix(matrixMul);
    }

    if(o instanceof SparseMatrix){
      Matrix matrix1 = this.matrixTransposition();
      Matrix matrix2 = ((SparseMatrix)o).matrixTransposition();

      return ((SparseMatrix)matrix2.dmul(matrix1)).matrixTransposition();
    }

    return new SparseMatrix();
  }

  /**
   * спавнивает с обоими вариантами
   * @param o - DenseMatrix
   * @return - True or False
   */
  @Override public boolean equals(Object o) {
    if(this == o){
      return true;
    }
    if(o instanceof DenseMatrix){
      if (this.row != ((DenseMatrix) o).row || this.column != ((DenseMatrix) o).column){
        return false;
      }
      if(((DenseMatrix) o).hashcode != this.hashcode) {
        return false;
      }
      else{
        if (this.hashcode != 0) {
          for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.column; j++) {
              if (Math.abs(this.matrixList[i][j] - ((DenseMatrix) o).matrixList[i][j]) > Epsilon) {
                return false;
              }
            }
          }
        }
        return true;
      }
    }

    if(o instanceof SparseMatrix){
      if (this.row != ((SparseMatrix) o).row || this.column != ((SparseMatrix) o).column){
        return false;
      }

      for(int i = 0; i < this.row; i++){
        for (int j = 0; j < this.column; j++){
          double value = ((SparseMatrix) o).matrixHashMap.containsKey(i) ?
                  ((SparseMatrix) o).matrixHashMap.get(i).getOrDefault(j, 0.0): 0;
          if(Math.abs(value - this.matrixList[i][j]) > Epsilon){
            return false;
          }
        }
      }
      return true;
    }

    return false;
  }
}
