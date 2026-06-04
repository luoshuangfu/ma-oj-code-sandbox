package com.mahotao.codesandbox;


import com.mahotao.codesandbox.model.ExecuteCodeRequest;
import com.mahotao.codesandbox.model.ExecuteCodeResponse;

/**
 * 代码沙箱接口定义
 */
public interface CodeSandBox {
    /**
     * 执行代码
     * @param request
     * @return
     */
    ExecuteCodeResponse executeCode(ExecuteCodeRequest request);
}
