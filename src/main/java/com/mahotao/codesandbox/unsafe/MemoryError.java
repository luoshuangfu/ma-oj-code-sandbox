package com.mahotao.codesandbox.unsafe;
import java.util.ArrayList;
import java.util.List;

/**
 * 无限占用空间（浪费系统内存）
 * 编译成功，但无运行成功
 */
public class MemoryError {
    public static void main(String[] args)throws InterruptedException {
        List<byte[]> bytes=new ArrayList<>();
        while(true){
            bytes.add(new byte[10000]);
        }
    }
}
