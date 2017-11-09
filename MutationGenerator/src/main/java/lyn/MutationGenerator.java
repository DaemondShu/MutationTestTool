package lyn;

import com.fasterxml.jackson.databind.JsonNode;

public class MutationGenerator
{

    /**
     *
     * @param sourcePath    原始项目位置
     * @param mutationPath  变异体输出的位置
     * @param config    变异配置
     */
    public MutationGenerator(String sourcePath, String mutationPath, JsonNode config)
    {
    }

    /**
     * 在mutationPath生成变异体
     * @return 以json格式返回每次变异的具体信息
     */
    public JsonNode runMutation()
    {
        return null;
    }
}
