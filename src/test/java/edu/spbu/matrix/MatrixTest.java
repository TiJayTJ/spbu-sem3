package edu.spbu.matrix;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MatrixTest
{
  /**
   * ожидается 4 таких теста
   */
  @Test
  public void mulDenseMatrixDD() {
    Matrix mD1 = new DenseMatrix("mD1.txt");
    Matrix m2 = new DenseMatrix("mD2.txt");
    Matrix expected = new DenseMatrix("result.txt");
    assertEquals(expected, mD1.mul(m2));
  }
  @Test
  public void mulDenseMatrixED(){
    Matrix mD1 = new DenseMatrix("mD1.txt");
    Matrix mD2 = new DenseMatrix("empty.txt");
    Matrix expected = new DenseMatrix("empty.txt");
    assertEquals(expected, mD1.mul(mD2));
  }
  @Test
  public void mulDenseMatrixDE(){
    Matrix mD1 = new DenseMatrix("empty.txt");
    Matrix mD2 = new DenseMatrix("mD1.txt");
    Matrix expected = new DenseMatrix("empty.txt");
    assertEquals(expected, mD1.mul(mD2));
  }
  @Test
  public void mulDenseMatrixEE(){
    Matrix mD1 = new DenseMatrix("empty.txt");
    Matrix mD2 = new DenseMatrix("empty.txt");
    Matrix expected = new DenseMatrix("empty.txt");
    assertEquals(expected, mD1.mul(mD2));
  }
  @Test
  public void mulSparseMatrixSS(){
    Matrix mS1 = new SparseMatrix("mS1.txt");
    Matrix mS2 = new SparseMatrix("mS2.txt");
    Matrix expected = new SparseMatrix("sresult.txt");
    Matrix mul = mS1.mul(mS2);
    assertEquals(expected, mul);
  }
  @Test
  public void mulSparseMatrixES(){
    Matrix mS1 = new SparseMatrix("empty.txt");
    Matrix mS2 = new SparseMatrix("mS2.txt");
    Matrix expected = new SparseMatrix("empty.txt");
    Matrix mul = mS1.mul(mS2);
    assertEquals(expected, mul);
  }
  @Test
  public void mulSparseMatrixSE(){
    Matrix mS1 = new SparseMatrix("mS2.txt");
    Matrix mS2 = new SparseMatrix("empty.txt");
    Matrix expected = new SparseMatrix("empty.txt");
    Matrix mul = mS1.mul(mS2);
    assertEquals(expected, mul);
  }
  @Test
  public void mulSparseMatrixSD(){
    Matrix mS1 = new SparseMatrix("mS1.txt");
    Matrix mS2 = new DenseMatrix("mSD.txt");
    Matrix expected = new SparseMatrix("SMresult.txt");
    Matrix mul = mS1.mul(mS2);
    assertEquals(expected, mul);
  }
  @Test
  public void mulSparseMatrixDS(){
    Matrix mS1 = new SparseMatrix("mS1.txt");
    Matrix mS2 = new DenseMatrix("mSD.txt");
    Matrix expected = new SparseMatrix("SMresult.txt");
    Matrix mul = mS2.mul(mS1);
    assertEquals(expected, mul);
  }
}
