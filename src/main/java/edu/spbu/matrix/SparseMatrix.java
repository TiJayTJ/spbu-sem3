package edu.spbu.matrix;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

/**
 * Разряженная матрица
 */
public class SparseMatrix implements Matrix
{
  public static final int rounding = 1000;
  public HashMap<Integer, HashMap<Integer, Double>> matrixHashMap = null;
  int column = 0;
  int row = 0;
  int hashcode = 0;

  /**
   * загружает матрицу из файла
   * @param fileName - name of file
   */
  public SparseMatrix(String fileName) {
    try {
      FileReader fr = new FileReader(fileName);
      BufferedReader reader = new BufferedReader(fr);
      String line = reader.readLine();
      this.matrixHashMap = new HashMap<>();

      if (line != null){
        column = line.split(" ").length;

        row = 0;
        while (line != null) {
          String[] lineString = line.split(" ");
          if (lineString.length != column){
            System.out.println(lineString.length + " " + column + " " + row);
            throw new IOException("Not rectangular matrix");
          }

          double[] lineDouble = Arrays.stream(lineString).mapToDouble(Double::parseDouble).toArray();
          HashMap<Integer, Double> lineDoubleHash = new HashMap<>();

          for (int i = 0; i < column; i++){
            if (lineDouble[i] != 0){
              lineDoubleHash.put(i, lineDouble[i]);
            }
          }

          matrixHashMap.put(row, lineDoubleHash);
          row++;
          line = reader.readLine();
        }
        hashcode = matrixHashCode(this.matrixHashMap);
      }
      else{
        this.matrixHashMap = new HashMap<>();
      }

      reader.close();
      fr.close();

    } catch (IOException e) {
      System.out.println(e.getMessage());
    }
  }
  public SparseMatrix(HashMap<Integer, HashMap<Integer, Double>> matrix, int newRow, int newColumn){
    matrixHashMap = matrix;
    column = newColumn;
    row = newRow;
    hashcode = matrixHashCode(matrix);
  }
  public SparseMatrix(){}

  public String toString(){
    if (row == 0 || column == 0) {
      return "";
    }
    else {
      StringBuilder matrixString = new StringBuilder();
      int i = 0, j = 0;
      for(Map.Entry<Integer, HashMap<Integer, Double>> line : this.matrixHashMap.entrySet()){
        for (Map.Entry<Integer, Double> item: line.getValue().entrySet()){
          while (i < line.getKey())
          {
            for (; j < column - 1; j++){
              matrixString.append("0 ");
            }
            matrixString.append("0\n");
            i++;
            j++;
            j = j % column;
          }
          for(; j < item.getKey(); j++){
            matrixString.append("0 ");
          }
          matrixString.append((double)Math.round(item.getValue() * rounding) / rounding);
          j++;
          if(j >= column){
            j = j % column;
            i++;
            matrixString.append("\n");
          }
          else{
            matrixString.append(" ");
          }
        }
      }
      while(i < row){
        while(j < column){
          matrixString.append("0 ");
          j++;
        }
        j = j % column;
        matrixString.append("\n");
        i++;
      }
      return matrixString.toString();
    }
  }

  public int matrixHashCode(HashMap<Integer, HashMap<Integer, Double>> matrix){
    int newHashcode = 0;
    for(Map.Entry<Integer, HashMap<Integer, Double>> line : matrix.entrySet()) {
      for (Map.Entry<Integer, Double> item : line.getValue().entrySet()) {
        newHashcode += item.getValue().intValue() % 100;
      }
      newHashcode %= 1000000;
    }

    return newHashcode;
  }
  public Matrix matrixTransposition(){
    HashMap<Integer, HashMap<Integer, Double>> newMatrix = new HashMap<>();
    for(Map.Entry<Integer, HashMap<Integer, Double>> line : this.matrixHashMap.entrySet()) {
      for (Map.Entry<Integer, Double> item : line.getValue().entrySet()) {
        if(newMatrix.containsKey(item.getKey())){
          newMatrix.get(item.getKey()).put(line.getKey(), item.getValue());
        }
        else{
          HashMap<Integer, Double> hashRow = new HashMap<>();
          hashRow.put(line.getKey(), item.getValue());
          newMatrix.put(item.getKey(), hashRow);
        }
      }
    }

    return new SparseMatrix(newMatrix, this.column, this.row);
  }

  public static double getItem(HashMap<Integer, HashMap<Integer, Double>> matrix, int i, int j){
    if(matrix.containsKey(i)){
      HashMap<Integer, Double> hashRow = matrix.get(i);
      if(hashRow.containsKey(j)){
        return hashRow.get(j);
      }
    }
      return 0;
  }

  /**
   * однопоточное умнджение матриц
   * должно поддерживаться для всех 4-х вариантов
   *
   * @param o - SparseMatrix
   * @return - SparseMatrix
   */
  @Override public Matrix mul(Matrix o)
  {
    HashMap<Integer, HashMap<Integer, Double>> matrixMul = new HashMap<>();
    HashMap<Integer, HashMap<Integer, Double>> matrix1 = this.matrixHashMap;

    if (o instanceof SparseMatrix){
      if (this.row == 0 || this.column == 0 || ((SparseMatrix) o).row == 0 || ((SparseMatrix) o).column == 0) {
        return new SparseMatrix(new HashMap<>(), 0, 0);
      }

      try {
        if (this.column != ((SparseMatrix) o).row) {
          throw new IOException("The number of columns of matrix 1 is not equal to the number of rows of matrix 2");
        }
      }
      catch (IOException e) {
        throw new IllegalArgumentException(e);
      }

      Matrix matrix2 = ((SparseMatrix)o).matrixTransposition();

      for(Map.Entry<Integer, HashMap<Integer, Double>> lineMatrix1 : matrix1.entrySet()){
        for(Map.Entry<Integer, HashMap<Integer, Double>> lineMatrix2 : ((SparseMatrix)matrix2).matrixHashMap.entrySet()){
          for(Map.Entry<Integer, Double> itemMatrix1 : lineMatrix1.getValue().entrySet()){
            int i = lineMatrix1.getKey(), j = lineMatrix2.getKey(), k = itemMatrix1.getKey();
            if (lineMatrix2.getValue().containsKey(k)){
              double newItem = itemMatrix1.getValue() * lineMatrix2.getValue().get(k);
              if(matrixMul.containsKey(i)){
                matrixMul.get(i).put(j, getItem(matrixMul, i, j) + newItem);
              }
              else {
                HashMap<Integer, Double> newRow = new HashMap<>();
                newRow.put(j, newItem);
                matrixMul.put(i, newRow);
              }
            }
          }
        }
      }

      return new SparseMatrix(matrixMul, this.row, ((SparseMatrix)o).column);
    }

    if (o instanceof DenseMatrix){
      if (this.row == 0 || this.column == 0 || ((DenseMatrix) o).row == 0 || ((DenseMatrix) o).column == 0) {
        return new SparseMatrix(new HashMap<>(), 0, 0);
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

      for(Map.Entry<Integer, HashMap<Integer, Double>> lineMatrix1 : matrix1.entrySet()){
        for (int j = 0; j < ((DenseMatrix)matrix2).row; j++){
          for(Map.Entry<Integer, Double> itemMatrix1 : lineMatrix1.getValue().entrySet()){
            int i = lineMatrix1.getKey(), k = itemMatrix1.getKey();
            double newItem = itemMatrix1.getValue() * ((DenseMatrix) matrix2).matrixList[j][k];
            if(matrixMul.containsKey(i)){
              newItem = getItem(matrixMul, i, j) + newItem;
              matrixMul.get(i).put(j, newItem);
            }
            else {
              HashMap<Integer, Double> newRow = new HashMap<>();
              newRow.put(j, newItem);
              matrixMul.put(i, newRow);
            }
          }
        }
      }

      return new SparseMatrix(matrixMul, this.row, ((DenseMatrix)o).column);
    }

    return new SparseMatrix(new HashMap<>(), 0, 0);
  }

  public static class matrixPartitionsMul extends Thread{
    Map.Entry<Integer, HashMap<Integer, Double>> line1;
    HashMap<Integer, HashMap<Integer, Double>> matrix2;
    final HashMap<Integer, HashMap<Integer, Double>> matrixMul; // synchronized(matrixMul)
    matrixPartitionsMul(Map.Entry<Integer, HashMap<Integer, Double>> line1,
                        HashMap<Integer, HashMap<Integer, Double>> matrix2,
                        HashMap<Integer, HashMap<Integer, Double>> matrixMul){
      this.line1 = line1;
      this.matrix2 = matrix2;
      this.matrixMul = matrixMul;
    }

    @Override public void run(){
      for(Map.Entry<Integer, HashMap<Integer, Double>> line2: matrix2.entrySet()){
        for(Map.Entry<Integer, Double> item: line1.getValue().entrySet()){
          int i = line1.getKey(), j = line2.getKey(), k = item.getKey();
          if (line2.getValue().containsKey(k)){
            double newItem = item.getValue() * line2.getValue().get(k);
            synchronized(matrixMul){
              if(matrixMul.containsKey(i)){
                matrixMul.get(i).put(j, getItem(matrixMul, i, j) + newItem);
              }
              else {
                HashMap<Integer, Double> newRow = new HashMap<>();
                newRow.put(j, newItem);
                matrixMul.put(i, newRow);
              }
            }

          }
        }
      }
    }
  }

  /**
   * многопоточное умножение матриц
   *
   * @param o - SparseMatrix
   * @return - SparseMatrix
   */
  @Override public Matrix dmul(Matrix o)
  {
    HashMap<Integer, HashMap<Integer, Double>> matrix1 = this.matrixHashMap;

    if(o instanceof SparseMatrix) {
      HashMap<Integer, HashMap<Integer, Double>> matrix2 = ((SparseMatrix)((SparseMatrix)o).matrixTransposition()).matrixHashMap;
      HashMap<Integer, HashMap<Integer, Double>> matrixMul = new HashMap<>();
      List<matrixPartitionsMul> allThreads = new ArrayList<>();

      for (Map.Entry<Integer, HashMap<Integer, Double>> line1: matrix1.entrySet()){
        allThreads.add(new matrixPartitionsMul(line1, matrix2, matrixMul));
        allThreads.get(allThreads.size()-1).start();
      }

      try {
        for (matrixPartitionsMul thread: allThreads){
          thread.join();
        }
      } catch (InterruptedException e) {
        throw new RuntimeException(e);
      }

      return new SparseMatrix(matrixMul, this.row, ((SparseMatrix)o).column);
    }

    return null;
  }

  public boolean elementByElementComparison(Object o) {
    HashMap<Integer, HashMap<Integer, Double>> matrix = ((SparseMatrix)o).matrixHashMap;
    for(Map.Entry<Integer, HashMap<Integer, Double>> line : this.matrixHashMap.entrySet()) {
      for (Map.Entry<Integer, Double> item : line.getValue().entrySet()) {
        if (matrix.containsKey(line.getKey())){
          if (Math.abs(item.getValue() - matrix.get(line.getKey()).getOrDefault(item.getKey(), 0.0)) > 0.000001){
            return false;
          }
        }
        else{
          return false;
        }
      }
    }

    return true;
  }
  /**
   * спавнивает с обоими вариантами
   * @param o - SparseMatrix
   * @return - SparseMatrix
   */
  @Override public boolean equals(Object o) {
    if(this == o){
      return true;
    }
    if(o instanceof SparseMatrix){
      if (this.row != ((SparseMatrix) o).row || this.column != ((SparseMatrix) o).column){
        return false;
      }
//      if(((SparseMatrix) o).hashcode != this.hashcode) {
//        return false;
//      }
//      else{
        if (this.hashcode == 0){
          return true;
        }
        else{
          return this.elementByElementComparison(o) && ((SparseMatrix)o).elementByElementComparison(this);
        }
      //}
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
          double[][] matrix = ((DenseMatrix) o).matrixList;
          for (int i = 0; i < this.row; i++) {
            for (int j = 0; j < this.column; j++) {
              double value = this.matrixHashMap.containsKey(i) ?
                      this.matrixHashMap.get(i).getOrDefault(j, 0.0) : 0;
              if (Math.abs(value - matrix[i][j]) > 0.0001) {
                return false;
              }
            }
          }
        }
        return true;
      }
    }

    return false;
  }
}
