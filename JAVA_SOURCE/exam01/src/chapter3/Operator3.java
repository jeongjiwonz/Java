package chapter3;

public class Operator3 {
	public static void main(String[]args) {
		int num = 10;
		int num2 = num++; // num2? 10, num? 11
		
		int num3 = ++num; // num3? 12, num? 12
		
		int num4 = num--; // num4? 12, num? 11
		int num5 = --num; // num5? 10, num? 10
	}
}
