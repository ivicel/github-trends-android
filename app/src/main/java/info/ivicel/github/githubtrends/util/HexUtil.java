package info.ivicel.github.githubtrends.util;



public class HexUtil {
    public static String bytesToHex(final byte[] data) {
        StringBuilder sb = new StringBuilder();
        for (byte b : data) {
            String hex = Integer.toHexString(b);
            if (hex.length() == 1) {
                sb.append("0");
            }
            sb.append(hex);
        }
        
        return sb.toString();
    }
}
