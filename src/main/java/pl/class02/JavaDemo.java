package pl.class02;

public class JavaDemo {
  
  static int x = 2;
  
  public static void main(String[] args) {
    {
      System.out.println(x);
      int x = 3;
      System.out.println(x);  
    }
  }
}
