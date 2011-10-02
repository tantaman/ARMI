package sandbox;

import java.lang.reflect.Method;

public class Reflect {
	public static void main(String[] args) throws SecurityException, NoSuchMethodException {
		Method m = Reflect.class.getMethod("ameth");
		if (m.getReturnType() == void.class) {
			System.out.println("void");
		} else if (m.getReturnType() == Void.class) {
			System.out.println("Void");
		}
	}
	
	public void ameth() {
		
	}
}
