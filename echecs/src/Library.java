import java.util.ArrayList;
import java.util.List;

public abstract class Library {

    public static long pow2(int pow) {
        if (pow == 63)
            return (long) -Math.pow(2, 63);
        else
            return (long) Math.pow(2, pow);
    }

    public static List<Long> extract(long l) {
        List<Long> res = new ArrayList<>();
        String binary = Long.toBinaryString(l);
        int pow = 0;
        for (int i = binary.length() - 1; i >= 0; i--) {
            if (binary.charAt(i) == '1')
                res.add(Library.pow2(pow));
            pow++;
        }
        return res;
    }

    public static void afficherLong(long l) {
        String chiffre = Long.toBinaryString(l);
        String binaire = chiffre;
        for (int i = 0; i < 64 - chiffre.length(); i++)
            binaire = "0" + binaire;

        for (int i = 0; i < 64; i += 8) {
            String ligne = binaire.substring(i, i + 8);
            for (int j = 7; j >= 0; j--)
                System.out.print(ligne.charAt(j));
            System.out.println();
        }
    }
}
