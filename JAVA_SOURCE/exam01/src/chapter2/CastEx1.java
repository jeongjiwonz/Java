package chapter2;

public class CastEx1 {
	public static void main(String[]args) {
		byte num1 = 10;
		int num2 = num1; // num1 -> byte -> int
		System.out.println(num2);
		
		float num3 = num2; // num3 자료형이 num2 보다 정밀 -> int -> float
		System.out.println(num3);
		
		long num4 = 1234567890L;
		float num5 = num4;
		System.out.println(num5);
		
		System.out.println(10/2.0f); // int / float
		//float num6 = 10/2.0f;
		double num6 = 10/2.0f; // float -> double
		System.out.println(num6);
	}
}
