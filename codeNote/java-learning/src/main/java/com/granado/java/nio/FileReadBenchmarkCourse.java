package com.granado.java.nio;

import cn.hutool.core.date.StopWatch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;

/**
 * @Author: Yang Songlin
 * @Date: 2020/7/22 3:48 下午
 */
public class FileReadBenchmarkCourse {

    public static void main(String[] args) throws Exception {
        final long FILE_SIZE = 1024L * 1024L * 1024L * 10L;
        final long FILE_SIZE_HALF = FILE_SIZE / 2L;
        File f = new File("/tmp/tmp.log");
        if (!f.exists()) {
            writeFileWithSpecificSize(f, FILE_SIZE);
        }

        char first = 0;
        //
        //
        //  文件流读取文件
        //
        //
        FileInputStream fis = new FileInputStream(f);
        {
            fis.skip(FILE_SIZE_HALF);
            long b = System.currentTimeMillis();
            byte[] buffer = new byte[32 * 1024];
            long c = 0, count = 0;
            while (count != -1) {
                count = fis.read(buffer);
                c += count;
                if (first == 0) {
                    first = (char) (buffer[0]);
                }
            }
            b = System.currentTimeMillis() - b;
            System.out.printf("直接读%s大小的物理文件，耗时%sms。%s%n", FILE_SIZE_HALF, b, c);
        }

        //
        //
        //  随机访问直接读取文件
        //
        //
        RandomAccessFile raf = new RandomAccessFile(f, "rw");
        {
            raf.seek(FILE_SIZE_HALF);
            long b = System.currentTimeMillis();
            long c = 0, count = 0;
            byte[] buffer = new byte[32 * 1024];
            for (; count != -1;) {
                count = raf.read(buffer);
                if (c == 0 && first == (char) buffer[0]) {
                    System.out.println("======");
                }
                c += count;
            }
            b = System.currentTimeMillis() - b;
            System.out.printf("直接读%s大小的物理文件，耗时%sms。%s%n", FILE_SIZE_HALF, b, c);
        }

        FileChannel channel = raf.getChannel();
        long b = System.currentTimeMillis();
        StopWatch watch = new StopWatch();
        for (long mmapStart = FILE_SIZE_HALF; mmapStart < FILE_SIZE; mmapStart += Integer.MAX_VALUE) {
            watch.start("mmap");
            MappedByteBuffer buf = channel.map(MapMode.READ_ONLY, mmapStart, Integer.MAX_VALUE);
            watch.stop();

            watch.start("first char");
            byte data = buf.get(0);
            watch.stop();

            if (first == (char) data) {
                System.out.println("======");
            }

            watch.start("second char");
            data = buf.get(1);
            watch.stop();

            for (int i = 0; i < Integer.MAX_VALUE && buf.remaining() > 0; i++) {
                data = buf.get(i);
            }
            System.out.println((char) data);
        }
        b = System.currentTimeMillis() - b;
        System.out.println(String.format("MMAP访问文件耗时: %sms %s", b, watch.prettyPrint()));
        raf.close();
    }

    private static void writeFileWithSpecificSize(File f, long size) throws Exception {
        FileWriter fw = new FileWriter(f);
        f.getParentFile().mkdirs();

        long batch = size / (long) Integer.MAX_VALUE;
        final int page = 16 * 1024 * 1024;
        final char[] cs = new char[page];

        for (int eachBatch = 0; eachBatch < batch; eachBatch++) {
            for (int i = 0; i < Integer.MAX_VALUE; i++) {
                cs[i % page] = (char) (0x30 + (i % 10));
                if (i != 0 && i % page == 0) {
                    fw.write(cs, 0, page);
                } else if (i == Integer.MAX_VALUE - 1) {
                    fw.write(cs, 0, i % page + 1);
                }
            }
            fw.flush();
        }

        long remnant = size % (long) Integer.MAX_VALUE;
        for (int i = 0; i < remnant; i++) {
            cs[i % page] = (char) (0x30 + (i % 10));
            if (i != 0 && i % page == 0) {
                fw.write(cs, 0, page);
            } else if (i == remnant - 1) {
                fw.write(cs, 0, i % page + 1);
            }
        }
        fw.flush();
        fw.close();
    }
}
