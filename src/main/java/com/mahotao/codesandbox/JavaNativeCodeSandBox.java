package com.mahotao.codesandbox;
import java.io.*;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.lang.UUID;
import com.mahotao.codesandbox.model.ExecuteCodeRequest;
import com.mahotao.codesandbox.model.ExecuteCodeResponse;
import com.mahotao.codesandbox.model.ExecuteMessage;
import com.mahotao.codesandbox.utils.ProcessUtils;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/**
 * java语言代码沙箱的具体实现
 */
public class JavaNativeCodeSandBox implements CodeSandBox {
    public static final String GLOBAL_CODE_DIR_NAME="tmpCode";
    public static final String GLOBAL_JAVA_CLASS_NAME="Main.java";

    public static void main(String[] args) {
        JavaNativeCodeSandBox javaNativeCodeSandBox = new JavaNativeCodeSandBox();
        ExecuteCodeRequest executeCodeRequest = new ExecuteCodeRequest();
        executeCodeRequest.setInputList(Arrays.asList("1 3","1 2"));
        //String code= ResourceUtil.readStr("testCode/simpleCompute/Main.java", StandardCharsets.UTF_8);
        String code= ResourceUtil.readStr("testCode/simpleComputeArgs/Main.java", StandardCharsets.UTF_8);
        executeCodeRequest.setCode(code);
        executeCodeRequest.setLanguage("java");
        ExecuteCodeResponse executeCodeResponse = javaNativeCodeSandBox.executeCode(executeCodeRequest);
        System.out.println(executeCodeResponse);
    }
    @Override
    public ExecuteCodeResponse executeCode(ExecuteCodeRequest request) {
        List<String> inputList = request.getInputList();
        String language = request.getLanguage();
        String code = request.getCode();
        //获取项目根目录，拼接出全局临时目录.../tmp
        String userDir=System.getProperty("user.dir");
        String globalCodePathName=userDir+ File.separator+GLOBAL_CODE_DIR_NAME;
        //判断全局代码文件目录是否存在
        if(!FileUtil.exist(globalCodePathName)){
            FileUtil.mkdir(globalCodePathName);
        }
        //把用户的代码隔离存放,.../tmp/uuid
        String userCodeParentPath=globalCodePathName+File.separator+ UUID.randomUUID();
        String userCodePath=userCodeParentPath+File.separator+GLOBAL_JAVA_CLASS_NAME;
        //将前端/测试传入的源码字符串，真实地写入到本地的 Main.java件中
        File userCodeFile=FileUtil.writeString(code,userCodePath, StandardCharsets.UTF_8);
        //2.编译代码，得到class文件
        //拼接编译命令：javac -encoding utf-8 /绝对路径/Main.java
        String compiledCmd=String.format("javac -encoding utf-8 %s",userCodeFile.getAbsoluteFile());
        try {
            //在操作系统中真正拉起一个独立进程执行 javac
            Process compileProcess=Runtime.getRuntime().exec(compiledCmd);
            //阻塞等待编译完成，并使用工具类ProcessUtils 获取进程的输出信息
            ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(compileProcess, "编译");
            System.out.println(executeMessage);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        //3.执行代码，得到结果
        for(String inputArgs:inputList){
            //拼接运行命令
            //-cp（classpath）指定类加载路径为当前用户的随机隔离目录
            // Main是主类名，inputArgs是作为命令行参数（args）传给Main方法
            String runCmd=String.format("java -Dfile.encoding=UTF-8 -cp %s Main %s",userCodeParentPath,inputArgs);
            try {
                //拉起进程执行java Main命令
                Process runProcess=Runtime.getRuntime().exec(runCmd);
                //获取执行结果
                ExecuteMessage executeMessage = ProcessUtils.runProcessAndGetMessage(runProcess, "运行");
                //交互式 switch
                //ExecuteMessage executeMessage = ProcessUtils.runInteractProcessAndGetMessage(runProcess,inputArgs);
                System.out.println(executeMessage);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
