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
  private HashMap<Integer, HashMap<Integer, Double>> matrixHashMap;
  int row, column;
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
            System.out.println(lineString.length + " " + column);
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
      }
      hashcode = this.matrixHashMap.hashCode();

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
    hashcode = matrix.hashCode();
  }

  public String toString(){
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
        matrixString.append(item.getValue());
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
    HashMap<Integer, HashMap<Integer, Double>> matrix2 = ((SparseMatrix)o).matrixHashMap;

    for(Map.Entry<Integer, HashMap<Integer, Double>> line : matrix1.entrySet()) {
      HashMap<Integer, Double> lineHash = new HashMap<>();
      for (Map.Entry<Integer, Double> item : line.getValue().entrySet()) {
        for(int i = 0; i < ((SparseMatrix)o).column; i++){
          double count = item.getValue() * matrix2.get(item.getKey()).getOrDefault(i, 0.0);
          if(matrixMul.containsKey(line.getKey())) {
            double c = matrixMul.get(line.getKey()).getOrDefault(i, 0.0);
            count += c;
          }
          if (count != 0){
            lineHash.put(i, (double) Math.round(count * 100) / 100);
          }
        }
        matrixMul.put(line.getKey(), lineHash);
      }
    }

    return new SparseMatrix(matrixMul, this.row, ((SparseMatrix)o).column);
  }

  /**
   * многопоточное умножение матриц
   *
   * @param o - SparseMatrix
   * @return - SparseMatrix
   */
  @Override public Matrix dmul(Matrix o)
  {
    return null;
  }

  public boolean elementByElementComparison(Object o) {
    HashMap<Integer, HashMap<Integer, Double>> matrix = ((SparseMatrix)o).matrixHashMap;
    for(Map.Entry<Integer, HashMap<Integer, Double>> line : this.matrixHashMap.entrySet()) {
      for (Map.Entry<Integer, Double> item : line.getValue().entrySet()) {
        if (!Objects.equals(item.getValue(), matrix.get(line.getKey()).get(item.getKey()))){
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
    else if(((SparseMatrix) o).hashcode != this.hashcode) {
      return false;
    }
    else{
      if (this.hashcode == 0){
        return true;
      }
      else{
        return this.elementByElementComparison(o) && ((SparseMatrix)o).elementByElementComparison(this);
      }
    }
  }
}
