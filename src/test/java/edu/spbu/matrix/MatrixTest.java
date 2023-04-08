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
    Matrix mD2 = new DenseMatrix("mD2.txt");
    Matrix resultDD = new DenseMatrix("resultDD.txt");

    long time1 = System.currentTimeMillis();
    Matrix mul = mD1.mul(mD2);
    long time2 = System.currentTimeMillis();
    Matrix dmul = mD1.dmul(mD2);
    long time3 = System.currentTimeMillis();

    System.out.println("mul:\t" + (time2 - time1));
    System.out.println("dmul:\t" + (time3 - time2));
    System.out.println("difference:\t" + ((time2 - time1) / (time3 - time2)) + "\n");
    assertEquals(mul, resultDD);
  }
  @Test
  public void mulDenseMatrixED(){
    Matrix mD1 = new DenseMatrix("m1.txt");
    Matrix mD2 = new DenseMatrix("empty.txt");
    Matrix expected = new DenseMatrix("empty.txt");
    assertEquals(expected, mD1.mul(mD2));
  }
  @Test
  public void mulDenseMatrixDE(){
    Matrix mD1 = new DenseMatrix("empty.txt");
    Matrix mD2 = new DenseMatrix("m1.txt");
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
    Matrix mS1 = new SparseMatrix("m1.txt");
    Matrix mS2 = new SparseMatrix("m2.txt");

    long time1 = System.currentTimeMillis();
    Matrix mul = mS1.mul(mS2);
    long time2 = System.currentTimeMillis();
    Matrix dmul = mS1.dmul(mS2);
    long time3 = System.currentTimeMillis();

    System.out.println("mul:\t" + (time2 - time1));
    System.out.println("dmul:\t" + (time3 - time2));
    System.out.println("difference:\t" + ((time2 - time1) / (time3 - time2)) + "\n");

    assertEquals(mul, dmul);
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
    Matrix mS1 = new SparseMatrix("m1.txt");
    Matrix mS2 = new DenseMatrix("m2.txt");

    long time1 = System.currentTimeMillis();
    Matrix mul = mS1.mul(mS2);
    long time2 = System.currentTimeMillis();
    Matrix dmul = mS1.dmul(mS2);
    long time3 = System.currentTimeMillis();

    System.out.println("mul:\t" + (time2 - time1));
    System.out.println("dmul:\t" + (time3 - time2));
    System.out.println("difference:\t" + ((time2 - time1) / (time3 - time2)) + "\n");

    assertEquals(mul, dmul);
  }
  @Test
  public void mulSparseMatrixDS(){
    Matrix mS1 = new SparseMatrix("m1.txt");
    Matrix mS2 = new DenseMatrix("m2.txt");

    long time1 = System.currentTimeMillis();
    Matrix mul = mS1.mul(mS2);
    long time2 = System.currentTimeMillis();
    Matrix dmul = mS1.dmul(mS2);
    long time3 = System.currentTimeMillis();

    System.out.println("mul:\t" + (time2 - time1));
    System.out.println("dmul:\t" + (time3 - time2));
    System.out.println("difference:\t" + ((time2 - time1) / (time3 - time2)) + "\n");

    assertEquals(mul, dmul);
  }
}
