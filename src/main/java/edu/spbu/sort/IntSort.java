package edu.spbu.sort;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Created by artemaliev on 07/09/15.
 */
public class IntSort {
  public static void merge(int array[], int left, int mid, int right){
    int i = 0;
    int j = 0;
    int result[] = new int[right - left];

    while (left + i < mid && mid + j < right)
    {
      if(array[left + i] < array[mid + j])
      {
        result[i+j] = array[left+i];
        i++;
      }
      else {
        result[i+j] = array[mid+j];
        j++;
      }
    }

    while(left + i < mid)
    {
      result[i+j] = array[left+i];
      i++;
    }

    while(mid + j < right)
    {
      result[i+j] = array[mid+j];
      j++;
    }

    for (int k = 0; k < i+j; k++)
    {
      array[left + k] = result[k];
    }
  }

  public static void sort (int array[]) {
    //Arrays.sort(array);
    int size = array.length;
    for (int i = 1; i < size; i *= 2)
    {
      for (int j = 0; j < size - i; j += i * 2)
      {
        merge(array, j, j+i, Math.min(j + i * 2, size));
      }
    }
  }

  public static void sort (List<Integer> list) {
    Collections.sort(list);
  }
}
