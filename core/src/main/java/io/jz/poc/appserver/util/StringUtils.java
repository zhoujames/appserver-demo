package io.jz.poc.appserver.util;

public class StringUtils {

    public static final byte[] CRLF = { 0x0d, 0x0a };


    public static String removeDuplicates(String s, char c) {
        int i = -1;
        while ((i = s.indexOf(c, i + 1)) > -1) {
            int end;
            for (end = i + 1; end < s.length() && s.charAt(end) == c; end++);
            if (end > i + 1)
                s = s.substring(0, i + 1) + s.substring(end);
        }
        return s;
    }

    public static byte[] getBytes(String... strings) {
        int n = 0;
        for (String s : strings)
            n += s.length();
        byte[] dest = new byte[n];
        n = 0;
        for (String s : strings)
            for (int i = 0, len = s.length(); i < len; i++)
                dest[n++] = (byte)s.charAt(i);
        return dest;
    }

    /**
     * https://www.freeutils.net/source/jlhttp/
     * Returns a human-friendly string approximating the given data size,
     * e.g. "316", "1.8K", "324M", etc.
     *
     * @param size the size to display
     * @return a human-friendly string approximating the given data size
     */
    public static String toSizeApproxString(long size) {
        final char[] units = { ' ', 'K', 'M', 'G', 'T', 'P', 'E' };
        int u;
        double s;
        for (u = 0, s = size; s >= 1000; u++, s /= 1024);
        return String.format(s < 10 ? "%.1f%c" : "%.0f%c", s, units[u]);
    }


    /**
     * https://www.freeutils.net/source/jlhttp/
     * Returns the parent of the given path.
     *
     * @param path the path whose parent is returned (must start with '/')
     * @return the parent of the given path (excluding trailing slash),
     *         or null if given path is the root path
     */
    public static String getParentPath(String path) {
        path = trimRight(path, '/'); // remove trailing slash
        int slash = path.lastIndexOf('/');
        return slash == -1 ? null : path.substring(0, slash);
    }

    /**
     * https://www.freeutils.net/source/jlhttp/
     * Returns the given string with all occurrences of the given character
     * removed from its right side.
     *
     * @param s the string to trim
     * @param c the character to remove
     * @return the trimmed string
     */
    public static String trimRight(String s, char c) {
        int len = s.length() - 1;
        int end;
        for (end = len; end >= 0 && s.charAt(end) == c; end--);
        return end == len ? s : s.substring(0, end + 1);
    }

}
