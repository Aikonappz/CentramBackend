package com.centram.core;

import com.fasterxml.jackson.databind.MappingIterator;
import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.envers.repository.support.EnversRevisionRepositoryFactoryBean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.io.IOException;
import java.util.List;
import java.util.Map;

//@EnableDiscoveryClient
//@EnableFeignClients(basePackages = {"com.erp.api.client"})
@EnableSwagger2
@ComponentScan(basePackages = {"com.centram.core", "com.centram.common", "com.centram.domain"})
@EntityScan(basePackages = {"com.centram.domain"})
@EnableJpaRepositories(repositoryFactoryBeanClass = EnversRevisionRepositoryFactoryBean.class, basePackages = {"com.centram.core.repository"})
@SpringBootApplication(scanBasePackages = {"com.centram.core", "com.centram.common", "com.centram.domain"})
public class CoreApp {
    public static void main(String[] args) throws Exception {
        /*File f = new File("/home/sumit/Documents/erp/item.csv");
        String content = Files.toString(f, Charsets.UTF_8);
        List<Map<?, ?>> data = readObjectsFromCsv(content);
        for (Map m : data) {
            System.out.println(data.get(0));
        }*/


        SpringApplication.run(CoreApp.class, args);
    }

    public static List<Map<?,?>> readObjectsFromCsv(String file) throws IOException {
        CsvSchema bootstrap = CsvSchema.emptySchema().withHeader();
        CsvMapper csvMapper = new CsvMapper();
        MappingIterator<Map<?, ?>> mappingIterator
                = csvMapper.reader(Map.class).with(bootstrap).readValues(file);
        return mappingIterator.readAll();
    }

}