package site.deercloud.identityverification.Utils;

public class RandomCode {
    public static String NewCodeWithAlphabet(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int random = (int) (Math.random() * 36);
            if (random < 10) {
                code.append(random);
            } else {
                code.append((char) (random + 87));
            }
        }
        return code.toString();
    }

    public static String NewCodeOnlyNumber(int length) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int random = (int) (Math.random() * 10);
            code.append(random);
        }
        return code.toString();
    }
}
