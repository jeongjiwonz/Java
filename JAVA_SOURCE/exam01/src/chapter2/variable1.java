package chapter2;

public class variable1 {
	public static void main(String[]args) {
		int num; // 선언 (자료형 변수명 -> 자료형 만큼 공간이 생성)
		// 용량 만큼 숫자를 입력(2진  32비트)
		num = 10; // num 공간에 10을 대입한다
		
		System.out.println(num);
		
		double num2 = 20;
		System.out.println(num2);
		
		long num3 = 2000000000000000L;
		System.out.println(num3);
		
		// 나이 - 정수
		int age = 24;
		System.out.println("나이는"+age);
	}
}
