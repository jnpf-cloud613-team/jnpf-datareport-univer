package jnpf.listener;


import com.fasterxml.jackson.core.StreamReadConstraints;

/**
 * 自定义设置
 */
public class DataReportStartInit {

    public static void init() {
        // Jackson 解析大文件
        StreamReadConstraints.overrideDefaultStreamReadConstraints(
                StreamReadConstraints.builder().maxStringLength(100000000).build()
        );
    }
}
