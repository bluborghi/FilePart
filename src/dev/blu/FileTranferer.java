package dev.blu;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class FileTranferer {
    protected static void transfer(FileInputStream fis, FileOutputStream fos, long part_length) throws IOException {
        int MAX_BUF_LENGTH = 1024*1024*1; //1MiB
        int buffer_length;
        int last_buffer_length;
        byte[] buffer;
        long numberOfTransfers;

        if (part_length > MAX_BUF_LENGTH) {
            buffer_length = MAX_BUF_LENGTH;
            numberOfTransfers = (int) ((part_length - 1) / buffer_length) + 1;
            last_buffer_length = (int) (part_length - buffer_length * (numberOfTransfers - 1));
            buffer = new byte[buffer_length];
            for (long i = numberOfTransfers; i > 1; i--) { //only if there's more than one part (you need to transfer more bytes than the buffer size)
                fis.read(buffer);
                fos.write(buffer);
            }
        } else {
            last_buffer_length = (int) part_length; //cast to int is ok (part_length <= MAX_BUF_LENGTH)
        }

        buffer = new byte[last_buffer_length];
        fis.read(buffer);
        fos.write(buffer);
    }
}
