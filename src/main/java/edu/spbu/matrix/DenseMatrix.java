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
  public List<List<Double>> matrixList = new ArrayList<>();
  int hashcode = 0;
  int column = 0;
  int row = 0;

  /**
   * загружает матрицу из файла
   * @param fileName - name of file
   */
  public DenseMatrix(String fileName) {
    try {
      FileReader fr = new FileReader(fileName);
      BufferedReader reader = new BufferedReader(fr);
      String line = reader.readLine();
      if (line == null){
        this.matrixList = new ArrayList<>();
      }
      else{
        column = line.split(" ").length;
        while (line != null) {
          row++;
          String[] lineString = line.split(" ");
          if (lineString.length != column){
            throw new IOException("Not rectangular matrix");
          }

          double[] lineDouble = new double[lineString.length];
          for (int i = 0; i < lineString.length; i++){
            lineDouble[i] = Double.parseDouble(lineString[i]);
          }

          this.matrixList.add(Arrays.stream(lineDouble).boxed().collect(Collectors.toList()));
          line = reader.readLine();
        }
        hashcode = this.matrixList.hashCode();
      }

      reader.close();
      fr.close();

    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
  public DenseMatrix(List<List<Double>> matrix, int newRow, int newColumn){
    this.matrixList = matrix;
    row = newRow;
    column = newColumn;
    if (!matrix.isEmpty()){
      hashcode = this.matrixList.hashCode();
    }
  }

  public String toString(){
    if (this.matrixList == null) {
      return "";
    }
    StringBuilder matrixString = new StringBuilder();
    for (List<Double> line: this.matrixList){
      for (double number: line){
        matrixString.append(number).append(" ");
      }
      matrixString.append("\n");
    }
    return matrixString.toString();
  }


  public Matrix matrixTransposition(){
    List<List<Double>> ret = new ArrayList<>();
    int n = this.matrixList.get(0).size();
    for (int i = 0; i < n; i++) {
      List<Double> col = new ArrayList<>();
      for (List<Double> row : this.matrixList) {
        col.add(row.get(i));
      }
      ret.add(col);
    }
    return new DenseMatrix(ret, this.column, this.row);

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
    List<List<Double>> matrixMul = new ArrayList<>();
    List<List<Double>> matrix1 = this.matrixList;

    if (o instanceof DenseMatrix){
      if (this.row == 0 || this.column == 0 || ((DenseMatrix) o).row == 0 || ((DenseMatrix) o).column == 0) {
        return new DenseMatrix(new ArrayList<>(), 0, 0);
      }

      Matrix matrix2 = ((DenseMatrix)o).matrixTransposition();
      double count = 0;

      try {
        if (this.column != ((DenseMatrix) o).row) {
          throw new IOException("The number of columns of matrix 1 is not equal to the number of rows of matrix 2");
        }
      }
      catch (IOException e) {
        throw new RuntimeException(e);
      }

      for (List<Double> matrix1Line: matrix1){
        List<Double> line = new ArrayList<>();
        for (List<Double> matrix2Line: ((DenseMatrix)matrix2).matrixList) {
          for (int i = 0; i < this.column; i++) {
            count += matrix1Line.get(i) * matrix2Line.get(i);
          }
          line.add((double) Math.round(count * 100) / 100);
          count = 0;
        }
        matrixMul.add(line);
      }

      return new DenseMatrix(matrixMul, this.row, ((DenseMatrix) o).column);
    }

    if (o instanceof SparseMatrix){
      if (this.row == 0 || this.column == 0 || ((SparseMatrix) o).row == 0 || ((SparseMatrix) o).column == 0) {
        return new DenseMatrix(new ArrayList<>(), 0, 0);
      }
      return o.mul(this);
    }

    return null;
  }

  /**
   * многопоточное умножение матриц
   *
   * @param o - DenseMatrix
   * @return -  DenseMatrix
   */
  @Override public Matrix dmul(Matrix o)
  {
    return null;
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
        if (this.hashcode == 0){
          return true;
        }
        else{
          return this.matrixList.equals(((DenseMatrix) o).matrixList);
        }
      }
    }

    if(o instanceof SparseMatrix){
      List<List<Double>> matrix = this.matrixList;
      for(int i = 0; i < this.row; i++){
        for (int j = 0; j < this.column; j++){
          double value = ((SparseMatrix) o).matrixHashMap.containsKey(i) ?
                  ((SparseMatrix) o).matrixHashMap.get(i).getOrDefault(j, 0.0): 0;
          if(value != matrix.get(i).get(j)){
            return false;
          }
        }
      }
      return true;
    }

    return false;
  }
}
