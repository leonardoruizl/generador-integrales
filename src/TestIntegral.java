import java.util.Scanner;

public class TestIntegral {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            Integral integral = new Integral();

            integral.mostrarIntegralLatex();
            integral.ejecutar(scanner);
        }
    }
}