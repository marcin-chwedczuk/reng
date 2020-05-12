package pl.marcinchwedczuk.reng;

public class Main {
    public static void main(String[] args) {
        // Regex: B(0|1)*E

        RAst r = RAst.concat(
                RAst.group('B'),
                RAst.star(
                    RAst.alternative(RAst.group('0'), RAst.group('1'))),
                RAst.group('E'));

        System.out.println(r);
    }
}
