package com.granado.java.nio;

import java.io.File;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;

public class FileCopyCourse {

    private static final String SOURCE_FILE_NAME = "/Users/gyang1/source.txt";

    private static final String TARGET_FILE_NAME = "/Users/gyang1/target.txt";

    // 大致4GB大小的文件行数
    private static final int FILE_LINE_NUM = 400552583;

    public static void main(String[] args) throws Exception {

        copyFile();
    }

    private static void copyFile() throws Exception {
        File sourceFile = new File(SOURCE_FILE_NAME);
        File targetFile = new File(TARGET_FILE_NAME);

        if (targetFile.exists()) {
            targetFile.delete();
        }

        if (!sourceFile.exists()) {
            FileWriter fileWriter = new FileWriter(sourceFile);
            for (int i = 0; i < FILE_LINE_NUM; i++) {
                fileWriter.write(String.valueOf(i));
                fileWriter.write("\r\n");
            }
            fileWriter.flush();
            fileWriter.close();

            System.out.printf("file size is: %d", calculateFileSize(FILE_LINE_NUM));
        } else {
            System.out.printf("source file size: %d \r\n", sourceFile.length());
        }

        RandomAccessFile sourceFileAccess = new RandomAccessFile(sourceFile, "r");
        FileChannel channel = sourceFileAccess.getChannel();

        RandomAccessFile targetFileAccess = new RandomAccessFile(targetFile, "rw");

        // 使用 transferTo 方法，一次最多能写入 Integer.MAX_VALUE 个单位。
        /*long blockSize = Integer.MAX_VALUE;
        long blockCount = sourceFile.length() / blockSize;
        blockCount = blockCount + (sourceFile.length() % blockSize == 0 ? 0 : 1);

        for (long i = 0; i < blockCount; i++) {
            long position = i * blockSize;
            channel.transferTo(position, blockSize, targetFileAccess.getChannel());
        }*/

        for (long l = 0; l != sourceFile.length();) {
            l += channel.transferTo(l, Integer.MAX_VALUE, targetFileAccess.getChannel());
        }

        channel.close();
        targetFileAccess.close();
        sourceFileAccess.close();
    }

    /**
     * 按自然数换行打印字符总的字节大小的计算。
     * 格式如下:
     * 1\r\n
     * 2\r\n
     * 3\r\n
     * .
     * .
     * .
     * 100\r\n
     */
    private static long calculateLineNumOld(long fileSize) {
        //long fileSize = 4294967296L;
        long i = 1, currentSize = 0;
        for (; (currentSize = calculateFileSize(i)) < fileSize; i = 10 * i) ;
        long j = i / 10, mid = (j + i) >> 2;
        currentSize = calculateFileSize(mid);
        long start = j, end = i;

        for (; start != mid && end != mid; ) {
            for (; mid != j && mid != i; ) {

                if (currentSize < fileSize) {
                    j = mid;
                } else if (currentSize > fileSize) {
                    i = mid;
                } else {
                    break;
                }

                mid = (j + i) >> 2;
                currentSize = calculateFileSize(mid);
            }

            if (currentSize < fileSize) {
                start = mid;
            } else if (currentSize > fileSize) {
                end = mid;
            } else {
                break;
            }

            j = start;
            i = end;
            mid = (j + i) >> 2;
        }


        if (currentSize != fileSize) {
            if (currentSize < fileSize) {
                while (currentSize < fileSize) {
                    currentSize = calculateFileSize(++mid);
                }
            } else {
                while (currentSize > fileSize) {
                    currentSize = calculateFileSize(--mid);
                }
            }
        }
        return mid;
    }

    public static long calculateFileSizeOld(long count) {
        long n = count < 10 ? 1 : Double.valueOf(Math.log10(count - 1)).intValue() + 1;
        //long n = count < 10 ? 1 : intLength(count - 1);
        long lineSize = count << 1; // /r/n 一共2个字节，一行就有2个，因此为 2 x count;

        long size = lineSize + 1; // 算0 ~ 9会少一个
        long radix = 1, lastRadix;
        for (int i = 1; i <= n - 1; i++) {
            lastRadix = radix;
            radix = radix * 10;
            // 0~9 每个为1字节，10个就是 10 * 1; 10 ~ 99 每个2字节，一共90个 改公式会导致0 ~ 9少算一个
            size = size + (radix - lastRadix) * i;
        }

        size = size + (count - radix) * n;

        return size;
    }

    /**
     * 优化算法
     * 设 n 为一个整型数的位数，count 表示打印行数
     * size = (s[n-1] - s[n-1] / 10) + (count - 10^(n-1)) * n + 2 * count + 1
     * s[0] = 0
     * s[n] = ((9 * n - 1) * 10^(n + 1) + 10) / 81
     */

    public static final long[] INT_10_RADIX = {1L, 10L, 100L, 1000L, 10000L, 100000L, 1000000L, 10000000L, 100000000L,
            1000000000L, 10000000000L, 100000000000L, 1000000000000L, 10000000000000L, 100000000000000L,
            1000000000000000L, 10000000000000000L, 100000000000000000L};

    // 公式法
    private static long calculateLineNum(long fileSize) {
        //long fileSize = 4294967296L;
        long i = 1;
        for (; calculateFileSize(i) < fileSize; i = 10 * i) ;
        int n = intLength(i - 1), n_1 = n - 1;
        long s = ((9 * n_1 - 1) * INT_10_RADIX[n] + 10) / 81;
        long count = fileSize - s + s / 10 - 1;
        count = (count + n * INT_10_RADIX[n_1]) / (n + 2);

        return count;
    }

    public static long calculateFileSize(long count) {

        int n = count < 10 ? 1 : intLength(count - 1);
        int n_1 = n - 1;
        long size = ((9 * n_1 - 1) * INT_10_RADIX[n] + 10) / 81;
        size = size - size / 10 + (count - INT_10_RADIX[n_1]) * n + 1 + (count << 1);
        return size;
    }

    // 计算整型位数
    public static int intLength(long num) {
        if (num == 0) {
            return 0;
        }
        int i = 0;
        for (; i < INT_10_RADIX.length && INT_10_RADIX[i] <= num; i++) ;
        return i;
    }
}
