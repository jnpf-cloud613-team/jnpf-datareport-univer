package jnpf;

import jnpf.listener.DataReportStartInit;
import org.dromara.x.file.storage.spring.EnableFileStorage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
@EnableFileStorage
public class ReportUniverApplication {

    public static void main(String[] args) {
        DataReportStartInit.init();
        SpringApplication springApplication = new SpringApplication(ReportUniverApplication.class);
        springApplication.run(args);
        System.out.println("报表启动完成");
    }

}
