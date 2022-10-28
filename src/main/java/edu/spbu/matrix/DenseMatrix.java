package edu.spbu.matrix;

import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Плотная матрица
 */
public class DenseMatrix implements Matrix
{
  /**
   * загружает матрицу из файла
   * @param fileName
   */
  private List<List<Integer>> matrixList = new ArrayList<>();
  int hashcode = 0;

  public DenseMatrix(String fileName) {
    try {
      FileReader fr = new FileReader(fileName);
      BufferedReader reader = new BufferedReader(fr);
      String line = reader.readLine();
      if (line == null){
        this.matrixList = null;
      }
      else{
        int m = line.split(" ").length;
        while (line != null) {
          String[] lineString = line.split(" ");
          if (lineString.length != m){
            throw new IOException("Not rectangular matrix");
          }

          int[] lineInt = new int[lineString.length];
          for (int i = 0; i < lineString.length; i++){
            lineInt[i] = Integer.parseInt(lineString[i]);
          }

          this.matrixList.add(Arrays.stream(lineInt).boxed().collect(Collectors.toList()));
          line = reader.readLine();
        }
        hashcode = this.matrixList.hashCode();
      }

      reader.close();
      fr.close();

    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
  public DenseMatrix(List<List<Integer>> matrix){
    this.matrixList = matrix;
    if (matrix != null){
      hashcode = this.matrixList.hashCode();
    }
  }

  public String toString(){
    if (this.matrixList == null) {
      return "";
    }
    StringBuilder matrixString = new StringBuilder();
    for (List<Integer> line: this.matrixList){
      for (int number: line){
        matrixString.append(number).append(" ");
      }
      matrixString.append("\n");
    }
    return matrixString.toString();
  }


  /**
   * однопоточное умнджение матриц
   * должно поддерживаться для всех 4-х вариантов
   *
   * @param o
   * @return
   */

  private List<List<Integer>> Matrix_transposition(){
    List<List<Integer>> ret = new ArrayList<>();
    int n = this.matrixList.get(0).size();
    for (int i = 0; i < n; i++) {
      List<Integer> col = new ArrayList<>();
      for (List<Integer> row : this.matrixList) {
        col.add(row.get(i));
      }
      ret.add(col);
    }
    return ret;

  }
  @Override public Matrix mul(Matrix o)
  {
    List<List<Integer>> matrixMul = new ArrayList<>();
    List<List<Integer>> matrix1 = this.matrixList;
    List<List<Integer>> matrix2 = (((DenseMatrix)o).matrixList != null) ? ((DenseMatrix)o).Matrix_transposition() : null;
    int count = 0;

    if (matrix1 == null || matrix2 == null){
      return new DenseMatrix((List<List<Integer>>)null);
    }

    try {
      if (matrix1.get(0).size() != matrix2.get(0).size()) {
        throw new IOException("The number of columns of matrix 1 is not equal to the number of rows of matrix 2");
      }
    }
    catch (IOException e) {
      throw new RuntimeException(e);
    }

    for (List<Integer> matrix1Line: matrix1){
      List<Integer> line = new ArrayList<>();
      for (List<Integer> matrix2Line: matrix2) {
        for (int i = 0; i < matrix1Line.size(); i++) {
          count += matrix1Line.get(i) * matrix2Line.get(i);
        }
        line.add(count);
        count = 0;
      }
      matrixMul.add(line);
    }

    return new DenseMatrix(matrixMul);
  }

  /**
   * многопоточное умножение матриц
   *
   * @param o
   * @return
   */
  @Override public Matrix dmul(Matrix o)
  {
    return null;
  }

  /**
   * спавнивает с обоими вариантами
   * @param o
   * @return
   */
  @Override public boolean equals(Object o) {
    if(this == o){
      return true;
    }
    if (o.getClass() != this.getClass()){
      return false;
    }
    if(((DenseMatrix) o).hashcode != this.hashcode) {
      return false;
    }
    else{
      if (this.hashcode == 0){
        return true;
      }
      else{
        return this.matrixList.equals(((DenseMatrix) o).matrixList);
      }
    }

  }
}
