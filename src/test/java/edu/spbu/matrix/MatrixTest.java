package edu.spbu.matrix;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class MatrixTest
{
  /**
   * ожидается 4 таких теста
   */
  @Test
  public void mulDD() {
    Matrix m1 = new DenseMatrix("m1.txt");
    Matrix m2 = new DenseMatrix("m2.txt");
    Matrix expected = new DenseMatrix("result.txt");
    assertEquals(expected, m1.mul(m2));
  }
  @Test
  public void mulED(){
    Matrix m1 = new DenseMatrix("m1.txt");
    Matrix m2 = new DenseMatrix("empty.txt");
    Matrix expected = new DenseMatrix("empty.txt");
    assertEquals(expected, m1.mul(m2));
  }
  @Test
  public void mulDE(){
    Matrix m1 = new DenseMatrix("empty.txt");
    Matrix m2 = new DenseMatrix("m1.txt");
    Matrix expected = new DenseMatrix("empty.txt");
    assertEquals(expected, m1.mul(m2));
  }
  @Test
  public void mulEE(){
    Matrix m1 = new DenseMatrix("empty.txt");
    Matrix m2 = new DenseMatrix("empty.txt");
    Matrix expected = new DenseMatrix("empty.txt");
    assertEquals(expected, m1.mul(m2));
  }
}
