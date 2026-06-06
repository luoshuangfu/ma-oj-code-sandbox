package com.mahotao.codesandbox.security;

import cn.hutool.core.io.FileUtil;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class TestSecurityManager {
    public static void main(String[] args) {
        System.setSecurityManager(new MySecurityManager());
        FileUtil.writeString("aa","aaa", Charset.defaultCharset());
    }
}
